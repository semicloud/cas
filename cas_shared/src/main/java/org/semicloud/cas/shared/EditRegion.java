package org.semicloud.cas.shared;

import com.supermap.data.GeoRegion;

/**
 * 人工编辑区域
 */
public class EditRegion {

    /**
     * 中心经度
     */
    private double _longitude;

    /**
     * 中心纬度
     */
    private double _latitude;

    /**
     * 烈度
     */
    private float _intensity;

    /**
     * 面对像，即几何对象
     */
    private GeoRegion _geoRegion;

    /**
     * Instantiates a new edits the region.
     */
    public EditRegion() {
    }

    /**
     * Instantiates a new edits the region.
     *
     * @param lng    the lng 中心经度
     * @param lat    the lat 中心纬度
     * @param iny    the iny 烈度
     * @param region the region 编辑区域
     */
    public EditRegion(double lng, double lat, float iny, GeoRegion region) {
        _longitude = lng;
        _latitude = lat;
        _intensity = iny;
        _geoRegion = region;
    }

    /**
     * Gets the longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return _longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude the new longitude
     */
    public void setLongitude(double longitude) {
        _longitude = longitude;
    }

    /**
     * Gets the latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return _latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude the new latitude
     */
    public void setLatitude(double latitude) {
        _latitude = latitude;
    }

    /**
     * Gets the intensity.
     *
     * @return the intensity
     */
    public float getIntensity() {
        return _intensity;
    }

    /**
     * Sets the intensity.
     *
     * @param iny the new intensity
     */
    public void setIntensity(float iny) {
        _intensity = iny;
    }

    /**
     * Gets the geo region.
     *
     * @return the geo region
     */
    public GeoRegion getGeoRegion() {
        return _geoRegion;
    }

    /**
     * Sets the geo region.
     *
     * @param geoRegion the new geo region
     */
    public void setGeoRegion(GeoRegion geoRegion) {
        _geoRegion = geoRegion;
    }
}
