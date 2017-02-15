package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;

/**
 * 次生灾害模型-山体滑坡
 */
public class SECONDARY_LANDSLIDE extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public SECONDARY_LANDSLIDE(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        String str;
        if (getMagnitude() <= 6) {
            str = "发生山体滑坡可能性较小";
        } else if (getMagnitude() > 6 && getMagnitude() < 8) {
            str = "发生山体滑坡可能性较大";
        } else {
            str = "发生山体滑坡可能性极大";
        }
        _log.info("land slide:" + str);
        resultJSONObject.put("land_slide", str);
        return resultJSONObject.toString();
    }
}
