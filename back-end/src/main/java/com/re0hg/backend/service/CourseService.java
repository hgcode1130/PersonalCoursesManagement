package com.re0hg.backend.service;

import org.springframework.stereotype.Service;

import com.re0hg.backend.pojo.Course;
import com.re0hg.backend.pojo.PageBean;

/**
 * @author re0hg
 * @version 1.0
 * @date 2025/6/22
 */
@Service
public interface CourseService {

  /**
   * 创建课程
   * 
   * @param course 课程信息
   * @param userId 用户ID
   * @return 创建后的课程
   */
  Course createCourse(Course course, Long userId);

  /**
   * 分页获取指定学期的所有课程
   * 
   * @param termId 学期ID
   * @param userId 用户ID
   * @param page   页码（从0开始）
   * @param size   每页大小
   * @return 分页结果
   */
  PageBean<Course> getCoursesForTermWithPagination(Long termId, Long userId, int page, int size);
}
