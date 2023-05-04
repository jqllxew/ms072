package org.example.ms072.handling.cashshop;

import  org.example.ms072.constants.ServerConfig;
import org.example.ms072.handling.MapleServerHandler;
import org.example.ms072.handling.ServerType;
import  org.example.ms072.handling.channel.PlayerStorage;
import  org.example.ms072.handling.netty.ServerConnection;
import java.io.IOException;
import java.net.InetSocketAddress;

public class CashShopServer {

    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private static int PORT = 8600;
    private static ServerConnection init;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run_startup_configurations() {
        //System.out.print("正在载入商城服务器...");
        PORT = ServerConfig.CashPort;
        ip = ServerConfig.interface_ + ":" + PORT;

        players = new PlayerStorage(-10);

        try {
            init = new ServerConnection(PORT, 0, -10, ServerType.商城服务器);
            init.run();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("商城服务器 绑定端口：" + PORT + "失败！", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }
    
    public static int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }
    
    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("正在关闭商城服务器...");
        players.disconnectAll();
        System.out.println("商城服务器解除端口绑定...");
        init.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
