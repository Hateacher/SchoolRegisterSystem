package com.login.schoolregistersystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的报名列表视图对象（GET /api/apply/my 返回）
 */
@Data
public class ApplyVO {

    /** 活动ID */
    private Long activityId;

    /** 活动标题 */
    private String activityTitle;

    /** 报名截止时间（活动时间） */
    private LocalDateTime deadline;

    /** 报名时间 */
    private LocalDateTime createTime;
}
