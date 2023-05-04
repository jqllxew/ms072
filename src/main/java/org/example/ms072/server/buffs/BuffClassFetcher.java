/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.server.buffs;

import org.example.ms072.server.MapleStatEffect;
import org.example.ms072.server.buffs.buffclasses.adventurer.BowmanBuff;
import org.example.ms072.server.buffs.buffclasses.adventurer.MagicianBuff;
import org.example.ms072.server.buffs.buffclasses.adventurer.PirateBuff;
import org.example.ms072.server.buffs.buffclasses.adventurer.ThiefBuff;
import org.example.ms072.server.buffs.buffclasses.adventurer.WarriorBuff;
import org.example.ms072.server.buffs.buffclasses.cygnus.DawnWarriorBuff;
import org.example.ms072.server.buffs.buffclasses.cygnus.MihileBuff;
import org.example.ms072.server.buffs.buffclasses.cygnus.WindArcherBuff;
import org.example.ms072.server.buffs.buffclasses.hero.AranBuff;
import org.example.ms072.server.buffs.buffclasses.hero.EvanBuff;
import org.example.ms072.server.buffs.buffclasses.hero.LuminousBuff;
import org.example.ms072.server.buffs.buffclasses.nova.AngelicBusterBuff;
import org.example.ms072.server.buffs.buffclasses.resistance.DemonBuff;
import org.example.ms072.server.buffs.buffclasses.resistance.WildHunterBuff;
import org.example.ms072.server.buffs.buffclasses.resistance.XenonBuff;
import org.example.ms072.server.buffs.buffclasses.sengoku.HayatoBuff;
import org.example.ms072.server.buffs.buffclasses.sengoku.KannaBuff;
import org.example.ms072.server.buffs.buffclasses.zero.ZeroBuff;

/**
 *
 * @author Saint
 */
public class BuffClassFetcher {

    public static final Class<?>[] buffClasses = {
        WarriorBuff.class,
        MagicianBuff.class,
        BowmanBuff.class,
        ThiefBuff.class,
        PirateBuff.class,
        DawnWarriorBuff.class,
        WindArcherBuff.class,
        MihileBuff.class,
        AranBuff.class,
        EvanBuff.class,
        LuminousBuff.class,
        AngelicBusterBuff.class,
        XenonBuff.class,
        WildHunterBuff.class,
        DemonBuff.class,
        KannaBuff.class,
        HayatoBuff.class,
        ZeroBuff.class
    };

    public static boolean getHandleMethod(MapleStatEffect eff, int skillid) {
        int jobid = skillid / 10000;
        for (Class<?> c : buffClasses) {
            try {
                if (!AbstractBuffClass.class.isAssignableFrom(c)) {
                    continue;
                }
                AbstractBuffClass cls = (AbstractBuffClass) c.newInstance();
                if (cls.containsJob(jobid)) {
                    if (!cls.containsSkill(skillid)) {
                        continue;
                    }
                    cls.handleBuff(eff, skillid);
                    return true;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("Error: handleBuff method was not found in " + c.getSimpleName() + ".class");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}
