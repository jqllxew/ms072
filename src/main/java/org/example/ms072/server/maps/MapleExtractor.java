package org.example.ms072.server.maps;

import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleClient;
import  org.example.ms072.tools.packet.CField;

public class MapleExtractor extends MapleMapObject {

    public int owner, timeLeft, itemId, fee;
    public long startTime;
    public String ownerName;

    public MapleExtractor(MapleCharacter owner, int itemId, int fee, int timeLeft) {
        super();
        this.owner = owner.getId();
        this.itemId = itemId;
        this.fee = fee;
        this.ownerName = owner.getName();
        this.startTime = System.currentTimeMillis();
        this.timeLeft = timeLeft;
        setPosition(owner.getPosition());
    }

    public int getTimeLeft() { //tbh idk if this is even right, lol
        return timeLeft;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.sendPacket(CField.makeExtractor(owner, ownerName, getTruePosition(), getTimeLeft(), itemId, fee));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.sendPacket(CField.removeExtractor(this.owner));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.EXTRACTOR;
    }
}
