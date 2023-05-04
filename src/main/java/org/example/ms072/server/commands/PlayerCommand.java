package org.example.ms072.server.commands;

import  org.example.ms072.client.MapleClient;
import  org.example.ms072.constants.ServerConstants.PlayerGMRank;
import  org.example.ms072.handling.world.World;
import  org.example.ms072.scripting.NPCConversationManager;
import  org.example.ms072.scripting.NPCScriptManager;
import  org.example.ms072.tools.StringUtil;
import  org.example.ms072.tools.packet.CField;
import  org.example.ms072.tools.packet.CField.NPCPacket;
import  org.example.ms072.tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            StringBuilder sb = new StringBuilder();
            sb.append("指令列表 :");
            sb.append("\r\n@解卡/@查看/@ea  <解除异常+查看当前状态>");
            sb.append("\r\n@爆率 <查看周围怪物的爆率>.");
            //sb.append("\r\n@功能 <一些功能>.");
            //sb.append("\r\n@修改密码 <修改当前帐号密码>.");
//            sb.append("\r\n@存挡 < 人物资料保存>.");
//            sb.append("\r\n@爆率  <查询当前地图怪物爆率>");
//            sb.append("\r\n @领取点券        < 充值领取点券 >");
//            sb.append("\r\n@CGM <讯息>  -- 传送讯息給GM");
            if (c.canClickNPC()) {
                NPCPacket.getNPCTalk(9010000, (byte) 0, sb.toString(), "00 00", (byte) 0);
            }
            for (String command : sb.toString().split("\r\n")) {
                c.getPlayer().dropMessage(5, command);
            }
            return 1;
        }
    }

    public static class 存档 extends save {
    }

    public static class 帮助 extends Help {
    }

    public static class 爆率 extends Mobdrop {
    }

    public static class ea extends 查看 {
    }

    public static class 解卡 extends 查看 {
    }

    public static class 查看 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
          //  c.sendPacket(CWvsContext.FamilyPacket.sendFamilyMessage(0));
           //  World.Broadcast.broadcastSmega(CWvsContext.serverNotice(11, "[全服公告] : " ));
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(CWvsContext.enableActions());
            c.getPlayer().dropMessage(1, "解卡完毕.");

            return 1;
        }
    }

    public static class save extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().dropMessage("存档成功");
            return 1;
        }
    }

    /*public static class 修改密码 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String s = splitted[1];
            if (s.length() > 4 && s.length() < 10) {
                if (c.getPlayer().getCSPoints(1) < 2000) {
                    c.getPlayer().dropMessage(5, "您的点卷不足2000点,无法修改密码");
                } else {
                    c.modifypassword(splitted[1]);
                    c.getPlayer().modifyCSPoints(1, -2000, false);
                    c.getPlayer().dropMessage(5, "修改密码成功");
                }
            } else {
                c.getPlayer().dropMessage(5, "密码长度必须大于4字节或者小于10字节");
            }

            return 1;

        }
    }*/
 /*public static class Event extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().isInBlockedMap() || c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "您不能使用此命令在这里.");
                return 0;
            }
            NPCScriptManager.getInstance().start(c, 9000000, null);
            return 1;
        }
    }*/

 /*public static class JoinEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getChannelServer().warpToEvent(c.getPlayer());
            return 1;
        }
    }*/

 /*public static class CashDrop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, "CashDrop");
            return 1;
        }
    }*/
    public static class CGM extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "已经成功发送给管理员.");
            if (splitted[1] == null) {
                c.getPlayer().dropMessage(6, "请打字谢谢.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因为你自己是GM无法使用此命令,可以尝试!cngm <讯息> 來建立GM聊天頻道~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 5 minutes.
                World.Broadcast.broadcastGMMessage(CField.multiChat("[管理员提示] " + c.getPlayer().getName(), StringUtil.joinStringFrom(splitted, 1), 6));
                c.getPlayer().dropMessage(6, "讯息已经发给GM了!");
            } else {
                c.getPlayer().dropMessage(6, "为了防止对GM刷屏所以每1分鐘只能发一次.");
            }
            return 1;
        }
    }

    public static class Mobdrop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(CWvsContext.enableActions());
            NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9900006, null);
            return 1;
        }
    }
}
