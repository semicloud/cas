package org.semicloud.cas.shared.intensity.oval;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.semicloud.utils.common.Convert;

/**
 * 华东地区烈度模型.
 *
 * @author LiQinyong
 */
@JmxResource(description = "hua dong di qu", domainName = "model", beanName = "m01")
public class M01 implements Oval {
    @JmxAttributeField(isWritable = true, description = "default: 6.046,1.48,-2.08,25.0 ")
    private String longAxisParameters = "6.046,1.48,-2.08,25.0";

    @JmxAttributeField(isWritable = true, description = "default: 2.617, 1.435,-1.441,7.0 ")
    private String shortAxisParameters = "2.617, 1.435,-1.441,7.0";

    @Override
    @JmxOperation
    public double calcLongAxis(float i, float m, float d) {
        String[] strs = longAxisParameters.split("[,]");
        float a = Convert.toFloat(strs[0].trim());
        float b = Convert.toFloat(strs[1].trim());
        float c = Convert.toFloat(strs[2].trim());
        float R = Convert.toFloat(strs[3].trim());
        return MComputer.compute(i, m, a, b, c, R);
        // the old one!
        // return MComputer.compute(i, m, 6.046f, 1.48f, -2.08f, 25.0f);
    }

    @Override
    @JmxOperation
    public double calcShortAxis(float i, float m, float d) {
        String[] strs = shortAxisParameters.split("[,]");
        float a = Convert.toFloat(strs[0].trim());
        float b = Convert.toFloat(strs[1].trim());
        float c = Convert.toFloat(strs[2].trim());
        float R = Convert.toFloat(strs[3].trim());
        return MComputer.compute(i, m, a, b, c, R);
        // the old one!
        // return MComputer.compute(i, m, 2.617f, 1.435f, -1.441f, 7.0f);
    }

}
