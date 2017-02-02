package org.semicloud.cas.shared.intensity;

import org.semicloud.cas.shared.EpiCenter;

/**
 * 烈度带对象
 *
 * @author Victor
 */
public class IntensityBelt {

    /**
     * 面积大的烈度圈
     */
    private IntensityCircle bigCircle;

    /**
     * 面积小的烈度圈
     */
    private IntensityCircle smallCircle;

    /**
     * 烈度值
     */
    private float intensity;

    /**
     * 震中
     */
    private EpiCenter epiCenter;

    /**
     * Instantiates a new intensity belt.
     *
     * @param bigCircle   the big circle
     * @param smallCircle the small circle
     */
    public IntensityBelt(IntensityCircle bigCircle, IntensityCircle smallCircle) {
        this.bigCircle = bigCircle;
        this.smallCircle = smallCircle;
        this.intensity = bigCircle.getIntensity();
        this.epiCenter = bigCircle.getEpiCenter();
    }

    /**
     * Gets the big circle.
     *
     * @return the big circle
     */
    public IntensityCircle getBigCircle() {
        return bigCircle;
    }

    /**
     * Sets the big circle.
     *
     * @param bigCircle the new big circle
     */
    public void setBigCircle(IntensityCircle bigCircle) {
        this.bigCircle = bigCircle;
    }

    /**
     * Gets the small circle.
     *
     * @return the small circle
     */
    public IntensityCircle getSmallCircle() {
        return smallCircle;
    }

    /**
     * Sets the small circle.
     *
     * @param smallCircle the new small circle
     */
    public void setSmallCircle(IntensityCircle smallCircle) {
        this.smallCircle = smallCircle;
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
     * Gets the epi center.
     *
     * @return the epi center
     */
    public EpiCenter getEpiCenter() {
        return epiCenter;
    }

    /**
     * Sets the epi center.
     *
     * @param epiCenter the new epi center
     */
    public void setEpiCenter(EpiCenter epiCenter) {
        this.epiCenter = epiCenter;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IntensityBelt [bigCircle=" + bigCircle + ", smallCircle=" + smallCircle + "]";
    }

}
