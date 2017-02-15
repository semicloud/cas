package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.util.Map;

/**
 * 灾区所需帐篷数量分析模型
 */
public class DISZASTER_TENT extends BaseModel {

    /**
     * 无家可归人员比例
     */
    private static final double HOME_LESS_RATIO = 0.1;

    /**
     * 季节参数
     */
    private static final double SEASON_PARAMETER = 1;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public DISZASTER_TENT(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        // 帐篷数量=0.25 * 无家可归者 * 季节系数
        Map<String, Object> map = ModelDal.getDeathAndPopulationNumber(eqID);
        if (map != null && map.size() > 0) {
            double totalDeath = Convert.toDouble(map.get("DEATH"));
            double totalPopulation = Convert.toDouble(map.get("POPULATION"));
            double totalHomeLess = (totalPopulation - totalDeath) * HOME_LESS_RATIO;
            double tentNumber = SEASON_PARAMETER * totalHomeLess * 0.25;
            resultJSONObject.put("tent_number", SharedCpt.getLowerUpperString(tentNumber));
            // TODO 先甭创建数据集了，搜救的数据库上有问题
            // GENERAL_TEXT_USV.createTextDatasetVector(eqID, "所需帐篷数量",
            // SharedCpt.getLowerUpperString(tentNumber),
            // epiCenter.getLongitude(), epiCenter.getLatitude());
        } else {
            _log.error("because casualty uncalculated, so tent number can not obtained yet!");
        }
        return resultJSONObject.toString();
    }
}
