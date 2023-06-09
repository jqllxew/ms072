/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.server.maps;

import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleClient;
import java.awt.Point;
import java.util.List;
import org.example.ms072.server.movement.LifeMovement;
import org.example.ms072.server.movement.LifeMovementFragment;
import org.example.ms072.server.movement.StaticLifeMovement;
import  org.example.ms072.tools.packet.CField;

/**
 *
 * @author Itzik
 */
public class MapleHaku extends AnimatedMapleMapObject {

    private final int owner;
    private final int jobid;
    private final int fh;
    private boolean stats;
    private Point pos = new Point(0, 0);

    public MapleHaku(MapleCharacter owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        this.fh = owner.getFH();
        this.stats = false;

        if ((this.jobid < 4200) || (this.jobid > 4212)) {
            throw new RuntimeException("Trying to create a haku while not a kanna.");
        }
        setPosition(owner.getTruePosition());
        setStance(this.fh);
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.sendPacket(CField.spawnHaku(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.sendPacket(CField.removeDragon(this.owner));
    }

    public int getOwner() {
        return this.owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    public void sendStats() {
        this.stats = (!this.stats);
    }

    public boolean getStats() {
        return this.stats;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public final Point getPos() {
        return this.pos;
    }

    public final void setPos(Point pos) {
        this.pos = pos;
    }

    public final void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if ((move instanceof LifeMovement)) {
                if ((move instanceof StaticLifeMovement)) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}
