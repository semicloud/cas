package org.semicloud.cas.model.al;

import com.supermap.analyst.spatialanalyst.GridStatisticsMode;
import com.supermap.data.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityBelt;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.cas.shared.utils.SharedGeoGen;
import org.semicloud.utils.gis.EngineSettings;
import org.semicloud.utils.gis.GISEngine;
import org.semicloud.utils.gis.GISEngineOracle;
import org.semicloud.utils.gis.param.OverlayAnalystParameterBuilder;
import org.semicloud.utils.gis.param.ParamHelper;
import org.semicloud.utils.gis.param.ZonalAnalysisParameterBuilder;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

import static org.semicloud.cas.shared.cfg.Settings.*;
import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 操作GIS数据源
 */
public class ModelGal {
    /**
     * 烈度圈数据集前缀
     */
    private static final String INY_CIRCLE_NAME = "INY_CIRCLE_";
    /**
     * 分带统计数据集前缀
     */
    private static final String ZONAL_RESULT_NAME = "ZON_RLT_";
    /**
     * 擦除操作结果数据集前缀
     */
    private static final String EARSE_RESULT_NAME = "EAR_RLT_";
    /**
     * 相交操作结果数据集前缀
     */
    private static final String INTERSECT_RESULT_NAME = "INC_RLT_";
    /**
     * 县级市中间结果数据集前缀
     */
    private static final String TMP_COUNTY_NAME = "TEP_COUNTY_";
    /**
     * 编辑后的区域数据集前缀
     */
    private static final String EDIT_REGION_NAME = "EDIT_REGION_";

    /**
     * 要保留的数据列名称列表
     */
    private static final String[] RAW_RETAINED_COLS = {"SMID"};

    /**
     * 县级市数据集要保留的数据列名称列表
     */
    private static final String[] COUNTY_RETAINED_COLS = {"SMID", "name_cn"};
    /**
     * 生成数据集名称中随机字符的位数
     */
    private static final int RAND_NAME_LEN = 6;
    /**
     * 随机生成数据集名称的字符表
     */
    private static final String RAND_NAME_DOMAIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * The _log.
     */
    private static Log _log = LogFactory.getLog(ModelGal.class);
    /**
     * EngineSettings
     */
    private static EngineSettings _settings = Settings.getGisSettings();
    /**
     * GISEngine对象
     */
    private static GISEngine _engine = GISEngineOracle.getInstance(_settings);

    /**
     * Instantiates a new model gal.
     */
    private ModelGal() {
    }

    /**
     * 反投影后，然后封装为震中
     *
     * @param x x坐标
     * @param y y坐标
     * @return the verse epi center
     */
    public static EpiCenter getVerseEpiCenter(double x, double y) {
        Point2D in = new Point2D(x, y);
        Point2D out = _engine.getVerseProjection(in);
        EpiCenter epiCenter = new EpiCenter((float) out.getX(), (float) out.getY());
        _log.info(text("get verse epicenter ({0},{1})->({2},{3})", x, y, epiCenter.getLongitude(),
                epiCenter.getLatitude()));
        return epiCenter;
    }

    /**
     * 为数据集生成临时的标识符.
     *
     * @param base 生成数据集标识符的前缀，一般是其类别
     * @return 生成的数据集标识符
     */
    private static String genName(String base) {
        return base.concat(RandomStringUtils.random(RAND_NAME_LEN, RAND_NAME_DOMAIN));
    }

    /**
     * 创建烈度带数据集，并返回烈度带数据集的名称.
     *
     * @param ib 烈度带
     * @return 创建的烈度带数据集名称
     */
    private static String createBeltRegionVectorAndReturnName(IntensityBelt ib) {
        String result = StringUtils.EMPTY;
        IntensityCircle bigCircle = ib.getBigCircle();
        IntensityCircle smallCircle = ib.getSmallCircle();
        String bigInyCircleDatasetVectorName = genName(INY_CIRCLE_NAME);
        String smallInyCircleDatasetVectorName = genName(INY_CIRCLE_NAME);
        String earseResultDatasetVector = genName(EARSE_RESULT_NAME);
        boolean bigCircleCreated = _engine.createDatasetVector(bigInyCircleDatasetVectorName, bigCircle.toGeoEllipse());
        boolean smallCircleCreated = _engine.createDatasetVector(smallInyCircleDatasetVectorName,
                smallCircle.toGeoEllipse());
        if (bigCircleCreated && smallCircleCreated) {
            OverlayAnalystParameterBuilder builder = new OverlayAnalystParameterBuilder(bigInyCircleDatasetVectorName,
                    RAW_RETAINED_COLS, smallInyCircleDatasetVectorName, RAW_RETAINED_COLS, earseResultDatasetVector,
                    DatasetType.REGION);
            if (_engine.earse(builder)) {
                result = earseResultDatasetVector;
            } else {
                _log.error("earse operation emit EXCEPTIONS!!!");
            }
            _engine.closeDataset(bigInyCircleDatasetVectorName);
            _engine.closeDataset(smallInyCircleDatasetVectorName);
            _engine.deleteDatasets(bigInyCircleDatasetVectorName);
            _engine.deleteDatasets(smallInyCircleDatasetVectorName);
        } else {
            _log.error("create belt dataset occured a ERROR!!!");
        }
        return result;
    }

    /**
     * 创建烈度圈数据集，并返回数据集名称.
     *
     * @param ic 烈度圈对象
     * @return 烈度圈对象的数据集
     */
    private static String getCircleRegionVectorName(IntensityCircle ic) {
        String inyCircleDatasetVectorName = genName(INY_CIRCLE_NAME);
        boolean isCreated = _engine.createDatasetVector(inyCircleDatasetVectorName, ic.toGeoEllipse());
        if (!isCreated) {
            inyCircleDatasetVectorName = StringUtils.EMPTY;
        }
        return inyCircleDatasetVectorName;
    }

    /**
     * 根据震中获取机场位置信息.
     *
     * @param epiCenter 震中
     * @return the airports infos
     */
    public static List<Map<String, Object>> getAirportsInfos(EpiCenter epiCenter) {
        return getDatasetVectorAttributesOverSearchRange(epiCenter, getModelAirportSearchRadius(),
                SpatialQueryMode.CONTAIN, getDatasetNameAirport(), getAttributesAirport());
    }

    /**
     * 根据震中位置获取国家信息.
     *
     * @param epiCenter 震中
     * @return the country attributes
     */
    public static Map<String, Object> getCountryAttributes(EpiCenter epiCenter) {
        List<Map<String, Object>> results = getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter,
                getModelCountrySearchRadius(), getDatasetNameCountry(), getAttributesCountry());
        if (results.size() > 0) {
            return results.get(0);
        } else {
            return new HashMap<String, Object>();
        }
    }

    /**
     * 获得国家级文化宗教信息.
     *
     * @param epiCenter 震中
     * @return the country level culture religion
     */
    public static List<Map<String, Object>> getCountryLevelCultureReligion(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameCountryCultureReligion(), getAttributesCountryCultureReligion());
    }

    /**
     * 获得国家级经济情况.
     *
     * @param epiCenter 震中
     * @return the country level economic info
     */
    public static List<Map<String, Object>> getCountryLevelEconomicInfo(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameCountryEconomic(), getAttributesCountryEconomic());
    }

    /**
     * 查询国家级地形地貌信息.
     *
     * @param epiCenter 震中
     * @return the country level landform info
     */
    public static List<Map<String, Object>> getCountryLevelLandformInfo(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameCountryLandform(), getAttributesCountryLandform());
    }

    /**
     * 获得县级市的内点，县级市的内点是指县级市的几何图形中的接近中央的一个点
     *
     * @param countyName 县级市名称
     * @return the county inner point
     */
    public static double[] getCountyInnerPoint(String countyName) {
        Geometry geometry = _engine.getGeometry(getDatasetNameCounty(), "name_cn='" + countyName + "'");
        double[] xy = new double[2];
        if (geometry != null) {
            Point2D point2d = geometry.getInnerPoint();
            xy[0] = point2d.getX();
            xy[1] = point2d.getY();
            _log.info("get inner_point(" + countyName + ") --> (" + xy[0] + "," + xy[1] + ")");
        }
        return xy;
    }

    /**
     * 获得烈度带或者烈度圈中的县级市的死亡人口数.
     *
     * @param datasetName 烈度圈或者烈度带的数据集名称
     * @param gridName    栅格数据集名称
     * @return Map<String,Double>
     */
    private static Map<String, Double> getCountyPopulationNumber(String datasetName, String gridName) {
        Map<String, Double> ans = new HashMap<>();
        String intersectRltDsvName = genName(INTERSECT_RESULT_NAME);
        OverlayAnalystParameterBuilder oBuilder = new OverlayAnalystParameterBuilder(getDatasetNameCounty(),
                COUNTY_RETAINED_COLS, datasetName, RAW_RETAINED_COLS, intersectRltDsvName, DatasetType.REGION);
        if (_engine.intersect(oBuilder)) {
            Map<String, Geometry> countys = _engine.queryGeometriesWithAttr(intersectRltDsvName, "name_cn");
            _log.info("summary populations from county(which county square greater than 1 km^2):");
            for (Entry<String, Geometry> county : countys.entrySet()) {
                String countyName = county.getKey();
                GeoRegion countyRegion = (GeoRegion) county.getValue();
                if (countyRegion.getArea() > 1000 * 1000) {
                    String countyDsvName = genName(TMP_COUNTY_NAME);
                    if (_engine.createDatasetVector(countyDsvName, countyRegion)) {
                        double population = getPopulationNumber(countyDsvName, gridName);
                        double area = countyRegion.getArea();
                        ans.put(countyName, population);
                        _log.info(text("name:{0}, area:{1},population:{2}", countyName, area, population));
                        _engine.closeDataset(countyDsvName);
                        _engine.deleteDatasets(countyDsvName);
                    }
                }
            }
            // 删除烈度圈与县级市相交的数据集
            _engine.closeDataset(intersectRltDsvName);
            _engine.deleteDatasets(intersectRltDsvName);
        }
        // 删除烈度带或烈度圈数据集
        _engine.closeDataset(datasetName);
        _engine.deleteDatasets(datasetName);
        return ans;
    }

    /**
     * 获得编辑区域县级市人口数.
     *
     * @param regions 编辑区域
     * @return the county population under edit region
     */
    public static Map<Float, Map<String, Double>> getCountyPopulationUnderEditRegion(List<EditRegion> regions) {
        Map<Float, Map<String, Double>> map = new HashMap<Float, Map<String, Double>>();
        Comparator<EditRegion> comparator = new Comparator<EditRegion>() {
            @Override
            public int compare(EditRegion o1, EditRegion o2) {
                return Float.compare(o1.getIntensity(), o2.getIntensity());
            }
        };
        Collections.sort(regions, comparator);

        Transformer t = new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((EditRegion) input).getIntensity();
            }
        };
        @SuppressWarnings("unchecked")
        Collection<Float> intensities = CollectionUtils.collect(regions, t);
        _log.info("after sort, the intensities of edit regions is " + StringUtils.join(intensities, ","));

        EditRegion keyRegion = regions.get(regions.size() - 1);
        float keyIny = keyRegion.getIntensity();
        double keyLng = keyRegion.getLongitude();
        double keyLat = keyRegion.getLatitude();
        EpiCenter center = getVerseEpiCenter(keyLng, keyLat);
        String grid = getPopulationDatasetGridName(center);
        _log.info("key region intensity is " + keyIny);

        for (EditRegion region : regions) {
            // 如果不是最内圈
            if (region.getIntensity() != keyIny) {
                _log.info("compute edit BELT population.");
                int cur = regions.indexOf(region);
                _log.info("cur:" + cur);
                int nxt = cur + 1;
                _log.info("nxt:" + nxt);
                EditRegion curRegion = regions.get(cur);
                EditRegion nxtRegion = regions.get(nxt);
                String belt = createEditBeltDatasetVectorAndReturnName(curRegion, nxtRegion);
                if (StringUtils.isNotEmpty(belt)) {
                    Map<String, Double> popInfo = getCountyPopulationNumber(belt, grid);
                    map.put(curRegion.getIntensity(), popInfo);
                }
            } else {
                // 最内圈的处理
                String name = genName(EDIT_REGION_NAME);
                boolean isCreated = _engine.createDatasetVector(name, region.getGeoRegion());
                if (isCreated) {
                    Map<String, Double> popInfo = getCountyPopulationNumber(name, grid);
                    map.put(region.getIntensity(), popInfo);
                }
            }
        }
        _log.info(StringUtils.repeat("-", 100));
        return map;
    }

    /**
     * 创建编辑后的烈度带并返回该烈度带的名称
     *
     * @param r1 编辑区域（大）
     * @param r2 编辑区域（小）
     * @return the string
     */
    private static String createEditBeltDatasetVectorAndReturnName(EditRegion r1, EditRegion r2) {
        String name = StringUtils.EMPTY;
        _log.info("now create edit BELT dataset vector.");
        // r2的烈度要大于r1，即r1的面积要大于r2，r1是大圈，r2是小圈
        if (r2.getIntensity() - r1.getIntensity() > 0) {
            String r1Name = genName(EDIT_REGION_NAME);
            _log.info("r1 name:" + r1Name);

            String r2Name = genName(EDIT_REGION_NAME);
            _log.info("r2 name:" + r2Name);

            String rltName = genName(EARSE_RESULT_NAME);
            _log.info("rlt name:" + rltName);

            boolean r1Created = _engine.createDatasetVector(r1Name, r1.getGeoRegion());
            boolean r2Created = _engine.createDatasetVector(r2Name, r2.getGeoRegion());
            boolean ready = r1Created && r2Created;
            if (ready) {
                OverlayAnalystParameterBuilder builder = new OverlayAnalystParameterBuilder(r1Name, RAW_RETAINED_COLS,
                        r2Name, RAW_RETAINED_COLS, rltName, DatasetType.REGION);
                if (_engine.earse(builder)) {
                    name = rltName;
                }
                _engine.closeDataset(r1Name);
                _engine.closeDataset(r2Name);
                _engine.deleteDatasets(r1Name);
                _engine.deleteDatasets(r2Name);
            } else {
                _log.error("create edit region datasetvector failed.");
            }
        }
        _log.info("create edit region belt datasetvector complete, name is " + name);
        return name;
    }

    /**
     * 获得烈度圈集合中县级市的人口数.
     *
     * @param circles 烈度圈集合
     * @return Map<烈度值,Map<县级市名称,人口数>>
     */
    public static Map<Float, Map<String, Double>> getCountyPopulationUnderIntensity(List<IntensityCircle> circles) {
        Map<Float, Map<String, Double>> ans = new HashMap<>();
        if (circles.size() > 0) {
            // 对烈度圈对象排序，保证从烈度从低到高开始计算
            Collections.sort(circles);
            // 根据震中经纬度获得人口栅格数据集名称
            String gridName = getPopulationDatasetGridName(circles.get(0).getEpiCenter());
            for (int i = 0; i < circles.size(); i++) {
                // 如果不是最内圈，则求烈度带中的县级市人口数
                if (i != circles.size() - 1) {
                    // 创建一个烈度带对象
                    IntensityBelt ib = new IntensityBelt(circles.get(i), circles.get(i + 1));
                    _log.info("belt----------------------" + ib.getIntensity() + ",big="
                            + ib.getBigCircle().getIntensity() + ",small=" + ib.getSmallCircle().getIntensity());
                    // 创建一个烈度带数据集并返回其名称
                    String beltDsvName = createBeltRegionVectorAndReturnName(ib);
                    // 若创建成功
                    if (!beltDsvName.isEmpty()) {
                        // 计算这个烈度带中的县级市人口数
                        ans.put(ib.getIntensity(), getCountyPopulationNumber(beltDsvName, gridName));
                    }
                    // 如果是最后一个烈度圈
                } else if (i == circles.size() - 1) {
                    // 创建一个烈度圈对象
                    IntensityCircle ic = circles.get(circles.size() - 1);
                    _log.info("circle----------------------" + ic.getIntensity());
                    // 创建烈度圈数据集
                    String circleDsvName = getCircleRegionVectorName(ic);
                    // 如果创建成功
                    if (!circleDsvName.isEmpty()) {
                        ans.put(ic.getIntensity(), getCountyPopulationNumber(circleDsvName, gridName));
                    }
                }
            }
            // 关闭人口栅格数据集
            _engine.closeDataset(gridName);
        }
        return ans;
    }

    /**
     * 获得县级市的SMID.
     *
     * @param countyName 县级市名称
     * @return the county smid
     */
    public static int getCountySMID(String countyName) {
        return Integer.parseInt(_engine.attributeQueryScalar(getDatasetNameCounty(), "name_cn='" + countyName + "'",
                "SMID").toString());
    }

    /**
     * 先根据震中查询面数据集，为空则声称查询区域，继续进行查询.
     *
     * @param epiCenter 震中
     * @param r         搜索半径
     * @param name      搜索数据集名称
     * @param cols      搜索数据集属性
     * @return the dataset vector attributes by epi center else by search region
     */
    private static List<Map<String, Object>> getDatasetVectorAttributesByEpiCenterElseBySearchRegion(
            EpiCenter epiCenter, double r, String name, String[] cols) {
        List<Map<String, Object>> records = getDatasetVectorAttributesOverEpiCenter(epiCenter, SpatialQueryMode.WITHIN,
                name, cols);
        if (records.isEmpty()) {
            records = getDatasetVectorAttributesOverSearchRange(epiCenter, getModelCountrySearchRadius(),
                    SpatialQueryMode.INTERSECT, name, cols);
        }
        return records;
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
     * 检查震中是否掉海里了
     *
     * @param center 震中
     * @return the list
     */
    public static List<Map<String, Object>> isLocSea(EpiCenter center) {
        return getDatasetVectorAttributesOverEpiCenter(center, SpatialQueryMode.WITHIN, "BM_COUNTRY_PG",
                COUNTY_RETAINED_COLS);
    }

    /**
     * 使用以震中点为圆心，r为搜索半径的区域进行搜索，返回形式为List Of Map<String, Object>.
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
        final QueryParameter regionParameter = ParamHelper.getQueryParameter(
                SharedGeoGen.getSearchEllipse(epiCenter, r * _settings.getMapUnit()), mode);
        List<Map<String, Object>> attributes = _engine.spatialQuery(name, cols, regionParameter);
        return attributes;
    }

    /**
     * 根据烈度圈查询数据集属性.
     *
     * @param ic   烈度圈
     * @param mode 空间查询模式
     * @param name 查询数据集名称
     * @param cols 查询数据集属性集合
     * @return the dataset vector attributes over search range
     */
    private static List<Map<String, Object>> getDatasetVectorAttributesOverSearchRange(IntensityCircle ic,
                                                                                       SpatialQueryMode mode, String name, String[] cols) {
        final QueryParameter regionParameter = ParamHelper.getQueryParameter(ic.toGeoEllipse(), mode);
        List<Map<String, Object>> attributes = _engine.spatialQuery(name, cols, regionParameter);
        return attributes;
    }

    /**
     * 获得震中附近的断层信息.
     *
     * @param epiCenter 震中
     * @return the fault infos
     */
    public static List<Map<String, Object>> getFaultInfos(EpiCenter epiCenter) {
        List<Map<String, Object>> records = getDatasetVectorAttributesOverSearchRange(epiCenter,
                getModelFaultSearchRadius(), SpatialQueryMode.INTERSECT, getDatasetNameActiveFault(),
                getAttributesActiveFault());
        if (records.size() > 0) {
            Point2D point2d = _engine.getProjection(epiCenter.toPoint2D());
            GeoPoint g1 = new GeoPoint(point2d);
            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> fault = records.get(i);
                GeoLine g2 = (GeoLine) _engine.getGeometry(getDatasetNameActiveFault(),
                        "SMID=" + Integer.parseInt(fault.get("SMID").toString()));
                if (g2 != null) {
                    double distance = Geometrist.distance(g1, g2);
                    fault.put("distance", Math.rint(distance / 1000));
                }
            }
        }
        Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double d1 = Double.parseDouble(o1.get("distance").toString());
                return d1.compareTo(Double.parseDouble(o2.get("distance").toString()));
            }
        };
        Collections.sort(records, comparator);
        return records;
    }

    /**
     * 查询历史地震信息.
     *
     * @param epiCenter 震中
     * @return the historical eq infos
     */
    public static List<Map<String, Object>> getHistoricalEqInfos(EpiCenter epiCenter) {
        return getDatasetVectorAttributesOverSearchRange(epiCenter, getModelHistorySearchRadius(),
                SpatialQueryMode.CONTAIN, getDatasetNameHistory(), getAttributesHistory());
    }

    /**
     * 获取烈度模型代码.
     *
     * @param epiCenter 震中
     * @return the intensity model code
     */
    public static String getIntensityModelCode(EpiCenter epiCenter) {
        String code = StringUtils.EMPTY;
        List<Map<String, Object>> codes = getDatasetVectorAttributesOverEpiCenter(epiCenter, SpatialQueryMode.WITHIN,
                getDatasetNameModelRange(), new String[]{"MODEL_CODE"});
        if (codes.size() > 0) {
            Map<String, Object> map = codes.get(0);
            if (MapUtils.isNotEmpty(map)) {
                code = map.get("MODEL_CODE").toString();
            }
        }
        return code;
    }

    /**
     * 获得烈度模型编号列表.
     *
     * @param center 震中
     * @return 烈度模型的代码列表
     */
    public static List<String> getIntensityModelCodeList(EpiCenter center) {
        List<String> list = new ArrayList<String>();
        List<Map<String, Object>> codes = getDatasetVectorAttributesOverEpiCenter(center, SpatialQueryMode.WITHIN,
                getDatasetNameModelRange(), new String[]{"MODEL_CODE"});
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
     * 获得极震区.
     *
     * @param ic 烈度圈对象
     * @return the meizoseismal area infos
     */
    public static List<Map<String, Object>> getMeizoseismalAreaInfos(IntensityCircle ic) {
        return getDatasetVectorAttributesOverSearchRange(ic, SpatialQueryMode.CONTAIN, getDatasetNameCountyPoint(),
                getAttributesCountyPoint());
    }

    /**
     * 得到编辑区域中的极震区.
     *
     * @param region 编辑区域
     * @return the meizoseismal area infos
     */
    public static List<Map<String, Object>> getMeizoseismalAreaInfos(EditRegion region) {
        QueryParameter parameter = ParamHelper.getQueryParameter(region.getGeoRegion(), SpatialQueryMode.CONTAIN);
        List<Map<String, Object>> attributes = _engine.spatialQuery(getDatasetNameCountyPoint(),
                getAttributesCountyPoint(), parameter);
        _log.info("query " + attributes.size() + " meizoseismal areas by edit region");
        for (Map<String, Object> map : attributes) {
            _log.info(map);
        }
        return attributes;
    }

    /**
     * 根据震中位置确定人口栅格数据集的名称.
     *
     * @param epiCenter 震中
     * @return the population dataset grid name
     */
    public static String getPopulationDatasetGridName(EpiCenter epiCenter) {
        String name = StringUtils.EMPTY;
        List<Map<String, Object>> records = _engine.spatialQuery(Settings.getDatasetNamePopGrid(),
                new String[]{"Name"}, ParamHelper.getQueryParameter(epiCenter.toPoint2D(), SpatialQueryMode.WITHIN));
        if (records.size() > 0) {
            name = records.get(0).get("Name").toString();
        } else {
            throw new IllegalArgumentException("查询[" + epiCenter + "]处人口栅格数据集失败，不存在的记录!");
        }
        String grid = name.substring(0, name.length() - 4);
        _log.info(text("query grid name:{0}->{1}", epiCenter, grid));
        return grid;
    }

    /**
     * 获取烈度圈中的人口密度.
     *
     * @param circle 烈度圈对象
     * @return double
     */
    public static double getPopulationDensity(IntensityCircle circle) {
        return getPopulationInfo(circle, GridStatisticsMode.MEAN);
    }

    /**
     * 得到编辑区域中的人口密度信息.
     *
     * @param region 编辑区域
     * @return the population density
     */
    public static double getPopulationDensity(EditRegion region) {
        double x = region.getLongitude(), y = region.getLatitude();
        EpiCenter center = getVerseEpiCenter(x, y);
        String grid = getPopulationDatasetGridName(center);
        double density = getGridInfo(region.getGeoRegion(), grid, GridStatisticsMode.MEAN);
        return density;
    }

    /**
     * 获得烈度圈中的人口密度.
     *
     * @param circles 烈度圈集合
     * @return the population density under iny
     */
    public static Map<Float, Integer> getPopulationDensityUnderIny(List<IntensityCircle> circles) {
        // 定义返回值
        Map<Float, Integer> densities = new HashMap<>();
        EpiCenter center = circles.get(0).getEpiCenter();
        String grid = getPopulationDatasetGridName(center);
        for (IntensityCircle circle : circles) {
            densities.put(circle.getIntensity(),
                    (int) getGridInfo(circle.toGeoEllipse(), grid, GridStatisticsMode.MEAN));
        }
        return densities;
        // // 对每一个烈度圈对喜爱那个
        // for (int i = 0; i < circles.size(); i++) {
        // // 生成烈度圈数据集名称
        // String inyCircleDatasetVectorName = genName(INY_CIRCLE_NAME);
        // // 生成分带统计结果数据集名称
        // String resultDatasetVectorName = genName(ZONAL_RESULT_NAME);
        // // 生成分带统计结果数据表名称
        // String resultTableName = genName(ZONAL_RESULT_NAME);
        // // 获取当前烈度圈对象
        // IntensityCircle circle = circles.get(i);
        // // 创建烈度圈对象的数据集
        // boolean isCreated =
        // _engine.createDatasetVector(inyCircleDatasetVectorName,
        // circle.toGeoEllipse());
        // try {
        // // 如果创建成功
        // if (isCreated) {
        // // 获取人口栅格数据集名称
        // String populationDatasetGridName =
        // getPopulationDatasetGridName(circle.getEpiCenter());
        // // 构建分带统计参数
        // ZonalAnalysisParameterBuilder builder = new
        // ZonalAnalysisParameterBuilder(
        // populationDatasetGridName, inyCircleDatasetVectorName,
        // GridStatisticsMode.MEAN, false,
        // resultDatasetVectorName, resultTableName);
        // // 进行分带统计
        // Recordset recordset = _engine.zonalStatisticsAnalysis(builder);
        // // 将结果集移到第一条前面
        // recordset.moveFirst();
        // // 获得结果集中的平均值
        // densities.put(circle.getIntensity(), (int)
        // recordset.getDouble("mean"));
        // recordset.close();
        // recordset.dispose();
        // _engine.closeDataset(populationDatasetGridName);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // _engine.deleteDatasets(resultDatasetVectorName, resultTableName,
        // inyCircleDatasetVectorName);
        // }
        // }
        // return densities;
    }

    /**
     * 查询图形和栅格数据集的操作结果.
     *
     * @param geometry       几何图形
     * @param grid           栅格数据集名称
     * @param statisticsMode 统计模式，Just Now Only Supported SUM, MEAN
     * @return 统计结果
     */
    public static double getGridInfo(Geometry geometry, String grid, GridStatisticsMode statisticsMode) {
        double ans = 0;
        String dsvName = genName(EDIT_REGION_NAME);
        String rltDsvName = genName(ZONAL_RESULT_NAME);
        String rltTabName = genName(ZONAL_RESULT_NAME);
        boolean isOK = _engine.createDatasetVector(dsvName, geometry);
        try {
            if (!isOK)
                throw new Exception("create datasetvector failed.");
            ZonalAnalysisParameterBuilder builder = new ZonalAnalysisParameterBuilder(grid, dsvName, statisticsMode,
                    false, rltDsvName, rltTabName);
            Recordset recordset = _engine.zonalStatisticsAnalysis(builder);
            if (recordset != null) {
                // 将结果集移到第一条前面
                recordset.moveFirst();
                // 获得结果集中结果
                if (statisticsMode == GridStatisticsMode.SUM) {
                    ans = recordset.getDouble("sum_value");
                } else if (statisticsMode == GridStatisticsMode.MEAN) {
                    ans = recordset.getDouble("mean");
                } else {
                    throw new Exception("No Supported Mode, Just Now, Only Supported SUM, MEAN");
                }
                recordset.close();
                recordset.dispose();
            }
            if (ans < 0)
                ans = 0;
            _engine.closeDataset(rltDsvName);
            _engine.closeDataset(rltTabName);
            _engine.closeDataset(dsvName);
            _engine.deleteDatasets(rltDsvName, rltTabName, dsvName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ans;
    }

    /**
     * 获得烈度圈中的人口数据
     *
     * @param circle 烈度圈
     * @param mode   统计模式
     * @return the population info
     */
    private static double getPopulationInfo(IntensityCircle circle, GridStatisticsMode mode) {
        double ans = 0;
        String grid = getPopulationDatasetGridName(circle.getEpiCenter());
        ans = getGridInfo(circle.toGeoEllipse(), grid, mode);
        return ans;
        // double ans = 0;
        // // 生成烈度圈数据集名称
        // String circleDsvName = genName(INY_CIRCLE_NAME);
        // // 生成分带统计结果数据集名称
        // String resultDsvName = genName(ZONAL_RESULT_NAME);
        // // 生成分带统计结果数据表名称
        // String resultTableName = genName(ZONAL_RESULT_NAME);
        // // 创建烈度圈对象的数据集
        // boolean isCreated = _engine.createDatasetVector(circleDsvName,
        // circle.toGeoEllipse());
        // try {
        // // 如果创建成功
        // if (isCreated) {
        // // 获取人口栅格数据集名称
        // String popDsgName =
        // getPopulationDatasetGridName(circle.getEpiCenter());
        // // 构建分带统计参数
        // ZonalAnalysisParameterBuilder builder = new
        // ZonalAnalysisParameterBuilder(popDsgName, circleDsvName,
        // mode, false, resultDsvName, resultTableName);
        // // 进行分带统计
        // Recordset recordset = _engine.zonalStatisticsAnalysis(builder);
        // if (recordset != null) {
        // // 将结果集移到第一条前面
        // recordset.moveFirst();
        // // 获得结果集中结果
        // if (mode == GridStatisticsMode.SUM) {
        // ans = recordset.getDouble("sum_value");
        // } else if (mode == GridStatisticsMode.MEAN) {
        // ans = recordset.getDouble("mean");
        // }
        // recordset.close();
        // recordset.dispose();
        // }
        // _engine.closeDataset(resultDsvName);
        // _engine.closeDataset(resultTableName);
        // _engine.closeDataset(circleDsvName);
        // _engine.deleteDatasets(resultDsvName, resultTableName,
        // circleDsvName);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // return ans;
    }

    /**
     * 得到烈度圈内的县级市信息
     *
     * @param intensityCircle 烈度圈对象
     * @return
     */
    public static List<Map<String, Object>> getCountiesList(IntensityCircle intensityCircle) {
        List<Map<String, Object>> answer = new ArrayList<Map<String, Object>>();
        SpatialQueryMode mode = SpatialQueryMode.INTERSECT;
        String name = Settings.getDatasetNameCounty();
        String[] cols = new String[]{"SMID", "name_cn", "Shape_Area"};
        answer = getDatasetVectorAttributesOverSearchRange(intensityCircle, mode, name, cols);
        return answer;
    }

    /**
     * 获取县级市的人口密度信息
     *
     * @param intensityCircle
     * @return
     */
    public static List<Map<String, Object>> getCountiesPopDensity(IntensityCircle intensityCircle) {
        List<Map<String, Object>> answer = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> countiesList = getCountiesList(intensityCircle);
        for (Map<String, Object> county : countiesList) {
            int smid = Integer.parseInt(county.get("SMID").toString());
            String name = county.get("name_cn").toString();
            Geometry geometry = _engine.getGeometry(Settings.getDatasetNameCounty(), "SMID=" + smid);
            // 县级市面积
            double area = Double.parseDouble(county.get("Shape_Area").toString()) / (1000 * 1000);
            String grid = getPopulationDatasetGridName(intensityCircle.getEpiCenter());
            double[] innerPoint = getCountyInnerPoint(name);
            double density = getGridInfo(geometry, grid, GridStatisticsMode.MEAN);
            _log.info(MessageFormat.format("SMID:{0}, NAME:{1}, INNER_POINT:{2},{3}, DENSITY:{4}, AREA:{5}", smid,
                    name, innerPoint[0], innerPoint[1], density, area));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("smid", smid);
            map.put("name_cn", name);
            map.put("x", innerPoint[0]);
            map.put("y", innerPoint[1]);
            map.put("area", ((Double) area).intValue() + " KM^2");
            map.put("density", ((Double) density).intValue());
            answer.add(map);
        }
        return answer;
    }

    /**
     * 获得烈度圈中的人口总数.
     *
     * @param circle 烈度圈对象
     * @return double
     */
    public static double getPopulationNumber(IntensityCircle circle) {
        return getPopulationInfo(circle, GridStatisticsMode.SUM);
    }

    /**
     * 获得名称为name的面数据集中所覆盖的人口数.
     *
     * @param vectorName 面数据集名称
     * @param gridName   栅格数据集名称
     * @return the population number
     */
    private static double getPopulationNumber(String vectorName, String gridName) {
        double population = 0.0d;
        String resultDatasetName = genName(ZONAL_RESULT_NAME);
        String resultDataTableName = genName(ZONAL_RESULT_NAME);
        ZonalAnalysisParameterBuilder builder = new ZonalAnalysisParameterBuilder(gridName, vectorName,
                GridStatisticsMode.SUM, false, resultDatasetName, resultDataTableName);
        Recordset recordset = _engine.zonalStatisticsAnalysis(builder);
        if (recordset != null && recordset.getRecordCount() > 0) {
            population = Double.parseDouble(recordset.getString("Sum_value"));
            if (population < 0)
                population = 0;
        }
        recordset.close();
        recordset.dispose();
        _engine.closeDataset(resultDatasetName);
        _engine.closeDataset(resultDataTableName);
        _engine.deleteDatasets(resultDatasetName);
        _engine.deleteDatasets(resultDataTableName);
        return population;
    }

    /**
     * 得到一组烈度圈中的人口数.
     *
     * @param circles 烈度圈序列
     * @return Map<烈度值,人口数>
     */
    public static Map<Float, Double> getPopulationNumbers(List<IntensityCircle> circles) {
        Map<Float, Double> result = new HashMap<Float, Double>();
        if (circles.size() > 0) {
            Collections.sort(circles); // 保证烈度圈按烈度值升序排序
            for (int i = 0; i < circles.size(); i++) {
                if (i != circles.size() - 1) {
                    IntensityBelt ib = new IntensityBelt(circles.get(i), circles.get(i + 1));
                    result.put(ib.getIntensity(), getPopulationNumberUnderBelt(ib));
                } else if (i == circles.size() - 1) {
                    IntensityCircle ic = circles.get(i);
                    result.put(ic.getIntensity(), getPopulationNumberUnderCircle(ic));
                }
            }
        }
        return result;
    }

    /**
     * 获取一个编辑区域中的人口数.
     *
     * @param region 编辑区域
     * @return the population number under region
     */
    public static double getPopulationNumberUnderRegion(EditRegion region) {
        _log.info("get population from edit region:" + region);

        double x = region.getLongitude(), y = region.getLatitude();
        _log.info(text("region x:{0}, y:{1}", x, y));

        EpiCenter center = getVerseEpiCenter(x, y);
        _log.info(text("inverse proj -> ({0},{1})", center.getLongitude(), center.getLatitude()));

        String gridName = getPopulationDatasetGridName(center);
        String vectorName = genName(EDIT_REGION_NAME);
        boolean isCreate = _engine.createDatasetVector(vectorName, region.getGeoRegion());
        _log.info(text("vectorName:{0},gridName:{1},vector created? -> {2}", vectorName, gridName, isCreate));

        double population = getPopulationNumber(vectorName, gridName);
        _log.info(text("get population: intensity:{0}, population:{1}", region.getIntensity(), population));

        _engine.closeDataset(vectorName);
        _engine.deleteDatasets(vectorName);
        return population;
    }

    /**
     * 烈度带中的总人口数.
     *
     * @param ib 烈度带对象
     * @return the population number under belt
     */
    public static double getPopulationNumberUnderBelt(IntensityBelt ib) {
        double population = 0.0d;
        String populationDatasetGridName = getPopulationDatasetGridName(ib.getEpiCenter());
        String inyBeltDatasetVectorName = createBeltRegionVectorAndReturnName(ib);
        if (!inyBeltDatasetVectorName.equals("")) {
            population = getPopulationNumber(inyBeltDatasetVectorName, populationDatasetGridName);
            _engine.closeDataset(inyBeltDatasetVectorName);
            _engine.deleteDatasets(inyBeltDatasetVectorName);
        }
        return population;
    }

    /**
     * 获得烈度圈中的总人数.
     *
     * @param ic 烈度圈
     * @return the population number under circle
     */
    public static double getPopulationNumberUnderCircle(IntensityCircle ic) {
        double population = 0.0d;
        String populationDatasetGridName = getPopulationDatasetGridName(ic.getEpiCenter());
        String inyCircleDatasetVectorName = genName(INY_CIRCLE_NAME);
        if (_engine.createDatasetVector(inyCircleDatasetVectorName, ic.toGeoEllipse())) {
            population = getPopulationNumber(inyCircleDatasetVectorName, populationDatasetGridName);
            _engine.closeDataset(inyCircleDatasetVectorName);
            _engine.closeDataset(populationDatasetGridName);
            _engine.deleteDatasets(inyCircleDatasetVectorName);
        }
        return population;
    }

    /**
     * 获得震中的投影值.
     *
     * @param epiCenter 震中
     * @return the projection
     */
    public static double[] getProjection(EpiCenter epiCenter) {
        double[] points = new double[2];
        Point2D point2d = _engine.getProjection(epiCenter.toPoint2D());
        points[0] = point2d.getX();
        points[1] = point2d.getY();
        return points;
    }

    /**
     * 根据震中位置获取省级信息.
     *
     * @param epiCenter 震中
     * @return the province attributes
     */
    public static Map<String, Object> getProvinceAttributes(EpiCenter epiCenter) {
        List<Map<String, Object>> result = getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter,
                getModelCountrySearchRadius() * _settings.getMapUnit(), getDatasetNameProvince(),
                getAttributesProvince());
        if (result.size() > 0)
            return result.get(0);
        return new HashMap<String, Object>();
    }

    /**
     * 获得省级文化宗教信息.
     *
     * @param epiCenter 震中
     * @return the province level culture religion
     */
    public static List<Map<String, Object>> getProvinceLevelCultureReligion(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameProvinceCultureReligion(), getAttributesProvinceCultureReligion());
    }

    /**
     * 获得省市级经济情况.
     *
     * @param epiCenter the epi center
     * @return the province level economic info
     */
    public static List<Map<String, Object>> getProvinceLevelEconomicInfo(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameProvinceEconomic(), getAttributesProvinceEconomic());
    }

    /**
     * 获得省级地形地貌信息.
     *
     * @param epiCenter 震中
     * @return the province level landform info
     */
    public static List<Map<String, Object>> getProvinceLevelLandformInfo(EpiCenter epiCenter) {
        return getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, getModelCountrySearchRadius(),
                getDatasetNameProvinceLandform(), getAttributesProvinceLandform());
    }

    /**
     * 判断震中点200公里内附近是否有国家.
     *
     * @param epiCenter 震中
     * @return true, if successful
     */
    public static boolean hasCountry(EpiCenter epiCenter) {
        boolean ans = true;
        List<Map<String, Object>> records = getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter, 200,
                getDatasetNameCountry(), RAW_RETAINED_COLS);
        if (records.size() == 0) {
            ans = false;
        }
        return ans;
    }

    /**
     * 获得省会城市信息.
     *
     * @param epiCenter 震中
     * @return the prefecture attribute
     */
    public static Map<String, Object> getPrefectureAttribute(EpiCenter epiCenter) {
        List<Map<String, Object>> result = getDatasetVectorAttributesByEpiCenterElseBySearchRegion(epiCenter,
                getModelCountrySearchRadius() * _settings.getMapUnit(), getDatasetNamePrefecture(),
                getAttributesPrefecture());
        Map<String, Object> ans = new HashMap<String, Object>();
        if (result != null) {
            if (result.size() > 0)
                ans = result.get(0);
        }
        return ans;
    }

    // public static void main(String[] args) {
    // EpiCenter epiCenter = new EpiCenter(124.11f, -42.21f);
    // System.out.println(getCountryAttributes(epiCenter));
    // }
}
