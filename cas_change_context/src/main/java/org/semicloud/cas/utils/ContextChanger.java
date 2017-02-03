package org.semicloud.cas.utils;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * 更改cas程序安装配置的小程序
 *
 * @author Semicloud
 * @version 1.0
 */
class ContextChanger {
    static CompositeConfiguration configuration;
    static String gisConfigFilePath = "E:\\CodeWorld\\IdeaJavaProjects\\cas\\cas_shared\\src\\main\\resources\\conf\\ini\\gis.ini";
    static String initConfigFilePath = "E:\\CodeWorld\\IdeaJavaProjects\\cas\\cas_shared\\src\\main\\resources\\conf\\xml\\init.xml";
    static String jdbcConfigFilePath = "E:\\CodeWorld\\IdeaJavaProjects\\utils\\utils_db\\src\\main\\resources\\c3p0-config.xml";

    static {
        try {
            HierarchicalINIConfiguration gisSetting = new HierarchicalINIConfiguration(gisConfigFilePath);
            XMLConfiguration initConfig = new XMLConfiguration(initConfigFilePath);
            XMLConfiguration jdbcConfig = new XMLConfiguration(jdbcConfigFilePath);
            jdbcConfig.setExpressionEngine(new XPathExpressionEngine());
            configuration = new CompositeConfiguration();
            configuration.addConfiguration(gisSetting);
            configuration.addConfiguration(initConfig);
            configuration.addConfiguration(jdbcConfig);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    private static String getString(String expr) {
        return configuration.getString(expr);
    }

    private static String getXPath(String xpath) {
        return configuration.getString("/default-config/property[@name='" + xpath + "']");
    }

    private static void displayConfig() {
        System.out.println("gis.ini文件：");
        System.out.println("\tdb.driver=" + getString("db.driver"));
        System.out.println("\tdb.server=" + getString("db.server"));
        System.out.println("\tdb.database=" + getString("db.database"));
        System.out.println("\tdb.user=" + getString("db.user"));
        System.out.println("\tdb.password=" + getString("db.password"));
        System.out.println("\tiny.server=" + getString("iny.server"));
        System.out.println("\tiny.user=" + getString("iny.user"));
        System.out.println("\tiny.password=" + getString("iny.password"));
        System.out.println("\tiny.exportpath=" + getString("iny.exportpath"));

        System.out.println("init.xml文件：");
        System.out.println("\tgis-server=" + getString("gis-server"));
        System.out.println("\tdb-dirs=" + getString("db-dirs"));
        System.out.println("\tdb-driver=" + getString("db-driver"));
        System.out.println("\tdb-conn=" + getString("db-conn"));
        System.out.println("\tdb-user=" + getString("db-user"));
        System.out.println("\tdb-password=" + getString("db-password"));

        System.out.println("c3p0-config.xml文件：");
        System.out.println("\tuser=" + getXPath("user"));
        System.out.println("\tpassword=" + getXPath("password"));
        System.out.println("\tjdbcUrl=" + getXPath("jdbcUrl"));
    }

    private static void saveGISIniFile(String key, String value) {
        HierarchicalINIConfiguration ini = (HierarchicalINIConfiguration) configuration.getConfiguration(0);
        ini.setProperty(key, value);
        try {
            ini.save(new File(gisConfigFilePath));
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    private static void saveInitXmlFile(String key, String value) {
        XMLConfiguration xml = (XMLConfiguration) configuration.getConfiguration(1);
        xml.setProperty(key, value);
        try {
            xml.save(new File(initConfigFilePath));
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveJdbcXmlFile(String key, String value) {
        XMLConfiguration jdbc = (XMLConfiguration) configuration.getConfiguration(2);
        jdbc.setProperty("/default-config/property[@name='" + key + "']", value);
        try {
            jdbc.save(new File(jdbcConfigFilePath));
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 开发机配置
     */
    private static void swithToDevelopEnvironment() {
        saveGISIniFile("db.server", "ORCL");
        saveGISIniFile("db.user", "MAPTEST");
        saveGISIniFile("db.password", "maptest");

        saveGISIniFile("iny.server", "ORCL");
        saveGISIniFile("iny.user", "inydata");
        saveGISIniFile("iny.password", "inydata");
        saveGISIniFile("iny.exportpath", "c://shapes");

        saveInitXmlFile("gis-server", "ORCL");
        saveInitXmlFile("db-dirs", "D:\\app\\Administrator\\oradata\\orcl\\#.DBF");
        saveInitXmlFile("db-conn", "jdbc:oracle:thin:@localhost:1521/ORCL");
        saveInitXmlFile("db-user", "b11");
        saveInitXmlFile("db-password", "b11");
        saveJdbcXmlFile("user", "b11");
        saveJdbcXmlFile("password", "b11");
        saveJdbcXmlFile("jdbcUrl", "jdbc:oracle:thin:@localhost:1521/orcl");
    }

    /**
     * 运行机配置
     */
    private static void swithToRunningEnvironment() {
        saveGISIniFile("db.server", "ORCL_10.9.103.17");
        saveGISIniFile("db.user", "mapdata1");
        saveGISIniFile("db.password", "mapdata1");

        saveGISIniFile("iny.server", "ORCL_10.9.103.17");
        saveGISIniFile("iny.user", "inydata");
        saveGISIniFile("iny.password", "inydata");
        saveGISIniFile("iny.exportpath", "D://deploy//apache-tomcat-7.0.53//webapps//caswebapp//shps");

        saveInitXmlFile("gis-server", "ORCL_10.9.103.17");
        saveInitXmlFile("db-dirs", "/u01/app/oracle/oradata/ORCL/#.dbf");
        saveInitXmlFile("db-conn", "jdbc:oracle:thin:@10.9.103.17:1521/ORCL");
        saveInitXmlFile("db-user", "b11");
        saveInitXmlFile("db-password", "b11");

        saveJdbcXmlFile("user", "b11");
        saveJdbcXmlFile("password", "b11");
        saveJdbcXmlFile("jdbcUrl", "jdbc:oracle:thin:@10.9.103.17:1521/orcl");
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        System.out.println("更改开发环境试用程序 ver1.0");
        System.out.println("当前开发环境如下：");
        displayConfig();
        System.out.println("请做出选择：1切换至本机开发环境，2切换至运行环境");
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        if (input == 1) {
            swithToDevelopEnvironment();
            System.out.println("配置已迁移至本机开发环境");
            displayConfig();
        } else {
            swithToRunningEnvironment();
            System.out.println("配置已迁移至运行环境");
            displayConfig();
        }
        System.out.println("配置修改完毕，按回车键关闭...");
        System.in.read();
        // System.out.println(configuration.getString("db.driver"))
        // System.out.println(configuration.getString("gis-server"))
        // System.out.println(configuration.getString("/default-config/property[@name='user']"))
        // HierarchicalINIConfiguration gisIni = (HierarchicalConfiguration)
        // configuration.getConfiguration(0);
        // XMLConfiguration jdbcXml = (XMLConfiguration)
        // configuration.getConfiguration(2);
        // jdbcXml.setProperty("/default-config/property[@name='user']", "miu~")
        // jdbcXml.save(new File(jdbcConfigFilePath))
        // gisIni.setProperty("db.database", "whatever")
        // gisIni.save(new File(gisConfigFilePath))
    }
}
