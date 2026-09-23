package com.login.schoolregistersystem.mapper;

import com.login.schoolregistersystem.entity.Activity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ActivityMapper {

    /** 列别名使下划线字段映射到驼峰属性，appliedCount 为关联子查询统计的已报名人数 */
    String COLUMNS = "a.activity_id AS activityId, a.title, a.description, "
            + "a.max_people AS maxPeople, a.deadline, a.create_user_id AS createUserId, "
            + "a.create_time AS createTime, "
            + "(SELECT COUNT(*) FROM apply_record r WHERE r.activity_id = a.activity_id) AS appliedCount";

    /** 查询全部活动（按创建时间倒序），带已报名人数 */
    @Select("SELECT " + COLUMNS + " FROM activity a "
            + "ORDER BY a.create_time DESC, a.activity_id DESC")
    List<Activity> selectAll();

    /** 按ID查询活动，带已报名人数 */
    @Select("SELECT " + COLUMNS + " FROM activity a WHERE a.activity_id = #{id}")
    Activity selectById(@Param("id") Long id);

    /** 按ID查询活动并对该行加写锁，避免并发报名超额 */
    @Select("SELECT " + COLUMNS + " FROM activity a WHERE a.activity_id = #{id} FOR UPDATE")
    Activity selectByIdForUpdate(@Param("id") Long id);

    /** 新增活动，主键回填到 activityId */
    @Insert("INSERT INTO activity(title, description, max_people, deadline, create_user_id) "
            + "VALUES(#{title}, #{description}, #{maxPeople}, #{deadline}, #{createUserId})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId", keyColumn = "activity_id")
    int insert(Activity activity);

    /** 编辑活动（仅标题、描述、名额、截止时间可改） */
    @Update("UPDATE activity SET title = #{title}, description = #{description}, "
            + "max_people = #{maxPeople}, deadline = #{deadline} "
            + "WHERE activity_id = #{activityId}")
    int update(Activity activity);

    /** 删除活动 */
    @Delete("DELETE FROM activity WHERE activity_id = #{id}")
    int deleteById(@Param("id") Long id);
}
