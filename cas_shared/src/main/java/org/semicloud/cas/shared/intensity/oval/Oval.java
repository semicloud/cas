package org.semicloud.cas.shared.intensity.oval;

import org.semicloud.cas.shared.cfg.Settings;

/**
 * 椭圆烈度模型基类
 */
public interface Oval {

    /**
     * 起始计算烈度.
     */
    public static final float START_INTENSITY = Settings.getModelStartIntensity();

    /**
     * 计算烈度圈长轴.
     *
     * @param i 烈度值
     * @param m 震级
     * @param d 深度
     * @return double
     */
    double calcLongAxis(float i, float m, float d);

    /**
     * 计算烈度圈短轴值.
     *
     * @param i 烈度值
     * @param m 震级
     * @param d 深度
     * @return double
     */
    double calcShortAxis(float i, float m, float d);
}
