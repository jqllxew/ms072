package org.example.ms072.tools.wztosql;

import  org.example.ms072.constants.ServerConstants;
import  org.example.ms072.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.ms072.tools.FileoutputUtil;

public class FixCharSets {

    public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getInstance().getDataSource().getConnection()) {
            try (ResultSet rs = con.prepareStatement("SELECT CONCAT('ALTER TABLE `', tbl.`TABLE_SCHEMA`, '`.`', tbl.`TABLE_NAME`, '` CONVERT TO CHARACTER SET gbk COLLATE gbk_chinese_ci;') FROM `information_schema`.`TABLES` tbl WHERE tbl.`TABLE_SCHEMA` = '" + ServerConstants.SQL_DATABASE + "'").executeQuery()) {
                PreparedStatement ps;
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                    try {
                        ps = con.prepareStatement(rs.getString(1));
                        ps.execute();
                        ps.close();
                    } catch (SQLException ex) {
                        System.err.println("FixCharSets" + ex);
                        FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("FixCharSets" + ex);
            FileoutputUtil.outputFileError("logs/数据库异常.txt", ex);
        }
    }
}
