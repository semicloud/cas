package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.utils.common.Convert;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * USGS经济损失模型
 */
public class ECONOMIC_USGS extends BaseModel {
    /**
     * 正态分布
     */
    private static final NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution();
    /**
     * 计算参数
     */
    private final ECONOMIC_USGS_PARAM parameter = ECONOMIC_USGS_PARAM.lookup(getCountryAttribute().getCountryAbbr());

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public ECONOMIC_USGS(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 计算烈度s下的经济损失率r(s).
     *
     * @param intensity 烈度
     * @return double
     */
    private double getRatio(float intensity) {
        double ratio = NORMAL_DISTRIBUTION.cumulativeProbability((1.0f / parameter.getBeta())
                * Math.log(((float) intensity) / parameter.getTheta()));
        _log.info(text("compute ratio under intensity {0} is {1}", intensity, ratio));
        return ratio;
    }

    /**
     * 获得烈度s下的总GDP.
     *
     * @param population 烈度s下的总人口数
     * @return double
     */
    public double getGDP(double population) {
        return population * parameter.getGdpPerPeople();
    }

    /**
     * 计算烈度s下的EconomicExposure的值.
     *
     * @param population 烈度s下的总人口数
     * @return double
     */
    public double getEcomomicExposure(double population) {
        return parameter.getAlpha() * getGDP(population);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        double totalEconomicLoss = 0.0d;
        JSONArray jsonArray = new JSONArray();
        for (float intensity = START_INTENSITY; intensity <= epiIntensity; intensity += 0.5) {
            JSONObject jsonObject = new JSONObject();
            IntensityCircle circle = getIntensityCircle(intensity);
            double[] epiCenter = ModelGal.getProjection(circle.getEpiCenter());
            double population = ModelGal.getPopulationNumber(circle);
            double economicLoss = getEcomomicExposure(population) * getRatio(intensity);
            double unit = Convert.toDouble(economicLoss / 1000000, "0.00") * 6.5;// 单位为百万美元
            jsonObject.put("intensity", circle.getIntensity());
            _log.info("intensity:" + intensity);

            jsonObject.put("longitude", epiCenter[0]);
            jsonObject.put("latitude", epiCenter[1]);
            _log.info("lng, lat:" + epiCenter[0] + ", " + epiCenter[1]);

            jsonObject.put("longAxis", Math.rint(circle.getLongAxis()));
            jsonObject.put("shortAxis", Math.rint(circle.getShortAxis()));
            jsonObject.put("azimuth", circle.getAzimuth());

            jsonObject.put("economic_losses", unit);
            _log.info("economic losses:" + unit);
            totalEconomicLoss += economicLoss;
            jsonArray.add(jsonObject);
        }
        resultJSONObject.put("economic", jsonArray);
        double totalLossUnit = Convert.toDouble(totalEconomicLoss / 1000000, "0.00") * 6.5;
        resultJSONObject.put("total_economic_losses", totalLossUnit);
        _log.info("economic_losses:" + totalLossUnit);
        return resultJSONObject.toString();
    }
}
