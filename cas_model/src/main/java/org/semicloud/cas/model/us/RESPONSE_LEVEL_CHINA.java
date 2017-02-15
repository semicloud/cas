package org.semicloud.cas.model.us;

import org.apache.commons.lang.StringUtils;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelDal;

/**
 * 国内应急响应级别判定模型
 */
public class RESPONSE_LEVEL_CHINA extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESPONSE_LEVEL_CHINA(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        StringBuilder sb = new StringBuilder("国内响应级别判定标准为：<br/>");
        sb.append("I级响应：死亡人数大于300人<br/>");
        sb.append("II级响应：死亡人数在50-300人之间<br/>");
        sb.append("III级响应：死亡人数在10-50人之间<br/>");
        sb.append("IV级响应:死亡人数在10人以下");
        double death = Double.parseDouble(ModelDal.getDeathAndPopulationNumber(eqID).get("death").toString());
        System.out.println(death);
        String level = StringUtils.EMPTY;
        if (death >= 300)
            level = "I级响应<br/>" + sb.toString();
        if (death < 300 && death >= 50)
            level = "I级响应<br/>" + sb.toString();
        if (death < 50 && death >= 10)
            level = "III级响应<br/>" + sb.toString();
        if (death < 10)
            level = "IV级响应<br/>" + sb.toString();
        resultJSONObject.put("level", level);
        return resultJSONObject.toString();
    }

}
