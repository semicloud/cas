package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.utils.common.Convert;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * USGS经济损失（县级市）研判模型
 *
 * @author Semicloud
 */
public class ECONOMIC_USGS_COUNTY extends BaseModel {

    /**
     * 正态分布计算类
     */
    private static NormalDistribution _distribution = new NormalDistribution();

    /**
     * 经济损失模型所需的参数
     */
    private ECONOMIC_USGS_PARAM _parameter;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public ECONOMIC_USGS_COUNTY(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
        _parameter = ECONOMIC_USGS_PARAM.lookup(getCountryAttribute().getCountryAbbr());
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
     * @param population 人口数
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
     * 获得烈度圈集合中烈度大于8的烈度圈，否则计算范围太大，计算时间太长
     *
     * @return the key circles
     */
    public List<IntensityCircle> getKeyCircles() {
        List<IntensityCircle> sources = getCircles();
        Predicate predicate = new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                IntensityCircle circle = (IntensityCircle) object;
                return circle.getIntensity() >= Settings.getEconomicKeyIntensity();
            }
        };
        @SuppressWarnings("unchecked")
        Collection<IntensityCircle> collection = CollectionUtils.select(sources, predicate);
        List<IntensityCircle> results = new ArrayList<IntensityCircle>(collection);
        return results;
    }

    /**
     * 获得县级市的经济损失情况
     *
     * @return 返回值Map<县级市名称，县级市经济损失>
     */
    private Map<String, Double> getCountyEconomicLosses() {
        Map<String, Double> countiesEconomicLoss = new HashMap<String, Double>();
        List<IntensityCircle> circles = getKeyCircles();
        Transformer transformer = new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((IntensityCircle) input).getIntensity();
            }
        };
        @SuppressWarnings("unchecked")
        Collection<Float> intensities = CollectionUtils.collect(circles, transformer);
        _log.info("compute county economic losses under intensity " + StringUtils.join(intensities, ','));

        Map<Float, Map<String, Double>> allIntensities = ModelGal.getCountyPopulationUnderIntensity(circles);
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
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        double totalEconomicLosses = 0;
        if (getCountryAttribute().getCountryAbbr().equals("CN")) {
            Map<String, Double> countyEconomicLosses = getCountyEconomicLosses();
            for (Entry<String, Double> lossEntry : countyEconomicLosses.entrySet()) {
                JSONObject jsonObject = new JSONObject();

                String county = lossEntry.getKey();
                double loss = lossEntry.getValue();
                DecimalFormat format = new DecimalFormat("#0.00");
                loss = Double.parseDouble(format.format(loss)) * 6.5;
                // 6.5是美元兑人民币的汇率
                int smid = ModelGal.getCountySMID(county);
                _log.info(text("{0} smid is {1}", county, smid));

                double[] inner = ModelGal.getCountyInnerPoint(county);
                _log.info(text("{0}, inner point is ({1},{2})", county, inner[0], inner[1]));

                jsonObject.put("name", county);
                jsonObject.put("loss", loss);
                jsonObject.put("unit", "百万元人民币");
                jsonObject.put("smid", smid);
                jsonObject.put("x", inner[0]);
                jsonObject.put("y", inner[1]);

                jsonArray.add(jsonObject);
                totalEconomicLosses += loss;
            }
        } else {
            _log.warn("out of CHINA, so I can not compute county economic losses. Sorry.");
        }
        resultJSONObject.put("economicLosses", jsonArray);
        resultJSONObject.put("totalEconomicLosses", totalEconomicLosses);
        _log.info("total economic losses, " + totalEconomicLosses + ", million dollars");
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // ECONOMIC_COUNTY_USV.createEconomicCountyDatasetVector(eqID,
        // resultJSONObject);
        // ECONOMIC_COUNTY_TEXT_USV.createEconomicCountyText(eqID,
        // resultJSONObject);
        return resultJSONObject.toString();
    }

    // public static void main(String[] args) {
    // ModelInitializer initilizer = new
    // ModelInitializer("N30300E10170020141122165550", "");
    // ECONOMIC_USGS_COUNTY county = new ECONOMIC_USGS_COUNTY(initilizer, "");
    // System.out.println(county.getJson());
    // }
}
