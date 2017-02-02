package org.semicloud.cas.model.attribute;

import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EpiCenter;

import java.util.Map;

/**
 * 省份属性
 */
public class ProvinceAttribute {

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
     * admID
     */
    private String admID = "";

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
     * 由震中获取省份属性
     *
     * @param epiCenter 震中
     * @return the province attribute
     */
    public static ProvinceAttribute lookup(EpiCenter epiCenter) {
        return valueOf(ModelGal.getProvinceAttributes(epiCenter));
    }

    /**
     * 由Map转换为CountryAttribute
     *
     * @param attributeMap the attribute map
     * @return the province attribute
     */
    private static ProvinceAttribute valueOf(Map<String, Object> attributeMap) {
        ProvinceAttribute pa = new ProvinceAttribute();
        if (!attributeMap.isEmpty()) {
            pa.setCountryID(attributeMap.get("country_id").toString());
            pa.setClassCode(attributeMap.get("class_code").toString());
            pa.setCountryAbbr(attributeMap.get("country_ab").toString());
            pa.setAdmID(attributeMap.get("adm_id").toString());
            pa.setNameCN(attributeMap.get("name_cn").toString());
            pa.setNameEN(attributeMap.get("name_en").toString());
            pa.setNameRU(attributeMap.get("name_ru").toString());
            pa.setNameFR(attributeMap.get("name_fr").toString());
            pa.setNameSP(attributeMap.get("name_sp").toString());
            pa.setCapitalNameCN(attributeMap.get("capital_cn").toString());
            pa.setCapitalNameEN(attributeMap.get("capital_en").toString());
            pa.setCapitalNameRU(attributeMap.get("capital_ru").toString());
            pa.setCapitalNameFR(attributeMap.get("capital_fr").toString());
            pa.setCapitalNameSP(attributeMap.get("capital_sp").toString());
        }
        return pa;
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
    private void setCountryID(String countryID) {
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
    private void setClassCode(String classCode) {
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
    private void setCountryAbbr(String countryAbbr) {
        this.countryAbbr = countryAbbr;
    }

    /**
     * Gets the adm id.
     *
     * @return the adm id
     */
    public String getAdmID() {
        return admID;
    }

    /**
     * Sets the adm id.
     *
     * @param admID the new adm id
     */
    private void setAdmID(String admID) {
        this.admID = admID;
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
    private void setNameCN(String nameCN) {
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
    private void setNameEN(String nameEN) {
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
    private void setNameRU(String nameRU) {
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
    private void setNameFR(String nameFR) {
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
    private void setNameSP(String nameSP) {
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
    private void setCapitalNameCN(String capitalNameCN) {
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
    private void setCapitalNameEN(String capitalNameEN) {
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
    private void setCapitalNameRU(String capitalNameRU) {
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
    private void setCapitalNameFR(String capitalNameFR) {
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
    private void setCapitalNameSP(String capitalNameSP) {
        this.capitalNameSP = capitalNameSP;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProvinceAttribute [countryID=" + countryID + ", classCode=" + classCode + ", countryAbbr="
                + countryAbbr + ", admID=" + admID + ", nameCN=" + nameCN + ", nameEN=" + nameEN + ", nameRU=" + nameRU
                + ", nameFR=" + nameFR + ", nameSP=" + nameSP + ", capitalNameCN=" + capitalNameCN + ", capitalNameEN="
                + capitalNameEN + ", capitalNameRU=" + capitalNameRU + ", capitalNameFR=" + capitalNameFR
                + ", capitalNameSP=" + capitalNameSP + "]";
    }
}
