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
package org.example.ms072.handling.channel.handler;

import  org.example.ms072.client.MapleCharacterUtil;
import  org.example.ms072.client.MapleCharacter;
import  org.example.ms072.client.MapleClient;
import  org.example.ms072.handling.world.World;
import  org.example.ms072.handling.world.family.MapleFamily;
import  org.example.ms072.handling.world.family.MapleFamilyBuff;
import  org.example.ms072.handling.world.family.MapleFamilyCharacter;
import java.util.List;
import  org.example.ms072.server.maps.FieldLimitType;
import  org.example.ms072.tools.data.LittleEndianAccessor;
import  org.example.ms072.tools.packet.CWvsContext;
import  org.example.ms072.tools.packet.CWvsContext.FamilyPacket;

public class FamilyHandler {

    public static final void RequestFamily(final LittleEndianAccessor slea, MapleClient c) {
        //90 00 06 00 4D 69 6E 61 4D 53
        //90 00 06 00 4D 69 6E 61 4D 53
        MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.sendPacket(FamilyPacket.getFamilyPedigree(chr));
        }
    }

    /*打开学院*/
    public static final void OpenFamily(final LittleEndianAccessor slea, MapleClient c) {
        c.sendPacket(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }

    /*使用学院BUFF*/
    public static final void UseFamily(final LittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt();
        if (MapleFamilyBuff.values().length <= type) {
            return;
        }
        MapleFamilyBuff entry = MapleFamilyBuff.values()[type];
        boolean success = c.getPlayer().getFamilyId() > 0 && c.getPlayer().canUseFamilyBuff(entry) && c.getPlayer().getCurrentRep() > entry.rep;
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (entry) {
            case 瞬移: //teleport: need add check for if not a safe place
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "传唤失败。您当前的位置或状态不允许传唤.");
                    success = false;
                } else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "无效名称或您不在同一频道.");
                    success = false;
                } else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId() && !victim.isInBlockedMap()) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                } else {
                    c.getPlayer().dropMessage(5, "传唤失败。您当前的位置或状态不允许传唤.");
                    success = false;
                }
                break;
            case 召唤: // TODO give a check to the player being forced somewhere else..
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) || c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "传唤失败。您当前的位置或状态不允许传唤.");
                } else if (victim == null || (victim.isGM() && !c.getPlayer().isGM())) {
                    c.getPlayer().dropMessage(1, "无效名称或您不在同一频道.");
                } else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "另一个角色要求传唤这个角色。请稍后再试.");
                } else if (victim.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(victim.getMap().getFieldLimit()) && victim.getId() != c.getPlayer().getId() && !victim.isInBlockedMap()) {
                    victim.getClient().sendPacket(FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                } else {
                    c.getPlayer().dropMessage(5, "传唤失败。您当前的位置或状态不允许传唤.");
                }
                return; //RETURN not break
            /*case Drop_12_15: // drop rate + 50% 15 min
            case EXP_12_15: // exp rate + 50% 15 min
            case Drop_12_30: // drop rate + 100% 15 min
            case Drop_15_15:
               entry.applyTo(c.getPlayer());
                break;*/
 /*case Bonding: // 6 family members in pedigree online Drop Rate & Exp Rate + 100% 30 minutes
                final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
                List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7) {
                    success = false;
                } else {
                    for (MapleFamilyCharacter chrz : chrs) {
                        int chr = World.Find.findChannel(chrz.getId());
                        if (chr == -1) {
                            continue; //STOP WTF?! take reps though..
                        }
                        MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                        entry.applyTo(chrr);
                        //chrr.getClient().sendPacket(FamilyPacket.familyBuff(entry.type, type, entry.effect, entry.duration*60000));
                    }
                }
                break;*/

        }
        if (success) { //again
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.sendPacket(FamilyPacket.changeRep(-entry.rep, c.getPlayer().getName()));
            c.getPlayer().useFamilyBuff(entry);
        } else {
            c.getPlayer().dropMessage(5, "发生了一个错误.");
        }
    }

    /*学院操作*/
    public static final void FamilyOperation(final LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x02)); //角色不在线，或角色名不正确。
        } else if (addChr.getFamilyId() == c.getPlayer().getFamilyId() && addChr.getFamilyId() > 0) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x03)); //是同一冒险学院
        } else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x06)); //只有在同一地图中的角色才能登录为同学
        } else if (addChr.getSeniorId() != 0) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x07)); //已经是其他角色的同学
        } else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x08)); //只能将比自己等级低的角色登录为同学
        } else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
            c.sendPacket(FamilyPacket.sendFamilyMessage(0x09)); //等级差异超过20，无法登录为同学。
        } else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "被邀请的角色等级必须大于10级。");
        } else if (c.getPlayer().getJunior1() > 0 && c.getPlayer().getJunior2() > 0) {
            c.getPlayer().dropMessage(1, "你已经有2位同学，无法继续邀请。");
        } else if (c.getPlayer().isGM() || !addChr.isGM()) {
            addChr.getClient().sendPacket(FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void FamilyPrecept(final LittleEndianAccessor slea, MapleClient c) {
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        if (fam == null || fam.getLeaderId() != c.getPlayer().getId()) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
    }

    /*学院召唤同学*/
    public static final void FamilySummon(final LittleEndianAccessor slea, MapleClient c) {
        MapleFamilyBuff cost = MapleFamilyBuff.召唤;
        MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (c.getPlayer().getFamilyId() > 0 && tt != null && tt.getFamilyId() == c.getPlayer().getFamilyId() && !FieldLimitType.VipRock.check(tt.getMap().getFieldLimit())
                && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && tt.canUseFamilyBuff(cost)
                && c.getPlayer().getTeleportName().equals(tt.getName()) && tt.getCurrentRep() > cost.rep && !c.getPlayer().isInBlockedMap() && !tt.isInBlockedMap()) {
            //whew lots of checks
            boolean accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().sendPacket(FamilyPacket.changeRep(-cost.rep, tt.getName()));
                tt.useFamilyBuff(cost);
            } else {
                tt.dropMessage(5, "召唤失败。您当前的位置或状态不允许召唤学院同学.");
            }
        } else {
            c.getPlayer().dropMessage(5, "召唤失败。您当前的位置或状态不允许召唤学院同学.");
        }
        c.getPlayer().setTeleportName("");
    }

    public static final void DeleteJunior(final LittleEndianAccessor slea, MapleClient c) {
        int juniorid = slea.readInt();
        if (c.getPlayer().getFamilyId() <= 0 || juniorid <= 0 || (c.getPlayer().getJunior1() != juniorid && c.getPlayer().getJunior2() != juniorid)) {
            return;
        }
        //junior is not required to be online.
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        final MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        final MapleFamilyCharacter oth = c.getPlayer().getMFC();
        boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);
        //if (!other.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());
        //}
        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " 已要求断绝与你的关系，所以家族关系已经结束了.", 0);
        MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(other.getName());
        if (receiver != null) {
            receiver.showNote();
        }
        if (!fam.splitFamily(juniorid, other)) { //juniorid splits to make their own family. function should handle the rest
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "已和(" + other.getName() + ")诀别。冒险学院关系已经结束。");
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void DeleteSenior(final LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getFamilyId() <= 0 || c.getPlayer().getSeniorId() <= 0) {
            return;
        }
        //not required to be online
        final MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId()); //this is old family
        final MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        final MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }
        //if (!mgc.isOnline()) {
        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());
        //}
        c.getPlayer().saveFamilyStatus();
        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + " 已要求断绝与你的关系，所以家族关系已经结束了.", 0);
        MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(mgc.getName());
        if (receiver != null) {
            receiver.showNote();
        }
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) { //now, we're the family leader
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getPlayer().dropMessage(1, "已和 (" + mgc.getName() + ")诀别。冒险学院关系已经结束。");
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void AcceptFamily(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (inviter != null
                && c.getPlayer().getSeniorId() == 0
                && (c.getPlayer().isGM() || !inviter.isHidden())
                && inviter.getLevel() - 20 <= c.getPlayer().getLevel()
                && inviter.getLevel() >= 10
                && inviter.getName().equals(slea.readMapleAsciiString())
                && inviter.getNoJuniors() < 2
                /*&& inviter.getFamily().getGens() < 1000*/
                && c.getPlayer().getLevel() >= 10) {

            boolean accepted = slea.readByte() > 0;
            inviter.getClient().sendPacket(FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                //c.sendPacket(FamilyPacket.sendFamilyMessage(0));
                c.sendPacket(FamilyPacket.getSeniorMessage(inviter.getName()));
                int old = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getFamilyId();
                int oldj1 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior1();
                int oldj2 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior2();
                if (inviter.getFamilyId() > 0 && World.Family.getFamily(inviter.getFamilyId()) != null) {
                    MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());
                    //if old isn't null, don't set the familyid yet, mergeFamily will take care of it
                    c.getPlayer().setFamily(old <= 0 ? inviter.getFamilyId() : old, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                    MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    } else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if (old > 0 && World.Family.getFamily(old) != null) { //has junior
                        MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                    } else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    if (fam != null) {
                        if (inviter.getNoJuniors() == 1 || old > 0) {//just got their first junior whoopee
                            fam.resetDescendants();
                        }
                        fam.resetPedigree(); //is this necessary?
                    }
                } else {
                    int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        //before loading the family, set sql
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0); //load the family
                        inviter.finishAchievement(36);
                        c.getPlayer().setFamily(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        MapleFamily fam = World.Family.getFamily(id);
                        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if (old > 0 && World.Family.getFamily(old) != null) { //has junior
                            MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                        } else {
                            fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam.resetDescendants();
                        fam.resetPedigree();

                    }
                }
                c.sendPacket(FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
    }
}
