package org.semicloud.cas.shared.intensity.oval;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.semicloud.utils.common.Convert;

/**
 * 中国西部地区烈度模型.
 *
 * @author LiQinyong
 */
@JmxResource(description = "hua xi di qu", domainName = "model", beanName = "m03")
public class M03 implements Oval {
    @JmxAttributeField(isWritable = true, description = "default: 5.643, 1.538, -2.109, 25.0 ")
    private String longAxisParameters = "5.643, 1.538, -2.109, 25.0";

    @JmxAttributeField(isWritable = true, description = "default: 2.941, 1.363, -1.494, 7.0 ")
    private String shortAxisParameters = "2.941, 1.363, -1.494, 7.0";

    @Override
    @JmxOperation
    public double calcLongAxis(float i, float m, float d) {
        String[] strs = longAxisParameters.split("[,]");
        float a = Convert.toFloat(strs[0].trim());
        float b = Convert.toFloat(strs[1].trim());
        float c = Convert.toFloat(strs[2].trim());
        float R = Convert.toFloat(strs[3].trim());
        return MComputer.compute(i, m, a, b, c, R);
        // return MComputer.compute(i, m, 5.643f, 1.538f, -2.109f, 25.0f);
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
        // return MComputer.compute(i, m, 2.941f, 1.363f, -1.494f, 7.0f);
    }

}
