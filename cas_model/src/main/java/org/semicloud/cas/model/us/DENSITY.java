package org.semicloud.cas.model.us;

import com.supermap.data.GeoEllipse;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.intensity.IntensityCircle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 人口密度分析模型
 */
public class DENSITY extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public DENSITY(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        List<IntensityCircle> circlesList = getCircles();
        // 烈度圈先排序
        circlesList.stream().sorted((c1, c2) -> Float.compare(c1.getIntensity(), c2.getIntensity()));
        // 找一找6度圈
        Optional<IntensityCircle> circle = circlesList.stream().filter(c -> c.getIntensity() == 6.0).findFirst();
        // 如果找得到6度圈，就用6度圈做空间谓词，找不到的话就用烈度最大的那个圈做空间谓词
        IntensityCircle predicate = circle.isPresent() ? circle.get() : circlesList.get(circlesList.size() - 1);
        boolean isChina = getCountryAttribute().getCountryAbbr().equals("CN");
        if (isChina) {
            _log.info("地震发生在国内，按照国内标准计算人口密度");
            List<Map<String, Object>> list = ModelGal.getCountiesPopDensity(predicate);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject jsonObject = JSONObject.fromObject(list.get(i));
                    jsonArray.add(jsonObject);
                }
            }
        } else {
            _log.info("地震没有发生在国内，按照国外标准");
            double[] epi = ModelGal.getProjection(epiCenter);
            for (float intensity = START_INTENSITY; intensity <= epiIntensity; intensity += 1.0) {
                JSONObject jsonObject = new JSONObject();
                IntensityCircle ic = getIntensityCircle(intensity);
                double density = ModelGal.getPopulationDensity(ic);
                jsonObject.put("intensity", ic.getIntensity());
                jsonObject.put("longitude", epi[0]);
                jsonObject.put("latitude", epi[1]);
                jsonObject.put("longAxis", Math.rint(ic.getLongAxis()));
                jsonObject.put("shortAxis", Math.rint(ic.getShortAxis()));
                jsonObject.put("azimuth", ic.getAzimuth());
                jsonObject.put("density", Math.rint(density));
                _log.info(text("intensity:{0}, density:{1}/persons per km^2", intensity, density));
                GeoEllipse ellipse = ic.toGeoEllipse();
                ellipse.setCenter(new Point2D(epi[0], epi[1]));
                Point2Ds point2Ds = ellipse.convertToRegion(144).getPart(0);
                JSONArray points = new JSONArray();
                for (int i = 0; i < point2Ds.getCount(); i++) {
                    JSONObject aPoint = new JSONObject();
                    aPoint.put("x", point2Ds.getItem(i).getX());
                    aPoint.put("y", point2Ds.getItem(i).getY());
                    points.add(aPoint);
                }
                jsonObject.put("points", points);
                jsonArray.add(jsonObject);
            }
        }
        // DENSITY_USV.createDensityDatasetGrid(eqID, predicate);
        resultJSONObject.put("density", jsonArray);
        // if (isChina)
        // DENSITY_USV.createDensityyDatasetVector(eqID, resultJSONObject);
        return resultJSONObject.toString();
    }

    @Deprecated
    public String old() {
        JSONArray jsonArray = new JSONArray();
        double[] epi = ModelGal.getProjection(epiCenter);
        for (float intensity = START_INTENSITY; intensity <= epiIntensity; intensity += 1.0) {
            JSONObject jsonObject = new JSONObject();
            IntensityCircle ic = getIntensityCircle(intensity);
            double density = ModelGal.getPopulationDensity(ic);
            jsonObject.put("intensity", ic.getIntensity());
            jsonObject.put("longitude", epi[0]);
            jsonObject.put("latitude", epi[1]);
            jsonObject.put("longAxis", Math.rint(ic.getLongAxis()));
            jsonObject.put("shortAxis", Math.rint(ic.getShortAxis()));
            jsonObject.put("azimuth", ic.getAzimuth());
            jsonObject.put("density", Math.rint(density));
            _log.info(text("intensity:{0}, density:{1}/persons per km^2", intensity, density));
            GeoEllipse ellipse = ic.toGeoEllipse();
            ellipse.setCenter(new Point2D(epi[0], epi[1]));
            Point2Ds point2Ds = ellipse.convertToRegion(144).getPart(0);
            JSONArray points = new JSONArray();
            for (int i = 0; i < point2Ds.getCount(); i++) {
                JSONObject aPoint = new JSONObject();
                aPoint.put("x", point2Ds.getItem(i).getX());
                aPoint.put("y", point2Ds.getItem(i).getY());
                points.add(aPoint);
            }
            jsonObject.put("points", points);
            jsonArray.add(jsonObject);
        }
        resultJSONObject.put("pop_density", jsonArray);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // DENSITY_USV.createDensityDatasetVector_old(eqID, resultJSONObject);
        return resultJSONObject.toString();
    }
}
