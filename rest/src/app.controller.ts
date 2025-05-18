import { Controller, Get, InternalServerErrorException, NotFoundException, Param, ParseIntPipe, Res, StreamableFile } from '@nestjs/common';
import { AppService } from './app.service';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';
import { CourseContent } from './dtos/course-content.dto';
import { AssginmentInfo } from './dtos/assignment-info.dto';

@Controller('api/moodle/rest')
export class AppController {
  constructor(
    private readonly appService: AppService,
    private readonly notifyAssignmentTaskService: NotifyAssignmentTaskService,
  ) { }

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  // Done
  @Get('site-info')
  async testConnection(): Promise<MoodleSiteInfo> {
    return await this.appService.getSiteInfo();
  }

  @Get('user/:userId/course/:courseId/grades')
  async getCourseGrades(
    @Param('userId') userId: number,
    @Param('courseId') courseId: number
  ): Promise<ReportStudentGrades[]> {
    return await this.appService.getGradeFromCourse(userId, courseId);
  }

  // Done
  @Get('courses')
  async getCourses(): Promise<CourseInfo[]> {
    return await this.appService.getCourses();
  }

  // Done
  @Get('courses/:userId')
  async getCoursesByStudentId(@Param('userId') userId: number): Promise<CourseInfo[]> {
    return await this.appService.getCoursesByStudentId(userId);
  }

  // Done
  @Get('courses/:courseId/contents')
  async getCourseContents(@Param('courseId') courseId: number): Promise<CourseContent[]> {
    return await this.appService.getCourseContents(courseId);
  }

  // Done
  @Get('enrolled-users/:courseId')
  async getEnrolledUsers(@Param('courseId') courseId: number): Promise<UserInfo[]> {
    return await this.appService.getEnrolledUsers(courseId);
  }

  // Done
  @Get('assignments/:courseId')
  async getAssignments(@Param('courseId') courseId: number): Promise<AssginmentInfo[]> {
    return await this.appService.getAssignments(courseId);
  }

  // Done
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

  @Get('check')
  async getCheck() {
    return await this.notifyAssignmentTaskService.manualCheck();
  }

}
