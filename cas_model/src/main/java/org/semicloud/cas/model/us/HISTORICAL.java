/*
 * 
 */
package org.semicloud.cas.model.us;

import com.supermap.data.GeoPoint;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;

import java.text.DecimalFormat;
import java.util.*;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 震中附近历史地震分布模型
 */
public class HISTORICAL extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public HISTORICAL(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        List<Map<String, Object>> historical = ModelGal.getHistoricalEqInfos(epiCenter);
        JSONArray jsonArray = new JSONArray();
        float r = Settings.getModelHistorySearchRadius();
        _log.info(text("with search radius {0} KM, return {1} history events, detailed infos as follows:", r,
                historical.size()));
        float m = getMagnitude();
        if (m <= 5.9)
            r = 200;
        if (m >= 6.0 && m <= 6.9)
            r = 300;
        if (m >= 7.0 && m <= 7.9)
            r = 450;
        if (m >= 8.0)
            r = 600;
        _log.info("historical r:" + r);
        double[] center = ModelGal.getProjection(epiCenter);
        List<Map<String, Object>> sortedList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < historical.size(); i++) {
            Map<String, Object> map = historical.get(i);
            _log.info("historical event, NO. " + (i + 1));
            _log.info("eq_name:" + map.get("eq_name"));
            _log.info("eq_place:" + map.get("eq_place"));
            _log.info("site:" + map.get("site"));
            _log.info("magnitude:" + map.get("magnitude"));
            _log.info("depth:" + map.get("depth"));
            _log.info("eq_date:" + map.get("eq_date"));
            _log.info("eq_time:" + map.get("eq_time"));
            _log.info("death:" + map.get("death"));
            _log.info("Injury:" + map.get("Injury"));
            _log.info("loss:" + map.get("loss"));
            _log.info("unit:" + map.get("unit"));
            _log.info("landforms:" + map.get("landforms"));
            _log.info("fault_type:" + map.get("fault_type"));
            _log.info("influence:" + map.get("influence"));
            _log.info("features:" + map.get("features"));
            _log.info("SMX:" + map.get("SMX"));
            _log.info("SMY:" + map.get("SMY"));
            double smx = (double) map.get("SMX"), smy = (double) map.get("SMY");
            double dist = Geometrist.distance(new GeoPoint(new Point2D(center[0], center[1])), new GeoPoint(smx, smy));
            dist = dist / 1000;
            DecimalFormat format = new DecimalFormat("#0.00");
            _log.info("dist:" + format.format(dist));
            map.put("dist", format.format(dist));

            EpiCenter e = ModelGal.getVerseEpiCenter(smx, smy);
            map.put("longitude", e.getLongitude());
            map.put("latitude", e.getLatitude());

            float mag = Convert.toFloat(map.get("magnitude"), "0.0");
            map.put("magnitude", mag);

            // JSONObject jsonObject = JSONObject.fromObject(map);
            // jsonArray.add(jsonObject);
            sortedList.add(map);
        }

        Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                double dist1 = Double.parseDouble(arg0.get("dist").toString());
                double dist2 = Double.parseDouble(arg1.get("dist").toString());
                return Double.compare(dist1, dist2);
            }
        };
        Collections.sort(sortedList, comparator);
        for (Map<String, Object> map : sortedList) {
            String dist = map.get("dist").toString();
            map.put("dist", dist + " KM");
            JSONObject jsonObject = JSONObject.fromObject(map);
            jsonArray.add(jsonObject);
        }
        resultJSONObject.put("historical", jsonArray);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // HISTORICAL_USV.createHistoryDatasetVector(eqID, resultJSONObject);
        // HISTORICAL_USV.createHistoryTextDatasetVector(eqID,
        // resultJSONObject);
        _log.info(taskID + " --> 历史震情模型计算完毕");
        return resultJSONObject.toString();
    }
}
