package org.semicloud.cas.model;

import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.model.attribute.CountryAttribute;
import org.semicloud.cas.model.attribute.ProvinceAttribute;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.cas.shared.intensity.IntensityCircles;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.sql.Timestamp;
import java.util.List;

/**
 * 模型初始化器.
 *
 * @author Victor
 */
public class ModelInitilizer {

    /**
     * 日期格式化字符串
     */
    private static final String DATETIME_PATTERN = "yyyyMMddHHmmss";
    /**
     * EQ_ID
     */
    private String eqID;
    /**
     * Task ID
     */
    private String taskID;
    /**
     * 震中
     */
    private EpiCenter epiCenter;
    /**
     * 震级
     */
    private float magnitude;
    /**
     * 震源深度
     */
    private float depth;
    /**
     * 发震时间
     */
    private Timestamp dateTime;
    /**
     * 起始烈度，由配置文件读出，默认为6度
     */
    private float startIntensity;
    /**
     * 震中烈度
     */
    private float epiIntensity;
    /**
     * 烈度圈列表
     */
    private List<IntensityCircle> circles;
    /**
     * 国家属性
     */
    private CountryAttribute countryAttribute;
    /**
     * 省份属性
     */
    private ProvinceAttribute provinceAttribute;

    /**
     * 构造函数
     *
     * @param eqID   EQ_ID
     * @param taskID TASK_ID
     */
    public ModelInitilizer(String eqID, String taskID) {
        this.eqID = eqID;
        this.taskID = taskID;
        this.magnitude = ModelDal.getMagnitude(eqID);
        this.epiCenter = EpiCenter.lookup(eqID);
        this.depth = ModelDal.getDepth(eqID);// 目前深度好像还没有什么用
        this.dateTime = Convert.stringToTimestamp(eqID.substring(13), DATETIME_PATTERN);
        this.startIntensity = Settings.getModelStartIntensity();
        // this.epiIntensity = SharedCpt.getEpiIntensity(magnitude);
        // TODO 在这里使用了新的方法计算了震中烈度值
        // this.epiIntensity = Epi.getValue(magnitude, depth);
        this.epiIntensity = SharedCpt.getEpiIntensity(magnitude);
        this.circles = IntensityCircles.getCircles(epiCenter, startIntensity, epiIntensity, magnitude, depth).getList();
        this.countryAttribute = CountryAttribute.lookup(epiCenter);
        this.provinceAttribute = ProvinceAttribute.lookup(epiCenter);
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
     * Gets the task id.
     *
     * @return the task id
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Gets the epi center.
     *
     * @return the epi center
     */
    public EpiCenter getEpiCenter() {
        return epiCenter;
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
     * Gets the depth.
     *
     * @return the depth
     */
    public float getDepth() {
        return depth;
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
     * Gets the start intensity.
     *
     * @return the start intensity
     */
    public float getStartIntensity() {
        return startIntensity;
    }

    /**
     * Gets the epi intensity.
     *
     * @return the epi intensity
     */
    public float getEpiIntensity() {
        return epiIntensity;
    }

    /**
     * Gets the circles.
     *
     * @return the circles
     */
    public List<IntensityCircle> getCircles() {
        return circles;
    }

    /**
     * Gets the country attribute.
     *
     * @return the country attribute
     */
    public CountryAttribute getCountryAttribute() {
        return countryAttribute;
    }

    /**
     * Gets the province attribute.
     *
     * @return the province attribute
     */
    public ProvinceAttribute getProvinceAttribute() {
        return provinceAttribute;
    }
}