/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.server.buffs.buffclasses.beasttamer;

import  org.example.ms072.client.MapleBuffStat;
import  org.example.ms072.constants.GameConstants;
import org.example.ms072.server.MapleStatEffect;
import org.example.ms072.server.MapleStatInfo;
import org.example.ms072.server.buffs.AbstractBuffClass;

/**
 *
 * @author Maple
 */
public class BeastTamerBuff extends AbstractBuffClass {

    public BeastTamerBuff() {
        buffs = new int[]{
            110001501, // Bear Mode
            110001502, // Snow Leopard Mode
            110001503, // Hawk Mode
            110001504, // Cat Mode
            112001009 // Bear Assault
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isBeastTamer(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 112000012:
                eff.statups.put(MapleBuffStat.ATTACK_SPEED, eff.info.get(MapleStatInfo.indieBooster));
                break;
            default:
                System.out.println("BeastTamer skill not coded: " + skill);
                break;
        }
    }
}
