function action(mode, type, selection) {
    if (cm.getPlayer().getParty() != null) { //�ж��Ƿ������
        if (isLeader() == true) {
            cm.removeAll(4001022);
            cm.removeAll(4001023);
            cm.getPlayer().endPartyQuest(1202); //might be a bad implentation.. incase they dc or something
            //cm.���������Ʒ�ӳ�˫��(4170005, 1, false); //��߸�����
            cm.gainExp(+2000);
            cm.warpParty(922010000);
            cm.����(3, "[" + cm.getPlayer().getName() + "]�ɹ�ͨ�ء�������� - ��߳���ӡ���ý�����");
            cm.dispose();
        } else {
            cm.sendOk("���ӳ�������������,����������˵����");
            cm.dispose();
        }
    } else {
        cm.sendOk("��û�ж��飡");
        cm.dispose();
    }
}

function isLeader() {
    if (cm.getParty() == null)
        return false;
    return cm.isLeader();
}