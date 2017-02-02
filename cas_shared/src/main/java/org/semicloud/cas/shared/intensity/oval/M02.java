package org.semicloud.cas.shared.intensity.oval;

import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.semicloud.utils.common.Convert;

/**
 * 华南地区烈度模型.
 *
 * @author LiQinyong // 崔鑫 华南地区地震烈度衰减模型的建立 华南地震 2010，6 //
 *         长轴参数：5.4979，0.8753，-1.0785，18 // 短轴参数：4.674，0.8642，-0.9769，11
 *         <p>
 *         // 王继、俞言祥 华中_华南中强震地区地震烈度衰减关系研判 震灾防御技术 2008，3 //
 *         长轴参数：6.6079，0.9543，-3.5688，18 // 短轴参数：4.9540，0.9543，-2.9566，9 全是负值 //
 *         magnitude:6.0 // i=6.0, LongAxis=-12.1, ShortAxis=-4.3 // i=7.0,
 *         LongAxis=-13.54, ShortAxis=-6.3 // magnitude:7.0 // i=6.0,
 *         LongAxis=-10.29, ShortAxis=-2.3 // i=7.0, LongAxis=-12.18,
 *         ShortAxis=-4.3 // i=8.0, LongAxis=-13.6, ShortAxis=-6.3 // i=9.0,
 *         LongAxis=-14.67, ShortAxis=-7.3 // magnitude:8.0 // i=6.0,
 *         LongAxis=-7.93, ShortAxis=0.3 // i=7.0, LongAxis=-10.39,
 *         ShortAxis=-2.3 // i=8.0, LongAxis=-12.25, ShortAxis=-4.3 // i=9.0,
 *         LongAxis=-13.66, ShortAxis=-6.3 // i=10.0, LongAxis=-14.72,
 *         ShortAxis=-7.3 // magnitude:9.0 // i=6.0, LongAxis=-4.84,
 *         ShortAxis=4.3 // i=7.0, LongAxis=-8.06, ShortAxis=0.3 // i=8.0,
 *         LongAxis=-10.49, ShortAxis=-2.3 // i=9.0, LongAxis=-12.32,
 *         ShortAxis=-4.3 // i=10.0, LongAxis=-13.71, ShortAxis=-6.3 // i=11.0,
 *         LongAxis=-14.76, ShortAxis=-7.3 // i=12.0, LongAxis=-15.55,
 *         ShortAxis=-7.3
 */
@JmxResource(description = "hua nan di qu", domainName = "model", beanName = "m02")
public class M02 implements Oval {
    @JmxAttributeField(isWritable = true, description = "default: 3.6345, 1.6124, -1.7106, 20.0 ")
    private String longAxisParameters = "3.6345, 1.6124, -1.7106, 20.0";

    @JmxAttributeField(isWritable = true, description = "default: 2.7030, 1.5779, -1.5470, 14.0 ")
    private String shortAxisParameters = "2.7030, 1.5779, -1.5470, 14.0";

    @Override
    @JmxOperation
    public double calcLongAxis(float i, float m, float d) {
        String[] strs = longAxisParameters.split("[,]");
        float a = Convert.toFloat(strs[0].trim());
        float b = Convert.toFloat(strs[1].trim());
        float c = Convert.toFloat(strs[2].trim());
        float R = Convert.toFloat(strs[3].trim());
        return MComputer.compute(i, m, a, b, c, R);
        // 林金瑛 华南沿海地区地震烈度衰减关系
        // 长轴参数：3.6345, 1.6124, -1.7106, 20
        // 短轴参数：2.7030, 1.5779, -1.5470, 14
        // return MComputer.compute(i, m, 3.6345f, 1.6124f, -1.7106f, 20.0f);
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
        // return MComputer.compute(i, m, 2.7030f, 1.5779f, -1.5470f, 14.0f);
    }

}
