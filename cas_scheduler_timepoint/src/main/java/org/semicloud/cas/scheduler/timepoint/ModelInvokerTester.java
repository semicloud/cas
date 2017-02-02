package org.semicloud.cas.scheduler.timepoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.scheduler.timepoint.task.TimePoint;
import org.semicloud.cas.shared.cfg.Settings;

/**
 * 模型单独运行的测试类
 *
 * @author Semicloud
 */
public class ModelInvokerTester {
    /**
     * The Constant log.
     */
    private static final Log log = LogFactory.getLog(ModelInvoker.class);

    public static void main(String[] args) {
        String eqID = args[0];
        log.info("eqID:" + eqID);
        Initilizer.init(eqID);
        ModelInvoker invoker = new ModelInvoker();
        System.out.println(Settings.getGisSettings().getDbServerName());
        System.out.println(Settings.getGisSettings().getDbUserName());
        System.out.println(Settings.getGisSettings().getDbUserPassword());
        for (BaseModel model : invoker.getModels(TimePoint.IMME, eqID)) {
            System.out.println("-------------------");
            System.err.println(model.getName() + "-->" + model.process());
            System.out.println("--------------------");
        }
        System.out.println("run here over...");
    }
}
