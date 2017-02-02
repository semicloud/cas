package org.semicloud.cas.shared.intensity;

import com.supermap.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.al.SharedGal;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.gis.GISEngine;
import org.semicloud.utils.gis.GISEngineOracle;

import java.util.*;

/**
 * 烈度圈的偏转角计算类.
 *
 * @author Semicloud
 */
public class ArcCalculator {

    /**
     * The _log.
     */
    private static Log _log = LogFactory.getLog(ArcCalculator.class);
    /**
     * GIS引擎
     */
    private static GISEngine _engine = GISEngineOracle.getInstance(Settings.getGisSettings());
    /**
     * 震中
     */
    private EpiCenter _center;

    /**
     * Instantiates a new arc calculator.
     *
     * @param center the center
     */
    public ArcCalculator(EpiCenter center) {
        super();
        _center = center;
    }

    /**
     * 弧度转角度
     *
     * @param arc the arc 弧度值
     * @return the double 角度值
     */
    private static double toDegree(double arc) {
        return arc * 180 / Math.PI;
    }

    /**
     * 获取一个比较器，用来比较断层对象距离震中的远近
     *
     * @return the comparator
     */
    private static Comparator<Map<String, Object>> getComparator() {
        Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                int ans = 0;
                if (map1.get("distance") != null && map2.get("distance") != null) {
                    try {
                        Double d1 = Double.parseDouble(map1.get("distance").toString());
                        Double d2 = Double.parseDouble(map2.get("distance").toString());
                        ans = d1.compareTo(d2);
                    } catch (NullPointerException e) {
                        System.err.println("Null Pointer Exception");
                    } catch (NumberFormatException e) {
                        System.err.println("Number Format Exception");
                    }
                }
                return ans;
            }
        };
        return comparator;
    }

    /**
     * 获取偏转角
     *
     * @return the value
     */
    public double getValue() {
        _log.info("compute arc value...");
        double ans = 0;
        List<Map<String, Object>> result = SharedGal.getFaultInfos(_center);
        if (result.size() == 0) {
            _log.info("未搜索到断层，返回默认偏转角度0");
            return ans;
        }
        Point2D start = new Point2D();
        Point2D end = new Point2D();
        GeoLine nearestPart = getNearestPart(getNearestFault());
        Point2D p1 = nearestPart.getPart(0).getItem(0);
        Point2D p2 = nearestPart.getPart(0).getItem(nearestPart.getPart(0).getCount() - 1);
        _log.info("p1:" + p1);
        _log.info("p2:" + p2);
        if (p1.getX() - p2.getX() >= 0) {
            start = p2;
            end = p1;
        } else {
            start = p1;
            end = p2;
        }
        _log.info("start:" + start);
        _log.info("end:" + end);
        if (start.getY() >= end.getY()) {
            Point2D theThird = new Point2D(start.getX(), end.getY());
            _log.info("the third:" + theThird);
            double startEnd = Geometrist.distance(new GeoPoint(start), new GeoPoint(end));
            double startThird = Geometrist.distance(new GeoPoint(start), new GeoPoint(theThird));
            ans = 180 - toDegree(Math.asin(startThird / startEnd));
        } else {
            Point2D theThird = new Point2D(start.getX(), end.getY());
            _log.info("the third:" + theThird);
            double startEnd = Geometrist.distance(new GeoPoint(start), new GeoPoint(end));
            double startThird = Geometrist.distance(new GeoPoint(start), new GeoPoint(theThird));
            ans = toDegree(Math.asin(startThird / startEnd));
        }
        _log.info("arc calculate complete, value is " + ans);
        return ans;
    }

    /**
     * Gets the double.
     *
     * @return the double
     */
    @Deprecated
    public double getDouble() {
        GeoLine nearestFault = getNearestFault();
        GeoLine nearestPart = getNearestPart(nearestFault);
        Point2D p1 = nearestPart.getPart(0).getItem(0);
        Point2D p2 = nearestPart.getPart(0).getItem(nearestPart.getPart(0).getCount() - 1);
        System.out.println("start:" + p1);
        System.out.println("end:" + p2);

        Point2D p3 = new Point2D(p2.getX(), p1.getY());
        System.out.println(p3);
        double p1p2 = Geometrist.distance(new GeoPoint(p1), new GeoPoint(p2));
        double p1p3 = Geometrist.distance(new GeoPoint(p1), new GeoPoint(p3));
        double p2p3 = Geometrist.distance(new GeoPoint(p2), new GeoPoint(p3));
        System.out.println("p1,p2:" + p1p2);
        System.out.println("p1,p3:" + p1p3);
        System.out.println("p2,p3:" + p2p3);

        double cosp1 = (Math.pow(p1p2, 2) + Math.pow(p1p3, 2) - Math.pow(p2p3, 2)) / (2 * p1p2 * p1p3);
        System.out.println("cos p1:" + cosp1);
        System.out.println("arc cos p1:" + toDegree(Math.acos(cosp1)));

        double sinp2 = Math.asin(p1p3 / p1p2);
        System.out.println("degree:" + (180 - toDegree(sinp2)));

        System.out.println(p2.getY() - p3.getY());
        return 0.0;
    }

    /**
     * 使用的超图的方法.
     *
     * @return the strike
     */
    @Deprecated
    public double getStrike() {
        // 玉树地震 96.6E 33.1N angle 144.505 还好
        // 甘肃定西地震 104.2E 34.5N angle 32.76 不行
        // 四川雅安地震 103.0E 30.3N angle 136.699 不行
        // 四川汶川地震 103.42E 31.01N angle 41.68 还好
        // 云南鲁甸 103.3E 27.1N angle 41.68 134.005 不行
        GeoLine nearestFault = getNearestFault();
        GeoLine nearestPart = getNearestPart(nearestFault);
        Point2D partStart = nearestPart.getPart(0).getItem(0);
        Point2D partEnd = nearestPart.getPart(0).getItem(nearestPart.getPart(0).getCount() - 1);
        _log.info("part point, start:" + partStart + ", end:" + partEnd);
        Point2D centerPoint = _engine.getProjection(new Point2D(_center.getLongitude(), _center.getLatitude()));
        Point2D footPoint = Geometrist.computePerpendicularPosition(centerPoint, partStart, partEnd);
        Point2D p1 = new Point2D(footPoint.getX() - 10000, footPoint.getY());
        Point2D p2 = new Point2D(footPoint.getX() + 10000, footPoint.getY());
        Point2D p3 = partStart;
        Point2D p4 = partEnd;
        GeoArc arc = Geometrist.computeFillet(p1, p2, p3, p4, 100);
        String format = String.format("%f", arc.getSweepAngle());
        return Double.parseDouble(format);
    }

    /**
     * 咱自个的方法.
     *
     * @return the arc
     */
    @Deprecated
    public double getArc() {
        // 玉树地震 96.6E 33.1N angle 40.716 不行
        // 甘肃定西地震 104.2E 34.5N angle 40.89 不行
        // 四川雅安地震 103.0E 30.3N angle 45.659 还好
        // 四川汶川地震 103.42E 31.01N angle 44.912 还好
        // 云南鲁甸 103.3E 27.1N angle 41.68 46.8976 还好
        GeoLine nearestFault = getNearestFault();
        GeoLine nearestPart = getNearestPart(nearestFault);
        Point2D centerPoint = _engine.getProjection(new Point2D(_center.getLongitude(), _center.getLatitude()));
        _log.info("center point :" + centerPoint);
        Point2D partStart = nearestPart.getPart(0).getItem(0);
        Point2D partEnd = nearestPart.getPart(0).getItem(nearestPart.getPart(0).getCount() - 1);
        _log.info("part point, start:" + partStart + ", end:" + partEnd);
        Point2D footPoint = Geometrist.computePerpendicularPosition(centerPoint, partStart, partEnd);
        _log.info("footpoint:" + footPoint);
        // 端点到垂足的长度
        double d1 = Geometrist.distance(new GeoPoint(centerPoint), new GeoPoint(footPoint));
        _log.info("the distance from center point to foot point is " + d1);
        Point2D thirdPoint = new Point2D(footPoint.getX() - d1, footPoint.getY());
        _log.info("third point is " + thirdPoint);
        double d2 = Geometrist.distance(new GeoPoint(centerPoint), new GeoPoint(thirdPoint));
        _log.info("the distance from center point to third point is " + d2);
        // 用余弦定理求其补角的余弦值
        double cos = (d1 * d1 * 2 - d2 * d2) / (2 * d1 * d1);
        double angle = Math.toDegrees(Math.cos(cos));
        _log.info("cos value is " + cos + ", degrees is " + angle);
        double arc = 90 - angle;
        _log.info("use 90 sub angle, result is " + arc);
        return arc;
    }

    /**
     * 获得一个GeoLine中距离震中最近的Part.
     *
     * @param line GeoLine
     * @return 最近的part
     */
    private GeoLine getNearestPart(GeoLine line) {
        Point2D point2d = new Point2D();
        point2d.setX(_center.getLongitude());
        point2d.setY(_center.getLatitude());
        _log.info("before projection, point is " + point2d);
        point2d = _engine.getProjection(point2d);
        _log.info("after projection, point is " + point2d);
        GeoPoint thePoint = new GeoPoint(point2d);
        List<Map<String, Object>> sortedParts = new ArrayList<Map<String, Object>>();
        _log.info("has " + line.getPartCount() + " parts.");
        for (int i = 0; i < line.getPartCount(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            GeoLine thePart = new GeoLine(line.getPart(i));
            double dist = Geometrist.distance(thePoint, thePart);
            map.put("P1", thePart.getPart(0).getItem(0));
            map.put("P2", thePart.getPart(0).getItem(thePart.getPart(0).getCount() - 1));
            map.put("distance", dist);
            _log.info("the point " + thePoint + " to " + (i + 1) + " part, distance is " + dist);
            sortedParts.add(map);
        }
        Collections.sort(sortedParts, getComparator());
        Map<String, Object> map = sortedParts.get(0);
        _log.info("the nearest part is P1:" + map.get("P1") + ",P2:" + map.get("P2") + ",dist:" + map.get("distance"));
        Point2Ds point2Ds = new Point2Ds();
        point2Ds.add((Point2D) map.get("P1"));
        point2Ds.add((Point2D) map.get("P2"));
        return new GeoLine(point2Ds);
    }

    /**
     * 获得最近的断层.
     *
     * @return GeoLine
     */
    private GeoLine getNearestFault() {
        List<Map<String, Object>> result = SharedGal.getFaultInfos(_center);
        Collections.sort(result, getComparator());
        _log.info("after sort, fault infos as follows:");
        for (Map<String, Object> map : result) {
            _log.info(map.toString());
        }
        Map<String, Object> map = result.get(0);
        _log.info("the nearest fault smid=" + map.get("SMID") + " name is " + map.get("name_cn") + ", distance "
                + map.get("distance"));
        int smid = Integer.parseInt(map.get("SMID").toString());
        System.out.println("smid=" + smid);
        return (GeoLine) SharedGal.getGeometryBySmid("ST_Activefault_PL", smid);
    }
}
