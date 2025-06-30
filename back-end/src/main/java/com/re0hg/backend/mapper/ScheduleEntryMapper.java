package com.re0hg.backend.mapper;

import com.re0hg.backend.pojo.ScheduleEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author re0hg
 * @version 1.0
 * @date 2025/6/30
 */
@Mapper
public interface ScheduleEntryMapper {

    /**
     * 查询课程的排程列表
     *
     * @param courseId 课程ID
     * @return 排程列表
     */
    @Select("SELECT id, location, day_of_week, start_period, end_period, start_week, end_week " +
            "FROM schedule_entries WHERE course_id = #{courseId}")
    List<ScheduleEntry> findScheduleEntriesByCourseId(@Param("courseId") Long courseId);

    /**
     * 删除课程的所有排程
     *
     * @param courseId 课程ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM schedule_entries WHERE course_id = #{courseId}")
    int deleteScheduleEntriesByCourseId(@Param("courseId") Long courseId);

    /**
     * 创建课程排程
     *
     * @param scheduleEntry 排程信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO schedule_entries (location, day_of_week, start_period, end_period, start_week, end_week, course_id) "
            +
            "VALUES (#{location}, #{dayOfWeek}, #{startPeriod}, #{endPeriod}, #{startWeek}, #{endWeek}, #{course.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createScheduleEntry(ScheduleEntry scheduleEntry);

    /**
     * 根据课程ID查询排程列表
     *
     * @param courseId 课程ID
     * @return 排程列表
     */
    @Select("SELECT * FROM schedule_entries WHERE course_id = #{courseId}")
    List<ScheduleEntry>findByCourseId(@Param("courseId") Long courseId);
}
