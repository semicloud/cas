package org.semicloud.cas.shared.intensity.oval;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.semicloud.utils.common.Convert;

/**
 * 中国北部地区烈度模型.
 * 来源 崔新等 华北地区地震烈度衰减模型的建立
 *
 * @author LiQinyong
 */
@JmxResource(description = "hua bei di qu", domainName = "model", beanName = "m04")
public class M04 implements Oval {
    @JmxAttributeField(isWritable = true, description = "default: 3.0117, 1.5495, -1.3509, 30.0 ")
    private String longAxisParameters = "3.0117, 1.5495, -1.3509, 30.0";
    @JmxAttributeField(isWritable = true, description = "default: 1.7865, 1.4523, -1.1155, 12.0f ")
    private String shortAxisParameters = "1.7865, 1.4523, -1.1155, 12.0f";

    @Override
    @JmxOperation
    public double calcLongAxis(float i, float m, float d) {
        String[] strs = longAxisParameters.split("[,]");
        float a = Convert.toFloat(strs[0].trim());
        float b = Convert.toFloat(strs[1].trim());
        float c = Convert.toFloat(strs[2].trim());
        float R = Convert.toFloat(strs[3].trim());
        return MComputer.compute(i, m, a, b, c, R);
        // return MComputer.compute(i, m, 3.0117f, 1.5495f, -1.3509f, 30.0f);
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
        // 唉，调整了一下，将公式中的-13.0改成了-12.0
        // return MComputer.compute(i, m, 1.7865f, 1.4523f, -1.1155f, 12.0f);
        // return MComputer.compute(i, m, 1.7865f, 1.4523f, -1.1155f, 13.0f);
    }

}
