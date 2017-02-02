package org.semicloud.cas.model.re;

import org.semicloud.cas.shared.EditResult;

/**
 * 重计算-震中附近机场分布模型
 */
public class RE_AIRPORT extends RE_BASE_MODEL {

    /**
     * Instantiates a new re airport.
     *
     * @param editResult the edit result
     * @param modelName  the model name
     */
    public RE_AIRPORT(EditResult editResult, String modelName) {
        super(editResult, modelName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.model.re.RE_BASE_MODEL#getJson()
     */
    @Override
    public String getJson() {
        // List<Map<String, Object>> airports =
        // ModelGal.getAirportsInfos(_center);
        // JSONArray jsonArray = new JSONArray();
        // _log.info("airport information as follows:");
        //
        // for (int i = 0; i < airports.size(); i++) {
        // Map<String, Object> airport = airports.get(i);
        // _log.info("airport, NO. " + (i + 1));
        // _log.info("city:" + airport.get("city_name").toString().trim());
        // _log.info("name cn:" + airport.get("name_cn").toString().trim());
        // _log.info("name en:" + airport.get("name_en").toString().trim());
        // _log.info("lng:" + airport.get("longitude").toString().trim());
        // _log.info("lat:" + airport.get("latitude").toString().trim());
        // _log.info("through put:" +
        // airport.get("Throughput").toString().trim());
        // _log.info("number:" + airport.get("runway").toString().trim());
        // _log.info("description:" +
        // airport.get("descriptio").toString().trim());
        // JSONObject object = JSONObject.fromObject(airports.get(i));
        // jsonArray.add(object);
        // }
        // _resultJsonObject.put("airports", jsonArray);
        // return _resultJsonObject.toString();
        return getOld("m_airport");
    }
}
