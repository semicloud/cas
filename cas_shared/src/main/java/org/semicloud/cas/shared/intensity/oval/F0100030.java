package org.semicloud.cas.shared.intensity.oval;

import static org.apache.commons.math3.util.FastMath.pow;

/**
 * 美国西部烈度模型，圆模型.
 *
 * @author LiQinyong
 */
public class F0100030 implements Oval {

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getLongAxis(float,
     * float, float)
     */
    @Override
    public double calcLongAxis(float i, float m, float d) {
        return pow(10.0, (4.1987 + 1.162 * m - i) / 3.2249) - 10;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getShorAxis(float,
     * float, float)
     */
    @Override
    public double calcShortAxis(float i, float m, float d) {
        return pow(10.0, (4.1987 + 1.162 * m - i) / 3.2249) - 10;
    }

}
