package org.example.ms072.handling.channel.handler;

import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleClient;
import  org.example.ms072.client.SkillFactory;
import  org.example.ms072.client.inventory.Item;
import  org.example.ms072.client.inventory.MapleInventory;
import  org.example.ms072.client.inventory.MapleInventoryType;
import  org.example.ms072.constants.GameConstants;
import  org.example.ms072.constants.ServerConstants;
import  org.example.ms072.constants.WorldConstants;
import org.example.ms072.handling.ServerType;
import org.example.ms072.handling.cashshop.CashShopServer;
import org.example.ms072.handling.cashshop.handler.CashShopOperation;
import org.example.ms072.handling.channel.ChannelServer;
import  org.example.ms072.handling.farm.FarmServer;
import  org.example.ms072.handling.login.LoginServer;
import  org.example.ms072.handling.world.*;
import  org.example.ms072.handling.world.exped.MapleExpedition;
import  org.example.ms072.handling.world.guild.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import  org.example.ms072.scripting.NPCScriptManager;
import  org.example.ms072.server.*;
import  org.example.ms072.server.maps.FieldLimitType;
import  org.example.ms072.server.maps.MapleMap;
import  org.example.ms072.tools.FileoutputUtil;
import  org.example.ms072.tools.Triple;
import  org.example.ms072.tools.data.LittleEndianAccessor;
import  org.example.ms072.tools.packet.CField;
import  org.example.ms072.tools.packet.CField.NPCPacket;
import  org.example.ms072.tools.packet.CSPacket;
import  org.example.ms072.tools.packet.CWvsContext;
import  org.example.ms072.tools.packet.CWvsContext.BuddylistPacket;
import  org.example.ms072.tools.packet.CWvsContext.FamilyPacket;
import  org.example.ms072.tools.packet.CWvsContext.GuildPacket;
import  org.example.ms072.tools.packet.FarmPacket;

public class InterServerHandler {

    public static void EnterCS(final MapleClient c, final MapleCharacter chr) {
        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器正在关闭，现在无法进入商城！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(chr.getId());
            if (farmtransfer != null) {
                c.sendPacket(FarmPacket.farmMessage("在访问你的农场时，你不能进入现金商店，但."));
            }
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "服务器正忙着。请在一分钟或更少的时间再试.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.sendPacket(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static void EnterFarm(final MapleClient c, final MapleCharacter chr) {
        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器正在关闭，现在无法进入农场！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "服务器正忙着。请在一分钟或更少的时间再试.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -30);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.sendPacket(CField.getChannelChange(c, Integer.parseInt(FarmServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void EnterMTS(final MapleClient c, final MapleCharacter chr) {

        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器正在关闭，现在无法使用拍卖！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        if (c.getPlayer().getTrade() != null) {
            c.getPlayer().dropMessage(1, "交易中无法进行其他操作！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        NPCScriptManager.getInstance().dispose(c);
        if (c.getPlayer().getLevel() >= 8) {
            NPCScriptManager.getInstance().dispose(c);
            NPCScriptManager.getInstance().start(c, 9900004);//拍卖npc
            c.sendPacket(CWvsContext.enableActions());
        } else {
            c.sendPacket(NPCPacket.getNPCTalk(9900004, (byte) 0, "玩家你好.等级不足8级无法使用拍卖功能.", "00 00", (byte) 0, 9900004));
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void Loggedin(final int playerid, final MapleClient c, final ServerType type) {
        CharacterTransfer transfer = null;
        try {
            transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        } catch (Exception e) {
            System.err.println("读取商城临时角色失败" + e);
        }

        if (type.equals(ServerType.商城服务器)) {
            if (transfer != null) {
                CashShopOperation.EnterCS(transfer, c);
            }
            return;
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            try {
                transfer = cserv.getPlayerStorage().getPendingCharacter(playerid);
            } catch (Exception e) {
                System.err.println("读取频道临时角色失败" + e);
            }
            if (transfer != null) {
                c.setChannel(cserv.getChannel());
                break;
            }
        }
        MapleCharacter player;
        boolean firstLoggedIn = true; //设置只有第1次登录的提示开关
        if (transfer == null) { // Player isn't in storage, probably isn't CC
            Triple<String, String, Integer> ip = LoginServer.getLoginAuth(playerid);
            String s = c.getSessionIPAddress();
            if (ip == null || !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.left)) {
                if (ip != null) {
                    LoginServer.putLoginAuth(playerid, ip.left, ip.mid, ip.right);
                }
                //System.out.println("Session close");
                c.getSession().close();
                FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
                return;
            }
            c.setTempIP(ip.mid);
            c.setChannel(ip.right);
            List<String> charNames = c.loadCharacterNamesByCharId(playerid);
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                for (final String name : charNames) {
                    if (cs.getPlayerStorage().getCharacterByName(name) != null) {
                        c.getSession().close();
                        return;
                    }
                }
            }
            for (final String name : charNames) {
                if (CashShopServer.getPlayerStorage().getCharacterByName(name) != null) {
                    c.getSession().close();
                    return;
                }
            }

            player = MapleCharacter.loadCharFromDB(playerid, c, true);
            //System.out.println("从数据库载入角色");
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
            //System.out.println("重新连接角色");
            firstLoggedIn = false;
        }
        final ChannelServer channelServer = c.getChannelServer();
        c.setPlayer(player);
        //System.out.println("设定玩家: " + player);
        c.setAccID(player.getAccountID());
        //System.out.println("设定账号编号 : " + player.getAccountID());
        c.loadAccountData(player.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            //System.out.println("连接 Hack");
            return;
        }
//        ChannelServer.forceRemovePlayerByAccId(c, player.getAccountID());
        final int state = c.getLoginState();
        //System.out.println("账号登入状态 = " + c.getLoginState());
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            //System.out.println("同意登入 = false");
            return;
        }

        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        //System.out.println("增加玩家");
        channelServer.addPlayer(player);

        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()), true);
        player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
        player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

        c.sendPacket(CField.getCharInfo(player));
        c.sendPacket(CWvsContext.updateMount(player, false));
        c.sendPacket(CWvsContext.temporaryStats_Reset());
        if (player.isGM()) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        }
        c.sendPacket(CWvsContext.updateSkills(c.getPlayer().getSkills(), false));//skill to 0 "fix"
        c.sendPacket(CSPacket.enableCSUse());

        player.getMap().addPlayer(player);
        try {
            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                final MapleParty party = player.getParty();
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
                if (party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            c.sendPacket(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.sendPacket(GuildPacket.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    for (byte[] pack : packetList) {
                        if (pack != null) {
                            c.sendPacket(pack);
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.sendPacket(FamilyPacket.getFamilyData());
            c.sendPacket(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        player.getClient().sendPacket(CWvsContext.broadcastMsg(channelServer.getServerMessage()));
        player.sendMacros();
        player.showNote();
        player.sendImp();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.baseSkills(); //fix people who've lost skills.
        player.updatePetAuto();
        c.sendPacket(CField.getKeymap(player.getKeyLayout()));
        player.expirationTask(true, transfer == null);
        //c.sendPacket(CWvsContext.updateMaplePoint(player.getCSPoints(2)));
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
        //player.spawnClones();
        player.spawnSavedPets();
        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon + (GameConstants.getBeginnerJob(player.getJob()) * 1000)).getEffect(1).applyTo(player);
        }
        MapleInventory equipped = player.getInventory(MapleInventoryType.EQUIPPED);
        List<Short> slots = new ArrayList<>();
        for (Item item : equipped.newList()) {
            slots.add(item.getPosition());
        }
        if (!player.isGM()) {
            for (short slot : slots) {
                if (GameConstants.isIllegalItem(equipped.getItem(slot).getItemId())) {
                    MapleInventoryManipulator.removeFromSlot(player.getClient(), MapleInventoryType.EQUIPPED, slot, (short) 1, false);
                    return;
                }
            }
        }
        //player.updateReward();
        player.getClient().sendPacket(CWvsContext.broadcastMsg(channelServer.getServerMessage()));
        //Thread.sleep(1000);
        //c.sendPacket(CWvsContext.getTopMsg("Earned Forever Single title!"));
        //Thread.sleep(3100);
        //if (c.getPlayer().getLevel() < 11) { 
        //NPCScriptManager.getInstance().start(c, 9010000, "LoginTot");
        //}
        //} catch (InterruptedException e) {
        //}
        //发送登录提示 只有第1次才有
        if (firstLoggedIn) {
            //上线提示
            if (player.getGMLevel() == 0 && player.getMap().getId() != 0) {
                if (player.getGender() == 0) {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(11, "[登录公告] 【帅哥】" + c.getPlayer().getName() + " : " + "进入游戏，大家热烈欢迎他吧！！！"));//广播
                } else {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(11, "[登录公告] 【美女】" + c.getPlayer().getName() + " : " + "进入游戏，大家热烈欢迎她吧！！！"));//广播
                }
            } else {
                int p = 0;
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr != null) {
                            ++p;
                        }
                    }
                }
                player.dropMessage(6, "[服务器信息]：尊敬的管理员，欢迎进入游戏。当前在线人数：" + p + "人");
            }
            String msg = "冒险岛当前游戏版本为: [Ver." + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH + "] 欢迎您回来，使用 @help 可以查看您当前能使用的命令 祝您玩的愉快！";
            if (player.getLevel() == 1) {
                player.dropMessage(1, "欢迎来到 " + c.getChannelServer().getServerName() + ", " + player.getName() + " ！\r\n使用 @help 可以查看您当前能使用的命令\r\n祝您玩的愉快！");
                player.dropMessage(5, "使用 @help 可以查看您当前能使用的命令 祝您玩的愉快！");
            } else {
                c.sendPacket(CField.sendHint(msg, 200, 5));
                player.dropMessage(1, "欢迎来到 " + c.getChannelServer().getServerName() + ", " + player.getName() + " ！\r\n使用 @help 可以查看您当前能使用的命令\r\n请保管好自己的游戏帐号，不要在QQ交流群和游戏中随便透露自己的帐号信息。\r\n非BUG测试需要,本服管理不会找玩家索要游戏帐号。\r\n如果玩家私下交易或者透露自己的帐号信息导致被骗或者装备被盗后果自负。");
            }
            if (c.getPlayer().hasEquipped(1122017)) {
                player.dropMessage(5, "您装备了精灵吊坠！打猎时可以额外获得30%的道具佩戴经验奖励！");
            }
            System.out.println("[MS072服务端][名字:" + c.getPlayer().getName() + "][等级:" + c.getPlayer().getLevel() + "]进入游戏.");
        }
    }

    public static final void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器正在关闭，现在无法切换频道！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "服务器正忙着。请在不到一分钟的时间再试一次.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        chr.updateTick(slea.readInt());
        if (!World.isChannelAvailable(chc, chr.getWorld())) {
            chr.dropMessage(1, "请求被拒绝由于未知的错误.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "请求被拒绝由于未知的错误.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "你已经在 " + chr.getMap().getMapName());
                    c.sendPacket(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, Objects.requireNonNull(warpz.getPortal("out00")));
                } else {
                    chr.dropMessage(1, "请求被拒绝由于未知的错误.");
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
    }
}
