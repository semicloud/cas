package org.semicloud.cas.model.us;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.db.factory.DaoFactory;

import java.util.*;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 人口死亡模型 尹之潜模型
 *
 * @author Semicloud
 */
@Deprecated
public class CASUALTY_YINZHIQIAN extends BaseModel {

    public CASUALTY_YINZHIQIAN(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    // 查询GIS，获得本次地震省会城市的名称（如果发生在省会城市的话）
    private static String getPrefectureNameCn(EpiCenter epiCenter) {
        String ans = StringUtils.EMPTY;
        Map<String, Object> map = ModelGal.getPrefectureAttribute(epiCenter);
        if (map.containsKey("name_cn"))
            ans = map.get("name_cn").toString();
        return ans;
    }

    private static List<Map<String, Object>> getAllDamageInfos(String cityName, String type, int start, int epi) {
        String cmd = "select * from bv_city_matrix where city_name=? and struct_type=? and intensity >=? and intensity <=?";
        List<Map<String, Object>> ans = DaoFactory.getInstance().queryObjects(cmd, cityName, type, start, epi);
        return ans;
    }

    private static Map<String, Object> getIntensityDamageInfo(final int intensity, List<Map<String, Object>> infos) {
        Predicate<Map<String, Object>> predicate = new Predicate<Map<String, Object>>() {
            @Override
            public boolean apply(Map<String, Object> input) {
                return Convert.toInteger(input.get("INTENSITY")) == intensity;
            }
        };
        Collection<Map<String, Object>> collection = Collections2.filter(infos, predicate);
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>(collection);
        if (maps.size() > 0)
            return maps.get(0);
        return new HashMap<String, Object>();
    }

    // private static List<Map<String, Object>> getCityMatrix(String cityName) {
    // List<Map<String, Object>> ans = new ArrayList<Map<String, Object>>();
    // String cmdText = "select * from bv_city_matrix where city_name=?";
    // ans = DaoFactory.getInstance().queryObjects(cmdText, cityName);
    // return ans;
    // }

    private static List<String> getStructureTypes(String cityName) {
        List<String> ans = Lists.newArrayList();
        String cmd = "select distinct(struct_type) from bv_city_matrix where city_name=?";
        List<Object> ret = DaoFactory.getInstance().queryList(cmd, cityName);
        ans = Lists.transform(ret, Functions.toStringFunction());
        return ans;
    }

    // 计算人口死亡率
    private static double getRatio(double damage) {
        return FastMath.exp(12.479 * FastMath.pow(damage, 0.1) - 13.3);
    }

    private static float parseFloat(Object o) {
        if (o == null)
            return 0;
        return Convert.toFloat(o);
    }

    public static void main(String[] args) {
        ModelInitilizer initilizer = new ModelInitilizer("N30300E10300020130430112325", "");
        CASUALTY_YINZHIQIAN model = new CASUALTY_YINZHIQIAN(initilizer, "");
        // System.out.println(initilizer.getCircles());
        System.out.println(model.getJson());
    }

    @Override
    public String getJson() {
        String cityName = getPrefectureNameCn(epiCenter);
        if (cityName == null || cityName == "") {
            resultJSONObject.put("deathInfo", "未找到相关基础数据");
            return resultJSONObject.toString();
        }

        List<String> structureType = getStructureTypes(cityName);
        int startIny = (int) Settings.getModelStartIntensity();
        int epiIny = (int) epiIntensity;
        for (String type : structureType) {
            for (int i = startIny; i <= epiIny; i++) {
                Map<String, Object> map = getIntensityDamageInfo(i, getAllDamageInfos(cityName, type, startIny, epiIny));
                _log.info(text("intensity {0}, matrix : {1}", i, map));
                if (!map.keySet().isEmpty()) {
                    float maxDamage = parseFloat(map.get("MAX_DAMAGE"));
                    float destory = parseFloat(map.get("DESTROY"));
                    _log.info(text("maxDamage:{0}, destory:{1}", maxDamage, destory));
                    double ratio = getRatio(maxDamage + destory);
                    _log.info(text("type:{0}, intensity:{1}, ratio:{2}", type, i, ratio));
                }
            }
        }
        return null;
    }
}

