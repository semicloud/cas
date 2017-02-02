package org.semicloud.cas.shared.al;

import com.supermap.data.GeoEllipse;
import com.supermap.data.GeoRegion;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.sql.NCLOB;
import oracle.sql.TIMESTAMP;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.BasicEqEvent;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.db.factory.DaoFactory;
import org.semicloud.utils.db.tool.OracleTypeConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 数据库操作类
 */
public class SharedDal {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(SharedDal.class);

    /**
     * 通过EQID查询震中，封装为EpiCenter对象返回
     *
     * @param eqID the eq id
     * @return the epi center 震中
     */
    public static EpiCenter getEpiCenter(String eqID) {
        String cmd = "SELECT LONGITUDE,LATITUDE FROM basic_eq_event WHERE EQ_ID=?";
        Map<String, Object> map = DaoFactory.getInstance().queryObject(cmd, eqID);
        EpiCenter epiCenter = new EpiCenter();
        if (map != null && MapUtils.isNotEmpty(map)) {
            epiCenter.setLongitude(Convert.toFloat(map.get("LONGITUDE")));
            epiCenter.setLatitude(Convert.toFloat(map.get("LATITUDE")));
            return epiCenter;
        } else {
            throw new IllegalArgumentException("加载震中错误，不存在的的EQID[" + eqID + "]。");
        }
    }

    /**
     * 通过EQID查询震源深度
     *
     * @param eqID the eq id EQID
     * @return the depth 震源深度
     */
    public static float getDepth(String eqID) {
        String cmd = "SELECT DEPTH FROM basic_eq_event where EQ_ID=?";
        BigDecimal object = (BigDecimal) DaoFactory.getInstance().queryScalar(cmd, eqID);
        return object.floatValue();
    }

    /**
     * 储存地震对象
     *
     * @param event the event
     * @return true, if successful
     */
    public static boolean saveEvent(BasicEqEvent event) {
        String eqID = event.getEqID();
        float lng = event.getLongitude();
        float lat = event.getLatitude();
        Timestamp time = event.getDateTime();
        float mag = event.getMagnitude();
        String info = event.getInfo();
        float dep = event.getDepth();
        String desc = event.getDescription();
        String src = event.getSource();
        boolean process = event.isProcess();
        String cmd = "INSERT INTO basic_eq_event VALUES (?,?,?,?,?,?,?,?,?,?)";
        boolean b = DaoFactory.getInstance().update(cmd, eqID, lng, lat, time, mag, info, dep, desc, src, process);
        log.info("save basic eq event ,eqID:" + eqID + ",->" + b);
        return b;
    }

    /**
     * 查询所有地震对象
     *
     * @return the list
     */
    public static List<BasicEqEvent> queryAllEvents() {
        List<Map<String, Object>> listMaps = DaoFactory.getInstance().queryObjects("SELECT * FROM basic_eq_event");
        List<BasicEqEvent> events = new ArrayList<BasicEqEvent>();
        for (Map<String, Object> map : listMaps) {
            events.add(BasicEqEvent.valueOf(map));
        }
        return events;
    }

    /**
     * 更改地震事件的研判状态.
     *
     * @param event 地震事件
     * @return boolean
     */
    public static boolean markEventIsProcessTrue(BasicEqEvent event) {
        String cmd = "UPDATE basic_eq_event SET IS_PROCESS=? WHERE EQ_ID=?";
        boolean b = DaoFactory.getInstance().update(cmd, 1, event.getEqID());
        log.info(text("mark BASIC_EQ_EVENT{0} state IS_PROCESS to {1} -> {2}", event.getEqID(), 1, b));
        return b;
    }

    /**
     * 更改重计算地震事件的研判状态.
     *
     * @param editResult the edit result
     * @return true, if successful
     */
    public static boolean markEventIsProcessTrue(EditResult editResult) {
        String cmd = "UPDATE basic_recompute_event SET IS_PROCESS=? WHERE EQ_ID=? AND TASK_ID=?";
        boolean b = DaoFactory.getInstance().update(cmd, 1, editResult.getEqID(), editResult.getTaskID());
        log.info(text("mark recompute event({0},{1}) state is_process to {2} -> {3}", editResult.getEqID(),
                editResult.getTaskID(), 1, b));
        return b;
    }

    /**
     * 获取最新的地震事件
     *
     * @return the latest basic eq event
     */
    public static BasicEqEvent getLatestBasicEQEvent() {
        String cmd = "SELECT * FROM (SELECT * FROM basic_eq_event WHERE IS_PROCESS=? ORDER BY ORIGIN_TIME) WHERE ROWNUM<=1";
        Map<String, Object> result = DaoFactory.getInstance().queryObject(cmd, false);
        BasicEqEvent basicEQEvent = null;
        if (result != null) {
            try {
                basicEQEvent = new BasicEqEvent();
                basicEQEvent.setEqID(result.get("EQ_ID").toString());
                basicEQEvent.setLongitude(Float.valueOf(result.get("LONGITUDE").toString()));
                basicEQEvent.setLatitude(Float.valueOf(result.get("LATITUDE").toString()));
                // TODO 异构数据统一中不同数据类型统一的体现
                if (result.get("ORIGIN_TIME") instanceof TIMESTAMP) {
                    // ORACLE Timestamp类型的处理
                    basicEQEvent.setDateTime(((TIMESTAMP) result.get("ORIGIN_TIME")).timestampValue());
                } else {
                    // MySQL Timestamp类型的处理
                    basicEQEvent.setDateTime(Timestamp.valueOf(result.get("ORIGIN_TIME").toString()));
                }
                basicEQEvent.setMagnitude(Float.valueOf(result.get("MAGNITUDE").toString()));
                basicEQEvent.setInfo(result.get("INFO").toString());
                basicEQEvent.setDepth(Float.valueOf(result.get("DEPTH").toString()));
                basicEQEvent.setDescription(result.get("DESCRIPTION").toString());
                basicEQEvent.setProcess(Boolean.valueOf(result.get("IS_PROCESS").toString()));
                basicEQEvent.setSource(result.get("SOURCE").toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return basicEQEvent;
    }

    /**
     * 获得最新的编辑结果，没有返回null.
     *
     * @return the latest edit result
     */
    public static EditResult getLatestEditResult() {
        String cmd = "SELECT * FROM (SELECT * FROM BASIC_RECOMPUTE_EVENT WHERE IS_PROCESS=? "
                + "ORDER BY RECEIVE_TIME) WHERE ROWNUM<=1";
        Map<String, Object> map = DaoFactory.getInstance().queryObject(cmd, false);
        EditResult editResult = null;
        if (MapUtils.isNotEmpty(map)) {
            log.info("query recompute event:" + map);
            String eqID = map.get("EQ_ID").toString();
            // String oldTaskID = map.get("OLD_TASK_ID").toString();
            String taskID = map.get("TASK_ID").toString();
            editResult = new EditResult();
            editResult.setEqID(eqID);
            editResult.setTaskID(taskID);
            List<EditRegion> regions = getRegions(getModelEditResult(eqID, taskID));
            for (EditRegion region : regions) {
                editResult.getRegions().add(region);
            }
        }
        return editResult;
    }

    /**
     * 从编辑后的烈度圈对象中解析出EditRegion.
     *
     * @param str the str
     * @return the regions
     */
    private static List<EditRegion> getRegions(String str) {
        List<EditRegion> regions = new ArrayList<EditRegion>();
        JSONObject jsonObject = JSONObject.fromObject(str);

        JSONArray circles = jsonObject.getJSONArray("circles");

        for (int i = 0; i < circles.size(); i++) {
            JSONObject circle = circles.getJSONObject(i);
            EditRegion editRegion = new EditRegion();
            GeoRegion geoRegion = null;
            if (circle.containsKey("longAxis") && circle.containsKey("shortAxis")) {
                editRegion.setLongitude(circle.getDouble("longitude"));
                editRegion.setLatitude(circle.getDouble("latitude"));
                editRegion.setIntensity(((Double) circle.getDouble("intensity")).floatValue());
                double lngx = circle.getDouble("longAxis");
                double shox = circle.getDouble("shortAxis");
                double azi = circle.getDouble("azimuth");
                editRegion.setIntensity((float) circle.getDouble("intensity"));
                GeoEllipse ellipse = new GeoEllipse();
                ellipse.setCenter(new Point2D(editRegion.getLongitude(), editRegion.getLatitude()));
                ellipse.setSemimajorAxis(lngx);
                ellipse.setSemiminorAxis(shox);
                ellipse.setRotation(azi);
                geoRegion = ellipse.convertToRegion(Settings.getRegionCount());
            }
            if (circle.containsKey("points")) {
                editRegion.setLongitude(circle.getDouble("longitude"));
                editRegion.setLatitude(circle.getDouble("latitude"));
                editRegion.setIntensity(((Double) circle.getDouble("intensity")).floatValue());
                JSONArray points = circle.getJSONArray("points");
                Point2Ds point2Ds = new Point2Ds();
                for (int j = 0; j < points.size(); j++) {
                    JSONObject point = points.getJSONObject(j);
                    Point2D point2d = new Point2D(point.getDouble("x"), point.getDouble("y"));
                    point2Ds.add(point2d);
                }
                geoRegion = new GeoRegion(point2Ds);
            }
            editRegion.setGeoRegion(geoRegion);
            regions.add(editRegion);
        }
        return regions;
    }

    /**
     * 获取编辑后的烈度圈JSON串.
     *
     * @param eqID   the eq id
     * @param taskID the task id
     * @return the model edit result
     */
    private static String getModelEditResult(String eqID, String taskID) {
        String cmd = "SELECT EDIT_RESULT FROM BASIC_RECOMPUTE_EVENT WHERE EQ_ID=? AND TASK_ID=?";
        NCLOB nclob = (NCLOB) DaoFactory.getInstance().queryScalar(cmd, eqID, taskID);
        return OracleTypeConverter.clobToString(nclob);
    }

}
