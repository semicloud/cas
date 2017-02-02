package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;

/**
 * 救援设备分析模型
 */
public class RESCUE_EQUIP extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESCUE_EQUIP(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        resultJSONObject.put("rescue_equip", "应急搜救车20辆，其他装备若干。（示例数据，实时库中无数据）");
        return resultJSONObject.toString();
    }
}
