import { Controller, Get, Param } from '@nestjs/common';
import { AppService } from './app.service';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';

@Controller('api/moodle')
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
    return await this.appService.getCourses();
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

}
