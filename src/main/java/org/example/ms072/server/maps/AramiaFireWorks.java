/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.example.ms072.server.maps;

import org.example.ms072.client.MapleCharacter;
import org.example.ms072.handling.world.World;
import java.awt.Point;
import org.example.ms072.server.MapleItemInformationProvider;
import org.example.ms072.server.Randomizer;
import org.example.ms072.server.Timer.EventTimer;
import org.example.ms072.server.life.MapleLifeFactory;
import org.example.ms072.tools.packet.CField;
import org.example.ms072.tools.packet.CWvsContext;

public class AramiaFireWorks {

    public final static int KEG_ID = 4001128, SUN_ID = 4001246, DEC_ID = 4001473;
    public final static int MAX_KEGS = 2400, MAX_SUN = 3000, MAX_DEC = 3600;
    private short kegs = 0;
    private short sunshines = MAX_SUN / 6; //start at 1/6 then go from that
    private short decorations = MAX_DEC / 6;
    private static final int[] arrayMob = {9500168, 9500169, 9500170, 9500171, 9500173,
        9500174, 9500175, 9500176, 9500170, 9500171, 9500172, 9500173, 9500174, 9500175};
    private static final int[] arrayX = {2100, 2605, 1800, 2600, 3120, 2700, 2320, 2062,
        2800, 3100, 2300, 2840, 2700, 2320};
    private static final int[] arrayY = {574, 364, 574, 316, 574, 574, 403, 364, 574, 574,
        403, 574, 574, 403};
    private static final int[] array_X = {720, 180, 630, 270, 360, 540, 450, 142,
        142, 218, 772, 810, 848, 232};
    private static final int[] array_Y = {1234, 1234, 1174, 1234, 1174, 1174, 1174, 1260,
        1234, 1234, 1234, 1234, 1234, 1114};
    private static final int flake_Y = 149;

    public final void giveKegs(final MapleCharacter c, final int kegs) {
        this.kegs += kegs;
        if (this.kegs >= MAX_KEGS) {
            this.kegs = 0;
            broadcastEvent(c);
        }
    }

    private void broadcastServer(final MapleCharacter c, final int itemid) {
        World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, itemid, "<频道 " + c.getClient().getChannel() + "> " + c.getMap().getMapName() + " : 阿乐米的 {" + MapleItemInformationProvider.getInstance().getName(itemid) + "} 收集够了,现在开始燃放烟花,还在等什么？"));
    }

    public final short getKegsPercentage() {
        double per = (((kegs) * 1.0) / (MAX_KEGS * 1.0));
        return (short) (per * 100);
    }

    private void broadcastEvent(final MapleCharacter c) {
        broadcastServer(c, KEG_ID);
        // Henesys Park
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startEvent(c.getClient().getChannelServer().getMapFactory().getMap(100000200));
            }
        }, 10000);
    }

    private void startEvent(final MapleMap map) {
        map.startMapEffect("烟花开始燃放.怪物受到惊吓开始出现!", 5120009);

        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                spawnMonster(map);
            }
        }, 5000);
    }

    private void spawnMonster(final MapleMap map) {
        Point pos;

        for (int i = 0; i < arrayMob.length; i++) {
            pos = new Point(arrayX[i], arrayY[i]);
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(arrayMob[i]), pos);
        }
    }

    public final void giveSuns(final MapleCharacter c, final int kegs) {
        this.sunshines += kegs;
        //have to broadcast a Reactor?
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(970010000);
        final MapleReactor reactor = map.getReactorByName("mapleTree");
        for (int gogo = kegs + (MAX_SUN / 6); gogo > 0; gogo -= (MAX_SUN / 6)) {
            switch (reactor.getState()) {
                case 0: //first state
                case 1: //first state
                case 2: //first state
                case 3: //first state
                case 4: //first state
                    if (this.sunshines >= (MAX_SUN / 6) * (2 + reactor.getState())) {
                        reactor.setState((byte) (reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
                    }
                    break;
                default:
                    if (this.sunshines >= (MAX_SUN / 6)) {
                        map.resetReactors(); //back to state 0
                    }
                    break;
            }
        }
        if (this.sunshines >= MAX_SUN) {
            this.sunshines = 0;
            broadcastSun(c);
        }
    }

    public final short getSunsPercentage() {
        return (short) ((sunshines / MAX_SUN) * 10000);
    }

    private void broadcastSun(final MapleCharacter c) {
        broadcastServer(c, SUN_ID);
        // Henesys Park
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startSun(c.getClient().getChannelServer().getMapFactory().getMap(970010000));
            }
        }, 10000);
    }

    private void startSun(final MapleMap map) {
        map.startMapEffect("树上绽放着阳光!", 5121010);
        for (int i = 0; i < 3; i++) {
            EventTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    spawnItem(map);
                }
            }, 5000 + (i * 10000));
        }
    }

    private void spawnItem(final MapleMap map) {
        Point pos;

        for (int i = 0; i < Randomizer.nextInt(5) + 10; i++) {
            pos = new Point(array_X[i], array_Y[i]);
            int itemId = 4001246;
            switch (Randomizer.nextInt(14)) {
                case 0:
                case 1:
                    itemId = 3010141;
                    break;
                case 2:
                    itemId = 3010146;
                    break;
                case 3:
                case 4:
                    itemId = 3010025;
                    break;
            }
            map.spawnAutoDrop(itemId, pos);
        }
    }

    public final void giveDecs(final MapleCharacter c, final int kegs) {
        this.decorations += kegs;
        //have to broadcast a Reactor?
        final MapleMap map = c.getClient().getChannelServer().getMapFactory().getMap(555000000);
        final MapleReactor reactor = map.getReactorByName("XmasTree");
        for (int gogo = kegs + (MAX_DEC / 6); gogo > 0; gogo -= (MAX_DEC / 6)) {
            switch (reactor.getState()) {
                case 0: //first state
                case 1: //first state
                case 2: //first state
                case 3: //first state
                case 4: //first state
                    if (this.decorations >= (MAX_DEC / 6) * (2 + reactor.getState())) {
                        reactor.setState((byte) (reactor.getState() + 1));
                        reactor.setTimerActive(false);
                        map.broadcastMessage(CField.triggerReactor(reactor, reactor.getState()));
                    }
                    break;
                default:
                    if (this.decorations >= MAX_DEC / 6) {
                        map.resetReactors(); //back to state 0
                    }
                    break;
            }
        }
        if (this.decorations >= MAX_DEC) {
            this.decorations = 0;
            broadcastDec(c);
        }
    }

    public final short getDecsPercentage() {
        return (short) ((decorations / MAX_DEC) * 10000);
    }

    private void broadcastDec(final MapleCharacter c) {
        broadcastServer(c, DEC_ID);
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public final void run() {
                startDec(c.getClient().getChannelServer().getMapFactory().getMap(555000000));
            }
        }, 10000); //no msg
    }

    private void startDec(final MapleMap map) {
        map.startMapEffect("树上有一片雪!", 5120000);
        for (int i = 0; i < 3; i++) {
            EventTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    spawnDec(map);
                }
            }, 5000 + (i * 10000));
        }
    }

    private void spawnDec(final MapleMap map) {
        Point pos;

        for (int i = 0; i < Randomizer.nextInt(10) + 40; i++) {
            pos = new Point(Randomizer.nextInt(800) - 400, flake_Y);
            map.spawnAutoDrop(Randomizer.nextInt(15) == 1 ? 4310012 : 4310011, pos);
        }
    }
}
