function action(mode, type, selection) {
    if (cm.getPlayer().getParty() != null) { //判断是否有组队
        if (isLeader() == true) {
            cm.removeAll(4001022);
            cm.removeAll(4001023);
            cm.getPlayer().endPartyQuest(1202); //might be a bad implentation.. incase they dc or something
            //cm.给予组队物品队长双倍(4170005, 1, false); //玩具副本蛋
            cm.gainExp(+2000);
            cm.warpParty(922010000);
            cm.喇叭(3, "[" + cm.getPlayer().getName() + "]成功通关【组队任务 - 玩具城组队】获得奖励！");
            cm.dispose();
        } else {
            cm.sendOk("“队长”必须在这里,请让他和我说话。");
            cm.dispose();
        }
    } else {
        cm.sendOk("你没有队伍！");
        cm.dispose();
    }
}

function isLeader() {
    if (cm.getParty() == null)
        return false;
    return cm.isLeader();
}