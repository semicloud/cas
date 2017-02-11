package org.semicloud.cas.shared.intensity;

import com.supermap.data.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.utils.SharedCpt;

import java.text.MessageFormat;
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

    private static Log log = LogFactory.getLog(IntensityLineCircle.class);

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

    public IntensityLineCircle() {
    }

    /**
     * 获取线源模型烈度圈集合
     * 未完成部分：线源模型参数还是要区分中国东部和中国西部，这里还未区分，需要咨询高娜；另外，计算中心烈度时也需要震源深度，这里还没用
     *
     * @param center     震中
     * @param projection 震中经纬度的投影
     * @param magnitude  震级
     * @param depth      震源深度
     * @param step       烈度圈的递增步长
     * @return
     */
    public static List<IntensityLineCircle> getIntensityLineCircles(EpiCenter center, Point2D projection, float magnitude, float depth, float step) {
        List<Double> parameters = Settings.getEastChinaLineCircleParams();
        log.info("加载中国东部线源模型参数：" + StringUtils.join(parameters, ","));
        double a = parameters.get(0), b = parameters.get(1);
        double p1 = parameters.get(2), p2 = parameters.get(3), p3 = parameters.get(4), p4 = parameters.get(5);
        float maxIntensity = SharedCpt.getEpiIntensity(magnitude);
        float minIntensity = Settings.getModelStartIntensity();
        // 生成线源模型烈度圈
        List<IntensityLineCircle> lineCircles = new ArrayList<>();
        ArcCalculator calculator = new ArcCalculator(center);
        double azimuth = calculator.getValue();
        // 烈度最大区域的矩形宽
        double minRectangleWidth = calculateRectangleWidth(a, b, magnitude);
        double factor = 3; // 这个弹性系数东西部地区不同
        double mapUnit = Settings.getGisSettings().getMapUnit(); // GIS地区中的单位，默认是1000
        for (float i = maxIntensity, j = 0; i >= minIntensity; i -= step, j++) {
            IntensityLineCircle lineCircle = new IntensityLineCircle();
            lineCircle.setEpiCenter(center);
            lineCircle.setProjection(projection);
            lineCircle.setIntensity(i);
            lineCircle.setRectangleWidth((minRectangleWidth + minRectangleWidth * j * factor) * mapUnit);
            lineCircle.setRadius(calcuateCircleRadius(p1, p2, p3, p4, magnitude, i) * mapUnit);
            lineCircle.setAzimuth(azimuth);
            lineCircles.add(lineCircle);
            log.info("加入" + lineCircle);
        }
        return lineCircles;
    }

    /**
     * 计算最高烈度的线源模型矩形的宽
     *
     * @return double
     */
    static double calculateRectangleWidth(double a, double b, double magnitude) {
        double width = Math.exp(a * magnitude + b);
        log.info("线源模型矩形宽度：" + width);
        return width;
    }

    /**
     * 计算线源烈度模型中圆部分的半径
     *
     * @param pa        参数a
     * @param pb        参数b
     * @param pc        参数c
     * @param pr        参数d
     * @param magnitude 震级
     * @param intensity 烈度
     * @return double
     */
    static double calcuateCircleRadius(double pa, double pb, double pc, double pr, float magnitude, float intensity) {
        double radius = Math.exp((pa + pb * magnitude - intensity) / (-pc)) - pr;
        log.info("线源模型圆半径：" + radius);
        return radius;
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

    public Point2D getProjection() {
        return projection;
    }

    public void setProjection(Point2D projection) {
        this.projection = projection;
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

    @Override
    public String toString() {
        return MessageFormat.format("线源烈度圈，烈度：{0}，震中（{1},{2} -> {3},{4}）" +
                        "，矩形宽度：{5} KM，圆半径：{6} KM，偏转角：{7}", intensity, epiCenter.getLongitude(), epiCenter.getLatitude(),
                projection.getX(), projection.getY(), rectangleWidth, radius, azimuth);
    }
}
