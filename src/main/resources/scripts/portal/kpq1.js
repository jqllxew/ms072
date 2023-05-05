function enter(pi) {
    var eim = pi.getEventManager("KerningPQ").getInstance("KerningPQ");
    if (eim.getProperty("1stageclear") == null) {
        pi.playerMessage(5, "门是关闭的.");
    } else {
        pi.warp(103000801, "st00");
    }
}