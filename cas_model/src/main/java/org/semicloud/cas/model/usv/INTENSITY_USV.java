package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.file.FileZipper;
import org.semicloud.utils.gis.ShapeFileExporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 烈度圈封装类
 */
class Circle implements Comparable<Circle> {
    double x;
    double y;
    double intensity;
    double azimuth;
    double longAxis;
    double shortAxis;

    public Circle() {
    }

    /**
     * @param x         震中经度
     * @param y         震中纬度
     * @param intensity 烈度值
     * @param azimuth   烈度圈偏转角
     * @param longAxis  长轴值
     * @param shortAxis 短轴值
     */
    public Circle(double x, double y, double intensity, double azimuth, double longAxis, double shortAxis) {
        this.x = x;
        this.y = y;
        this.intensity = intensity;
        this.azimuth = azimuth;
        this.longAxis = longAxis;
        this.shortAxis = shortAxis;
    }

    @Override
    public int compareTo(Circle o) {
        return Double.compare(this.intensity, o.intensity);
    }
}

/**
 * 创建地震烈度数据集
 */
public class INTENSITY_USV extends USVBase {

    // private static List<Circle> getCircles(JSONArray jsonArray) {
    // ArrayList<Circle> circles = new ArrayList<Circle>();
    // ArrayList<Circle> dao = new ArrayList<Circle>();
    // if (jsonArray != null) {
    // for (int i = 0; i < jsonArray.size(); i++) {
    // JSONObject singleJsonObject = jsonArray.getJSONObject(i);
    // double intensity = singleJsonObject.getDouble("intensity"), azimuth =
    // singleJsonObject
    // .getDouble("azimuth");
    // double lng = singleJsonObject.getDouble("longitude"), lat =
    // singleJsonObject.getDouble("latitude");
    // double lngx = singleJsonObject.getDouble("longAxis"), shtx =
    // singleJsonObject.getDouble("shortAxis");
    // Circle circle = new Circle(lng, lat, intensity, azimuth, lngx, shtx);
    // circles.add(circle);
    // }
    // Collections.sort(circles);
    // for (int i = circles.size(); i >= 0; i--) {
    // dao.add(circles.get(i));
    // }
    // }
    // return dao;
    // }

    /**
     * 创建烈度圈数据集
     *
     * @param targetDatasourceName the target datasource name 目标数据源
     * @param theJsonObject        the the json object 模型研判结果JSON对象
     */
    @Deprecated
    public static void createIntensityDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "烈度图";
            PrjCoordSys prjCoordSys = getCoordSys();
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                datasetVector.getFieldInfos().add(getFieldInfo("Intensity", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Azimuth", FieldType.DOUBLE));
                // 设置投影系统
                datasetVector.setPrjCoordSys(prjCoordSys);
                JSONArray jsonArray = theJsonObject.getJSONArray("circles");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    Double intensity = singleJsonObject.getDouble("intensity");
                    Double azimuth = singleJsonObject.getDouble("azimuth");
                    double lng = singleJsonObject.getDouble("longitude"), lat = singleJsonObject.getDouble("latitude");
                    double lngx = singleJsonObject.getDouble("longAxis"), shtx = singleJsonObject
                            .getDouble("shortAxis");
                    GeoEllipse ellipse = new GeoEllipse();
                    ellipse.setCenter(new Point2D(lng, lat));
                    ellipse.setSemimajorAxis(lngx);
                    ellipse.setSemiminorAxis(shtx);
                    ellipse.setRotation(azimuth);
                    log.info("创建" + intensity + "度圈");
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(ellipse.convertToRegion(360));
                        recordset.setDouble("Intensity", intensity);
                        recordset.setDouble("Azimuth", azimuth);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                }
                datasetVector.close();
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 根据烈度圈的JSON文件创建烈度圈数据集
     *
     * @param eqID       eqID，创建的烈度圈数据集名字为eqID
     * @param center     震中位置
     * @param jsonObject 烈度圈的JSON表示
     */
    public static void createIntensityDatasetVector2(String eqID, EpiCenter center, JSONObject jsonObject) {
        try {
            // 设置数据集名称，获取目标数据源（即存储Shp数据集的数据源名称）
            String dsName = eqID;
            Datasource dataSource = getSaveDataSource(); // 获取保存烈度圈数据集的数据源
            // 如果目标数据源中已经有名为eqID的数据集，删除之
            if (dataSource.getDatasets().contains(dsName)) {
                dataSource.getDatasets().delete(dsName);
                log.warn(String.format("already has dataset %s, deleting it!", dsName));
            }
            // 创建烈度圈数据集，并测试是否创建成功
            dataSource.getDatasets().create(getDatasetVectorInfo(dsName, DatasetType.REGION));
            if (dataSource.getDatasets().contains(dsName)) {
                DatasetVector dv = (DatasetVector) SimpleGISUtils.getDataset(dsName, dataSource);
                //region 设置投影，以前高娜说有问题，后来发现无问题
                // 读取oracle内的投影数据集
                // PrjCoordSys prjCoordSys = getExportCoordSys();

                // 使用外部的shp文件做投影数据集
                // PrjCoordSys prjCoordSys2 = new PrjCoordSys();
                // prjCoordSys2.fromFile("C://Krasovsky_1940_Albers.prj", PrjFileType.ESRI);
                // dv.setPrjCoordSys(prjCoordSys2);
                //endregion

                // 给这个数据集添加烈度和偏转角属性
                dv.getFieldInfos().add(getFieldInfo("Intensity", FieldType.DOUBLE));
                dv.getFieldInfos().add(getFieldInfo("Azimuth", FieldType.DOUBLE));

                // 遍历JsonArray，创建椭圆对象
                JSONArray jsonArray = jsonObject.getJSONArray("circles");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    double intensity = singleJsonObject.getDouble("intensity");
                    double azimuth = singleJsonObject.getDouble("azimuth");
                    // 还是使用地理坐标投影吧，读取长轴短轴
                    double lngx = singleJsonObject.getDouble("longAxis");
                    double shtx = singleJsonObject.getDouble("shortAxis");
                    GeoEllipse ellipse = new GeoEllipse();
                    ellipse.setCenter(getExProjection(center.getLongitude(), center.getLatitude()));
                    // ellipse.setCenter(center.toPoint2D());
                    log.info(text("lng:{0}, lat:{1}", center.getLongitude(), center.getLatitude()));
                    ellipse.setSemimajorAxis(lngx);
                    ellipse.setSemiminorAxis(shtx);
                    ellipse.setRotation(azimuth);
                    log.info("创建" + intensity + "度圈");
                    Recordset recordset = dv.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(ellipse.convertToRegion(360));
                        recordset.setDouble("Intensity", intensity);
                        recordset.setDouble("Azimuth", azimuth);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                }
                // datasetVector.setPrjCoordSys(getExportCoordSys());
                dv.close();
            }
            log.info("eqID:" + eqID + ", dataset create success!");
        } catch (Exception ex) {
            log.error(String.format("creating dataset for [%s] failed, messages: %s .", eqID, ex.getMessage()));
        } finally {
            closeTargetDatasource();
        }
    }

    /**
     * 将创建好的烈度圈数据集保存为shapefile文件
     *
     * @param eqID
     */
    public static void saveShapeFile(String eqID) {
        Datasource targetDatasource = getSaveDataSource();
        try {
            if (targetDatasource.getDatasets().contains(eqID)) {
                ShapeFileExporter.export(targetDatasource, eqID, Settings.getShpFileExportedPath());
            } else {
                log.error(String.format("dataset %s does not exist, exporting failed!", eqID));
            }
        } catch (Exception ex) {
            log.error("a error occurs when save shp file, message:" + ex.getMessage());
        } finally {
            closeTargetDatasource();
        }
    }

    /**
     * 将已经导出的shapefile压缩为.zip文件
     *
     * @param eqID
     */
    public static void zipShapeFile(String eqID) {
        File file = new File(Settings.getShpFileExportedPath() + "\\" + eqID + "\\" + eqID + ".shp");
        if (file.exists()) {
            String shpFolder = Settings.getShpFileExportedPath() + "\\" + eqID;
            String zipPathName = Settings.getShpFileExportedPath() + "\\" + eqID + ".zip";
            FileZipper.zipSimpleFolder(new File(shpFolder), StringUtils.EMPTY, zipPathName);
            if (new File(zipPathName).exists()) {
                log.info(String.format("[%s] file is generated!", zipPathName));
            } else {
                log.error(String.format("generate [%s] file failed!", zipPathName));
            }
        } else {
            log.error(String.format("shape file at [%s] is not generated, so can not zip it!", file.getAbsolutePath()));
        }
    }

    // ! 导出烈度圈数据为shapefile文件

    /**
     * 创建烈度圈（文本）数据集
     *
     * @param targetDatasourceName the target datasource name 目标数据源名称
     * @param theJsonObject        the the json object 模型研判结果JSON表示
     */
    public static void createIntensityTextDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "烈度图_文字";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT));
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                JSONArray jsonArray = theJsonObject.getJSONArray("circles");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    Double intensity = singleJsonObject.getDouble("intensity");
                    Double azimuth = singleJsonObject.getDouble("azimuth");
                    double lng = singleJsonObject.getDouble("longitude"), lat = singleJsonObject.getDouble("latitude");
                    double lngx = singleJsonObject.getDouble("longAxis"), shtx = singleJsonObject
                            .getDouble("shortAxis");
                    GeoEllipse ellipse = new GeoEllipse();
                    ellipse.setCenter(new Point2D(lng, lat));
                    ellipse.setSemimajorAxis(lngx);
                    ellipse.setSemiminorAxis(shtx);
                    ellipse.setRotation(azimuth);
                    Point2D point2d = new Point2D(ellipse.convertToRegion(360).getPart(0).getItem(0));
                    GeoText geoText = new GeoText(getTextPart(getIntensityString(intensity), point2d.getX(),
                            point2d.getY()));
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(geoText);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                }
                datasetVector.close();
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 获取烈度圈表示文字
     *
     * @param iny the iny 烈度值
     * @return the intensity string 烈度圈上应标示的文字
     */
    private static String getIntensityString(Double iny) {
        Map<Double, String> map = new HashMap<Double, String>();
        map.put(6.0, "VI");
        map.put(7.0, "VII");
        map.put(8.0, "VIII");
        map.put(9.0, "IX");
        map.put(10.0, "X");
        map.put(11.0, "XI");
        map.put(12.0, "XII");
        return map.get(iny);
    }
}
