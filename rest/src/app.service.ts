import { Injectable, InternalServerErrorException, NotFoundException, StreamableFile } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleService } from './config/moodle.service';
import { CourseInfo } from './dtos/course.dto';
import { ReportStudentGrades } from './dtos/report-student-grades.dto';
import { UserInfo } from './dtos/user-info.dto';
import { MapperService } from './utils/mapper-dto.service';
import { CourseContent } from './dtos/course-content.dto';
import { AssignmentInfo } from './dtos/assignment-info.dto';
import { GradeInfo } from './dtos/grade-info.dto';

@Injectable()
export class AppService {

  constructor(
    private readonly moodleService: MoodleService,
    private readonly mapperService: MapperService,
  ) { }

  getHello(): string {
    return 'Hello World!';
  }

  async getSiteInfo(): Promise<MoodleSiteInfo> {
    try {
      const response = await this.moodleService.executeGetRequest('core_webservice_get_site_info', {});
      return this.mapperService.mapMoodleSiteInfo(response);
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving site info');
    }
  }

  async getSingleGrade(
    courseId: number,
    userId: number,
    gradeItemName: string
  ): Promise<GradeInfo> {
    try {
      const params = { courseid: courseId, userid: userId };
      const response = await this.moodleService.executeGetRequest<{ tables?: any[] }>(
        'gradereport_user_get_grades_table',
        params
      );

      if (!response?.tables?.[0]) {
        throw new NotFoundException('No grades found');
      }

      const rawData = response.tables[0];
      const gradeInfo = this.mapperService.mapToGradeInfo(rawData, rawData.userfullname);

      const searchTerm = gradeItemName.toLowerCase().trim();
      gradeInfo.gradeItems = gradeInfo.gradeItems.filter(item =>
        item.itemname.toLowerCase().includes(searchTerm)
      );

      if (gradeInfo.gradeItems.length === 0) {
        throw new NotFoundException(`Grade item "${gradeItemName}" not found`);
      }

      return gradeInfo;
    } catch (error) {
      if (error instanceof NotFoundException) throw error;
      throw new InternalServerErrorException('Error retrieving grade');
    }
  }

  async getCourses(): Promise<CourseInfo[]> {
    try {
      const response = await this.moodleService.executeGetRequest('core_course_get_courses', {});
      return this.mapperService.mapCourses(response);
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving courses');
    }
  }

  async getCourseContents(courseId: number): Promise<CourseContent[]> {
    try {
      const params = { courseid: courseId };
      const response = await this.moodleService.executeGetRequest('core_course_get_contents', params);
      const rawResponseArray: any[] = response as any[];
      const mappedContent: CourseContent[] = this.mapperService.mapCourseContents(rawResponseArray);
      return mappedContent;
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving course contents');
    }
  }

  async getGradeFromCourse(userId: number, courseId: number): Promise<ReportStudentGrades[]> {
    try {
      const params = { courseid: courseId, userid: userId };
      const response: any = await this.moodleService.executeGetRequest('gradereport_user_get_grades_table', params);
      const rawResponseArray: any[] = response as any[];
      const mappedGradesReports: ReportStudentGrades[] = this.mapperService.mapReportOfGrades(rawResponseArray);
      return mappedGradesReports;
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving course grades');
    }
  }


  async getGradeFromCourseWithDetails(courseId: number): Promise<ReportStudentGrades[]> {
    try {
      const gradeReportParams = { courseid: courseId };
      const rawGradeReportResponse: any = await this.moodleService.executeGetRequest('gradereport_user_get_grades_table', gradeReportParams);

      const allCourses: CourseInfo[] = await this.getCourses();
      const courseDetails = allCourses.find((c) => Number(c.id) === Number(courseId));
      const courseidnumber = courseDetails?.idnumber ?? '';

      const enrolledUsers = await this.getEnrolledUsers(courseId);

      let teacheremail = '';
      const teacherUser = enrolledUsers.find((user: any) =>
        Array.isArray(user.roles) && user.roles.some((role: any) =>
          role.shortname === 'editingteacher' || role.shortname === 'teacher' || role.shortname === 'manager'
        )
      );
      if (teacherUser) {
        teacheremail = teacherUser.email ?? '';
      }

      const mappedGradesReports: ReportStudentGrades[] = this.mapperService.mapReportOfGrades(rawGradeReportResponse);

      const finalReports: ReportStudentGrades[] = mappedGradesReports.map(report => {
        const studentUser = enrolledUsers.find((u: any) => Number(u.id) === Number(report.userid));
        const studentemail = studentUser?.email ?? '';
        const studentroles = studentUser?.roles?.map((r: any) => r.shortname) ?? [];
        return {
          ...report,
          courseidnumber: courseidnumber,
          useremail: studentemail,
          teacheremail: teacheremail,
          userroles: studentroles,
        };
      });

      return finalReports;
    } catch (error) {
      if (error?.errorcode === 'accessexception') {
        throw new InternalServerErrorException('Permission denied to retrieve data from Moodle.');
      }
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Error retrieving course grades with details');
    }
  }

  async getCoursesByStudentId(userId: number): Promise<CourseInfo[]> {
    try {
      const params = { userid: userId };
      const response = await this.moodleService.executeGetRequest('core_enrol_get_users_courses', params);
      const courses: CourseInfo[] = this.mapperService.mapCourses(response);
      return courses;
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving courses by student ID');
    }
  }

  async getEnrolledUsers(courseId: number): Promise<UserInfo[]> {
    try {
      const params = { courseid: courseId };
      const response = await this.moodleService.executeGetRequest('core_enrol_get_enrolled_users', params);
      const rawResponseArray: any[] = response as any[];
      const users: UserInfo[] = this.mapperService.mapUserInfo(rawResponseArray);
      return users;
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving enrolled users');
    }
  }

  async getAssignments(courseId: number): Promise<AssignmentInfo[]> {
    try {
      const params = { 'courseids[0]': courseId.toString() };
      const response: any = await this.moodleService.executeGetRequest('mod_assign_get_assignments', params);

      if (response && response.courses && Array.isArray(response.courses)) {
        const courseData = response.courses.find((course: any) => Number(course.id) === Number(courseId));
        if (courseData && Array.isArray(courseData.assignments) && courseData.assignments.length > 0) {
          return this.mapperService.mapAssignments(courseData.assignments);
        }
        if (courseData) {
          return [];
        }
      }
      throw new NotFoundException(`No assignments found for courseId ${courseId}`);
    } catch (error) {
      if (error instanceof NotFoundException) throw error;
      throw new InternalServerErrorException('Error retrieving assignments');
    }
  }

  // LA DIFERENCIA ES QUE ESTA FUNCION NO FILTRA POR CURSO (util para la tarea programada)
  async getActiveAssignments(): Promise<any[]> {
    try {
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
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving active assignments');
    }
  }

  async getAssignmentsBetween(
    courseId: number,
    startDate: Date | number,
    endDate: Date | number
  ): Promise<AssignmentInfo[]> {
    try {
      const allAssignments: AssignmentInfo[] = await this.getAssignments(courseId);
      const startTimestamp = startDate instanceof Date ? Math.floor(startDate.getTime() / 1000) : startDate;
      const endTimestamp = endDate instanceof Date ? Math.floor(endDate.getTime() / 1000) : endDate;
      const filteredAssignments = allAssignments.filter(assignment => {
        if (assignment.duedate === null) {
          return false;
        }
        let dueDateTimestamp: number;
        if (assignment.duedate instanceof Date) {
          dueDateTimestamp = Math.floor(assignment.duedate.getTime() / 1000);
        } else if (typeof assignment.duedate === 'number') {
          dueDateTimestamp = assignment.duedate;
        } else {
          return false;
        }
        return dueDateTimestamp >= startTimestamp && dueDateTimestamp <= endTimestamp;
      });

      return filteredAssignments;
    } catch (error) {
      throw new InternalServerErrorException('Error retrieving assignments between dates');
    }
  }


  async getSubmissionFile(
    courseId: number,
    assignmentId: number,
    userId: number,
  ): Promise<StreamableFile> {
    try {
      const submission = await this.getAssignmentSubmission(assignmentId, userId);
      const filePlugin = submission.plugins?.find((p: any) => p.type === 'file');
      const fileArea = filePlugin?.fileareas?.find((a: any) => a.area === 'submission_files');
      const files = fileArea?.files;

      if (!files?.length) {
        throw new NotFoundException('No files found in this submission');
      }

      const file = files[0];
      const fileBuffer = await this.moodleService.downloadFile(file.fileurl);

      const streamable = new StreamableFile(fileBuffer, {
        type: file.mimetype || 'application/octet-stream',
        disposition: `attachment; filename="${encodeURIComponent(file.filename)}"`,
        length: fileBuffer.length,
      });

      return streamable;
    } catch (error) {
      if (error instanceof NotFoundException) throw error;
      throw new InternalServerErrorException('Error retrieving submission file');
    }
  }

  private async getAssignmentSubmission(
    assignmentId: number,
    userId: number,
  ): Promise<any> {
    try {
      const params = {
        assignid: assignmentId,
        userid: userId,
      };

      const response = await this.moodleService.executeGetRequest(
        'mod_assign_get_submission_status',
        params,
      ) as {
        lastattempt?: {
          submission?: {
            userid: number;
            plugins?: any[];
          };
        };
      };

      if (!response?.lastattempt?.submission) {
        throw new NotFoundException('Submission not found');
      }

      if (Number(response.lastattempt.submission.userid) !== Number(userId)) {
        throw new NotFoundException('Submission not found');
      }

      return response.lastattempt.submission;
    } catch (error) {
      if (error?.errorcode === 'nopermission') {
        throw new NotFoundException('Submission not found or no permission');
      }
      throw error;
    }
  }
}
