package org.semicloud.cas.model.usv;

import com.mysql.jdbc.StringUtils;
import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 创建历史地震数据集
 */
public class HISTORICAL_USV extends USVBase {

    /**
     * 创建历史地震分布数据集
     *
     * @param targetDatasourceName the target datasource name
     * @param theJsonObject        the the json object
     */
    public static void createHistoryTextDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "历史地震分布_文字";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT);
            targetDatasource.getDatasets().create(datasetVectorInfo);
            JSONArray jsonArray = theJsonObject.getJSONArray("historical");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    double smx = singleJsonObject.getDouble("SMX");
                    double smy = singleJsonObject.getDouble("SMY");
                    String eqName = singleJsonObject.getString("eq_name");
                    // String eqPlace = singleJsonObject.getString("eq_place");
                    double magnitude = singleJsonObject.getDouble("magnitude");
                    if (StringUtils.isEmptyOrWhitespaceOnly(eqName))
                        eqName = "未知";
                    GeoText geoText = new GeoText(getTextPart(eqName + "," + magnitude, smx, smy), getTextStyle());
                    DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
                    Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
                    if (recordset != null) {
                        recordset.addNew(geoText);
                    }
                    recordset.update();
                    recordset.close();
                    recordset.dispose();
                    datasetVector.close();
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create HISTORICAL shp file failed! msg: \n" + ex.getMessage());
        }
    }

    /**
     * 创建历史地震分布.
     *
     * @param targetDatasourceName 目标数据源名称，默认为eqID
     * @param theJsonObject        历史地震
     */
    public static void createHistoryDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "历史地震分布";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.POINT);
            targetDatasource.getDatasets().create(datasetVectorInfo);
            DatasetVector theDatasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            theDatasetVector.getFieldInfos().add(getFieldInfo("EqName", FieldType.TEXT));
            theDatasetVector.getFieldInfos().add(getFieldInfo("EqPlace", FieldType.TEXT));
            theDatasetVector.getFieldInfos().add(getFieldInfo("Magnitude", FieldType.DOUBLE));
            JSONArray jsonArray = theJsonObject.getJSONArray("historical");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    processSingleHistory(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create HISTORICAL shp file failed! msg: \n" + ex.getMessage());
        }
    }

    /**
     * 操作一个历史地震对象.
     *
     * @param targetDatasource     目标数据源，该数据源需为打开状态
     * @param theDatasetVectorName 目标数据集名称
     * @param singleJsonObject     单个JSON对象
     */
    private static void processSingleHistory(Datasource targetDatasource, String theDatasetVectorName,
                                             JSONObject singleJsonObject) {
        try {
            double smx = singleJsonObject.getDouble("SMX");
            double smy = singleJsonObject.getDouble("SMY");
            String eqName = singleJsonObject.getString("eq_name");
            String eqPlace = singleJsonObject.getString("eq_place");
            double magnitude = singleJsonObject.getDouble("magnitude");

            DatasetVector theDatasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = theDatasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (magnitude > 0) {
                GeoPoint point = new GeoPoint(smx, smy);
                if (recordset != null) {
                    recordset.addNew(point);
                    recordset.setString("EqName", eqName);
                    recordset.setString("EqPlace", eqPlace);
                    recordset.setDouble("Magnitude", magnitude);
                }
                recordset.update();
                recordset.close();
                recordset.dispose();
                theDatasetVector.close();
            }
        } catch (Exception ex) {
            log.error("create HISTORICAL shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
