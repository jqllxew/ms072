var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 1)
            status++;
        else
            status--;

        if (cm.haveItem(4031011)) {
            if (status == 0)
                cm.sendNext("噢, 你是 #b达克鲁#k 介绍来的吗")
            else if (status == 1)
                cm.sendNextPrev("所以你要证明你的实力吗 ? 很好...");
            else if (status == 2)
                cm.sendNextPrev("我可以给你一次机会,请你把握.");
            else if (status == 3)
                cm.sendYesNo("请给我 #b30 #t4031013##k. 祝你好运.");
            else if (status == 4) {
                cm.warp(108000400, 0);
                cm.dispose();
            }
        } else {
            cm.sendOk("很抱歉,我需要 #b达克鲁的信件#k 请去找达克鲁拿取谢谢");
            cm.dispose();
        }
    }
}	