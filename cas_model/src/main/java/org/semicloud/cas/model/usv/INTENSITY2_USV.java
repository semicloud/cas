package org.semicloud.cas.model.usv;

import com.supermap.data.*;

/**
 * 该类用于绘制线源模型的影响场
 * Created by Administrator on 2017/2/6.
 */
public class INTENSITY2_USV extends USVBase {
    public static void main(String[] args) {
        Workspace ws = new Workspace();
        DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
        datasourceConnectionInfo.setServer("ORCL");
        datasourceConnectionInfo.setUser("MAPTEST");
        datasourceConnectionInfo.setPassword("maptest");
        datasourceConnectionInfo.setEngineType(EngineType.ORACLEPLUS);
        datasourceConnectionInfo.setDatabase("");
        datasourceConnectionInfo.setAlias("ForABetterLife");
        Datasource datasource = ws.getDatasources().open(datasourceConnectionInfo);
        if (datasource.isOpened()) {
            System.out.println("datasource is open.");
        }
        Geo

        DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo("Test", DatasetType.REGION);
        DatasetVector datasetVector = datasource.getDatasets().create(datasetVectorInfo);


        ws.getDatasources().closeAll();
        System.out.println("datasource closed..");
    }
}
