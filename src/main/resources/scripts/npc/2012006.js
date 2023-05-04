var mapid = new Array(200000111,200000121,200000131,200000141,200000151,200000161,200000170);
var platform = new Array("码头<开往金银岛>","码头<开往玩具城>","码头<开往神木村>","港口通道<开往武陵>","码头＜阿里安特＞","	码头<开往圣地>","码头<开往埃德尔斯坦>");
var flight = new Array("ship","ship","ship","Hak","Geenie","ship","ship");
var menu;
var select;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if(mode == 0) {
		cm.sendOk("请仔细选择好你要去的站台，再跟我讲。");
	cm.dispose();
	return;
    }
    if(mode == 1)
	status++;
    else
	status--;
    if(status == 0) {
	menu = "天空之城来往航班纵横交错，请选择一个可以带你到目的地的站台。请放心，即使你选择错了，还可以回来跟我说，我将带你到正确的站台等待航班。请在下面选择你要去的站台。";
	for(var i=0; i < platform.length; i++) {
	    menu += "\r\n#L"+i+"##bThe platform to the ship that heads to "+platform[i]+"#k#l";
	}
	cm.sendSimple(menu);
    } else if(status == 1) {
		
	select = selection;
	cm.sendYesNo("即使你选择错了站台，你还可以回到这里来跟我说，现在你将要移动到开往 #b "+flight[select]+"  "+platform[select]+" #k的站台？");
    } else if(status == 2) {
	cm.warp(mapid[select], 0);
	cm.dispose();
    }
}