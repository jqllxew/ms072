var status = 0;
var job;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 0 && status === 2) {
        cm.sendOk("决定了再来找我");
        cm.dispose();
        return;
    }
    if (mode === 1)
        status++;
    else
        status--;
    if (status === 0) {
        if (cm.getJob() === 0) {
            if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() === 0) {
                cm.sendNext("你想成为一名 #r战士#k 吗？\r\n战士拥有很强的攻击力和体力,因此在战斗中处于非常重要的地位.因为基本攻击很强,所以学习高级技能的话可以发挥超强的力量。");
            } else {
                cm.sendOk("你的等级不足10级。无法转职成为战士。");
                cm.dispose();
            }
        } else {
            if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() === 100) {
                if (cm.getQuestStatus(100003) >= 1) {
                    cm.completeQuest(100005);
                    if (cm.getQuestStatus(100005) === 2) {
                        status = 20;
                        cm.sendNext("我看你干得不错。我会让你在你漫长的道路上迈出下一步。.");
                    } else {
                        if (!cm.haveItem(4031008)) {
                            cm.gainItem(4031008, 1);
                        }
                       cm.sendNext("请去找 #r战士二转教官#k.他就在 #r金银岛 - 西部岩山Ⅳ#k.");
                        cm.dispose();
                    }
                } else {
                    status = 10;
                   cm.sendNext("你已经可以转职了,要转职请点下一页.");
                }
            } else if (cm.getQuestStatus(100100) === 1) {
                cm.completeQuest(100101);
                if (cm.getQuestStatus(100101) === 2) {
                    cm.sendOk("好了，现在把这# 太拉斯 # K.");
                } else {
                    cm.sendOk("Hey, #b#h0##k! I need a #bBlack Charm#k. Go and find the Door of Dimension.");
                    cm.startQuest(100101);
                }
                cm.dispose();
            } else {
                cm.sendOk("你选择得很明智。");
                cm.dispose();
            }
        }
    } else if (status === 1) {
        cm.sendNextPrev("这是一个重要而最终的选择。你将无法回头。.");
    } else if (status === 2) {
        cm.sendYesNo("你想成为一个 #r战士#k?");
    } else if (status === 3) {
        if (cm.getJob() === 0) {
            cm.resetStats(35, 4, 4, 4);
            cm.expandInventory(1, 4);
            cm.expandInventory(4, 4);
            cm.changeJob(100);
        }
        cm.gainItem(1402001, 1);
        cm.sendOk("是这么回事！现在走吧，带着骄傲去.");
        cm.dispose();
    } else if (status === 11) {
        cm.sendNextPrev("你可能已经准备好迈出下一步了。 #r战士#k, #r准骑士#k or #r枪战士#k.")
    } else if (status === 12) {
        cm.askAcceptDecline("但首先我必须测试你的技能。你准备好了吗?");
    } else if (status === 13) {
        cm.gainItem(4031008, 1);
        cm.startQuest(100003);
         cm.sendNext("请去找 #r战士二转教官#k.他就在 #r金银岛 - 西部岩山Ⅳ#k.");
        cm.dispose();
    } else if (status === 21) {
        cm.sendSimple("你想成为什么样的人？?#b\r\n#L0#剑客  #l\r\n#L1#准骑士#l\r\n#L2#枪战士#l#k");
    } else if (status === 22) {
        var jobName;
        if (selection === 0) {
            jobName = "剑客  ";
            job = 110;
        } else if (selection === 1) {
            jobName = "准骑士";
            job = 120;
        } else {
            jobName = "枪战士";
            job = 130;
        }
        cm.sendYesNo("你想成为一个 #r" + jobName + "#k?");
    } else if (status === 23) {
        cm.changeJob(job);
        cm.gainItem(4031012, -1);
        cm.sendOk("是这么回事！现在走吧，带着骄傲去.");
        cm.dispose();
    }
}
