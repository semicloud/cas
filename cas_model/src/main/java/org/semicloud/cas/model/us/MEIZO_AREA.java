package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityCircle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc

/**
 * 极震区分析模型.
 *
 * @author Semicloud
 */
public class MEIZO_AREA extends BaseModel {

    /**
     * 构造函数.
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public MEIZO_AREA(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 查询最大烈度圈所覆盖的县级市的名称.
     *
     * @return 最大烈度圈所覆盖的县级市的名称
     */
    private List<Map<String, Object>> getCityNameByEpiInyCovered() {
        // 找到烈度最大的烈度圈
        List<Map<String, Object>> countyMapList = new ArrayList<>();
        IntensityCircle ic = getIntensityCircle(Settings.getMeiziAreKeyIntensity());
        if (ic != null) {
            countyMapList = ModelGal.getMeizoseismalAreaInfos(ic);
        }
        return countyMapList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    public String getJson() {
        List<Map<String, Object>> results = getCityNameByEpiInyCovered();
        JSONArray array = new JSONArray();
        _log.info("meizo areas as follows, about " + results.size());
        if (results.size() > 0) {
            for (Map<String, Object> map : results) {
                _log.info(map);
                JSONObject object = JSONObject.fromObject(map);
                array.add(object);
            }
        }
        _log.info("run here.");
        resultJSONObject.put("rescue_cities", array);
        return resultJSONObject.toString();
    }
}
