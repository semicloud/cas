package org.semicloud.cas.shared.intensity.oval;

import static org.apache.commons.math3.util.FastMath.exp;

/**
 * 中国地区烈度模型长短轴计算公式.
 */
public class MComputer {

    /**
     * 中国地区烈度模型长短轴计算公式.
     *
     * @param i 烈度值
     * @param m 震级
     * @param a 参数a
     * @param b 参数b
     * @param c 参数c
     * @param r 参数r
     * @return 长轴或短轴的值，依据a，b，c取值的不同而不同
     */
    protected static double compute(float i, float m, float a, float b, float c, float r) {
        return exp((i - a - b * m) / c) - r;
    }
}
