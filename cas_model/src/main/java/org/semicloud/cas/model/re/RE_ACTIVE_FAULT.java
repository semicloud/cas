package org.semicloud.cas.model.re;

import org.semicloud.cas.shared.EditResult;

/**
 * 重计算-震中附近断层分布模型
 */
public class RE_ACTIVE_FAULT extends RE_BASE_MODEL {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RE_ACTIVE_FAULT(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        return getOld("m_activefault");
    }
}
