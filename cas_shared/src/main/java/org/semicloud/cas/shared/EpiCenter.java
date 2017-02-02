package org.semicloud.cas.shared;

import com.supermap.data.Point2D;
import org.semicloud.cas.shared.al.SharedDal;

/**
 * 震中
 */
public class EpiCenter {

    /**
     * 震中经度
     */
    private float longitude;

    /**
     * 震中纬度
     */
    private float latitude;

    /**
     * Instantiates a new epi center.
     */
    public EpiCenter() {
    }

    /**
     * Instantiates a new epi center.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     */
    public EpiCenter(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 通过eqID寻找相关地震的震中.
     *
     * @param eqID the eq id
     * @return the epi center
     */
    public static EpiCenter lookup(String eqID) {
        return SharedDal.getEpiCenter(eqID);
    }

    /**
     * Gets the longitude.
     *
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude the new longitude
     */
    public void setLongitude(float longitude) {
        if (longitude > -180 && longitude < 180)
            this.longitude = longitude;
        else {
            throw new IllegalArgumentException("经度范围必须在-180度~180度之间!");
        }
    }

    /**
     * Gets the latitude.
     *
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude the new latitude
     */
    public void setLatitude(float latitude) {
        if (latitude > -90 && latitude < 90) {
            this.latitude = latitude;
        } else {
            throw new IllegalArgumentException("纬度范围必须在-90度~90度之间!");
        }
    }

    /**
     * 转换为Supermap point2d类型
     *
     * @return the point2 d
     */
    public Point2D toPoint2D() {
        return new Point2D(this.longitude, this.latitude);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EpiCenter [longitude=" + longitude + ", latitude=" + latitude + "]";
    }
}
