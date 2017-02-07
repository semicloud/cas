package org.semicloud.cas.shared.intensity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.oval.MComputer;
import org.semicloud.cas.shared.intensity.oval2.OvalParams;

import java.util.ArrayList;
import java.util.List;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 烈度模型2.0 省的名字配合IntensityParams.xml加载烈度模型
 *
 * @author Semicloud
 */
public class IntensityCircles2 {
    /**
     * 烈度圈列表
     */
    private List<IntensityCircle> circles;

    /**
     * 震中
     */
    private EpiCenter epiCenter;

    // 国家信息
    private String country;

    // 省的信息
    private String province;

    /**
     * 起始计算烈度
     */
    private float startIntensity;

    /**
     * 震中烈度
     */
    private float epiIntensity;

    /**
     * 震级
     */
    private float magnitude;

    /**
     * 震源深度
     */
    private float depth;

    /**
     * 烈度计算步长
     */
    private static final float STEP = 0.5f;

    /**
     * 地图单位
     */
    private static final int MAP_UNIT = (int) Settings.getGisSettings().getMapUnit();

    /**
     * 牛顿-拉佛森计算器
     */
    private static final NewtonRaphsonSolver SOLVER = new NewtonRaphsonSolver();

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(IntensityCircles.class);

    /**
     * 初始化烈度圈
     *
     * @param epiCenter      震中点
     * @param country        国家名称
     * @param province       省的名称
     * @param startIntensity 起始烈度
     * @param epiIntensity   最大烈度
     * @param magnitude      震级
     */
    private IntensityCircles2(EpiCenter epiCenter, String country, String province, float startIntensity,
                              float epiIntensity, float magnitude) {
        this.epiCenter = epiCenter;
        this.startIntensity = startIntensity;
        this.epiIntensity = epiIntensity;
        this.magnitude = magnitude;
        this.country = country;
        this.province = province;
        this.circles = new ArrayList<>();
        initilize2();
    }

    /**
     * 初始化烈度圈
     *
     * @param epiCenter      震中点
     * @param country        国家名称
     * @param province       省的名称
     * @param startIntensity 起始烈度
     * @param epiIntensity   震中烈度
     * @param magnitude      震级
     * @param depth          深度
     */
    private IntensityCircles2(EpiCenter epiCenter, String country, String province, float startIntensity,
                              float epiIntensity, float magnitude, float depth) {
        this.epiCenter = epiCenter;
        this.country = country;
        this.province = province;
        this.startIntensity = startIntensity;
        this.epiIntensity = epiIntensity;
        this.magnitude = magnitude;
        this.depth = depth;
        this.circles = new ArrayList<>();
        initilize2();
    }

    /**
     * 初始化烈度圈
     */
    private void initilize2() {
        // 国内使用国内的烈度模型，其他国家使用圆模型
        if (country.equals("CN")) {
            // 震级小于7.5，使用点源模型
            String regionName = Settings.getModelRegionByProvince(province);
            if (StringUtils.isNotBlank(regionName)) {
                log.info(text("Earthquake occurs at {0}, using intensity model 【{1}】", province, regionName));
                OvalParams params = Settings.getOvalParams(regionName);
                log.info("load model paramtetrs：\n" + params);
                initOvalCircles(params, regionName);
            } else {
                log.error(text("ERROR! Proinve {0} has not mapping to a model!", province));
            }
        } else {
            // 国外地震，直接使用圆模型
            initCircleCircles();
        }
    }

    /**
     * 初始化椭圆模型
     *
     * @param p          烈度模型参数集合
     * @param regionName 使用的烈度模型
     */
    private void initOvalCircles(OvalParams p, String regionName) {
        log.info(text("使用【{0}】地区的烈度模型", regionName));
        ArcCalculator ac = new ArcCalculator(this.epiCenter);
        double azimuth = ac.getValue();
        log.info(text("intensity:{0}~{1}", startIntensity, epiIntensity));
        log.info("azimuth：" + String.format("%.2f", azimuth));
        for (float i = startIntensity; i <= epiIntensity; i += STEP) {
            IntensityCircle ic = new IntensityCircle();
            ic.setEpiCenter(epiCenter);
            ic.setLongAxis(MComputer.compute(p.getBase(), i, magnitude, p.getLa(), p.getLb(), p.getLc(), p.getLr())
                    * MAP_UNIT);
            ic.setShortAxis(MComputer.compute(p.getBase(), i, magnitude, p.getSa(), p.getSb(), p.getSc(), p.getSr())
                    * MAP_UNIT);
            ic.setAzimuth(azimuth);
            ic.setIntensity(i);
            // 如果长轴或短轴出现负数，就不加入烈度集合了
            if (ic.getLongAxis() > 0 && ic.getShortAxis() > 0) {
                circles.add(ic);
            } else {
                // 如果烈度圈计算出现了负数，那就将长短轴的长度都设置为2000
                // 等甲方那边调参数再改为直接加模型...咨询一下高娜再说吧
                ic.setLongAxis(2000);
                ic.setShortAxis(2000);
                circles.add(ic);
                log.warn(text("intensity {0} has negative number!({1},{2})", i, ic.getLongAxis(), ic.getShortAxis()));
            }
        }
    }

    /**
     * 初始化圆烈度对象.
     */
    private void initCircleCircles() {
        log.info("use circle intensity model");
        for (float iny = startIntensity; iny <= epiIntensity; iny += STEP) {
            IntensityCircle ic;
            ic = new IntensityCircle();
            double radius = calcRadius(iny);
            ic.setIntensity(iny);
            ic.setEpiCenter(epiCenter);
            ic.setLongAxis(FastMath.rint(radius * MAP_UNIT));
            ic.setShortAxis(FastMath.rint(radius * MAP_UNIT));
            ic.setAzimuth(0.0);
            circles.add(ic);
        }
    }

    /**
     * 使用牛顿拉佛森解法计算圆模型中圆的半径.
     *
     * @param intensity 烈度值
     * @return double
     */
    private double calcRadius(final float intensity) {
        return SOLVER.solve(100, new UnivariateDifferentiableFunction() {
            @Override
            public double value(double x) {
                return 0.00106 * x + 2.7 * FastMath.log10(x) + intensity - epiIntensity - 3.2;
            }

            @Override
            public DerivativeStructure value(DerivativeStructure t) throws MathIllegalArgumentException {
                return t.multiply(0.00106).add(t.log10().multiply(2.7)).add(intensity).subtract(epiIntensity)
                        .subtract(3.2);
            }
        }, 0, 50);
    }

    /**
     * Gets the list.
     *
     * @return the list
     */
    public List<IntensityCircle> getList() {
        return circles;
    }

    /**
     * 获得烈度圈对象
     *
     * @param epiCenter      the epi center 震中
     * @param startIntensity the start intensity 起始计算烈度
     * @param epiIntensity   the epi intensity 震中烈度
     * @param magnitude      the magnitude 震级
     * @param depth          the depth 震源深度
     * @return the circles 烈度圈列表
     */
    public static IntensityCircles2 getCircles(EpiCenter epiCenter, String country, String province,
                                               float startIntensity, float epiIntensity, float magnitude, float depth) {
        return new IntensityCircles2(epiCenter, country, province, startIntensity, epiIntensity, magnitude, depth);
    }

}
