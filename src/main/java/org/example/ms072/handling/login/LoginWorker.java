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
package org.example.ms072.handling.login;

import  org.example.ms072.client.MapleClient;
import org.example.ms072.handling.channel.ChannelServer;
import  org.example.ms072.handling.login.handler.CharLoginHandler;
import  org.example.ms072.server.Timer.PingTimer;
import  org.example.ms072.tools.FileoutputUtil;
import  org.example.ms072.tools.packet.CWvsContext;
import  org.example.ms072.tools.packet.LoginPacket;

import java.util.Map;
import java.util.Map.Entry;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.sendPacket(CWvsContext.broadcastMsg(1, "当前服务器设置只能管理员进入游戏.\r\n请稍后再试."));
            c.sendPacket(LoginPacket.getLoginFailed(7));
            return;
        }

        if (System.currentTimeMillis() - lastUpdate > 600000) { // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load.size() <= 0) { // In an unfortunate event that client logged in before load
                lastUpdate = 0;
                c.sendPacket(LoginPacket.getLoginFailed(7));
                return;
            }
            final double loadFactor = 1200 / ((double) LoginServer.getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }

        if (c.finishLogin() == 0) {
            LoginServer.forceRemoveClient(c);
            ChannelServer.forceRemovePlayerByAccId(c, c.getAccID());
            LoginServer.getClientStorage().registerAccount(c);
            c.sendPacket(LoginPacket.getAuthSuccessRequest(c));
            CharLoginHandler.ServerListRequest(c);
            c.setIdleTask(PingTimer.getInstance().schedule(() -> {
                c.getSession().close();
                FileoutputUtil.logToFile(FileoutputUtil.DC_Log, "\r\n服务器主动断开用户端连接，调用位置: " + new Throwable().getStackTrace()[0]);
            }, 10 * 60 * 10000));
        } else {
            c.sendPacket(LoginPacket.getLoginFailed(7));
        }
    }
}
