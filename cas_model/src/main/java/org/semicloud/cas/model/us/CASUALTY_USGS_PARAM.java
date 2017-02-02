package org.semicloud.cas.model.us;

import org.semicloud.cas.model.al.ModelDal;
import org.semicloud.utils.common.Convert;

import java.util.Map;

/**
 * USGS人口死亡模型参数Wrapper
 */
public class CASUALTY_USGS_PARAM {

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
     * N
     */
    private float n;

    /**
     * Status
     */
    private String status;

    /**
     * 根据isocode初始化包装器对象
     *
     * @param isoCode the iso code
     */
    private CASUALTY_USGS_PARAM(String isoCode) {
        Map<String, Object> parameters = ModelDal.getFatalityModelParameter(isoCode);
        if (!parameters.isEmpty()) {
            this.id = Convert.toInteger(parameters.get("ID"));
            this.countryName = parameters.get("COUNTRY_NAME").toString();
            this.isoCode = parameters.get("ISO_CODE").toString();
            this.theta = Convert.toFloat(parameters.get("THETA"));
            this.beta = Convert.toFloat(parameters.get("BETA"));
            this.zeta = Convert.toFloat(parameters.get("ZETA"));
            this.n = Convert.toInteger(parameters.get("N"));
            this.status = parameters.get("STATUS").toString();
        } else {
            throw new IllegalArgumentException("不存在的国家代码：" + isoCode + "！");
        }
    }

    /**
     * 根据ISOCODE查找参数
     *
     * @param isoCode the iso code
     * @return the casualty usgs param
     */
    public static CASUALTY_USGS_PARAM lookup(String isoCode) {
        return new CASUALTY_USGS_PARAM(isoCode);
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
     * Gets the n.
     *
     * @return the n
     */
    public float getN() {
        return n;
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
        return "UsgsPagerFatalityModelParameter [id=" + id + ", countryName=" + countryName + ", isoCode=" + isoCode
                + ", theta=" + theta + ", beta=" + beta + ", zeta=" + zeta + ", n=" + n + ", status=" + status + "]";
    }
}
