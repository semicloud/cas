package org.semicloud.cas.scheduler.timepoint;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Workspace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.cfg.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 初始化操作，主要负责数据库用户的创建，GIS数据源的创建.
 *
 * @author Semicloud
 */
public class Initilizer {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(Initilizer.class);

    /**
     * 模型计算前的初始化操作，创建数据库用户、创建表空间
     *
     * @param eqID the eq id eqID
     */
    public static void init(String eqID) {
        createDbUser(eqID);
        createGISDatasource(eqID);
    }

    // public static void main(String[] args) {
    // NCLOB nclob = (NCLOB) DaoFactory.getInstance().queryScalar(
    // "select edit_result from basic_recompute_event where task_id=?",
    // "N34500E10420020150507125314_20150507130506");
    // System.out.println(OracleTypeConverter.clobToString(nclob));
    // JSONObject jsonObject =
    // JSONObject.fromObject(OracleTypeConverter.clobToString(nclob));
    // JSONArray jsonArray = jsonObject.getJSONArray("circles");
    // System.out.println(jsonArray == null);
    // }

    /**
     * 创建GIS数据源.
     *
     * @param eqID eqID
     */
    private static void createGISDatasource(String eqID) {
        try {
            Workspace workspace = new Workspace();
            DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
            datasourceConnectionInfo.setEngineType(EngineType.ORACLEPLUS);

            datasourceConnectionInfo.setServer(Settings.getGISInstanceNameForInit());
            datasourceConnectionInfo.setDatabase("");
            datasourceConnectionInfo.setUser(eqID);
            datasourceConnectionInfo.setPassword(eqID);
            datasourceConnectionInfo.setAlias(eqID);
            Datasource datasource = workspace.getDatasources().create(datasourceConnectionInfo);
            if (datasource != null) {
                log.info("数据源" + eqID + "创建成功！");

                log.info("Server:" + Settings.getGISInstanceNameForInit());
                log.info("User:" + eqID);
                log.info("Password:" + eqID);
                log.info("Alias:" + eqID);
            } else {
                log.error("GIS数据源创建失败！");
            }
        } catch (Exception ex) {
            log.error("为事件" + eqID + "创建GIS数据源失败！");
        }
    }

    /**
     * 创建数据库用户，并赋予权限.
     *
     * @param eqID the eq id
     */
    private static void createDbUser(String eqID) {
        String dirs = Settings.getDbDirsForInit();
        if (dirs.contains("#"))
            dirs = dirs.replaceAll("[#]", eqID);
        String sql1 = "CREATE TABLESPACE " + eqID + " LOGGING DATAFILE '" + dirs
                + "' SIZE 32M AUTOEXTEND ON NEXT 32M MAXSIZE 1024M EXTENT MANAGEMENT LOCAL";
        String sql2 = "CREATE USER " + eqID + " IDENTIFIED BY \"" + eqID + "\" DEFAULT TABLESPACE " + eqID
                + " PROFILE DEFAULT";
        String sql3 = "GRANT CONNECT,RESOURCE TO " + eqID;
        log.info("sql1:" + sql1);
        log.info("sql2:" + sql2);
        log.info("sql3:" + sql3);
        Connection conn = null;
        try {
            Class.forName(Settings.getDbDriverForInit());
            conn = DriverManager.getConnection(Settings.getDbConnForInit(), Settings.getDbUserForInit(),
                    Settings.getDbPasswordForInit());
            Statement statement = conn.createStatement();
            statement.execute(sql1);
            statement.execute(sql2);
            statement.execute(sql3);
            log.info("数据库用户" + eqID + "创建成功！");
        } catch (Exception ex) {
            log.error("数据库用户" + eqID + "失败：" + ex.getLocalizedMessage());
        }
    }
}
