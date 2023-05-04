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

import  org.example.ms072.client.MapleClient;
import  org.example.ms072.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import  org.example.ms072.server.maps.MapleReactor;
import  org.example.ms072.server.maps.ReactorDropEntry;
import  org.example.ms072.tools.FileoutputUtil;

public class ReactorScriptManager extends AbstractScriptManager {

    private static final ReactorScriptManager instance = new ReactorScriptManager();
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    public static final ReactorScriptManager getInstance() {
        return instance;
    }

    public final void act(final MapleClient c, final MapleReactor reactor) {
        try {
            final Invocable iv = getInvocable("reactor/" + reactor.getReactorId() + ".js", c);

            if (iv == null) {
                return;
            }
            final ScriptEngine scriptengine = (ScriptEngine) iv;
            ReactorActionManager rm = new ReactorActionManager(c, reactor);
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage("你已建立与反应物 " + reactor.getReactorId() + " 的连结");
            }
            scriptengine.put("rm", rm);
            iv.invokeFunction("act");
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing reactor script. ReactorID: " + reactor.getReactorId() + ", ReactorName: " + reactor.getName() + ":" + e);
        }
    }

    public final List<ReactorDropEntry> getDrops(final int rid) {
        List<ReactorDropEntry> ret = drops.get(rid);
        if (ret != null) {
            return ret;
        }
        ret = new LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            ps = con.prepareStatement("SELECT * FROM reactordrops WHERE reactorid = ?");
            ps.setInt(1, rid);
            rs = ps.executeQuery();

            while (rs.next()) {
                ret.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
            }
            rs.close();
            ps.close();
        } catch (final SQLException e) {
            System.err.println("Could not retrieve drops for reactor " + rid + e);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", e);
            return ret;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ignore) {
                FileoutputUtil.outputFileError("logs/数据库异常.txt", ignore);
                return ret;
            }
        }
        drops.put(rid, ret);
        return ret;
    }

    public final void clearDrops() {
        drops.clear();
    }
}
