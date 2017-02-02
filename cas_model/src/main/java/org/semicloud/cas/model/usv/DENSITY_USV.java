package org.semicloud.cas.model.usv;

import com.supermap.analyst.spatialanalyst.RasterClip;
import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.intensity.IntensityCircle;

/**
 * 创建人口密度数据集
 */
public class DENSITY_USV extends USVBase {
    /**
     * 创建经济损失（县级市）数据集.
     *
     * @param targetDatasourceName 目标数据源名称，默认为eqID
     * @param theJsonObject        目标Json对象
     */
    public static void createDensityyDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "人口密度_县级市_矢量";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));
            log.info("创建县级市人口密度数据集:" + theDatasetVectorName);
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                log.info("获取数据集" + theDatasetVectorName + ", " + (datasetVector != null));
                datasetVector.getFieldInfos().add(getFieldInfo("Density", FieldType.TEXT));
                datasetVector.getFieldInfos().add(getFieldInfo("Name", FieldType.TEXT));
                datasetVector.getFieldInfos().add(getFieldInfo("Area", FieldType.TEXT));
                log.info("add field infos");
                JSONArray jsonArray = theJsonObject.getJSONArray("density");
                log.info("处理density, json array size is " + jsonArray.size());
                log.info("开始迭代处理JSONObject");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    log.info("处理jsonObj " + i);
                    processSingleCountyDensity(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
    }

    /**
     * 创建一个县级市经济损失.
     *
     * @param targetDatasource     目标数据源
     * @param theDatasetVectorName 目标数据集名称
     * @param singleJsonObject     目标JSON对象
     */
    private static void processSingleCountyDensity(Datasource targetDatasource, String theDatasetVectorName,
                                                   JSONObject singleJsonObject) {
        try {
            int smid = singleJsonObject.getInt("smid");
            String density = singleJsonObject.getString("density");
            String area = singleJsonObject.getString("area");
            String name = singleJsonObject.getString("name_cn");
            log.info("process json object, smid=" + smid + ", name=" + name);
            Datasource source = getSrcDatasource();
            log.info("get src datasource:" + source);
            GeoRegion region = (GeoRegion) SimpleGISUtils.getGeometry("BM_County_PG", "SMID=" + smid, source);
            log.info("get region:" + region);
            closeSrcDatasource();
            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            log.info("get target datasetvector " + datasetVector);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(region);
                recordset.setString("Density", density);
                recordset.setString("Name", name);
                recordset.setString("Area", area);
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
    }

    public static void createDensityDatasetGrid(String targetDatasourceName, IntensityCircle intensityCircle) {
        String rltName = "人口密度分布_栅格";
        Datasource source = getSrcDatasource();
        Datasource target = getTargetDatasource(targetDatasourceName);
        Point2D point = intensityCircle.getEpiCenter().toPoint2D();
        point = SimpleGISUtils.getProjection(point, source);
        GeoEllipse ellipse = new GeoEllipse();
        ellipse.setCenter(point);
        ellipse.setSemimajorAxis(intensityCircle.getLongAxis());
        ellipse.setSemiminorAxis(intensityCircle.getShortAxis());
        ellipse.setRotation(intensityCircle.getAzimuth());
        GeoRegion region = ellipse.convertToRegion(36);
        String gridName = ModelGal.getPopulationDatasetGridName(intensityCircle.getEpiCenter());
        DatasetGrid grid = (DatasetGrid) source.getDatasets().get(gridName);
        RasterClip.clip(grid, region, true, true, target, rltName);
        if (target.getDatasets().contains(rltName)) {
            log.info("人口密度栅格数据集创建成功");
        }
        closeSrcDatasource();
        closeTargetDatasource();
    }

    /**
     * 创建人口密度数据集.
     *
     * @param targetDatasourceName 目标数据源
     * @param theJsonObject        人口密度JSON对象
     */
    @Deprecated
    public static void createDensityDatasetVector_old(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "人口密度分布";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                datasetVector.getFieldInfos().add(getFieldInfo("Intensity", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Density", FieldType.DOUBLE));
                JSONArray jsonArray = theJsonObject.getJSONArray("pop_density");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    Double intensity = singleJsonObject.getDouble("intensity");
                    Double density = singleJsonObject.getDouble("density");
                    double lng = singleJsonObject.getDouble("longitude"), lat = singleJsonObject.getDouble("latitude");
                    double lngx = singleJsonObject.getDouble("longAxis"), shtx = singleJsonObject
                            .getDouble("shortAxis");
                    double azimuth = singleJsonObject.getDouble("azimuth");
                    GeoEllipse ellipse = new GeoEllipse();
                    ellipse.setCenter(new Point2D(lng, lat));
                    ellipse.setSemimajorAxis(lngx);
                    ellipse.setSemiminorAxis(shtx);
                    ellipse.setRotation(azimuth);
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(ellipse.convertToRegion(180));
                        recordset.setDouble("Intensity", intensity);
                        recordset.setDouble("Density", density);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                    datasetVector.close();
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create DENSITY shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
