/* Author: aaroncsn (MapleSea Like)
	NPC Name: 		Yang the Pilot
	Map(s): 		China Shanghai- Temporary Airport(701000100)
	Description: 	Pilot
*/
let status = 0
function start() {
    cm.sendYesNo("你想回到勇士部落吗？");
}

function action(mode, type, selection){
    mode === 1? status++: status--;
    if (status === 1){
        cm.warp(102000000, 0);
    }
    cm.dispose();
}