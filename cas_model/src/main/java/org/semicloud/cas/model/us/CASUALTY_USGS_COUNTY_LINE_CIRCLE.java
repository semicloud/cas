package org.semicloud.cas.model.us;

import com.supermap.data.Point2D;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.us.entity.FatalityInfo;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.intensity.IntensityLineCircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 线源模型的县级市人口伤亡数据集
 * Created by Administrator on 2017/2/7.
 */
public class CASUALTY_USGS_COUNTY_LINE_CIRCLE extends BaseModel {
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
     * 构造函数.
     *
     * @param initilizer ModelInitilizer对象
     * @param modelName  模型名称
     */
    public CASUALTY_USGS_COUNTY_LINE_CIRCLE(ModelInitializer initilizer, String modelName) {
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
        _log.info(text("烈度{0}下人口死亡率为：{1}", s, ratio));
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
        _log.info("统计线源烈度模型下县级市的人口损失...");
        for (Map.Entry<Float, Map<String, Double>> allIntensities : countyPopInfosUnderIny.entrySet()) {
            float intensity = allIntensities.getKey();
            _log.info(text("烈度值：{0}", intensity));

            double ratio = getRatio(intensity);

            Map<String, Double> allPopulations = allIntensities.getValue();
            _log.info(text("烈度{0}的烈度圈中共有{1}个县：", intensity, allPopulations.size()));

            for (Map.Entry<String, Double> counties : allPopulations.entrySet()) {
                String countyName = counties.getKey();
                double populationNumber = counties.getValue();
                double deathNumber = populationNumber * ratio;
                _log.info(text("县名:{0}, 人口数:{1}, 死亡率:{2}, 死亡数:{3}", countyName, populationNumber, ratio,
                        deathNumber));
                FatalityInfo fatality = new FatalityInfo();
                fatality.setPopulationNumber(populationNumber);
                fatality.setDeathNumber(deathNumber);
                if (!fatalities.containsKey(countyName)) {
                    fatalities.put(countyName, fatality);
                    _log.info(text("新加入：{0}，预估死亡人数：{1}", countyName, fatality.getDeathNumber()));
                } else {
                    FatalityInfo oldFi = fatalities.get(countyName);
                    double oldPop = oldFi.getPopulationNumber();
                    double oldDeath = oldFi.getDeathNumber();
                    double newPop = oldPop + fatality.getPopulationNumber();
                    double newDeath = oldDeath + fatality.getDeathNumber();
                    oldFi.setPopulationNumber(newPop);
                    oldFi.setDeathNumber(newDeath);
                    _log.debug(text("叠加：{0}，人口数：{1}->{2}，预估死亡人数：{3}->{4}", countyName, oldPop,
                            newPop, oldDeath, newDeath));
                }
            }
        }
        return fatalities;
    }

    /**
     * 获取线源烈度圈的测试方法
     *
     * @return
     */
    @Deprecated
    private List<IntensityLineCircle> getIntensityLineCircles() {
        EpiCenter center = new EpiCenter(103.1f, 33.3f);
        double[] proj = ModelGal.getProjection(center);
        Point2D projection = new Point2D(proj[0], proj[1]);
        IntensityLineCircle ilc1 = new IntensityLineCircle(center, projection,
                300 * 1000, 100 * 1000, 60, 9);
        IntensityLineCircle ilc2 = new IntensityLineCircle(center, projection,
                200 * 1000, 50 * 1000, 60, 10);
        IntensityLineCircle ilc3 = new IntensityLineCircle(center, projection,
                100 * 1000, 25 * 1000, 60, 11);
        List<IntensityLineCircle> intensityLineCircles = new ArrayList<>();
        intensityLineCircles.add(ilc1);
        intensityLineCircles.add(ilc2);
        intensityLineCircles.add(ilc3);
        return intensityLineCircles;
    }


    /**
     * 获得线源烈度的县级市人口死亡结果
     *
     * @return
     */
    @Override
    public String getJson() {
        if (this.magnitude >= 7.5 && getCountryAttribute().getCountryAbbr().equals("CN")) {
            JSONArray jsonArray = new JSONArray();
            double totalPopulation = 0, totalDeathNumber = 0;
            double[] projection = ModelGal.getProjection(epiCenter);
            if (getCountryAttribute().getCountryAbbr().equals("CN")) {
                // 只统计8度圈之内的死亡人口数，获取8度圈以上的线源烈度圈
                List<IntensityLineCircle> intensityCircles = IntensityLineCircle.getIntensityLineCircles(epiCenter,
                        new Point2D(projection[0], projection[1]), magnitude, depth, 0.5f).stream()
                        .filter(c -> c.getIntensity() >= 8.0).collect(Collectors.toList());
                // 显示一下过滤后的烈度值，即8度以上的
                List<Float> intensities = intensityCircles.stream().
                        map(IntensityLineCircle::getIntensity).collect(Collectors.toList());
                _log.info("计算以下烈度的人口伤亡数据（基于线源烈度模型）：" + StringUtils.join(intensities, ','));
                // 获取各个烈度（带）内的县级市人口信息
                final Map<Float, Map<String, Double>> populationInfos = ModelGal
                        .getCountyPopulationUnderIntensityLineCircle(intensityCircles);
                // 计算各个烈度（带）内的人口伤亡数目
                final Map<String, FatalityInfo> fatalities = getCountyFatalityInfos(populationInfos);
                // 构造JSON字符串
                _log.info("Result:");
                for (Map.Entry<String, FatalityInfo> fatality : fatalities.entrySet()) {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject();
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
                // 本来是要将人口死亡专门保存在数据中的一个位置，后来不用了，这行代码也就取消了
                // boolean stored = ModelDal.saveDeathAndPopulationNumber(eqID,
                // totalDeathNumber, totalPopulation);
            } else {
                _log.warn("out of CHINA, so I can not compute county casualties. Sorry.");
            }
            resultJSONObject.put("casualties", jsonArray);
            resultJSONObject.put("totalDeathNumber", totalDeathNumber);
            resultJSONObject.put("totalPopulation", totalPopulation);
            // 先甭创建数据集了，搜救的数据库上有问题
            // CASUALTY_COUNTY_USV.createCasualtyCounty(eqID, resultJSONObject);
            // CASUALTY_COUNTY_TEXT_USV.createCasualtyCountyText(eqID,
            // resultJSONObject);
            _log.info(text("total population:{0}, totalDeathNumber:{1}", totalPopulation, totalDeathNumber));
        } else {
            _log.info("震级小于7.5级，不调用线源模型进行县级市人口伤亡计算！");
            resultJSONObject.put("totalDeathNumber", "震级未达到7.5级或地震未发生在国内");
            resultJSONObject.put("totalPopulation", "震级未达到7.5级或地震未发生在国内");
        }
        return resultJSONObject.toString();
    }
}
