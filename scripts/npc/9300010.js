function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	cm.sendNext("你要出去吗?~");
	cm.safeDispose();
}