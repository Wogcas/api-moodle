import { Controller, Get, HttpException, HttpStatus, Param } from '@nestjs/common';
import { AppService } from './app.service';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';
import { CourseContent } from './dtos/course-content.dto';
import { AssignmentInfo } from './dtos/assignment-info.dto';

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
    try {
      return await this.appService.getSiteInfo();
    } catch (error) {
      throw new HttpException('Error retrieving site info', HttpStatus.BAD_REQUEST);
    }
  }

  @Get('user/:userId/course/:courseId/grades')
  async getCourseGradesFromStudent(
    @Param('userId') userId: number,
    @Param('courseId') courseId: number
  ): Promise<ReportStudentGrades[]> {
    try {
      return await this.appService.getGradeFromCourse(userId, courseId);
    } catch (error) {
      throw new HttpException('Error retrieving course grades', HttpStatus.BAD_REQUEST);
    }
  }

  
    // Done
    @Get('course/:courseId/grades-all')
    async getAllCourseGrades(
      @Param('courseId') courseId: number
    ): Promise<ReportStudentGrades[]> {
      try {
        return await this.appService.getGradeFromCourseWithDetails(courseId);
      } catch (error) {
        throw new HttpException('Error retrieving all course grades', HttpStatus.BAD_REQUEST);
      }
    }
  

  // Done
  @Get('courses')
  async getCourses(): Promise<CourseInfo[]> {
    try {
      return await this.appService.getCourses();
    } catch (error) {
      throw new HttpException('Error retrieving courses', HttpStatus.BAD_REQUEST);
    }
  }

  // Done
  @Get('courses/:userId')
  async getCoursesByStudentId(@Param('userId') userId: number): Promise<CourseInfo[]> {
    try {
      return await this.appService.getCoursesByStudentId(userId);
    } catch (error) {
      throw new HttpException('Error retrieving courses by student ID', HttpStatus.BAD_REQUEST);
    }
  }

  // Done
  @Get('courses/:courseId/contents')
  async getCourseContents(@Param('courseId') courseId: number): Promise<CourseContent[]> {
    try {
      return await this.appService.getCourseContents(courseId);
    } catch (error) {
      throw new HttpException('Error retrieving course contents', HttpStatus.BAD_REQUEST);
    }
  }

  // Done
  @Get('enrolled-users/:courseId')
  async getEnrolledUsers(@Param('courseId') courseId: number): Promise<UserInfo[]> {
    try {
      return await this.appService.getEnrolledUsers(courseId);
    } catch (error) {
      throw new HttpException('Error retrieving enrolled users', HttpStatus.BAD_REQUEST);
    }
  }

  // Done
  @Get('assignments/:courseId')
  async getAssignments(@Param('courseId') courseId: number): Promise<AssignmentInfo[]> {
    try {
      return await this.appService.getAssignments(courseId);
    } catch (error) {
      throw new HttpException('Error retrieving assignments', HttpStatus.BAD_REQUEST);
    }
  }

  // Done
  @Get('assignments/:courseId/between/:start/:end')
  async getAssignmentsBetween(
    @Param('courseId') courseId: number,
    @Param('start') start: string,
    @Param('end') end: string
  ): Promise<AssignmentInfo[]> {
    try {
      const startDate = new Date(start);
      const endDate = new Date(end);
      return await this.appService.getAssignmentsBetween(courseId, startDate, endDate);
    } catch (error) {
      throw new HttpException('Error retrieving assignments between dates', HttpStatus.BAD_REQUEST);
    }
  }

  @Get('check')
  async getCheck() {
    try {
      return await this.notifyAssignmentTaskService.manualCheck();
    } catch (error) {
      throw new HttpException('Error retrieving check', HttpStatus.BAD_REQUEST);
    }
  }

}
