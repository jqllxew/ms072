package scripting;

import client.MapleClient;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final boolean hasScript(final MapleClient c, final int npc, String script) {
        Invocable iv = getInvocable("npc/" + npc + ".js", c, true);
        if (script != null && !script.isEmpty()) {
            iv = getInvocable("npc/" + script + ".js", c, true);
        }
        return iv != null;
    }

    public void start(MapleClient c, int npc) {
        start(c, npc, 0);
    }

    public void start(final MapleClient c, final int npc, int wh) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
//            if (c.getPlayer().isGM()) {
                if (wh != 0) {
                    c.getPlayer().dropMessage("[系统提示]当前与您对话的NPCID为 : " + npc + "_" + wh + ".js");
                } else {
                    c.getPlayer().dropMessage("[系统提示]当前与您对话的NPCID为 : " + npc + ".js");
                }
//            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv;
                if (wh != 0) {
                    iv = getInvocable("npc/" + npc + "_" + wh + ".js", c, true);
                } else {
                    iv = getInvocable("npc/" + npc + ".js", c, true);
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm;
                if (wh != 0) {
                    cm = new NPCConversationManager(c, npc, -1, null, (byte) -1, iv, wh);
                } else {
                    cm = new NPCConversationManager(c, npc, -1, null, (byte) -1, iv, 0);
                }
                cms.put(c, cm);
                if ((iv == null) || (getInstance() == null)) {
                    if (wh != 0) {
                        cm.sendOk("很抱歉，我并没有被管理员设置可使用，如果你觉得我应该工作，那就请你汇报给管理员.\r\n我的ID是: #b" + npc + "_" + wh + "#k.");
                    } else {
                        cm.sendOk("很抱歉，我并没有被管理员设置可使用，如果你觉得我应该工作，那就请你汇报给管理员.\r\n我的ID是: #b" + npc + "#k.");
                    }
                    cm.dispose();
                    return;
                }
                scriptengine.put("cm", cm);
                scriptengine.put("npcid", npc);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                dispose(c);
                //c.removeClickedNPC();
            }
        } catch (final Exception e) {
            System.err.println("[NPC脚本] NPC脚本出错（ID : " + npc + "）\r\n错误内容: " + e);
            FileoutputUtil.log("logs/异常/脚本异常.log", "NPC脚本出错（ID : " + npc + "）\r\n错误信息：" + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void start(final MapleClient c, final int npc, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage(5, "对话NPC：" + npc + " 模式：" + script);
            }
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("npc/" + npc + ".js", c, true); //safe disposal
                if (script != null) {
                    iv = getInvocable("special/" + script + ".js", c, true); //safe disposal
                }
                if (iv == null) {
                    System.err.println("[NPC脚本] 找不到NPC脚本(ID:" + npc + "), 特殊模式(" + script + "),所在地图(ID:" + c.getPlayer().getMapId() + ")");
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, (byte) -1, iv, 0);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //String npcmsg = "Started NPC ID: " + npc;
                //String scriptmsg = "Started NPC ID: " + npc + " with script: " + script;
                //System.out.println(script != null ? scriptmsg : npcmsg);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                dispose(c);
                //c.removeClickedNPC();
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("[NPC脚本] NPC脚本出错（ID : " + npc + "）模式：" + script + " \r\n错误内容: " + e);
            FileoutputUtil.log("logs/异常/脚本异常.log", "NPC脚本出错（ID : " + npc + "）模式" + script + ".\r\n错误信息：" + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        action(c, (byte) mode, (byte) type, selection, 0);
    }

    public void action(final MapleClient c, final byte mode, final byte type, final int selection, int wh) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    dispose(c);
                } else if (wh != 0) {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection, wh);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final ScriptException | NoSuchMethodException e) {
                int npcId = cm.getNpc();
                String npcMode = cm.getScript();
                System.err.println("[NPC脚本] NPC脚本出错（ID : " + npcId + "）模式：" + npcMode + "  \r\n错误内容：" + e);
                FileoutputUtil.log("logs/异常/脚本异常.log", "NPC脚本出错（ID : " + npcId + "）模式：" + npcMode + ". \r\n错误信息：" + e);
                dispose(c);
                c.removeClickedNPC();
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isAdmin()) {
                        c.getPlayer().dropMessage(5, "开始任务脚本不存在 NPC：" + npc + " Quest：" + quest);
                    }
                    dispose(c);
                    FileoutputUtil.log("logs/异常/任务脚本异常.txt", "开始任务脚本不存在 NPC：" + npc + " Quest：" + quest);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, (byte) 0, iv, 0);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            } else {
                dispose(c);
                //c.removeClickedNPC();
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("logs/异常/任务脚本异常.txt", "执行任务脚本失败 任务ID: (" + quest + ")..NPCID: " + npc + ". \r\n错误信息: " + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, (byte) 1, iv, 0);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            } else {
                dispose(c);
                //c.removeClickedNPC();
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
            //c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void startItemScript(final MapleClient c, final int npc, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("item/" + script + ".js", c, true);
                if (iv == null) {
                    System.out.println("New scripted item : " + script);
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, (byte) -1, iv, 0);
                cms.put(c, cm);
                scriptengine.put("im", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("use");
            } else {
                dispose(c);
                c.removeClickedNPC();
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Item script, SCRIPT : " + script + ". " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Item script, SCRIPT : " + script + ". " + e);
            dispose(c);
            c.removeClickedNPC();
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                if (npccm.getwh() == 0) {
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                } else {
                    c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + "_" + npccm.getwh() + ".js");
                }
                c.removeScriptEngine("scripts/npc/" + npccm.getScript() + ".js");
                c.removeScriptEngine("scripts/npc/notcoded.js");
            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }
}
