importPackage(Packages.tools);
importPackage(java.awt);

var status;
var curMap;
var random = java.lang.Math.floor(Math.random() * 9 + 1);//0~9  +1��Ϊ0
var random1 = java.lang.Math.floor(Math.random() * 20000 + 1);//������� 0~20000 +1��Ϊ0
var random2 = java.lang.Math.floor(Math.random() * 10 + 1);//���þ����� 0~10 +1��Ϊ0
var questions = Array(
        "��һ�����⣺תְ��սʿ����͵ȼ��Ƕ��٣�\r\n�𰸣�10��#b\r\n���򵹹����ȡ��Ӧ������֤�顣��",
        "��һ�����⣺תְ��սʿ���������ֵ��SEX���Ƕ��٣�\r\n�𰸣�35��#b\r\n���򵹹����ȡ��Ӧ������֤�顣��",
        "��һ�����⣺תְ��ħ��ʦ���������ֵ��INT���Ƕ��٣�\r\n�𰸣�20��#b\r\n���򵹹����ȡ��Ӧ������֤�顣��",
        "��һ�����⣺תְ�ɹ����ֵ��������ֵ��DEX���Ƕ��٣�\r\n�𰸣�25��#b\r\n���򵹹����ȡ��Ӧ������֤�顣��",
        "��һ�����⣺תְ�ɷ������������ֵ��DEX���Ƕ��٣�\r\n�𰸣�25#b\r\n���򵹹����ȡ��Ӧ������֤�顣��",
        "��һ�����⣺�ȼ�1 �����ȼ�2 ����ľ���ֵ�Ƕ��٣�\r\n�𰸣�15��#b\r\n���򵹹����ȡ��Ӧ������֤�顣��");
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
    /*��һ�׶�*/
    if (curMap == 1) {
        /*����ӳ�*/
        if (cm.isLeader()) {
            var eim = cm.getPlayer().getEventInstance();
            party = eim.getPlayers();
            preamble = eim.getProperty("leader1stpreamble");
            if (preamble == null) {
                cm.sendNext("��ã���ӭ������һ���׶Σ�����������ܻ������ܶ��׺ݵ����㣬�һ������ÿһ���˳�һ���⣬�����ٴ��׺ݵ������ȡ��Ӧ��Ŀ��֤�齻���ҡ�֮���һ������һ��ͨ��֤�����ǰ�ͨ��֤������ӳ�����ӳ��ٺ��ҽ������Ϳ���˳��ͨ���ˣ���ôף��һ��˳����");
                eim.setProperty("leader1stpreamble", "done");
                cm.dispose();
            } else {
                var complete = eim.getProperty(curMap + "stageclear");
                if (complete != null) {
                    cm.sendNext("��Ͽ������һ���׶Σ��������Ѿ��򿪣�");
                    cm.dispose();
                } else {
                    /*���˶ӳ����⣬�������еĳ�Ա����Ҫͨ�����ԡ�*/
                    var numpasses = party.size() - 1;
                    var strpasses = "#b" + numpasses + " ͨ��#k";
                    if (!cm.haveItem(4001008, numpasses)) {
                        cm.sendNext("�ܱ�Ǹ,���ͨ��֤��������. ���ռ�ͨ��֤����; ����Ϊ��Ķ�Ա����, " + strpasses + " . ������Ķ�Ա��ȥ�����׶�����㣬�ռ�֤�飬�����Ҷһ�ͨ��֤�󽻸���.�����Ϳ��������һ���ˡ�");
                        cm.dispose();
                    } else {
                        cm.sendNext("���ռ��� " + strpasses + "!ף�ظ������! �һ�Ϊ��򿪵���һ���׶ε���. ��������ʱ�����ƣ��������㡣ף���Ǻ��ˣ�");
                        clear(1, eim, cm);
                        cm.givePartyExp(1000, party);//������Ӿ��� 2000
                        cm.gainItem(4001008, -numpasses);
                        cm.dispose();
                    }
                }
            }
            /*������ӳ�*/
        } else {
            var eim = cm.getPlayer().getEventInstance();
            pstring = "member1stpreamble" + cm.getPlayer().getId();
            preamble = eim.getProperty(pstring);
            if (status == 0) {
                if (preamble == null) {
                    var qstring = "member1st" + cm.getPlayer().getId();
                    var question = eim.getProperty(qstring);
                    if (question == null) {
                        /*ѡ��һ�����������������ҡ�*/
                        var questionNum = Math.floor(Math.random() * questions.length);
                        eim.setProperty(qstring, questionNum);
                    }
                    cm.sendNext("���ˣ�����Ҫ�ռ�#b��Ӧ��Ŀ#k��֤����ҡ�\r\n���������Ҫ�ռ� #b#z4001007##k ��������𰸵�����������ָ�����������㣬���ָ��������֤�飬�����ҾͿ��Զһ�ͨ��֤��Ȼ�󽻸���ӳ�����");
                    /*���⣬����Ƿ������*/
                } else {
                    var complete = eim.getProperty(curMap + "stageclear");
                    if (complete != null) { // 
                        cm.sendNext("���ڿ��Ե���һ���ؿ��ˣ���������Ļ����ſ��ܾ͹ر��ˡ�");
                        cm.dispose();
                    } else {
                        /*�ش���Ҷ����������������ȷ/����ȷ�Ļش�*/
                        var qstring = "member1st" + cm.getPlayer().getId();
                        var numcoupons = qanswers[parseInt(eim.getProperty(qstring))];
                        /*�ж���Ʒ���� 4001007 - ֤�� - ���˹����õ���֤�顣���Ը�ͨ�����֤������*/
                        var qcorr = cm.itemQuantity(4001007);
                        if (numcoupons == qcorr) {
                            cm.sendNext("ף���㣬���Ѿ�������ͨ��֤�����ͨ��֤�����ӳ�֮�󣬰����������Ѱɣ�");
                            cm.gainItem(4001007, -numcoupons);
                            cm.gainItem(4001008, 1);
                        } else
                            cm.sendNext("�Բ����ǲ�����ȷ�Ĵ𰸣��������ı�����֤���������");
                    }
                    cm.dispose();
                }
            } else if (status == 1) {
                if (preamble == null) {
                    var qstring = "member1st" + cm.getPlayer().getId();
                    var question = parseInt(eim.getProperty(qstring));
                    cm.sendNextPrev(questions[question]);
                } else { // ��Ӧ�÷��������������ֱ�Ӵ���
                    cm.dispose();
                }
            } else if (status == 2) { // Preamble completed
                eim.setProperty(pstring, "done");
                cm.dispose();
            }
        } // ������һ�ؽű�
    } else if (2 <= curMap && 4 >= curMap) {
        rectanglestages(cm);
        /*���Ľ׶�*/
    } else if (curMap == 5) {
        var eim = cm.getPlayer().getEventInstance();
        var stage5done = eim.getProperty("5stageclear");
        if (stage5done == null) {
            /*����ӳ�*/
            if (cm.isLeader()) {
                //4001008 - ͨ�����֤ - ��֤�齻�׵õ���ͨ��֤����ͨ�����֤����ȥ����һ��������
                if (cm.haveItem(4001008, 10)) {
                    /*�ӳ��Ի�*/
                    cm.sendNext("�������ͨ�����һ���ؿ��������кܶ����͵Ĺ��������ף�������������ͨ��������ս��");
                    party = eim.getPlayers();
                    cm.gainItem(4001008, -10);
                    clear(5, eim, cm);
                    cm.givePartyExp(15000, party);
                    cm.dispose();
                    /*��û�����*/
                } else {
                    cm.sendNext("��ã���ӭ������5�׶Σ��������ߣ����ܻᷢ�ֺܶ����͵Ĺ��������ǣ���ȡͨ��֤���ٰ����ǽ����ҡ���ס��������ܱ���ǿ��ܶ࣬��С��һ�㣬ף��ͨ����һ�ء�");
                }
                cm.dispose();
                /*��Ա�Ի�*/
            } else {
                cm.sendNext("��ӭ������5�׶Σ��ڵ�ͼ�����ߣ���ͻῴ��������͵Ĺ��������ǻ�ȡ�������ϵ�ͨ��֤���������ǵ���ӳ���");
                cm.dispose();
            }
            /*���轱��*/
        } else {
            if (status == 0) {
                cm.sendNext("�������ţ����������Ա�����������������ս����Ϊ�������ҽ�����һЩ��������ȷ���������������������װ�����Ƿ���һ����Ŀ���ϵĿո�");
            } else if (status == 1) {
                if (cm.isLeader()) {
                    getPrize(eim, cm);
                } else {
                    cm.sendOk("������Ķӳ�������˵��.");
                }
                cm.dispose();
            }
        }
    } else { //��Ч�Ĺؿ�
        cm.sendNext("��Ч�Ĺؿ�������ϵGM��");
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
        nthobj = "����";
        nthverb = "����������";
        nthpos = "����������̫��";
        curArray = stage2Rects;
        curCombo = stage2combos;
        objset = [0, 0, 0, 0];

    } else if (curMap == 3) {
        nthtext = "3rd";
        nthobj = "ƽ̨";
        nthverb = "վ������";
        nthpos = "վ�����Ե̫��";
        curArray = stage3Rects;
        curCombo = stage3combos;
        objset = [0, 0, 0, 0, 0];

    } else if (curMap == 4) {
        nthtext = "4th";
        nthobj = "ľͰ";
        nthverb = "վ������";
        nthpos = "վ�����Ե̫��";
        curArray = stage4Rects;
        curCombo = stage4combos;
        objset = [0, 0, 0, 0, 0, 0];
    }
    /*�������Ի����Ƕӳ�*/
    if (cm.isLeader()) {
        if (status == 0) {
            party = eim.getPlayers();
            preamble = eim.getProperty("leader" + nthtext + "preamble");
            /*��һ�ζԻ�*/
            if (preamble == null) {
                cm.sendNext("��ӭ������ " + nthtext + " �׶�.  �������Աߵ� " + nthobj + "���� ��Ҫ���ľ�������Ķ�Ա������ " + nthobj + " �� " + nthverb + " .#k\r\n��ס " + nthverb + "��ʱ��λ��Ҫ����һ�㡣 Ȼ��ӳ����ң�����ʾ�����ͨ����3����Ա�ֱ�������" + nthverb + "�������ȷ�Ĵ𰸣�����ͨ��");
                eim.setProperty("leader" + nthtext + "preamble", "done");
                var sequenceNum = Math.floor(Math.random() * curCombo.length);
                eim.setProperty("stage" + nthtext + "combo", sequenceNum.toString());
                cm.dispose();
            } else {
                var complete = eim.getProperty(curMap + "stageclear");
                if (complete != null) {
                    cm.sendNext("���㵽��һ���׶Σ��ſ���!");
                    cm.dispose();
                    /*��������ϵ��˺����ǵ�λ��*/
                } else {
                    var ������ = 0;
                    for (var i = 0; i < party.size(); i++) {
                        for (var y = 0; y < curArray.length; y++) {
                            if (curArray[y].contains(party.get(i).getPosition())) {
                                ������++;
                                objset[y] = 1;
                                break;
                            }
                        }
                    }

                    if (������ == 3 || cm.getPlayer().getGMLevel() > 0) {
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
                                    exp = 2000;//�ڶ��ؾ���
                                    break;
                                case 3:
                                    exp = 3000;//�����ؾ���
                                    break;
                                case 4:
                                    exp = 4000;//���Ĺؾ���
                                    break;
                                default:
                                    exp = 1000;
                                    break;
                            }
                            /*������Ӿ���*/
                            cm.givePartyExp(exp, party);
                            cm.dispose();
                        } else { //����
                            failstage(eim, cm);
                            cm.dispose();
                        }
                    } else {
                        cm.sendNext("�㿴��������û�з�����ȷ��λ�ã������٣�����ӳ�Ա�ҵ���ȷ��λ�á�\r\n�����㻹û���ҵ���3���� " + nthverb + " ������һ����ͬ�����" + nthobj + ". ֻ����3�� " + nthverb + " �� " + nthobj + "��, ����� " + nthpos + "����ܲ���һ����ȷ�𰸡�, �������ס��һ�㡣����ǰ��!");
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
        /*���Ի���������Ƕӳ�*/
    } else {
        var complete = eim.getProperty(curMap.toString() + "stageclear");
        /*����Ѿ����*/
        if (complete != null) {
            cm.sendNext("ʱ�䲻���ˣ����㵽����һ���ؿ���");
        } else {//δ���
            cm.sendNext("���������ӳ�����̸����");
        }
        cm.dispose();
    }
}
/*���ս�������*/
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
   // cm.���������Ʒ�ӳ�˫��(4170002, 1, false);//������
    if (random <= 2 && random >= 1) {//���Ƴ��ֽ������þ�ļ���
        cm.gainDY(random2);
        if (cm.isLeader()) {
            //cm.����(2, "��ң�[" + cm.getName() + "]�������Ķ�������˷���������Ӹ�������õ��þ�" + random2)
        }
    } else if (random >= 3 && random <= 4) {//���Ƴ��ֽ�����ҵļ���
        cm.gainMeso(random1);
        if (cm.isLeader()) {
            //cm.����(4, "��ң�[" + cm.getName() + "]�������Ķ�������˷���������Ӹ�������ý�ң�" + random1)
        }
    } else {
        if (cm.isLeader()) {
            //cm.����(4, "��ң�[" + cm.getName() + "]�������Ķ�������˷���������Ӹ�������ý�ң�" + random1)
        }
    }
    var target = eim.getMapInstance(103000805);
    var targetPortal = target.getPortal("st00");
    cm.warpParty(103000805);
    //cm.����(4, "��ң�[" + cm.getName() + "]�������Ķ��������[����������Ӹ���]��")
    cm.dispose();
}