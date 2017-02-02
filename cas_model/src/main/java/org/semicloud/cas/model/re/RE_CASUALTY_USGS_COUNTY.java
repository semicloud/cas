package org.semicloud.cas.model.re;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 重计算-USGS PAGER 人口死亡计算模型.
 *
 * @author Semicloud
 */
public class RE_CASUALTY_USGS_COUNTY extends RE_CASYALTY_USGS {

    /**
     * Instantiates a new re casualty usgs county.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_CASUALTY_USGS_COUNTY(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /**
     * 获取县级市的人口死亡数据.
     *
     * @param populationMap 某烈度下县级市的人口数据
     * @return the county fatalities
     */
    private Map<String, FatalityInfo> getCountyFatalities(Map<Float, Map<String, Double>> populationMap) {
        Map<String, FatalityInfo> fatalities = new HashMap<String, FatalityInfo>();

        for (Entry<Float, Map<String, Double>> intensityPopulation : populationMap.entrySet()) {
            float intensity = intensityPopulation.getKey();
            _log.info(text("compute fatality under intensity {0}", intensity));
            double ratio = getRatio(intensity);
            _log.info(text("death ratio of intensity {0} is {1}", intensity, ratio));

            Map<String, Double> countyPopulation = intensityPopulation.getValue();
            _log.info(text("the population info under intensity {0} as follows:", intensity));

            for (Entry<String, Double> countyPopEntry : countyPopulation.entrySet()) {
                String county = countyPopEntry.getKey();
                double population = countyPopEntry.getValue();
                double death = population * ratio;
                _log.info(text("county name:{0}, population:{1}, ratio{2}, death:{3}", county, population, ratio, death));

                FatalityInfo fi = new FatalityInfo();
                fi.setPopulationNumber(population);
                fi.setDeathNumber(death);

                if (!fatalities.containsKey(county)) {
                    fatalities.put(county, fi);
                    _log.info(text("add new {0} death {1} to collection", county, death));
                } else {
                    _log.info(text("add {0} death {1} to collection", county, death));
                    FatalityInfo fatality = fatalities.get(county);
                    double oldPop = fatality.getPopulationNumber();
                    double oldDeath = fatality.getDeathNumber();
                    fatality.setPopulationNumber(oldPop + fi.getPopulationNumber());
                    fatality.setDeathNumber(oldDeath + fi.getDeathNumber());
                    _log.info(text("update {0} population from {1} to {2}, death number from {3} to {4}", county,
                            oldPop, fatality.getPopulationNumber(), oldDeath, fatality.getDeathNumber()));
                }
            }
        }
        return fatalities;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_CASYALTY_USGS#getJson()
     */
    @Override
    public String getJson() {
        JSONArray jsonArray = new JSONArray();
        double totalPopulation = 0, totalDeathNumber = 0;
        if (getCountryAbbr().equals("CN")) {
            // 只统计8度圈之内的死亡人口数
            Predicate p = new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    EditRegion region = (EditRegion) object;
                    return region.getIntensity() - 8.0 >= 0;
                }
            };
            @SuppressWarnings("unchecked")
            Collection<EditRegion> regions = CollectionUtils.select(_editResult.getRegions(), p);
            _log.info("find EditRegion where intensity greater than 8.0, seems like " + regions.size() + " regions");

            Map<Float, Map<String, Double>> populationInfos = ModelGal
                    .getCountyPopulationUnderEditRegion(new ArrayList<EditRegion>(regions));
            _log.info("population info as follows:");
            for (Entry<Float, Map<String, Double>> e1 : populationInfos.entrySet()) {
                _log.info("intensity:" + e1.getKey() + StringUtils.repeat("-", 30));
                for (Entry<String, Double> e2 : e1.getValue().entrySet()) {
                    _log.info(text("county name:{0},population{1}.", e2.getKey(), e2.getValue()));
                }
            }

            Map<String, FatalityInfo> fatalities = getCountyFatalities(populationInfos);
            _log.info("compute county casualty as follows:");
            for (Entry<String, FatalityInfo> fatality : fatalities.entrySet()) {
                JSONObject jsonObject = new JSONObject();
                String county = fatality.getKey();
                double population = FastMath.round(fatality.getValue().getPopulationNumber());
                double death = FastMath.round(fatality.getValue().getDeathNumber());
                int smID = ModelGal.getCountySMID(county);
                double[] innerpoint = ModelGal.getCountyInnerPoint(county);
                jsonObject.put("name", county);
                jsonObject.put("population", population);
                jsonObject.put("deathNumber", death);
                jsonObject.put("SMID", smID);
                jsonObject.put("x", innerpoint[0]);
                jsonObject.put("y", innerpoint[1]);
                _log.info(text("{0},population {1} , death {2}, SMID:{3}, innerPoint({4},{5})", county, population,
                        death, smID, innerpoint[0], innerpoint[1]));
                jsonArray.add(jsonObject);
                totalPopulation += population;
                totalDeathNumber += death;
            }
        } else {
            _log.warn(text("there is no county dataset vector at {0}, model compute failed.", getCountryAbbr()));
        }
        _resultJsonObject.put("casualties", jsonArray);
        _resultJsonObject.put("totalDeathNumber", totalDeathNumber);
        _resultJsonObject.put("totalPopulation", totalPopulation);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // CASUALTY_RECOMPUTE_COUNTY_REGION_USV.createCasualtyCounty(_eqID,
        // _taskID, _resultJsonObject);
        // CASUALTY_RECOMPUTE_COUNTY_REGION_USV.createCasualtyCountyText(_eqID,
        // _taskID, _resultJsonObject);
        _log.info(text("for all: totalPopulation {0},totalDeath: {1}", totalPopulation, totalDeathNumber));
        return _resultJsonObject.toString();
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
