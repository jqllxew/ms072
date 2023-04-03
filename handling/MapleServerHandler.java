package handling;

import handling.farm.handler.FarmHandler;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.*;
import handling.channel.ChannelServer;
import handling.channel.handler.*;
import handling.farm.handler.FarmOperation;
import handling.login.LoginServer;
import handling.login.handler.*;
import handling.netty.MaplePacketDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.CashItemFactory;
import server.CashItemInfo;
import server.Randomizer;
import server.commands.CommandProcessor;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.Pair;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.LoginPacket;
import tools.packet.CSPacket;

public class MapleServerHandler extends ChannelInboundHandlerAdapter {

    public static final List<String> BlockIPList = new ArrayList<>();
    public static final boolean preventIpAttack = true;
    private static boolean show = false;
    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<>();
    private int world = -1, channel = -1;
    private ServerType type = null;

    public MapleServerHandler(int world, int channel, ServerType type) {
        this.world = world;
        this.channel = channel;
        this.type = type;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 开始检测IP
        String address = ctx.channel().remoteAddress().toString().split(":")[0];
        if (BlockIPList.contains(address)) { //是否在禁止的IP列表
            ctx.channel().close();
            return;
        }
        if (!address.equals("/127.0.0.1")) { // 过滤本地IP  && (type == ServerType.登录服务器 || type == ServerType.聊天服务器 || type == ServerType.频道服务器)
            Pair<Long, Byte> track = tracker.get(address);
            byte count;
            if (track == null) {
                count = 1;
            } else {
                count = track.right;
                long difference = System.currentTimeMillis() - track.left;
                if (difference < 2000) { //同一个IP地址连接时间检测 当前为2秒
                    count++;
                } else if (difference > 20000) { //清除连接次数的时间 当前为20秒
                    count = 1;
                }
                if (preventIpAttack && count > 5) { // 单个IP的连接上限 达到多少次就禁止连接
                    BlockIPList.add(address);
                    tracker.remove(address); // Cleanup
                    ctx.channel().close();
                    return;
                }
            }
            tracker.put(address, new Pair<>(System.currentTimeMillis(), count));
            // 结束IP检测.
        }

        if (channel > -1) {
            if (ChannelServer.getInstance(channel).isShutdown()) { //如果频道是关闭的就断开连接
                ctx.channel().close();
                return;
            }
        } else if (type == ServerType.商城服务器) {
            if (CashShopServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        } else if (type == ServerType.登录服务器) {
            if (LoginServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        }
        /*else if (type == ServerType.聊天服务器) {
            if (ChatServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        }*/

        byte[] ivRecv = {70, 114, 122, 82};
        byte[] ivSend = {82, 48, 120, 115};
        ivRecv[3] = (byte) (Math.random() * 255);
        ivSend[3] = (byte) (Math.random() * 255);
        MapleAESOFB sendCypher = new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION));
        MapleAESOFB recvCypher = new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION);
        MapleClient client = new MapleClient(sendCypher, recvCypher, ctx.channel());
        client.setSessionId(Randomizer.nextLong());
        client.setChannel(channel);
        client.setWorld(world);
        ctx.channel().writeAndFlush(LoginPacket.getHello(ivSend, ivRecv));
        ctx.channel().attr(MapleClient.CLIENT_KEY).set(client);
        LoginServer.removeIPAuth(address);

        StringBuilder sb = new StringBuilder();
        if (channel > -1) {
            sb.append("[Channel Server] Channel ").append(channel).append(" : ");
        } else if (type == ServerType.商城服务器) {
            sb.append("[Cash Server] ");
        } else if (type == ServerType.聊天服务器) {
            sb.append("[Chat Server]");
        } else {
            sb.append("[Login Server] ");
        }
        sb.append("IoSession opened ").append(address);
        System.out.println(sb);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            try {
                client.disconnect(true, ServerType.商城服务器.equals(type));
            } catch (Throwable t) {
                System.err.println("连接异常关闭" + t);
            } finally {
                ctx.channel().attr(MapleClient.CLIENT_KEY).set(null);
                ctx.channel().attr(MaplePacketDecoder.DECODER_STATE_KEY).set(null);
                ctx.channel().close();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LittleEndianAccessor slea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) msg));
        if (slea.available() < 2) {
            return;
        }
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client == null || !client.isReceiving()) {
            return;
        }
        short packetId = slea.readShort();
        for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == packetId) {
                if (recv.NeedsChecking() && !client.isLoggedIn()) {
                    break;
                }
                try {
                    if (client.getPlayer() != null && client.isMonitored()/* && ServerConfig.DEBUG_MODE*/) {
                        try (FileWriter fw = new FileWriter(new File("logs/MonitorLogs/" + client.getPlayer().getName() + "_log.txt"), true)) {
                            fw.write(recv + " (" + Integer.toHexString(packetId) + ") Handled: \r\n" + slea.toString() + "\r\n");
                            fw.flush();
                        }
                    }
                    handlePacket(recv, slea, client, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("封包: " + lookupRecv(packetId) + "\r\n" + slea.toString(true) + " 异常信息：" + e);
                }
                return;
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null && type == ServerType.频道服务器) {
            client.sendPing();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
            if (client != null && client.getPlayer() != null) {
                client.getPlayer().saveToDB(false, false);
                System.err.println("Exception caught by: " + client.getPlayer().getName() + cause);
            }
        }
    }

    private String lookupRecv(short header) {
        for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header) {
                return recv.name();
            }
        }
        return "UNKNOWN";
    }

    public static void handlePacket(final RecvPacketOpcode header, final LittleEndianAccessor slea, final MapleClient c, ServerType type) throws Exception {
        switch (header) {
            case LOGIN_REDIRECTOR:
                if (!ServerConstants.Redirector) {
                    System.out.println("Redirector login packet recieved, but server is not set to redirector. Please change it in ServerConstants!");
                } else {
                    String username = slea.readMapleAsciiString(); // lol
                    c.loginData(username);
                    c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
                }
                break;
            case CHECK_ACCOUNT:
                CharLoginHandler.CheckAccount(slea, c);
                break;
            case REGISTER_ACCOUNT:
                CharLoginHandler.RegisterAccount(slea, c);
                break;
            case CRASH_INFO:
                System.out.println("Crash" + slea.toString());
                break;
            case CLIENT_HELLO:
                System.out.println(c.getSessionIPAddress() + " Connected!");
                break;
            case PONG:
                c.pongReceived();
                break;
            case STRANGE_DATA:
                break;

            case LOGIN_PASSWORD:
                CharLoginHandler.login(slea, c);
                break;
            case SET_GENDER:
                CharLoginHandler.setGender(slea, c);
                break;
            case WRONG_PASSWORD:
//                if (c.getSessionIPAddress().contains("8.31.99.141") || c.getSessionIPAddress().contains("8.31.99.143") || c.getSessionIPAddress().contains("127.0.0.1")) {
//                    c.loginData("admin");
//                    c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
//                }
                break;
            case AUTH_REQUEST:
                CharLoginHandler.handleAuthRequest(slea, c);
                break;
            case CLIENT_START:
//                if (c.getSessionIPAddress().contains("8.31.99.141") || c.getSessionIPAddress().contains("8.31.99.143") || c.getSessionIPAddress().contains("127.0.0.1")) {
//                    c.loginData("admin");
//                    c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
//                }
                break;
            case CLIENT_FAILED:
                break;
            case VIEW_SERVERLIST:
                if (slea.readByte() == 0) {
                    CharLoginHandler.ServerListRequest(c);
                }
                break;
            case REDISPLAY_SERVERLIST:
            case SERVERLIST_REQUEST:
                CharLoginHandler.ServerListRequest(c);
                break;
            case CHARLIST_REQUEST:
                CharLoginHandler.CharlistRequest(slea, c);
                break;
            case SERVERSTATUS_REQUEST:
                CharLoginHandler.ServerStatusRequest(c);
                break;
            case CHECK_CHAR_NAME:
                CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
                break;
            case CREATE_CHAR:
            case CREATE_SPECIAL_CHAR:
                CharLoginHandler.CreateChar(slea, c);
                break;
            case CREATE_ULTIMATE:
                CharLoginHandler.CreateUltimate(slea, c);
                break;
            case DELETE_CHAR:
                CharLoginHandler.DeleteChar(slea, c);
                break;
            case CHAR_SELECT_NO_PIC:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c, false, false);
                break;
            case VIEW_REGISTER_PIC:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c, true, true);
                break;
            case PART_TIME_JOB:
                CharLoginHandler.PartJob(slea, c);
                break;
            case CHAR_SELECT:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c, true, false);
                break;
            case VIEW_SELECT_PIC:
                CharLoginHandler.Character_WithSecondPassword(slea, c, true);
                break;
            case AUTH_SECOND_PASSWORD:
                CharLoginHandler.Character_WithSecondPassword(slea, c, false);
                break;
            case CHARACTER_CARD:
                CharLoginHandler.updateCCards(slea, c);
                break;
            case CLIENT_ERROR:
                FileoutputUtil.log("logs/异常/错误日志.txt", "Packet: " + HexTool.toStringFromAscii(slea.read((int) slea.available())));
                break;
            case ENABLE_SPECIAL_CREATION:
                c.sendPacket(LoginPacket.enableSpecialCreation(c.getAccID(), true));
                break;
            case RSA_KEY:
                break;
            // END OF LOGIN SERVER
            case CHANGE_CHANNEL:
            case CHANGE_ROOM_CHANNEL:
                InterServerHandler.ChangeChannel(slea, c, c.getPlayer(), header == RecvPacketOpcode.CHANGE_ROOM_CHANNEL);
                break;
            case PLAYER_LOGGEDIN:
                //slea.readInt();
                final int playerid = slea.readInt();
                InterServerHandler.Loggedin(playerid, c, type);
                break;
            case ENTER_PVP:
            case ENTER_PVP_PARTY:
                PlayersHandler.EnterPVP(slea, c);
                break;
            case PVP_RESPAWN:
                PlayersHandler.RespawnPVP(slea, c);
                break;
            case LEAVE_PVP:
                PlayersHandler.LeavePVP(slea, c);
                break;
            case ENTER_AZWAN:
                PlayersHandler.EnterAzwan(slea, c);
                break;
            case ENTER_AZWAN_EVENT:
                PlayersHandler.EnterAzwanEvent(slea, c);
                break;
            case LEAVE_AZWAN:
                PlayersHandler.LeaveAzwan(slea, c);
                c.sendPacket(CField.showEffect("hillah/fail"));
                c.sendPacket(CField.UIPacket.sendAzwanResult());
                break;
            case PVP_ATTACK:
                PlayersHandler.AttackPVP(slea, c);
                break;
            case PVP_SUMMON:
                SummonHandler.SummonPVP(slea, c);
                break;
            case ENTER_FARM:
                InterServerHandler.EnterFarm(c, c.getPlayer());
                break;
            case FARM_COMPLETE_QUEST:
                FarmHandler.completeQuest(slea, c);
                break;
            case FARM_NAME:
                FarmHandler.createFarm(slea, c);
                break;
            case PLACE_FARM_OBJECT:
                FarmHandler.placeBuilding(slea, c);
                break;
            case FARM_SHOP_BUY:
                FarmHandler.buy(slea, c);
                break;
            case HARVEST_FARM_BUILDING:
                FarmHandler.harvest(slea, c);
                break;
            case USE_FARM_ITEM:
                FarmHandler.useItem(slea, c);
                break;
            case RENAME_MONSTER:
                FarmHandler.renameMonster(slea, c);
                break;
            case NURTURE_MONSTER:
                FarmHandler.nurtureMonster(slea, c);
                break;
            case FARM_QUEST_CHECK:
                FarmHandler.checkQuestStatus(slea, c);
                break;
            case FARM_FIRST_ENTRY:
                FarmHandler.firstEntryReward(slea, c);
                break;
            case EXIT_FARM:
                FarmOperation.LeaveFarm(slea, c, c.getPlayer());
                break;
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(c, c.getPlayer());
                break;
            case ENTER_MTS:
                InterServerHandler.EnterMTS(c, c.getPlayer());
                // c.getPlayer().dropMessage(5, "服务器暂不开放拍卖系统。");
                break;
            case MOVE_PLAYER:
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            case CHAR_INFO_REQUEST:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                break;
            case CLOSE_RANGE_ATTACK:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), false);
                break;
            case RANGED_ATTACK:
                PlayerHandler.rangedAttack(slea, c, c.getPlayer());
                break;
            case MAGIC_ATTACK:
                PlayerHandler.MagicDamage(slea, c, c.getPlayer());
                break;
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            case PASSIVE_ENERGY:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), true);
                break;
            case GET_BOOK_INFO:
                PlayersHandler.MonsterBookInfoRequest(slea, c, c.getPlayer());
                break;
            case MONSTER_BOOK_DROPS:
                PlayersHandler.MonsterBookDropsRequest(slea, c, c.getPlayer());
                break;
            case CHANGE_CODEX_SET:
                PlayersHandler.ChangeSet(slea, c, c.getPlayer());
                break;
            case PROFESSION_INFO:
                ItemMakerHandler.ProfessionInfo(slea, c);
                break;
            case CRAFT_DONE:
                ItemMakerHandler.CraftComplete(slea, c, c.getPlayer());
                break;
            case CRAFT_MAKE:
                ItemMakerHandler.CraftMake(slea, c, c.getPlayer());
                break;
            case CRAFT_EFFECT:
                ItemMakerHandler.CraftEffect(slea, c, c.getPlayer());
                break;
            case START_HARVEST:
                ItemMakerHandler.StartHarvest(slea, c, c.getPlayer());
                break;
            case STOP_HARVEST:
                ItemMakerHandler.StopHarvest(slea, c, c.getPlayer());
                break;
            case MAKE_EXTRACTOR:
                ItemMakerHandler.MakeExtractor(slea, c, c.getPlayer());
                break;
            case USE_BAG:
                ItemMakerHandler.UseBag(slea, c, c.getPlayer());
                break;
            case USE_FAMILIAR:
                MobHandler.UseFamiliar(slea, c, c.getPlayer());
                break;
            case SPAWN_FAMILIAR:
                MobHandler.SpawnFamiliar(slea, c, c.getPlayer());
                break;
            case RENAME_FAMILIAR:
                MobHandler.RenameFamiliar(slea, c, c.getPlayer());
                break;
            case MOVE_FAMILIAR:
                MobHandler.MoveFamiliar(slea, c, c.getPlayer());
                break;
            case ATTACK_FAMILIAR:
                MobHandler.AttackFamiliar(slea, c, c.getPlayer());
                break;
            case TOUCH_FAMILIAR:
                MobHandler.TouchFamiliar(slea, c, c.getPlayer());
                break;
            case REVEAL_FAMILIAR:
                break;
            case USE_RECIPE:
                ItemMakerHandler.UseRecipe(slea, c, c.getPlayer());
                break;
            case MOVE_HAKU:
                PlayerHandler.MoveHaku(slea, c, c.getPlayer());
                break;
            case CHANGE_HAKU:
                PlayerHandler.MoveHaku(slea, c, c.getPlayer());
                break;
            case MOVE_ANDROID:
                PlayerHandler.MoveAndroid(slea, c, c.getPlayer());
                break;
            case FACE_EXPRESSION:
                PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            case FACE_ANDROID:
                PlayerHandler.ChangeAndroidEmotion(slea.readInt(), c.getPlayer());
                break;
            case TAKE_DAMAGE:
                PlayerHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            case HEAL_OVER_TIME:
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            case MECH_CANCEL:
                PlayerHandler.CancelMech(slea, c.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            case USE_TITLE:
                PlayerHandler.UseTitle(slea.readInt(), c, c.getPlayer());
                break;
            case ANGELIC_CHANGE:
                PlayerHandler.AngelicChange(slea, c, c.getPlayer());
                break;
            case DRESSUP_TIME:
                PlayerHandler.DressUpTime(slea, c);
                break;
            case USE_CHAIR:
                PlayerHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            case WHEEL_OF_FORTUNE:
                break; //whatever
            case USE_ITEMEFFECT:
                PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            case QUICK_SLOT:
                PlayerHandler.QuickSlot(slea, c.getPlayer());
                break;
            case MESO_DROP:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            case PET_BUFF:
                PlayerHandler.ChangePetBuff(slea, c.getPlayer());
                break;
            case UPDATE_ENV:
                // We handle this in MapleMap
                break;
            case CHANGE_MAP:
                if (type == ServerType.商城服务器) {
                    CashShopOperation.LeaveCS(slea, c, c.getPlayer());
                } else {
                    PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                }
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            case LIE_DETECTOR_SKILL:
                //c.getPlayer().dropMessage(5, "服务器暂不开放测谎系统。");
                PlayersHandler.LieDetector(slea, c, c.getPlayer(), false);
                break;
            case LIE_DETECTOR:
                //c.getPlayer().dropMessage(5, "服务器暂不开放测谎系统。");
                PlayersHandler.LieDetector(slea, c, c.getPlayer(), true);
                break;
            case LIE_DETECTOR_RESPONSE:
                //c.getPlayer().dropMessage(5, "服务器暂不开放测谎系统。");
                PlayersHandler.LieDetectorResponse(slea, c);
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(c, c.getPlayer(), 1);
                break;
            case SKILL_MACRO:
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;

            case GIVE_FAME:
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            case USE_MECH_DOOR:
                PlayersHandler.UseMechDoor(slea, c.getPlayer());
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(slea, c);
                break;
            case CLICK_REACTOR:
            case TOUCH_REACTOR:
                PlayersHandler.TouchReactor(slea, c);
                break;
            case CLOSE_CHALKBOARD:
                c.getPlayer().setChalkboard(null);
                break;
            case ITEM_SORT:
                InventoryHandler.ItemSort(slea, c);
                break;
            case ITEM_GATHER:
                InventoryHandler.ItemGather(slea, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.ItemMove(slea, c);
                break;
            case MOVE_BAG:
                InventoryHandler.MoveBag(slea, c);
                break;
            case SWITCH_BAG:
                InventoryHandler.SwitchBag(slea, c);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case ITEM_PICKUP:
                InventoryHandler.Pickup_Player(slea, c, c.getPlayer());
                break;
            case USE_ITEM_BAOWU:
                InventoryHandler.UseItemBaowu(slea, c);
                break;
            case USE_CASH_ITEM:
                InventoryHandler.UseCashItem(slea, c);
                break;
            case USE_ITEM:
                InventoryHandler.UseItem(slea, c, c.getPlayer());
                break;
            case USE_COSMETIC:
                InventoryHandler.UseCosmetic(slea, c, c.getPlayer());
                break;
            case USE_MAGNIFY_GLASS:
                InventoryHandler.UseMagnify(slea, c);
                break;
            case USE_SCRIPTED_NPC_ITEM:
                InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            case USE_RETURN_SCROLL:
                InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            case USE_NEBULITE:
                InventoryHandler.UseNebulite(slea, c);
                break;
            case USE_ALIEN_SOCKET:
                InventoryHandler.UseAlienSocket(slea, c);
                break;
            case USE_ALIEN_SOCKET_RESPONSE:
                slea.skip(4); // all 0
                c.sendPacket(CSPacket.useAlienSocket(false));
                break;
            case GOLDEN_HAMMER:
                InventoryHandler.UseGoldenHammer(slea, c);
                break;
            case VICIOUS_HAMMER:
                slea.skip(4); // 3F 00 00 00
                slea.skip(4); // all 0
                c.sendPacket(CSPacket.ViciousHammer(false, 0));
                break;
            case USE_NEBULITE_FUSION:
                InventoryHandler.UseNebuliteFusion(slea, c);
                break;
            case USE_UPGRADE_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), slea.readShort(), c, c.getPlayer(), slea.readByte() > 0);
                break;
            case USE_FLAG_SCROLL:
            case USE_POTENTIAL_SCROLL:
            case USE_EQUIP_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), (short) 0, c, c.getPlayer(), slea.readByte() > 0);
                break;
            case USE_ABYSS_SCROLL:
                InventoryHandler.UseAbyssScroll(slea, c);
                break;
            case USE_CARVED_SEAL:
                InventoryHandler.UseCarvedSeal(slea, c);
                break;
            case USE_CRAFTED_CUBE:
                InventoryHandler.UseCube(slea, c);
                break;
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            case USE_TREASURE_CHEST:
                InventoryHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            case USE_SKILL_BOOK:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSkillBook((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case USE_EXP_POTION:
                InventoryHandler.UseExpPotion(slea, c, c.getPlayer());
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            case REWARD_ITEM:
                InventoryHandler.UseRewardItem(slea, c, c.getPlayer());
                break;
            case SOLOMON_EXP:
                InventoryHandler.UseExpItem(slea, c, c.getPlayer());
                break;
            case HYPNOTIZE_DMG:
                MobHandler.HypnotizeDmg(slea, c.getPlayer());
                break;
            case MOB_NODE:
                MobHandler.MobNode(slea, c.getPlayer());
                break;
            case DISPLAY_NODE:
                MobHandler.DisplayNode(slea, c.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.MoveMonster(slea, c, c.getPlayer());
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            case FRIENDLY_DAMAGE:
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            case REISSUE_MEDAL:
                PlayerHandler.ReIssueMedal(slea, c, c.getPlayer());
                break;
            case MONSTER_BOMB:
                MobHandler.MonsterBomb(slea.readInt(), c.getPlayer());
                break;
            case MOB_BOMB:
                MobHandler.MobBomb(slea, c.getPlayer());
                break;
            case MONSTER_BOOK_COVER:
                PlayerHandler.ChangeMonsterBookCover(slea.readInt(), c, c.getPlayer());
                break;
            case NPC_SHOP:
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(slea, c);
                break;
            case MARRAGE_RECV:
                NPCHandler.MarrageNpc(c);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            case TOT_GUIDE:
                break;
            case STORAGE:
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            case GENERAL_CHAT:
                if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
                    //c.getPlayer().updateTick(slea.readInt());
                    ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
                }
                break;
            case PARTYCHAT:
                //c.getPlayer().updateTick(slea.readInt());
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            case COMMAND:
                ChatHandler.Command(slea, c);
                break;
            case MESSENGER:
                ChatHandler.Messenger(slea, c);
                break;
            case AUTO_ASSIGN_AP:
                StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_SP:
                StatsHandling.DistributeSP(slea, c, c.getPlayer());
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            case ADMIN_CHAT:
                ChatHandler.AdminChat(slea, c, c.getPlayer());
                break;
            case ADMIN_COMMAND:
                PlayerHandler.AdminCommand(slea, c, c.getPlayer());
                break;
            case ADMIN_LOG:
                CommandProcessor.logCommandToDB(c.getPlayer(), slea.readMapleAsciiString(), "adminlog");
                break;
            case GUILD_OPERATION:
                GuildHandler.Guild(slea, c);
                break;
            case DENY_GUILD_REQUEST:
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            case ALLIANCE_OPERATION:
                AllianceHandler.HandleAlliance(slea, c, false);
                break;
            case DENY_ALLIANCE_REQUEST:
                AllianceHandler.HandleAlliance(slea, c, true);
                break;
            case QUICK_MOVE:
                NPCHandler.OpenQuickMove(slea, c);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperation(slea, c);
                break;
            case PARTY_OPERATION:
                PartyHandler.PartyOperation(slea, c);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            case ALLOW_PARTY_INVITE:
                PartyHandler.AllowPartyInvite(slea, c);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperation(slea, c);
                break;
            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            case BUY_CS_ITEM:
                CashShopOperation.BuyCashItem(slea, c, c.getPlayer());
                break;
            case COUPON_CODE:
                slea.skip(2);
                String code = slea.readMapleAsciiString();
                CashShopOperation.CouponCode(code, c);
//                CashShopOperation.doCSPackets(c);
                break;
            case CASH_CATEGORY:
                CashShopOperation.SwitchCategory(slea, c);
                break;
            case TWIN_DRAGON_EGG:
                System.out.println("TWIN_DRAGON_EGG: " + slea.toString());
                final CashItemInfo item = CashItemFactory.getInstance().getItem(10003055);
                Item itemz = c.getPlayer().getCashInventory().toItem(item);
                //Aristocat c.sendPacket(CSPacket.sendTwinDragonEgg(true, true, 38, itemz, 1));
                break;
            case XMAS_SURPRISE:
                System.out.println("XMAS_SURPRISE: " + slea.toString());
                break;
            case CS_UPDATE:
                CashShopOperation.CSUpdate(c);
                break;
            case USE_POT:
                ItemMakerHandler.UsePot(slea, c);
                break;
            case CLEAR_POT:
                ItemMakerHandler.ClearPot(slea, c);
                break;
            case FEED_POT:
                ItemMakerHandler.FeedPot(slea, c);
                break;
            case CURE_POT:
                ItemMakerHandler.CurePot(slea, c);
                break;
            case REWARD_POT:
                ItemMakerHandler.RewardPot(slea, c);
                break;
            case DAMAGE_SUMMON:
                slea.skip(4);
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            case SUMMON_ATTACK:
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            case MOVE_DRAGON:
                SummonHandler.MoveDragon(slea, c.getPlayer());
                break;
            case SUB_SUMMON:
                SummonHandler.SubSummon(slea, c.getPlayer());
                break;
            case REMOVE_SUMMON:
                SummonHandler.RemoveSummon(slea, c);
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(slea, c, c.getPlayer());
                break;
            case MOVE_PET:
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            case PET_CHAT:
                //System.out.println("Pet chat: " + slea.toString());
                if (slea.available() < 12) {
                    break;
                }
                final int petid = c.getPlayer().getPetIndex(slea.readInt());
                c.getPlayer().updateTick(slea.readInt());
                PetHandler.PetChat(petid, slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
                break;
            case PET_COMMAND:
                MaplePet pet;
                pet = c.getPlayer().getPet(c.getPlayer().getPetIndex(slea.readInt()));
                //c.getPlayer().updateTick(slea.readInt());
                slea.readInt();
                slea.readByte(); //always 0?
                if (pet == null) {
                    return;
                }
                PetHandler.PetCommand(pet, PetDataFactory.getPetCommand(pet.getPetItemId(), slea.readByte()), c, c.getPlayer());
                break;
            case PET_FOOD:
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            case PET_LOOT:
                //System.out.println("PET_LOOT ACCESSED");
                InventoryHandler.Pickup_Pet(slea, c, c.getPlayer());
                break;
            case PET_AUTO_POT:
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            case PET_IGNORE:
                PetHandler.PetIgnoreTag(slea, c, c.getPlayer());
                break;
            case MONSTER_CARNIVAL:
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            case PACKAGE_OPERATION:
                PackageHandler.handleAction(slea, c);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(c, true);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;
            case CANCEL_DEBUFF:
                // Ignore for now
                break;
            case MAPLETV:
                break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, c);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, c);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, c);
                break;
            case REPAIR:
                NPCHandler.repair(slea, c);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(c);
                break;
            case BUY_SILENT_CRUSADE:
                PlayersHandler.buySilentCrusade(slea, c);
                break;
            //case GAME_POLL:
            //    UserInterfaceHandler.InGame_Poll(slea, c);
            //    break;
            case OWL:
                InventoryHandler.Owl(slea, c);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, c);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, c);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, c);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, c);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, c);
                break;
            case FOLLOW_REQUEST:
                PlayersHandler.FollowRequest(slea, c);
                break;
            case AUTO_FOLLOW_REPLY:
            case FOLLOW_REPLY:
                PlayersHandler.FollowReply(slea, c);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, c);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, c);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, c);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, c);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, c);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, c);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, c);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, c);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, c);
                break;
            case SOLOMON:
                PlayersHandler.Solomon(slea, c);
                break;
            case GACH_EXP:
                PlayersHandler.GachExp(slea, c);
                break;
            case PARTY_SEARCH_START:
                PartyHandler.MemberSearch(slea, c);
                break;
            case PARTY_SEARCH_STOP:
                PartyHandler.PartySearch(slea, c);
                break;
            case EXPEDITION_LISTING:
                PartyHandler.PartyListing(slea, c);
                break;
            case EXPEDITION_OPERATION:
                PartyHandler.Expedition(slea, c);
                break;
            case USE_TELE_ROCK:
                InventoryHandler.TeleRock(slea, c);
                break;
            case AZWAN_REVIVE:
                PlayersHandler.reviveAzwan(slea, c);
                break;
            case INNER_CIRCULATOR:
                InventoryHandler.useInnerCirculator(slea, c);
                break;
            case PAM_SONG:
                InventoryHandler.PamSong(slea, c);
                break;
            case REPORT:
                PlayersHandler.Report(slea, c);
                break;
            //working
            case CANCEL_OUT_SWIPE:
                slea.readInt();
                break;
            //working
            case VIEW_SKILLS:
                PlayersHandler.viewSkills(slea, c);
                break;
            //working
            case SKILL_SWIPE:
                PlayersHandler.StealSkill(slea, c);
                break;
            case CHOOSE_SKILL:
                PlayersHandler.ChooseSkill(slea, c);
                break;
            case MAGIC_WHEEL:
                System.out.println("[MAGIC_WHEEL] [" + slea.toString() + "]");
                PlayersHandler.magicWheel(slea, c);
                break;
            case REWARD:
                PlayersHandler.onReward(slea, c);
                break;
            case BLACK_FRIDAY:
                PlayersHandler.blackFriday(slea, c);
            case UPDATE_RED_LEAF:
                PlayersHandler.updateRedLeafHigh(slea, c);
                break;
            case SPECIAL_STAT:
                PlayersHandler.updateSpecialStat(slea, c);
                break;
            case UPDATE_HYPER:
                StatsHandling.DistributeHyper(slea, c, c.getPlayer());
                break;
            case RESET_HYPER:
                StatsHandling.ResetHyper(slea, c, c.getPlayer());
                break;
            case DF_COMBO:
                PlayerHandler.absorbingDF(slea, c);
                break;
            case MESSENGER_RANKING:
                PlayerHandler.MessengerRanking(slea, c, c.getPlayer());
                break;
            case OS_INFORMATION:
                System.out.println(c.getSessionIPAddress());
                break;
//            case BUFF_RESPONSE://wat does it do?
//                break;
            case BUTTON_PRESSED:
                break;
            case CASSANDRAS_COLLECTION:
                PlayersHandler.CassandrasCollection(slea, c);
                break;
            case LUCKY_LUCKY_MONSTORY:
                PlayersHandler.LuckyLuckyMonstory(slea, c);
                break;
            case PLAYER_UPDATE:
                PlayerHandler.PlayerUpdate(c.getPlayer());
                break;
            case BUFF_RESPONSE:
                break;
            case ITEM_SUNZI:
                InventoryHandler.SunziBF(slea, c);
                break;
            case BEANS_OPERATION:
                BeanGame.BeansGameAction(slea, c);
                break;
            case BEANS_UPDATE:
                BeanGame.BeansUpdate(slea, c);
                break;
            default:
                System.out.println("[UNHANDLED] Recv [" + header.toString() + "] found");
                break;
        }
    }

}
