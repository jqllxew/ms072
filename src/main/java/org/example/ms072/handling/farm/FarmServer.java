/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.ms072.handling.farm;

import  org.example.ms072.constants.ServerConfig;
import org.example.ms072.handling.ServerType;
import org.example.ms072.handling.channel.PlayerStorage;
import org.example.ms072.handling.netty.ServerConnection;
import java.net.InetSocketAddress;

/**
 *
 * @author Itzik
 */
public class FarmServer {

    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private static final int PORT = 8601;
    private static ServerConnection init;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run_startup_configurations() {
        ip = ServerConfig.interface_ + ":" + PORT;

        players = new PlayerStorage(-30);
        try {
            init = new ServerConnection(PORT, 0, -30, ServerType.farm服务器);
            init.run();
            System.out.println("Farm Server is listening on port 8601.");
        } catch (Exception e) {
            System.err.println("Binding to port 8601 failed");
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (Farm)...");
        players.disconnectAll();
        System.out.println("Shutting down Farm...");
        init.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
