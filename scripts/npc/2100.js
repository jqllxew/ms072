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
        cm.sendYesNo("欢迎来到冒险岛世界。这个训练营的目的是帮助初学者。你想参加这个训练营吗？有些人不参加训练就开始旅行。但我强烈建议你先参加培训计划。");
    else
        cm.sendNext("这是你的第一个训练计划开始的影像室。在这个房间里，你可以提前看看你选择的职业。");
}

function action(mode, type, selection) {
    status++;
    if (mode != 1) {
        if(mode == 0 && status == 0){
            cm.sendYesNo("你真的想马上开始旅行吗？");
            return;
        }else if(mode == 0 && status == 1 && type == 0){
            status -= 2;
            start();
            return;
        }else if(mode == 0 && status == 1 && type == 1)
            cm.sendNext("当你最终做出决定时，请再对我说一次。");
        cm.dispose();
        return;
    }
    if (cm.c.getPlayer().getMapId() == 0 || cm.c.getPlayer().getMapId() == 3){
        if(status == 0){
            cm.sendNext("很好，那我就让你进入训练营吧。请听从老师的指挥。");
        }else if(status == 1 && type == 1){
            cm.sendNext("看来你想开始你的旅程而不参加培训计划。然后，我会让你转到训练场。小心~");
        }else if(status == 1){
            cm.warp(1, 0);
            cm.dispose();
        }else{
            cm.warp(40000);
            cm.dispose();
        }
    }else
    if(status == 0)
        cm.sendPrev("一旦你努力训练，你就有资格从事一项职业。你可以在射手村成为一个弓箭手，在魔法密林成为一个魔术师，在勇士部落成为一个战士，在废弃都市成为一名飞侠…");
    else
        cm.dispose();
}