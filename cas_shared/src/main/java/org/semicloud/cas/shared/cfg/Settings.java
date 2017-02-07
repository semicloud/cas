package org.semicloud.cas.shared.cfg;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.semicloud.cas.shared.intensity.oval2.LineParams;
import org.semicloud.cas.shared.intensity.oval2.OvalParams;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.gis.EngineSettings;

import java.text.MessageFormat;
import java.util.*;

import static org.semicloud.utils.common.MyStringUtils.evaluate;

/**
 * 配置类，综合研判系统所有的配置项都在这个类中 使用了Commons-Configuration库实现 <br/>
 * 加载了以下配置文件： <br/>
 * conf/ini/dsv.ini <br/>
 * conf/ini/arm.ini <br/>
 * conf/ini/gis.ini <br/>
 * conf/ini/tsk.ini <br/>
 * conf/ini/drw.ini <br/>
 * conf/ini/mod.ini <br/>
 * conf/xml/model.xml <br/>
 * conf/xml/task.xml <br/>
 * conf/xml/init.xml <br/>
 * conf/xml/intensityParams.xml <br/>
 */
public class Settings {

    /**
     * The _all configurations.
     */
    private static CompositeConfiguration _allConfigurations;

    static {
        try {
            HierarchicalINIConfiguration dsvSetting = new HierarchicalINIConfiguration("conf/ini/dsv.ini");
            HierarchicalINIConfiguration armSetting = new HierarchicalINIConfiguration("conf/ini/arm.ini");
            HierarchicalINIConfiguration gisSetting = new HierarchicalINIConfiguration("conf/ini/gis.ini");
            HierarchicalINIConfiguration isrSetting = new HierarchicalINIConfiguration("conf/ini/drw.ini");
            HierarchicalINIConfiguration tskSetting = new HierarchicalINIConfiguration("conf/ini/tsk.ini");
            HierarchicalINIConfiguration modSetting = new HierarchicalINIConfiguration("conf/ini/mod.ini");

            // !! Be Careful
            XMLConfiguration modelConfig = new XMLConfiguration("conf/xml/model.xml");
            modelConfig.setExpressionEngine(new XPathExpressionEngine());
            XMLConfiguration taskConfig = new XMLConfiguration("conf/xml/task.xml");
            taskConfig.setExpressionEngine(new XPathExpressionEngine());
            XMLConfiguration initConfig = new XMLConfiguration("conf/xml/init.xml");
            XMLConfiguration intensityParameterConfig = new XMLConfiguration("conf/xml/intensityParams.xml");
            intensityParameterConfig.setExpressionEngine(new XPathExpressionEngine());

            _allConfigurations = new CompositeConfiguration();
            _allConfigurations.addConfiguration(dsvSetting);
            _allConfigurations.addConfiguration(armSetting);
            _allConfigurations.addConfiguration(gisSetting);
            _allConfigurations.addConfiguration(isrSetting);
            _allConfigurations.addConfiguration(tskSetting);
            _allConfigurations.addConfiguration(modSetting);
            _allConfigurations.addConfiguration(modelConfig);
            _allConfigurations.addConfiguration(taskConfig);
            _allConfigurations.addConfiguration(initConfig);
            _allConfigurations.addConfiguration(intensityParameterConfig);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取导出影响场时，利用其进行投影的数据集名称 (导出数据集时使用的投影与程序里使用的投影不一样)
     *
     * @return
     */
    public static String getExPrjStdDatasetName() {
        return _allConfigurations.getString("map.exprj");
    }

    /**
     * 烈度场数据的用户名，配置在 gis.ini中
     *
     * @return
     */
    public static String getInyDatabaseUserName() {
        return _allConfigurations.getString("iny.user");
    }

    /**
     * 烈度场数据的密码，配置在gis.ini中
     *
     * @return
     */
    public static String getInyDatabasePassword() {
        return _allConfigurations.getString("iny.password");
    }

    /**
     * 烈度场数据的导出路径，配置在gis.ini中
     *
     * @return
     */
    public static String getShpFileExportedPath() {
        return _allConfigurations.getString("iny.exportpath");
    }

    public static double getNationalMagnitudeThreshold() {
        return _allConfigurations.getDouble("default.national_magnitude_threshold");
    }

    public static double getInternationalMagnitudeThreshold() {
        return _allConfigurations.getDouble("default.international_magnitude_threshold");
    }

    public static double getOceanMagnitudeThreshold() {
        return _allConfigurations.getDouble("default.ocean_magnitude_threshold");
    }

    /**
     * 获取线源参数
     *
     * @return
     */
    public static LineParams getLineParams() {
        String path = "/lines/{0}/{1}";
        LineParams params = new LineParams();
        params.setLia(_allConfigurations.getDouble(MessageFormat.format(path, "li", "pa")));
        params.setLib(_allConfigurations.getDouble(MessageFormat.format(path, "li", "pb")));
        params.setLic(_allConfigurations.getDouble(MessageFormat.format(path, "li", "pc")));
        params.setRa(_allConfigurations.getDouble(MessageFormat.format(path, "r", "pa")));
        params.setRb(_allConfigurations.getDouble(MessageFormat.format(path, "r", "pb")));
        params.setRc(_allConfigurations.getDouble(MessageFormat.format(path, "r", "pc")));
        params.setRr(_allConfigurations.getDouble(MessageFormat.format(path, "r", "pr")));
        path = "/lines/base";
        String str = _allConfigurations.getString(path);
        params.setBase(str.equals("10") ? 10 : Math.E);
        return params;
    }

    /**
     * 获取中国西部地区的线源模型参数，共 7 个数，详见 IntensityParams.xml
     *
     * @return
     */
    public static List<Double> getWestChinaLineCircleParams() {
        Double[] parameters = new Double[7];
        parameters[0] = _allConfigurations.getDouble("/line-circle-params/region-west/l-parameter/la");
        parameters[1] = _allConfigurations.getDouble("/line-circle-params/region-west/l-parameter/lb");
        parameters[2] = _allConfigurations.getDouble("/line-circle-params/region-west/r-parameter/pa");
        parameters[3] = _allConfigurations.getDouble("/line-circle-params/region-west/r-parameter/pb");
        parameters[4] = _allConfigurations.getDouble("/line-circle-params/region-west/r-parameter/pc");
        parameters[5] = _allConfigurations.getDouble("/line-circle-params/region-west/r-parameter/pr");
        parameters[6] = _allConfigurations.getDouble("/line-circle-params/region-west/base");
        return Arrays.asList(parameters);
    }

    /**
     * 获取中国东部地区的线源模型参数，共 7 个数，详见 IntensityParams.xml
     *
     * @return
     */
    public static List<Double> getEastChinaLineCircleParams() {
        Double[] parameters = new Double[7];
        parameters[0] = _allConfigurations.getDouble("/line-circle-params/region-east/l-parameter/la");
        parameters[1] = _allConfigurations.getDouble("/line-circle-params/region-east/l-parameter/lb");
        parameters[2] = _allConfigurations.getDouble("/line-circle-params/region-east/r-parameter/pa");
        parameters[3] = _allConfigurations.getDouble("/line-circle-params/region-east/r-parameter/pb");
        parameters[4] = _allConfigurations.getDouble("/line-circle-params/region-east/r-parameter/pc");
        parameters[5] = _allConfigurations.getDouble("/line-circle-params/region-east/r-parameter/pr");
        parameters[6] = _allConfigurations.getDouble("/line-circle-params/region-east/base");
        return Arrays.asList(parameters);
    }

    /**
     * 通过地区名称获取烈度参数
     *
     * @param regionName
     * @return
     */
    public static OvalParams getOvalParams(String regionName) {
        String ls = "long-axis", ss = "short-axis";
        String path = "/regions/region[@name=\"{0}\"]/{1}/{2}";
        OvalParams params = new OvalParams();
        params.setLa(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ls, "pa")));
        params.setLb(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ls, "pb")));
        params.setLc(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ls, "pc")));
        params.setLr(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ls, "pr")));

        params.setSa(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ss, "pa")));
        params.setSb(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ss, "pb")));
        params.setSc(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ss, "pc")));
        params.setSr(_allConfigurations.getDouble(MessageFormat.format(path, regionName, ss, "pr")));

        path = "/regions/region[@name=\"{0}\"]/{1}";
        String str = _allConfigurations.getString(MessageFormat.format(path, regionName, "base"));
        params.setBase(str.equals("10") ? 10 : Math.E);
        return params;
    }

    /**
     * 获取国内所有烈度模型的适用地区
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<String> getModelRegions() {
        return (List<String>) _allConfigurations.getList("/regions//region/@name");
    }

    /**
     * 获取地区-模型的映射的省的集合
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<String> getMappingProvinces() {
        return (List<String>) _allConfigurations.getList("/province-region-mappings/mapping/@province");
    }

    /**
     * 通过省名查询适用的烈度模型的地区
     * 注意，如果查询到不存在的省，该函数返回null
     *
     * @param provinceName
     * @return
     */
    public static String getModelRegionByProvince(String provinceName) {
        String xpath = "/province-region-mappings/mapping[@province=\"{0}\"]/@region";
        xpath = MessageFormat.format(xpath, provinceName);
        return _allConfigurations.getString(xpath);
    }

    /**
     * 获取烈度参数 region：地区名称，ls：长短轴(l表示长轴，s表示短轴)，pname：参数名称
     * getIntensityParameter("华东地区","l", "a") - 取华东地区长轴的a参数
     *
     * @param region
     * @param ls
     * @param pname
     * @return
     */
    @Deprecated
    public static double getIntensityParameter(String region, String ls, String pname) {
        String axis = ls.equals("l") ? "long-axis" : "short-axis";
        String xpath = "/regions/region[@name=\"" + region + "\"]/" + axis + "/" + pname;
        return _allConfigurations.getDouble(xpath);
    }

    /**
     * main函数，用于测试读取配置是否成功
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        System.out.println("中国西部地区线源模型参数：" + StringUtils.join(getWestChinaLineCircleParams(), ","));
        System.out.println("中国东部地区线源模型参数：" + StringUtils.join(getEastChinaLineCircleParams(), ","));

        System.out.println(getInyDatabasePassword());
        System.out.println(getInyDatabaseUserName());
        System.out.println(getIntensityParameter("四川地区", "l", "pa"));
        System.out.println(getNationalMagnitudeThreshold());
        System.out.println(getInternationalMagnitudeThreshold());
        System.out.println(getOceanMagnitudeThreshold());
        System.out.println(getRegionCount());
        System.out.println(getModelDisplay("地震烈度模型"));
        System.out.println(getClientProperties());
        System.out.println(StringUtils.center("model cfg", 80, "-"));
        System.out.println(getModelProgram("地震烈度模型"));
        System.out.println(getModelTable("地震烈度模型"));
        System.out.println(getModelPicCode("地震烈度模型"));
        System.out.println(getModelPicCode("aaa") == StringUtils.EMPTY);
        System.out.println(StringUtils.repeat("-", 80) + "\n");

        System.out.println(StringUtils.center("alarm setting", 80, "-"));
        System.out.println(getServerAlarmPath());
        System.out.println(getServerAlarmPort());
        System.out.println(getAlarmClient("10.9.100.7"));
        System.out.println(getAlarmClient("aaa") == StringUtils.EMPTY);
        System.out.println(getClientProperties());
        System.out.println(StringUtils.repeat("-", 80) + "\n");

        System.out.println(StringUtils.center("timepoint setting", 80, "-"));
        System.out.println(getTimePointImme());
        System.out.println(getTimePointMin30());
        System.out.println(getTimePointHour1());
        System.out.println(getTimePointHour3());
        System.out.println(getTimePointHour6());
        System.out.println(getTimePointHour10());
        System.out.println(getTimePointHour14());
        System.out.println(StringUtils.repeat("-", 80) + "\n");

        System.out.println(getGisSettings());

        startnew("dsv setting");
        print(getDatasetNameActiveFault());
        print(getDatasetNameModelRange());
        print(getDatasetNameProvince());
        print(getDatasetNameCountry());
        print(getDatasetNameAirport());
        print(getDatasetNameCountyPoint());
        print(getDatasetNameCountryCultureReligion());
        print(getDatasetNameProvinceCultureReligion());
        print(getDatasetNameHistory());
        print(getDatasetNameCountryLandform());
        print(getDatasetNameProvinceLandform());
        print(getDatasetNameCounty());
        print(getDatasetNameCountryEconomic());
        print(getDatasetNameProvinceEconomic());
        printArray(getAttributesActiveFault());
        printArray(getAttributesCountry());
        printArray(getAttributesProvince());
        printArray(getAttributesAirport());
        printArray(getAttributesHistory());
        printArray(getAttributesCountyPoint());
        printArray(getAttributesCountryLandform());
        printArray(getAttributesProvinceLandform());
        printArray(getAttributesCountryEconomic());
        printArray(getAttributesProvinceEconomic());
        printArray(getAttributesCountryCultureReligion());
        printArray(getAttributesProvinceCultureReligion());
        end();

        startnew("draw server setting");
        print(getDrawAddress());
        print(getDrawPort());
        print(getDrawPath());
        end();

        startnew("model settings");
        print(getModelStartIntensity());
        print(getModelDefaultAzimuth());
        print(getModelAirportSearchRadius());
        print(getModelCountrySearchRadius());
        print(getModelHistorySearchRadius());
        print(getModelFaultSearchRadius());
        print(getModelPagerReportUrl());
        print(getMeiziAreKeyIntensity());
        print(getCasulatyKeyIntensity());
        print(getEconomicKeyIntensity());
        end();

        printArray(getModelNames("re-calc").toArray(new String[getModelNames("re-calc").size()]));

        String[] faults = getAttributesActiveFault();
        for (String str : faults) {
            System.out.println(str);
        }

        List<String> models = getModelNames("min_30");
        for (String str : models) {
            System.out.println(str);
        }

        System.out.println(getGISInstanceNameForInit());
    }

    /**
     * 打印一个Object
     *
     * @param obj the obj
     */
    private static void print(Object obj) {
        System.out.println(obj);
    }

    /**
     * 打印一个String 数组
     *
     * @param strs the strs
     */
    private static void printArray(String[] strs) {
        System.out.println(Arrays.toString(strs));
    }

    /**
     * 打印美化
     *
     * @param title the title
     */
    private static void startnew(String title) {
        System.out.println(StringUtils.center(title, 80, "-"));
    }

    /**
     * 打印美化
     */
    private static void end() {
        System.out.println(StringUtils.repeat("-", 80) + "\n");
    }

    // 初始化配置

    /**
     * 获取初始化操作的GIS-Server
     *
     * @return the GIS instance name for init
     */
    public static String getGISInstanceNameForInit() {
        return _allConfigurations.getString("gis-server");
    }

    /**
     * 获取初始化操作创建表空间的路径
     *
     * @return the db dirs for init
     */
    public static String getDbDirsForInit() {
        return _allConfigurations.getString("db-dirs");
    }

    /**
     * 获取初始化操作的数据库驱动
     *
     * @return the db driver for init
     */
    public static String getDbDriverForInit() {
        return _allConfigurations.getString("db-driver");
    }

    /**
     * 获取初始化操作的数据连接字符串
     *
     * @return the db conn for init
     */
    public static String getDbConnForInit() {
        return _allConfigurations.getString("db-conn");
    }

    /**
     * 获取初始化操作的用户名，该用户应具有创建用户、表空间以及给用户授权的权限
     *
     * @return the db user for init
     */
    public static String getDbUserForInit() {
        return _allConfigurations.getString("db-user");
    }

    /**
     * 获取初始化操作的用户名密码
     *
     * @return the db password for init
     */
    public static String getDbPasswordForInit() {
        return _allConfigurations.getString("db-password");
    }

    /**
     * 获取重计算时烈度圈转换为面对象时的逼近点数
     *
     * @return the region count
     */
    public static int getRegionCount() {
        return _allConfigurations.getInt("recompute.region");
    }

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月14日, 下午12:49:52
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 任务配置，即在时间点调用哪些模型的配置
    // ***********************************************************

    /**
     * 根据时间点查询要调用的模型名称列表
     *
     * @param timepoint the timepoint
     * @return the model names
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<String> getModelNames(String timepoint) {
        List list = _allConfigurations.getList("/" + timepoint + "/model");
        return Lists.transform(list, Functions.toStringFunction());
    }

    // ***********************************************************
    // End of 任务配置，即在时间点调用哪些模型的配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月13日, 下午10:24:58
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 数据集名称和属性配置
    // ***********************************************************

    /**
     * 获取断裂带数据集名称
     *
     * @return the dataset name active fault
     */
    public static String getDatasetNameActiveFault() {
        return getString("dsv.fault");
    }

    /**
     * 获取Model_Range数据集名称
     *
     * @return the dataset name model range
     */
    public static String getDatasetNameModelRange() {
        return getString("dsv.range");
    }

    /**
     * 获取省份数据集名称
     *
     * @return the dataset name province
     */
    public static String getDatasetNameProvince() {
        return getString("dsv.province");
    }

    /**
     * 获取地级市数据集名称
     *
     * @return the dataset name prefecture
     */
    public static String getDatasetNamePrefecture() {
        return getString("dsv.prefecture");
    }

    /**
     * 获取国家数据集名称
     *
     * @return the dataset name country
     */
    public static String getDatasetNameCountry() {
        return getString("dsv.country");
    }

    /**
     * 获取机场数据集名称
     *
     * @return the dataset name airport
     */
    public static String getDatasetNameAirport() {
        return getString("dsv.airport");
    }

    /**
     * 获取县级市（点数据）数据集名称
     *
     * @return the dataset name county point
     */
    public static String getDatasetNameCountyPoint() {
        return getString("dsv.county-point");
    }

    /**
     * 获取国家级文化宗教数据集名称
     *
     * @return the dataset name country culture religion
     */
    public static String getDatasetNameCountryCultureReligion() {
        return getString("dsv.country-culture-religion");
    }

    /**
     * 获取省市级文化宗教数据集名称
     *
     * @return the dataset name province culture religion
     */
    public static String getDatasetNameProvinceCultureReligion() {
        return getString("dsv.province-culture-religion");
    }

    /**
     * 获取历史地震分布数据集名称
     *
     * @return the dataset name history
     */
    public static String getDatasetNameHistory() {
        return getString("dsv.history");
    }

    /**
     * 获取国家级地形地貌数据集名称
     *
     * @return the dataset name country landform
     */
    public static String getDatasetNameCountryLandform() {
        return getString("dsv.country-landform");
    }

    /**
     * 获取省市级地形地貌数据集名称
     *
     * @return the dataset name province landform
     */
    public static String getDatasetNameProvinceLandform() {
        return getString("dsv.province-landform");
    }

    /**
     * 获取县级市数据集名称
     *
     * @return the dataset name county
     */
    public static String getDatasetNameCounty() {
        return getString("dsv.county");
    }

    /**
     * 获取国家级经济情况数据集名称
     *
     * @return the dataset name country economic
     */
    public static String getDatasetNameCountryEconomic() {
        return getString("dsv.country-economic");
    }

    /**
     * 获取省市级级经济情况数据集名称
     *
     * @return the dataset name province economic
     */
    public static String getDatasetNameProvinceEconomic() {
        return getString("dsv.province-economic");
    }

    /**
     * 获取popgrid数据集名称，该数据集用于确定调用那个人口栅格数据集
     *
     * @return the dataset name pop grid
     */
    public static String getDatasetNamePopGrid() {
        return getString("dsv.pop");
    }

    /**
     * 断裂带数据集属性
     *
     * @return the attributes active fault
     */
    public static String[] getAttributesActiveFault() {
        return getStringArray("attrs.fault");
    }

    /**
     * 国家级行政区属性
     *
     * @return the attributes country
     */
    public static String[] getAttributesCountry() {
        return getStringArray("attrs.country");
    }

    /**
     * 省级行政区属性
     *
     * @return the attributes province
     */
    public static String[] getAttributesProvince() {
        return getStringArray("attrs.province");
    }

    /**
     * 机场属性
     *
     * @return the attributes airport
     */
    public static String[] getAttributesAirport() {
        return getStringArray("attrs.airport");
    }

    /**
     * 历史地震属性
     *
     * @return the attributes history
     */
    public static String[] getAttributesHistory() {
        return getStringArray("attrs.history");
    }

    /**
     * 县级市（点属性）属性
     *
     * @return the attributes county point
     */
    public static String[] getAttributesCountyPoint() {
        return getStringArray("attrs.county-point");
    }

    /**
     * 地级市属性
     *
     * @return the attributes prefecture
     */
    public static String[] getAttributesPrefecture() {
        return getStringArray("attrs.prefecture");
    }

    /**
     * 国家级地形地貌属性
     *
     * @return the attributes country landform
     */
    public static String[] getAttributesCountryLandform() {
        return getStringArray("attrs.country-landform");
    }

    //

    /**
     * 省市级地形地貌属性
     *
     * @return the attributes province landform
     */
    public static String[] getAttributesProvinceLandform() {
        return getStringArray("attrs.province-landform");
    }

    /**
     * 国家级经济状况属性
     *
     * @return the attributes country economic
     */
    public static String[] getAttributesCountryEconomic() {
        return getStringArray("attrs.country-economic");
    }

    /**
     * 省市级经济情况属性
     *
     * @return the attributes province economic
     */
    public static String[] getAttributesProvinceEconomic() {
        return getStringArray("attrs.province-economic");
    }

    /**
     * 国家级文化宗教属性
     *
     * @return the attributes country culture religion
     */
    public static String[] getAttributesCountryCultureReligion() {
        return getStringArray("attrs.country-culture-religion");
    }

    /**
     * 省市级文化宗教属性
     *
     * @return the attributes province culture religion
     */
    public static String[] getAttributesProvinceCultureReligion() {
        return getStringArray("attrs.province-culture-religion");
    }

    // ***********************************************************
    // End of 数据集名称和属性配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月13日, 下午10:06:40
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start GIS数据源配置
    // ***********************************************************

    /**
     * 获取GIS服务器设置
     *
     * @return the gis settings
     */
    public static EngineSettings getGisSettings() {
        EngineSettings settings = new EngineSettings();
        settings.setDbDriverName(getString("db.driver"));
        settings.setDbServerName(getString("db.server"));
        settings.setDbDatabaseName(getString("db.database"));
        settings.setDbUserName(getString("db.user"));
        settings.setDbUserPassword(getString("db.password"));
        settings.setMapUnit(_allConfigurations.getFloat("map.unit"));
        settings.setProjDatasetName(_allConfigurations.getString("map.prj"));
        return settings;
    }

    // ***********************************************************
    // End of GIS数据源 配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月13日, 下午9:58:58
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 出图配置
    // ***********************************************************

    /**
     * 出图模块地址
     *
     * @return the draw address
     */
    @Deprecated
    public static String getDrawAddress() {
        return getString("draw.address");
    }

    /**
     * 出图模块端口
     *
     * @return the draw port
     */
    @Deprecated
    public static int getDrawPort() {
        return _allConfigurations.getInt("draw.port");
    }

    /**
     * 出图模块触发路径
     *
     * @return the draw path
     */
    @Deprecated
    public static String getDrawPath() {
        return getString("draw.path");
    }

    // ***********************************************************
    // End of 出图配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月14日, 上午11:26:41
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 模型默认值配置
    // ***********************************************************

    /**
     * 获取国家查询范围，该值一般用作震中的计算范围
     *
     * @return the model country search radius
     */
    public static float getModelCountrySearchRadius() {
        return _allConfigurations.getFloat("rad.country");
    }

    /**
     * 获取历史地震模型的计算范围
     *
     * @return the model history search radius
     */
    public static float getModelHistorySearchRadius() {
        return _allConfigurations.getFloat("rad.history");
    }

    /**
     * 获得断层默认搜索半径
     *
     * @return the model fault search radius
     */
    public static float getModelFaultSearchRadius() {
        return _allConfigurations.getFloat("rad.fault");
    }

    /**
     * 获取机场搜索半径
     *
     * @return the model airport search radius
     */
    public static float getModelAirportSearchRadius() {
        return _allConfigurations.getFloat("rad.airport");
    }

    /**
     * 获得起始计算烈度
     *
     * @return the model start intensity
     */
    public static float getModelStartIntensity() {
        return _allConfigurations.getFloat("default.start");
    }

    /**
     * 获得烈度圈默认偏转角
     *
     * @return the model default azimuth
     */
    public static float getModelDefaultAzimuth() {
        return _allConfigurations.getFloat("default.azimuth");
    }

    /**
     * 获取极震区核心范围
     *
     * @return the meizi are key intensity
     */
    public static float getMeiziAreKeyIntensity() {
        return _allConfigurations.getFloat("key.area");
    }

    /**
     * 获得人口死亡模型中的计算范围
     *
     * @return the casulaty key intensity
     */
    public static float getCasulatyKeyIntensity() {
        return _allConfigurations.getFloat("key.casualty");
    }

    /**
     * 获得经济损失模型中的计算范围
     *
     * @return the economic key intensity
     */
    public static float getEconomicKeyIntensity() {
        return _allConfigurations.getFloat("key.economic");
    }

    /**
     * 获取USGS PAGER 报告的下载地址
     *
     * @return the model pager report url
     */
    public static String getModelPagerReportUrl() {
        return _allConfigurations.getString("url.pager");
    }

    // ***********************************************************
    // End of 模型默认值配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月14日, 上午11:04:14
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 时间点配置
    // ***********************************************************

    /**
     * 获取速判的触发时间
     *
     * @return the time point imme
     */
    public static int getTimePointImme() {
        return Convert.toInteger(evaluate(getString("timepoint.imme")));
    }

    /**
     * 获取震后30分钟研判的触发时间
     *
     * @return the time point min30
     */
    public static int getTimePointMin30() {
        return Convert.toInteger(evaluate(getString("timepoint.min30")));
    }

    /**
     * 获取震后1小时研判的触发时间
     *
     * @return the time point hour1
     */
    public static int getTimePointHour1() {
        return Convert.toInteger(evaluate(getString("timepoint.hour1")));
    }

    /**
     * 获取震后3小时研判的触发时间
     *
     * @return the time point hour3
     */
    public static int getTimePointHour3() {
        return Convert.toInteger(evaluate(getString("timepoint.hour3")));
    }

    /**
     * 获取震后6小时研判的触发时间
     *
     * @return the time point hour6
     */
    public static int getTimePointHour6() {
        return Convert.toInteger(evaluate(getString("timepoint.hour6")));
    }

    /**
     * 获取震后10小时研判的触发时间
     *
     * @return the time point hour10
     */
    public static int getTimePointHour10() {
        return Convert.toInteger(evaluate(getString("timepoint.hour10")));
    }

    /**
     * 获取震后14小时研判的触发时间
     *
     * @return the time point hour14
     */
    public static int getTimePointHour14() {
        return Convert.toInteger(evaluate(getString("timepoint.hour14")));
    }

    /**
     * 获取扫描地震事件的时间间隔
     *
     * @return the time point scheduler scan interval
     */
    public static int getTimePointSchedulerScanInterval() {
        return Convert.toInteger(evaluate(getString("timepoint.interval")));
    }

    // ***********************************************************
    // End of 时间点配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月13日, 下午9:28:39
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 告警服务器配置
    // ***********************************************************

    /**
     * 获取接收告警服务的端口
     *
     * @return the server alarm port
     */
    public static int getServerAlarmPort() {
        return _allConfigurations.getInt("server.port");
    }

    /**
     * 获取接收告警服务的路径
     *
     * @return the server alarm path
     */
    public static String getServerAlarmPath() {
        return _allConfigurations.getString("server.path");
    }

    /**
     * 根据IP地址获取告警源名称
     *
     * @param address the address IP地址
     * @return the alarm client
     */
    @SuppressWarnings("unchecked")
    public static String getAlarmClient(String address) {
        Iterator<String> itor = _allConfigurations.getKeys("client");
        String client = StringUtils.EMPTY;
        while (itor.hasNext()) {
            String cur = itor.next();
            if (_allConfigurations.getString(cur).equals(address))
                client = cur.split("[.]")[1];
        }
        return client;
    }

    /**
     * 获取告警客户端的所有信息
     *
     * @return the client properties
     */
    @SuppressWarnings("unchecked")
    public static Properties getClientProperties() {
        Iterator<String> itor = _allConfigurations.getKeys("client");
        Properties properties = new Properties();
        while (itor.hasNext()) {
            String key = itor.next();
            properties.put(key, _allConfigurations.getString(key));
        }
        return properties;
    }

    // ***********************************************************
    // End of 告警服务器配置
    // ***********************************************************

    // ----------------------------------------------------------------------------------------------------------------
    // We are beautiful split lines, and we created @ 2014年8月13日, 下午9:42:59
    // ----------------------------------------------------------------------------------------------------------------

    // ***********************************************************
    // Start 模型配置
    // ***********************************************************

    /**
     * 获取字符串，内部方法
     *
     * @param expr the expr
     * @return the string
     */
    private static String getString(String expr) {
        return _allConfigurations.getString(expr);
    }

    /**
     * 获取字符串，内部方法
     *
     * @param xpath    the xpath
     * @param defValue the def value
     * @return the string
     */
    private static String getString(String xpath, String defValue) {
        return _allConfigurations.getString(xpath, defValue);
    }

    /**
     * 获取字符串数组，内部方法
     *
     * @param expr the expr
     * @return the string array
     */
    private static String[] getStringArray(String expr) {
        List<String> strs = Arrays.asList(_allConfigurations.getString(expr).split("[,]"));
        List<String> answers = new ArrayList<String>();
        for (String str : strs) {
            answers.add(str.trim());
        }
        return answers.toArray(new String[answers.size()]);
    }

    /**
     * 根据模型名称查询模型所在路径
     *
     * @param name the name
     * @return the model program
     */
    public static String getModelProgram(String name) {
        return getString("/model[@name='" + name + "']/program");
    }

    /**
     * 查询模型存储表名
     *
     * @param name the name
     * @return the model table
     */
    public static String getModelTable(String name) {
        return getString("/model[@name='" + name + "']/table");
    }

    /**
     * 获得模型的出图代码
     *
     * @param name the name
     * @return the model pic code
     */
    @Deprecated
    public static String getModelPicCode(String name) {
        return getString("/model[@name='" + name + "']/pic-code", StringUtils.EMPTY);
    }

    /**
     * 获取模型的展示名称
     *
     * @param name the name
     * @return the model display
     */
    public static String getModelDisplay(String name) {
        return getString("/model[@name='" + name + "']/display");
    }

    // ***********************************************************
    // End of 模型配置
    // ***********************************************************

    /**
     * A.
     *
     * @return the string
     */
    public static String a() {
        return _allConfigurations.getString("dsv.fault");
    }

    /**
     * Strs.
     *
     * @return the string[]
     */
    public static String[] strs() {
        return _allConfigurations.getStringArray("attrs.fault");
    }

}
