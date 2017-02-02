package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.utils.common.Convert;

/**
 * 建筑物损失模型-郑超模型
 */
public class CONSTRUCTION_ZHENGCHAO extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public CONSTRUCTION_ZHENGCHAO(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /**
     * 计算平均震害指数.
     *
     * @param p1        参数1
     * @param p2        参数2
     * @param p3        参数3
     * @param intensity the intensity
     * @return 平均震害指数
     */
    private double calc(double p1, double p2, double p3, float intensity) {
        return p1 * Math.pow(intensity, 2) + p2 * intensity + p3;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    public String getJson() {
        JSONArray array = new JSONArray();
        for (float iny = START_INTENSITY; iny <= epiIntensity; iny += 1) {
            JSONObject obj = new JSONObject();
            obj.put("intensity", iny);
            obj.put("a_level", Convert.toDouble(calc(-0.0145, 0.4124, 1.8326, iny), "#0.000"));
            obj.put("a_plus_level", Convert.toDouble(calc(0.035, -0.3623, 0.9818, iny), "#0.000"));
            obj.put("b_level", Convert.toDouble(calc(0.028, -0.2534, 0.5925, iny), "#0.000"));
            obj.put("c_level", Convert.toDouble(calc(0.0133, -0.0829, 0.0718, iny), "#0.000"));
            obj.put("d_level", Convert.toDouble(calc(0.01971, -0.2081, 0.576, iny), "#0.000"));
            array.add(obj);
        }
        resultJSONObject.put("Construction", array);
        return resultJSONObject.toString();
    }
}
