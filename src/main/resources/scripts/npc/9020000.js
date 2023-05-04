var status;
var 最小等级 = 15;
var 最大等级 = 200;
var 最小人数 = 1;
var 最大人数 = 6;
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var text = "";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        text += "#e<组队任务：废弃的都市>#n\r\n";
        text += "你想和队员们一起努力，完成任务吗？\r\n等级在#b" + 最小等级 + " ~ " + 最大等级 + "#k级之间。人数在#b" + 最小人数 + " ~ " + 最大人数 + "#k人之间即可进入。\r\n";
        text += "如果想挑战的话，请让#b所属组队的队长#k来和我说话。\r\n\r\n";
        text += "#L1#" + 蓝色箭头 + "#r[进入副本]#l#k#n\r\n\r\n";
        text += "#L2#" + 蓝色箭头 + "#b我想听一下说明#n#l#k\r\n";

        cm.sendSimple(text);
    } else if (selection == 1) {
        var 队伍 = cm.getParty();
        var 队长 = cm.isLeader();
        /*没有组队*/
        if (队伍 == null) {
            cm.sendOk("你还没有队伍，请组队后和我谈话。");
            cm.dispose();
            return;
        }
        /*不是队长*/
        if (!队长) {
            cm.sendOk("你不是队长，请让队长和我谈话。");
            cm.dispose();
            return;
        }
        /*声明变量party 赋值 获取队伍人数*/
        var 队伍成员 = cm.getParty().getMembers();
        /*声明变量inMap 赋值 获取队伍所有人所在地图*/
        var inMap = cm.partyMembersInMap();
        /*级别有效*/
        var 级别有效 = 0;
        /*循环获取队伍人数*/
        for (var i = 0; i < 队伍成员.size(); i++) {
            /*判断队伍成员等级是否超过限定等级*/
            if (队伍成员.get(i).getLevel() >= 最小等级 && 队伍成员.get(i).getLevel() <= 最大等级)
                级别有效++;
        }
        /*判断初始地图队伍的人数 是否匹配限定人数*/
        if (inMap < 最小人数 || inMap > 最大人数) {
            cm.sendOk("你的队伍人数不足" + 最小人数 + "人.请把你的队伍人员召集到废气都市再进入副本.");
            cm.dispose();
            /*获取当前地图的队伍成员等级是否匹配*/
        } else if (级别有效 != inMap) {
            cm.sendOk("请确保你的队伍人员最小等级在 " + 最小等级 + " 和 " + 最大等级 + "之间. 并且 #b" + 级别有效 + "#k 队伍成员的等级处于正确的范围内.");
            cm.dispose();
        } else {
            /*调用事件*/
            var em = cm.getEventManager("KerningPQ");
            /*判断事件脚本不存在*/
            if (em == null) {
                cm.sendOk("事件发生错误，请联系管理员.");
                cm.dispose();
                return;
            } else {
                /*判断副本地图人数，是否为0*/
                //103000800 - 金银岛 - 组队训练场<1阶段>
                //103000801 - 金银岛 - 组队训练场<2阶段>
                //103000802 - 金银岛 - 组队训练场<3阶段>
                //103000803 - 金银岛 - 组队训练场<4阶段>
                //103000804 - 金银岛 - 组队训练场<最后阶段>
                //103000805 - 金银岛 - 组队训练场<奖金地点>
                //103000890 - 金银岛 - 组队训练场<出口>
                if (cm.getPlayerCount(103000800) <= 0
                        && cm.getPlayerCount(103000801) <= 0
                        && cm.getPlayerCount(103000802) <= 0
                        && cm.getPlayerCount(103000803) <= 0
                        && cm.getPlayerCount(103000804) <= 0) {
                    /*传送队伍进入副本*/
                    em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                } else {
                    cm.sendOk("请稍等...任务正在进行中.");
                    cm.dispose();
                }
            }
        }
    } else if (selection == 2) {
        cm.sendOk("废弃组队任务说明：\r\n废弃都市的组队任务总共有5个关卡,在这个组队任务里，不会做会被人看不起的,下面我将一关一关来为大家讲述！\r\n第一关，进入后找NPC,她会问每人一个问题，答案是一个数字，必须拿到这个数目的证书才可以找她领通行证，然后上去打鳄鱼，打死一只掉一张证书（红色的）。拿够了证书去NPC那里换通行证。队长集齐通行证，点NPC——通过\r\n第二关4条绳子3个人爬，有四种组合，分别空出不同的绳子就行了,同时队长点NPC，会显示错误与通过。\r\n5个台阶3个人站，注意：台阶的编号是台阶上小猫有几只就是几号。检验次序如下：123；124；125；134；135；145；234；235；245；345\r\n第三关站1的人一开始不要乱动，让另外两个人跳就行了，同时队长点NPC ，会显示错误与通过。跳桶的时候需要一定技巧，兄弟悠着点不要急，越急越跳不好。\r\n最终关共要消灭3个风独眼兽，6只青蛇和一个超级大的绿水灵，队长集齐了就去点最上面的右边的NPC，显示通过。此时不要马上出去，每个人都要点击右边这个NPC才可以获得奖励，点之前要确认你的背包的装备，消耗和其他栏分别都至少有一格空着。");
        cm.dispose();
    } else if (selection == 3) {
        /* if (cm.haveItem(4170002, 50)) {
         cm.gainItem(4170002, -50);
         cm.gainItem(1072369, 5, 5, 5, 5, 100, 100, 0, 0, 5, 5, 0, 0, 0, 20);
         cm.即时存档();
         cm.sendOk("兑换成功！");
         cm.喇叭(4, "[" + cm.getPlayer().getName() + "]使用50个副本蛋在废弃组队NPC兑换了【绿黏液鞋子】鞋子！");
         cm.dispose();
         } else {
         cm.sendOk("#z4170002#不足50个无法兑换#v1072369#！");
         cm.dispose();
         }*/
        cm.dispose();
    }
}
