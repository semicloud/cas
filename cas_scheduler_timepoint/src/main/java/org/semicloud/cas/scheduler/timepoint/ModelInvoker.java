package org.semicloud.cas.scheduler.timepoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.model.ModelInitializer;
import org.semicloud.cas.model.us.CASUALTY_USGS_COUNTY_LINE_CIRCLE;
import org.semicloud.cas.scheduler.timepoint.task.TimePoint;
import org.semicloud.cas.shared.cfg.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 模型调用者
 */
public class ModelInvoker {

    /**
     * The Constant log.
     */
    private static final Log log = LogFactory.getLog(ModelInvoker.class);

    /**
     * 构造函数
     */
    public ModelInvoker() {
    }

    /**
     * run 方法，用来测试
     *
     * @param timePoint the time point
     */
    private static void run(TimePoint timePoint) {
        System.out.println(timePoint.name() + " >>>>>>>>>> ");
        Initilizer.init("N30300E10300020150127140159");
        ModelInvoker invoker = new ModelInvoker();
        System.out.println(Settings.getGisSettings().getDbServerName());
        System.out.println(Settings.getGisSettings().getDbUserName());
        System.out.println(Settings.getGisSettings().getDbUserPassword());
        for (BaseModel model : invoker.getModels(timePoint, "N30300E10310020160327222631")) {
            System.out.println("-------------------");
            System.err.println(model.getName() + "-->" + model.process());
            System.out.println("--------------------");
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // run(TimePoint.IMME);

        // 日本地震 N38100E14260020110311124307
        // 甘肃地震 N34500E10420020130722095022
        // 芦山地震 N30300E10300020150520154723
        // 汶川地震 N31010E10342020160406151825

        // 运行单个模型的代码
        ModelInitializer initilizer = new ModelInitializer("N31010E10342020160406151825", "N31010E10342020160406151825");
        CASUALTY_USGS_COUNTY_LINE_CIRCLE cuclc = new CASUALTY_USGS_COUNTY_LINE_CIRCLE(initilizer, "USGSPAGER县级市人口伤亡模型（基于线源模型）");
        System.out.println(cuclc.process());

        // System.out.println(casualty_USGS.getJson());
        // INTENSITY intensity = new INTENSITY(initilizer, "d");
        // System.out.println(intensity.getJson());
        // ACTIVE_FAULT fault = new ACTIVE_FAULT(initilizer, "d1");
        // System.out.println(fault.getJson());
        // HISTORICAL historical = new HISTORICAL(initilizer, "d2");
        // System.out.println(historical.getJson());
    }

    /**
     * 根据时间点返回要运行的模型列表.
     *
     * @param timePoint 时间点
     * @param eqID      地震ID（初始参数）
     * @return 模型列表
     */
    public List<BaseModel> getModels(TimePoint timePoint, String eqID) {
        List<BaseModel> models = new ArrayList<>();
        String taskID = eqID.concat("_").concat(timePoint.name().toUpperCase());
        ModelInitializer initilizer = new ModelInitializer(eqID, taskID);
        for (String modelName : Settings.getModelNames(timePoint.name().toLowerCase())) {
            models.add(loadModel(initilizer, modelName));
        }
        // if (BaseModel.willComputed(eqID)) {
        // // 这行语句必须在if条件之后做，因为ModelInitilizer就需要GIS操作了
        // ModelInitializer initilizer = new ModelInitializer(eqID, taskID);
        // for (String modelName :
        // Settings.getModelNames(timePoint.name().toLowerCase())) {
        // models.add(loadModel(initilizer, modelName));
        // }
        // } else {
        // log.info("eartuquake event [" + ModelDal.getDescription(eqID) +
        // "]，not up to condition, so not do "
        // + timePoint.name() + "analysis");
        // }
        log.info("@Timepoint:" + timePoint + ", will invoke models as follows:");
        for (BaseModel baseModel : models) {
            log.info(baseModel.getName());
        }
        return models;
    }

    /**
     * 反射加载模型
     *
     * @param initilizer the initilizer ModelInitilizer对象
     * @param modelName  the model name 模型名称
     * @return the base model
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public BaseModel loadModel(ModelInitializer initilizer, String modelName) {
        BaseModel model = null;
        try {
            // 生成初始化参数：eqID，taskID，模型名称
            Object[] parameters = new Object[]{initilizer, modelName};
            // 设置构造器的数据类型的列表
            Class[] types = {ModelInitializer.class, String.class};
            // 获取模型类的Class对象
            Class c = Class.forName(Settings.getModelProgram(modelName));
            // 获取构造器对象
            Constructor constructor = c.getConstructor(types);
            // 由初始参数生成模型对象
            model = (BaseModel) constructor.newInstance(parameters);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return model;
    }
}
