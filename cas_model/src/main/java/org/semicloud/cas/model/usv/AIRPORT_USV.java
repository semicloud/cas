package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 创建震中附近机场分布数据集
 */
public class AIRPORT_USV extends USVBase {

    /**
     * 创建机场shp file文件.
     *
     * @param targetDatasourceName 目标数据源名称
     * @param theJsonObject        機場模型的研判结果
     */
    public static void createAirportsDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "机场分布";
            log.info("create AIRPORT shp files..., name is " + theDatasetVectorName);
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.POINT);
            targetDatasource.getDatasets().create(datasetVectorInfo);
            DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName, targetDatasource);
            log.info("create datasetvector " + SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource));
            datasetVector.getFieldInfos().add(getFieldInfo("Name_CN", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("Name_EN", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("City", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("Through_Put", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("Run_Way", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("Description", FieldType.TEXT));
            datasetVector.getFieldInfos().add(getFieldInfo("Distance", FieldType.DOUBLE));

            JSONArray jsonArray = theJsonObject.getJSONArray("airports");
            log.info("parse json array, the array contains " + jsonArray.size());
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    log.info("for json object " + (i + 1));
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    String nameCn = singleJsonObject.getString("name_cn");
                    String nameEn = singleJsonObject.getString("name_en");
                    String city = singleJsonObject.getString("city_name");
                    String throughPut = singleJsonObject.getString("Throughput");
                    String runWay = singleJsonObject.getString("runway");
                    String Description = singleJsonObject.getString("descriptio");
                    // 经度纬度暂时没用
                    // double lng = singleJsonObject.getDouble("longitude"), lat
                    // =
                    // singleJsonObject.getDouble("latitude");
                    double smx = singleJsonObject.getDouble("SMX"), smy = singleJsonObject.getDouble("SMY");
                    double distance = singleJsonObject.getDouble("distance");
                    GeoPoint point = new GeoPoint(smx, smy);
                    log.info("create point " + smx + ", " + smy);
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(point);
                        recordset.setString("Name_CN", nameCn);
                        recordset.setString("Name_EN", nameEn);
                        recordset.setString("City", city);
                        recordset.setString("Through_Put", throughPut);
                        recordset.setString("Run_Way", runWay);
                        recordset.setString("Description", Description);
                        recordset.setDouble("Distance", distance);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                }
            }
            datasetVector.close();
            closeTargetDatasource();
            log.info("create AIRPORT shp files complete");
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
    }

    /**
     * 创建机场 文字 shape file文件.
     *
     * @param targetDatasourceName 目标数据源名称
     * @param theJsonObject        機場模型的研判结果
     */
    public static void createAirportsTextDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "机场分布_文字";
            log.info("create ACTIVEFAULT_TEXT shp files..., name is " + theDatasetVectorName);
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT);
            targetDatasource.getDatasets().create(datasetVectorInfo);
            log.info("create datasetvector " + SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource));
            DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                    targetDatasource);
            JSONArray jsonArray = theJsonObject.getJSONArray("airports");
            log.info("parse json array, the array contains " + jsonArray.size());
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    log.info("for json object " + (i + 1));
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    String nameCn = singleJsonObject.getString("name_cn");
                    // String nameEn = singleJsonObject.getString("name_en");
                    String city = singleJsonObject.getString("city_name");
                    // String throughPut =
                    // singleJsonObject.getString("Throughput");
                    // String runWay = singleJsonObject.getString("runway");
                    // String Description =
                    // singleJsonObject.getString("descriptio");
                    double distance = singleJsonObject.getDouble("distance");
                    // 经度纬度暂时没用
                    double lng = singleJsonObject.getDouble("longitude"), lat = singleJsonObject.getDouble("latitude");
                    double smx = singleJsonObject.getDouble("SMX"), smy = singleJsonObject.getDouble("SMY");
                    String text = nameCn + "(" + city + ")" + "\n" + lng + "," + lat + "\n" + distance + " KM";
                    GeoText geoText = new GeoText(getTextPart(text, smx, smy), getTextStyle());
                    log.info("create geo text " + geoText);
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(geoText);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                }
            }
            datasetVector.close();
            closeTargetDatasource();
            log.info("create AIRPORT_TEXT shp files complete");
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
