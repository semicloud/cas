package org.semicloud.cas.scheduler.realtime;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.model.re.RE_BASE_MODEL;
import org.semicloud.cas.shared.EditResult;
import org.semicloud.cas.shared.cfg.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 重计算应用研判模型模型调用者
 *
 * @author Semicloud
 */
public class ReComputeModelInvoker {

    /**
     * The _log.
     */
    private static Log _log = LogFactory.getLog(ReComputeModelInvoker.class);
    /**
     * 人工编辑结果对象
     */
    private EditResult _editResult;

    /**
     * 构造函数
     *
     * @param editResult the edit result 人工编辑结果
     */
    public ReComputeModelInvoker(EditResult editResult) {
        _editResult = editResult;
        _log.info("recompute model invoker created, new task id:" + editResult.getTaskID());
    }

    /**
     * 反射加载模型
     *
     * @param editResult the edit result 人工编辑结果
     * @param modelName  the model name 模型名称
     * @return the re base model
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static RE_BASE_MODEL loadModel(EditResult editResult, String modelName) {
        RE_BASE_MODEL model = null;
        try {
            // 生成初始化参数：eqID，taskID，模型名称
            Object[] parameters = new Object[]{editResult, modelName};
            // 设置构造器的数据类型的列表
            Class[] types = {EditResult.class, String.class};
            // 获取模型类的Class对象
            Class c = Class.forName(Settings.getModelProgram(modelName));
            // 获取构造器对象
            Constructor constructor = c.getConstructor(types);
            // 由初始参数生成模型对象
            model = (RE_BASE_MODEL) constructor.newInstance(parameters);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * 获取要执行的重计算研判模型列表
     *
     * @return the models
     */
    public List<RE_BASE_MODEL> getModels() {
        List<String> modelNames = Settings.getModelNames("re-calc");
        List<RE_BASE_MODEL> models = new ArrayList<RE_BASE_MODEL>();
        for (String modelName : modelNames) {
            models.add(loadModel(_editResult, modelName));
        }

        _log.info("get recompute models:");
        _log.info(StringUtils.repeat("-", 30));
        for (RE_BASE_MODEL model : models) {
            _log.info(model.getModelName());
        }
        _log.info(StringUtils.repeat("-", 30));
        return models;
    }
}
