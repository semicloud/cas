package org.semicloud.cas.shared.intensity.oval;

/**
 * 烈度模型F0100011，适用区域：地中海大西洋-葡萄牙.
 */
public class F0100011 implements Oval {

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getLongAxis(float,
     * float, float)
     */
    @Override
    public double calcLongAxis(float i, float m, float d) {
        return compute(i, m);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getShorAxis(float,
     * float, float)
     */
    @Override
    public double calcShortAxis(float i, float m, float d) {
        return compute(i, m);
    }

    /**
     * Compute.
     *
     * @param i the i
     * @param m the m
     * @return the double
     */
    private double compute(float i, float m) {
        return Math.exp((6.8 + 1.13 * m - i) / 1.68) - 14;
    }

}
