package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.OnlyID;
import constants.ServerConfig;
import constants.ServerConstants;
import database.DatabaseConnection;
import gui.MS072;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleDojoRanking;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.PingTimer;
import server.Timer.WorldTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.CustomPlayerRankings;
import tools.FileoutputUtil;

public class Start {

    private static MS072 CashGui;
    public static boolean Check = true;
    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);

    public void run(boolean openGui) throws InterruptedException, IOException {
        long start = System.currentTimeMillis();
        //System.out.println("Loading Properties");
        loadProperties();
        //System.out.println("Loading loadon_off");
        loadon_off();
        System.out.println("Loading ServerIP: " + ServerConfig.interface_ + ":" + LoginServer.PORT);
        System.out.println("Loading Client version: 0" + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);
        System.out.println("Loading WorldServer");
        World.init();
        System.out.println("Loading TimerThread");
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        System.out.println("Loading MapleDojoRanking");
        MapleDojoRanking.getInstance().load();
        System.out.println("Loading MapleGuildRanking");
        MapleGuildRanking.getInstance().load();
        System.out.println("Loading CustomPlayerRankings");
        CustomPlayerRankings.getInstance().load();
        System.out.println("Loading MapleGuild");
        MapleGuild.loadAll();
        System.out.println("Loading MapleFamily");
        MapleFamily.loadAll();
        System.out.println("Loading MapleInventoryIdentifier");
        MapleInventoryIdentifier.getInstance();
        System.out.println("Loading MapleMapFactory");
        MapleMapFactory.loadCustomLife();
        System.out.println("Loading MapleLifeFactory");
        MapleLifeFactory.loadQuestCounts();
        System.out.println("Loading MapleQuest");
        MapleQuest.initQuests();
        System.out.println("Loading MapleOxQuizFactory");
        MapleOxQuizFactory.getInstance();
        System.out.println("Loading PredictCardFactory");
        PredictCardFactory.getInstance().initialize();
        System.out.println("Loading MapleMonsterInformationProvider");
        MapleMonsterInformationProvider.getInstance().load();
        MapleMonsterInformationProvider.getInstance().addExtra();
        System.out.println("Loading MapleItemInformationProvider");
        MapleItemInformationProvider.getInstance().runItems();
        MapleItemInformationProvider.getInstance().runEtc();
        System.out.println("Loading SkillFactory");
        SkillFactory.load();
        System.out.println("Loading MobSkillFactory");
        MobSkillFactory.getInstance();
        System.out.println("Loading MapleCarnivalFactory");
        MapleCarnivalFactory.getInstance();
        System.out.println("Loading RandomRewards");
        RandomRewards.load();
        System.out.println("Loading LoginInformationProvider");
        LoginInformationProvider.getInstance();
        System.out.println("Loading SpeedRunner");
        SpeedRunner.loadSpeedRuns();
        System.out.println("Loading CashItemFactory");
        CashItemFactory.getInstance().initialize();
        System.out.println("Loading MapleServerHandler");
        // MapleServerHandler.initiate();
        //System.out.println("Loading LoginServer");
        LoginServer.run_startup_configurations();
        //System.out.println("Loading ChannelServer");
        ChannelServer.startChannel_Main();
        //System.out.println("Loading CashShopServer");
        CashShopServer.run_startup_configurations();
        //FarmServer.run_startup_configurations();
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        System.out.println("Loading registerRespawn");
        World.registerRespawn();
        System.out.println("Loading ShutdownServer");
        ShutdownServer.registerMBean();
        System.out.println("Loading clearDrops");
        MapleMonsterInformationProvider.getInstance().clearDrops();
        PlayerNPC.loadAll();
        LoginServer.setOn();
        RankingWorker.run();
        OnlyID.getInstance();
        long now = System.currentTimeMillis() - start;
        long seconds = now / 1000;
        long ms = now % 1000;
        System.out.println(
                "经验倍率：" + ServerConfig.ExpRate + "倍"
                + " 金币倍率：" + ServerConfig.MesoRate + "倍"
                + " 怪物倍率：" + ServerConfig.DropRate + "倍"
                + " BOSS倍率：" + ServerConfig.BossDropRate + "倍");
        System.out.println("服务端启动完毕 耗时: " + seconds + "秒 " + ms + "毫秒");
        if (openGui)
            CashGui();
    }

    public void startServer() throws InterruptedException {
        Check = false;//只启动一个实例
        long start = System.currentTimeMillis();
        //System.out.println("Loading Properties");
        loadProperties();
        //System.out.println("Loading loadon_off");
        loadon_off();
        System.out.println("Loading ServerIP: " + ServerConfig.interface_ + ":" + LoginServer.PORT);
        System.out.println("Loading Client version: 0" + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);
        System.out.println("Loading WorldServer");
        World.init();
        System.out.println("Loading TimerThread");
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        System.out.println("Loading MapleDojoRanking");
        MapleDojoRanking.getInstance().load();
        System.out.println("Loading MapleGuildRanking");
        MapleGuildRanking.getInstance().load();
        System.out.println("Loading CustomPlayerRankings");
        CustomPlayerRankings.getInstance().load();
        System.out.println("Loading MapleGuild");
        MapleGuild.loadAll();
        System.out.println("Loading MapleFamily");
        MapleFamily.loadAll();
        System.out.println("Loading MapleInventoryIdentifier");
        MapleInventoryIdentifier.getInstance();
        System.out.println("Loading MapleMapFactory");
        MapleMapFactory.loadCustomLife();
        System.out.println("Loading MapleLifeFactory");
        MapleLifeFactory.loadQuestCounts();
        System.out.println("Loading MapleQuest");
        MapleQuest.initQuests();
        System.out.println("Loading MapleOxQuizFactory");
        MapleOxQuizFactory.getInstance();
        System.out.println("Loading PredictCardFactory");
        PredictCardFactory.getInstance().initialize();
        System.out.println("Loading MapleMonsterInformationProvider");
        MapleMonsterInformationProvider.getInstance().load();
        MapleMonsterInformationProvider.getInstance().addExtra();
        System.out.println("Loading MapleItemInformationProvider");
        MapleItemInformationProvider.getInstance().runItems();
        MapleItemInformationProvider.getInstance().runEtc();
        System.out.println("Loading SkillFactory");
        SkillFactory.load();
        System.out.println("Loading MobSkillFactory");
        MobSkillFactory.getInstance();
        System.out.println("Loading MapleCarnivalFactory");
        MapleCarnivalFactory.getInstance();
        System.out.println("Loading RandomRewards");
        RandomRewards.load();
        System.out.println("Loading LoginInformationProvider");
        LoginInformationProvider.getInstance();
        System.out.println("Loading SpeedRunner");
        SpeedRunner.loadSpeedRuns();
        System.out.println("Loading CashItemFactory");
        CashItemFactory.getInstance().initialize();
        System.out.println("Loading MapleServerHandler");
        // MapleServerHandler.initiate();
        //System.out.println("Loading LoginServer");
        LoginServer.run_startup_configurations();
        //System.out.println("Loading ChannelServer");
        ChannelServer.startChannel_Main();
        //System.out.println("Loading CashShopServer");
        CashShopServer.run_startup_configurations();
        //FarmServer.run_startup_configurations();
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        System.out.println("Loading registerRespawn");
        World.registerRespawn();
        System.out.println("Loading ShutdownServer");
        ShutdownServer.registerMBean();
        System.out.println("Loading clearDrops");
        MapleMonsterInformationProvider.getInstance().clearDrops();
        PlayerNPC.loadAll();
        LoginServer.setOn();
        RankingWorker.run();
        OnlyID.getInstance();
        long now = System.currentTimeMillis() - start;
        long seconds = now / 1000;
        long ms = now % 1000;
        System.out.println(
                "经验倍率：" + ServerConfig.ExpRate + "倍"
                + " 金币倍率：" + ServerConfig.MesoRate + "倍"
                + " 怪物倍率：" + ServerConfig.DropRate + "倍"
                + " BOSS倍率：" + ServerConfig.BossDropRate + "倍");
        System.out.println("服务端启动完毕 耗时: " + seconds + "秒 " + ms + "毫秒");
    }

    public static void CashGui() {
        if (CashGui != null) {
            CashGui.dispose();
        }
        CashGui = new MS072();
        CashGui.setVisible(true);
    }

    public static void loadProperties() {
        Properties p = new Properties();
        try {
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream("ms072server.properties"), StandardCharsets.UTF_8);
            p.load(reader);
        } catch (IOException ex) {
            System.out.println("载入 ms072server.properties 失败");
            System.exit(0);
        }
        ServerConfig.serverMessage = p.getProperty("serverMessage");
        ServerConfig.eventMessage = p.getProperty("eventMessage");
        ServerConfig.LevelExp10 = Integer.parseInt(p.getProperty("LevelExp10", "1"));
        ServerConfig.LevelExp30 = Integer.parseInt(p.getProperty("LevelExp30", "1"));
        ServerConfig.LevelExp70 = Integer.parseInt(p.getProperty("LevelExp70", "1"));
        ServerConfig.LevelExp120 = Integer.parseInt(p.getProperty("LevelExp120", "1"));
        ServerConfig.LevelExp150 = Integer.parseInt(p.getProperty("LevelExp150", "1"));
        ServerConfig.maxCharacters = Integer.parseInt(p.getProperty("maxCharacters", "3"));
        ServerConfig.channelCount = Integer.parseInt(p.getProperty("channelCount", "2"));
        ServerConfig.userLimit = Integer.parseInt(p.getProperty("userLimit", "1500"));
        ServerConfig.logPackets = Boolean.parseBoolean(p.getProperty("logOps"));
        ServerConfig.adminOnly = Boolean.parseBoolean(p.getProperty("adminOnly"));
        ServerConfig.USE_FIXED_IV = Boolean.parseBoolean(p.getProperty("antiSniff"));
        ServerConfig.events = p.getProperty("Events");
        ServerConfig.interface_ = p.getProperty("ServerIP");
        ServerConfig.LoginPort = Integer.parseInt(p.getProperty("LoginPort", "5100"));
        ServerConfig.ChannelPort = Integer.parseInt(p.getProperty("ChannelPort", "2524"));
        ServerConfig.CashPort = Integer.parseInt(p.getProperty("CashPort", "5200"));
        ServerConfig.ExpRate = Integer.parseInt(p.getProperty("ExpRate", "1"));
        ServerConfig.DropRate = Integer.parseInt(p.getProperty("DropRate", "1"));
        ServerConfig.MesoRate = Integer.parseInt(p.getProperty("MesoRate", "1"));
        ServerConfig.BossDropRate = Integer.parseInt(p.getProperty("BossDropRate", "1"));
        ServerConfig.cashRate = Integer.parseInt(p.getProperty("cashRate", "1"));
        ServerConfig.traitRate = Integer.parseInt(p.getProperty("traitRate", "1"));
        ServerConfig.普通注册 = Boolean.parseBoolean(p.getProperty("AutoRegister"));
        ServerConfig.官方式注册 = Boolean.parseBoolean(p.getProperty("OfficialAutoRegister"));
        ServerConfig.ApStatLimit = Integer.parseInt(p.getProperty("ApStatLimit", "999"));
        // 数据库连接设定
        ServerConstants.SQL_PORT = p.getProperty("SQL_Port");
        ServerConstants.SQL_USER = p.getProperty("SQL_Username");
        ServerConstants.SQL_PASSWORD = p.getProperty("SQL_Password");
        ServerConstants.SQL_DATABASE = p.getProperty("SQL_Name");
        ServerConstants.SQL_INITIALSIZE = Integer.parseInt(p.getProperty("SQL_InitialSize"));
        ServerConstants.SQL_MINIDLE = Integer.parseInt(p.getProperty("SQL_MinIdle"));
        ServerConstants.SQL_MAXACTIVE = Integer.parseInt(p.getProperty("SQL_MaxActive"));
        ServerConstants.SQL_MAXWAIT = Integer.parseInt(p.getProperty("SQL_MaxWait"));
        ServerConstants.SQL_TIMEBETWEENEVICTIONRUNSMILLIS = Integer.parseInt(p.getProperty("SQL_TimeBetweenEvictionRunsMillis"));
        ServerConstants.SQL_MINEVICTABLEIDLETIMEMILLIS = Integer.parseInt(p.getProperty("SQL_MinEvictableIdleTimeMillis"));
        ServerConstants.SQL_VALIDATIONQUERY = p.getProperty("SQL_ValidationQuery");
        ServerConstants.SQL_TESTWHILEIDLE = Boolean.parseBoolean(p.getProperty("SQL_TestWhileIdle"));
        ServerConstants.SQL_TESTONBORROW = Boolean.parseBoolean(p.getProperty("SQL_TestOnBorrow"));
        ServerConstants.SQL_TESTONRETURN = Boolean.parseBoolean(p.getProperty("SQL_TestOnReturn"));
        ServerConstants.SQL_POOLPREPAREDSTATEMENTS = Boolean.parseBoolean(p.getProperty("SQL_PoolPreparedStatements"));
        ServerConstants.SQL_MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE = Integer.parseInt(p.getProperty("SQL_MaxPoolPreparedStatementPerConnectionSize"));
        ServerConstants.SQL_USEUNFAIRLOCK = Boolean.parseBoolean(p.getProperty("SQL_UseUnfairLock"));
    }

    public static void loadon_off() {
        if (ServerConfig.adminOnly || ServerConstants.Use_Localhost) {
            System.out.println("仅管理员登录模式已开启");
        }
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("执行中出现异常 - 无法连线到数据库." + ex);
        }
        PreparedStatement ps;
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            ps = con.prepareStatement("DELETE FROM `moonlightachievements` where achievementid > 0;");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("moonlightachievements" + ex);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
        }
        if (ServerConfig.logPackets) {
            System.out.println("数据包记录已开启.");
        }
        if (ServerConfig.USE_FIXED_IV) {
            System.out.println("反抓包已开启.");
        }
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }
}
