package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;

/**
 * 互联网主题分析模型，连接互联网智能处理系统，未实现
 */
public class INTERNET_TOPIC extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public INTERNET_TOPIC(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        resultJSONObject.put("internet_data_channel", "互联网智能处理系统尚未搜索到相关消息");
        return resultJSONObject.toString();
    }

}
