package com.login.schoolregistersystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校园活动实体（对应表 activity）
 */
@Data
public class Activity {

    /** 活动ID */
    private Long activityId;

    /** 活动标题 */
    private String title;

    /** 活动描述 */
    private String description;

    /** 最大报名人数 */
    private Integer maxPeople;

    /** 报名截止时间 */
    private LocalDateTime deadline;

    /** 创建者ID */
    private Long createUserId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 当前已报名人数（非表字段，查询时统计） */
    private Integer appliedCount;
}
