package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;

/**
 * 救援人员出队分析模型
 */
public class RESCUE_PEOPLE_FORM extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RESCUE_PEOPLE_FORM(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        resultJSONObject.put("rescue_form", "分20队，每队20人。(示例数据，实时库中目前无数据)。");
        return resultJSONObject.toString();
    }
}
