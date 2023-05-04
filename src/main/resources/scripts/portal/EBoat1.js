let cField = Java.type('org.example.ms072.tools.packet.CField');
function enter(pi) {
    pi.playPortalSE();
    pi.warp(200090000, 4);
    if ("true".equals(pi.getEventManager("Boats").getProperty("haveBalrog")) &&
        "false".equals(pi.getEventManager("Boats").getProperty("clear1"))) {
        pi.getClient().sendPacket(cField.monsterBoat(true));
        pi.getClient().sendPacket(cField.musicChange("Bgm04/ArabPirate"));
    }
    return true;
}
