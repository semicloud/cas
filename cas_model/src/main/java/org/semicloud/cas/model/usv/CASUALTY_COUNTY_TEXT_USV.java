package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CASUALTY_COUNTY_TEXT_USV extends USVBase {
    /**
     * 创建县级市人口死亡文本数据集
     *
     * @param targetDatasourceName 数据源名称
     * @param theJsonObject        JSON对象
     */
    public static void createCasualtyCountyText(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "县级市人口死亡分布_文本";
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
     * 创建一个县级市人口死亡文本数据集
     *
     * @param targetDatasource
     * @param theDatasetVectorName
     * @param singleJsonObject
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


}
