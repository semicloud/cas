package org.semicloud.cas.model;

import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.cas.model.attribute.CountryAttribute;
import org.semicloud.cas.model.attribute.ProvinceAttribute;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.IntensityCircle;
import org.semicloud.cas.shared.intensity.IntensityCircles2;
import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

import java.sql.Timestamp;
import java.util.List;

/**
 * 模型初始化器.
 *
 * @author Victor
 */
public class ModelInitializer {

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
    public ModelInitializer(String eqID, String taskID) {
        this.eqID = eqID;
        this.taskID = taskID;
        this.magnitude = ModelDal.getMagnitude(eqID);
        this.epiCenter = EpiCenter.lookup(eqID);
        this.depth = ModelDal.getDepth(eqID);// 目前深度好像还没有什么用
        this.dateTime = Convert.stringToTimestamp(eqID.substring(13), DATETIME_PATTERN);
        this.startIntensity = Settings.getModelStartIntensity();
        // this.epiIntensity = SharedCpt.getEpiIntensity(magnitude);
        // 在这里使用了 王海鹰 的方法计算了震中烈度值
        // this.epiIntensity = Epi.getValue(magnitude, depth); // 6.3级地震，震中烈度是8；6.4级地震，震中烈度也是8
        // 最老的方法，即震级减一然后乘以二分之三下取整的方法
        // this.epiIntensity = SharedCpt.getEpiIntensity(magnitude);
        // 高娜给的方法 imax = 4.15 + 0.11M^2 + 0.05*h
        this.epiIntensity = SharedCpt.getEpiIntensity(magnitude, depth);
        // this.circles = IntensityCircles.getCircles(epiCenter, startIntensity, epiIntensity, magnitude, depth).getList();
        this.countryAttribute = CountryAttribute.lookup(epiCenter);
        this.provinceAttribute = ProvinceAttribute.lookup(epiCenter);
        // 2017-02-15 修改，由于新的烈度圈需要省份信息，所以必须设置国家和省份后才能初始化烈度圈
        this.circles = IntensityCircles2.getCircles(epiCenter, countryAttribute.getCountryAbbr(),
                provinceAttribute.getNameCN(), startIntensity, epiIntensity, magnitude, depth).getList();
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
