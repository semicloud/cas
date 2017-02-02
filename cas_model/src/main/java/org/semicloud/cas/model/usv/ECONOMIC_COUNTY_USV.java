package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 创建USGS-经济损失（县级市）数据集
 */
public class ECONOMIC_COUNTY_USV extends USVBase {

    /**
     * 创建经济损失（县级市）数据集.
     *
     * @param targetDatasourceName 目标数据源名称，默认为eqID
     * @param theJsonObject        目标Json对象
     */
    public static void createEconomicCountyDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "县级市经济损失分布_面数据";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));
            log.info("创建县级市经济损失数据集:" + theDatasetVectorName);
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                log.info("获取数据集" + theDatasetVectorName + ", " + (datasetVector != null));
                datasetVector.getFieldInfos().add(getFieldInfo("Loss", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Unit", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Name", FieldType.TEXT));
                log.info("add field infos");
                JSONArray jsonArray = theJsonObject.getJSONArray("economicLosses");
                log.info("处理economic losses, json array size is " + jsonArray.size());
                log.info("开始迭代处理JSONObject");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    log.info("处理jsonObj " + i);
                    processSingleCountyEconomic(targetDatasource, theDatasetVectorName, singleJsonObject);
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
    private static void processSingleCountyEconomic(Datasource targetDatasource, String theDatasetVectorName,
                                                    JSONObject singleJsonObject) {
        try {
            int smid = singleJsonObject.getInt("smid");
            double loss = singleJsonObject.getDouble("loss");
            String unit = singleJsonObject.getString("unit");
            String name = singleJsonObject.getString("name");
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
                recordset.setDouble("Loss", loss);
                recordset.setString("Unit", unit);
                recordset.setString("Name", name);
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
