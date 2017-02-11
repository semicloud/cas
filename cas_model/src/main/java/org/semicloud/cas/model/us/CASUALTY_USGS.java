package org.semicloud.cas.model.us;

import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.op4j.Op;
import org.op4j.functions.FnDouble;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.utils.common.Convert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * USGS人口伤亡研判模型
 */
public class CASUALTY_USGS extends BaseModel {
    /**
     * 正态分布计算类
     */
    private static final NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution();
    /**
     * 根据国家代码提取人口死亡模型参数
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
    public CASUALTY_USGS(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 计算该烈度下人口死亡率.
     *
     * @param iny 烈度值
     * @return the ratio
     */
    private double getRatio(float iny) {
        double ratio = NORMAL_DISTRIBUTION.cumulativeProbability((1 / BETA) * Math.log(iny / THETA));
        _log.info(text("compute ratio under intensity {0} is {1}", iny, ratio));
        return ratio;
    }

    /**
     * 获得人口死亡信息
     *
     * @return the casualties
     */
    private List<CASUALTY> getCasualties() {
        List<CASUALTY> casualties = Lists.newArrayList();
        Map<Float, Double> populationsMap = ModelGal.getPopulationNumbers(getCircles());
        for (Entry<Float, Double> populationEntry : populationsMap.entrySet()) {
            float intensity = populationEntry.getKey();
            double population = populationEntry.getValue();
            double ratio = getRatio(intensity);
            int death = (int) Math.rint(population * ratio);
            CASUALTY casualty = new CASUALTY();
            casualty.setIntensity(intensity);
            casualty.setPopulation(population);
            casualty.setRatio(ratio);
            casualty.setDeath(death);
            casualties.add(casualty);
        }
        return casualties;
    }

    /**
     * 获得统计后人口死亡信息
     * <p>
     * 人口死亡计算计算得到的烈度圈步长为0.5，需要改成步长为1的
     *
     * @param casualties the casualties
     * @return the summaried casualties
     */
    public List<CASUALTY> getSummariedCasualties(List<CASUALTY> casualties) {
        List<CASUALTY> summariedCasualties = Lists.newArrayList();
        _log.info("summaried casualties...");
        for (float i = START_INTENSITY; i <= epiIntensity; i++) {
            _log.info("for intensity " + i);
            List<CASUALTY> subList = Op.onList(casualties).removeAllFalse(CASUALTY.getCasualtyFunction(i)).get();
            _log.info("old casualties :" + subList);
            double population = Op.onList(Op.onList(subList).map(CASUALTY.getPopulationFunction()).get())
                    .exec(FnDouble.sum()).get();
            _log.info("population: " + population);
            double death = Op.onList(Op.onList(subList).map(CASUALTY.getDeathFunction()).get()).exec(FnDouble.sum())
                    .get();
            _log.info("death: " + death);
            double ratio = Op.onList(Op.onList(subList).map(CASUALTY.getRatioFunction()).get()).exec(FnDouble.avg())
                    .get();
            _log.info("ratio: " + ratio);
            CASUALTY aCasualty = new CASUALTY();
            aCasualty.setIntensity(i);
            aCasualty.setPopulation(population);
            aCasualty.setDeath(death);
            aCasualty.setRatio(ratio);
            _log.info("summaried casualties:" + aCasualty);
            summariedCasualties.add(aCasualty);
        }
        return summariedCasualties;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        List<CASUALTY> summaried = getSummariedCasualties(getCasualties());
        double totalPopulation = 0, totalDeathNumber = 0;
        JSONArray jsonArray = new JSONArray();
        for (CASUALTY casualty : summaried) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("intensity", casualty.getIntensity());
            jsonObject.put("population", casualty.getPopulation());
            jsonObject.put("ratio", Convert.toDouble(casualty.getRatio(), "0.0000"));
            jsonObject.put("death", casualty.getDeath());
            jsonArray.add(jsonObject);
            totalDeathNumber += casualty.getDeath();
            totalPopulation += casualty.getPopulation();
        }
        resultJSONObject.put("casualties", jsonArray);
        resultJSONObject.put("totalDeathNumber", totalDeathNumber);
        resultJSONObject.put("totalPopulation", totalPopulation);

        // TODO 注意！这里需要恢复原状
        boolean isOK = ModelDal.saveDeathAndPopulationNumber(eqID, totalDeathNumber, totalPopulation);
        _log.info("total death number:" + totalDeathNumber + ", save to database -> " + isOK);
        return resultJSONObject.toString();
    }
}

