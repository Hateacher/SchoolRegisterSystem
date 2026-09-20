package com.login.schoolregistersystem.mapper;

import com.login.schoolregistersystem.vo.ApplyVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApplyMapper {

    /** 新增报名记录 */
    @Insert("INSERT INTO apply_record(user_id, activity_id) VALUES(#{userId}, #{activityId})")
    int insert(@Param("userId") Long userId, @Param("activityId") Long activityId);

    /** 删除本人某活动的报名记录，返回受影响行数 */
    @Delete("DELETE FROM apply_record "
            + "WHERE user_id = #{userId} AND activity_id = #{activityId}")
    int deleteByUserAndActivity(@Param("userId") Long userId, @Param("activityId") Long activityId);

    /** 删除某活动的全部报名记录（活动删除时级联清理） */
    @Delete("DELETE FROM apply_record WHERE activity_id = #{activityId}")
    int deleteByActivity(@Param("activityId") Long activityId);

    /** 统计活动当前报名人数（名额校验） */
    @Select("SELECT COUNT(*) FROM apply_record WHERE activity_id = #{activityId}")
    int countByActivity(@Param("activityId") Long activityId);

    /** 统计某用户对某活动是否已有报名记录（重复报名校验） */
    @Select("SELECT COUNT(*) FROM apply_record "
            + "WHERE user_id = #{userId} AND activity_id = #{activityId}")
    int countByUserAndActivity(@Param("userId") Long userId, @Param("activityId") Long activityId);

    /** 我的报名列表：报名记录关联活动信息 */
    @Select("SELECT r.activity_id AS activityId, a.title AS activityTitle, "
            + "a.deadline AS deadline, r.create_time AS createTime "
            + "FROM apply_record r JOIN activity a ON r.activity_id = a.activity_id "
            + "WHERE r.user_id = #{userId} "
            + "ORDER BY r.create_time DESC, r.id DESC")
    List<ApplyVO> selectMyApplies(@Param("userId") Long userId);
}
