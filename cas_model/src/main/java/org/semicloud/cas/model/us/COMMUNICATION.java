package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.utils.common.Convert;

import java.util.Random;

/**
 * 通信损失模型，其实没用，生成了一个随机数返回了
 */
public class COMMUNICATION extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public COMMUNICATION(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        resultJSONObject.put("communication_loss", Convert.toFloat(String.valueOf(new Random().nextFloat()), "0.00"));
        return resultJSONObject.toString();
    }
}
