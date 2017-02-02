package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;

import java.util.List;

/**
 * 重计算-地震烈度模型
 */
public class RE_INTENSITY extends RE_BASE_MODEL {

    /**
     * Instantiates a new re intensity.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_INTENSITY(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        List<EditRegion> regions = _editResult.getRegions();
        JSONArray jsonArray = new JSONArray();
        for (EditRegion region : regions) {
            JSONObject jsonObject = new JSONObject();
            float intensity = region.getIntensity();
            jsonObject.put("intensity", intensity);
            jsonObject.put("longitude", _keyRegion.getLongitude());
            jsonObject.put("latitude", _keyRegion.getLatitude());
            jsonObject.put("points", getRegionPoints(intensity));
            jsonArray.add(jsonObject);
        }
        _resultJsonObject.put("circles", jsonArray);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // INTENSITY_RECOMPUTE_USV.createIntensityReComputeDatasetVector(_eqID,
        // _taskID, regions);
        return _resultJsonObject.toString();
    }

}
