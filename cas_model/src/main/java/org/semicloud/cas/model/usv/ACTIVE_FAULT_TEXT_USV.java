package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 断裂带分布文字数据集创建类.
 *
 * @author Semicloud
 */
public class ACTIVE_FAULT_TEXT_USV extends USVBase {

    /**
     * 创建断裂带分布_文本数据集.
     *
     * @param targetDatasourceName 目标数据源名称，默认为eqID
     * @param theJsonObject        断裂带JSON对象
     */
    public static void createActiveFaultsTextDatasetVector(String targetDatasourceName, JSONObject theJsonObject) {
        String theDatasetVectorName = "断裂带分布_文本";
        log.info("create ACTIVEFAULT_TEXT shp files..., name is " + theDatasetVectorName);
        List<InnerClass> innerClasses = getInnerClasses(theJsonObject);
        List<GeoLine> lines = getSrcLines(innerClasses);
        Datasource targetDatasource = getTargetDatasource(targetDatasourceName);
        DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(theDatasetVectorName, DatasetType.TEXT);
        targetDatasource.getDatasets().create(datasetVectorInfo);
        log.info("create datasetvector " + SimpleGISUtils.existDataset(theDatasetVectorName, targetDatasource));
        int count = innerClasses.size() > 5 ? 5 : innerClasses.size();
        for (int i = 0; i < count; i++) {
            InnerClass innerClass = innerClasses.get(i);
            Point2D point = lines.get(i).getInnerPoint();
            if (StringUtils.isEmpty(innerClass.nameCn))
                innerClass.nameCn = "未知名称";
            String text = innerClass.nameCn + "\n" + "距离震中:" + innerClass.distance + "KM";
            GeoText geoText = new GeoText(getTextPart(text, point.getX(), point.getY()), getTextStyle());
            DatasetVector datasetVector = (DatasetVector) targetDatasource.getDatasets().get(theDatasetVectorName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
            if (recordset != null) {
                recordset.addNew(geoText);
            }
            recordset.update();
            recordset.close();
            recordset.dispose();
            datasetVector.close();
        }
        closeTargetDatasource();
    }

    /**
     * 根据InnserClass获得断裂带的线对象
     *
     * @param innerClasses the inner classes
     * @return the src lines
     */
    private static List<GeoLine> getSrcLines(List<InnerClass> innerClasses) {
        int count = innerClasses.size() > 5 ? 5 : innerClasses.size();
        List<GeoLine> lines = new ArrayList<GeoLine>();
        Datasource srcDatasource = getSrcDatasource();
        for (int i = 0; i < count; i++) {
            GeoLine geoLine = (GeoLine) SimpleGISUtils.getGeometry("ST_Activefault_PL", "SMID="
                    + innerClasses.get(i).smId, srcDatasource);
            lines.add(geoLine);
        }
        closeSrcDatasource();
        return lines;
    }

    /**
     * 将模型研判结果封装为InnerClass
     *
     * @param jsonObject the json object
     * @return the inner classes
     */
    private static List<InnerClass> getInnerClasses(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("active_faults");
        List<InnerClass> list = new ArrayList<InnerClass>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject theObject = jsonArray.getJSONObject(i);
                InnerClass innerClass = new InnerClass();
                innerClass.smId = theObject.getInt("SMID");
                innerClass.nameCn = theObject.getString("name_cn");
                innerClass.distance = theObject.getDouble("distance");
                list.add(innerClass);
            }
        }
        Collections.sort(list);
        return list;
    }
}

class InnerClass implements Comparable<InnerClass> {
    int smId;
    String nameCn;
    Double distance;

    @Override
    public int compareTo(InnerClass o) {
        return this.distance.compareTo(o.distance);
    }
}
