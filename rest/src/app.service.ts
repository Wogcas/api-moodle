import { Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleService } from './config/moodle.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';
import { MapperService } from './utils/mapper-dto.service';
import { lastValueFrom, map } from 'rxjs';
import { HttpService } from '@nestjs/axios';
import { enviroment } from './config/enviroment';
import { ConfigService } from '@nestjs/config';
import { CourseContent } from './dtos/course-content.dto';
import { AssginmentInfo } from './dtos/assignment-info.dto';
import { UserDTO } from './dtos/user.dto';

@Injectable()
export class AppService {

  constructor(
    private readonly moodleService: MoodleService,
    private readonly mapperService: MapperService,
    private readonly configService: ConfigService,
  ) {}

  getHello(): string {
    return 'Hello World!';
  }

  async getSiteInfo(): Promise<MoodleSiteInfo> {
    const response = await this.moodleService.executeGetRequest('core_webservice_get_site_info', {});
    return this.mapperService.mapMoodleSiteInfo(response);
  }

  async getCourses(): Promise<CourseInfo[]> {
    const response = await this.moodleService.executeGetRequest('core_course_get_courses', {});
    return this.mapperService.mapCourses(response);
  }

  async getCourseContents(courseId: number): Promise<CourseContent[]> {
    const params = { courseid: courseId };
    const response = await this.moodleService.executeGetRequest('core_course_get_contents', params);
    const rawResponseArray: any[] = response as any[];
    const mappedContent: CourseContent[] = this.mapperService.mapCourseContents(rawResponseArray);
    return mappedContent;
  }

  async getGradeFromCourse(userId: number, courseId: number): Promise<ReportStudentGrades[]> {
    const params = { courseid: courseId, userid: userId };
    const response: any = await this.moodleService.executeGetRequest('gradereport_user_get_grades_table', params);
    const rawResponseArray: any[] = response as any[];
    const mappedGradesReports: ReportStudentGrades[] = this.mapperService.mapReportOfGrades(rawResponseArray);

    return mappedGradesReports;
  }

  async getCoursesByStudentId(userId: number): Promise<CourseInfo[]> {
    const params = { userid: userId };
    const response = await this.moodleService.executeGetRequest('core_enrol_get_users_courses', params);
    const courses: CourseInfo[] = this.mapperService.mapCourses(response);
    return courses;
  }

  async getEnrolledUsers(courseId: number): Promise<UserInfo[]> {
    const params = { courseid: courseId };
    const response = await this.moodleService.executeGetRequest('core_enrol_get_enrolled_users', params);
    const rawResponseArray: any[] = response as any[];
    const users: UserInfo[] = this.mapperService.mapUserInfo(rawResponseArray);
    return users;
  }

  async getAssignments(courseId: number): Promise<AssginmentInfo[]> {
    const params = { 'courseids[0]': courseId.toString() };
    const response: any = await this.moodleService.executeGetRequest('mod_assign_get_assignments', params);
    if (response && Array.isArray(response.courses) && response.courses.length > 0) {
      // Buscamos el objeto del curso específico dentro del array de cursos devuelto
      const courseData = response.courses.find((course: any) => course.id === courseId);
      if (courseData && Array.isArray(courseData.assignments)) {
        // Si encontramos el curso y tiene un array de asignaciones, lo retornamos
        return courseData.assignments;
      }
    }
    return response.courses[0]?.assignments || []; // Retornamos un array vacío si no hay asignaciones
  }

  // LA DIFERENCIA ES QUE ESTA FUNCION NO FILTRA POR CURSO (util para la tarea programada)
  async getActiveAssignments(): Promise<any[]> {
    const response: any = await this.moodleService.executeGetRequest('mod_assign_get_assignments', {});
    const result: any[] = [];
    if (response && response.courses) {
      for (const course of response.courses) {
        if (course.assignments) {
          result.push(...course.assignments);
        }
      }
    }
    return result;
  }


  // ULTIMOO PENDIENNTEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  async getAssignmentsBetween(
    courseId: number,
    startDate: Date | number, 
    endDate: Date | number   
  ): Promise<any[]> { 

    const allAssignments = await this.getAssignments(courseId); // Llama a tu método que ya funciona

    const startTimestamp = startDate instanceof Date ? Math.floor(startDate.getTime() / 1000) : startDate;
    const endTimestamp = endDate instanceof Date ? Math.floor(endDate.getTime() / 1000) : endDate;

    const filteredAssignments = allAssignments.filter(assignment => {
      const duedate = assignment.duedate;
      if (typeof duedate !== 'number' || duedate <= 0) {
        return false; // Ignorar asignaciones sin fecha de entrega válida
      }
      return duedate >= startTimestamp && duedate <= endTimestamp;
    });
    return filteredAssignments;
  }

}
