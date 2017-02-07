package org.semicloud.cas.shared.intensity;

import org.semicloud.cas.shared.EpiCenter;

/**
 * 线源模型烈度带
 * Created by Administrator on 2017/2/7.
 */
public class IntensityLineBelt {
    private IntensityLineCircle bigLineCircle;
    private IntensityLineCircle smallLineCircle;
    private float intensity;
    private EpiCenter epiCenter;

    public IntensityLineBelt(IntensityLineCircle bigLineCircle, IntensityLineCircle smallLineCircle) {
        this.bigLineCircle = bigLineCircle;
        this.smallLineCircle = smallLineCircle;
        this.intensity = bigLineCircle.getIntensity();
        this.epiCenter = bigLineCircle.getEpiCenter();
    }

    @Override
    public String toString() {
        return "线源烈度带：大圈烈度：" + this.bigLineCircle.getIntensity()
                + "，小圈烈度：" + this.smallLineCircle.getIntensity();
    }

    public IntensityLineCircle getBigLineCircle() {
        return bigLineCircle;
    }

    public void setBigLineCircle(IntensityLineCircle bigLineCircle) {
        this.bigLineCircle = bigLineCircle;
    }

    public IntensityLineCircle getSmallLineCircle() {
        return smallLineCircle;
    }

    public void setSmallLineCircle(IntensityLineCircle smallLineCircle) {
        this.smallLineCircle = smallLineCircle;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public EpiCenter getEpiCenter() {
        return epiCenter;
    }

    public void setEpiCenter(EpiCenter epiCenter) {
        this.epiCenter = epiCenter;
    }
}
