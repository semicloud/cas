package org.semicloud.cas.shared.utils;

import com.supermap.data.GeoEllipse;
import org.semicloud.cas.shared.EpiCenter;

/**
 * 几何图形生成类.
 *
 * @author Semicloud
 */
public class SharedGeoGen {

    /**
     * 获得一个以震中为中心，radius为半径的搜索区域.
     *
     * @param epiCenter 震中对象
     * @param radius    搜索半径
     * @return GeoEllipse
     */
    public static GeoEllipse getSearchEllipse(EpiCenter epiCenter, double radius) {
        GeoEllipse ellipse = new GeoEllipse();
        ellipse.setCenter(epiCenter.toPoint2D());
        ellipse.setSemimajorAxis(radius);
        ellipse.setSemiminorAxis(radius);
        ellipse.setRotation(0);
        return ellipse;
    }
}
