let status = -1;
let 小烟花 ="#fMap/MapHelper/weather/squib/squib4/1#";
let 星星 ="#fMap/MapHelper/weather/witch/3#";
let 红枫叶 ="#fMap/MapHelper/weather/maple/1#";
let itemId = 0;
function start(){
    action(1, 0, 0);
}

function action(mode, type, selection){
    if (mode === 0 && status === 0){
        cm.dispose();
        return;
    }
    mode === 1 ? status ++ : status -- ;
    if (status === 0){
        let sel = `${星星}你好[#b#e#h ##n#k],欢迎来到冒险岛服务中心${星星}\r\n\r\n`+
            `当前您的（点卷#r${cm.getPlayer().getCSPoints(1)}#d `+
            `抵用券#r${cm.getPlayer().getCSPoints(2)}#d `+
            `金币#r${cm.getPlayer().getMeso()}#d）\r\n\r\n`+
            `#L5#极光戒指升级#v1112908#${红枫叶}\r\n\r\n`+
            `#b#L1#内测金币#k#r#L2#内测点卷#k#b#L3#内测抵用#k#r#L4#内测道具#k#l\r\n `
        cm.sendSimple(sel);
    }else if (status === 1){
        if (selection === 1){
            itemId = 1;
            cm.sendYesNo("你确定要获得内测金币吗？")
        }else if (selection === 2){
            itemId = 2;
            cm.sendYesNo("你确定要获得内测点卷吗？")
        }else if (selection === 3){
            itemId = 3;
            cm.sendYesNo("你确定要获得内测抵用卷吗？")
        }else if (selection === 4){
            cm.sendGetNumber("请输入道具代码", 1, 500000, 9999999)
        }else if (selection === 5){
            cm.dispose();
            cm.openNpc(9900004,1);
        }else {
            cm.dispose();
        }
    }else if (status === 2){
        if (selection > 500000){
            itemId = selection;
            cm.sendGetNumber("#e请确认道具：#b#z" + itemId + "#\r\n#r请输入获取数量", 1, 1, 1000);
        }else {
            if (itemId === 1){
                cm.gainMeso(200000000);
                cm.sendOk("您已获得 #b2E#k 金币！");
            }else if (itemId === 2){
                cm.getPlayer().modifyCSPoints(1,999999,true);
                cm.sendOk("您已获得 #b999999#k 点卷！");
            }else if (itemId === 3){
                cm.getPlayer().modifyCSPoints(2,999999,true);
                cm.sendOk("您已获得 #b999999#k 抵用券！");
            }
            cm.dispose();
        }
    }else if (status === 3){
        if (itemId > 500000){
            cm.getPlayer().dropMessage('尝试获得道具' + itemId)
            cm.gainItem(itemId, selection);
        }
        cm.dispose();
    }else {
        cm.dispose();
    }
}