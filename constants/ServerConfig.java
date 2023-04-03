package constants;

public class ServerConfig {

    public static boolean checkCopyItem = true;
    public static boolean adminOnly = false;
    public static boolean logPackets = false;
    public static boolean AutoDisconnect = true;
    public static final double Activity_Bonus_Rate = 50 / 100.0D;
    public static final int flags = 3;
    public static final String serverName = "大白兔";
    public static String eventMessage = "MapleStory";
    public static final int flag = 3;
    public static int maxCharacters = 3;
    public static String serverMessage;
    public static int userLimit = 1500;
    public static String interface_;
    public static int channelCount = 3;
    public static int cashRate = 1;
    public static int traitRate = 1;
    public static int BossDropRate = 1;
    public static int ExpRate = 1;
    public static int MesoRate = 1;
    public static int DropRate = 1;
    public static int LoginPort = 5100;
    public static int ChannelPort = 2524;
    public static int DEFAULT_PORT = 2524;
    public static int CashPort = 5200;
    public static int ApStatLimit = 999;
    public static String events = ",";//""/* + "AutomatedEvent,"*/ + "Relic,HontalePQ,HorntailBattle,cpq2,elevator,Christmas,FireDemon,Amoria,cpq,AutomatedEvent,Flight,English,English0,English1,English2,WuGongPQ,ElementThanatos,4jberserk,4jrush,Trains,Geenie,AirPlane,OrbisPQ,HenesysPQ,Romeo,Juliet,Pirate,Ellin,DollHouse,BossBalrog_NORMAL,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,Vergamot,CoreBlaze,GuildQuest,Aufhaven,KyrinTrainingGroundC,KyrinTrainingGroundV,ProtectPig,ScarTarBattle,s4resurrection,s4resurrection2,s4nest,s4aWorld,DLPracticeField,ServerMessage,BossQuestEASY,BossQuestHARD,BossQuestHELL,BossQuestMed,shaoling,Ravana,MV,BossBalrog,QiajiPQ,Relic,Boats";
    /*Anti-Sniff*/
    public static boolean USE_FIXED_IV;
    public static final byte[] Static_LocalIV = new byte[]{71, 113, 26, 44};
    public static final byte[] Static_RemoteIV = new byte[]{70, 112, 25, 43};
    public static final int MAXIMUM_CONNECTIONS = 1000;
    public static boolean 普通注册 = false;
    public static boolean 官方式注册 = false;

    public static int LevelExp10 = 1;
    public static int LevelExp30 = 1;
    public static int LevelExp70 = 1;
    public static int LevelExp120 = 1;
    public static int LevelExp150 = 1;
}
