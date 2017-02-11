package org.semicloud.cas.model.us;

import com.supermap.data.GeoEllipse;
import com.supermap.data.Point2D;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.usv.INTENSITY_USV;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.cas.shared.intensity.IntensityLineCircle;

import java.util.List;
import java.util.stream.Collectors;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 地震烈度模型
 */
public class INTENSITY extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public INTENSITY(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 获得烈度圈的面积，单位是平方千米.
     *
     * @param ic 烈度圈对象
     * @return the area
     */
    private static double getArea(IntensityCircle ic) {
        GeoEllipse ellipse = new GeoEllipse();
        Point2D point2d = new Point2D(ic.getEpiCenter().getLongitude(), ic.getEpiCenter().getLatitude());
        ellipse.setCenter(point2d);
        ellipse.setSemimajorAxis(ic.getLongAxis());
        ellipse.setSemiminorAxis(ic.getShortAxis());
        ellipse.setRotation(ic.getAzimuth());
        // 返回烈度圈的面积，单位是平方千米
        return ellipse.getArea() / (1000 * 1000);
    }

    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        double[] epi = ModelGal.getProjection(epiCenter);
        for (float intensity = START_INTENSITY; intensity <= epiIntensity; intensity += 1) {
            JSONObject jsonObject = new JSONObject();
            IntensityCircle ic = getIntensityCircle(intensity);

            jsonObject.put("intensity", ic.getIntensity());
            _log.info("intensity:" + intensity);

            jsonObject.put("longitude", epi[0]);
            jsonObject.put("latitude", epi[1]);
            _log.info(text("lng|lat:{0}|{1}", epi[0], epi[1]));

            jsonObject.put("longAxis", Math.rint(ic.getLongAxis()));
            _log.info("longAxis:" + Math.rint(ic.getLongAxis()));

            jsonObject.put("shortAxis", Math.rint(ic.getShortAxis()));
            _log.info("shortAxis:" + Math.rint(ic.getShortAxis()));

            jsonObject.put("azimuth", Double.parseDouble(String.format("%.2f", ic.getAzimuth())));
            _log.info("azimuth:" + ic.getAzimuth());

            jsonObject.put("area", String.format("%.2f", getArea(ic)) + "KM^2");

            jsonArray.add(jsonObject);
        }

        resultJSONObject.put("circles", jsonArray);
        resultJSONObject.put("country", getCountryAttribute().getCountryAbbr());
        // 导出点源模型数据集
        INTENSITY_USV.createIntensityDatasetVector2(this.eqID, this.epiCenter, resultJSONObject);
        INTENSITY_USV.saveShapeFile(eqID);
        INTENSITY_USV.zipShapeFile(eqID);

        // 注意，如果地震发生在中国，且震级大于等于7.5则生成线源烈度模型的数据集供导出
        // 2017-02-11
        if (magnitude >= 7.5 && getCountryAttribute().getCountryAbbr().equals("CN")) {
            _log.info("地震震级大于7.5且发生在国内，导出线源烈度模型数据集");
            String dataSetName = eqID + "_L";
            // 生成线源烈度模型的对象，注意这里的烈度步长是1.0
            Point2D projection = INTENSITY_USV.getExProjection(epiCenter.getLongitude(), epiCenter.getLatitude());
            List<IntensityLineCircle> lineCircles = IntensityLineCircle
                    .getIntensityLineCircles(epiCenter, projection, magnitude, depth, 1.0f);
            // 把低于8度的烈度圈删除
            lineCircles = lineCircles.stream().filter(lc -> lc.getIntensity() >= 8.0).collect(Collectors.toList());
            // 按照烈度从小到大排序
            lineCircles = lineCircles.stream().sorted((c1, c2) -> Float.compare(c1.getIntensity(), c2.getIntensity()))
                    .collect(Collectors.toList());
            // 打印烈度看一下
            List<Float> exportedIntensities = lineCircles.stream().map(IntensityLineCircle::getIntensity).collect(Collectors.toList());
            _log.info("Exported Line Circle with Intensities:" + StringUtils.join(exportedIntensities, ", "));
            INTENSITY_USV.createLineCircleIntensityDatasetVector(dataSetName, epiCenter, lineCircles);
            INTENSITY_USV.saveShapeFile(dataSetName);
            INTENSITY_USV.zipShapeFile(dataSetName);

        }
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // INTENSITY_USV.createIntensityDatasetVector(eqID, resultJSONObject);
        // INTENSITY_USV.createIntensityTextDatasetVector(eqID,
        // resultJSONObject);
        // EPICENTER_USV.createEpiCenterDatasetVector(eqID, epi[0], epi[1]);
        // EPICENTER_USV.createEpiCenterTextVector(eqID, epi[0], epi[1]);
        return resultJSONObject.toString();
    }
}
