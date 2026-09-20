package com.login.schoolregistersystem.service.impl;

import com.login.schoolregistersystem.common.BusinessException;
import com.login.schoolregistersystem.entity.Activity;
import com.login.schoolregistersystem.mapper.ActivityMapper;
import com.login.schoolregistersystem.mapper.ApplyMapper;
import com.login.schoolregistersystem.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ApplyMapper applyMapper;

    @Override
    public Long add(Activity activity, Long loginUserId) {
        validate(activity);
        activity.setActivityId(null);
        activity.setCreateUserId(loginUserId);
        activityMapper.insert(activity);
        return activity.getActivityId();
    }

    @Override
    public void update(Activity activity, Long loginUserId) {
        Activity old = requireOwned(activity.getActivityId(), loginUserId, "只能修改自己创建的活动");
        validate(activity);
        old.setTitle(activity.getTitle());
        old.setDescription(activity.getDescription());
        old.setMaxPeople(activity.getMaxPeople());
        old.setDeadline(activity.getDeadline());
        activityMapper.update(old);
    }

    @Override
    @Transactional
    public void delete(Long activityId, Long loginUserId) {
        requireOwned(activityId, loginUserId, "只能删除自己创建的活动");
        applyMapper.deleteByActivity(activityId);
        activityMapper.deleteById(activityId);
    }

    @Override
    public List<Activity> list() {
        return activityMapper.selectAll();
    }

    @Override
    public Activity detail(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return activity;
    }

    /** 发布/编辑共用的字段校验：标题非空、名额为正数、截止时间晚于当前时间 */
    private void validate(Activity activity) {
        if (activity.getTitle() == null || activity.getTitle().isBlank()) {
            throw new BusinessException("活动标题不能为空");
        }
        if (activity.getTitle().length() > 100) {
            throw new BusinessException("活动标题不能超过100个字");
        }
        if (activity.getMaxPeople() == null || activity.getMaxPeople() <= 0) {
            throw new BusinessException("最大报名人数必须大于0");
        }
        if (activity.getDeadline() == null) {
            throw new BusinessException("报名截止时间不能为空");
        }
        if (!activity.getDeadline().isAfter(LocalDateTime.now())) {
            throw new BusinessException("报名截止时间必须晚于当前时间");
        }
    }

    /** 校验活动存在且操作者为创建者，不满足抛出业务异常 */
    private Activity requireOwned(Long activityId, Long loginUserId, String notOwnerMessage) {
        Activity old = activityMapper.selectById(activityId);
        if (old == null) {
            throw new BusinessException("活动不存在");
        }
        if (!old.getCreateUserId().equals(loginUserId)) {
            throw new BusinessException(notOwnerMessage);
        }
        return old;
    }
}
