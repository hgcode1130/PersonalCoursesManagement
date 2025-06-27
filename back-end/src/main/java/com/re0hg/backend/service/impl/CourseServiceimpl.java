package com.re0hg.backend.service.impl;

import com.re0hg.backend.mapper.CourseMapper;
import com.re0hg.backend.mapper.TermMapper;
import com.re0hg.backend.pojo.Course;
import com.re0hg.backend.pojo.PageBean;
import com.re0hg.backend.pojo.ScheduleEntry;
import com.re0hg.backend.pojo.Term;
import com.re0hg.backend.service.CourseService;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author re0hg
 * @version 1.0
 * @date 2025/6/22
 */

@Service
public class CourseServiceimpl implements CourseService {
  @Autowired
  private CourseMapper courseMapper;

  @Autowired
  private TermMapper termMapper;

  @Override
  @Transactional
  public Course createCourse(Course course, Long userId) {
    // 1. 验证学期是否存在并属于当前用户
    Long termId = course.getTerm().getId();
    boolean isOwner = courseMapper.isTermOwnedByUser(termId, userId);
    if (!isOwner) {
      throw new RuntimeException("无权限操作此学期或学期不存在");
    }

    // 2. 获取完整的学期信息
    Term term = termMapper.findById(termId);
    course.setTerm(term);

    // 3. 设置默认标签值（如果未提供）
    if (course.getTag() == null) {
      course.setTag(1); // 默认为必修
    }

    // 4. 创建课程
    try {
      courseMapper.createCourse(course);
    } catch (Exception e) {
      throw new RuntimeException("创建课程失败: " + e.getMessage());
    }

    // 5. 如果有排程信息，创建排程
    if (course.getScheduleEntries() != null && !course.getScheduleEntries().isEmpty()) {
      for (ScheduleEntry entry : course.getScheduleEntries()) {
        // 验证排程时间是否合法
        validateScheduleEntry(entry);

        // 设置课程关联
        entry.setCourse(course);

        try {
          courseMapper.createScheduleEntry(entry);
        } catch (Exception e) {
          throw new RuntimeException("创建排程失败: " + e.getMessage());
        }
      }
    }

    // 6. 查询完整的课程信息（包含排程）
    Course createdCourse = courseMapper.findById(course.getId());
    List<ScheduleEntry> scheduleEntries = courseMapper.findScheduleEntriesByCourseId(course.getId());

    // 生成周次列表
    for (ScheduleEntry entry : scheduleEntries) {
      List<Integer> weeks = new ArrayList<>();
      for (int i = entry.getStartWeek(); i <= entry.getEndWeek(); i++) {
        weeks.add(i);
      }
      entry.setWeeks(weeks);
    }

    createdCourse.setScheduleEntries(scheduleEntries);

    return createdCourse;
  }

   @Override
  public PageBean<Course> getCoursesForTermWithPagination(Long termId, Long userId, int page, int size) {
    // 1. 验证学期是否属于当前用户
    boolean isOwner = courseMapper.isTermOwnedByUser(termId, userId);
    if (!isOwner) {
      throw new RuntimeException("无权限访问此学期或学期不存在");
    }

    // 2. 计算偏移量
    int offset = page * size;

    // 3. 查询当前页的课程列表
    List<Course> courses = courseMapper.findByTermIdWithPagination(termId, offset, size);

    // 4. 查询总记录数
    long totalElements = courseMapper.countByTermId(termId);

    // 5. 为每个课程查询排程信息
    for (Course course : courses) {
      List<ScheduleEntry> scheduleEntries = courseMapper.findScheduleEntriesByCourseId(course.getId());

      // 生成周次列表
      for (ScheduleEntry entry : scheduleEntries) {
        List<Integer> weeks = new ArrayList<>();
        for (int i = entry.getStartWeek(); i <= entry.getEndWeek(); i++) {
          weeks.add(i);
        }
        entry.setWeeks(weeks);
      }

      course.setScheduleEntries(scheduleEntries);
    }

    // 6. 构建分页结果
    return new PageBean<>(courses, page, size, totalElements);
  }
  /**
   * 验证排程信息是否合法
   */
  private void validateScheduleEntry(ScheduleEntry entry) {
    // 1. 验证星期几
    if (entry.getDayOfWeek() == null || entry.getDayOfWeek() < 1 || entry.getDayOfWeek() > 7) {
      throw new RuntimeException("星期几必须为1-7的整数");
    }

    // 2. 验证节次
    if (entry.getStartPeriod() == null || entry.getStartPeriod() < 1 || entry.getStartPeriod() > 12) {
      throw new RuntimeException("开始节次必须为1-12的整数");
    }

    if (entry.getEndPeriod() == null || entry.getEndPeriod() < entry.getStartPeriod() || entry.getEndPeriod() > 12) {
      throw new RuntimeException("结束节次必须大于等于开始节次且不超过12");
    }

    // 3. 验证周次
    if (entry.getStartWeek() == null || entry.getStartWeek() < 1 || entry.getStartWeek() > 20) {
      throw new RuntimeException("开始周必须为1-20的整数");
    }

    if (entry.getEndWeek() == null || entry.getEndWeek() < entry.getStartWeek() || entry.getEndWeek() > 20) {
      throw new RuntimeException("结束周必须大于等于开始周且不超过20");
    }
  }
  
}
