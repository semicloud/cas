package org.semicloud.cas.scheduler.timepoint.task;

/**
 * 研判时间点
 */
public enum TimePoint {

    /**
     * 速判.
     */
    IMME,

    /**
     * 30分钟综合研判.
     */
    MIN_30,

    /**
     * 1小时综合研判.
     */
    HOUR_1,

    /**
     * 3小时综合研判.
     */
    HOUR_3,

    /**
     * 6小时综合研判.
     */
    HOUR_6,

    /**
     * 10小时综合研判.
     */
    HOUR_10,

    /**
     * 14小时综合研判.
     */
    HOUR_14,

    /**
     * 救援行动分析.
     */
    RESCUE_ANALYSIS
}
