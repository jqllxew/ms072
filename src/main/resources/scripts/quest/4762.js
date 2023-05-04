
let status = -1;
function start(mode, type, selection) {
    if (mode === 1){
        status ++;
        qm.sendNext("嗨~！ 小家伙~ 送你30w金币");
        qm.gainMeso(300000);
        if (status >= 0){
            qm.forceStartQuest();
            qm.dispose()
        }
    }else {
        status --
    }
}

function end(mode, type, selection) {
}