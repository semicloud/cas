package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;

/**
 * 救援后勤保障分析模型
 */
public class RESCUE_LOGISTIC extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESCUE_LOGISTIC(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        resultJSONObject.put("rescue_logistic", "需带食品2000份，饮水3000份。（示例数据，实时库中目前无数据）");
        return resultJSONObject.toString();
    }
}
