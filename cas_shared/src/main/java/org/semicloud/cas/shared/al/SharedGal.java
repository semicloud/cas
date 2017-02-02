package org.semicloud.cas.shared.al;

import com.supermap.data.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.FastMath;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.utils.SharedGeoGen;
import org.semicloud.utils.gis.EngineSettings;
import org.semicloud.utils.gis.GISEngineOracle;
import org.semicloud.utils.gis.param.ParamHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GIS操作类
 */
public class SharedGal {

    /**
     * GIS设置
     */
    private static EngineSettings _settings = Settings.getGisSettings();

    /**
     * GIS引擎对象
     */
    private static GISEngineOracle _engine = GISEngineOracle.getInstance(_settings);

    public static String getCountryName(double lng, double lat) {
        List<Map<String, Object>> answers = _engine.spatialQuery("BM_COUNTRY_PG", new String[]{"country_ab"},
                ParamHelper.getQueryParameter(new Point2D(lng, lat), SpatialQueryMode.WITHIN));
        if (answers.size() > 0) {
            return answers.get(0).get("country_ab").toString();
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) {
        System.out.println(getCountryName(103.1, 33.3));
    }

    /**
     * 通过smID从数据集中获得几何形状.
     *
     * @param dsvName 数据集名称
     * @param smid    smid
     * @return the geometry by smid
     */
    public static Geometry getGeometryBySmid(String dsvName, int smid) {
        return _engine.getGeometry(dsvName, "smid=" + smid);
    }

    /**
     * 获得烈度模型编号列表.
     *
     * @param center 震中点
     * @return 烈度模型的代码列表
     */
    public static List<String> getIntensityModelCodeList(EpiCenter center) {
        List<String> list = new ArrayList<String>();
        List<Map<String, Object>> codes = getDatasetVectorAttributesOverEpiCenter(center, SpatialQueryMode.WITHIN,
                Settings.getDatasetNameModelRange(), new String[]{"MODEL_CODE"});
        if (codes.size() > 0) {
            for (Map<String, Object> map : codes) {
                if (MapUtils.isNotEmpty(map)) {
                    list.add(map.get("MODEL_CODE").toString());
                }
            }
        }
        return list;
    }

    /**
     * 使用震中点进行查询.
     *
     * @param epiCenter 震中点
     * @param mode      空间查询模式
     * @param name      查询数据集名称
     * @param cols      查询数据集属性
     * @return the dataset vector attributes over epi center
     */
    private static List<Map<String, Object>> getDatasetVectorAttributesOverEpiCenter(EpiCenter epiCenter,
                                                                                     SpatialQueryMode mode, String name, String[] cols) {
        List<Map<String, Object>> attributes = new ArrayList<>();
        final QueryParameter parameter = ParamHelper.getQueryParameter(epiCenter.toPoint2D(), mode);
        attributes = _engine.spatialQuery(name, cols, parameter);
        return attributes;
    }

    /**
     * 获得震中附近的断层信息.
     *
     * @param epiCenter 震中点
     * @return the fault infos
     */
    public static List<Map<String, Object>> getFaultInfos(EpiCenter epiCenter) {
        List<Map<String, Object>> listMaps = getDatasetVectorAttributesOverSearchRange(epiCenter,
                Settings.getModelFaultSearchRadius(), SpatialQueryMode.INTERSECT, Settings.getDatasetNameActiveFault(),
                Settings.getAttributesActiveFault());
        if (listMaps.size() > 0) {
            Point2D point2d = _engine.getProjection(epiCenter.toPoint2D());
            GeoPoint g1 = new GeoPoint(point2d);
            for (int i = 0; i < listMaps.size(); i++) {
                Map<String, Object> fault = listMaps.get(i);
                GeoLine g2 = (GeoLine) _engine.getGeometry(Settings.getDatasetNameActiveFault(),
                        "SMID=" + Integer.parseInt(fault.get("SMID").toString()));
                if (g2 != null) {
                    double distance = Geometrist.distance(g1, g2);
                    fault.put("distance", FastMath.rint(distance / 1000));
                }
            }
        }
        return listMaps;
    }

    /**
     * 使用以震中点为圆心，r为搜索半径的区域进行搜索.
     *
     * @param epiCenter 震中对象
     * @param r         搜索半径
     * @param mode      查询模式
     * @param name      查询数据集名称
     * @param cols      查询数据集属性集合
     * @return the dataset vector attributes over search range
     */
    private static List<Map<String, Object>> getDatasetVectorAttributesOverSearchRange(EpiCenter epiCenter, double r,
                                                                                       SpatialQueryMode mode, String name, String[] cols) {
        GeoEllipse ellipse = SharedGeoGen.getSearchEllipse(epiCenter, r * _settings.getMapUnit());
        QueryParameter parameter = ParamHelper.getQueryParameter(ellipse, mode);
        List<Map<String, Object>> attributes = _engine.spatialQuery(name, cols, parameter);
        return attributes;
    }
}
