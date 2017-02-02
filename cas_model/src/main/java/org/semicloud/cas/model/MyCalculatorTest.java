package org.semicloud.cas.model;

import com.supermap.data.*;
import org.semicloud.cas.model.usv.SimpleGISUtils;
import org.semicloud.cas.model.usv.USVBase;

/**
 * 测试类，已不再使用
 */
@Deprecated
public class MyCalculatorTest extends USVBase {

    /**
     * Draw.
     */
    public static void draw() {
        Datasource d = getTargetDatasource("N34500E10420020150518124129");
        System.out.println(d);
        d.getDatasets().create(getDatasetVectorInfo("thepoints", DatasetType.POINT));
        double x1 = 1.1630211780533321E7, y1 = 4002620.8762624636;
        double x2 = 1.1431662336754441E7, y2 = 4130409.514421884;
        double x3 = 1.1431662336754441E7, y3 = 4002620.8762624636;
        Point2D p1 = new Point2D(x1, y1);
        Point2D p2 = new Point2D(x2, y2);
        Point2D p3 = new Point2D(x3, y3);
        SimpleGISUtils.updateGeometry("thepoints", new GeoPoint(p1), d);
        SimpleGISUtils.updateGeometry("thepoints", new GeoPoint(p2), d);
        SimpleGISUtils.updateGeometry("thepoints", new GeoPoint(p3), d);

        d.getDatasets().create(getDatasetVectorInfo("thelines", DatasetType.LINE));
        Point2Ds line1 = new Point2Ds();
        line1.add(p1);
        line1.add(p2);
        Point2Ds line2 = new Point2Ds();
        line2.add(p1);
        line2.add(p3);
        Point2Ds line3 = new Point2Ds();
        line3.add(p2);
        line3.add(p3);
        SimpleGISUtils.updateGeometry("thelines", new GeoLine(line1), d);
        SimpleGISUtils.updateGeometry("thelines", new GeoLine(line2), d);
        SimpleGISUtils.updateGeometry("thelines", new GeoLine(line3), d);
        closeTargetDatasource();
    }

    private static void giveMePoints() {

        GeoEllipse ellipse = new GeoEllipse();
        ellipse.setCenter(new Point2D(103.1, 30.3));
        ellipse.setRotation(30.0);
        ellipse.setSemimajorAxis(100000);
        ellipse.setSemiminorAxis(60000);
        GeoRegion region = ellipse.convertToRegion(360);
        Point2Ds points = region.getPart(0);
        for (int i = 0; i < points.getCount(); i++) {
            System.out.println(points.getItem(i));
        }
        System.out.println(region);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // draw();
        giveMePoints();
    }
}
