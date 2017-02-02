package org.semicloud.cas.model.usv;

import com.supermap.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 简单的GIS操作类
 * <p>
 * 专门用来创建研判结果的数据集.
 */
public class SimpleGISUtils {

    /**
     * 烈度圈的逼近点数
     */
    private static final int SEGMENT_COUNT = 180;

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(SimpleGISUtils.class);

    /**
     * 投影操作
     *
     * @param point2d     the point2d 要投影的点对象
     * @param prjCoordSys the prj coord sys 投影系统
     * @return the projection 投影结果
     */
    public static Point2D getProjection(Point2D point2d, PrjCoordSys prjCoordSys) {
        Point2Ds points = new Point2Ds();
        points.add(point2d);
        CoordSysTranslator.forward(points, prjCoordSys);
        return new Point2D(points.getItem(0).getX(), points.getItem(0).getY());
    }

    /**
     * 投影操作
     *
     * @param point2d    the point2d 要投影的点对象
     * @param datasource the datasource 数据源
     * @return the projection 投影操作
     */
    public static Point2D getProjection(Point2D point2d, Datasource datasource) {
        String prjDatasetName = "BM_Country_PG";
        Point2D ans = getProjection(point2d, getPrjCoordSys(prjDatasetName, datasource));
        log.debug("get projection(" + point2d.getX() + "," + point2d.getY() + ") --> (" + ans.getX() + "," + ans.getY()
                + "), prj_dataset=" + prjDatasetName);
        return ans;
    }

    /**
     * 投影操作
     *
     * @param ellipse    the ellipse 要投影的椭圆对象，其实投影的是椭圆的圆心
     * @param datasource the datasource 数据源
     * @return the projection 投影结果，即投影后的椭圆的圆心
     */
    public static GeoEllipse getProjection(GeoEllipse ellipse, Datasource datasource) {
        Point2D center = ellipse.getCenter();
        ellipse.setCenter(getProjection(center, datasource));
        return ellipse;
    }

    /**
     * 判断一个数据集是否存在
     *
     * @param name       the name 数据集名称
     * @param datasource the datasource 数据源名称
     * @return true or false
     */
    public static boolean existDataset(String name, Datasource datasource) {
        boolean isExist = datasource.getDatasets().contains(name);
        log.debug("exist(" + name + ") --> " + isExist);
        return isExist;
    }

    /**
     * 获取某数据集的投影系统
     *
     * @param dsvName    数据集名称
     * @param datasource the datasource 数据源
     * @return the prj coord sys 投影系统
     */
    public static PrjCoordSys getPrjCoordSys(String dsvName, Datasource datasource) {
        PrjCoordSys sys = null;
        if (existDataset(dsvName, datasource)) {
            sys = datasource.getDatasets().get(dsvName).getPrjCoordSys();
        } else {
            throw new IllegalArgumentException("unexisted dataset[" + dsvName + "].");
        }
        return sys;
    }

    /**
     * 从数据集中查询一个几何对象
     *
     * @param name       the name 数据集名称
     * @param filterExpr the filter expr 查询条件
     * @param datasource the datasource 数据源名称
     * @return the geometry 几何对象
     * @throws NullPointerException the null pointer exception
     */
    public static Geometry getGeometry(String name, String filterExpr, Datasource datasource)
            throws NullPointerException {
        Geometry g = null;
        if (existDataset(name, datasource)) {
            DatasetVector dsv = (DatasetVector) getDataset(name, datasource);
            Recordset recordset = dsv.query(filterExpr, CursorType.STATIC);
            if (recordset != null) {
                if (recordset.getRecordCount() > 0) {
                    g = recordset.getGeometry();
                }
            }
            recordset.close();
            recordset.dispose();
            dsv.close();
        } else {
            throw new IllegalArgumentException("get geometry failed! because the dataset[" + name + "] is not exist.");
        }
        return g;
    }

    /**
     * 将一个几何对象更新到数据集中，目前支持GeoLine,GeoPoint,GeoEllipse,GeoRegion
     *
     * @param name       the name 数据集名称
     * @param geometry   the geometry 几何对象
     * @param datasource the datasource 数据源名称
     * @return true or false
     */
    public static boolean updateGeometry(String name, Geometry geometry, Datasource datasource) {
        boolean isUpdated = false;
        DatasetVector dsv = (DatasetVector) getDataset(name, datasource);
        Recordset recordset = dsv.getRecordset(false, CursorType.DYNAMIC);
        if (recordset != null) {
            log.debug("get recordset(" + name + ") --> true, record_count=" + recordset.getRecordCount());
        }
        if (geometry instanceof GeoLine) {
            recordset.addNew((GeoLine) geometry);
        }
        if (geometry instanceof GeoPoint) {
            recordset.addNew((GeoPoint) geometry);
        }
        if (geometry instanceof GeoEllipse) {
            recordset.addNew(getProjection((GeoEllipse) geometry, datasource).convertToRegion(SEGMENT_COUNT));
        }
        if (geometry instanceof GeoRegion) {
            recordset.addNew((GeoRegion) geometry);
        }
        isUpdated = recordset.update();
        // 防止数据泄露问题，如果不释放Recordset，很容易引起数据泄露问题
        recordset.close();
        recordset.dispose();
        dsv.close();
        if (isUpdated) {
            log.debug("updated(" + name + "," + geometry.getType().toString() + ") --> true");
        } else {
            log.error("updated(" + name + "," + geometry.getType().toString() + ") --> false");
        }
        return isUpdated;
    }

    /**
     * 从数据源中获取数据集对象
     *
     * @param name       the name 数据集名称
     * @param datasource the datasource 数据源
     * @return the dataset 数据集对象
     */
    public static Dataset getDataset(String name, Datasource datasource) {
        Dataset dataset = null;
        if (existDataset(name, datasource)) {
            dataset = datasource.getDatasets().get(name);
            if (dataset != null) {
                log.debug("get dataset (" + name + "," + dataset.getType().toString() + ") --> OK");
            } else {
                log.error("get dataset (" + name + ") --> FAIL");
            }
        } else {
            throw new IllegalArgumentException("get dataset[" + name + "]failed，unexisted dataset[" + name + "]!");
        }
        return dataset;
    }
}
