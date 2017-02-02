package org.semicloud.cas.scheduler.realtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.model.re.RE_BASE_MODEL;
import org.semicloud.cas.shared.EditResult;

import java.util.Date;
import java.util.List;

/**
 * 重计算调度器
 */
public class RealTimeScheduler implements Runnable {

    /**
     * The Constant SCAN_INTERVAL.
     */
    private static final int SCAN_INTERVAL = 60;

    /**
     * 单例对象
     */
    private static RealTimeScheduler _scheduler = new RealTimeScheduler();

    /**
     * The _log.
     */
    private static Log _log = LogFactory.getLog(RealTimeScheduler.class);

    /**
     * 构造函数
     */
    private RealTimeScheduler() {
        System.out.println("real time scheduler started... at " + new Date());
    }

    /**
     * 获取重计算调度器的实例
     *
     * @return single instance of RealTimeScheduler
     */
    public static RealTimeScheduler getInstance() {
        if (_scheduler == null)
            _scheduler = new RealTimeScheduler();
        return _scheduler;
    }

    /**
     * 开始调度
     *
     * @param result the result 人工编辑结果对象
     */
    private static void schedule(EditResult result) {
        if (result != null) {
            if (result.updateProcessState()) {
                ReComputeModelInvoker invoker = new ReComputeModelInvoker(result);
                List<RE_BASE_MODEL> models = invoker.getModels();
                _log.info("seems like " + models.size() + " models to be invoked.");
                for (RE_BASE_MODEL model : models) {
                    model.process();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            EditResult editResult = EditResult.getLatest();
            if (editResult != null) {
                schedule(editResult);
            } else {
                sleeping();
            }
        }
    }

    /**
     * Sleeping.
     */
    private void sleeping() {
        try {
            Thread.sleep(SCAN_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
