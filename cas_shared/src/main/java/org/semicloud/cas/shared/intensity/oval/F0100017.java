package org.semicloud.cas.shared.intensity.oval;

/**
 * 西亚_伊朗.
 *
 * @author LiQinyong
 */
public class F0100017 implements Oval {

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getLongAxis(float,
     * float, float)
     */
    @Override
    public double calcLongAxis(float i, float m, float d) {
        return Math.exp((11.564 + 0.943 * m - i) / 2.508) - 33;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getShorAxis(float,
     * float, float)
     */
    @Override
    public double calcShortAxis(float i, float m, float d) {
        return Math.exp((9.469 + 0.717 * m - i) / 2.121) - 13;
    }

}
