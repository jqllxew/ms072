/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.server.farm.inventory;

import  org.example.ms072.client.inventory.Equip;
import java.util.List;
import java.util.Map;
import org.example.ms072.server.StructRewardItem;
import  org.example.ms072.tools.Triple;

/**
 *
 * @author Itzik
 */
public class FarmItemInformation {

    public List<Integer> scrollReqs = null, questItems = null, incSkill = null;
    public short slotMax, itemMakeLevel;
    public Equip eq = null;
    public Map<String, Integer> equipStats;
    public double price = 0.0;
    public int itemId, wholePrice, monsterBook, stateChange, meso, questId, totalprob, replaceItem, mob, cardSet, create, flag, npc;
    public String name, desc, msg, replaceMsg, afterImage, script;
    public byte karmaEnabled;
    public List<StructRewardItem> rewardItems = null;
    public List<Triple<String, String, String>> equipAdditions = null;
    public Map<Integer, Map<String, Integer>> equipIncs = null;
}
