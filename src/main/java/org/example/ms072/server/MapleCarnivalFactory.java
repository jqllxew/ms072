package org.example.ms072.server;

import org.example.ms072.client.MapleDisease;
import org.example.ms072.provider.MapleData;
import org.example.ms072.provider.MapleDataProvider;
import org.example.ms072.provider.MapleDataProviderFactory;
import org.example.ms072.provider.MapleDataTool;
import org.example.ms072.server.life.MobSkill;
import org.example.ms072.server.life.MobSkillFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapleCarnivalFactory {

    private final static MapleCarnivalFactory instance = new MapleCarnivalFactory();
    private final Map<Integer, MCSkill> skills = new HashMap<>();
    private final Map<Integer, MCSkill> guardians = new HashMap<>();
    private final MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider(new File((System.getProperty("wzpath") != null ? System.getProperty("wzpath") : "") + "wz/Skill.wz"));

    public MapleCarnivalFactory() {
        //whoosh
        initialize();
    }

    public static final MapleCarnivalFactory getInstance() {
        return instance;
    }

    private void initialize() {
        if (!skills.isEmpty()) {
            return;
        }
        for (MapleData z : dataRoot.getData("MCSkill.img")) {
            skills.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), MapleDataTool.getInt("target", z, 1) > 1));
        }
        for (MapleData z : dataRoot.getData("MCGuardian.img")) {
            guardians.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), true));
        }
    }

    public MCSkill getSkill(final int id) {
        return skills.get(id);
    }

    public MCSkill getGuardian(final int id) {
        return guardians.get(id);
    }

    public static class MCSkill {

        public int cpLoss, skillid, level;
        public boolean targetsAll;

        public MCSkill(int _cpLoss, int _skillid, int _level, boolean _targetsAll) {
            cpLoss = _cpLoss;
            skillid = _skillid;
            level = _level;
            targetsAll = _targetsAll;
        }

        public MobSkill getSkill() {
            return MobSkillFactory.getMobSkill(skillid, 1); //level?
        }

        public MapleDisease getDisease() {
            if (skillid <= 0) {
                return MapleDisease.getRandom();
            }
            return MapleDisease.getBySkill(skillid);
        }
    }
}
