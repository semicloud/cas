package org.semicloud.cas.model.attribute;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EpiCenter;

import java.util.Map;

/**
 * 国家属性
 */
public class CountryAttribute {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(CountryAttribute.class);
    /**
     * 国家ID
     */
    private String countryID = "";
    /**
     * 分类代码
     */
    private String classCode = "";
    /**
     * 国家缩写
     */
    private String countryAbbr = "";
    /**
     * 国家名称-中文
     */
    private String nameCN = "";
    /**
     * 国家名称-英文
     */
    private String nameEN = "";
    /**
     * 国家名称-俄文
     */
    private String nameRU = "";
    /**
     * 国家名称-法文
     */
    private String nameFR = "";
    /**
     * 国家名称-西班牙文
     */
    private String nameSP = "";
    /**
     * 首都名称-中文
     */
    private String capitalNameCN = "";
    /**
     * 首都名称-英文
     */
    private String capitalNameEN = "";
    /**
     * 首都名称-俄文
     */
    private String capitalNameRU = "";
    /**
     * 首都名称-法文
     */
    private String capitalNameFR = "";
    /**
     * 首都名称-西班牙文
     */
    private String capitalNameSP = "";

    /**
     * 通过震中点获取国家属性.
     *
     * @param epiCenter 震中
     * @return the country attribute
     */
    public static CountryAttribute lookup(EpiCenter epiCenter) {
        Map<String, Object> map = ModelGal.getCountryAttributes(epiCenter);
        if (map != null && map.size() > 0) {
            return valueOf(ModelGal.getCountryAttributes(epiCenter));
        } else {
            log.warn("country attribute not find!");
            return new CountryAttribute();
        }
    }

    /**
     * 由Map转换为CountryAttribute
     *
     * @param attributeMap the attribute map
     * @return the country attribute
     */
    private static CountryAttribute valueOf(Map<String, Object> attributeMap) {
        CountryAttribute ca = new CountryAttribute();
        if (!attributeMap.isEmpty()) {
            ca.setClassCode(attributeMap.get("class_code").toString());
            ca.setCountryID(attributeMap.get("country_id").toString());
            ca.setCountryAbbr(attributeMap.get("country_ab").toString());
            ca.setNameCN(attributeMap.get("name_cn").toString());
            ca.setNameEN(attributeMap.get("name_en").toString());
            ca.setNameRU(attributeMap.get("name_ru").toString());
            ca.setNameFR(attributeMap.get("name_fr").toString());
            ca.setNameSP(attributeMap.get("name_sp").toString());
            ca.setCapitalNameCN(attributeMap.get("capital_cn").toString());
            ca.setCapitalNameEN(attributeMap.get("capital_en").toString());
            ca.setCapitalNameRU(attributeMap.get("capital_ru").toString());
            ca.setCapitalNameFR(attributeMap.get("capital_fr").toString());
            ca.setCapitalNameSP(attributeMap.get("capital_sp").toString());
        }
        return ca;
    }

    /**
     * Gets the country id.
     *
     * @return the country id
     */
    public String getCountryID() {
        return countryID;
    }

    /**
     * Sets the country id.
     *
     * @param countryID the new country id
     */
    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    /**
     * Gets the class code.
     *
     * @return the class code
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * Sets the class code.
     *
     * @param classCode the new class code
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    /**
     * Gets the country abbr.
     *
     * @return the country abbr
     */
    public String getCountryAbbr() {
        return countryAbbr;
    }

    /**
     * Sets the country abbr.
     *
     * @param countryAbbr the new country abbr
     */
    public void setCountryAbbr(String countryAbbr) {
        this.countryAbbr = countryAbbr;
    }

    /**
     * Gets the name cn.
     *
     * @return the name cn
     */
    public String getNameCN() {
        return nameCN;
    }

    /**
     * Sets the name cn.
     *
     * @param nameCN the new name cn
     */
    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    /**
     * Gets the name en.
     *
     * @return the name en
     */
    public String getNameEN() {
        return nameEN;
    }

    /**
     * Sets the name en.
     *
     * @param nameEN the new name en
     */
    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    /**
     * Gets the name ru.
     *
     * @return the name ru
     */
    public String getNameRU() {
        return nameRU;
    }

    /**
     * Sets the name ru.
     *
     * @param nameRU the new name ru
     */
    public void setNameRU(String nameRU) {
        this.nameRU = nameRU;
    }

    /**
     * Gets the name fr.
     *
     * @return the name fr
     */
    public String getNameFR() {
        return nameFR;
    }

    /**
     * Sets the name fr.
     *
     * @param nameFR the new name fr
     */
    public void setNameFR(String nameFR) {
        this.nameFR = nameFR;
    }

    /**
     * Gets the name sp.
     *
     * @return the name sp
     */
    public String getNameSP() {
        return nameSP;
    }

    /**
     * Sets the name sp.
     *
     * @param nameSP the new name sp
     */
    public void setNameSP(String nameSP) {
        this.nameSP = nameSP;
    }

    /**
     * Gets the capital name cn.
     *
     * @return the capital name cn
     */
    public String getCapitalNameCN() {
        return capitalNameCN;
    }

    /**
     * Sets the capital name cn.
     *
     * @param capitalNameCN the new capital name cn
     */
    public void setCapitalNameCN(String capitalNameCN) {
        this.capitalNameCN = capitalNameCN;
    }

    /**
     * Gets the capital name en.
     *
     * @return the capital name en
     */
    public String getCapitalNameEN() {
        return capitalNameEN;
    }

    /**
     * Sets the capital name en.
     *
     * @param capitalNameEN the new capital name en
     */
    public void setCapitalNameEN(String capitalNameEN) {
        this.capitalNameEN = capitalNameEN;
    }

    /**
     * Gets the capital name ru.
     *
     * @return the capital name ru
     */
    public String getCapitalNameRU() {
        return capitalNameRU;
    }

    /**
     * Sets the capital name ru.
     *
     * @param capitalNameRU the new capital name ru
     */
    public void setCapitalNameRU(String capitalNameRU) {
        this.capitalNameRU = capitalNameRU;
    }

    /**
     * Gets the capital name fr.
     *
     * @return the capital name fr
     */
    public String getCapitalNameFR() {
        return capitalNameFR;
    }

    /**
     * Sets the capital name fr.
     *
     * @param capitalNameFR the new capital name fr
     */
    public void setCapitalNameFR(String capitalNameFR) {
        this.capitalNameFR = capitalNameFR;
    }

    /**
     * Gets the capital name sp.
     *
     * @return the capital name sp
     */
    public String getCapitalNameSP() {
        return capitalNameSP;
    }

    /**
     * Sets the capital name sp.
     *
     * @param capitalNameSP the new capital name sp
     */
    public void setCapitalNameSP(String capitalNameSP) {
        this.capitalNameSP = capitalNameSP;
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(nameCN.trim());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "国家属性 [国家ID=" + countryID + ", 分类代码=" + classCode + ", 国家缩写=" + countryAbbr + ", 中文名称=" + nameCN
                + ", 英文名称=" + nameEN + ", 俄文名称=" + nameRU + ", 法文名称=" + nameFR + ", 西班牙文名称=" + nameSP + ", 首都中文名称="
                + capitalNameCN + ", 首都英文名称=" + capitalNameEN + ", 首都俄文名称=" + capitalNameRU + ", 首都法文名称="
                + capitalNameFR + ", 首都西班牙文名称=" + capitalNameSP + "]";
    }

}
