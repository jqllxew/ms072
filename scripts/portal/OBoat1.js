let cField = Java.type('tools.packet.CField');
function enter(pi) {
    pi.playPortalSE();
    pi.warp(200090010, 4);
    if ("true".equals(pi.getEventManager("Boats").getProperty("haveBalrog")) &&
        "false".equals(pi.getEventManager("Boats").getProperty("clear2"))) {
        pi.getClient().sendPacket(cField.monsterBoat(true));
        pi.getClient().sendPacket(cField.musicChange("Bgm04/ArabPirate"));
    }
    return true;
}
