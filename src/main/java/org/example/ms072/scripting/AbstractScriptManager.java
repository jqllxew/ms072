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
package org.example.ms072.scripting;

import org.example.ms072.client.MapleClient;
import org.example.ms072.tools.FileoutputUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    private final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                InputStream scriptStream = getClass().getClassLoader().getResourceAsStream(path);
                if (scriptStream == null) {
                    return null;
                }
                engine = sem.getEngineByName("graal.js");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                BufferedReader bf = new BufferedReader(new InputStreamReader(scriptStream, StandardCharsets.UTF_8));
                engine.eval(bf);
            } else if (npc) {
                c.getPlayer().dropMessage(-1, "You already are talking to this NPC. Use @ea if this is not intended.");
            }
            return (Invocable) engine;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log("logs/异常/脚本异常.log", "Error executing script. Path: " + path + "\r\nException " + e);
            return null;
        }
    }
}
