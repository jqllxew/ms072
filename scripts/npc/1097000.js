load("nashorn:mozilla_compat.js");
importPackage(java.lang);

var status = 0;
var minLevel = 30;
var maxLevel = 250;
var minPlayers = 1;
var maxPlayers = 6;
var open = false;
var PQ = 'coockpq';

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (status >= 1 && mode === 0) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }
    if (mode === 1)
        status++;
    else
        status--;

    if (status === 0) {
        if (cm.getPlayer().getMapId() != 910002000) {
            cm.sendSimple("你想为诺第留斯号船上的船员做一些可口的饭菜吗？我可以教你怎么做.#b\r\n#L0#去派对探索大厅.")
        } else if (cm.getPlayer().getMapId() === 910002000) {
            cm.sendSimple("你想为诺第留斯号船上的船员做一些可口的饭菜吗？我可以教你怎么做.\r\n #b#L1# 开始做Tangyoon.#l \r\n #b#L4# 得到Tangyoon的厨师服.#l \r\n #L3# 听关于烹饪和Tangyoon的解释。#l \r\n #L5#看看今天的剩余挑战计数.#l");
        } else {
            cm.dispose();
        }
    } else if (status === 1) {
        if (selection === 0) {
            cm.saveLocation("MULUNG_TC");
            cm.warp(910002000, 0);
            cm.dispose();
        } else if (selection === 1) {
            if (cm.getParty() === null) {
                cm.sendSimple("这个地方被满月的神秘光环包围着，所以你不能自己进来。如果你要进来，你的党领袖必须和我谈谈。");
                cm.dispose();
            } else if (!cm.isLeader()) {
                cm.sendOk("请对账说话.");
                cm.dispose();
            } else if (cm.getPQLog(PQ) >= 10) {
                cm.sendOk("对不起的.您已经超过了今天的最大尝试次数。请明天再来。.");
                cm.dispose();
            } else if (!cm.allMembersHere()) {
                cm.sendSimple("很抱歉，你参加的聚会至少有2个成员。请调整你的聚会，以确保你的聚会至少有2个成员，都在30级或更高级别。等你做完后再告诉我。");
                cm.dispose();
            } else {

                var party = cm.getParty().getMembers();
                var mapId = cm.getMapId();
                var next = true;
                var levelValid = 0;
                var inMap = 0;
                var it = party.iterator();

                while (it.hasNext()) {
                    var cPlayer = it.next();
                    if (cPlayer.getLevel() >= minLevel && cPlayer.getLevel() <= maxLevel) {
                        levelValid += 1;
                    } else {
                        cm.sendOk("你需要介于两者之间。 " + minLevel + " 和 " + maxLevel + " 接受这史诗般的挑战!");
                        cm.dispose();
                        next = false;
                    }
                    if (cPlayer.getMapid() === mapId) {
                        inMap += 1;
                    }
                }
                if (party.size() > maxPlayers || inMap < minPlayers) {
                    next = false;
                }
                if (next) {
                    var em = cm.getEventManager("CookingPQ");
                    if (em === null || open === false) {
                        cm.sendSimple("此PQ当前不打开。.");
                        cm.dispose();
                    } else {
                        var prop = em.getProperty("state");
                        if (prop === null || prop.equals("0")) {
                            em.startInstance(cm.getParty(), cm.getMap(), 70);
                        } else {
                            cm.sendSimple("有人已经在尝试PQ。请等待他们完成，或者找到另一个频道。.");
                        }
                        cm.removeAll(4001453);
                        cm.setPQLog(PQ);
                        cm.dispose();
                    }
                } else {
                    cm.sendYesNo("Your party is not a party between " + minPlayers + " and " + maxPlayers + " party members. Please come back when you have between " + minPlayers + " and " + maxPlayers + " party members.");
                }
            }
        } else if (selection === 3) {
            cm.sendOk("#e <Party Quest: Moon Bunny's Rice Cake>#n \r\n A mysterious Moon Bunny that only appears in #b#m910010000##k durning full moons. #b#p1012112##k of #b#m100000200##k is looking for Maplers to find #rMoon Bunny's Rice Cake#k for #b#p1012114##k. If you want to meet the Moon Bunny, plant Primrose Seeds in the designated locations and summon forth a full moon. Protect the Moon Bunny from wild animals until all #r10 Rice Cakes#k are made.\r\n #e - Level:#n 10 or above #r (Recommended Level: 10 - 20)#k \r\n #e - Time Limit:#n 10 min \r\n #e - Number of Participants:#n 3 to 6 \r\n #e - Reward:#n #i1003266:# Rice Cake Topper #b \r\n(obtained by giving Tory 100 Rice Cakes)#k \r\n #e - Items:#n #i1002798:# A Rice Cake on Top of My Head #b \r\n(obtained by giving Tory 10 Rice Cakes).");
            cm.dispose();
        } else if (selection === 4) {
            cm.sendOk("Oh, my! You brought Moon Bunny's Rice Cakes for me? Well, I've prepared some gifts to show you my appreciation. How many rice cakes do you want to give me?#b\r\n#L10#Moon Bunny's Rice Cake x10 - A Rice Cake on Top of My Head#l\r\n#L11#Moon Bunny's Rice Cake x100 - Rice Cake Topper");
        } else if (selection === 5) {
            var pqtry = 10 - cm.getPQLog(PQ);
            if (pqtry >= 10) {
                cm.sendOk("Sorry you have exceeded the maximum number of tries for today. Please come back tomorrow.");
                cm.dispose();
            } else {
                cm.sendOk("You can do this quest 10 times a day. You have done it " + pqtry + " time(s) today.");
                cm.dispose();
            }
        }
    } else if (status === 2) {
        if (selection === 10) {
            if (!cm.canHold(1002798, 1)) {
                cm.sendOk("Make room for this Hat.");
            } else if (cm.haveItem(4001101, 10)) {
                cm.gainItem(1002798, 1);
                cm.gainItem(4001101, -10);
                cm.sendOk("Thank you so much. I'm really going to enjoy these cakes!");
                cm.dispose();
            } else {
                cm.sendOk("Please make sure you have the amount of cakes needed.");
                cm.dispose();
            }
        } else if (selection === 11) {
            if (!cm.canHold(1003266, 1)) {
                cm.sendOk("Make room for this Hat.");
            } else if (cm.haveItem(4001101, 100)) {
                cm.gainItem(1003266, 1);
                cm.gainItem(4001101, -100);
                cm.sendOk("Thank you so much. I'm really going to enjoy these cakes!");
                cm.dispose();
            } else {
                cm.sendOk("Please make sure you have the amount of cakes needed.");
                cm.dispose();
            }
        }
        if (mode === 0) {
            cm.dispose();
        }
    }
}
