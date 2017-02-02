package org.semicloud.cas.model.us;

import org.op4j.functions.ExecCtx;
import org.op4j.functions.IFunction;

// TODO: Auto-generated Javadoc

/**
 * 人口死亡实体类.
 */
public class CASUALTY {

    /**
     * 烈度值
     */
    float intensity;

    /**
     * 人口数
     */
    double population;

    /**
     * 人口死亡率
     */
    double ratio;

    /**
     * 人口死亡数
     */
    double death;

    /**
     * Gets the casualty function.
     *
     * @param f the f
     * @return the casualty function
     */
    public static IFunction<CASUALTY, Boolean> getCasualtyFunction(float f) {
        final String str = Integer.toString((int) f);
        return new IFunction<CASUALTY, Boolean>() {
            @Override
            public Boolean execute(CASUALTY arg0, ExecCtx arg1) throws Exception {
                return Float.toString(arg0.getIntensity()).startsWith(str);
            }
        };
    }

    /**
     * Gets the population function.
     *
     * @return the population function
     */
    public static IFunction<CASUALTY, Double> getPopulationFunction() {
        return new IFunction<CASUALTY, Double>() {
            @Override
            public Double execute(CASUALTY arg0, ExecCtx arg1) throws Exception {
                return arg0.getPopulation();
            }
        };
    }

    /**
     * Gets the death function.
     *
     * @return the death function
     */
    public static IFunction<CASUALTY, Double> getDeathFunction() {
        return new IFunction<CASUALTY, Double>() {
            @Override
            public Double execute(CASUALTY arg0, ExecCtx arg1) throws Exception {
                return arg0.getDeath();
            }
        };
    }

    /**
     * Gets the ratio function.
     *
     * @return the ratio function
     */
    public static IFunction<CASUALTY, Double> getRatioFunction() {
        return new IFunction<CASUALTY, Double>() {
            @Override
            public Double execute(CASUALTY arg0, ExecCtx arg1) throws Exception {
                return arg0.getRatio();
            }
        };
    }

    /**
     * Gets the intensity.
     *
     * @return the intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * Sets the intensity.
     *
     * @param intensity the new intensity
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    /**
     * Gets the population.
     *
     * @return the population
     */
    public double getPopulation() {
        return population;
    }

    /**
     * Sets the population.
     *
     * @param population the new population
     */
    public void setPopulation(double population) {
        this.population = population;
    }

    /**
     * Gets the ratio.
     *
     * @return the ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Sets the ratio.
     *
     * @param ratio the new ratio
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * Gets the death.
     *
     * @return the death
     */
    public double getDeath() {
        return death;
    }

    /**
     * Sets the death.
     *
     * @param death the new death
     */
    public void setDeath(double death) {
        this.death = death;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Casualty [intensity=" + intensity + ", population=" + population + ", ratio=" + ratio + ", death="
                + death + "]";
    }
}
