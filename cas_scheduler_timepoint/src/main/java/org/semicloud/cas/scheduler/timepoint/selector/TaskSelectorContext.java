package org.semicloud.cas.scheduler.timepoint.selector;

import org.semicloud.cas.shared.BasicEqEvent;

/**
 * 任务选择器上下文类，用了一个策略模式
 */
public class TaskSelectorContext {

    /**
     * 任务选择接口
     */
    private TaskSelector selector;

    /**
     * Instantiates a new task selector context.
     */
    public TaskSelectorContext() {
    }

    /**
     * 初始化一个新的研判任务选择器context
     *
     * @param type the type
     */
    public TaskSelectorContext(SelectorType type) {
        switch (type) {
            case SimpleSelector:
                selector = new SimpleTaskSelector();
                break;
            default:
                break;
        }
    }

    /**
     * 选择最新的研判任务
     *
     * @return the basic eq event
     */
    public BasicEqEvent selectLatest() {
        return selector.select();
    }
}
