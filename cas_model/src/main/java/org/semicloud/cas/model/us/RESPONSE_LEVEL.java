package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.utils.common.Convert;

/**
 * 响应级别判定模型
 */
public class RESPONSE_LEVEL extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESPONSE_LEVEL(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        Double deathNumber = Convert.toDouble(ModelDal.getDeathAndPopulationNumber(eqID).get("DEATH"));
        String level = "";
        StringBuilder description = new StringBuilder("\n告警级别说明如下：\n");
        description.append("红色告警：估计死亡人数大于1000人；\n");
        description.append("橙色告警：估计死亡人数在100-999人之间；\n");
        description.append("黄色告警：估计死亡人数在1-99人之间；\n");
        description.append("蓝色告警：预计无人员伤亡；");
        if (deathNumber != null) {
            if (deathNumber > 1000) {
                level = "红色" + description.toString();
            } else if (deathNumber < 999 && deathNumber > 100) {
                level = "橙色" + description.toString();
            } else if (deathNumber < 99 && deathNumber > 1) {
                level = "黄色" + description.toString();
            } else {
                level = "蓝色" + description.toString();
            }
        } else {
            level = "人口死亡结果尚未计算出，无法得到相应等级！";
        }
        _log.info("response level:" + level);
        resultJSONObject.put("response_level", level);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // GENERAL_TEXT_USV.createTextDatasetVector(eqID, "告警等级", level,
        // epiCenter.getLongitude(), epiCenter.getLatitude());
        return resultJSONObject.toString();
    }
}
