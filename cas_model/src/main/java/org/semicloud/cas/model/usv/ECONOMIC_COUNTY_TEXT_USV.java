package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 创建USGS-经济损失（县级市）-文本数据集
 */
public class ECONOMIC_COUNTY_TEXT_USV extends USVBase {

    /**
     * 创建县级市经济损失文本数据集.
     *
     * @param targetDatasourceName 数据源名称
     * @param theJsonObject        JSON对象
     */
    public static void createEconomicCountyText(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "县级市经济损失分布_文本";
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT));

            if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
                JSONArray jsonArray = theJsonObject.getJSONArray("economicLosses");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    processSingleEconomicCountyText(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * 创建一个县级市经济损失文本数据集.
     *
     * @param targetDatasource     the target datasource
     * @param theDatasetVectorName the the dataset vector name
     * @param singleJsonObject     the single json object
     */
    private static void processSingleEconomicCountyText(Datasource targetDatasource, String theDatasetVectorName,
                                                        JSONObject singleJsonObject) {
        try {
            double loss = singleJsonObject.getDouble("loss");
            String name = singleJsonObject.getString("name"), unit = singleJsonObject.getString("unit");
            double x = singleJsonObject.getDouble("x"), y = singleJsonObject.getDouble("y");
            String text = name + "\n" + "经济损失:" + loss + "\n" + unit;
            TextPart textPart = new TextPart(text, new Point2D(x, y));
            TextStyle textStyle = new TextStyle();
            textStyle.setFontHeight(4);
            textStyle.setFontName("Times New Roma");
            GeoText geoText = new GeoText(textPart);
            geoText.setTextStyle(textStyle);
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
