var status = -1;

function start() {
    if (cm.c.getPlayer().getMapId() === 0 || cm.c.getPlayer().getMapId() === 3)
        cm.sendYesNo("欢迎来到冒险岛世界。这个训练营的目的是帮助初学者。你想参加这个训练营吗？有些人不参加训练就开始旅行。但我强烈建议你先参加培训计划。");
    else
        cm.sendNext("这是你的第一个训练计划开始的影像室。在这个房间里，你可以提前看看你选择的工作。");
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (mode === 0 && status === 0) {
            cm.sendYesNo("你真的想马上开始旅行吗？?");
            return;
        } else if (mode === 0 && status === 1 && type === 0) {
            status -= 2;
            start();
            return;
        } else if (mode === 0 && status === 1 && type === 1)
            cm.sendNext("当你最终做出决定时，请再对我说一次。.");
        cm.dispose();
        return;
    }
    if (cm.c.getPlayer().getMapId() === 0 || cm.c.getPlayer().getMapId() === 3) {
        if (status === 0) {
            cm.sendNext("好吧，那我就让你进入训练营吧。请听从老师的指挥。.");
        } else if (status === 1 && type === 1) {
            cm.sendNext("看来你想开始你的旅程而不参加培训计划。然后，我会让你转到训练场。小心~");
        } else if (status === 1) {
            cm.warp(1);
            dispose();
        } else {
            cm.warp(40000);
            dispose();
        }
    } else
    if (status === 0)
        cm.sendPrev("一旦你努力训练，你就有资格从事一项工作。你可以在Henesys成为一个弓箭手，在Ellinia的一个魔术师，在他的一个战士，在废弃都市的飞侠...");
    else
        cm.dispose();
}
