package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import org.semicloud.utils.db.factory.DaoFactory;

/**
 * 创建震中数据集
 */
public class EPICENTER_USV extends USVBase {

    /**
     * Creates the epi center dataset vector.
     *
     * @param targetDatasourceName the target datasource name
     * @param lng                  the lng
     * @param lat                  the lat
     */
    public static void createEpiCenterDatasetVector(String targetDatasourceName, double lng, double lat) {
        String theDatasetVectorName = "震中点";
        Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
        targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.POINT));
        if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
            SimpleGISUtils.updateGeometry(theDatasetVectorName, new GeoPoint(lng, lat), targetDatasource);
        }
        closeTargetDatasource();
    }

    /**
     * Creates the epi center text vector.
     *
     * @param targetDatasourceName the target datasource name
     * @param lng                  the lng
     * @param lat                  the lat
     */
    public static void createEpiCenterTextVector(String targetDatasourceName, double lng, double lat) {
        String theDatasetVectorName = "震中点_文字";
        Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
        targetDatasource.getDatasets().create(getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT));
        if (SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource)) {
            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(new GeoText(getTextPart(getInfo(targetDatasourceName), lng, lat)));
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        }
        closeTargetDatasource();
    }

    /**
     * Gets the info.
     *
     * @param eqID the eq id
     * @return the info
     */
    private static String getInfo(String eqID) {
        String sql = "SELECT INFO FROM BASIC_EQ_EVENT WHERE EQ_ID=?";
        String info = (String) DaoFactory.getInstance().queryScalar(sql, eqID);
        return info;
    }
}
