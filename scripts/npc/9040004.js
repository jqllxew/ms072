function start() {
    cm.sendSimple("#L0##r�������а�\r\n\r\n#L1##g�ȼ����а�\r\n\r\n#L2##b������а�#l\r\n\r\n#L3##b�������а�#l");
}

function action(mode, type, selection) {
    if (selection == 0) {
        cm.�������а�();
        cm.dispose();
    } else if (selection == 1) {
        cm.�ȼ����а�();
        cm.dispose();
    } else if (selection == 2) {
        cm.������а�();
        cm.dispose();
    } else if (selection == 3) {
        cm.�������а�();
        cm.dispose();
    }
    cm.dispose();
}
