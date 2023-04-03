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
                  cm.sendNext("你想成为一名 #r弓箭手#k 吗？\r\n弓手拥有高敏捷及力量,在战斗中负责远距离攻击,假如弓手职业能巧妙地运用地势的话,打猎可是非常轻松厉害。");
            } else {
                 cm.sendOk("你的等级不足10级。无法转职成为弓箭手。");
                cm.dispose();
            }
        } else {
            if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() === 300) {
                if (cm.getQuestStatus(100000) >= 1) {
                    cm.completeQuest(100002);
                    if (cm.getQuestStatus(100002) === 2) {
                        status = 20;
                        cm.sendNext("我看你干得不错。我会让你在你漫长的道路上迈出下一步。.");
                    } else {
                        if (!cm.haveItem(4031010)) {
                            cm.gainItem(4031010, 1);
                        }
                        cm.sendOk("去看看 #r专职指导书#k.")
                        cm.dispose();
                    }
                } else {
                    status = 10;
                    cm.sendNext("你所取得的进步是惊人的。");
                }
            } else if (cm.getQuestStatus(100100) === 1) {
                cm.completeQuest(100101);
                if (cm.getQuestStatus(100101) === 2) {
                    cm.sendOk("好的，现在把这个拿给 #b赫丽娜#k.");
                } else {
                    cm.sendOk("嘿, #b#h0##k!我需要一个 #bBlack Charm#k. Go and find the Door of Dimension.");
                    cm.startQuest(100101);
                }
                cm.dispose();
            } else {
                cm.sendOk("你选择得很明智。");
                cm.dispose();
            }
        }
    } else if (status === 1) {
        cm.sendNextPrev("这是一个重要而最终的选择。你将无法回头。");
    } else if (status === 2) {
        cm.sendYesNo("你想成为一个 #r弓箭手#k?");
    } else if (status === 3) {
        if (cm.getJob() === 0) {
            cm.resetStats(4, 25, 4, 4);
            cm.expandInventory(1, 4);
            cm.expandInventory(4, 4);
            cm.changeJob(300);
        }
        cm.gainItem(1452002, 1);
        cm.gainItem(2060000, 1000);
        cm.sendOk("是这么回事！现在走吧，带着骄傲去.");
        cm.dispose();
    } else if (status === 11) {
        cm.sendNextPrev("你可能已经准备好迈出下一步了。#r猎人#k or #r弩手#k.")
    } else if (status === 12) {
        cm.askAcceptDecline("但首先我必须测试你的技能。你准备好了吗?");
    } else if (status === 13) {
        cm.startQuest(100000);
        cm.gainItem(4031010, 1);
        cm.sendOk("去看#b转职教练#k 她在射手村. 他会指引你方向的。.");
        cm.dispose();
    } else if (status === 21) {
        cm.sendSimple("你想成为什么样的人？?#b\r\n#L0#猎人#l\r\n#L1#弩手#l#k");
    } else if (status === 22) {
        var jobName;
        if (selection === 0) {
            jobName = "猎人";
            job = 310;
        } else {
            jobName = "弩手";
            job = 320;
        }
        cm.sendYesNo("你想成为一个 #r" + jobName + "#k?");
    } else if (status === 23) {
        cm.changeJob(job);
        cm.gainItem(4031012, -1);
        cm.sendOk("是这么回事！现在走吧，带着骄傲去。");
    }
}
