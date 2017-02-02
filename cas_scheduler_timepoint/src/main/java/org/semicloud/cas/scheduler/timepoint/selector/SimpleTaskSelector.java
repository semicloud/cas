package org.semicloud.cas.scheduler.timepoint.selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.shared.BasicEqEvent;

/**
 * 简单研判任务选择器
 */
public class SimpleTaskSelector implements TaskSelector {

    /**
     * The log.
     */
    private Log log = LogFactory.getLog(SimpleTaskSelector.class);

    /*
     * (non-Javadoc)
     *
     * @see org.semicloud.cas.scheduler.timepoint.selector.TaskSelector#select()
     */
    @Override
    public BasicEqEvent select() {
        BasicEqEvent event = BasicEqEvent.getLatest();
        if (event != null) {
            log.info("地震事件[" + event.getDescription() + "]开始研判。");
        }
        return event;
    }
}
