function enter(pi) {
    var eim = pi.getEventManager("KerningPQ").getInstance("KerningPQ");
    if (eim.getProperty("4stageclear") == null) {
        pi.playerMessage(5, "���ǹرյ�.");
    } else {
        pi.warp(103000804, "st00");
    }
}