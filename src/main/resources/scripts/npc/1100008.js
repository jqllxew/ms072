var status = -1;

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else
    if (status === 0) {
        cm.sendNext("不感兴趣？哦。.");
        cm.dispose();
        status--;
    }
    if (status === 0) {
        cm.sendYesNo("这艘船将驶向 #b#m130000000##k, 一个岛，你会发现红色的叶子沐浴阳光，轻柔的微风，漫过了小溪，和枫后，天鹅座。如果你有兴趣加入天鹅骑士感兴趣，那么你一定要去那里。你对来访感兴趣吗？ #m130000000#?\r\n\r\n旅行将花费你的 #b1000#k 金币.");
    } else if (status === 1) {
        cm.warp(130000210, 0);
        cm.dispose();
    }
}
