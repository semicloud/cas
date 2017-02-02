package org.semicloud.cas.shared.intensity;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通过epi.xml查询震中烈度
 *
 * @author Semicloud
 */
public class Epi {

    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(Epi.class);

    /**
     * 获取震源深度
     *
     * @param value the value
     * @return the int
     */
    private static int parseDepth(float value) {
        int depth = 0;
        if (value <= 5)
            depth = 5;
        else if (value > 5 && value <= 10)
            depth = 10;
        else if (value > 10 && value <= 15)
            depth = 15;
        else if (value > 15 && value <= 20)
            depth = 20;
        else if (value > 20 && value <= 25)
            depth = 25;
        else
            depth = 25;
        return depth;
    }

    /**
     * 根据震源深度和震级获取震中烈度
     *
     * @param mag   the mag
     * @param depth the depth
     * @return the value
     */
    public static float getValue(float mag, float depth) {
        float epi = 0.0f;
        try {
            int parsedDepth = parseDepth(depth);
            XMLConfiguration epiXml = new XMLConfiguration("conf/xml/epi.xml");
            epiXml.setExpressionEngine(new XPathExpressionEngine());
            epi = epiXml.getFloat("/mag[@value='" + mag + "']/depth[@value='" + parsedDepth + "']");
        } catch (ConfigurationException ex) {
            log.error("没有查询到震级为" + mag + "，深度为" + depth + "的震中烈度！");
        }
        return epi;
    }

    // public static void main(String[] args) {
    // System.out.println(Epi.getValue(6.6f, 14.0f));
    // }
}
