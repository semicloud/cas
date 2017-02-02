package org.semicloud.cas.shared;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.al.SharedDal;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.db.BaseDao;
import org.semicloud.utils.db.factory.DaoFactory;
import org.semicloud.utils.db.tool.OracleTypeConverter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 地震事件
 */
public class BasicEqEvent implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The _lg.
     */
    private static Log log = LogFactory.getLog(BasicEqEvent.class);
    /**
     * 地震ID
     */
    private String eqID;
    /**
     * 震中经度
     */
    private float longitude;
    /**
     * 震中纬度
     */
    private float latitude;
    /**
     * 发震时刻
     */
    private Timestamp dateTime;
    /**
     * 震级
     */
    private float magnitude;
    /**
     * 地震地点
     */
    private String info;
    /**
     * 告警源
     */
    private String source;
    /**
     * 震源深度
     */
    private float depth;
    /**
     * 地震描述
     */
    private String description;
    /**
     * 是否已处理标识
     */
    private boolean process;

    /**
     * Instantiates a new basic eq event.
     */
    public BasicEqEvent() {
    }

    /**
     * Instantiates a new basic eq event.
     *
     * @param eqID        the eq id EQID
     * @param longitude   the longitude 震中经度
     * @param latitude    the latitude 震中纬度
     * @param dateTime    the date time 发震时刻
     * @param magnitude   the magnitude 震级
     * @param info        the info 发震地点描述
     * @param source      the source 告警源
     * @param depth       the depth 震源深度
     * @param description the description 地震描述
     * @param process     the process 是否已处理标识
     */
    public BasicEqEvent(String eqID, float longitude, float latitude, Timestamp dateTime, float magnitude, String info,
                        String source, float depth, String description, boolean process) {
        this.eqID = eqID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateTime = dateTime;
        this.magnitude = magnitude;
        this.info = info;
        this.source = source;
        this.depth = depth;
        this.description = description;
        this.process = process;
    }

    /**
     * 通过Map构建BasicEqEvent对象.
     *
     * @param map the map
     * @return BasicEqEvent
     */
    public static BasicEqEvent valueOf(Map<String, Object> map) {
        BasicEqEvent event = new BasicEqEvent();
        if (!map.isEmpty()) {
            event.setEqID(map.get("EQ_ID").toString());
            event.setLongitude(Convert.toFloat(map.get("LONGITUDE")));
            event.setLatitude(Convert.toFloat(map.get("LATITUDE")));
            event.setMagnitude(Convert.toFloat(map.get("MAGNITUDE")));
            event.setDateTime(OracleTypeConverter.objectToTimestamp(map.get("ORIGIN_TIME")));
            event.setInfo(map.get("INFO").toString());
            event.setDepth(Convert.toFloat(map.get("DEPTH")));
            event.setSource(map.get("SOURCE").toString());
            event.setDescription(map.get("DESCRIPTION").toString());
            event.setProcess(OracleTypeConverter.toBool(map.get("IS_PROCESS")));
        }
        return event;
    }

    /**
     * 加载所有地震事件
     *
     * @return the list
     */
    public static List<BasicEqEvent> loadAllEvent() {
        return SharedDal.queryAllEvents();
    }

    /**
     * 获得最新的地震事件
     *
     * @return the latest
     */
    public static BasicEqEvent getLatest() {
        return SharedDal.getLatestBasicEQEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicEqEvent [eqID=" + eqID + ", longitude=" + longitude + ", latitude=" + latitude + ", dateTime="
                + dateTime + ", magnitude=" + magnitude + ", info=" + info + ", source=" + source + ", depth=" + depth
                + ", description=" + description + ", process=" + process + "]";
    }

    /**
     * Gets the eq id.
     *
     * @return the eq id
     */
    public String getEqID() {
        return eqID;
    }

    /**
     * Sets the eq id.
     *
     * @param eqID the new eq id
     */
    public void setEqID(String eqID) {
        this.eqID = eqID;
    }

    /**
     * Gets the longitude.
     *
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude the new longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the latitude.
     *
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude the new latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the date time.
     *
     * @return the date time
     */
    public Timestamp getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date time.
     *
     * @param dateTime the new date time
     */
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the magnitude.
     *
     * @return the magnitude
     */
    public float getMagnitude() {
        return magnitude;
    }

    /**
     * Sets the magnitude.
     *
     * @param magnitude the new magnitude
     */
    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Gets the info.
     *
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the info.
     *
     * @param info the new info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the depth.
     *
     * @return the depth
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the depth.
     *
     * @param depth the new depth
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     * Checks if is process.
     *
     * @return true, if is process
     */
    public boolean isProcess() {
        return process;
    }

    /**
     * Sets the process.
     *
     * @param process the new process
     */
    public void setProcess(boolean process) {
        this.process = process;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 将地震事件存入数据库.
     *
     * @return Boolean
     */
    public boolean save() {
        boolean ans = false;
        if (this != null) {
            ans = SharedDal.saveEvent(this);
        }
        return ans;
    }

    /**
     * 标记地震事件为处理状态
     *
     * @return true, if successful
     */
    public boolean markProcess() {
        boolean isOK = SharedDal.markEventIsProcessTrue(this);
        log.info("mark process flag to 1 -> " + isOK);
        return isOK;
    }

    public boolean markIsAnalysis(Boolean analysis) {
        BaseDao dao = DaoFactory.getInstance();
        String sql = "update basic_eq_event set usgs_code=? where eq_id=?";
        return dao.update(sql, analysis.toString(), eqID);
    }
}
