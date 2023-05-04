package org.example.ms072.client;

import org.example.ms072.server.MapleStatEffect;

import java.util.concurrent.ScheduledFuture;

public class MapleBuffStatValueHolder {

    public MapleStatEffect effect;
    public long startTime;
    public int value, localDuration, cid;
    public ScheduledFuture<?> schedule;

    public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value, int localDuration, int cid) {
        super();
        this.effect = effect;
        this.startTime = startTime;
        this.schedule = schedule;
        this.value = value;
        this.localDuration = localDuration;
        this.cid = cid;
    }
}
