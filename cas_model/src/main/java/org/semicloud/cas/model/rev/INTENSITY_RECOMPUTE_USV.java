package org.semicloud.cas.model.rev;

import com.supermap.data.*;
import org.semicloud.cas.model.usv.USVBase;
import org.semicloud.cas.shared.EditRegion;

import java.util.List;

/**
 * 重计算-烈度圈数据集创建类
 */
public class INTENSITY_RECOMPUTE_USV extends USVBase {

    /**
     * 创建重计算-烈度圈数据集
     *
     * @param targetDatasourceName the target datasource name 目标数据源名称
     * @param taskId               the task id 任务ID
     * @param regions              the regions 编辑区域对象
     */
    public static void createIntensityReComputeDatasetVector(String targetDatasourceName, String taskId,
                                                             List<EditRegion> regions) {
        String theDatasetVectorName = "重_烈度_" + taskId.split("[_]")[1];
        Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
        DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.REGION);
        targetDatasource.getDatasets().create(datasetVectorInfo);
        for (EditRegion editRegion : regions) {
            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(editRegion.getGeoRegion());
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
        }
        closeTargetDatasource();
    }
}
