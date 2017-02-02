package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.us.ECONOMIC_USGS_PARAM;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.utils.common.Convert;

import java.util.List;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 重计算-USGS经济损失模型
 */
public class RE_ECONOMIC_USGS extends RE_BASE_MODEL {

    /**
     * The _distribution.
     */
    private static NormalDistribution _distribution = new NormalDistribution();

    /**
     * The _parameter.
     */
    private ECONOMIC_USGS_PARAM _parameter;

    /**
     * Instantiates a new re economic usgs.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_ECONOMIC_USGS(EditResult editResult, String modelName) {
        super(editResult, modelName);
        _parameter = ECONOMIC_USGS_PARAM.lookup(getCountryAbbr());
        _log.info("load economic parameter ");
        if (_parameter != null) {
            _log.info("country:" + _parameter.getCountryName());
            _log.info("theta:" + _parameter.getTheta());
            _log.info("beta:" + _parameter.getBeta());
            _log.info("gdp:" + _parameter.getGdpPerPeople());
        } else {
            _log.error("economic parameter seems like null!");
        }
    }

    /**
     * 计算烈度s下的经济损失率r(s).
     *
     * @param iny 烈度
     * @return double
     */
    private double getRatio(float iny) {
        double ratio = _distribution.cumulativeProbability((1.0f / _parameter.getBeta())
                * FastMath.log(iny / _parameter.getTheta()));
        _log.info(text("compute ratio under intensity {0}, is {1}", iny, ratio));
        return ratio;
    }

    /**
     * Gets the gdp.
     *
     * @param population the population
     * @return the gdp
     */
    private double getGdp(double population) {
        double gdp = population * _parameter.getGdpPerPeople();
        _log.info(text("population is {0}, gdp per people is {1}, total gdp is {2}", population,
                _parameter.getGdpPerPeople(), gdp));
        return gdp;
    }

    /**
     * Gets the economic exposure.
     *
     * @param population the population
     * @return the economic exposure
     */
    private double getEconomicExposure(double population) {
        double ee = _parameter.getAlpha() * getGdp(population);
        _log.info(text("compute economic explorer {0} ", ee));
        return ee;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        double total = 0;
        JSONArray jsonArray = new JSONArray();
        List<EditRegion> regions = _editResult.getRegions();
        for (EditRegion region : regions) {
            JSONObject jsonObject = new JSONObject();
            float intensity = region.getIntensity();
            double population = ModelGal.getPopulationNumberUnderRegion(region);
            double loss = getEconomicExposure(population) * getRatio(intensity);
            double unit = Convert.toDouble(loss / 1000000, "#0.00");
            _log.info(text("under intensity {0}, {1} people, economic loss {2} million dollars", intensity, population,
                    unit));
            jsonObject.put("intensity", intensity);
            jsonObject.put("longitude", _keyRegion.getLongitude());
            jsonObject.put("latitude", _keyRegion.getLatitude());
            jsonObject.put("economic_losses", unit);
            jsonObject.put("points", getRegionPoints(intensity));
            total += loss;
            jsonArray.add(jsonObject);
        }
        _resultJsonObject.put("economic", jsonArray);
        double totalUnit = Convert.toDouble(total / 1000000, "#0.00");
        _resultJsonObject.put("total_economic_losses", totalUnit);
        return _resultJsonObject.toString();
    }
}
