package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.attribute.CountryAttribute;
import org.semicloud.cas.model.us.CASUALTY_USGS_PARAM;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.utils.common.Convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 重计算-USGS人口伤亡模型
 */
public class RE_CASYALTY_USGS extends RE_BASE_MODEL {

    /**
     * The _distribution.
     */
    private static NormalDistribution _distribution = new NormalDistribution();

    /**
     * The _theta.
     */
    private double _theta;

    /**
     * The _beta.
     */
    private double _beta;

    /**
     * Instantiates a new re casyalty usgs.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_CASYALTY_USGS(EditResult editResult, String modelName) {
        super(editResult, modelName);
        double lng = _keyRegion.getLongitude();
        double lat = _keyRegion.getLatitude();
        EpiCenter center = ModelGal.getVerseEpiCenter(lng, lat);
        CountryAttribute ca = CountryAttribute.lookup(center);
        CASUALTY_USGS_PARAM param = CASUALTY_USGS_PARAM.lookup(ca.getCountryAbbr());
        _theta = param.getTheta();
        _beta = param.getBeta();
    }

    /**
     * 计算该烈度下人口死亡率.
     *
     * @param iny 烈度值
     * @return the ratio
     */
    protected double getRatio(float iny) {
        double ratio = _distribution.cumulativeProbability((1 / _beta) * FastMath.log(iny / _theta));
        return ratio;
    }

    /**
     * 获得人口数据.
     *
     * @return the population
     */
    public Map<Float, Double> getPopulation() {
        Map<Float, Double> map = new HashMap<Float, Double>();
        for (EditRegion region : _editResult.getRegions()) {
            map.put(region.getIntensity(), ModelGal.getPopulationNumberUnderRegion(region));
        }
        _log.info("population under region:" + map);
        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        logStart();
        Map<Float, Double> map = getPopulation();
        JSONArray jsonArray = new JSONArray();
        double totalPopulation = 0, totalDeath = 0;

        for (Entry<Float, Double> entry : map.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            final float iny = entry.getKey();
            double population = entry.getValue();
            double ratio = Convert.toDouble(getRatio(iny), "#0.000");
            double death = Convert.toInteger(population * ratio);
            jsonObject.put("ratio", ratio);
            jsonObject.put("population", population);
            jsonObject.put("death", death);
            jsonObject.put("intensity", iny);
            _log.info(text("intensity:{0},population:{1},ratio:{2},death:{3}", iny, population, ratio, death));

            totalPopulation += population;
            totalDeath += death;
            jsonObject.put("points", getRegionPoints(iny));

            jsonArray.add(jsonObject);
        }
        _resultJsonObject.put("totalPopulation", totalPopulation);
        _resultJsonObject.put("totalDeathNumber", totalDeath);
        _resultJsonObject.put("casualties", jsonArray);
        _log.info(text("for all, total population is {0}, and death {1} peoples. what a bad day...", totalPopulation,
                totalDeath));
        logEnd();
        return _resultJsonObject.toString();
    }
}
