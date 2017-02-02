package org.semicloud.cas.model.us;

import org.apache.commons.collections.MapUtils;
import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.utils.common.Convert;

import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * USGS经济损失模型参数包装类
 */
public class ECONOMIC_USGS_PARAM {

    /**
     * ID
     */
    private int id;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * ISOCODE
     */
    private String isoCode;

    /**
     * 参数Theta
     */
    private float theta;

    /**
     * 参数Beta
     */
    private float beta;

    /**
     * 参数Zeta
     */
    private float zeta;

    /**
     * 该国家的人均GDP
     */
    private float gdpPerPeople;

    /**
     * 参数Alpha
     */
    private float alpha;

    /**
     * Status
     */
    private String status;

    /**
     * 根据isocode初始化包装器对象
     *
     * @param isoCode the iso code
     */
    private ECONOMIC_USGS_PARAM(String isoCode) {
        Map<String, Object> map = ModelDal.getEconomicModelParameter(isoCode);
        if (map != null && MapUtils.isNotEmpty(map)) {
            this.id = Convert.toInteger(map.get("ID"));
            this.countryName = map.get("COUNTRY_NAME").toString();
            this.isoCode = map.get("ISO_CODE").toString();
            this.theta = Convert.toFloat(map.get("THETA"));
            this.beta = Convert.toFloat(map.get("BETA"));
            this.zeta = Convert.toFloat(map.get("ZETA"));
            this.gdpPerPeople = Convert.toFloat(map.get("GDP"));
            this.alpha = Convert.toFloat(map.get("ALPHA"));
            this.status = map.get("STATUS").toString();
        } else {
            throw new IllegalArgumentException(text("unexisted country code:{0}!load parameters error!", isoCode));
        }
    }

    /**
     * 根据ISOCODE查找参数
     *
     * @param isoCode the iso code
     * @return the economic usgs param
     */
    public static ECONOMIC_USGS_PARAM lookup(String isoCode) {
        return new ECONOMIC_USGS_PARAM(isoCode);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the country name.
     *
     * @return the country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Gets the iso code.
     *
     * @return the iso code
     */
    public String getIsoCode() {
        return isoCode;
    }

    /**
     * Gets the theta.
     *
     * @return the theta
     */
    public float getTheta() {
        return theta;
    }

    /**
     * Gets the beta.
     *
     * @return the beta
     */
    public float getBeta() {
        return beta;
    }

    /**
     * Gets the zeta.
     *
     * @return the zeta
     */
    public float getZeta() {
        return zeta;
    }

    /**
     * Gets the gdp per people.
     *
     * @return the gdp per people
     */
    public float getGdpPerPeople() {
        return gdpPerPeople;
    }

    /**
     * Gets the alpha.
     *
     * @return the alpha
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UsgsEconomicModelParameter [id=" + id + ", countryName=" + countryName + ", isoCode=" + isoCode
                + ", theta=" + theta + ", beta=" + beta + ", zeta=" + zeta + ", gdp=" + gdpPerPeople + ", alpha="
                + alpha + ", status=" + status + "]";
    }
}
