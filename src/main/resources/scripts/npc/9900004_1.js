
const 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
const 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
const CONDITION_MAP = {
    'meso_1': 10_0000,
    'meso_2': 50_0000,
    'meso_3': 100_0000,
    'meso_4': 200_0000,
    'meso_5': 500_0000,
    'item_1': 100,
    'item_2': 200,
    'item_3': 300,
    'item_4': 400,
    'item_5': 500,
    'meso_str_1': '10w',
    'meso_str_2': '50w',
    'meso_str_3': '100w',
    'meso_str_4': '200w',
    'meso_str_5': '500w',
}
const EFFECT_MAP = {
    'star_1': 2,
    'star_2': 3,
    'star_3': 5,
    'star_4': 8,
    'star_5': 10,
}
const OWNER_LIST = ["","★","★★","★★★","★★★★","★★★★★"]

const MapleInventoryManipulator = Java.type('org.example.ms072.server.MapleInventoryManipulator');
const MapleInventoryType = Java.type('org.example.ms072.client.inventory.MapleInventoryType');
let status = -1

function start(){
    action(1, 0, 0);
}

function action(mode, type, selection){
    mode === 1? status++ : status--;
    if (status === 0){
        let text = "\r\n"
        text += "   \t\t\t   #e#d欢迎来到#r极光戒指升级系统#k#r#n \r\n"
        text += "   \t\t\t      #b萌新记得好好看说明哦#k#n\r\n"
        text += "   \t\t\t Tips:请确定您拥有该道具#v1112908#\r\n"
        text += "#L0##b" + 红色箭头 + "升星系统说明\r\n";
        text += "#L1##b" + 红色箭头 + "升级#e#n#r★　　　　#e#b需要#v4001126#×100个" + 蓝色角点 + "10w金币#l\r\n";
        text += "#L2##b" + 红色箭头 + "升级#e#n#r★★　　　#e#b需要#v4001126#×200个" + 蓝色角点 + "50w金币#l\r\n";
        text += "#L3##b" + 红色箭头 + "升级#e#n#r★★★　　#e#b需要#v4001126#×300个" + 蓝色角点 + "100w金币#l\r\n";
        text += "#L4##b" + 红色箭头 + "升级#e#n#r★★★★　#e#b需要#v4001126#×400个" + 蓝色角点 + "200w金币#l\r\n";
        text += "#L5##b" + 红色箭头 + "升级#e#n#r★★★★★#e#b需要#v4001126#×500个" + 蓝色角点 + "500w金币#l\r\n";
        cm.sendSimple(text);
    }else if (status === 1){
        if (selection === 0){
            cm.sendOk(
                "全属性（该戒指系统角色必须到#r10级#k方可使用~）\r\n\r\n" +
                "请把#r极光戒指#k#v1112908#放在#b背包第一格！！！\r\n\r\n" +
                "装备必须首先升到#r★#k方可继续升#r★★#k，无法跳跃式升星.#k#n\r\n\r\n" +
                "成功率90%#r★#k全属性（带双G）+2\r\n" +
                "成功率80%#r★★#k全属性（带双G）+3\r\n" +
                "成功率70%#r★★★#k全属性（带双G）+5\r\n" +
                "成功率60%#r★★★★#k全属性（带双G）+8\r\n" +
                "成功率50%#r★★★★★#k全属性（带双G）+10\r\n\n"
            );
            status = -1;
        }else starUp(selection);
    }else {
        cm.dispose();
    }
}

function starUp(star= 0){
    let item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)
    if (!item || item.getItemId() !== 1112908){
        cm.sendOk("#v1112908#该道具不在您背包的第一格!..");
        status = -1;
    }else if(condition(star, item.getOwner())){
        let eff = EFFECT_MAP['star_'+star]
        let owner = OWNER_LIST[star]
        cm.gainItem(4001126, -CONDITION_MAP['item_'+star]);
        cm.gainMeso(-CONDITION_MAP['meso_'+star]);
        let _item = item.copy()
        _item.setOwner(owner);
        _item.setStr(_item.getStr() * 1 + eff);
        _item.setDex(_item.getDex() * 1 + eff);
        _item.setInt(_item.getInt() * 1 + eff);
        _item.setLuk(_item.getLuk() * 1 + eff);
        _item.setWdef(_item.getWdef() * 1 + eff);
        _item.setMdef(_item.getMdef() * 1 + eff);
        _item.setMatk(_item.getMatk() * 1 + eff);
        _item.setWatk(_item.getWatk() * 1 + eff);
        _item.setHp(_item.getHp() * 1 + eff);
        _item.setMp(_item.getMp() * 1 + eff);
        MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
        MapleInventoryManipulator.addFromDrop(cm.getC(), _item, false);
        cm.喇叭(3,"『升星公告』：恭喜【" + cm.getChar().getName() + "】的极光戒指升为【" + owner + "】 战斗力大幅提升！");
        cm.sendOk("#e#b成功的将#v1112908#提升至#r " + owner);
    }
    status = -1;
}

function condition(star, owner){
    if (!star || star < 1 || star > 5){
        console.error('升星异常,角色id:'+cm.getPlayer().getName());
        return false;
    }
    if (OWNER_LIST[star-1] !== owner){
        if (!owner){
            cm.sendOk(`您当前戒指星级为0，无法进行${OWNER_LIST[star]}的升级!..`);
        }else {
            cm.sendOk(`您当前戒指星级为${owner}，无法进行${OWNER_LIST[star]}的升级!..`);
        }
        return false;
    }
    let mesoCount = CONDITION_MAP['meso_'+star]
    let itemCount = CONDITION_MAP['item_'+star]
    if (!mesoCount || !itemCount){
        return false;
    }
    let mesoCountMsg = CONDITION_MAP['meso_str_'+star]
    if (!cm.haveItem(4001126, itemCount)){
        cm.sendOk(`#v4001126#不足${itemCount}个!..`);
        return false;
    }
    if (cm.getMeso() < mesoCount){
        cm.sendOk(`金币不足${mesoCountMsg}!..`);
        return false;
    }
    return true;
}