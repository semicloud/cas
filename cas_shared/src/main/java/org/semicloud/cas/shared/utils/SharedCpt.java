package org.semicloud.cas.shared.utils;

import java.util.Random;

/**
 * Shared Computer 公用运算器.
 *
 * @author Semicloud
 */
public class SharedCpt {

    /**
     * 获得震中烈度.
     *
     * @param mag 地震震级
     * @return 震中烈度
     */
    public static float getEpiIntensity(float mag) {
        return Float.parseFloat(Double.toString((mag - 1) * 3 / 2).split("[.]")[0]);
    }

    /**
     * 获得一个小于upper的随机整数
     *
     * @param upper the upper
     * @return the random integer
     */
    public static int getRandomInteger(int upper) {
        Random rnd = new Random();
        return rnd.nextInt(upper);
    }

    /**
     * 得到一个数值的上界~下界的表示字符串.
     *
     * @param d the d 数值
     * @return the lower upper string
     */
    public static String getLowerUpperString(double d) {
        return (int) getLower(d) + "~" + (int) getUpper(d);
    }

    /**
     * 获得下界
     *
     * @param f the f
     * @return the lower
     */
    public static float getLower(double f) {
        return (float) (f - f / 6.0);
    }

    /**
     * 获得上界
     *
     * @param f the f
     * @return the upper
     */
    public static float getUpper(double f) {
        return (float) (f + f / 6.0);
    }
}
