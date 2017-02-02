package org.semicloud.cas.alarm.processor;

import net.sf.json.JSONObject;
import oracle.sql.NCLOB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.log.LogLevel;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.db.factory.DaoFactory;
import org.semicloud.utils.db.tool.OracleTypeConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

import static org.semicloud.cas.log.CasLogger.getCurrentTime;
import static org.semicloud.cas.log.CasLogger.writeLogToDB;
import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 人工编辑系统-重计算请求消息处理器
 */
public class EditClientProcessor extends ClientProcessor {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(EditClientProcessor.class);

    /**
     * 保存用户编辑结果
     *
     * @param eqID       EQID
     * @param oldTaskID  旧的Task ID
     * @param taskID     新的TaskID
     * @param jsonObject JSONObject
     * @param timestamp  时间戳
     * @return true, if successful
     */
    public static boolean saveEditResult(String eqID, String oldTaskID, String taskID, JSONObject jsonObject,
                                         Timestamp timestamp) {
        String cmd = "INSERT INTO BASIC_RECOMPUTE_EVENT (EQ_ID,OLD_TASK_ID,TASK_ID,EDIT_RESULT,RECEIVE_TIME) VALUES (?,?,?,?,?)";
        boolean isOK = DaoFactory.getInstance().update(cmd, eqID, oldTaskID, taskID, jsonObject.toString(), timestamp);
        log.info("save re-compute alarm to basic_recompute_event -> " + isOK);
        return isOK;
    }

    /**
     * 获得编辑后的模型结果.
     *
     * @param eqID   地震ID
     * @param taskID 任务ID
     * @return JSONObject
     */
    public static JSONObject getEditJson(String eqID, String taskID) {
        String cmd = "SELECT DATA FROM M_INTENSITY WHERE EQ_ID=? AND TASK_ID=? AND FLAG=1";
        Object scalar = DaoFactory.getInstance().queryScalar(cmd, eqID, taskID);
        JSONObject jsonObject = null;
        if (scalar != null) {
            NCLOB nclob = (NCLOB) scalar;
            String str = OracleTypeConverter.clobToString(nclob);
            jsonObject = JSONObject.fromObject(str);
            log.info("get edit result json -> " + (jsonObject != null));
        } else {
            log.error(text("the edit result not found by:"));
            log.error(text("eqID:{0}", eqID));
            log.error(text("taskID:{0}", taskID));
            try {
                throw new Exception("the json object not found!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return jsonObject;
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
        log.info("processing edit client request...");
        String eqID = req.getParameter("eqID").trim();
        log.info("eqID:" + eqID);

        String oldTaskID = req.getParameter("oldTaskID");
        log.info("oldTaskID:" + oldTaskID);

        JSONObject jsonObject = getEditJson(eqID, oldTaskID);
        if (jsonObject == null)
            return false;

        Timestamp current = new Timestamp(System.currentTimeMillis());
        String taskID = eqID + "_" + Convert.timestampToString(current, "yyyyMMddHHmmss");
        log.info("generate task id:" + taskID);
        writeLogToDB(eqID, getCurrentTime(), LogLevel.INFO, this.getClass(), "重计算请求已接收");
        return saveEditResult(eqID, oldTaskID, taskID, jsonObject, current);
    }

    /*
     * 检查消息是否合法
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
