package org.semicloud.cas.model.us;

import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.db.factory.DaoFactory;

/**
 * USGS PAGER报告模型，未实现，已弃用
 */
@Deprecated
public class PAGER_REPORT extends BaseModel {

    /**
     * The eq id.
     */
    private String eqID;

    /**
     * Instantiates a new pager report.
     *
     * @param initilizer the initilizer
     * @param modelName  the model name
     */
    public PAGER_REPORT(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
        this.eqID = initilizer.getEqID();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        String code = (String) DaoFactory.getInstance().queryScalar(
                "SELECT USGS_CODE FROM BASIC_EQ_EVENT WHERE EQ_ID=?", eqID);
        if (code == "null") {
            resultJSONObject.put("PAGER_REPORT", "本次地震不是由USGS触发，无法得到PAGER研判报告");
            _log.info("本次地震不是由USGS触发，无法得到PAGER研判报告");
        } else {
            String url = Settings.getModelPagerReportUrl().replace("#", code);
            resultJSONObject.put("PAGER_REPORT", url);
            _log.info("PAGER_REPORT:" + url);
        }
        return resultJSONObject.toString();
    }
}
