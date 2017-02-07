package org.semicloud.cas.model.us.entity;

/**
 * 人口死亡信息
 * Created by Administrator on 2017/2/7.
 */
public class FatalityInfo {
    private double populationNumber;
    private double deathNumber;
    private double ratio;

    public double getPopulationNumber() {
        return populationNumber;
    }

    public void setPopulationNumber(double populationNumber) {
        this.populationNumber = populationNumber;
    }

    public double getDeathNumber() {
        return deathNumber;
    }

    public void setDeathNumber(double deathNumber) {
        this.deathNumber = deathNumber;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "CountyFatalityInfo [populationNumber=" + populationNumber + ", deathNumber=" + deathNumber + ", ratio="
                + ratio + "]";
    }
}
