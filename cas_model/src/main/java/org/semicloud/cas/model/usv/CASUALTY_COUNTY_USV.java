package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CASUALTY_COUNTY_USV extends USVBase {
    /**
     * 创建县级市人口死亡数据集
     *
     * @param targetDatasourceName 目标数据源名称 默认为eqID
     * @param theJsonObject        结果Json对象
     */
    public static void createCasualtyCounty(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "县级市人口死亡分布_面数据";
            log.info("create CASUALTY_COUNTY shp files..., name is " + theDatasetVectorName);
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));
            log.info("create datasetvector " + SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource));
            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                datasetVector.getFieldInfos().add(getFieldInfo("Death", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Population", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Name", FieldType.TEXT));
                JSONArray jsonArray = theJsonObject.getJSONArray("casualties");
                log.info("parse json array, the array contains " + jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    log.info("for json object " + (i + 1));
                    processSingleCountyCasualty(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create CASUALTY_COUNTY shp file failed! msg: \n" + ex.getMessage());
        }
    }

    /**
     * 处理一个县级市对象
     *
     * @param targetDatasource     目标数据源
     * @param theDatasetVectorName 目标数据集名称
     * @param singleJsonObject     JsonObject对象
     */
    private static void processSingleCountyCasualty(Datasource targetDatasource, String theDatasetVectorName,
                                                    JSONObject singleJsonObject) {
        try {
            int smid = singleJsonObject.getInt("SMID");
            double death = singleJsonObject.getDouble("deathNumber");
            double population = singleJsonObject.getDouble("population");
            String name = singleJsonObject.getString("name");
            Datasource source = getSrcDatasource();
            GeoRegion region = (GeoRegion) SimpleGISUtils.getGeometry("BM_County_PG", "SMID=" + smid, source);
            closeSrcDatasource();

            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(region);
                recordset.setDouble("Death", death);
                recordset.setDouble("Population", population);
                recordset.setString("Name", name);
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        } catch (Exception ex) {
            log.error("create CASUALTY_COUNTY shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
