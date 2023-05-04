function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("Hope you enjoy getting around Orbis.");
    cm.warp(101000300, 0)
    cm.dispose();
}