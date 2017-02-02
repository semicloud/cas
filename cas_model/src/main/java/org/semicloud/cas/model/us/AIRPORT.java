package org.semicloud.cas.model.us;

import com.supermap.data.GeoPoint;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.shared.cfg.Settings;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 震中附近机场分布模型
 */
public class AIRPORT extends BaseModel {

    /**
     * 构造函数
     *
     * @param initilizer 模型初始化对象
     * @param modelName  模型名称
     */
    public AIRPORT(ModelInitilizer initilizer, String modelName) {
        super(initilizer, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.BaseModel#getJson()
     */
    @Override
    public String getJson() {
        float r = Settings.getModelAirportSearchRadius();
        List<Map<String, Object>> airports = ModelGal.getAirportsInfos(epiCenter);
        JSONArray array = new JSONArray();
        _log.info(text("with search radius {0} KM, return {1} airports, detailed infos as follows:", r, airports.size()));
        for (int i = 0; i < airports.size(); i++) {
            Map<String, Object> airport = airports.get(i);
            _log.info("airport, NO. " + (i + 1));
            _log.info("city:" + airport.get("city_name"));
            _log.info("name cn:" + airport.get("name_cn"));
            _log.info("name en:" + airport.get("name_en"));
            _log.info("lng:" + airport.get("longitude"));
            _log.info("lat:" + airport.get("latitude"));
            _log.info("through put:" + airport.get("Throughput"));
            _log.info("number:" + airport.get("runway"));
            _log.info("description:" + airport.get("descriptio"));
            double smx = (double) airport.get("SMX"), smy = (double) airport.get("SMY");
            double[] center = ModelGal.getProjection(epiCenter);
            Point2D point2d = new Point2D(center[0], center[1]);
            double dist = Geometrist.distance(new GeoPoint(point2d), new GeoPoint(smx, smy));
            dist = dist / 1000;
            DecimalFormat format = new DecimalFormat("#0.00");
            airport.put("distance", Double.parseDouble(format.format(dist)));
            airport.put("longitude", Double.parseDouble(format.format((Double) airport.get("longitude"))));
            airport.put("latitude", Double.parseDouble(format.format((Double) airport.get("latitude"))));
            JSONObject object = JSONObject.fromObject(airport);
            array.add(object);
        }
        resultJSONObject.put("airports", array);
        // TODO 先甭创建数据集了，搜救的数据库上有问题
        // AIRPORT_USV.createAirportsDatasetVector(eqID, resultJSONObject);
        // AIRPORT_USV.createAirportsTextDatasetVector(eqID, resultJSONObject);
        return resultJSONObject.toString();
    }
}
