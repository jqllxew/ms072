function enter(pi) {
    var eim = pi.getEventManager("KerningPQ").getInstance("KerningPQ");
    if (eim.getProperty("2stageclear") == null) {
	pi.playerMessage(5, "���ǹرյ�.");
    } else {
	pi.warp(103000802, "st00");
    }
}