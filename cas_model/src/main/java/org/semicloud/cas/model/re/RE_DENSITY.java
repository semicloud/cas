package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;

import java.util.List;

/**
 * 重计算-人口密度分析模型
 */
public class RE_DENSITY extends RE_BASE_MODEL {

    /**
     * Instantiates a new re density.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_DENSITY(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    //好像是需要修改
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        List<EditRegion> regions = _editResult.getRegions();
        for (EditRegion region : regions) {
            JSONObject jsonObject = new JSONObject();
            float intensity = region.getIntensity();
            _log.info("intensity:" + intensity);

            double density = ModelGal.getPopulationDensity(region);
            _log.info("density:" + density + "p/m^2");

            JSONArray points = getRegionPoints(region.getIntensity());
            _log.info("region has " + points.size() + "points");
            jsonObject.put("intensity", region.getIntensity());
            jsonObject.put("longitude", _keyRegion.getLongitude());
            jsonObject.put("latitude", _keyRegion.getLatitude());
            jsonObject.put("density", FastMath.rint(density));
            jsonObject.put("points", points);
            jsonArray.add(jsonObject);
        }
        _resultJsonObject.put("pop_density", jsonArray);
        return _resultJsonObject.toString();
    }

}
