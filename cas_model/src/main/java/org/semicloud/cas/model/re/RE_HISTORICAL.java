package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EditResult;

import java.util.List;
import java.util.Map;

/**
 * 重计算-震区历史地震分布模型
 */
public class RE_HISTORICAL extends RE_BASE_MODEL {

    /**
     * Instantiates a new re historical.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_HISTORICAL(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        List<Map<String, Object>> historicals = ModelGal.getHistoricalEqInfos(_center);
        JSONArray array = new JSONArray();
        _log.info("has " + historicals.size() + " history information, as follows:");

        for (int i = 0; i < historicals.size(); i++) {
            Map<String, Object> historical = historicals.get(i);
            _log.info("history earthquake, NO. " + (i + 1));
            _log.info("eqDate:" + historical.get("eq_date"));
            _log.info("eqTime:" + historical.get("eq_time"));
            _log.info("name:" + historical.get("eq_name"));
            _log.info("place:" + historical.get("eq_place"));
            _log.info("site:" + historical.get("site"));
            _log.info("mag:" + historical.get("magnitude"));
            _log.info("injury:" + historical.get("Injury"));
            _log.info("death:" + historical.get("death"));
            _log.info("fault_type:" + historical.get("fault_type"));
            _log.info("features:" + historical.get("features"));
            _log.info("influence:" + historical.get("influence"));
            _log.info("landforms:" + historical.get("landforms"));
            JSONObject object = JSONObject.fromObject(historicals.get(i));
            array.add(object);
        }
        _resultJsonObject.put("historical", array);
        return _resultJsonObject.toString();
    }
}
