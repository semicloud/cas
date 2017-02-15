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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 烈度模型2.0
 * 省的名字配合IntensityParams.xml加载烈度模型
 * 2017年 应甲方要求添加
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
    private static Log log = LogFactory.getLog(IntensityCircles2.class);

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
        initialize();
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
        initialize();
    }

    /**
     * 初始化烈度圈
     */
    private void initialize() {
        // 国内使用国内的烈度模型，其他国家使用圆模型
        // 国内的地震，震级小于7.5使用点源模型，大于等于7.5使用线源模型；程序中，不管震级几级，都调用线源模型
        // 震级小于7.5时，如果地震发生在内蒙古，则以包头的经度109.8度作为分界线
        // 大于109.8使用东北模型
        // 小于109.8使用新疆模型
        if (country.equals("CN")) {
            float baoTouLongitude = 109.1f; // 包头的经纬度
            String regionName = Settings.getModelRegionByProvince(province);
            if (StringUtils.isNotBlank(regionName)) {
                OvalParams params = null;
                if (!Objects.equals(regionName, "内蒙古自治区")) {
                    log.info(text("地震发生在{0}，使用烈度模型【{1}】", province, regionName));
                    params = Settings.getOvalParams(regionName);
                    log.info("加载烈度模型参数：\n" + params);
                } else {
                    params = this.epiCenter.getLongitude() >= baoTouLongitude ?
                            Settings.getOvalParams("东北地区") :
                            Settings.getOvalParams("新疆地区");
                    log.info("地震发生在内蒙古地区，按照包头经度加载烈度模型：" + params);
                }
                initOvalCircles(params, regionName);
            } else {
                log.error(text("ERROR! 没有为 {0} 找到一个烈度模型！", province));
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
            // 如果烈度圈计算出现了负数，那就取上一个圈儿的长短轴（该圈儿的长短轴需不是负数）各乘以一个系数，比如1/2
            if (ic.getLongAxis() > 0 && ic.getShortAxis() > 0) {
                circles.add(ic);
            } else {
                log.info("!!!!!Finding negative axis: " + ic.getLongAxis() +
                        "," + ic.getShortAxis() + " at intensity=" + ic.getIntensity());
                // 乘以的系数
                double factor = 0.5;
                float preIntensity = i - STEP;
                IntensityCircle preCircle = this.circles.stream().
                        filter(circle -> circle.getIntensity() == preIntensity)
                        .findFirst().orElse(null);
                if (preCircle != null) {
                    ic.setLongAxis(preCircle.getLongAxis() * factor);
                    ic.setShortAxis(preCircle.getShortAxis() * factor);
                    log.info(MessageFormat.format("modify to: {0},{1}, with preIntensity={2}(L:{3},S:{4}) "
                                    + "and factor={5}", ic.getLongAxis(), ic.getShortAxis(), preIntensity,
                            preCircle.getLongAxis(), preCircle.getShortAxis(), factor));
                } else {
                    log.error("Can not find IntensityCircle with intensity " + preCircle);
                }
                circles.add(ic);
            }
        }
        log.info("Initilized Complete:");
        for (IntensityCircle intensityCircle : this.circles) {
            log.info("\tInteisty: " + intensityCircle.getIntensity());
            log.info("\tLong Axis: " + intensityCircle.getLongAxis());
            log.info("\tShort Axis: " + intensityCircle.getShortAxis());
            log.info("\tAzimuth: " + intensityCircle.getAzimuth());
            log.info("-----------------");
        }
    }

    /**
     * 初始化圆烈度对象,用于国外地震
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
