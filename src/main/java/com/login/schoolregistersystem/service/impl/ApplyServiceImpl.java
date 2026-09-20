package com.login.schoolregistersystem.service.impl;

import com.login.schoolregistersystem.common.BusinessException;
import com.login.schoolregistersystem.entity.Activity;
import com.login.schoolregistersystem.mapper.ActivityMapper;
import com.login.schoolregistersystem.mapper.ApplyMapper;
import com.login.schoolregistersystem.service.ApplyService;
import com.login.schoolregistersystem.vo.ApplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final ApplyMapper applyMapper;
    private final ActivityMapper activityMapper;

    /**
     * 报名四步校验（顺序固定，任一失败即拒绝）：
     * 1. 活动是否存在 → 2. 是否超过报名截止时间 → 3. 名额是否已满 → 4. 是否重复报名
     */
    @Override
    @Transactional
    public void apply(Long activityId, Long loginUserId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if (LocalDateTime.now().isAfter(activity.getDeadline())) {
            throw new BusinessException("活动已截止，无法报名");
        }
        if (applyMapper.countByActivity(activityId) >= activity.getMaxPeople()) {
            throw new BusinessException("名额已满，无法报名");
        }
        if (applyMapper.countByUserAndActivity(loginUserId, activityId) > 0) {
            throw new BusinessException("请勿重复报名");
        }
        try {
            applyMapper.insert(loginUserId, activityId);
        } catch (DuplicateKeyException e) {
            // 并发报名穿透第4步校验时，由数据库唯一约束兜底，转为业务提示
            throw new BusinessException("请勿重复报名");
        }
    }

    @Override
    public void cancel(Long activityId, Long loginUserId) {
        int rows = applyMapper.deleteByUserAndActivity(loginUserId, activityId);
        if (rows == 0) {
            throw new BusinessException("您尚未报名该活动");
        }
    }

    @Override
    public List<ApplyVO> myApplies(Long loginUserId) {
        return applyMapper.selectMyApplies(loginUserId);
    }
}
