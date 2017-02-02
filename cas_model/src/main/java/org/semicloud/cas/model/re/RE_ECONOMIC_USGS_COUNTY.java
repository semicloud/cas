package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.us.ECONOMIC_USGS_PARAM;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;

import java.util.*;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 重计算-USGS经济损失（县级市）模型.
 *
 * @author Semicloud
 */
public class RE_ECONOMIC_USGS_COUNTY extends RE_BASE_MODEL {

    /**
     * 正态分布
     */
    private static NormalDistribution _distribution = new NormalDistribution();

    /**
     * 参数集合
     */
    private ECONOMIC_USGS_PARAM _parameter;

    /**
     * Instantiates a new re economic usgs county.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_ECONOMIC_USGS_COUNTY(EditResult editResult, String modelName) {
        super(editResult, modelName);
        _parameter = ECONOMIC_USGS_PARAM.lookup(getCountryAbbr());
    }

    /**
     * 计算烈度intensity下的损失率
     *
     * @param intensity the intensity
     * @return the ratio
     */
    private double getRatio(float intensity) {
        double ratio = _distribution.cumulativeProbability((1.0f / _parameter.getBeta())
                * Math.log(((float) intensity) / _parameter.getTheta()));
        _log.info(text("compute ratio under intensity {0} is {1}", intensity, ratio));
        return ratio;
    }

    /**
     * 通过人均GDP和人口数计算总的GDP
     *
     * @param population the population
     * @return the gdp
     */
    private double getGDP(double population) {
        return population * _parameter.getGdpPerPeople();
    }

    /**
     * 通过人口数计算EconomicExposure
     *
     * @param population the population
     * @return the ecomomic exposure
     */
    private double getEcomomicExposure(double population) {
        return _parameter.getAlpha() * getGDP(population);
    }

    /**
     * 获得烈度圈集合中烈度大于8的编辑区域，否则计算范围太大，计算时间太长
     *
     * @return the key regions
     */
    public List<EditRegion> getKeyRegions() {
        List<EditRegion> sources = _editResult.getRegions();
        Predicate predicate = new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                EditRegion region = (EditRegion) object;
                return region.getIntensity() >= Settings.getEconomicKeyIntensity();
            }
        };
        @SuppressWarnings("unchecked")
        Collection<EditRegion> collection = CollectionUtils.select(sources, predicate);
        List<EditRegion> results = new ArrayList<EditRegion>(collection);
        return results;
    }

    /**
     * 获得县级市经济损失模型
     *
     * @return the county economic losses
     */
    private Map<String, Double> getCountyEconomicLosses() {
        Map<String, Double> countiesEconomicLoss = new HashMap<String, Double>();
        List<EditRegion> regions = getKeyRegions();
        Transformer transformer = new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((EditRegion) input).getIntensity();
            }
        };
        @SuppressWarnings("unchecked")
        Collection<Float> intensities = CollectionUtils.collect(regions, transformer);
        _log.info("compute county economic losses under intensity " + StringUtils.join(intensities, ','));

        Map<Float, Map<String, Double>> allIntensities = ModelGal.getCountyPopulationUnderEditRegion(regions);
        for (Entry<Float, Map<String, Double>> intensityEntry : allIntensities.entrySet()) {
            float intensity = intensityEntry.getKey();
            Map<String, Double> countiesPopulation = intensityEntry.getValue();
            for (Entry<String, Double> countyEntry : countiesPopulation.entrySet()) {
                String county = countyEntry.getKey();
                double population = countyEntry.getValue();
                double loss = getEcomomicExposure(population) * getRatio(intensity);
                double unit = Convert.toDouble((loss / 1000000), "#0.00");
                if (!countiesEconomicLoss.containsKey(county)) {
                    countiesEconomicLoss.put(county, unit);
                    _log.info(text("put new economic loss {0}, {1} to collection", county, unit));
                } else {
                    double oldUnit = countiesEconomicLoss.get(county);
                    double newUnit = unit + oldUnit;
                    countiesEconomicLoss.put(county, newUnit);
                    _log.info(text("county {0} already existed, update, unit:{1}->{2}", county, oldUnit, newUnit));
                }
            }
        }
        return countiesEconomicLoss;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        double totalEconomicLosses = 0;
        if (getCountryAbbr().equals("CN")) {
            Map<String, Double> countyEconomicLosses = getCountyEconomicLosses();
            for (Entry<String, Double> lossEntry : countyEconomicLosses.entrySet()) {
                JSONObject jsonObject = new JSONObject();

                String county = lossEntry.getKey();
                double loss = lossEntry.getValue();

                int smid = ModelGal.getCountySMID(county);
                _log.info(text("{0} smid is {1}", county, smid));

                double[] inner = ModelGal.getCountyInnerPoint(county);
                _log.info(text("{0}, inner point is ({1},{2})", county, inner[0], inner[1]));

                jsonObject.put("name", county);
                jsonObject.put("loss", loss);
                jsonObject.put("unit", "百万美元");
                jsonObject.put("smid", smid);
                jsonObject.put("x", inner[0]);
                jsonObject.put("y", inner[1]);

                jsonArray.add(jsonObject);
                totalEconomicLosses += loss;
            }
        } else {
            _log.warn("out of CHINA, so I can not compute county economic losses. Sorry.");
        }
        _resultJsonObject.put("economicLosses", jsonArray);
        _resultJsonObject.put("totalEconomicLosses", totalEconomicLosses);
        _log.info("total economic losses, " + totalEconomicLosses + ", million dollars");
        return _resultJsonObject.toString();
    }

}
