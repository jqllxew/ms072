var status = -1;

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status === 0) {
        cm.sendNextNoESC("那么，你为什么要去枫叶岛呢？这些天来的人不多。根据你的衣着判断，你也不是游客。.");
    } else if (status === 1) {
        cm.sendPlayerToNpc("我要去枫叶岛训练…在那之后，我将前往维多利亚岛成为一名伟大的冒险家！这就是它的工作原理，对吗？?");
    } else if (status === 2) {
        cm.sendNextNoESC("的确如此！枫叶岛是一个训练的好地方，因为那里没有危险的怪物。另外，你会交很多朋友，学习基础知识。当你准备好了，世界将会有一个广阔的世界让你去探索。!");
    } else if (status === 3) {
        cm.sendPlayerToNpc("呵呵，我等不及了！我要好好训练，学会干掉所有强大的怪物。我完全准备好了。!");
    } else if (status === 4) {
        cm.sendNextNoESC("多么伟大的态度！那会帮助你成功。尽管如此，你永远无法确定会发生什么。只记得， #beverything事出有因.#k");
    } else if (status === 5) {
        cm.sendPlayerToNpc("嘿，你听到什么了吗？?");
    } else if (status === 6) {
        cm.sendDirectionStatus(4, 0);
        cm.sendDirectionStatus(3, 2);
        cm.sendDirectionInfo("Effect/Summon.img/15");
        cm.sendDirectionStatus(1, 2000);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balog");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/npc/0");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/User/0");
        cm.sendDirectionStatus(1, 1000);
        cm.showWZEffect("Effect/Direction4.img/effect/cannonshooter/face02");
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/npc/1");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/User/1");
        cm.sendDirectionStatus(1, 1000);
        cm.showWZEffect("Effect/Direction4.img/effect/cannonshooter/face05");
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balog/0");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Mob/8150000.img/attack2/info/effect");
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/User/2");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionStatus(3, 6);
        cm.sendDirectionInfo("Mob/8130100.img/attack1/info/effect");
        cm.sendDirectionInfo("Mob/8130100.img/attack1/info/hit");
        cm.showWZEffect("Effect/Direction4.img/effect/cannonshooter/face01");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionStatus(3, 2);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balog/1");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/User/3");
        cm.sendDirectionStatus(1, 1000);
        cm.sendDirectionInfo("Mob/8150000.img/attack2/info/hit");
        cm.warp(912060100, 0);
        cm.dispose();
    }
}
