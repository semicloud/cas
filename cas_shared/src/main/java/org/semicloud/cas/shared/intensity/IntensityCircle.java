package org.semicloud.cas.shared.intensity;

import com.supermap.data.GeoEllipse;
import com.supermap.data.GeoRegion;
import net.sf.json.JSONObject;
import org.semicloud.cas.shared.EpiCenter;

import java.io.Serializable;

/**
 * 烈度圈
 *
 * @author Victor
 */
@SuppressWarnings("serial")
public class IntensityCircle implements Comparable<IntensityCircle>, Serializable {

    /**
     * 震中
     */
    private EpiCenter epiCenter;

    /**
     * 长轴
     */
    private double longAxis;

    /**
     * 短轴
     */
    private double shortAxis;

    /**
     * 偏转角
     */
    private double azimuth;

    /**
     * 烈度值
     */
    private float intensity;

    /**
     * 构造函数.
     *
     * @param epiCenter the epi center 震中
     * @param longAxis  the long axis 长轴
     * @param shortAxis the short axis 短轴
     * @param azimuth   the azimuth 偏转角
     * @param intensity the intensity 烈度值
     */
    public IntensityCircle(EpiCenter epiCenter, double longAxis, double shortAxis, double azimuth, float intensity) {
        this.epiCenter = epiCenter;
        this.longAxis = longAxis;
        this.shortAxis = shortAxis;
        this.azimuth = azimuth;
        this.intensity = intensity;
    }

    /**
     * Instantiates a new intensity circle.
     */
    public IntensityCircle() {
    }

    /**
     * 从JSON对象反向解析出烈度圈对象<br/>
     * 注意，该方法并没有设置烈度圈对象的震中.
     *
     * @param jsonObject 烈度圈对象的JSON表示
     * @return IntensityCircle
     */
    public static IntensityCircle parse(JSONObject jsonObject) {
        IntensityCircle circle = new IntensityCircle();
        if (jsonObject != null) {
            try {
                circle.setLongAxis(jsonObject.getDouble("longAxis"));
                circle.setShortAxis(jsonObject.getDouble("shortAxis"));
                circle.setAzimuth(jsonObject.getDouble("azimuth"));
                circle.setIntensity(Float.parseFloat(jsonObject.get("intensity").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("Intensity JsonObject is NULL");
        }
        return circle;
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

    /**
     * Gets the azimuth.
     *
     * @return the azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Sets the azimuth.
     *
     * @param azimuth the new azimuth
     */
    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
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
     * Gets the long axis.
     *
     * @return the long axis
     */
    public double getLongAxis() {
        return longAxis;
    }

    /**
     * Sets the long axis.
     *
     * @param longAxis the new long axis
     */
    public void setLongAxis(double longAxis) {
        this.longAxis = longAxis;
    }

    /**
     * Gets the short axis.
     *
     * @return the short axis
     */
    public double getShortAxis() {
        return shortAxis;
    }

    /**
     * Sets the short axis.
     *
     * @param shortAxis the new short axis
     */
    public void setShortAxis(double shortAxis) {
        this.shortAxis = shortAxis;
    }

    /**
     * 将烈度圈对象转换为supermap geoellipse对象
     *
     * @return the geo ellipse
     */
    public GeoEllipse toGeoEllipse() {
        GeoEllipse ellipse = new GeoEllipse();
        ellipse.setCenter(epiCenter.toPoint2D());
        ellipse.setSemimajorAxis(this.longAxis);
        ellipse.setSemiminorAxis(this.shortAxis);
        ellipse.setRotation(this.azimuth);
        return ellipse;
    }

    /**
     * 将烈度圈对象转换为supermap georegion 面对象
     *
     * @param segment the segment
     * @return the geo region
     */
    public GeoRegion toGeoRegion(int segment) {
        return toGeoEllipse().convertToRegion(segment);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IntensityCircle [epiCenter=" + epiCenter + ", longAxis=" + longAxis + ", shortAxis=" + shortAxis
                + ", azimuth=" + azimuth + ", intensity=" + intensity + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IntensityCircle o) {
        return Float.valueOf(this.intensity).compareTo(o.getIntensity());
    }
}
