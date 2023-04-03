package database;

import com.alibaba.druid.pool.DruidDataSource;
import constants.ServerConstants;

public class DatabaseConnection {

    private static DruidDataSource dataSource = null;
    public static final int RETURN_GENERATED_KEYS = 1;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("[数据库信息] 找不到JDBC驱动.");
            System.exit(0);
        }

    }

    private static class InstanceHolder {

        public static final DatabaseConnection instance = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return InstanceHolder.instance;
    }

    private DatabaseConnection() {
    }

    public DruidDataSource getDataSource() {
        if (dataSource == null) {
            InitDBConPool();
        }
        return dataSource;
    }

    private void InitDBConPool() {
        dataSource = new DruidDataSource();
        dataSource.setName("mysql_pool");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + "127.0.0.1" + ":" + ServerConstants.SQL_PORT + "/" + ServerConstants.SQL_DATABASE + "?" +
                "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowMultiQueries=true&zeroDateTimeBehavior=CONVERT_TO_NULL");
        dataSource.setUsername(ServerConstants.SQL_USER);//传递给JDBC驱动的用于建立连接的用户名
        dataSource.setPassword(ServerConstants.SQL_PASSWORD);
        /*dataSource.setInitialSize(30);
        dataSource.setMinIdle(50);
        dataSource.setMaxActive(400);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setMaxWait(60000);
        dataSource.setUseUnfairLock(true);*/
        dataSource.setInitialSize(ServerConstants.SQL_INITIALSIZE);
        dataSource.setMinIdle(ServerConstants.SQL_MINIDLE);
        dataSource.setMaxActive(ServerConstants.SQL_MAXACTIVE);
        dataSource.setMaxWait(ServerConstants.SQL_MAXWAIT);
        dataSource.setTimeBetweenEvictionRunsMillis(ServerConstants.SQL_TIMEBETWEENEVICTIONRUNSMILLIS);
        dataSource.setMinEvictableIdleTimeMillis(ServerConstants.SQL_MINEVICTABLEIDLETIMEMILLIS);
        dataSource.setValidationQuery(ServerConstants.SQL_VALIDATIONQUERY);//SELECT 'x'
        dataSource.setTestWhileIdle(ServerConstants.SQL_TESTWHILEIDLE);
        dataSource.setTestOnBorrow(ServerConstants.SQL_TESTONBORROW);
        dataSource.setTestOnReturn(ServerConstants.SQL_TESTONRETURN);
        dataSource.setPoolPreparedStatements(ServerConstants.SQL_POOLPREPAREDSTATEMENTS);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(ServerConstants.SQL_MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE);
        dataSource.setUseUnfairLock(ServerConstants.SQL_USEUNFAIRLOCK);


    }

}
