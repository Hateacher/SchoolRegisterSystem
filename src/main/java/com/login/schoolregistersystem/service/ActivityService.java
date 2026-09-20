package com.login.schoolregistersystem.service;

import com.login.schoolregistersystem.entity.Activity;

import java.util.List;

public interface ActivityService {

    /** 发布活动：校验标题、名额、截止时间，创建者取当前登录用户 */
    Long add(Activity activity, Long loginUserId);

    /** 编辑活动：仅允许修改本人创建的活动 */
    void update(Activity activity, Long loginUserId);

    /** 删除活动（下架）：仅允许删除本人创建的活动 */
    void delete(Long activityId, Long loginUserId);

    /** 活动列表（带已报名人数） */
    List<Activity> list();

    /** 活动详情（带已报名人数） */
    Activity detail(Long activityId);
}
