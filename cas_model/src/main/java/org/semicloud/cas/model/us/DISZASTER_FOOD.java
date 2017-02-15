package org.semicloud.cas.model.us;

import org.apache.commons.collections.MapUtils;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 灾区所示食品数量分析模型
 */
public class DISZASTER_FOOD extends BaseModel {

    /**
     * 区域因子
     */
    private static final double FACTOR = 1;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public DISZASTER_FOOD(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        Map<String, Object> map = ModelDal.getDeathAndPopulationNumber(eqID);
        if (map != null && MapUtils.isNotEmpty(map)) {
            double totalDeath = Convert.toDouble(map.get("DEATH"));
            double totalPopulation = Convert.toDouble(map.get("POPULATION"));
            _log.info(text("get population and death, population:{0}, death:{1}", totalPopulation, totalDeath));
            // 受伤人数
            double injury = totalDeath * 2;
            _log.info("injury:" + injury);
            // 粮食
            double food = FACTOR * (5.7 * injury + 1.6 * totalPopulation);
            _log.info("food:" + food);
            resultJSONObject.put("food_kg", SharedCpt.getLowerUpperString(food));

            // 熟食
            double cooked = FACTOR * 0.5 * totalPopulation;
            _log.info("cooked:" + cooked);
            resultJSONObject.put("cooked_kg", SharedCpt.getLowerUpperString(cooked));

            // 方便面
            double noodles = FACTOR * 2 * totalPopulation;
            _log.info("noodles bag:" + noodles);
            resultJSONObject.put("instant_noodles_bag", SharedCpt.getLowerUpperString(noodles));

            StringBuilder sb = new StringBuilder("受伤人数:" + injury + "\n");
            sb.append("粮食:" + food + "\n");
            sb.append("熟食：" + cooked + "\n");
            sb.append("方便面:" + cooked + "\n");
            // GENERAL_TEXT_USV.createTextDatasetVector(eqID, "所需食品数量",
            // sb.toString(), epiCenter.getLongitude(),
            // epiCenter.getLatitude());
        } else {
            _log.error("because casualty uncalculated, so food number can not obtained yet!");
        }
        return resultJSONObject.toString();
    }
}
