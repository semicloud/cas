package org.semicloud.cas.model.us;

import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;

import java.util.List;
import java.util.Map;

/**
 * 震中附近文化-宗教分析.
 */
public class CULTURE_RELIGION extends BaseModel {
    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public CULTURE_RELIGION(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 过去国家级文化宗教
     *
     * @return the country level culture religion
     */
    private JSONObject getCountryLevelCultureReligion() {
        List<Map<String, Object>> results = ModelGal.getCountryLevelCultureReligion(epiCenter);
        _log.info("country culture religion detailed infos has " + results.size() + " items");
        JSONObject jsonObject = new JSONObject();
        if (results.size() > 0) {
            Map<String, Object> map = results.get(0);
            _log.info("language:" + map.get("language"));
            _log.info("nation_day:" + map.get("nation_day"));
            _log.info("nation:" + map.get("nation"));
            _log.info("religion:" + map.get("religion"));
            _log.info("feature:" + map.get("feature"));
            jsonObject = JSONObject.fromObject(map);
        }
        return jsonObject;
    }

    /**
     * 获得省市级文化宗教
     *
     * @return the province level culture religion
     */
    private JSONObject getProvinceLevelCultureReligion() {
        List<Map<String, Object>> results = ModelGal.getProvinceLevelCultureReligion(epiCenter);
        _log.info("province culture religion detailed infos has " + results.size() + " items");
        JSONObject jsonObject = new JSONObject();
        if (results.size() > 0) {
            Map<String, Object> map = results.get(0);
            _log.info("language:" + map.get("language"));
            _log.info("nation:" + map.get("nation"));
            _log.info("p_capital:" + map.get("p_capital"));
            _log.info("religion:" + map.get("religion"));
            _log.info("feature:" + map.get("feature"));
            jsonObject = JSONObject.fromObject(map);
        }
        return jsonObject;
    }

    /**
     * 获取研判结果Json结果表示
     *
     * @return Json字符串
     */
    public String getJson() {
        JSONObject countryJsonObject = getCountryLevelCultureReligion();
        JSONObject provinceJsonObject = getProvinceLevelCultureReligion();
        resultJSONObject.put("country_level_culture_religion", getCountryLevelCultureReligion());
        resultJSONObject.put("province_level_culture_religion", getProvinceLevelCultureReligion());
        StringBuilder builder = new StringBuilder("国家级别文化、宗教信仰情况：" + "\n");
        builder.append("语言：" + countryJsonObject.getString("language") + "\n");
        builder.append("国庆日：" + countryJsonObject.getString("nation_day") + "\n");
        builder.append("民族：" + countryJsonObject.getString("nation") + "\n");
        builder.append("宗教信仰：" + countryJsonObject.getString("religion") + "\n");
        builder.append("特点：" + countryJsonObject.getString("feature") + "\n");
        builder.append("省级别文化、宗教信仰情况：" + "\n");
        builder.append("语言：" + provinceJsonObject.getString("language") + "\n");
        builder.append("民族：" + provinceJsonObject.getString("p_capital") + "\n");
        builder.append("宗教信仰：" + provinceJsonObject.getString("religion") + "\n");
        builder.append("特点：" + provinceJsonObject.getString("feature") + "\n");
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // GENERAL_TEXT_USV.createTextDatasetVector(eqID, "文化和宗教信仰情况",
        // builder.toString(), epiCenter.getLongitude(),
        // epiCenter.getLatitude());
        return resultJSONObject.toString();
    }
    // public static void main(String[] args) {
    // ModelInitilizer initilizer = new
    // ModelInitilizer("N20400E03830020140203110846", "");
    // CULTURE_RELIGION model = new CULTURE_RELIGION(initilizer, "");
    // System.out.println(model.getJson());
    // }

}
