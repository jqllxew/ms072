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
package org.example.ms072.server;

import org.example.ms072.handling.channel.ChannelServer;

import java.awt.Point;

import org.example.ms072.scripting.PortalScriptManager;
import org.example.ms072.server.maps.MapleMap;
import org.example.ms072.tools.packet.CWvsContext;
import org.example.ms072.client.MapleClient;
import org.example.ms072.client.anticheat.CheatingOffense;

public class MaplePortal {

    public static final int MAP_PORTAL = 2;
    public static final int DOOR_PORTAL = 6;
    private String name, target, scriptName;
    private Point position;
    private int targetmap, type, id;
    private boolean portalState = true;

    public MaplePortal(final int type) {
        this.type = type;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final Point getPosition() {
        return position;
    }

    public final String getTarget() {
        return target;
    }

    public final int getTargetMapId() {
        return targetmap;
    }

    public final int getType() {
        return type;
    }

    public final String getScriptName() {
        return scriptName;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final void setPosition(final Point position) {
        this.position = position;
    }

    public final void setTarget(final String target) {
        this.target = target;
    }

    public final void setTargetMapId(final int targetmapid) {
        this.targetmap = targetmapid;
    }

    public final void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    public final void enterPortal(final MapleClient c) {
        if (getPosition().distanceSq(c.getPlayer().getPosition()) > 40000 && !c.getPlayer().isGM()) {
            c.sendPacket(CWvsContext.enableActions());
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        final MapleMap currentmap = c.getPlayer().getMap();
        if (!c.getPlayer().hasBlockedInventory() && (portalState || c.getPlayer().isGM())) {
            if (getScriptName() != null) {
                c.getPlayer().checkFollow();
                try {
                    PortalScriptManager.getInstance().executePortalScript(this, c);
                } catch (Exception Ex) {
                    Ex.printStackTrace();
                }
            } else if (getTargetMapId() != 999999999) {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());

                if (to == null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (!c.getPlayer().isGM()) {
                    if (to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
                        c.getPlayer().dropMessage(-1, "你的等级太低.");
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    }
                }
                if (c.getPlayer().getMapId() == 109010100 || c.getPlayer().getMapId() == 109010104 || c.getPlayer().getMapId() == 109020001) {
                    c.getPlayer().dropMessage(5, "您可能无法活动事件地图.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                c.getPlayer().changeMapPortal(to, to.getPortal(getTarget()) == null ? to.getPortal(0) : to.getPortal(getTarget()));
            }
        }
        if (c != null && c.getPlayer() != null && c.getPlayer().getMap() == currentmap) { // Character is still on the same map.
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public boolean getPortalState() {
        return portalState;
    }

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}
