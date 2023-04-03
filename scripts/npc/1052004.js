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
/* Denma the Owner
 Henesys VIP Eye Change.
 */
var status = 0;
var beauty = 0;
var price = 1000000;
var mface = Array(20000,20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20015,20016,20017,20020,20021,20022,20025,20026,20027,20028,20100,20101,20102,20103,20104,20105,20106,20107,20108,20109,20110,20111,20112,20113,20114,20115,20116,20117,20120,20121,20122,20125,20126,20127,20128,20200,20201,20202,20203,20204,20205,20206,20207,20208,20209,20210,20211,20212,20213,20214,20215,20216,20217,20220,20221,20222,20225,20226,20227,20228,20300,20301,20302,20303,20304,20305,20306,20307,20308,20309,20310,20311,20312,20313,20314,20315,20316,20317,20320,20321,20322,20325,20326,20327,20328,20400,20401,20402,20403,20404,20405,20406,20407,20408,20409,20410,20411,20412,20413,20414,20415,20416,20417,20420,20421,20422,20425,20426,20427,20428,20500,20501,20502,20503,20504,20505,20506,20507,20508,20509,20510,20511,20512,20513,20514,20515,20516,20517,20520,20521,20522,20525,20526,20527,20528,20600,20601,20602,20603,20604,20605,20606,20607,20608,20609,20610,20611,20612,20613,20614,20615,20616,20617,20620,20621,20622,20625,20626,20627,20628,20700,20701,20702,20703,20704,20705,20706,20707,20708,20709,20710,20711,20712,20713,20714,20715,20716,20717,20720,20721,20722,20725,20726,20727,20728,20827,20828);
var fface = Array(21001,21002,21003,21004,21005,21006,21007,21008,21009,21010,21011,21012,21013,21014,21015,21016,21017,21020,21021,21023,21025,21026,21100,21101,21102,21103,21104,21105,21106,21107,21108,21109,21110,21111,21112,21113,21114,21115,21116,21117,21120,21121,21123,21125,21126,21200,21201,21202,21203,21204,21205,21206,21207,21208,21209,21210,21211,21212,21213,21214,21215,21216,21217,21220,21221,21223,21225,21226,21300,21301,21302,21303,21304,21305,21306,21307,21308,21309,21310,21311,21312,21313,21314,21315,21316,21317,21320,21321,21323,21325,21326,21400,21401,21402,21403,21404,21405,21406,21407,21408,21409,21410,21411,21412,21413,21414,21415,21416,21417,21420,21421,21423,21425,21426,21500,21501,21502,21503,21504,21505,21506,21507,21508,21509,21510,21511,21512,21513,21514,21515,21516,21517,21520,21521,21523,21525,21526,21600,21601,21602,21603,21604,21605,21606,21607,21608,21609,21610,21611,21612,21613,21614,21615,21616,21617,21620,21621,21623,21625,21626,21700,21701,21702,21703,21704,21705,21706,21707,21708,21709,21710,21711,21712,21713,21714,21715,21716,21717,21720,21721,21723,21725,21726);
var facenew = Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("我是黛玛院长. 如果你有 #b#t5152001##k 任何机会，那么怎么样让我帮整形? \r\n\#L2#使用 #b#t5152001##k");
        } else if (status == 1) {
            if (selection == 1) {
                if (cm.getMeso() >= price) {
                    cm.gainMeso(-price);
                    cm.gainItem(5152001, 1);
                    cm.sendOk("Enjoy!");
                } else
                    cm.sendOk("You don't have enough mesos to buy a coupon!");
                cm.dispose();
            } else if (selection == 2) {
                facenew = Array();
                if (cm.getPlayer().getGender() == 0) {
                    for (var i = 0; i < mface.length; i++)
                        facenew.push(mface[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100));
                }
                if (cm.getPlayer().getGender() == 1) {
                    for (var i = 0; i < fface.length; i++)
                        facenew.push(fface[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100));
                }
                cm.sendStyle("让我看看选择一个想要的..", 5152001, facenew);
            }
        } else if (status == 2) {
            cm.dispose();
            if (cm.haveItem(5152001) == true) {
                cm.gainItem(5152001, -1);
                cm.setFace(facenew[selection]);
                cm.sendOk("享受!!");
            } else
                cm.sendOk("您貌似没有#b#t5152001##k..");
        }
    }
}
