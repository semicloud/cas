package org.semicloud.cas.model.re;

import com.supermap.data.Point2Ds;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.sql.NCLOB;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.log.LogLevel;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.attribute.CountryAttribute;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.net.HttpSender;
import org.semicloud.utils.db.BaseDao;
import org.semicloud.utils.db.factory.DaoFactory;
import org.semicloud.utils.db.tool.OracleTypeConverter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.semicloud.cas.log.CasLogger.getCurrentTime;
import static org.semicloud.cas.log.CasLogger.writeLogToDB;
import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 重计算模型的基类.
 *
 * @author Semicloud
 */
public abstract class RE_BASE_MODEL {

    /**
     * 起始计算烈度
     */
    protected static final float START_INY = Settings.getModelStartIntensity();
    /**
     * 烈度步长
     */
    protected static final float INY_STEP = 0.5f;
    /**
     * The _log.
     */
    protected static Log _log = LogFactory.getLog(RE_BASE_MODEL.class);
    /**
     * 地震ID
     */
    protected String _eqID;//
    /**
     * 任务ID
     */
    protected String _taskID;//
    /**
     * 人工编辑对象
     */
    protected EditResult _editResult; //
    /**
     * 震中地区（烈度最大的地区）对象
     */
    protected EditRegion _keyRegion; //
    /**
     * 震中对象
     */
    protected EpiCenter _center; //
    /**
     * 结果JSON对象
     */
    protected JSONObject _resultJsonObject;
    /**
     * 模型名称
     */
    private String _modelName;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public RE_BASE_MODEL(EditResult editResult, String modelName) {
        super();

        setModelName(modelName);
        _editResult = editResult;
        _eqID = editResult.getEqID();
        _taskID = editResult.getTaskID();
        _keyRegion = findKeyRegion();
        _center = ModelGal.getVerseEpiCenter(_keyRegion.getLongitude(), _keyRegion.getLatitude());

        _resultJsonObject = new JSONObject();
        _resultJsonObject.put("eqID", _eqID);
        _resultJsonObject.put("taskID", _taskID);
    }

    /**
     * 获得模型研判结果，以JSON串的形式返回
     *
     * @return the json
     */
    public abstract String getJson();

    /**
     * 获得编辑前的模型研判结果
     *
     * @param oldTableName the old table name
     * @return the old
     */
    protected String getOld(String oldTableName) {
        String sql = "select data from " + oldTableName + " where eq_id=? and task_id=?";
        BaseDao dao = DaoFactory.getInstance();
        Object scalar = dao.queryScalar(sql, _eqID, _eqID + "_IMME");
        if (scalar == null) {
            _log.error("未查询到" + oldTableName + "数据!");
            return "NOT FOUND!";
        }
        return OracleTypeConverter.clobToString((NCLOB) dao.queryScalar(sql, _eqID, _eqID + "_IMME"));
    }

    /**
     * 打印开始计算的日志.
     */
    protected void logStart() {
        _log.info(StringUtils.center(text("{0}", Settings.getModelDisplay(getModelName())), 80, "-"));
        _log.info(text("eqID:{0}", _eqID));
        _log.info(text("taskID:{0}", _taskID));
        _log.info(text("started at:{0}", new Date()));
    }

    /**
     * 打印结束日志.
     */
    protected void logEnd() {
        _log.info(text("{0} complete @ {1}", Settings.getModelDisplay(getModelName()), new Date()).trim());
        _log.info(StringUtils.repeat("-", 80));
    }

    /**
     * 获得国家缩写
     *
     * @return the country abbr
     */
    protected String getCountryAbbr() {
        EditRegion keyRegion = findKeyRegion();
        double lng = keyRegion.getLongitude();
        double lat = keyRegion.getLatitude();
        EpiCenter center = ModelGal.getVerseEpiCenter(lng, lat);
        CountryAttribute attribute = CountryAttribute.lookup(center);
        return attribute.getCountryAbbr();
    }

    /**
     * 找到烈度为iny的编辑区域.
     *
     * @param iny the iny
     * @return the edits the region
     */
    protected EditRegion findRegion(final float iny) {
        Predicate p = new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                EditRegion region = (EditRegion) object;
                return region.getIntensity() == iny;
            }
        };
        return (EditRegion) CollectionUtils.find(_editResult.getRegions(), p);
    }

    /**
     * 得到该烈度下的所有点的集合.
     *
     * @param iny 烈度
     * @return the region points
     */
    protected JSONArray getRegionPoints(final float iny) {
        EditRegion region = findRegion(iny);
        JSONArray points = new JSONArray();
        if (region != null) {
            Point2Ds point2Ds = region.getGeoRegion().getPart(0);
            for (int i = 0; i < point2Ds.getCount(); i++) {
                JSONObject point = new JSONObject();
                point.put("x", point2Ds.getItem(i).getX());
                point.put("y", point2Ds.getItem(i).getY());
                points.add(point);
            }
        }
        return points;
    }

    /**
     * 获得编辑后烈度最大的编辑区域.
     *
     * @return EditRegion
     */
    protected EditRegion findKeyRegion() {
        List<EditRegion> regions = _editResult.getRegions();
        Comparator<EditRegion> comparator = new Comparator<EditRegion>() {
            @Override
            public int compare(EditRegion o1, EditRegion o2) {
                return Float.compare(o1.getIntensity(), o2.getIntensity());
            }
        };
        Collections.sort(regions, comparator);
        return regions.get(regions.size() - 1);
    }

    /**
     * 保存模型研判结果
     *
     * @return true, if successful
     */
    public boolean save() {
        boolean isOK = false;
        if (StringUtils.isNotEmpty(getModelName())) {
            String table = Settings.getModelTable(getModelName());
            isOK = ModelDal.saveModelResult(_modelName, _eqID, _taskID, getJson(), "url", false);
            _log.debug("save model result to table " + table + "->" + isOK);
            writeLogToDB(_eqID, getCurrentTime(), LogLevel.INFO, getClass(), _modelName + " 重计算模型计算完成");
        }
        return isOK;
    }

    /**
     * Send.
     *
     * @return true, if successful
     */
    @Deprecated
    public boolean send() {
        boolean isOK = false;
        String code = Settings.getModelPicCode(getModelName());
        if (StringUtils.isNotEmpty(code)) {
            String addr = Settings.getDrawAddress();
            int port = Settings.getDrawPort();
            String servlet = Settings.getDrawPath();

            HttpSender hs = new HttpSender(addr, port, servlet);
            hs.addParameter("eqID", _eqID);
            hs.addParameter("taskID", _taskID);
            hs.addParameter("action", code);

            _log.info(text("prepare to send draw request to http://{0}:{1}{2}", addr, port, servlet));
            _log.info(text("with parameters as follows:"));
            _log.info("eqID:" + _eqID);
            _log.info("taskID:" + _taskID);
            _log.info("action:" + code);

            _log.info("sending request...");

            isOK = (hs.send() == 200);
            _log.info("send complete, result -> " + isOK);
            return isOK;
        }
        return true;
    }

    /**
     * 处理模型
     *
     * @return true, if successful
     */
    public boolean process() {
        logStart();
        boolean process = save();
        // 不再给iServer发消息了
        // boolean process = false;
        // if (save()) {
        // process = send();
        // }
        logEnd();
        return process;
    }

    // public static void main(String[] args) {
    // EditRegion r1 = new EditRegion();
    // r1.setIntensity(3.0f);
    //
    // EditRegion r2 = new EditRegion();
    // r2.setIntensity(5.0f);
    //
    // EditRegion r3 = new EditRegion();
    // r3.setIntensity(7.0f);
    //
    // List<EditRegion> regions = new ArrayList<EditRegion>();
    // regions.add(r1);
    // regions.add(r3);
    // regions.add(r2);
    //
    // System.out.println("Before Sort:");
    // for (EditRegion region : regions) {
    // System.out.println(region.getIntensity());
    // }
    //
    // Comparator<EditRegion> comparator = new Comparator<EditRegion>() {
    // @Override
    // public int compare(EditRegion o1, EditRegion o2) {
    // return Float.compare(o1.getIntensity(), o2.getIntensity());
    // }
    // };
    // Collections.sort(regions, comparator);
    //
    // System.out.println("After Sort:");
    // for (EditRegion region : regions) {
    // System.out.println(region.getIntensity());
    // }
    //
    // EditRegion regionMaxIny = regions.get(regions.size() - 1);
    // System.out.println("Max Iny is:" + regionMaxIny.getIntensity());
    //
    // }

    /**
     * Gets the model name.
     *
     * @return the model name
     */
    public String getModelName() {
        return _modelName;
    }

    /**
     * Sets the model name.
     *
     * @param modelName the new model name
     */
    public void setModelName(String modelName) {
        _modelName = modelName;
    }
}
