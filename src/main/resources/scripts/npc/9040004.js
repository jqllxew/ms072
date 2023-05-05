function start() {
    cm.sendSimple("#L0##r家族排行榜\r\n\r\n#L1##g等级排行榜\r\n\r\n#L2##b金币排行榜#l\r\n\r\n#L3##b人气排行榜#l");
}

function action(mode, type, selection) {
    if (selection == 0) {
        cm.家族排行榜();
        cm.dispose();
    } else if (selection == 1) {
        cm.等级排行榜();
        cm.dispose();
    } else if (selection == 2) {
        cm.金币排行榜();
        cm.dispose();
    } else if (selection == 3) {
        cm.人气排行榜();
        cm.dispose();
    }
    cm.dispose();
}
