package org.semicloud.cas.shared.intensity.oval;

import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.sqrt;

/**
 * 俄罗斯_东部 圆模型.
 *
 * @author LiQinyong
 */
public class F0100047 implements Oval {

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getLongAxis(float,
     * float, float)
     */
    @Override
    public double calcLongAxis(float i, float m, float d) {
        return sqrt(pow(10.0, ((3 * m + 6 - 2 * i) / 3.5)) - pow(d, 2));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getShorAxis(float,
     * float, float)
     */
    @Override
    public double calcShortAxis(float i, float m, float d) {
        return sqrt(pow(10.0, ((3 * m + 6 - 2 * i) / 3.5)) - pow(d, 2));
    }

}
