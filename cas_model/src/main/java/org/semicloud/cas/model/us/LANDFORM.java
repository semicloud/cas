package org.semicloud.cas.model.us;

import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;

import java.util.List;
import java.util.Map;

/**
 * 震中附近地形地貌分析模型
 */
public class LANDFORM extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public LANDFORM(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 获取国家级的地理情况.
     *
     * @return JSONObject
     */
    private JSONObject getCountryLevelLandForm() {
        List<Map<String, Object>> results = ModelGal.getCountryLevelLandformInfo(epiCenter);
        JSONObject jsonObject = new JSONObject();
        _log.info("country level land forms:");
        if (results.size() > 0) {
            _log.info("landform:" + results.get(0).get("landform"));
            jsonObject = JSONObject.fromObject(results.get(0));
        }
        return jsonObject;
    }

    /**
     * 获取省级的地理情况.
     *
     * @return the province level land form
     */
    private JSONObject getProvinceLevelLandForm() {
        List<Map<String, Object>> results = ModelGal.getProvinceLevelLandformInfo(epiCenter);
        JSONObject jsonObject = new JSONObject();
        if (results.size() > 0) {
            _log.info("province level land forms:");
            _log.info("province:" + results.get(0).get("province"));
            _log.info("landform:" + results.get(0).get("landform"));
            jsonObject = JSONObject.fromObject(results.get(0));
        }
        return jsonObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    public String getJson() {
        resultJSONObject.put("country_level_landform", getCountryLevelLandForm());
        resultJSONObject.put("province_level_landform", getProvinceLevelLandForm());
        StringBuilder builder = new StringBuilder("国家级地理情况:");
        builder.append(getCountryLevelLandForm().getString("landform"));
        builder.append("省市级地理情况:");
        builder.append(getProvinceLevelLandForm().getString("landform"));
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // GENERAL_TEXT_USV.createTextDatasetVector(eqID, "灾区地理情况",
        // builder.toString(), epiCenter.getLongitude(),
        // epiCenter.getLatitude());
        _log.info(taskID + " --> 地形地貌模型计算完毕");
        return resultJSONObject.toString();
    }
}
