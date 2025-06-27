package com.re0hg.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.re0hg.backend.pojo.Course;
import com.re0hg.backend.pojo.ScheduleEntry;

/**
 * @author re0hg
 * @version 1.0
 * @date 2025/6/22
 */
@Mapper
public interface CourseMapper {
  /**
   * 创建课程
   * 
   * @param course 课程信息
   * @return 影响的行数
   */
  @Insert("INSERT INTO courses (name, teachers, main_teacher_email, course_group_chat_id, tag, note, term_id, user_id) "
      +
      "VALUES (#{name}, #{teachers}, #{mainTeacherEmail}, #{courseGroupChatId}, #{tag}, #{note}, #{term.id}, #{user.id})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int createCourse(Course course);

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
   * 根据ID查询课程
   * 
   * @param courseId 课程ID
   * @return 课程信息
   */
  @Select("SELECT id, name, teachers, main_teacher_email, course_group_chat_id, tag, note, created_at, updated_at, term_id "
      +
      "FROM courses WHERE id = #{courseId}")
  Course findById(@Param("courseId") Long courseId);

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
   * 检查学期是否属于用户
   * 
   * @param termId 学期ID
   * @param userId 用户ID
   * @return 是否属于用户
   */
  @Select("SELECT COUNT(*) > 0 FROM terms WHERE id = #{termId} AND user_id = #{userId}")
  boolean isTermOwnedByUser(@Param("termId") Long termId, @Param("userId") Long userId);

  /**
   * 分页查询指定学期的课程列表
   * 
   * @param termId   学期ID
   * @param offset   偏移量
   * @param pageSize 每页大小
   * @return 课程列表
   */
  @Select("SELECT id, name, teachers, main_teacher_email, course_group_chat_id, tag, note, term_id, user_id " +
      "FROM courses WHERE term_id = #{termId} ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
  List<Course> findByTermIdWithPagination(@Param("termId") Long termId, @Param("offset") int offset,
      @Param("pageSize") int pageSize);

  /**
   * 统计指定学期的课程总数
   * 
   * @param termId 学期ID
   * @return 课程总数
   */
  @Select("SELECT COUNT(*) FROM courses WHERE term_id = #{termId}")
  long countByTermId(@Param("termId") Long termId);
}
