package org.semicloud.cas.alarm.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.common.ConvertSetting;
import org.semicloud.utils.db.BaseDao;
import org.semicloud.utils.db.factory.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 地震告警消息处理器
 */
public class AlarmClientProcessor extends ClientProcessor {

    /**
     * The dao.
     */
    private static BaseDao dao = DaoFactory.getInstance();

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(AlarmClientProcessor.class);

    /**
     * 把EQ_ID中截取的时间使用年-月-日形式表示.
     *
     * @param str EQID
     * @return the string
     */
    private static String prettyTime(String str) {
        StringBuffer sb = new StringBuffer(str);
        sb.insert(4, "年");
        sb.insert(7, "月");
        sb.insert(10, "日");
        sb.insert(13, "时");
        sb.insert(16, "分");
        sb.insert(19, "秒");
        return sb.toString();
    }

    /*
     * 处理消息
     *
     * @see
     * org.semicloud.cas.alarm.processor.ClientProcessor#process(javax.servlet
     * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public boolean process(HttpServletRequest req, HttpServletResponse resp) {
        return storedAlarm(req, resp);
    }

    /**
     * 存储告警信息.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @return true, if successful
     */
    public boolean storedAlarm(HttpServletRequest req, HttpServletResponse resp) {
        log.info("processing alarm request, save to database...");
        String eqID = req.getParameter("evtId");
        log.info("eqID:" + eqID);

        float lng = Convert.toFloat(req.getParameter("longitude"));
        float lat = Convert.toFloat(req.getParameter("latitude"));
        log.info(text("lng:{0}, lat:{1}", lng, lat));

        String pattern = ConvertSetting.DATETIME_PATTERN;
        Timestamp timestamp = Convert.stringToTimestamp(req.getParameter("time"), pattern);
        log.info("time:" + timestamp.toString());

        float mag = Convert.toFloat(req.getParameter("magnitude"));
        log.info("mag:" + mag);

        String info = req.getParameter("info");
        log.info("info:" + info);

        float depth = Convert.toFloat(req.getParameter("depth"));
        log.info("depth:" + depth);

        String src = req.getParameter("source");
        log.info("source:" + src);

        String code = req.getParameter("code");
        log.info("usgs code:" + code);

        String desc = getEQDescription(req);
        log.info("description:" + desc);

        String cmd = "INSERT INTO basic_eq_event VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        boolean isOK = dao.update(cmd, eqID, lng, lat, timestamp, mag, info, depth, desc, src, 0, code);
        log.info("save above infos to basic_eq_event -> " + isOK);
        return isOK;
    }

    /**
     * 获取地震描述.
     *
     * @param req HttpServletRequest
     * @return 地震描述
     */
    public String getEQDescription(HttpServletRequest req) {
        return req.getParameter("info") + "(" + req.getParameter("longitude") + "," + req.getParameter("latitude")
                + ")" + req.getParameter("magnitude") + "级地震" + ","
                + prettyTime(req.getParameter("evtId").substring(13));
    }

    /*
     * 檢查消息是否合法
     *
     * @see
     * org.semicloud.cas.alarm.processor.ClientProcessor#check(javax.servlet
     * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public boolean check(HttpServletRequest req, HttpServletResponse resp) {
        return true;
    }
}
