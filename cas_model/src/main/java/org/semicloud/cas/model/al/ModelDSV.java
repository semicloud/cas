package org.semicloud.cas.model.al;

import org.semicloud.cas.model.ModelInitilizer;
import org.semicloud.cas.model.us.*;

/**
 * 测试类，已不再使用
 *
 * @author Semicloud
 */
@Deprecated
public class ModelDSV {
    public static void main(String[] args) {
        String eqID = "N41000E14320020141011103546";
        ModelInitilizer initilizer = new ModelInitilizer(eqID, eqID);

        INTENSITY intensity = new INTENSITY(initilizer, "");
        System.out.println(intensity.getJson());

        AIRPORT airport = new AIRPORT(initilizer, "");
        System.out.println(airport.getJson());

        DENSITY density = new DENSITY(initilizer, "");
        System.out.println(density.getJson());

        ACTIVE_FAULT activeFault = new ACTIVE_FAULT(initilizer, "");
        System.out.println(activeFault.getJson());

        HISTORICAL historical = new HISTORICAL(initilizer, "");
        System.out.println(historical.getJson());

        ECONOMIC_USGS_COUNTY economic_USGS_COUNTY = new ECONOMIC_USGS_COUNTY(initilizer, "");
        System.out.println(economic_USGS_COUNTY.getJson());

        CASUALTY_USGS_COUNTY casualtyUsgsCounty = new CASUALTY_USGS_COUNTY(initilizer, "");
        System.out.println(casualtyUsgsCounty.getJson());

    }
}
