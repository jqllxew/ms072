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
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConfig;
import constants.SetConstants;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.Randomizer;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class StatsHandling {

    public static final int statLimit = ServerConfig.ApStatLimit;
    public static final int hpMpLimit = 500000;

    public static void DistributeAP(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        Map<MapleStat, Integer> statupdate = new EnumMap<>(MapleStat.class);
        c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
        chr.updateTick(slea.readInt());
        final int statmask = slea.readInt();
        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        String type = "";
        if (chr.getRemainingAp() > 0) {
            switch (statmask) {
                case 0x100: // Str
                    type = "力量";
                    if (stat.getStr() >= statLimit) {
                        return;
                    }
                    stat.setStr((short) (stat.getStr() + 1), chr);
                    statupdate.put(MapleStat.STR, stat.getStr());
                    break;
                case 0x200: // Dex
                    type = "敏捷";
                    if (stat.getDex() >= statLimit) {
                        return;
                    }
                    stat.setDex((short) (stat.getDex() + 1), chr);
                    statupdate.put(MapleStat.DEX, stat.getDex());
                    break;
                case 0x400: // Int
                    type = "智力";
                    if (stat.getInt() >= statLimit) {
                        return;
                    }
                    stat.setInt((short) (stat.getInt() + 1), chr);
                    statupdate.put(MapleStat.INT, stat.getInt());
                    break;
                case 0x800: // Luk
                    type = "运气";
                    if (stat.getLuk() >= statLimit) {
                        return;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1), chr);
                    statupdate.put(MapleStat.LUK, stat.getLuk());
                    break;
                case 0x2000: // HP
                    type = "HP";
                    int maxhp = stat.getMaxHp();
                    if (chr.getHpApUsed() == 10000 || maxhp == 30000) {
                        return;
                    }
                    Skill improvingMaxHP;
                    int improvingMaxHPLevel;
                    if (job == 0) { // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if (job >= 100 && job <= 132) { // Warrior
                        improvingMaxHP = SkillFactory.getSkill(1000001);
                        improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += Randomizer.rand(20, 24) + improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        } else {
                            maxhp += Randomizer.rand(20, 24);
                        }
                        //maxhp += Randomizer.rand(36, 42);
                    } else if (job >= 200 && job <= 232) { // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434)) { // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 510 && job <= 512)) {
                        maxhp += Randomizer.rand(28, 32);
                    } else if ((job >= 500 && job <= 532)) { // Pirate
                        improvingMaxHP = SkillFactory.getSkill(5100000);
                        improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += Randomizer.rand(16, 20) + improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        } else {
                            maxhp += Randomizer.rand(16, 20);
                        }
                        //maxhp += Randomizer.rand(18, 22);
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxhp += Randomizer.rand(40, 50);
                    } else { // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = Math.min(30000, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp, chr);
                    statupdate.put(MapleStat.MAXHP, maxhp);
                    break;
                case 0x8000: // MP
                    type = "MP";
                    int maxmp = stat.getMaxMp();
                    if (chr.getHpApUsed() == 10000 || stat.getMaxMp() == 30000) {
                        return;
                    }

                    if (job == 0) { // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 100 && job <= 132) { // Warrior
                        maxmp += Randomizer.rand(2, 4);
                    } else if (job >= 200 && job <= 232) { // Magician
                        Skill improvingMaxMP = SkillFactory.getSkill(2000001);
                        int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += Randomizer.rand(18, 20) + improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                        } else {
                            maxmp += Randomizer.rand(18, 20);
                        }
                        //maxmp += Randomizer.rand(38, 40);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532)) { // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if (job >= 100 && job <= 132) { // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxmp += Randomizer.rand(6, 9);
                    } else { // GameMaster
                        maxmp += Randomizer.rand(50, 100);

                    }
                    maxmp = Math.min(30000, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp, chr);
                    statupdate.put(MapleStat.MAXMP, maxmp);
                    break;
                default:
                    c.sendPacket(CWvsContext.enableActions());
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.put(MapleStat.AVAILABLEAP, chr.getRemainingAp());
            c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
            if (chr.isAdmin()) {
                chr.dropMessage(5, "手动增加能力值 - 能力类型: " + type + " - 数值: " + statupdate);
            }
        }
        System.out.println(slea.toString());
    }

    public static void DistributeSP(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        SetConstants skillfj_id = new SetConstants();
        String skillidfjid[] = skillfj_id.getSkillfj_id();

        c.getPlayer().updateTick(slea.readInt());
        final int skillid = slea.readInt();
        //final int amount = slea.readInt();//v148
        boolean isBeginnerSkill = false;
        final int remainingSp;
        if (GameConstants.isBeginnerJob(skillid / 10000) && (skillid % 10000 == 1000 || skillid % 10000 == 1001 || skillid % 10000 == 1002 || skillid % 10000 == 2)) {
            final boolean resistance = skillid / 10000 == 3000 || skillid / 10000 == 3001;
            final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1000));
            final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1001));
            final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + (resistance ? 2 : 1002)));
            remainingSp = Math.min((chr.getLevel() - 1), resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
            isBeginnerSkill = true;
        } else if (GameConstants.isBeginnerJob(skillid / 10000)) {
            return;
        } else {
            remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
        }
        Skill skill = SkillFactory.getSkill(skillid);
        for (Pair<String, Integer> ski : skill.getRequiredSkills()) {
            if (ski.left.equals("level")) {
                if (chr.getLevel() < ski.right) {
                    return;
                }
            } else {
                int left = Integer.parseInt(ski.left);
                if (chr.getSkillLevel(SkillFactory.getSkill(left)) < ski.right) {
                    //AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill without the required skill (" + skillid + ")");
                    return;
                }
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !GameConstants.isDualBlade(chr.getJob()) && !isBeginnerSkill && chr.getMasterLevel(skill) <= 0)) {
                c.sendPacket(CWvsContext.enableActions());
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }
        for (String skillidfjid1 : skillidfjid) {
            if (skill.getId() == Integer.parseInt(skillidfjid1)) {
                c.getPlayer().dropMessage(1, "这个技能暂未开放.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        if ((remainingSp >= 0 && curLevel + 1 <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skillid);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
            chr.changeSingleSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            //} else if (!skill.canBeLearnedBy(chr.getJob())) {
            //    AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill for a different job (" + skillid + ")");
        } else {
            System.out.println("Skill errors!!");
            System.out.println("isbeginner " + isBeginnerSkill);
            System.out.println("canlearn " + skill.canBeLearnedBy(chr.getJob()));
            System.out.println("remainingsp " + remainingSp);
            System.out.println("amount " + 1);
            System.out.println("curlvl " + curLevel);
            System.out.println("maxlvl " + maxlevel);
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static final void AutoAssignAP(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        Map statupdate = new EnumMap(MapleStat.class);
        c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
        PlayerStats playerst = chr.getStat();
        int count = slea.readInt();
        String type = "";
        for (int i = 0; i < count; i++) {
            int PrimaryStat = slea.readInt();
            int updatenumber = slea.readInt();
            if (chr.getRemainingAp() >= updatenumber) {
                switch (PrimaryStat) {
                    case 0x100://256 力量
                        type = "力量";
                        if (playerst.getStr() + updatenumber > statLimit) {
                            return;
                        }
                        playerst.setStr((short) (playerst.getStr() + updatenumber), chr);
                        statupdate.put(MapleStat.STR, playerst.getStr());
                        break;
                    case 0x200://512 敏捷
                        type = "敏捷";
                        if (playerst.getDex() + updatenumber > statLimit) {
                            return;
                        }
                        playerst.setDex((short) (playerst.getDex() + updatenumber), chr);
                        statupdate.put(MapleStat.DEX, playerst.getDex());
                        break;
                    case 0x400://1024 智力
                        type = "智力";
                        if (playerst.getInt() + updatenumber > statLimit) {
                            return;
                        }
                        playerst.setInt((short) (playerst.getInt() + updatenumber), chr);
                        statupdate.put(MapleStat.INT, playerst.getInt());
                        break;
                    case 0x800://2048 运气
                        type = "运气";
                        if (playerst.getLuk() + updatenumber > statLimit) {
                            return;
                        }
                        playerst.setLuk((short) (playerst.getLuk() + updatenumber), chr);
                        statupdate.put(MapleStat.LUK, playerst.getLuk());
                        break;
                    default:
                        c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
                        return;
                }
                if (chr.isAdmin()) {
                    chr.dropMessage(6, "自动分配能力值 - 待分配的主属性数量：" + count + "种， 分配到：" + type + " - 分配数量：" + updatenumber + " 点.");
                }
                chr.setRemainingAp((short) (chr.getRemainingAp() - updatenumber));
            }
        }
        statupdate.put(MapleStat.AVAILABLEAP, chr.getRemainingAp());
        c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
    }

    public static void DistributeHyper(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        SetConstants skillfj_id = new SetConstants();
        String skillidfjid[] = skillfj_id.getSkillfj_id();

        chr.updateTick(slea.readInt());
        int skillid = slea.readInt();
        final Skill skill = SkillFactory.getSkill(skillid);
        final int remainingSp = chr.getRemainingHSp(skill.getHyper() - 1);

        final int maxlevel = 1;
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if (maxlevel <= 0) {
                c.sendPacket(CWvsContext.enableActions());
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }
        for (String skillidfjid1 : skillidfjid) {
            if (skill.getId() == Integer.parseInt(skillidfjid1)) {
                c.getPlayer().dropMessage(1, "这个技能暂未开放.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        if ((remainingSp >= 1 && curLevel == 0) && skill.canBeLearnedBy(chr.getJob())) {
            chr.setRemainingHSp(skill.getHyper() - 1, remainingSp - 1);
            chr.changeSingleSkillLevel(skill, (byte) 1, (byte) 1, -1L, true);
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void ResetHyper(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        short times = slea.readShort();
        if (times < 1 || times > 3) {
            times = 3;
        }
        int price = 10000 * (int) Math.pow(10, times);
        if (chr.getMeso() < price) {
            chr.dropMessage(1, "你没有足够的金币.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int ssp = 0;
        int spp = 0;
        int sap = 0;
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (skil.isHyper()) {
                sa.put(skil, new SkillEntry(0, (byte) 1, -1));
                if (skil.getHyper() == 1) {
                    ssp++;
                } else if (skil.getHyper() == 2) {
                    spp++;
                } else if (skil.getHyper() == 3) {
                    sap++;
                }
            }
        }
        chr.gainMeso(-price, false);
        chr.changeSkillsLevel(sa, true);
        chr.gainHSP(0, ssp);
        chr.gainHSP(1, spp);
        chr.gainHSP(2, sap);
    }
}
