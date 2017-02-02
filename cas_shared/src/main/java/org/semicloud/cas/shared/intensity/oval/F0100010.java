package org.semicloud.cas.shared.intensity.oval;

import org.semicloud.cas.shared.utils.SharedCpt;

/**
 * 烈度模型F0100010，适用区域：地中海大西洋-西班牙.
 *
 * @author LiQinyong
 */
public class F0100010 implements Oval {

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getLongAxis(float,
     * float, float)
     */
    @Override
    public double calcLongAxis(float i, float m, float d) {
        return compute(i, m, d);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.shared.intensity.oval.Oval#getShorAxis(float,
     * float, float)
     */
    @Override
    public double calcShortAxis(float i, float m, float d) {
        return compute(i, m, d);
    }

    /**
     * 计算轴的值
     *
     * @param i the i
     * @param m the m
     * @param d the d
     * @return the double
     */
    private double compute(float i, float m, float d) {
        return Math.exp((compute_i0(m) + 12.55 - i) / 3.53) - 25;
    }

    /**
     * 计算震中烈度
     *
     * @param m the m
     * @return the float
     */
    private float compute_i0(float m) {
        return SharedCpt.getEpiIntensity(m);
    }

}
