package org.semicloud.cas.model.us;

import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;

import java.util.List;
import java.util.Map;

/**
 * 震中附近经济发展情况.
 *
 * @author Semicloud
 */
public class ECONOMIC_INFO extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public ECONOMIC_INFO(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    // public static void main(String[] args) {
    // ModelInitilizer initilizer = new
    // ModelInitilizer("N62000W15180020140926015117", "MIN_30");
    // ECONOMIC_INFO economic = new ECONOMIC_INFO(initilizer, "");
    // System.out.println(economic.getJson());
    // }

    /**
     * 获取国家级经济发展情况
     *
     * @return the country level economic info
     */
    private JSONObject getCountryLevelEconomicInfo() {
        List<Map<String, Object>> results = ModelGal.getCountryLevelEconomicInfo(epiCenter);
        JSONObject jsonObject = new JSONObject();
        if (results.size() > 0) {
            Map<String, Object> map = results.get(0);
            _log.info("country level economic info as follows:");
            _log.info("gdp:" + map.get("gdp"));
            _log.info("gdp_per:" + map.get("gdp_per"));
            _log.info("unit:" + map.get("unit"));
            _log.info("sat_time:" + map.get("sat_time"));
            _log.info("descriptio:" + map.get("descriptio"));
            jsonObject = JSONObject.fromObject(map);
        }
        return jsonObject;
    }

    /**
     * 获取省份经济发展情况
     *
     * @return the province level economic info
     */
    private JSONObject getProvinceLevelEconomicInfo() {
        List<Map<String, Object>> results = ModelGal.getProvinceLevelEconomicInfo(epiCenter);
        JSONObject jsonObject = new JSONObject();
        if (results.size() > 0) {
            Map<String, Object> map = results.get(0);
            _log.info("province level economic info as follows:");
            _log.info("province:" + map.get("province"));
            _log.info("gdp:" + map.get("gdp"));
            _log.info("gdp_per:" + map.get("gdp_per"));
            _log.info("unit:" + map.get("unit"));
            _log.info("sat_time:" + map.get("sat_time"));
            _log.info("description" + map.get("descriptio"));
            jsonObject = JSONObject.fromObject(map);
        }
        return jsonObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    public String getJson() {
        resultJSONObject.put("country_level_economic", getCountryLevelEconomicInfo());
        resultJSONObject.put("province_level_economic", getProvinceLevelEconomicInfo());
        return resultJSONObject.toString();
    }
}
