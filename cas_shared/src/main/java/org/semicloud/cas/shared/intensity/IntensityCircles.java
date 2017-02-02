package org.semicloud.cas.shared.intensity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.al.SharedGal;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.oval.M01;
import org.semicloud.cas.shared.intensity.oval.Oval;
import org.semicloud.utils.common.Convert;

import java.util.*;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * The Class IntensityCircles.
 */
public class IntensityCircles {

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
     * 烈度圈列表
     */
    private List<IntensityCircle> circles;
    /**
     * 震中
     */
    private EpiCenter epiCenter;
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
     * Instantiates a new intensity circles.
     *
     * @param epiCenter      the epi center
     * @param startIntensity the start intensity
     * @param epiIntensity   the epi intensity
     * @param magnitude      the magnitude
     */
    private IntensityCircles(EpiCenter epiCenter, float startIntensity, float epiIntensity, float magnitude) {
        this.epiCenter = epiCenter;
        this.startIntensity = startIntensity;
        this.epiIntensity = epiIntensity;
        this.magnitude = magnitude;
        this.circles = new ArrayList<>();
        initilize();
    }

    /**
     * Instantiates a new intensity circles.
     *
     * @param epiCenter      the epi center
     * @param startIntensity the start intensity
     * @param epiIntensity   the epi intensity
     * @param magnitude      the magnitude
     * @param depth          the depth
     */
    private IntensityCircles(EpiCenter epiCenter, float startIntensity, float epiIntensity, float magnitude, float depth) {
        this.epiCenter = epiCenter;
        this.startIntensity = startIntensity;
        this.epiIntensity = epiIntensity;
        this.magnitude = magnitude;
        this.depth = depth;
        this.circles = new ArrayList<>();
        initilize();
    }

    // TODO 在这里决定了要调用什么烈度模型

    /**
     * 根据模型代码加载椭圆烈度模型对象.
     *
     * @param code 模型代码
     * @return the oval
     */
    private static Oval loadModel(String code) {
        try {
            Class<?> c = Class.forName("org.semicloud.cas.shared.intensity.oval.".concat(code));
            return (Oval) c.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new M01();
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
    public static IntensityCircles getCircles(EpiCenter epiCenter, float startIntensity, float epiIntensity,
                                              float magnitude, float depth) {
        return new IntensityCircles(epiCenter, startIntensity, epiIntensity, magnitude, depth);
    }

    /**
     * 初始化，本方法中确定了是调用圆模型还是椭圆模型
     */
    private void initilize() {
        List<String> codes = SharedGal.getIntensityModelCodeList(epiCenter);
        List<String> filted = CodeFilter.filte(codes);
        if (filted.size() == 1) {
            initOvalCircles(filted.get(0));
            // initOvalCircles(code);
        } else {
            initCircleCircles();
        }
    }

    /**
     * 初始化椭圆烈度模型
     *
     * @param code the code 模型列表
     */
    private void initOvalCircles(String code) {
        log.info(text("attention: use intensity model {0}", code));
        Oval oval = loadModel(code);
        // TODO 在这里使用了新的烈度圈偏转角计算方法
        // double azimuth = calcAzimuth();
        ArcCalculator ac = new ArcCalculator(epiCenter);
        double azimuth = ac.getValue();
        log.info("azimuth:" + azimuth);
        for (float i = startIntensity; i <= epiIntensity; i += STEP) {
            IntensityCircle ic = new IntensityCircle();
            ic.setEpiCenter(epiCenter);
            ic.setLongAxis(oval.calcLongAxis(i, magnitude, depth) * MAP_UNIT);
            ic.setShortAxis(oval.calcShortAxis(i, magnitude, depth) * MAP_UNIT);
            ic.setAzimuth(azimuth);
            ic.setIntensity(i);
            circles.add(ic);
        }
    }

    /**
     * 初始化圆烈度对象.
     */
    private void initCircleCircles() {
        log.info("use circle intensity model");
        for (float iny = startIntensity; iny <= epiIntensity; iny += STEP) {
            IntensityCircle ic = new IntensityCircle();
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
     * 调用GIS操作，获取偏转角.
     *
     * @return the float
     */
    @SuppressWarnings("unused")
    @Deprecated
    private float calcAzimuth() {
        // 获得震中为圆心，搜索半径为半径的圆之内的所有断层信息
        List<Map<String, Object>> listMap = SharedGal.getFaultInfos(epiCenter);
        // 默认断层交角
        float azimuth = Settings.getModelDefaultAzimuth();
        // 计算断层至震中点的距离，确定距离震中最近的断层，将其偏转角设置为烈度圈偏转角
        if (listMap.size() > 0) {
            Collections.sort(listMap, getActiveFaultComparator());
            if (listMap.get(0).containsKey("strike")) {
                String strike = listMap.get(0).get("strike").toString().trim();
                if (!strike.equals("")) {
                    try {
                        azimuth = Float.parseFloat(strike);
                    } catch (Exception e) {
                        log.error("parse data error, [" + strike + "] can not convert to FLOAT");
                    }
                }
            }
        }
        if (azimuth > 0) {
            // 将距离转换为公里
            double distance = FastMath.rint(Convert.toDouble(listMap.get(0).get("distance").toString()) / 1000);
            String faultName = listMap.get(0).get("name_cn").toString();
            log.info(text("fault-name:{0}, strike:{1}, distance from epi:{2} KM", faultName, azimuth, distance));
        } else {
            log.warn(text("no strike from database, use default value {0} to compute", azimuth));
        }
        return azimuth;
    }

    /**
     * Gets the active fault comparator.
     *
     * @return the active fault comparator
     */
    @Deprecated
    private Comparator<Map<String, Object>> getActiveFaultComparator() {
        Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                int ans = 0;
                if (map1.get("distance") != null && map2.get("distance") != null) {
                    try {
                        Double d1 = Double.parseDouble(map1.get("distance").toString());
                        Double d2 = Double.parseDouble(map2.get("distance").toString());
                        ans = d1.compareTo(d2);
                    } catch (NullPointerException e) {
                        log.error("Null Pointer Exception");
                    } catch (NumberFormatException e) {
                        log.error("Number Format Exception");
                    }
                }
                return ans;
            }
        };
        return comparator;
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
}
