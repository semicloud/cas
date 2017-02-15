package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 灾区所示药品数量分析模型
 */
public class DISZASTER_MEDICINE extends BaseModel {

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
    public DISZASTER_MEDICINE(ModelInitializer initilizer, String modelName) {
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
        double totalDeath = Convert.toDouble(map.get("DEATH"));
        _log.info(text("get population and death, death:{1}", totalDeath));
        if (map != null && map.size() > 0) {
            // 受伤人数
            double injury = totalDeath * 2;
            // 医疗人员数量
            resultJSONObject.put("doctor_number", SharedCpt.getLowerUpperString(FACTOR * 0.11 * injury));
            // 担架数量
            resultJSONObject.put("stretcher_fu", SharedCpt.getLowerUpperString(FACTOR * 0.08 * injury));
            // 手术用血浆
            resultJSONObject.put("blood_ml", SharedCpt.getLowerUpperString(50 * injury));
            // 病房面积
            resultJSONObject.put("sickroom_m^2", SharedCpt.getLowerUpperString(FACTOR * 0.61 * injury));
            // 病床张数
            resultJSONObject.put("sickbed", SharedCpt.getLowerUpperString(FACTOR * 0.215 * injury));
            _log.info("disaster medicine compute ok");
        } else {
            _log.error("because casualty uncalculated, so medicine number can not obtained yet!");
        }
        return resultJSONObject.toString();
    }
}
