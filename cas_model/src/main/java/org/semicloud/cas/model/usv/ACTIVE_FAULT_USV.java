package org.semicloud.cas.model.usv;

import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.GeoLine;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 创建断裂带分布数据集
 */
public class ACTIVE_FAULT_USV extends USVBase {

    /**
     * 创建断裂带分布.
     *
     * @param targetDatasourceName 目标数据源名称，默认为eqID
     * @param theJsonObject        断裂带JSON对象
     */
    public static void createActiveFaultsDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        try {
            String theDatasetVectorName = "断裂带分布";
            log.info("create ACTIVEFAULT shp files..., name is " + theDatasetVectorName);
            Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
            DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.LINE);
            targetDatasource.getDatasets().create(datasetVectorInfo);
            log.info("create datasetvector " + SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource));
            JSONArray jsonArray = theJsonObject.getJSONArray("active_faults");
            log.info("parse json array, the array contains " + jsonArray.size());
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    log.info("for json object " + (i + 1));
                    JSONObject singleJsonObject = jsonArray.getJSONObject(i);
                    processSingleActiveFault(targetDatasource, theDatasetVectorName, singleJsonObject);
                }
            }
            closeTargetDatasource();
            log.info("create ACTIVEFAULT shp files complete");
        } catch (Exception ex) {
            log.error("create active fault shp file failed! msg: \n" + ex.getMessage());
        }
        closeTargetDatasource();
    }

    /**
     * 操作一个断裂带对象.
     *
     * @param targetDatasource     目标数据源，该数据源需为打开状态
     * @param theDatasetVectorName 目标数据集名称
     * @param singleJsonObject     单个JSON对象
     */
    private static void processSingleActiveFault(Datasource targetDatasource, String theDatasetVectorName,
                                                 JSONObject singleJsonObject) {
        int smid = singleJsonObject.getInt("SMID");
        log.info("get smid " + smid);
        Datasource sourceDatasource = getSrcDatasource();
        GeoLine geoLine = (GeoLine) SimpleGISUtils.getGeometry("ST_Activefault_PL", "SMID=" + smid, sourceDatasource);
        log.info("query line from ST_Activefault_PL, ID=" + geoLine.getID());
        closeSrcDatasource();
        SimpleGISUtils.updateGeometry(theDatasetVectorName, geoLine, targetDatasource);
    }
}
