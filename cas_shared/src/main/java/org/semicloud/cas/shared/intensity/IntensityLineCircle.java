package org.semicloud.cas.shared.intensity;

import com.supermap.data.*;
import org.semicloud.cas.shared.EpiCenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 线源模型烈度圈
 * 线源模型主要由 3 个参数构成： L 和 R 以及 偏转角度 T
 * L 是线源模型中矩形的长，2 * R 构成矩形的宽，两边再加上两个半圆弧
 * 就是线源模型中的烈度圈了
 * Created by Administrator on 2017/2/7.
 */
public class IntensityLineCircle {
    private double rectangleWidth;
    private double radius;
    private double azimuth;
    private float intensity;
    private EpiCenter epiCenter;
    private Point2D projection;

    /**
     * 生成一个线源烈度圈
     *
     * @param epiCenter      震中经纬度
     * @param projection     震中经纬度的投影值
     * @param rectangleWidth 线源烈度圈矩形部分的宽度
     * @param radius         线源烈度圈圆部分的半径
     * @param azimuth        线源烈度圈的偏转角度
     * @param intensity
     */
    public IntensityLineCircle(EpiCenter epiCenter, Point2D projection, double rectangleWidth, double radius, double azimuth, float intensity) {
        this.rectangleWidth = rectangleWidth;
        this.radius = radius;
        this.azimuth = azimuth;
        this.epiCenter = epiCenter;
        this.intensity = intensity;
        this.projection = projection;
    }

    /**
     * 获取线源烈度圈集合
     *
     * @return
     */
    public List<IntensityLineCircle> getIntensityLineCircle(EpiCenter center, Point2D projection, float magnitude, float depth) {
        return new ArrayList<>();
    }

    /**
     * 获取线源烈度圈的GeoRegion对象
     * （注意：导出的线源烈度圈和计算人口伤亡的线源线源烈度圈投影不一样）
     *
     * @return GeoRegion
     */
    public GeoRegion getGeoRegion() {
        GeoRectangle rectangle = new GeoRectangle(projection, this.rectangleWidth, 2 * this.radius, this.azimuth);
        Point2Ds rectanglePoints = rectangle.convertToRegion().getPart(0);

        Point2D arcCenter1 = new Point2D();
        arcCenter1.setX((rectanglePoints.getItem(0).getX() + rectanglePoints.getItem(1).getX()) / 2);
        arcCenter1.setY((rectanglePoints.getItem(0).getY() + rectanglePoints.getItem(1).getY()) / 2);
        GeoArc arc1 = new GeoArc(arcCenter1, this.radius, 0, 180);
        arc1.rotate(arcCenter1, this.azimuth + 90);

        Point2D arcCenter2 = new Point2D();
        arcCenter2.setX((rectanglePoints.getItem(2).getX() + rectanglePoints.getItem(3).getX()) / 2);
        arcCenter2.setY((rectanglePoints.getItem(2).getY() + rectanglePoints.getItem(3).getY()) / 2);
        GeoArc arc2 = new GeoArc(arcCenter2, this.radius, 0, 180);
        arc2.rotate(arcCenter2, this.azimuth + 270);

        Point2Ds allPoints = new Point2Ds();
        // 加入矩形的第一个点，即左上角的点，逆时针1,2,3,4个点，1，2点对应arc1,3，4点对应arc2
        allPoints.add(rectanglePoints.getItem(0));

        Point2Ds arc1Points = arc1.convertToLine(72).getPart(0);
        for (int i = 0; i < arc1Points.getCount(); i++) {
            allPoints.add(arc1Points.getItem(i));
        }

        allPoints.add(rectanglePoints.getItem(1));
        allPoints.add(rectanglePoints.getItem(2));

        Point2Ds arc2Points = arc2.convertToLine(72).getPart(0);
        for (int i = 0; i < arc2Points.getCount(); i++) {
            allPoints.add(arc2Points.getItem(i));
        }
        // 加入矩形的最后一个点并封闭面对象
        allPoints.add(rectanglePoints.getItem(3));
        allPoints.add(rectanglePoints.getItem(4));
        return new GeoRegion(allPoints);
    }

    public double getRectangleWidth() {
        return rectangleWidth;
    }

    public void setRectangleWidth(double rectangleWidth) {
        this.rectangleWidth = rectangleWidth;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public EpiCenter getEpiCenter() {
        return epiCenter;
    }

    public void setEpiCenter(EpiCenter epiCenter) {
        this.epiCenter = epiCenter;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

}
