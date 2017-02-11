package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.cfg.Settings;

/**
 * 数据集创建类的基类
 */
public abstract class USVBase {

    /**
     * 源工作空间
     */
    protected static Workspace srcWorkspace = new Workspace();

    /**
     * 目标工作空间
     */
    protected static Workspace tarWorkspace = new Workspace();

    /**
     * The log.
     */
    protected static Log log = LogFactory.getLog(USVBase.class);

    public static PrjCoordSys getCoordSys() {
        Datasource datasource = getSrcDatasource();
        Dataset dataset = datasource.getDatasets().get("BM_Country_PG");
        PrjCoordSys coordSys = dataset.getPrjCoordSys();
        closeSrcDatasource();
        return coordSys;
    }

    /**
     * 给要导出的点投影
     *
     * @param x
     * @param y
     * @return
     */
    public static Point2D getExProjection(double x, double y) {
        Point2D point2d = new Point2D(x, y);
        log.info("point in: " + x + "," + y);
        Point2Ds point2Ds = new Point2Ds();
        point2Ds.add(point2d);
        CoordSysTranslator.forward(point2Ds, getExportCoordSys());
        log.info("point out:" + point2Ds.getItem(0).getX() + "," + point2Ds.getItem(0).getY());
        return point2Ds.getItem(0);
    }

    /**
     * 获取导出时使用的投影系统
     *
     * @return
     */
    public static PrjCoordSys getExportCoordSys() {
        Datasource datasource = getSrcDatasource();
        DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(Settings.getExPrjStdDatasetName());
        log.info("Exported Prj Supporter:" + Settings.getExPrjStdDatasetName());
        PrjCoordSys prjCoordSys = datasetVector.getPrjCoordSys();
        closeSrcDatasource();
        return prjCoordSys;
    }

    /**
     * 获取TextPart
     *
     * @param text 文本
     * @param x    文本X坐标
     * @param y    文本Y坐标
     * @return the text part
     */
    public static TextPart getTextPart(String text, double x, double y) {
        return new TextPart(text, new Point2D(x, y));
    }

    /**
     * 获取TextStyle
     *
     * @return the text style
     */
    public static TextStyle getTextStyle() {
        TextStyle textStyle = new TextStyle();
        textStyle.setFontHeight(4);
        textStyle.setFontName("宋体");
        return textStyle;
    }

    /**
     * 创建DatasetVectorInfo，设置了数据集名称，数据集类型和数据集投影！！
     *
     * @param name 数据集名称
     * @param type 数据集类型
     * @return the dataset vector info
     */
    protected static DatasetVectorInfo getDatasetVectorInfo(String name, DatasetType type) {
        DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
        datasetVectorInfo.setName(name);
        datasetVectorInfo.setType(type);
        return datasetVectorInfo;
    }

    /**
     * 获取FieldInfo
     *
     * @param name Field名称
     * @param type Field类型
     * @return the field info
     */
    protected static FieldInfo getFieldInfo(String name, FieldType type) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setName(name);
        fieldInfo.setType(type);
        return fieldInfo;
    }

    /**
     * 获得源数据源
     *
     * @return the src datasource
     */
    protected static Datasource getSrcDatasource() {
        Datasource datasource = null;
        DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
        connectionInfo.setServer(Settings.getGISInstanceNameForInit());
        connectionInfo.setDatabase("");
        connectionInfo.setUser(Settings.getGisSettings().getDbUserName());
        connectionInfo.setPassword(Settings.getGisSettings().getDbUserPassword());
        connectionInfo.setEngineType(EngineType.ORACLEPLUS);
        connectionInfo.setAlias("alias");
        try {
            datasource = srcWorkspace.getDatasources().open(connectionInfo);
            log.info("get source datasource, [alias] " + datasource.getAlias());
        } catch (Exception ex) {
            log.error("open datasource error, with eqID:maptest");
        }
        return datasource;
    }

    /**
     * 关闭源数据源
     */
    protected static void closeSrcDatasource() {
        log.info("close source datasource.");
        srcWorkspace.getDatasources().closeAll();
    }

    /**
     * 获得存储ShpFile数据集的数据源，该数据源属于tarWorkspace 关闭该数据源请调用 closeTargetDatasouce()
     *
     * @return
     */
    protected static Datasource getSaveDataSource() {
        Datasource datasource = null;
        DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
        datasourceConnectionInfo.setServer(Settings.getGISInstanceNameForInit());
        // 注意，这里千万不能设置值，只能设为空字符串
        datasourceConnectionInfo.setDatabase("");
        datasourceConnectionInfo.setUser(Settings.getInyDatabaseUserName());
        datasourceConnectionInfo.setPassword(Settings.getInyDatabasePassword());
        datasourceConnectionInfo.setEngineType(EngineType.ORACLEPLUS);
        datasourceConnectionInfo.setAlias("alias1");
        try {
            datasource = tarWorkspace.getDatasources().open(datasourceConnectionInfo);
            log.info("get target datasource [alias1] " + datasource.getAlias());
        } catch (Exception ex) {
            log.error("open datasource [" + Settings.getInyDatabaseUserName() + "] error, message:" + ex.getMessage());
        }
        return datasource;
    }

    /**
     * 获得目标数据源
     *
     * @param eqID the eq id
     * @return the target datasource
     */
    protected static Datasource getTargetDatasource(String eqID) {
        Datasource datasource = null;
        DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
        connectionInfo.setServer(Settings.getGISInstanceNameForInit());
        connectionInfo.setDatabase("");
        connectionInfo.setUser(eqID);
        connectionInfo.setPassword(eqID);
        connectionInfo.setEngineType(EngineType.ORACLEPLUS);
        connectionInfo.setAlias(eqID);
        try {
            datasource = tarWorkspace.getDatasources().open(connectionInfo);
            log.info("get target datasource [alias] " + datasource.getAlias());
        } catch (Exception ex) {
            log.error("open datasource error, with eqID:" + eqID);
        }
        return datasource;
    }

    /**
     * 关闭目标数据源
     */
    protected static void closeTargetDatasource() {
        tarWorkspace.getDatasources().closeAll();
        log.info("close target datasource");
    }
}
