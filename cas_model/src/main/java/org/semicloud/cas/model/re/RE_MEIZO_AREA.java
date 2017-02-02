package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;

import java.util.List;
import java.util.Map;

/**
 * 重计算-极震区分析模型
 */
public class RE_MEIZO_AREA extends RE_BASE_MODEL {

    /**
     * Instantiates a new re meizo area.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_MEIZO_AREA(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        logStart();
        EditRegion region = _editResult.getRegions().get(0);
        List<Map<String, Object>> counties = ModelGal.getMeizoseismalAreaInfos(region);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> map : counties) {
            JSONObject object = JSONObject.fromObject(map);
            jsonArray.add(object);
        }
        _resultJsonObject.put("rescue_cities", jsonArray);
        logEnd();
        return _resultJsonObject.toString();
    }

}
