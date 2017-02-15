package org.semicloud.cas.model.us;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.cfg.Settings;

import java.util.List;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 震中附近断层分布模型
 */
public class ACTIVE_FAULT extends BaseModel {
    // static int LIMIT_SIZE = 60;

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public ACTIVE_FAULT(ModelInitializer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    public static void main(String[] args) {
        // 70.8 36.5
        ModelInitializer initilizer = new ModelInitializer("N36500E07080020151027094119",
                "N36500E07080020151027094119_IMME");
        ACTIVE_FAULT active_FAULT = new ACTIVE_FAULT(initilizer, "");
        active_FAULT.getJson();
    }

    @Override
    public String getJson() {
        float r = Settings.getModelFaultSearchRadius();
        _log.info("r:" + r);
        List<Map<String, Object>> faults = ModelGal.getFaultInfos(epiCenter);
        _log.info(text("with search radius {0} KM, return {1} faults, detailed infos as follows:", r, faults.size()));
        // 2015年12月11日 王海鹰 搜索出多少断层就多少断层，不要去掉，她已知晓这样可能会慢
        // if (faults.size() > LIMIT_SIZE) {
        // faults = faults.subList(0, LIMIT_SIZE);
        // _log.info("too many faults, sub to " + LIMIT_SIZE);
        // }
        JSONArray array = new JSONArray();
        if (faults.size() > 0) {
            for (Map<String, Object> fault : faults) {
                int idx = faults.indexOf(fault);
                _log.debug("active fault info, NO." + (idx + 1));
                _log.debug("ID:" + fault.get("SMID"));
                _log.debug("name cn:" + fault.get("name_cn"));
                _log.debug("strike:" + fault.get("strike"));
                _log.debug("distance to epicenter:" + fault.get("distance") + " KM");
                JSONObject object = JSONObject.fromObject(fault);
                array.add(object);
            }
        }
        resultJSONObject.put("active_faults", array);
        resultJSONObject.put("longitude", getEpiCenter().getLongitude());
        resultJSONObject.put("latitude", getEpiCenter().getLongitude());
        resultJSONObject.put("r", r + "km");
        //TODO 先甭创建数据集了，搜救的数据库上有问题
        // ACTIVE_FAULT_USV.createActiveFaultsDatasetVector(eqID,
        // resultJSONObject);
        // ACTIVE_FAULT_TEXT_USV.createActiveFaultsTextDatasetVector(eqID,
        // resultJSONObject);
        return resultJSONObject.toString();
    }
}
