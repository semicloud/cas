package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;

import java.util.List;
import java.util.Map;

/**
 * 次生灾害分析模型-海啸
 */
public class SECONDARY_TSUNAMI extends BaseModel {

    /**
     * Instantiates a new secondary tsunami.
     *
     * @param initilizer the initilizer
     * @param modelName  the model name
     */
    public SECONDARY_TSUNAMI(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 检查是不是掉海里了
     *
     * @return true, if is in sea
     */
    public boolean isInSea() {
        List<Map<String, Object>> attributes = ModelGal.isLocSea(epiCenter);
        return attributes.size() == 0;
    }

    // public static void main(String[] args) {
    // ModelInitilizer initilizer = new
    // ModelInitilizer("N62000W15180020140926015117",
    // "N27100E10300020140915132904_MIN30");
    // SECONDARY_TSUNAMI tsunami = new SECONDARY_TSUNAMI(initilizer, "");
    // System.out.println(tsunami.getJson());
    // }

    /**
     * 根据地震震级计算海啸等级.
     *
     * @return 海啸危险性的等级描述
     */
    private String calc() {
        String str;
        boolean isInSea = isInSea();
        if (getMagnitude() <= 6.5 && isInSea) {
            str = "可能发生海啸，可能造成微量损失";
        } else if (getMagnitude() > 6.5 && getMagnitude() <= 7.5 && isInSea) {
            str = "可能发生海啸，可能造成轻微损失";
        } else if (getMagnitude() > 7.5 && getMagnitude() <= 8 && isInSea) {
            str = "可能发生海啸，可能造成车船房屋损失";
        } else if (getMagnitude() > 8 && getMagnitude() <= 8.5 && isInSea) {
            str = "可能发生海啸，可能造成人员伤亡，房屋倒塌";
        } else {
            str = "预估不会发生海啸";
        }
        _log.info("land slide:" + str);
        return str;
    }

    // 构建要返回的JSON对象
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.semicloud.cas.model.BaseModel#getJson()
	 */
    @Override
    public String getJson() {
        resultJSONObject.put("tsunami", calc());
        return resultJSONObject.toString();
    }
}
