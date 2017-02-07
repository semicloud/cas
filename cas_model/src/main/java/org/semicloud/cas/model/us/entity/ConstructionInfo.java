package org.semicloud.cas.model.us.entity;

/**
 * 建筑物类
 *
 * @author Semicloud
 */
class ConstructionInfo {
    private String _cityName;
    private float _intensity;
    private String _structType;
    private double _good;
    private double _minDamage;
    private double _midDamage;
    private double _maxDamage;
    private double _destroy;

    public String getCityName() {
        return _cityName;
    }

    public void setCityName(String cityName) {
        _cityName = cityName;
    }

    public float getIntensity() {
        return _intensity;
    }

    public void setIntensity(float intensity) {
        _intensity = intensity;
    }

    public String getStructType() {
        return _structType;
    }

    public void setStructType(String structType) {
        _structType = structType;
    }

    public double getGood() {
        return _good;
    }

    public void setGood(double good) {
        _good = good;
    }

    public double getMinDamage() {
        return _minDamage;
    }

    public void setMinDamage(double minDamage) {
        _minDamage = minDamage;
    }

    public double getMidDamage() {
        return _midDamage;
    }

    public void setMidDamage(double midDamage) {
        _midDamage = midDamage;
    }

    public double getMaxDamage() {
        return _maxDamage;
    }

    public void setMaxDamage(double maxDamage) {
        _maxDamage = maxDamage;
    }

    public double getDestroy() {
        return _destroy;
    }

    public void setDestroy(double destroy) {
        _destroy = destroy;
    }
}
