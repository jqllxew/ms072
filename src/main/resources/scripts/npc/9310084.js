var 星星 = "";
var 爱心 = "";
var 红色箭头 = "";
var 正方形 = "";
var 蓝色箭头 = "";
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            
			text += "\t\t\t  #e欢迎来到#b大白兔冒险岛v079 #k!#n\r\n#r结婚说明如下：\r\n结婚要求：必须是2个人\r\n结婚对象：必须是一男一女 是否可以离婚：结婚以后不可以离婚，考虑好在结婚\r\n想像我女儿跟郭靖那样双宿双飞么？恩恩爱爱？夫妻双双把床摇？还不赶紧买单"

            text += "\r\n#L1##e#d" + 蓝色箭头 + "我要跟情侣进入结婚地图#l\r\n\r\n#L2##d" + 蓝色箭头 + "购买结婚钥匙---需要点卷33333点#l\r\n\r\n"//3
     
          
            cm.sendSimple(text);
        } else if (selection == 1) { //月妙组队副本		//cm.warp(700000000 ,0);
            if (!cm.haveItem(4031409,1)) {  
				cm.sendOk("进入结婚地图需要#v4031409##z4031409#,再不买你老婆就要跑了！");
                cm.dispose();
              }else{
				cm.warp(700000000,0);  
				//cm.gainItem(4031409, -1);//
				cm.dispose();
                return;
	      } 
        } else if (selection == 2) {  //废弃组队副本
                    if (!cm.canHold(4031409, 1)) {
            cm.sendOk("背包满了，请清理一下！！");
                cm.dispose();
			}else if (cm.haveItem(4031409,1)) {  
            cm.sendOk("你已经有了#v4031409##z4031409#,不要重复购买！");
                cm.dispose();
			}else if (cm.getPlayer().getCSPoints(1) >= 33333) {
				cm.gainNX(-33333);
				cm.gainItem(4031409, 1);//
			cm.喇叭(2, "恭喜[" + cm.getPlayer().getName() + "]成功购买结婚钥匙一个，请和你的伴侣一起进入结婚地图，准备洞房吧！！");
            cm.sendOk("购买成功！");
            cm.dispose();
			}else{
            cm.sendOk("点卷不足8888W点，无法购买!");
            cm.dispose();
			}
        } else if (selection == 3) { //玩具组队副本
            cm.openNpc(2040034, 0);
        } else if (selection == 4) {//天空组队副本
            cm.openNpc(2013000, 0);
        } else if (selection == 5) {//毒物组队副本
            cm.warp(300030100);
            cm.dispose();
        } else if (selection == 6) {//海盗组队副本
            cm.openNpc(2094000, 0);
        } else if (selection == 7) {//罗密欧与朱丽叶组队副本
			cm.warp(261000011);
            cm.dispose();
        } else if (selection == 8) {//遗址公会对抗战
			cm.warp(101030104);
            cm.dispose();
        } else if (selection == 9) {//英语学院副本
            cm.warp(702090400);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 11) {//千年树精王遗迹
            cm.warp(541020700);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 12) {//人偶师BOSS挑战
            cm.warp(910510001);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        } else if (selection == 13) {//绯红
            if (cm.getLevel() < 120 ) {  
            cm.sendOk("本地图限制等级120级。您的能力没有资格挑战绯红副本");
                cm.dispose();
              }else{
			cm.warp(803001200);  
				cm.dispose();
                return;
	      } 
        } else if (selection == 14) {//御姐
            if (cm.getLevel() < 140 ) {  
            cm.sendOk("本地图限制等级140级。您的能力没有资格挑战御姐副本");
                cm.dispose();
              }else{
			cm.warp(803000505);  
                cm.dispose();
                return;
	      } 
        } else if (selection == 10) {//.怪物嘉年华
            cm.warp(980000000);
            cm.dispose();
            //cm.openNpc(9310057, 0);
        }
    }
}


