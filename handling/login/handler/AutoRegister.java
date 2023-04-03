package handling.login.handler;

import client.LoginCrypto;
import constants.ServerConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.FileoutputUtil;

public class AutoRegister {

    private static final int ACCOUNTS_PER_MAC = 1;
    public static boolean autoRegister = ServerConstants.getAutoReg();
    public static boolean success = false, mac = true;

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        try {
            Connection con = DatabaseConnection.getInstance().getDataSource().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
            rs.close();;
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
        }
        return accountExists;
    }

    public static void createAccount(String login, String pwd, String sockAddr, String macs) {
        Connection con;
        try {
            con = DatabaseConnection.getInstance().getDataSource().getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        try {
            ResultSet rs;
            PreparedStatement ipc = con.prepareStatement("SELECT macs FROM accounts WHERE macs = ?");
            ipc.setString(1, macs);
            rs = ipc.executeQuery();
            if (!rs.first() || rs.last() && rs.getRow() < ACCOUNTS_PER_MAC) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP,gender) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, login);
                ps.setString(2, LoginCrypto.hexSha1(pwd));
                ps.setString(3, "autoregister@mail.com");
                ps.setString(4, "2016-04-10");
                ps.setString(5, macs);
                ps.setString(6, "/" + sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                ps.setInt(7, 10);
                ps.executeUpdate();
                success = true;
            }
            //  
            //  ipc.close();
            if (rs.getRow() >= ACCOUNTS_PER_MAC) {
                mac = false;
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
        }
    }
}
