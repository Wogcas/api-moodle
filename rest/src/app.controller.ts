import { Controller, Get, Param } from '@nestjs/common';
import { AppService } from './app.service';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';

@Controller('api/moodle/rest')
export class AppController {
  constructor(
    private readonly appService: AppService
  ) { }

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Get('site-info')
  async testConnection(): Promise<MoodleSiteInfo> {
    return await this.appService.getSiteInfo();
  }

  @Get('user/:userId/course/:courseId/grades')
  async getCourseGrades(@Param('userId') userId: number, @Param('courseId') courseId: number): Promise<ReportStudentGrades[]> {
    return await this.appService.getGradeFromCourse(userId, courseId);
  }

  @Get('courses')
  async getCourses(): Promise<CourseInfo[]> {
    const response = await this.appService.getCourses();
    const courses: CourseInfo[] = response.map((course) => ({
      id: course.id,
      fullname: course.fullname,
      displayname: course.displayname,
      idnumber: course.idnumber,
      format: course.format,
      timecreated: course.timecreated,
      timemodified: course.timemodified,
    }));
    return courses;
  }

  @Get('courses/:userId')
  async getCoursesByStudentId(@Param('userId') userId: number): Promise<CourseInfo[]> {
    return await this.appService.getCoursesByStudentId(userId);
  }

  @Get('courses/:courseId/contents')
  async getCourseContents(@Param('courseId') courseId: number) {
    return this.appService.getCourseContents(courseId);
  }

  @Get('enrolled-users/:courseId')
  async getEnrolledUsers(@Param('courseId') courseId: number): Promise<UserInfo[]> {
    return this.appService.getEnrolledUsers(courseId);
  }

  @Get('assignments/:courseId')
  async getAssignments(@Param('courseId') courseId: number): Promise<any[]> {
    return await this.appService.getAssignments(courseId);
  }

  @Get('assignments/:courseId/between/:start/:end')
  async getAssignmentsBetween(
    @Param('courseId') courseId: number,
    @Param('start') start: string,
    @Param('end') end: string
  ): Promise<any[]> {
    const startDate = new Date(start);
    const endDate = new Date(end);
    return await this.appService.getAssignmentsBetween(courseId, startDate, endDate);
  }

  


}
