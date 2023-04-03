package constants;

import java.util.Calendar;
import tools.Triple;

public class ServerConstants {

    public static boolean autoban = true;
    public static String SQL_PORT = "";
    public static String SQL_DATABASE = "";
    public static String SQL_USER = "";
    public static String SQL_PASSWORD = "";
    public static int SQL_INITIALSIZE = 30;
    public static int SQL_MINIDLE = 20;
    public static int SQL_MAXACTIVE = 400;
    public static long SQL_MAXWAIT = 60000;
    public static long SQL_TIMEBETWEENEVICTIONRUNSMILLIS = 300000;
    public static long SQL_MINEVICTABLEIDLETIMEMILLIS = 300000;
    public static String SQL_VALIDATIONQUERY = "SELECT 'x'";
    public static boolean SQL_TESTWHILEIDLE = true;
    public static boolean SQL_TESTONBORROW = false;
    public static boolean SQL_TESTONRETURN = false;
    public static boolean SQL_POOLPREPAREDSTATEMENTS = true;
    public static int SQL_MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE = 3000;
    public static boolean SQL_USEUNFAIRLOCK = true;

    public static byte Class_Bonus_EXP(final int job) {
        switch (job) {
            case 501:
            case 530:
            case 531:
            case 532:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
            case 11212:
            case 800:
            case 900:
            case 910:
                return 10;
        }
        return 0;
    }

    public static boolean getEventTime() {
        int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        switch (Calendar.DAY_OF_WEEK) {
            case 1:
                return time >= 1 && time <= 5;
            case 2:
                return time >= 4 && time <= 9;
            case 3:
                return time >= 7 && time <= 12;
            case 4:
                return time >= 10 && time <= 15;
            case 5:
                return time >= 13 && time <= 18;
            case 6:
                return time >= 16 && time <= 21;
        }
        return time >= 19;
    }

    private static byte[] gateway(){
        String ip = ServerConfig.interface_;
        byte[] gatewayIp = new byte[4];
        String[] split = ip.split("\\.");
        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
            String str = split[i];
            gatewayIp[i] = (byte) Integer.parseInt(str);
        }
        return gatewayIp;
    }

    // GMS stuff
    public static boolean TESPIA = false;
    public static final String ASCII = "gbk";
    public static final short MAPLE_VERSION = (short) 72;
    public static final String MAPLE_PATCH = "1";
    public static final String MAIN_WORLD = "童话村";
    public static byte MAPLE_LOCAL = TESPIA ? (byte) 5 : (byte) 4;
    public static final byte[] Gateway_IP = gateway();

    // Server stuff
    public static final String SOURCE_REVISION = "2";
    public static final boolean BLOCK_CS = false;
    public static final boolean Old_Maps = false;
    public static final boolean Use_Localhost = false;
    public static final boolean Redirector = true;
    public static final boolean LOG_SHARK = false;
    public static boolean MultiLevel = false;
    public static final boolean AntiKS = false;
    public static final int miracleRate = 1;
    public static final byte SHOP_DISCOUNT = 0;
    public static boolean isBetaForAdmins = false;
    public static boolean 自动注册 = true;
    public static boolean LOG_DAMAGE = true;
    public static Triple<String, Integer, Boolean>[] backgrounds = new Triple[]{ //boolean for randomize
        new Triple<>("20140430/0", 1, false),
        new Triple<>("20140326/0", 0, false),
        new Triple<>("20140326/1", 0, false)
    };

    public static enum PlayerGMRank {

        NORMAL('@', 0),
        INTERN('!', 1),
        GM('!', 2),
        SUPERGM('!', 3),
        ADMIN('!', 4);
        private final char commandPrefix;
        private final int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public String getCommandPrefix() {
            return String.valueOf(commandPrefix);
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1);
        private final int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static boolean getAutoReg() {
        return 自动注册;
    }

    public static String ChangeAutoReg() {
        自动注册 = !getAutoReg();
        return 自动注册 ? "开启" : "关闭";
    }
}
