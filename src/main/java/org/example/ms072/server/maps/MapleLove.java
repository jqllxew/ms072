package org.example.ms072.server.maps;

import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleClient;

import java.awt.Point;
import  org.example.ms072.tools.packet.CField;

public class MapleLove extends MapleMapObject {

    private Point pos;
    private MapleCharacter owner;
    private String text;
    private int ft;
    private int itemid;

    public MapleLove(MapleCharacter owner, Point pos, int ft, String text, int itemid) {
        this.owner = owner;
        this.pos = pos;
        this.text = text;
        this.ft = ft;
        this.itemid = itemid;
    }

    public MapleMapObjectType getType() {
        return MapleMapObjectType.LOVE;
    }

    public Point getPosition() {
        return this.pos.getLocation();
    }

    public MapleCharacter getOwner() {
        return this.owner;
    }

    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    public void sendDestroyData(MapleClient client) {
        client.sendPacket(makeDestroyData());
    }

    public void sendSpawnData(MapleClient client) {
        client.sendPacket(makeSpawnData());
    }

    public byte[] makeSpawnData() {
        return CField.spawnKite(getObjectId(), this.itemid, this.owner.getName(), this.text, this.pos, this.ft);
    }

    public byte[] makeDestroyData() {
        return CField.removeKite(getObjectId());
    }
}
