importPackage(Packages.tools);
importPackage(java.awt);

var status;
var curMap;
var random = java.lang.Math.floor(Math.random() * 9 + 1);//0~9  +1不为0
var random1 = java.lang.Math.floor(Math.random() * 20000 + 1);//金币数量 0~20000 +1不为0
var random2 = java.lang.Math.floor(Math.random() * 10 + 1);//抵用卷数量 0~10 +1不为0
var questions = Array(
        "第一个问题：转职成战士的最低等级是多少？\r\n答案：10个#b\r\n（打倒怪物，获取相应数量的证书。）",
        "第一个问题：转职成战士的最低力量值（SEX）是多少？\r\n答案：35个#b\r\n（打倒怪物，获取相应数量的证书。）",
        "第一个问题：转职成魔法师的最低智力值（INT）是多少？\r\n答案：20个#b\r\n（打倒怪物，获取相应数量的证书。）",
        "第一个问题：转职成弓箭手的最低敏捷值（DEX）是多少？\r\n答案：25个#b\r\n（打倒怪物，获取相应数量的证书。）",
        "第一个问题：转职成飞侠的最低敏捷值（DEX）是多少？\r\n答案：25#b\r\n（打倒怪物，获取相应数量的证书。）",
        "第一个问题：等级1 ～　等级2 所需的经验值是多少？\r\n答案：15个#b\r\n（打倒怪物，获取相应数量的证书。）");
var qanswers = Array(10, 35, 20, 25, 25, 15);
var party;
var preamble;

var stage2Rects = Array(
        Rectangle(-770, -132, 28, 178),
        Rectangle(-733, -337, 26, 105),
        Rectangle(-601, -328, 29, 105),
        Rectangle(-495, -125, 24, 165));

var stage3Rects = Array(
        Rectangle(608, -180, 140, 50),
        Rectangle(791, -117, 140, 45),
        Rectangle(958, -180, 140, 50),
        Rectangle(876, -238, 140, 45),
        Rectangle(702, -238, 140, 45));

var stage4Rects = Array(
        Rectangle(910, -236, 35, 5),
        Rectangle(877, -184, 35, 5),
        Rectangle(946, -184, 35, 5),
        Rectangle(845, -132, 35, 5),
        Rectangle(910, -132, 35, 5),
        Rectangle(981, -132, 35, 5));

var stage2combos = Array(
        Array(0, 1, 1, 1),
        Array(1, 0, 1, 1),
        Array(1, 1, 0, 1),
        Array(1, 1, 1, 0));

var stage3combos = Array(
        Array(0, 0, 1, 1, 1),
        Array(0, 1, 0, 1, 1),
        Array(0, 1, 1, 0, 1),
        Array(0, 1, 1, 1, 0),
        Array(1, 0, 0, 1, 1),
        Array(1, 0, 1, 0, 1),
        Array(1, 0, 1, 1, 0),
        Array(1, 1, 0, 0, 1),
        Array(1, 1, 0, 1, 0),
        Array(1, 1, 1, 0, 0));

var stage4combos = Array(
        Array(0, 0, 0, 1, 1, 1),
        Array(0, 0, 1, 0, 1, 1),
        Array(0, 0, 1, 1, 0, 1),
        Array(0, 0, 1, 1, 1, 0),
        Array(0, 1, 0, 0, 1, 1),
        Array(0, 1, 0, 1, 0, 1),
        Array(0, 1, 0, 1, 1, 0),
        Array(0, 1, 1, 0, 0, 1),
        Array(0, 1, 1, 0, 1, 0),
        Array(0, 1, 1, 1, 0, 0),
        Array(1, 0, 0, 0, 1, 1),
        Array(1, 0, 0, 1, 0, 1),
        Array(1, 0, 0, 1, 1, 0),
        Array(1, 0, 1, 0, 0, 1),
        Array(1, 0, 1, 0, 1, 0),
        Array(1, 0, 1, 1, 0, 0),
        Array(1, 1, 0, 0, 0, 1),
        Array(1, 1, 0, 0, 1, 0),
        Array(1, 1, 0, 1, 0, 0),
        Array(1, 1, 1, 0, 0, 0));

var eye = 9300002;
var necki = 9300000;
var slime = 9300003;
var monsterIds = Array(
        eye,
        eye,
        eye,
        necki,
        necki,
        necki,
        necki,
        necki,
        necki,
        slime);

var prizeIdScroll = Array(
        2040502,
        2040505,
        2040802,
        2040002,
        2040402,
        2040602);

var prizeIdUse = Array(
        2000001,
        2000002,
        2000003,
        2000006,
        2000004,
        2022000,
        2022003);

var prizeQtyUse = Array(
        80,
        80,
        80,
        50,
        5,
        15,
        15);

var prizeIdEquip = Array(
        1032004,
        1032005,
        1032009,
        1032006,
        1032007,
        1032010,
        1032002,
        1002026,
        1002089,
        1002090);

var prizeIdEtc = Array(
        4010000,
        4010001,
        4010002,
        4010003,
        4010004,
        4010005,
        4010006,
        4020000,
        4020001,
        4020002,
        4020003,
        4020004,
        4020005,
        4020006,
        4020007,
        4020008,
        4003000);

var prizeQtyEtc = Array(
        15,
        15,
        15,
        15,
        8,
        8,
        8,
        8,
        8,
        8,
        8,
        8,
        8,
        8,
        3,
        3,
        30);

function start() {
    status = -1;
    curMap = cm.getPlayer().getMapId() - 103000799;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (type == 0 && mode == 0)
        status--;
    else {
        cm.dispose();
        return;
    }
    /*第一阶段*/
    if (curMap == 1) {
        /*是组队长*/
        if (cm.isLeader()) {
            var eim = cm.getPlayer().getEventInstance();
            party = eim.getPlayers();
            preamble = eim.getProperty("leader1stpreamble");
            if (preamble == null) {
                cm.sendNext("你好，欢迎来到第一个阶段，在这里你可能会遇到很多凶狠的鳄鱼，我会给你们每一个人出一道题，你们再打倒凶狠的鳄鱼获取相应数目的证书交给我。之后我会给你们一张通行证，你们把通行证交给组队长，组队长再和我讲话，就可以顺利通关了，那么祝你一切顺利！");
                eim.setProperty("leader1stpreamble", "done");
                cm.dispose();
            } else {
                var complete = eim.getProperty(curMap + "stageclear");
                if (complete != null) {
                    cm.sendNext("请赶快进入下一个阶段，传送门已经打开！");
                    cm.dispose();
                } else {
                    /*除了队长以外，队伍所有的成员都需要通过考试。*/
                    var numpasses = party.size() - 1;
                    var strpasses = "#b" + numpasses + " 通过#k";
                    if (!cm.haveItem(4001008, numpasses)) {
                        cm.sendNext("很抱歉,你的通行证数量不符. 请收集通行证给我; 数量为你的队员人数, " + strpasses + " . 告诉你的队员，去打猎凶恶的鳄鱼，收集证书，来找我兑换通行证后交给你.这样就可以完成这一关了。");
                        cm.dispose();
                    } else {
                        cm.sendNext("你收集好 " + strpasses + "!祝贺副本完成! 我会为你打开到下一个阶段的门. 到那里有时间限制，所以请快点。祝你们好运！");
                        clear(1, eim, cm);
                        cm.givePartyExp(1000, party);//给予组队经验 2000
                        cm.gainItem(4001008, -numpasses);
                        cm.dispose();
                    }
                }
            }
            /*不是组队长*/
        } else {
            var eim = cm.getPlayer().getEventInstance();
            pstring = "member1stpreamble" + cm.getPlayer().getId();
            preamble = eim.getProperty(pstring);
            if (status == 0) {
                if (preamble == null) {
                    var qstring = "member1st" + cm.getPlayer().getId();
                    var question = eim.getProperty(qstring);
                    if (question == null) {
                        /*选择一个随机的问题来问玩家。*/
                        var questionNum = Math.floor(Math.random() * questions.length);
                        eim.setProperty(qstring, questionNum);
                    }
                    cm.sendNext("好了，你需要收集#b相应数目#k的证书给我。\r\n在这里，你需要收集 #b#z4001007##k 根据问题答案的数量，打猎指定数量的鳄鱼，获得指定数量的证书，交给我就可以兑换通行证，然后交给你队长即可");
                    /*此外，检查是分期完成*/
                } else {
                    var complete = eim.getProperty(curMap + "stageclear");
                    if (complete != null) { // 
                        cm.sendNext("现在可以到下一个关卡了，如果不快点的话，门可能就关闭了。");
                        cm.dispose();
                    } else {
                        /*回答玩家对他们所问问题的正确/不正确的回答*/
                        var qstring = "member1st" + cm.getPlayer().getId();
                        var numcoupons = qanswers[parseInt(eim.getProperty(qstring))];
                        /*判断物品数量 4001007 - 证书 - 打退怪物后得到的证书。可以跟通行许可证交换。*/
                        var qcorr = cm.itemQuantity(4001007);
                        if (numcoupons == qcorr) {
                            cm.sendNext("祝贺你，我已经给你了通行证，请把通行证交给队长之后，帮助其它队友吧！");
                            cm.gainItem(4001007, -numcoupons);
                            cm.gainItem(4001008, 1);
                        } else
                            cm.sendNext("对不起，那不是正确的答案！请检查您的背包中证书的数量。");
                    }
                    cm.dispose();
                }
            } else if (status == 1) {
                if (preamble == null) {
                    var qstring = "member1st" + cm.getPlayer().getId();
                    var question = parseInt(eim.getProperty(qstring));
                    cm.sendNextPrev(questions[question]);
                } else { // 不应该发生。如果发生，直接处理
                    cm.dispose();
                }
            } else if (status == 2) { // Preamble completed
                eim.setProperty(pstring, "done");
                cm.dispose();
            }
        } // 结束第一关脚本
    } else if (2 <= curMap && 4 >= curMap) {
        rectanglestages(cm);
        /*最后的阶段*/
    } else if (curMap == 5) {
        var eim = cm.getPlayer().getEventInstance();
        var stage5done = eim.getProperty("5stageclear");
        if (stage5done == null) {
            /*是组队长*/
            if (cm.isLeader()) {
                //4001008 - 通行许可证 - 与证书交易得到的通行证。有通行许可证可以去到下一个场所。
                if (cm.haveItem(4001008, 10)) {
                    /*队长对话*/
                    cm.sendNext("这里可以通过最后一个关卡，这里有很多凶猛的怪物，我衷心祝福你和你的组队能通过这项挑战。");
                    party = eim.getPlayers();
                    cm.gainItem(4001008, -10);
                    clear(5, eim, cm);
                    cm.givePartyExp(15000, party);
                    cm.dispose();
                    /*还没有完成*/
                } else {
                    cm.sendNext("你好，欢迎来到第5阶段，到处走走，可能会发现很多凶猛的怪物，打败它们，获取通行证，再把他们交给我。记住，怪物可能比你强大很多，请小心一点，祝你通过这一关。");
                }
                cm.dispose();
                /*队员对话*/
            } else {
                cm.sendNext("欢迎来到第5阶段，在地图上走走，你就会看见许多凶猛的怪物，打败他们获取他们身上的通行证，交给你们的组队长。");
                cm.dispose();
            }
            /*给予奖励*/
        } else {
            if (status == 0) {
                cm.sendNext("不敢相信！你和你的组队员们终于完成了所有挑战！做为奖励，我将送你一些东西，请确保你的消耗栏、其它栏、装备栏是否有一个栏目以上的空格？");
            } else if (status == 1) {
                if (cm.isLeader()) {
                    getPrize(eim, cm);
                } else {
                    cm.sendOk("请让你的队长来跟我说话.");
                }
                cm.dispose();
            }
        }
    } else { //无效的关卡
        cm.sendNext("无效的关卡，请联系GM！");
        cm.dispose();
    }
}

function clear(stage, eim, cm) {
    eim.setProperty(stage.toString() + "stageclear", "true");

    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
    cm.environmentChange(true, "gate");

    var mf = eim.getMapFactory();
    map = mf.getMap(103000800 + stage);
    var nextStage = eim.getMapFactory().getMap(103000800 + stage);
    var portal = nextStage.getPortal("next00");
    if (portal != null) {
        portal.setScriptName("kpq" + (stage + 1).toString());
    }
}

function failstage(eim, cm) {
    var map = eim.getMapInstance(cm.getPlayer().getMapId());
    cm.showEffect(true, "quest/party/wrong_kor");
    cm.playSound(true, "Party1/Failed");
}

function rectanglestages(cm) {
    var eim = cm.getPlayer().getEventInstance();
    var nthtext;
    var nthobj;
    var nthverb;
    var nthpos;
    var curArray;
    var curCombo;
    var objset;

    if (curMap == 2) {
        nthtext = "2nd";
        nthobj = "绳索";
        nthverb = "悬挂在上面";
        nthpos = "悬挂在绳索太低";
        curArray = stage2Rects;
        curCombo = stage2combos;
        objset = [0, 0, 0, 0];

    } else if (curMap == 3) {
        nthtext = "3rd";
        nthobj = "平台";
        nthverb = "站在上面";
        nthpos = "站得离边缘太近";
        curArray = stage3Rects;
        curCombo = stage3combos;
        objset = [0, 0, 0, 0, 0];

    } else if (curMap == 4) {
        nthtext = "4th";
        nthobj = "木桶";
        nthverb = "站在上面";
        nthpos = "站得离边缘太近";
        curArray = stage4Rects;
        curCombo = stage4combos;
        objset = [0, 0, 0, 0, 0, 0];
    }
    /*检测如果对话的是队长*/
    if (cm.isLeader()) {
        if (status == 0) {
            party = eim.getPlayers();
            preamble = eim.getProperty("leader" + nthtext + "preamble");
            /*第一次对话*/
            if (preamble == null) {
                cm.sendNext("欢迎来到第 " + nthtext + " 阶段.  看到我旁边的 " + nthobj + "了吗？ 你要做的就是让你的队员，爬上 " + nthobj + " 和 " + nthverb + " .#k\r\n记住 " + nthverb + "的时候，位置要调整一点。 然后队长点我，会提示错误和通过。3个队员分别随机组合" + nthverb + "。获得正确的答案，即可通关");
                eim.setProperty("leader" + nthtext + "preamble", "done");
                var sequenceNum = Math.floor(Math.random() * curCombo.length);
                eim.setProperty("stage" + nthtext + "combo", sequenceNum.toString());
                cm.dispose();
            } else {
                var complete = eim.getProperty(curMap + "stageclear");
                if (complete != null) {
                    cm.sendNext("请快点到下一个阶段，门开了!");
                    cm.dispose();
                    /*检查绳索上的人和他们的位置*/
                } else {
                    var 玩家组合 = 0;
                    for (var i = 0; i < party.size(); i++) {
                        for (var y = 0; y < curArray.length; y++) {
                            if (curArray[y].contains(party.get(i).getPosition())) {
                                玩家组合++;
                                objset[y] = 1;
                                break;
                            }
                        }
                    }

                    if (玩家组合 == 3 || cm.getPlayer().getGMLevel() > 0) {
                        var combo = curCombo[parseInt(eim.getProperty("stage" + nthtext + "combo"))];
                        var correctCombo = true;
                        for (i = 0; i < objset.length && correctCombo; i++)
                            if (combo[i] != objset[i])
                                correctCombo = false;

                        if (correctCombo || cm.getPlayer().getGMLevel() > 0) {
                            clear(curMap, eim, cm);
                            var exp = 0;
                            switch (curMap) {
                                case 2:
                                    exp = 2000;//第二关经验
                                    break;
                                case 3:
                                    exp = 3000;//第三关经验
                                    break;
                                case 4:
                                    exp = 4000;//第四关经验
                                    break;
                                default:
                                    exp = 1000;
                                    break;
                            }
                            /*给予组队经验*/
                            cm.givePartyExp(exp, party);
                            cm.dispose();
                        } else { //错误
                            failstage(eim, cm);
                            cm.dispose();
                        }
                    } else {
                        cm.sendNext("你看起来好像没有发现正确的位置，别气馁，让组队成员找到正确的位置。\r\n看来你还没有找到。3个人 " + nthverb + " ，请想一个不同的组合" + nthobj + ". 只允许3个 " + nthverb + " 在 " + nthobj + "上, 如果你 " + nthpos + "这可能不是一个正确答案。, 所以请记住这一点。继续前进!");
                        cm.dispose();
                    }
                }
            }
        } else {
            var complete = eim.getProperty(curMap + "stageclear");
            if (complete != null) {
                var target = eim.getMapInstance(103000800 + curMap);
                var targetPortal = target.getPortal("st00");
                cm.getPlayer().changeMap(target, targetPortal);
            }
            cm.dispose();
        }
        /*检测对话的如果不是队长*/
    } else {
        var complete = eim.getProperty(curMap.toString() + "stageclear");
        /*如果已经完成*/
        if (complete != null) {
            cm.sendNext("时间不多了，请快点到达下一个关卡。");
        } else {//未完成
            cm.sendNext("请让你的组队长和我谈话。");
        }
        cm.dispose();
    }
}
/*最终奖励设置*/
function getPrize(eim, cm) {
    var itemSetSel = Math.random();
    var itemSet;
    var itemSetQty;
    var hasQty = false;
    if (itemSetSel < 0.3)
        itemSet = prizeIdScroll;
    else if (itemSetSel < 0.6)
        itemSet = prizeIdEquip;
    else if (itemSetSel < 0.9) {
        itemSet = prizeIdUse;
        itemSetQty = prizeQtyUse;
        hasQty = true;
    } else {
        itemSet = prizeIdEtc;
        itemSetQty = prizeQtyEtc;
        hasQty = true;
    }
    var sel = Math.floor(Math.random() * itemSet.length);
    var qty = 1;
    if (hasQty)
        qty = itemSetQty[sel];
    cm.gainItem(itemSet[sel], qty);
   // cm.给予组队物品队长双倍(4170002, 1, false);//副本蛋
    if (random <= 2 && random >= 1) {//控制出现奖励抵用卷的几率
        cm.gainDY(random2);
        if (cm.isLeader()) {
            //cm.喇叭(2, "玩家：[" + cm.getName() + "]带领他的队伍完成了废弃都市组队副本！获得抵用卷：" + random2)
        }
    } else if (random >= 3 && random <= 4) {//控制出现奖励金币的几率
        cm.gainMeso(random1);
        if (cm.isLeader()) {
            //cm.喇叭(4, "玩家：[" + cm.getName() + "]带领他的队伍完成了废弃都市组队副本！获得金币：" + random1)
        }
    } else {
        if (cm.isLeader()) {
            //cm.喇叭(4, "玩家：[" + cm.getName() + "]带领他的队伍完成了废弃都市组队副本！获得金币：" + random1)
        }
    }
    var target = eim.getMapInstance(103000805);
    var targetPortal = target.getPortal("st00");
    cm.warpParty(103000805);
    //cm.喇叭(4, "玩家：[" + cm.getName() + "]带领他的队伍完成了[废弃都市组队副本]！")
    cm.dispose();
}