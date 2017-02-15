package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;

/**
 * 地震烈度模型中的线源模型
 * Created by Administrator on 2017/2/7.
 */
public class INTENSITY_LINE extends BaseModel {
    /**
     * 构造函数.
     *
     * @param initilizer ModelInitilizer对象
     * @param modelName  模型名称
     */
    public INTENSITY_LINE(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    @Override
    public String getJson() {
        return null;
    }
}
