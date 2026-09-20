package com.login.schoolregistersystem.service;

import com.login.schoolregistersystem.vo.ApplyVO;

import java.util.List;

public interface ApplyService {

    /** 报名：严格执行四步校验 */
    void apply(Long activityId, Long loginUserId);

    /** 取消报名：只允许取消本人报名记录 */
    void cancel(Long activityId, Long loginUserId);

    /** 我的报名列表 */
    List<ApplyVO> myApplies(Long loginUserId);
}
