import { Injectable } from "@nestjs/common";
import { AssignmentInfo } from "src/dtos/assignment-info.dto";
import { CourseContent } from "src/dtos/course-content.dto";
import { CourseInfo } from "src/dtos/course.dto";
import { ReportStudentGrades } from "src/dtos/report-student-grades.dto";
import { MoodleSiteInfo } from "src/dtos/site-info.dto";
import { UserInfo } from "src/dtos/user-info.dto";

@Injectable()
export class MapperService {

    mapMoodleSiteInfo(response: any): MoodleSiteInfo {
        const {
            sitename,
            username,
            fullname,
            functions,
            release,
            userissiteadmin,
        } = response;
        return { sitename, username, fullname, functions, release, userissiteadmin };
    }

    mapCourses(response: any): CourseInfo[] {
        if (!Array.isArray(response)) return [];
        return response
            .filter((course: any) => course.format === 'topics')
            .map((course: any) => {
                const {
                    id,
                    fullname,
                    displayname,
                    idnumber,
                    format,
                    timecreated,
                    timemodified
                } = course;
                return {
                    id,
                    fullname,
                    displayname,
                    idnumber,
                    format,
                    timecreated: typeof timecreated === 'number' ? this.unixTimestampMapper(timecreated) : timecreated,
                    timemodified: typeof timemodified === 'number' ? this.unixTimestampMapper(timemodified) : timemodified
                };
            });
    }

    mapCourseContents(rawCourseContent: any[]): CourseContent[] {
        if (!Array.isArray(rawCourseContent)) return [];
        return rawCourseContent.map(rawSection => {
            const mappedSection: CourseContent = {
                id: rawSection.id,
                name: rawSection.name,
                section: rawSection.section,
                modules: []
            };
            if (rawSection.modules && Array.isArray(rawSection.modules)) {
                mappedSection.modules = rawSection.modules.map((rawModule: any) => {
                    const mappedModule = {
                        id: rawModule.id,
                        name: rawModule.name,
                        dates: []
                    };
                    if (rawModule.dates && Array.isArray(rawModule.dates)) {
                        mappedModule.dates = rawModule.dates.map((rawDate: any) => {
                            const mappedDate = {
                                label: rawDate.label,
                                timestamp: typeof rawDate.timestamp === 'number'
                                    ? this.unixTimestampMapper(rawDate.timestamp)
                                    : rawDate.timestamp,
                            };
                            return mappedDate;
                        });
                    }
                    return mappedModule;
                });
            }
            return mappedSection;
        });
    }

    mapUserInfo(rawUserInfo: any[]): UserInfo[] {
        if (!Array.isArray(rawUserInfo)) return [];
        return rawUserInfo.map(rawUser => {
            const mappedUser: UserInfo = {
                id: rawUser.id,
                username: rawUser.username,
                firstname: rawUser.firstname,
                lastname: rawUser.lastname,
                fullname: rawUser.fullname,
                email: rawUser.email,
                enrolledcourses: []
            };
            if (rawUser.enrolledcourses && Array.isArray(rawUser.enrolledcourses)) {
                mappedUser.enrolledcourses = rawUser.enrolledcourses.map((rawCourse: any) => ({
                    id: rawCourse.id,
                    fullname: rawCourse.fullname,
                    shortname: rawCourse.shortname
                }));
            }
            return mappedUser;
        });
    }

    mapAssignments(rawAssignments: any[]): AssignmentInfo[] {
        if (!Array.isArray(rawAssignments)) {
            return [];
        }
        return rawAssignments.map(rawAssignment => {

            const cleanIntro = rawAssignment.intro
                ? rawAssignment.intro.replace(/<[^>]*>/g, '') // Si intro existe, remueve las etiquetas HTML
                : '';

            const mappedAssignment: AssignmentInfo = {
                id: rawAssignment.id,
                cmid: rawAssignment.cmid,
                course: rawAssignment.course,
                name: rawAssignment.name,
                sendnotifications: rawAssignment.sendnotifications,
                duedate: typeof rawAssignment.duedate === 'number' && rawAssignment.duedate !== 0
                    ? this.unixTimestampMapper(rawAssignment.duedate)
                    : null,
                grade: rawAssignment.grade,
                intro: cleanIntro
            };
            return mappedAssignment;
        });
    }

    mapReportOfGrades(rawReportOfGrades: any): ReportStudentGrades[] {
        if (!rawReportOfGrades || !Array.isArray(rawReportOfGrades.tables)) {
            return [];
        }

        return rawReportOfGrades.tables.map((table: any) => {
            const { courseid, userid, userfullname, tabledata } = table;

            const gradeItems = Array.isArray(tabledata)
                ? tabledata
                    .filter((item: any) => item.itemname && item.itemname.content)
                    .map((item: any) => {
                        const itemname = item.itemname.content
                            ? item.itemname.content.replace(/<[^>]*>/g, '').trim()
                            : '';

                        let grade: number | string | null = null;
                        if (item.grade && typeof item.grade.content === 'string') {
                            const parsed = parseFloat(item.grade.content.replace(',', '.'));
                            grade = isNaN(parsed) ? item.grade.content : parsed;
                        }
                        return {
                            itemname,
                            grade
                        };
                    })
                : [];

            return {
                courseid,
                userid,
                userfullname,
                gradeItems,
            };
        });
    }


    private unixTimestampMapper(timestamp: number): Date {
        return new Date(timestamp * 1000);
    }

}