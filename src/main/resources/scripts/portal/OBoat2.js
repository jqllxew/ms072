let cField = Java.type('org.example.ms072.tools.packet.CField');
function enter(pi) {
    pi.playPortalSE();
    pi.warp(200090010, 5);
    if ("true".equals(pi.getEventManager("Boats").getProperty("haveBalrog")) &&
        "false".equals(pi.getEventManager("Boats").getProperty("clear2"))) {
        pi.getClient().sendPacket(cField.monsterBoat(true));
        pi.getClient().sendPacket(cField.musicChange("Bgm04/ArabPirate"));
    }
    return true;
}
