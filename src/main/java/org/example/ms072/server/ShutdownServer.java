package org.example.ms072.server;

import org.example.ms072.handling.cashshop.CashShopServer;
import org.example.ms072.handling.channel.ChannelServer;
import org.example.ms072.handling.login.LoginServer;
import org.example.ms072.handling.world.World;
import org.example.ms072.server.Timer.*;
import org.example.ms072.tools.FileoutputUtil;
import org.example.ms072.tools.packet.CWvsContext;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class ShutdownServer implements ShutdownServerMBean {

    public static ShutdownServer instance;

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ShutdownServer();
            mBeanServer.registerMBean(instance, new ObjectName("server:type=ShutdownServer"));
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            System.out.println("Error registering Shutdown MBean");
        }
    }

    public static ShutdownServer getInstance() {
        return instance;
    }
    public int mode = 0;

    @Override
    public void shutdown() {//can execute twice
        run();
    }

    @Override
    public void run() {
        World.isShutDown = true;
        try {
            int ret = 0;
            World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0, "游戏服务器将关闭维护，请玩家安全下线..."));
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.setShutdown();
                cs.setServerMessage("游戏服务器将关闭维护，请玩家安全下线...");
                ret += cs.closeAllMerchant();
            }
            System.out.println("共保存了" + ret + "个雇佣商人");
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
            System.out.println("服务端关闭事件 1 已完成.");
            System.out.println("服务端关闭事件 2 开始...");

            Integer[] chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
            for (int i : chs) {
                try {
                    ChannelServer cs = ChannelServer.getInstance(i);
                    synchronized (this) {
                        cs.shutdown();
                    }
                } catch (Exception e) {
                }
            }
            try {
                LoginServer.shutdown();
                System.out.println("登录服务器关闭完成...");
            } catch (Exception e) {
            }
            try {
                CashShopServer.shutdown();
                System.out.println("商城服务器关闭完成...");
            } catch (Exception e) {
            }
            //try {
            //DatabaseConnection.closeAll();
            // } catch (Exception e) {
            // }
            try {
                WorldTimer.getInstance().stop();
                MapTimer.getInstance().stop();
                BuffTimer.getInstance().stop();
                CloneTimer.getInstance().stop();
                EventTimer.getInstance().stop();
                CheatTimer.getInstance().stop();
                EtcTimer.getInstance().stop();
                PingTimer.getInstance().stop();

            } catch (Exception e) {
            }
            System.out.println("服务器关闭事件 2 已完成.");
            /*    try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //shutdown
            }*/
        } catch (Exception ex) {
            FileoutputUtil.log("logs/关服异常.log", "错误信息：" + ex.toString());
        }
        //not sure if this is really needed for ChannelServer
        System.exit(0);
    }
}
