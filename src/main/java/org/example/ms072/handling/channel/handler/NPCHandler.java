package org.example.ms072.handling.channel.handler;

import  org.example.ms072.client.inventory.Equip;
import  org.example.ms072.client.inventory.Item;
import  org.example.ms072.client.inventory.MapleInventoryType;
import  org.example.ms072.client.MapleClient;
import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.constants.GameConstants;
import  org.example.ms072.client.MapleQuestStatus;
import  org.example.ms072.client.RockPaperScissors;
import  org.example.ms072.client.inventory.ItemFlag;
import  org.example.ms072.constants.QuickMove;
import org.example.ms072.handling.SendPacketOpcode;
import  org.example.ms072.handling.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import  org.example.ms072.server.shops.MapleShop;
import  org.example.ms072.server.MapleInventoryManipulator;
import  org.example.ms072.server.MapleStorage;
import  org.example.ms072.server.life.MapleNPC;
import  org.example.ms072.server.quest.MapleQuest;
import  org.example.ms072.scripting.NPCScriptManager;
import  org.example.ms072.scripting.NPCConversationManager;
import  org.example.ms072.server.AutobanManager;
import  org.example.ms072.server.MapleItemInformationProvider;
import  org.example.ms072.server.maps.MapScriptMethods;
import  org.example.ms072.tools.FileoutputUtil;
import  org.example.ms072.tools.packet.CField;
import  org.example.ms072.tools.Pair;
import  org.example.ms072.tools.data.LittleEndianAccessor;
import  org.example.ms072.tools.data.MaplePacketLittleEndianWriter;
import  org.example.ms072.tools.packet.CField.EffectPacket;
import  org.example.ms072.tools.packet.CWvsContext;

public class NPCHandler {

    public static void NPCAnimation(LittleEndianAccessor slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
        int length = (int) slea.available();
        if (length == 6) {
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
            //mplew.writeInt(slea.readInt());
        } else if (length > 6) {
            return;
            //mplew.write(slea.read(length - 9));
        } else {
            return;
        }

        c.sendPacket(mplew.getPacket());
    }

    public static final void NPCShop(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (World.isShutDown) {
            c.getPlayer().dropMessage(5, "服务器正在关闭，现在无法使用商店！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte bmode = slea.readByte();
        if (chr == null) {
            return;
        }

        switch (bmode) {
            case 0: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                short slot = slea.readShort();
                slot++;
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.buy(c, slot, itemId, quantity);
                break;
            }
            case 1: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case 2: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            default:
                chr.setConversation(0);
        }
    }

    public static void NPCTalk(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleNPC npc = chr.getMap().getNPCByOid(slea.readInt());
        if (npc == null) {
            return;
        }
        if (chr.hasBlockedInventory()) {
            return;
        }
        if (NPCScriptManager.getInstance().hasScript(c, npc.getId(), null)) { //I want it to come before shop
            NPCScriptManager.getInstance().start(c, npc.getId());
        } else if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
    }

    public static final void QuestAction(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte action = slea.readByte();
        int quest = slea.readUShort();
        if (chr == null) {
            return;
        }
        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                chr.updateTick(slea.readInt());
                //slea.readInt();
                final int itemid = slea.readInt();
                q.RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                final int npc = slea.readInt();
                if (npc == 0 && quest > 0) {
                    q.forceStart(chr, npc, null);
                } else if (!q.hasStartScript()) {
                    q.start(chr, npc);
                }
                if (c.getPlayer().isAdmin()) {
                    chr.dropMessage(6, "开始系统任务 NPC: " + npc + " Quest：" + quest);
                }
                break;
            }
            case 2: { // Complete Quest
                final int npc = slea.readInt();
                chr.updateTick(slea.readInt());
                //slea.readInt();
                if (q.hasEndScript()) {
                    return;
                }
                try{
                    System.out.println("当前线程："+Thread.currentThread().getId());
                    if (slea.available() >= 4) {
                        q.complete(chr, npc, slea.readInt());
                    } else {
                        q.complete(chr, npc);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                // c.sendPacket(CField.completeQuest(c.getPlayer(), quest));
                //c.sendPacket(CField.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
                // 6 = start quest
                // 7 = unknown error
                // 8 = equip is full
                // 9 = not enough mesos
                // 11 = due to the equipment currently being worn wtf o.o
                // 12 = you may not posess more than one of this item
                if (c.getPlayer().isAdmin()) {
                    chr.dropMessage(6, "完成系统任务 NPC: " + npc + " Quest: " + quest);
                }
                break;
            }
            case 3: { // Forfeit Quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                    if (c.getPlayer().isAdmin()) {
                        chr.dropMessage(6, "放弃系统任务 Quest: " + quest);
                    }
                } else {
                    chr.dropMessage(1, "无法放弃这个任务.");
                }
                break;
            }
            case 4: { // Scripted Start Quest
                final int npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(slea.readInt());
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
//                if (c.getPlayer().isAdmin()) {
                    chr.dropMessage(6, "执行脚本任务 NPC：" + npc + " Quest: " + quest);
//                }
                break;
            }
            case 5: { // Scripted End Quest
                final int npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(slea.readInt());
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.sendPacket(EffectPacket.showForeignEffect(9)); // Quest completion
                chr.getMap().broadcastMessage(chr, EffectPacket.showForeignEffect(chr.getId(), 9), false);
                if (c.getPlayer().isAdmin()) {
                    chr.dropMessage(6, "完成脚本任务 NPC：" + npc + " Quest: " + quest);
                }
                break;
            }
        }
    }

    @SuppressWarnings("empty-statement")
    public static final void Storage(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (World.isShutDown) {
            c.getPlayer().dropMessage(1, "服务器正在关闭，现在无法使用仓库！\r\n请立即下线，否则后果自负！");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte mode = slea.readByte();
        if (chr == null) {
            return;
        }
        MapleStorage storage = chr.getStorage();
        switch (mode) {
            case 4: {// 仓库 - 取出物品
                int needmeso = chr.getMapId() == 910000000 ? 1000 : 0;
                byte type = slea.readByte();
                byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
                //获取道具在仓库的信息
                Item item = storage.takeOut(slot);
                if (item != null) {
                    //检测是否是唯一道具
                    if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.getItemQuantity(item.getItemId(), true) > 0) {
                        return;
                    }
                    //检测取回道具金币是否足够
                    int meso = storage.getNpcId() == 9030100 || storage.getNpcId() == 9031016 ? 1000 : 0;
                    if (needmeso > 0) {
                        if (chr.getMeso() < meso) {
                            //storage.store(item);
                            chr.dropMessage(1, "你没有足够的金币取出物品");
                            return;
                        }
                        chr.gainMeso(-needmeso, true);
                    }
                    //检测角色背包是否有位置
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        // 背包不足，存回物品
                        storage.store(item);
                        chr.dropMessage(1, "你的背包已满");
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    }
                } else {
                    //AutobanManager.getInstance().autoban(c, "试图从仓库取出不存在的道具.");
                    System.err.println("[作弊] " + chr.getName() + " (等级 " + chr.getLevel() + ") 试图从仓库取出不存在的道具.");
                    World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM消息] 玩家: " + chr.getName() + " (等级 " + chr.getLevel() + ") 试图从仓库取出不存在的道具."));
                    c.sendPacket(CWvsContext.enableActions());
                }
                break;
            }
            case 5: {//仓库 - 存入物品
                //如果是自由市场，存入手续金币500反之100
                int needmeso = chr.getMapId() == 910000000 ? 500 : 100;
                byte slot = (byte) slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();

                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                //检测保存道具的数量是否小于1
                if (quantity < 1) {
                    AutobanManager.getInstance().autoban(c, "试图存入到仓库的道具数量: " + quantity + " 道具ID: " + itemId);
                    return;
                }
                //检测仓库的道具是否已满
                if (storage.isFull()) {
                    c.sendPacket(CField.NPCPacket.getStorageFull());
                    return;
                }
                //检测角色背包当前道具是否有道具
                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                if (chr.getInventory(type).getItem((short) slot) == null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                //检测金币是否足够
                if (chr.getMeso() < needmeso) {
                    chr.dropMessage(1, "你没有足够的金币存入物品");
                    return;
                }
                //开始操作保存道具到仓库
                Item item = chr.getInventory(type).getItem((short) slot).copy();
                //检测道具是否为宠物道具
                if (GameConstants.isPet(item.getItemId())) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                //检测道具是否为唯一道具 且角色仓库已经有这个道具
                short flag = item.getFlag();
                if ((ii.isPickupRestricted(item.getItemId())) && (storage.findById(item.getItemId()) != null)) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if ((item.getItemId() == itemId) && ((item.getQuantity() >= quantity) || (GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId)))) {
                    //保存道具的状态属性
                    if (ii.isDropRestricted(item.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                        } else if (ItemFlag.KARMA_ACC.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_ACC.getValue()));
                        } else if (ItemFlag.KARMA_ACC_USE.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_ACC_USE.getValue()));
                        } else {
                            c.sendPacket(CWvsContext.enableActions());
                            return;
                        }
                    }
                    //如果是飞镖子弹道具就设置保存的数量为道具当前的数量
                    if ((GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId))) {
                        quantity = item.getQuantity();
                    }
                    //删除角色背包中的道具
                    MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
                    //收取保存到仓库的手续费用
                    chr.gainMeso(-needmeso, true, false, false);
                    //设置道具的数量
                    item.setQuantity(quantity);
                    //存入道具到仓库
                    storage.store(item);
                    //发送当前仓库的道具封包
                    storage.sendStored(c, GameConstants.getInventoryType(itemId));
                } else {
                    AutobanManager.getInstance().addPoints(c, 1000, 0, "试图存入到仓库的道具: " + itemId + " 数量: " + quantity + " 当前玩家用道具: " + item.getItemId() + " 数量: " + item.getQuantity());
                }
                break;
            }
            case 6://仓库的物品排序按钮
                storage.arrange();
                storage.update(c);
                break;
            case 7: {//金币
                int meso = slea.readInt();
                int storageMesos = storage.getMeso();
                int playerMesos = chr.getMeso();

                if (((meso > 0L) && (storageMesos >= meso)) || ((meso < 0L) && (playerMesos >= -meso))) {
                    if ((meso < 0L) && (storageMesos - meso < 0L)) {
                        meso = -(2147483647 - storageMesos);
                        if (-meso <= playerMesos);
                    } else if ((meso > 0L) && (playerMesos + meso < 0L)) {
                        meso = 2147483647 - playerMesos;
                        if (meso > storageMesos) {
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, false, false);
                } else {
                    return;
                }
                storage.sendMeso(c);
                break;
            }
            case 8://退出仓库
                storage.close();
                chr.setConversation(0);
                break;
            default:
                System.out.println("Unhandled Storage mode : " + mode);
        }
    }

    public static final void MarrageNpc(MapleClient c) {
        if (c != null && c.getPlayer() != null) {
            if (c.getPlayer().getMapId() == 700000100) {
                c.getPlayer().changeMap(700000200, 0);
            }
        }
    }

    public static void NPCMoreTalk(final LittleEndianAccessor slea, final MapleClient c) {
        final byte lastMsg = slea.readByte(); // 00 (last msg type I think)
        if (lastMsg == 9 && slea.available() >= 4) {
            slea.readShort();
        }
        final byte action = slea.readByte(); // 00 = end chat, 01 == follow

        if (((lastMsg == 0x12 && c.getPlayer().getDirection() >= 0) || (lastMsg == 0x12 && c.getPlayer().getDirection() == -1)) && action == 1) {
            byte lastbyte = slea.readByte(); // 00 = end chat, 01 == follow
            if (lastbyte == 0) {
                c.sendPacket(CWvsContext.enableActions());
            } else {
                MapScriptMethods.startDirectionInfo(c.getPlayer(), lastMsg == 0x13);
                c.sendPacket(CWvsContext.enableActions());
            }
            return;
        }
        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        /*if (cm != null && lastMsg == 0x17) {
         c.getPlayer().handleDemonJob(slea.readInt());
         return;
         }*/
        if (cm == null || c.getPlayer().getConversation() == 0 || cm.getLastMsg() != lastMsg) {
            return;
        }
        cm.setLastMsg((byte) -1);
        /*if (lastMsg == 1) {
         NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
         } else*/ if (lastMsg == 2) {
            if (action != 0) {
                cm.setGetText(slea.readMapleAsciiString());
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
        } else if (lastMsg == 0x17) {
            NPCScriptManager.getInstance().action(c, (byte) 1, lastMsg, action);
        } else {
            int selection = -1;
            if (slea.available() >= 4) {
                selection = slea.readInt();
            } else if (slea.available() > 0) {
                selection = slea.readByte();
            }
            if (lastMsg == 4 && selection == -1) {
                cm.dispose();
                return;//h4x
            }
            if (selection >= -1 && action != -1) {
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                }
            } else {
                cm.dispose();
            }
        }
    }

    public static final void repairAll(final MapleClient c) {
        Equip eq;
        double rPercentage;
        int price = 0;
        Map<String, Integer> eqStats;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<Equip, Integer> eqs = new HashMap<>();
        final MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
                if (item instanceof Equip) { //redundant
                    eq = (Equip) item;
                    if (eq.getDurability() >= 0) {
                        eqStats = ii.getEquipStats(eq.getItemId());
                        if (eqStats.containsKey("durability") && eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")) {
                            rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
                            eqs.put(eq, eqStats.get("durability"));
                            price += (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
                        }
                    }
                }
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        Equip ez;
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }

    public static final void repair(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() < 4) { //leafre for now
            return;
        }
        final int position = slea.readInt(); //who knows why this is a int
        final MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        final Item item = c.getPlayer().getInventory(type).getItem((byte) position);
        if (item == null) {
            return;
        }
        final Equip eq = (Equip) item;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || !eqStats.containsKey("durability") || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        final double rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
        //drpq level 105 weapons - ~420k per %; 2k per durability point
        //explorer level 30 weapons - ~10 mesos per %
        final int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0)); // / 100 for level 30?
        //TODO: need more data on calculating off client
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }

    public static final void UpdateQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

    public static final void UseItemQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final short slot = slea.readShort();
        final int itemId = slea.readInt();
        final Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        final int qid = slea.readInt();
        final MapleQuest quest = MapleQuest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight() != null && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break; //i believe it's any order
                }
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = slea.readInt();
            final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short) 1, false);
            }
        }
    }

    public static final void RPSGame(LittleEndianAccessor slea, MapleClient c) {
        if ((slea.available() == 0L) || (c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (!c.getPlayer().getMap().containsNPC(9000019))) {
            if ((c.getPlayer() != null) && (c.getPlayer().getRPS() != null)) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        byte mode = slea.readByte();
        switch (mode) {
            case 0:
            case 5:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000L) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.sendPacket(CField.getRPSMode((byte) 8, -1, -1, -1));
                }
                break;
            case 1:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().answer(c, slea.readByte()))) {
                    c.sendPacket(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 2:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().timeOut(c))) {
                    c.sendPacket(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 3:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().nextRound(c))) {
                    c.sendPacket(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 4:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.sendPacket(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
        }
    }

    public static void OpenQuickMove(final LittleEndianAccessor slea, final MapleClient c) {
        final int npcid = slea.readInt();
        if (c.getPlayer().hasBlockedInventory() || c.getPlayer().isInBlockedMap() || c.getPlayer().getLevel() < 10) {
            return;
        }
        for (QuickMove qm : QuickMove.values()) {
            if (qm.getMap() == c.getPlayer().getMapId()) {
                List<QuickMove.QuickMoveNPC> qmn = new LinkedList();
                int npcs = qm.getNPCFlag();
                for (QuickMove.QuickMoveNPC npc : QuickMove.QuickMoveNPC.values()) {
                    if ((npcs & npc.getValue()) != 0 && npc.getId() == npcid) {
                        NPCScriptManager.getInstance().start(c, npcid);
                        break;
                    }
                }
            }
        }
    }

    public static Invocable getInvocable(String path, MapleClient c, boolean npc) {
        ScriptEngineManager sem = new ScriptEngineManager();
        FileReader fr = null;
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("javascript");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                fr = new FileReader(scriptFile);
                engine.eval(fr);
            } else if (c != null && npc) {
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.sendPacket(CWvsContext.enableActions());
                //c.getPlayer().dropMessage(-1, "You already are talking to this NPC. Use @ea if this is not intended.");
            }
            return (Invocable) engine;
        } catch (FileNotFoundException | ScriptException e) {
            e.printStackTrace();
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
