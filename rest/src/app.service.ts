import { Injectable } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleService } from './config/moodle.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';
import { MapperService } from './utils/mapper-dto.service';

@Injectable()
export class AppService {
  constructor(
    private readonly moodleService: MoodleService,
    private readonly mapperService: MapperService
  ) { }

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

  async getCourseContents(courseId: number) {
    const params = { courseid: courseId };
    return this.moodleService.executeGetRequest('core_course_get_contents', params);
  }

  async getGradeFromCourse(userId: number, courseId: number): Promise<ReportStudentGrades[]> {
    const params = { userid: userId, courseid: courseId };
    return this.moodleService.executeGetRequest('gradereport_user_get_grades_table', params);
  }

  async getCoursesByStudentId(userId: number): Promise<CourseInfo[]> {
    const params = { userid: userId };
    return this.moodleService.executeGetRequest('core_enrol_get_users_courses', params);
  }

  async getEnrolledUsers(courseId: number): Promise<UserInfo[]> {
    const params = { courseid: courseId };
    return this.moodleService.executeGetRequest('core_enrol_get_enrolled_users', params);
  }

  async getAssignments(courseId: number): Promise<any[]> {
    const params = {
      'courseids[0]': courseId.toString()
    };
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

  async getAssignmentsBetween(
    courseId: number,
    startDate: Date | number, // Aceptar Date o timestamp
    endDate: Date | number   // Aceptar Date o timestamp
  ): Promise<any[]> { // Considera usar AssignmentInfo[]

    const allAssignments = await this.getAssignments(courseId); // Llama a tu método que ya funciona

    // Convertir fechas a timestamps de segundos si son objetos Date
    const startTimestamp = startDate instanceof Date ? Math.floor(startDate.getTime() / 1000) : startDate;
    const endTimestamp = endDate instanceof Date ? Math.floor(endDate.getTime() / 1000) : endDate;

    const filteredAssignments = allAssignments.filter(assignment => {
      // Moodle duedate es un timestamp en segundos. 0 a veces significa sin fecha.
      const duedate = assignment.duedate;
      // Asegurarse de que duedate es un número válido y no 0 (si 0 significa sin fecha)
      if (typeof duedate !== 'number' || duedate <= 0) {
        return false; // Ignorar asignaciones sin fecha de entrega válida
      }
      // Verificar si la fecha de entrega está dentro del rango [startDate, endDate]
      return duedate >= startTimestamp && duedate <= endTimestamp;
    });
    return filteredAssignments; // Retorna el array de asignaciones filtradas
  }
}
