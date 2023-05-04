/* Author: Xterminator
	NPC Name: 		Tian
	Map(s): 		Ludibrium: Station<Orbis> (220000110)
	Description: 		Ludibrium Ticketing Usher
*/
var status = 0;

function start() {
    status = -1;
    train = cm.getEventManager("Trains");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if (mode === 0) {
        cm.sendNext("你在这里一定有事情要处理，对吧?");
        cm.dispose();
        return;
    }
    if (status === 0) {
    	console.log("entry:"+train.getProperty("entry"))
    	console.log("docked:"+train.getProperty("docked"))
        if (train == null) {
            cm.sendNext("船现在都抛锚了。");
            cm.dispose();
        } else if (train.getProperty("entry").equals("true")) {
            cm.sendYesNo("看起来这船还有很大的空间。请把船票准备好，我好让你上船。路途会很长，但你会顺利到达目的地的。你怎么看?你想上这趟船吗?");
        } else if (train.getProperty("entry").equals("false") && train.getProperty("docked").equals("true")) {
            cm.sendNext("船正准备启航。我很抱歉，但你得赶下一趟航班。乘船时刻表可通过售票处的引座员查询。");
            cm.dispose();
        } else {
            cm.sendNext("我们将在启航前1分钟开始登船。请耐心等几分钟。请注意，船将准时启航，在启航前一分钟我们停止收票，所以请务必准时到这里。");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.warp(220000111, 0);
        cm.dispose();
    }
}