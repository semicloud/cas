package org.semicloud.cas.model.al;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.db.factory.DaoFactory;

import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 操作数据库
 */
public class ModelDal {

    /**
     * The _log.
     */
    private static Log log = LogFactory.getLog(ModelDal.class);

    /**
     * 根据EQ_ID查询地震震级
     *
     * @param eqID EQ_ID
     * @return the magnitude
     */
    public static float getMagnitude(String eqID) {
        String cmd = "SELECT MAGNITUDE FROM basic_eq_event WHERE EQ_ID=?";
        float mag = Convert.toFloat(DaoFactory.getInstance().queryScalar(cmd, eqID));
        log.info(text("query mag by eqID={0}, and return mag={1}", eqID, mag));
        return mag;
    }

    /**
     * 根据EQ_ID查询震源深度
     *
     * @param eqID EQ_ID
     * @return the depth
     */
    public static float getDepth(String eqID) {
        String cmd = "SELECT DEPTH FROM basic_eq_event WHERE EQ_ID=?";
        float depth = Convert.toFloat(DaoFactory.getInstance().queryScalar(cmd, eqID));
        log.info(text("query depth by eqID={0}, and return depth={1}", eqID, depth));
        return depth;
    }

    /**
     * 根据EQ_ID查询地震描述
     *
     * @param eqID EQ_ID
     * @return the description
     */
    public static String getDescription(String eqID) {
        String cmd = "SELECT DESCRIPTION FROM basic_eq_event WHERE EQ_ID=?";
        String desc = DaoFactory.getInstance().queryScalar(cmd, eqID).toString();
        log.info("query description with eqID=" + eqID);
        log.info("return description:" + desc);
        return desc;
    }

    /**
     * 保存模型计算结果
     *
     * @param model  模型名称
     * @param eqID   EQ_ID
     * @param taskID TASK_ID
     * @param result 模型计算结果，一个JSON字符串
     * @param url    URL，该列无用处
     * @param b      是否为重计算编辑结果
     * @return true, if successful
     */
    public static boolean saveModelResult(String model, String eqID, String taskID, String result, String url, boolean b) {
        String table = Settings.getModelTable(model);
        String cmd = "INSERT INTO " + table + " VALUES (?,?,?,?,?,?)";
        boolean isOK = DaoFactory.getInstance().update(cmd, eqID, taskID, result, url, b, model);
        log.info(text("save model result({0}units) to {1} -> {2}", result.length(), table, isOK));
        return isOK;
    }

    /**
     * 将计算得到的死亡总数和人口总数存储到数据表.
     *
     * @param eqID  地震ID
     * @param death 全部死亡总数
     * @param pop   全部人口总数
     * @return true, if successful
     */
    public static boolean saveDeathAndPopulationNumber(String eqID, double death, double pop) {
        String cmd = "INSERT INTO casualty(EQ_ID,DEATH,POPULATION) VALUES (?,?,?)";
        boolean b = DaoFactory.getInstance().update(cmd, eqID, death, pop);
        log.info(text("save eqID:{0},death:{1},pop:{2} to table casualty.isOK->{3}", eqID, death, pop, b));
        return b;
    }

    /**
     * 获得某次地震的死亡人数和总人数.
     *
     * @param eqID EQ_ID
     * @return the death and population number
     */
    public static Map<String, Object> getDeathAndPopulationNumber(String eqID) {
        String cmd = "SELECT * FROM (SELECT * FROM casualty WHERE EQ_ID=? "
                + "ORDER BY RECEIVED_TIMESTAMP DESC) WHERE ROWNUM<=1";
        Map<String, Object> map = DaoFactory.getInstance().queryObject(cmd, eqID);
        log.info(text("select death, pop from casualty where eqID={0}", eqID));
        log.info(map.toString());
        return map;
    }

    /**
     * 查询人口伤亡损失模型参数.
     *
     * @param isoCode 国家的isoCode
     * @return Map<参数名，参数值>
     */
    public static Map<String, Object> getFatalityModelParameter(String isoCode) {
        String cmd = "SELECT * FROM usgs_fatality_params WHERE ISO_CODE=?";
        Map<String, Object> map = DaoFactory.getInstance().queryObject(cmd, isoCode);
        log.info(text("select usgs casualty parameters as follows:"));
        log.info(map.toString());
        return map;
    }

    /**
     * 查询经济损失模型参数.
     *
     * @param isoCode 国家的isoCode
     * @return Map<参数名，参数值>
     */
    public static Map<String, Object> getEconomicModelParameter(String isoCode) {
        String cmd = "SELECT * FROM usgs_economic_params WHERE ISO_CODE=?";
        Map<String, Object> map = DaoFactory.getInstance().queryObject(cmd, isoCode);
        log.info(text("select usgs economic parameters as follows:"));
        log.info(map.toString());
        return map;
    }
}
