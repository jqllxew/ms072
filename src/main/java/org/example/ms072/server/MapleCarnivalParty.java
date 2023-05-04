package org.example.ms072.server;

import org.example.ms072.client.MapleCharacter;
import org.example.ms072.handling.channel.ChannelServer;
import java.util.LinkedList;
import java.util.List;
import java.lang.ref.WeakReference;
import org.example.ms072.server.maps.MapleMap;
import org.example.ms072.tools.packet.CField;

public class MapleCarnivalParty {

    private final List<Integer> members = new LinkedList<>();
    private final WeakReference<MapleCharacter> leader;
    private final byte team;
    private final int channel;
    private short availableCP = 0, totalCP = 0;
    private boolean winner = false;

    public MapleCarnivalParty(final MapleCharacter owner, final List<MapleCharacter> members1, final byte team1) {
        leader = new WeakReference<>(owner);
        for (MapleCharacter mem : members1) {
            members.add(mem.getId());
            mem.setCarnivalParty(this);
        }
        team = team1;
        channel = owner.getClient().getChannel();
    }

    public final MapleCharacter getLeader() {
        return leader.get();
    }

    public void addCP(MapleCharacter player, int ammount) {
        totalCP += ammount;
        availableCP += ammount;
        player.addCP(ammount);
    }

    public int getTotalCP() {
        return totalCP;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void useCP(MapleCharacter player, int ammount) {
        availableCP -= ammount;
        player.useCP(ammount);
    }

    public List<Integer> getMembers() {
        return members;
    }

    public int getTeam() {
        return team;
    }

    public void warp(final MapleMap map, final String portalname) {
        for (int chr : members) {
            final MapleCharacter c = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(chr);
            if (c != null) {
                c.changeMap(map, map.getPortal(portalname));
            }
        }
    }

    public void warp(final MapleMap map, final int portalid) {
        for (int chr : members) {
            final MapleCharacter c = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(chr);
            if (c != null) {
                c.changeMap(map, map.getPortal(portalid));
            }
        }
    }

    public boolean allInMap(MapleMap map) {
        for (int chr : members) {
            if (map.getCharacterById(chr) == null) {
                return false;
            }
        }
        return true;
    }

    public void removeMember(MapleCharacter chr) {
        try {
            int index = -1;
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i) == chr.getId()) {
                    index = i;
                }
            }

            if (index != -1) {
                chr.setCarnivalParty(null);
                members.remove(index);
            }

        } finally {

        }

    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean status) {
        winner = status;
    }

    public void displayMatchResult() {
        final String effect = winner ? "quest/carnival/win" : "quest/carnival/lose";
        final String sound = winner ? "MobCarnival/Win" : "MobCarnival/Lose";
        boolean done = false;
        for (int chr : members) {
            final MapleCharacter c = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(chr);
            if (c != null) {
                c.getClient().sendPacket(CField.showEffect(effect));
                c.getClient().sendPacket(CField.playSound(sound));
                if (!done) {
                    done = true;
                    c.getMap().killAllMonsters(true);
                    c.getMap().setSpawns(false); //resetFully will take care of this
                }
            }
        }

    }
}
