package org.semicloud.cas.model.usv;

import com.supermap.data.*;

/**
 * 通用文本数据集创建类，
 * <p>
 * 用于创建宗教信仰呀，之类的只有文本的数据集.
 *
 * @author Semicloud
 */
public class GENERAL_TEXT_USV extends USVBase {

    /**
     * 创建文本数据集
     *
     * @param targetDatasourceName 目标数据源名称
     * @param theDatasetVectorName 目标数据集名称
     * @param text                 文本
     * @param x                    x坐标
     * @param y                    y坐标
     */
    public static void createTextDatasetVector(String targetDatasourceName, String theDatasetVectorName, String text,
                                               double x, double y) {
        try {
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT));
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
            closeTargetDatasource();
        } catch (Exception ex) {
            log.error("create GENERAL_TEXT shp file failed! msg: \n" + ex.getMessage());
        }
    }
}
