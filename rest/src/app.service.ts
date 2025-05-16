import { Injectable } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleService } from './config/moodle.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';

@Injectable()
export class AppService {
  constructor(private readonly moodleService: MoodleService) { }

  getHello(): string {
    return 'Hello World!';
  }

  async getSiteInfo(): Promise<MoodleSiteInfo> {
    const params = {};
    return this.moodleService.executeGetRequest('core_webservice_get_site_info', params);
  }

  async getCourses(): Promise<CourseInfo[]> {
    const params = {};
    return this.moodleService.executeGetRequest('core_course_get_courses', params);
  }

  async getCourseContents(courseId: number) {
    const params = { courseid: courseId };
    return this.moodleService.executeGetRequest('core_course_get_contents', params);
  }

  async getGradeFromCourse(userId: number, courseId: number): Promise<ReportStudentGrades[]> {
    const params = {userid: userId, courseid: courseId};
    return this.moodleService.executeGetRequest('gradereport_user_get_grades_table', params);
  }

  async getCoursesByStudentId(userId: number): Promise<CourseInfo[]> {
    const params = { userid: userId };
    return this.moodleService.executeGetRequest('core_enrol_get_users_courses', params);
  }

  async getEnrolledUsers(courseId: number): Promise<UserInfo[]> {
    const params = {courseid: courseId};
    return this.moodleService.executeGetRequest('core_enrol_get_enrolled_users', params);
  }
}
