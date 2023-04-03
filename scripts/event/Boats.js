
const ARRIVED_TIME = 420000 // 到达等待时间
const TAKEOFF_TIME = 120000 // 启航等待时间
const STOPENTRY_TIME = 80000 // 停止检票时间
const INVASION_TIME = 80000 // 怪物出现时间
let cField = Java.type('tools.packet.CField');

function init() {
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.setProperty("haveBalrog", "false");
    em.setProperty("clear1", "false"); // 怪物是否被全部清掉
    em.setProperty("clear2", "false");
    em.schedule("stopentry", STOPENTRY_TIME);
    em.schedule("takeoff", TAKEOFF_TIME);

    em.broadcastShip(200000111, 1);
    em.broadcastShip(101000300, 1);

    em.getMapFactory().getMap(200090000).killAllMonsters(false);
    em.getMapFactory().getMap(200090010).killAllMonsters(false);
    _setPortal();
}

function stopentry() {
    em.setProperty("entry", "false");
    em.getMapFactory().getMap(200090011).resetReactors();
    em.getMapFactory().getMap(200090001).resetReactors();
}

function takeoff() {
    try{
        em.warpAllPlayer(200000112, 200090000);
        em.warpAllPlayer(101000301, 200090010);
        em.broadcastShip(200000111, 3);
        em.broadcastShip(101000300, 3);
        em.setProperty("docked", "false");
        em.schedule("invasion", INVASION_TIME);
        em.schedule("arrived", ARRIVED_TIME);
        // console.log("魔法密林启航")
    }catch (e){
        console.error(e)
    }
}

function arrived() {
    try{
        em.warpAllPlayer(200090010, 200000100);
        em.warpAllPlayer(200090011, 200000100);
        em.warpAllPlayer(200090000, 101000300);
        em.warpAllPlayer(200090001, 101000300);
        em.broadcastShip(200000111, 1);
        em.broadcastShip(101000300, 1);
        em.getMapFactory().getMap(200090010).killAllMonsters(false);
        em.getMapFactory().getMap(200090000).killAllMonsters(false);
        em.setProperty("haveBalrog", "false");
        scheduleNew();
        // console.log("魔法密林到达")
    }catch (e){
        console.error(e)
    }
}

function invasion() {
    em.setProperty("haveBalrog", "true");
    let map1 = em.getMapFactory().getMap(200090000);
    let pos1 = new java.awt.Point(-538, 143);
    _invasion(map1, pos1);
    let map2 = em.getMapFactory().getMap(200090010);
    let pos2 = new java.awt.Point(339, 148);
    _invasion(map2, pos2);
    em.schedule("checkMonsters", 5000)
}

function _invasion(map, pos){
    map.broadcastMessage(cField.monsterBoat(true));
    map.broadcastMessage(cField.musicChange("Bgm04/ArabPirate"));
    for(let i =0; i< 5;i++){
        map.spawnMonsterOnGroundBelow(em.getMonster(2300100), pos);
    }
    // for(let i =0; i< 2;i++){
    //     map.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos);
    // }
}

function _setPortal(){
    let oMap = em.getChannelServer().getMapFactory().getMap(200090011);
    let eMap = em.getChannelServer().getMapFactory().getMap(200090001);
    oMap.getPortal("out00").setScriptName("OBoat1");
    oMap.getPortal("out01").setScriptName("OBoat2");
    eMap.getPortal("out00").setScriptName("EBoat1");
    eMap.getPortal("out01").setScriptName("EBoat2");
}

function checkMonsters(){
    if ("true".equals( em.getProperty("haveBalrog"))){
        let map1 = em.getMapFactory().getMap(200090000);
        let map2 = em.getMapFactory().getMap(200090010);
        if ("false".equals(em.getProperty("clear1")) && !map1.getSpawnedMonstersOnMap()){
            map1.broadcastMessage(cField.monsterBoat(false));
            map1.broadcastMessage(cField.musicChange("Bgm04/UponTheSky"));
            em.setProperty("clear1", "true")
        }
        if ("false".equals(em.getProperty("clear2")) && !map2.getSpawnedMonstersOnMap()){
            map2.broadcastMessage(cField.monsterBoat(false));
            map2.broadcastMessage(cField.musicChange("Bgm04/UponTheSky"));
            em.setProperty("clear2", "true")
        }
        if ("false".equals(em.getProperty("clear1")) ||
            "false".equals(em.getProperty("clear2"))){
            em.schedule("checkMonsters", 5000)
        }
    }
}