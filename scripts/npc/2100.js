/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/* Author: Xterminator
	NPC Name: 		Sera
	Map(s): 		Maple Road : Entrance - Mushroom Town Training Camp (0), Maple Road: Upper level of the Training Camp (1), Maple Road : Entrance - Mushroom Town Training Camp (3)
	Description: 		First NPC
*/

var status = -1;

function start() {
    if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3)
        cm.sendYesNo("��ӭ����ð�յ����硣���ѵ��Ӫ��Ŀ���ǰ�����ѧ�ߡ�����μ����ѵ��Ӫ����Щ�˲��μ�ѵ���Ϳ�ʼ���С�����ǿ�ҽ������Ȳμ���ѵ�ƻ���");
    else
        cm.sendNext("������ĵ�һ��ѵ���ƻ���ʼ��Ӱ���ҡ������������������ǰ������ѡ���ְҵ��");
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if(mode == 0 && status == 0){
            cm.sendYesNo("����������Ͽ�ʼ������");
            return;
        }else if(mode == 0 && status == 1 && type == 0){
            status -= 2;
            start();
            return;
        }else if(mode == 0 && status == 1 && type == 1)
            cm.sendNext("����������������ʱ�����ٶ���˵һ�Ρ�");
        cm.dispose();
        return;
    }
    if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3){
        if(status == 0){
            cm.sendNext("�ܺã����Ҿ��������ѵ��Ӫ�ɡ���������ʦ��ָ�ӡ�");
        }else if(status == 1 && type == 1){
            cm.sendNext("�������뿪ʼ����ó̶����μ���ѵ�ƻ���Ȼ���һ�����ת��ѵ������С��~");
        }else if(status == 1){
            cm.warp(1, 0);
            cm.dispose();
        }else{
            cm.warp(40000);
            cm.dispose();
        }
    }else
    if(status == 0)
        cm.sendPrev("һ����Ŭ��ѵ����������ʸ����һ��ְҵ������������ִ��Ϊһ�������֣���ħ�����ֳ�Ϊһ��ħ��ʦ������ʿ�����Ϊһ��սʿ���ڷ������г�Ϊһ��������");
    else
        cm.dispose();
}