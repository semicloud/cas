package org.semicloud.cas.scheduler.timepoint.selector;

import org.semicloud.cas.shared.BasicEqEvent;

/**
 * 研判任务选择器接口
 */
public interface TaskSelector {

    /**
     * 选择策略
     *
     * @return the basic eq event
     */
    BasicEqEvent select();
}
