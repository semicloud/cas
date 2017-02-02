package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.utils.db.factory.DaoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 建筑物易损性矩阵分析模型
 */
public class CONSTRUCTION_MATRIX extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public CONSTRUCTION_MATRIX(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 根据烈度值找到相应的MAP
     *
     * @param intensity 烈度值
     * @param list      ListOfMap
     * @return
     */
    private static List<Map<String, Object>> findMap(int intensity, List<Map<String, Object>> list) {
        List<Map<String, Object>> ans = new ArrayList<>();
        for (Map<String, Object> map : list) {
            if (Integer.parseInt(map.get("INTENSITY").toString()) == intensity) {
                ans.add(map);
            }
        }
        return ans;
    }

    @Override
    public String getJson() {
        resultJSONObject.put("constructionLosses", getConstructionLosses());
        return resultJSONObject.toString();
    }

    /**
     * 获得省会城市的名称
     *
     * @return
     */
    private String getCityName() {
        String cityName = StringUtils.EMPTY;
        Map<String, Object> map = ModelGal.getPrefectureAttribute(epiCenter);
        if (map != null) {
            if (map.containsKey("name_cn")) {
                cityName = map.get("name_cn").toString();
            }
        }
        return cityName;
    }

    /**
     * 获得建筑物损失矩阵
     *
     * @return the construction losses
     */
    private JSONArray getConstructionLosses() {
        String cityName = getCityName();
        _log.info(text("cityName:{0}", cityName));
        if (cityName == StringUtils.EMPTY) {
            return new JSONArray();
        }
        JSONArray jsonArray = new JSONArray();
        String sql = "select * from bv_city_matrix where city_name=? and intensity between ? and ? order by intensity";
        float start = START_INTENSITY;
        float epi = epiIntensity;
        int startInt = (int) start;
        int epiInt = (int) epi;
        List<Map<String, Object>> allRecords = DaoFactory.getInstance().queryObjects(sql, cityName, startInt, epiInt);
        List<IntensityCircle> circles = getCircles();
        for (float s = START_INTENSITY; s <= epiIntensity; s += 1.0) {
            JSONObject jsonObject = new JSONObject();
            IntensityCircle circle = (IntensityCircle) JXPathContext.newContext(circles).getValue(
                    text(".[intensity={0}]", s));
            double[] epiCenter = ModelGal.getProjection(circle.getEpiCenter());
            jsonObject.put("intensity", circle.getIntensity());
            jsonObject.put("longitude", epiCenter[0]);
            jsonObject.put("latitude", epiCenter[1]);
            jsonObject.put("longAxis", Math.rint(circle.getLongAxis()));
            jsonObject.put("shortAxis", Math.rint(circle.getShortAxis()));
            jsonObject.put("azimuth", circle.getAzimuth());
            List<Map<String, Object>> maps = findMap((int) circle.getIntensity(), allRecords);
            jsonObject.put("construction", JSONArray.fromObject(maps));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
