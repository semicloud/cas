package org.semicloud.cas.model.rev;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.usv.SimpleGISUtils;
import org.semicloud.cas.model.usv.USVBase;

/**
 * 创建重计算-人口伤亡模型数据集
 */
public class CASUALTY_RECOMPUTE_COUNTY_REGION_USV extends USVBase {

    /**
     * 创建县级市人口死亡文本数据集.
     *
     * @param targetDatasourceName 目标数据源名称
     * @param taskID               the task id 任務ID
     * @param theJsonObject        模型研判結果JSON對象
     */
    public static void createCasualtyCountyText(String targetDatasourceName, String taskID, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "重_县人_文本_" + taskID.split("[_]")[1];
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT));

            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                JSONArray jsonArray = theJsonObject.getJSONArray("casualties");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    processSingleCountyText(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 创建一个县级市人口死亡文本数据集.
     *
     * @param targetDatasource     the target datasource 目标数据源
     * @param theDatasetVectorName the the dataset vector name 文本数据集名称
     * @param singleJsonObject     the single json object 模型研判结果
     */
    private static void processSingleCountyText(Datasource targetDatasource, String theDatasetVectorName,
                                                JSONObject singleJsonObject) {
        try {
            double death = singleJsonObject.getDouble("deathNumber"), population = singleJsonObject
                    .getDouble("population");
            String name = singleJsonObject.getString("name");
            double x = singleJsonObject.getDouble("x"), y = singleJsonObject.getDouble("y");
            String text = name + "\n" + "人口总数:" + population + "\n" + "死亡人数:" + death;
            GeoText geoText = new GeoText(getTextPart(text, x, y), getTextStyle());
            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(geoText);
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 创建县级市人口死亡数据集.
     *
     * @param targetDatasourceName 目标数据源名称 默认为eqID
     * @param taskID               the task id 任务ID
     * @param theJsonObject        结果Json对象
     */
    public static void createCasualtyCounty(String targetDatasourceName, String taskID, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "重_县人_" + taskID.split("[_]")[1];
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION));

            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                DatasetVector datasetVector = (DatasetVector) SimpleGISUtils.getDataset(theDatasetVectorName,
                        targetDatasource);
                datasetVector.getFieldInfos().add(getFieldInfo("Death", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Population", FieldType.DOUBLE));
                datasetVector.getFieldInfos().add(getFieldInfo("Name", FieldType.TEXT));
                JSONArray jsonArray = theJsonObject.getJSONArray("casualties");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    processSingleCountyCasualty(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 处理一个县级市对象.
     *
     * @param targetDatasource     目标数据源
     * @param theDatasetVectorName 目标数据集名称
     * @param singleJsonObject     模型研判结果
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
            System.err.println(ex.getMessage());
        }
    }
}
