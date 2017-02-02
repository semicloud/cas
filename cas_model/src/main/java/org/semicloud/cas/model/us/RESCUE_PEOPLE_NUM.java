package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.util.Map;

/**
 * 救援队分数分析模型
 */
public class RESCUE_PEOPLE_NUM extends BaseModel {

    /**
     * FACTOR.
     */
    private static final double FACTOR = 1;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESCUE_PEOPLE_NUM(ModelInitilizer initilizer, String modelName) {
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
        if (map != null && map.size() > 0) {
            double totalDeath = Convert.toDouble(map.get("DEATH"));
            double totalPopulation = Convert.toDouble(map.get("POPULATION"));
            // 受伤人数
            double injury = totalDeath * 2;
            // 救灾指挥人员
            resultJSONObject.put("commander", SharedCpt.getLowerUpperString(FACTOR * 0.00143 * totalPopulation));
            // 灾区自救人员
            resultJSONObject.put("rescue_self",
                    SharedCpt.getLowerUpperString(FACTOR * (0.807 * injury + 0.16 * totalPopulation)));
            // 灾区互救人员
            resultJSONObject.put("rescue_other",
                    SharedCpt.getLowerUpperString(FACTOR * (0.005 * totalPopulation + 2.94 * totalDeath)));
            // 军队救灾
            resultJSONObject.put("army", SharedCpt.getLowerUpperString(FACTOR * 0.054 * totalPopulation));
        } else {
            _log.error("because casualty number uncalculated, so the rescue number can not obtained yet!");
        }
        return resultJSONObject.toString();
    }
}
