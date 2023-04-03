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
package handling.login;

import client.MapleClient;
import constants.GameConstants;
import constants.ServerConfig;
import handling.MapleServerHandler;
import handling.ServerType;
import handling.netty.ServerConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import tools.Triple;

public class LoginServer {

    public static int PORT = 5100;
    private static InetSocketAddress InetSocketadd;
    private static ServerConnection init;
    private static Map<Integer, Integer> load = new HashMap<>();
    private static String serverName, eventMessage;
    private static byte flag;
    private static int maxCharacters, userLimit, usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;
    private static final HashMap<Integer, Triple<String, String, Integer>> loginAuth = new HashMap<>();
    private static final HashSet<String> loginIPAuth = new HashSet<>();
    private static final Map<Integer, Long> LoginTime = new WeakHashMap<>();
    private static AccountStorage clients;
    
    public static boolean CheckSelectChar(int accid, long lastTime) {
        if (LoginTime.containsKey(accid)) {
            long lastLoginTime = LoginTime.get(accid);
            if (lastLoginTime + 8000 > System.currentTimeMillis()) {
                return false;
            }
            LoginTime.remove(accid);
        } else {
            LoginTime.put(accid, lastTime);
        }
        return true;
    }

    public static void putLoginTime(int accid, long lastTime) {
        LoginTime.put(accid, lastTime);
    }

    public static void putLoginAuth(int chrid, String ip, String tempIP, int channel) {
        Triple<String, String, Integer> put = loginAuth.put(chrid, new Triple<>(ip, tempIP, channel));
        loginIPAuth.add(ip);
    }

    public static Triple<String, String, Integer> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }

    public static void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public static void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() {
        userLimit = ServerConfig.userLimit;
        serverName = ServerConfig.serverName;
        eventMessage = ServerConfig.eventMessage;
        flag = ServerConfig.flag;
        adminOnly = ServerConfig.adminOnly;
        maxCharacters = ServerConfig.maxCharacters;
        PORT = ServerConfig.LoginPort;

        try {
            init = new ServerConnection(PORT, -1, -1, ServerType.登录服务器);
            init.run();
        } catch (Exception e) {
            System.out.println("登录服务器 绑定端口：" + PORT + " 失败" + e);
        }
    }

    public static final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("正在关闭登录服务器...");
        init.close();
        finishedShutdown = true; //nothing. lol
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getTrueServerName() {
        return serverName.substring(0, serverName.length() - (GameConstants.GMS ? 2 : 3));
    }

    public static String getEventMessage() {
        return eventMessage;
    }

    public static int getMaxCharacters() {
        return maxCharacters;
    }

    public static Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static String getEventMessage(int world) { //TODO: Finish this
        switch (world) {
            case 0:
                return null;
        }
        return null;
    }

    public static final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    //public static final int getNumberOfSessions() {
    //    return acceptor.getManagedSessions(InetSocketadd).size();
    //}

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }

    public static String ChangeAdminOnly() {
        adminOnly = !isAdminOnly();
        return adminOnly ? "开启" : "关闭";
    }
    
    public static void forceRemoveClient(MapleClient client) {
        Collection<MapleClient> cls = getClientStorage().getAllClientsThreadSafe();
        for (MapleClient c : cls) {
            if (c == null) {
                continue;
            }
            if (c.getAccID() == client.getAccID() || c == client) {
                if (c != client) {
                    c.unLockDisconnect();
                }
                removeClient(c);
            }
        }
    }

    public static AccountStorage getClientStorage() {
        if (clients == null) {
            clients = new AccountStorage();
        }
        return clients;
    }

    public static final void addClient(final MapleClient c) {
        getClientStorage().registerAccount(c);
    }

    public static final void removeClient(final MapleClient c) {
        getClientStorage().deregisterAccount(c);
    }
}
