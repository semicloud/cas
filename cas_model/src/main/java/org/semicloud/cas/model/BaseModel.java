package org.semicloud.cas.model;

import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.log.LogLevel;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.model.attribute.CountryAttribute;
import org.semicloud.cas.model.attribute.ProvinceAttribute;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.al.SharedDal;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.common.ConvertSetting;
import org.semicloud.utils.common.net.HttpSender;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.semicloud.cas.log.CasLogger.getCurrentTime;
import static org.semicloud.cas.log.CasLogger.writeLogToDB;
import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 应用研判模型（时间点）基类.
 *
 * @author Semicloud
 */
public abstract class BaseModel {
    /**
     * 烈度值计算步长
     */
    protected static final float INTENSITY_STEP = 0.5f;
    /**
     * 烈度值起始计算阈值，由配置文件读出
     */
    protected static final float START_INTENSITY = Settings.getModelStartIntensity();
    /**
     * The _log.
     */
    protected static Log _log = LogFactory.getLog(BaseModel.class);
    /**
     * 地震ID
     */
    protected final String eqID;
    /**
     * 任务ID
     */
    protected final String taskID;
    /**
     * 震中
     */
    protected final EpiCenter epiCenter;
    /**
     * 震级
     */
    protected final float magnitude;
    /**
     * 震源深度
     */
    protected final float depth;
    /**
     * 发震时间
     */
    protected final Timestamp dateTime;
    /**
     * 震中烈度
     */
    protected final float epiIntensity;
    /**
     * 模型名称
     */
    protected String name;
    /**
     * 模型计算结果存储表
     */
    protected String table;
    /**
     * 模型展示名称
     */
    protected String display;
    /**
     * 模型出图代码
     */
    @Deprecated
    protected String drawPicCode;
    /**
     * 模型计算结果JSON字符串
     */
    protected JSONObject resultJSONObject;
    /**
     * 国家属性
     */
    private CountryAttribute countryAttribute;
    /**
     * 省份属性
     */
    private ProvinceAttribute provinceAttribute;
    /**
     * 烈度圈集合
     */
    private List<IntensityCircle> circles;

    /**
     * 构造函数.
     *
     * @param initializer ModelInitializer对象
     * @param modelName   模型名称
     */
    public BaseModel(ModelInitializer initializer, String modelName) {
        _log.info(StringUtils.center("initialize base model", 80, "-"));
        // 地震事件本身的属性
        this.eqID = initializer.getEqID();
        _log.info("eqID:" + eqID);

        this.taskID = initializer.getTaskID();
        _log.info("taskID:" + taskID);

        this.epiCenter = initializer.getEpiCenter();
        _log.info(text("epi center, lng={0} lat={1}", epiCenter.getLongitude(), epiCenter.getLatitude()));

        this.magnitude = initializer.getMagnitude();
        _log.info("mag:" + magnitude);

        this.depth = initializer.getDepth();
        _log.info("depth:" + depth);

        this.dateTime = initializer.getDateTime();
        _log.info("date:" + dateTime.toString());

        this.epiIntensity = initializer.getEpiIntensity();
        _log.info("epi intensity:" + epiIntensity);

        // 可以通过地震事件本身的属性进行查询、计算得到的属性
        this.countryAttribute = initializer.getCountryAttribute();
        _log.info("country name cn:" + countryAttribute.getNameCN());
        this.provinceAttribute = ProvinceAttribute.lookup(epiCenter);
        _log.info("province name cn:" + provinceAttribute.getNameCN());

        this.circles = initializer.getCircles();
        _log.info("initialize circles, as follows:");
        for (IntensityCircle c : circles) {
            _log.info("circle, where intensity:" + c.getIntensity());
            _log.info("long axis:" + c.getLongAxis());
            _log.info("short axis:" + c.getShortAxis());
            _log.info("azimuth:" + c.getAzimuth());
        }

        // 模型计算过程的配置属性
        this.name = modelName;
        _log.info("model name:" + modelName);

        this.table = Settings.getModelTable(modelName);
        _log.info("table name:" + table);

        this.display = Settings.getModelDisplay(modelName);
        _log.info("display name:" + display);

        this.drawPicCode = Settings.getModelPicCode(modelName);
        _log.info("draw pic code:" + drawPicCode);

        // 初始化结果JSON对象
        this.resultJSONObject = new JSONObject();
        this.resultJSONObject.put("eqID", eqID);
        this.resultJSONObject.put("taskID", taskID);
        _log.info(StringUtils.repeat("-", 80));
    }

    /**
     * 判断一个模型是否展开计算.
     *
     * @param eqID EQID
     * @return true, if successful
     */
    @Deprecated
    public static boolean willComputed(String eqID) {
        boolean isOK = upToMagnitude(eqID) && withARingHasCountry(eqID);
        return isOK;
    }

    /**
     * 判断这次地震是否达到研判的震级和震中烈度
     *
     * @param eqID EQID
     * @return true, if successful
     */
    private static boolean upToMagnitude(String eqID) {
        float mag = ModelDal.getMagnitude(eqID);
        // EpiCenter epiCenter = SharedDal.getEpiCenter(eqID);
        float depth = SharedDal.getDepth(eqID);
        float epi = 0;
        try {
            // epi = Epi.getValue(mag, depth);
            epi = SharedCpt.getEpiIntensity(mag, depth);
        } catch (Exception ex) {
            _log.info("没有查询到震级为" + mag + "，深度为" + depth + "的震中烈度！");
        }
        float start = Settings.getModelStartIntensity();
        _log.info(text("start iny:{0}, epi iny:{1}, is need to compute? -> {2}", start, epi, epi >= start));
        return epi >= start;
    }

    /**
     * 判断如果已震中为圆心，一定距离为半径的圆中是否有国家.
     *
     * @param eqID the EQID
     * @return true, if successful
     */
    private static boolean withARingHasCountry(String eqID) {
        return ModelGal.hasCountry(EpiCenter.lookup(eqID));
    }

    /**
     * 获取烈度值为intensity的烈度圈对象.
     *
     * @param intensity 烈度值
     * @return the intensity circle
     */
    protected IntensityCircle getIntensityCircle(final float intensity) {
        List<IntensityCircle> list = getCircles();
        IntensityCircle circle = null;
        if (list != null && list.size() > 0) {
            Predicate predicate = new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    IntensityCircle circle = (IntensityCircle) object;
                    return circle.getIntensity() == intensity;
                }
            };
            circle = (IntensityCircle) CollectionUtils.find(list, predicate);
        }
        _log.info("find intensity circle by intensity:" + intensity + ", -> " + (circle != null));
        return circle;
    }

    /**
     * 获得烈度圈.
     *
     * @return ListOfIntensityCircle
     */
    public List<IntensityCircle> getCircles() {
        return circles;
    }

    /**
     * 获得震中.
     *
     * @return the epi center
     */
    protected EpiCenter getEpiCenter() {
        return epiCenter;
    }

    /**
     * 获得国家级属性信息.
     *
     * @return the country attribute
     */
    protected CountryAttribute getCountryAttribute() {
        return countryAttribute;
    }

    /**
     * 获得省份数据.
     *
     * @return the province attribute
     */
    protected ProvinceAttribute getProvinceAttribute() {
        return provinceAttribute;
    }

    /**
     * 获得震级.
     *
     * @return the magnitude
     */
    protected float getMagnitude() {
        return magnitude;
    }

    /**
     * 获得模型名称.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 获得模型展示名称
     *
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * 将模型计算结果保存到数据库中.
     *
     * @return true, if successful
     */
    protected boolean save() {
        boolean isOK = false;
        try {
            isOK = ModelDal.saveModelResult(name, eqID, taskID, getJson(), "url", false);
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }
        _log.info("save model result to table " + table + " -> " + isOK);
        writeLogToDB(eqID, getCurrentTime(), LogLevel.INFO, getClass(), name + " 模型计算完成");
        return isOK;
    }

    /**
     * 打印开始计算的日志.
     */
    private void logStart() {
        String start = Convert.dateToString(new Date(), ConvertSetting.LONG_DATE_TIME_PATTERN);
        _log.info(StringUtils.center(text("{0} started @ {1}", getDisplay(), start), 80, "-"));
        _log.info(text("eqID:{0}", eqID));
        _log.info(text("taskID:{0}", taskID));
    }

    /**
     * 打印结束日志.
     */
    private void logEnd() {
        _log.info(text("{0} complete @ {1}", getDisplay(), new Date()).trim());
        _log.info(StringUtils.repeat("-", 80));
    }

    /**
     * 需要出图的模型发送到出图端进行出图，该方法已不再使用
     *
     * @return true, if successful
     * @deprecated
     */
    @Deprecated
    protected boolean send() {
        boolean isSend = false;
        if (drawPicCode != null) {
            if (!drawPicCode.equals("")) {
                String addr = Settings.getDrawAddress();
                int port = Settings.getDrawPort();
                String prefix = Settings.getDrawPath();

                HttpSender sender = new HttpSender(addr, port, prefix);
                sender.addParameter("eqID", eqID);
                sender.addParameter("taskID", taskID);
                sender.addParameter("action", drawPicCode);

                _log.info(text("prepare to send draw request to http://{0}:{1}{2}", addr, port, prefix));
                _log.info(text("with parameters as follows:"));
                _log.info("eqID:" + eqID);
                _log.info("taskID:" + taskID);
                _log.info("action:" + drawPicCode);

                _log.info("sending request...");
                boolean isOK = (sender.send() == 200);
                _log.info("send complete, result -> " + isOK);
                return isOK;
            }
        } else {
            isSend = true;
        }
        return isSend;
    }

    /**
     * 操作模型.
     *
     * @return true, if successful
     */
    public boolean process() {
        logStart();
        boolean process = save();
        logEnd();
        return process;
    }

    /**
     * 获得模型研判结果，模型研判结果封装为JSON对象的形式，虚方法，需要子类实现
     *
     * @return the json
     */
    abstract public String getJson();

    // abstract public void createDatasetVector();
}
