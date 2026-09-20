package com.login.schoolregistersystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名记录实体（对应表 apply_record）
 * 关联：ApplyRecord n:1 Activity，ApplyRecord n:1 SysUser
 */
@Data
public class ApplyRecord {

    /** 报名记录ID */
    private Long id;

    /** 报名用户ID */
    private Long userId;

    /** 活动ID */
    private Long activityId;

    /** 报名时间 */
    private LocalDateTime createTime;
}
