package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.intensity.IntensityCircle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * USGS人口伤亡（县级市）研判模型
 */
public class CASUALTY_USGS_COUNTY extends BaseModel {
    /**
     * 正态分布计算类
     */
    private static final NormalDistribution DISTRIBUTION = new NormalDistribution();
    /**
     * USGS PAGER模型参数类
     */
    private final CASUALTY_USGS_PARAM PARAMETER = CASUALTY_USGS_PARAM.lookup(getCountryAttribute().getCountryAbbr());
    /**
     * 参数theta
     */
    private final double THETA = PARAMETER.getTheta();
    /**
     * 参数beta
     */
    private final double BETA = PARAMETER.getBeta();

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public CASUALTY_USGS_COUNTY(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 计算某烈度值下的人口死亡率.
     *
     * @param s 烈度值
     * @return 烈度s下的人口死亡率
     */
    private double getRatio(float s) {
        double ratio = DISTRIBUTION.cumulativeProbability(1 / BETA * Math.log(s / THETA));
        _log.info(text("compute ratio under intensity {0} is {1}", s, ratio));
        return ratio;
    }

    /**
     * 获得每个县级市的死亡人数.
     *
     * @param countyPopInfosUnderIny 每个烈度下，每个县级市的人口数量
     * @return 每个县级市的人口死亡数 Map<String,FatalityInfo>
     */
    private Map<String, FatalityInfo> getCountyFatalityInfos(Map<Float, Map<String, Double>> countyPopInfosUnderIny) {
        Map<String, FatalityInfo> fatalities = new HashMap<String, FatalityInfo>();
        _log.info("summary county fatalities...");
        for (Entry<Float, Map<String, Double>> allIntensities : countyPopInfosUnderIny.entrySet()) {
            float intensity = allIntensities.getKey();
            _log.info(text("intensity {0}", intensity));

            double ratio = getRatio(intensity);

            Map<String, Double> allPopulations = allIntensities.getValue();
            _log.info(text("{0} counties under intensity {1}, now compute them", allPopulations.size(), intensity));

            for (Entry<String, Double> counties : allPopulations.entrySet()) {
                String countyName = counties.getKey();
                double populationNumber = counties.getValue();
                double deathNumber = populationNumber * ratio;
                _log.info(text("county:{0}, population:{1}, ratio:{2}, death:{3}", countyName, populationNumber, ratio,
                        deathNumber));
                FatalityInfo fatality = new FatalityInfo();
                fatality.setPopulationNumber(populationNumber);
                fatality.setDeathNumber(deathNumber);
                if (!fatalities.containsKey(countyName)) {
                    fatalities.put(countyName, fatality);
                    _log.info(text("put new {0},death {1} to collection", countyName, fatality.getDeathNumber()));
                } else {
                    FatalityInfo oldFi = fatalities.get(countyName);
                    double oldPop = oldFi.getPopulationNumber();
                    double oldDeath = oldFi.getDeathNumber();
                    double newPop = oldPop + fatality.getPopulationNumber();
                    double newDeath = oldDeath + fatality.getDeathNumber();
                    oldFi.setPopulationNumber(newPop);
                    oldFi.setDeathNumber(newDeath);
                    _log.info(text("county {0} already exist, update, pop:{1}->{2},death:{3}->{4}", countyName, oldPop,
                            newPop, oldDeath, newDeath));
                }
            }
        }
        return fatalities;
    }

    /**
     * 去掉低烈度地区的烈度圈，避免计算范围太大
     *
     * @param source 烈度圈序列
     * @return the list
     */
    private List<IntensityCircle> removeLowerIntensityCircles(List<IntensityCircle> source) {
        List<IntensityCircle> answer = source;
        if (answer != null) {
            Iterator<IntensityCircle> itor = answer.iterator();
            while (itor.hasNext()) {
                IntensityCircle tmp = itor.next();
                if (tmp.getIntensity() < 8.0) {
                    itor.remove();
                }
            }
        }
        return answer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        double totalPopulation = 0, totalDeathNumber = 0;
        if (getCountryAttribute().getCountryAbbr().equals("CN")) {
            // 只统计8度圈之内的死亡人口数
            List<IntensityCircle> intensityCircles = getCircles().stream().filter(c -> c.getIntensity() >= 8.0)
                    .collect(Collectors.toList());
            List<Float> intensities = intensityCircles.stream().map(c -> c.getIntensity()).collect(Collectors.toList());
            // final List<IntensityCircle> circles =
            // removeLowerIntensityCircles(getCircles());
            // Transformer transformer = new Transformer() {
            // @Override
            // public Object transform(Object input) {
            // IntensityCircle circle = (IntensityCircle) input;
            // return circle.getIntensity();
            // }
            // };
            // @SuppressWarnings("unchecked")
            // Collection<Object> intensities = CollectionUtils.collect(circles,
            // transformer);
            _log.info("compute county death info under intensity " + StringUtils.join(intensities, ','));

            final Map<Float, Map<String, Double>> populationInfos = ModelGal
                    .getCountyPopulationUnderIntensity(intensityCircles);
            final Map<String, FatalityInfo> fatalities = getCountyFatalityInfos(populationInfos);

            _log.info("Result:");
            for (Entry<String, FatalityInfo> fatality : fatalities.entrySet()) {
                JSONObject jsonObject = new JSONObject();
                String countyName = fatality.getKey();
                double population = Math.round(fatality.getValue().getPopulationNumber());
                double death = Math.round(fatality.getValue().getDeathNumber());
                int smid = ModelGal.getCountySMID(countyName);

                jsonObject.put("name", countyName);
                _log.info("name:" + countyName);

                jsonObject.put("population", population);
                _log.info("population:" + population);

                jsonObject.put("deathNumber", death);
                _log.info("death:" + death);

                jsonObject.put("SMID", smid);
                _log.info("SMID:" + smid);

                double[] innerpoint = ModelGal.getCountyInnerPoint(countyName);
                jsonObject.put("x", innerpoint[0]);
                jsonObject.put("y", innerpoint[1]);
                _log.info(text("inner point:({0},{1})", innerpoint[0], innerpoint[1]));

                jsonArray.add(jsonObject);
                totalPopulation += population;
                totalDeathNumber += death;
            }
            // boolean stored = ModelDal.saveDeathAndPopulationNumber(eqID,
            // totalDeathNumber, totalPopulation);
        } else {
            _log.warn("out of CHINA, so I can not compute county casualties. Sorry.");
        }
        resultJSONObject.put("casualties", jsonArray);
        resultJSONObject.put("totalDeathNumber", totalDeathNumber);
        resultJSONObject.put("totalPopulation", totalPopulation);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // CASUALTY_COUNTY_USV.createCasualtyCounty(eqID, resultJSONObject);
        // CASUALTY_COUNTY_TEXT_USV.createCasualtyCountyText(eqID,
        // resultJSONObject);
        _log.info(text("total population:{0}, totalDeathNumber:{1}", totalPopulation, totalDeathNumber));
        return resultJSONObject.toString();
    }
}

class FatalityInfo {
    private double populationNumber;
    private double deathNumber;
    private double ratio;

    public double getPopulationNumber() {
        return populationNumber;
    }

    public void setPopulationNumber(double populationNumber) {
        this.populationNumber = populationNumber;
    }

    public double getDeathNumber() {
        return deathNumber;
    }

    public void setDeathNumber(double deathNumber) {
        this.deathNumber = deathNumber;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "CountyFatalityInfo [populationNumber=" + populationNumber + ", deathNumber=" + deathNumber + ", ratio="
                + ratio + "]";
    }
}
