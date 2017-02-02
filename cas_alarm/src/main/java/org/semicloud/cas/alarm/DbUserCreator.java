package org.semicloud.cas.alarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 该类用于在得到强震消息并存储后，在数据源中创建表空间和用户，用于数据集的存储 \n 该类已不再使用，但可作为数据库中创建用户的测试类使用.
 */
@Deprecated
public class DbUserCreator {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(DbUserCreator.class);

    /**
     * 创建数据库用户
     *
     * @param eqID 数据库用户名，默认为eqID
     * @param dirs 表空间所在的文件目录
     */
    public static void createUser(String eqID, String dirs) {
        String sql1 = "CREATE TABLESPACE " + eqID + " LOGGING DATAFILE '" + dirs
                + "' SIZE 32M AUTOEXTEND ON NEXT 32M MAXSIZE 1024M EXTENT MANAGEMENT LOCAL";
        String sql2 = "CREATE USER " + eqID + " IDENTIFIED BY \"" + eqID + "\" DEFAULT TABLESPACE " + eqID
                + " PROFILE DEFAULT";
        String sql3 = "GRANT CONNECT,RESOURCE TO " + eqID;

        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/ORCL", "system", "system");
            Statement statement = conn.createStatement();
            statement.execute(sql1);
            statement.execute(sql2);
            statement.execute(sql3);
            log.info("数据库用户" + eqID + "创建成功！");
        } catch (Exception ex) {
            log.info("数据库用户" + eqID + "失败：" + ex.getLocalizedMessage());
        }
    }

    // public static void main(String[] args) {
    // createUser("N39020E11566020150506163011",
    // "D:\\APP\\SEMICLOUD\\ORADATA\\ORCL\\N39020E11566020150506163011.DBF");
    // }
}
