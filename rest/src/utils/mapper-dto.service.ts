import { Injectable } from "@nestjs/common";
import { AssignmentInfo } from "src/dtos/assignment-info.dto";
import { CourseContent } from "src/dtos/course-content.dto";
import { CourseInfo } from "src/dtos/course.dto";
import { GradeInfo } from "src/dtos/grade-info.dto";
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
            .filter((course: any) => course.format === 'weeks')
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
                enrolledcourses: [],
                roles: [],
            };
            if (rawUser.enrolledcourses && Array.isArray(rawUser.enrolledcourses)) {
                mappedUser.enrolledcourses = rawUser.enrolledcourses.map((rawCourse: any) => ({
                    id: rawCourse.id,
                    fullname: rawCourse.fullname,
                    shortname: rawCourse.shortname
                }));
            }
            if (rawUser.roles && Array.isArray(rawUser.roles)) {
                mappedUser.roles = rawUser.roles.map((rawRole: any) => ({
                    roleid: typeof rawRole?.roleid === 'number' ? rawRole.roleid : 0,
                    shortname: typeof rawRole?.shortname === 'string' ? rawRole.shortname : '',

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
                ? rawAssignment.intro.replace(/<[^>]*>/g, '')
                : '';

            const mappedAssignment: AssignmentInfo = {
                id: rawAssignment.id,
                cmid: rawAssignment.cmid,
                course: rawAssignment.course,
                name: rawAssignment.name,
                sendnotifications: rawAssignment.sendnotifications,
            duedate: typeof rawAssignment.duedate === 'number' && rawAssignment.duedate !== 0
                ? this.unixTimestampMapper(rawAssignment.duedate) // Esto ahora devuelve un string
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
                        // Limpia etiquetas HTML en itemname
                        let itemname = item.itemname.content
                            ? item.itemname.content.replace(/<[^>]*>/g, '').trim()
                            : '';
                        itemname = itemname.replace(/\s+/g, ' ');
                        itemname = itemname.replace(/^Assignment\s*/, 'Assignment ');
                        itemname = itemname.replace(/^Calculated grade\s*/, 'Calculated grade ');
                        itemname = itemname.replace(/^Course total\s*/, 'Course total ');
                        itemname = itemname.replace(/^Calculated grade\s*Course total$/, 'Calculated grade Course total');
                        itemname = itemname.replace(/QuizExamen/g, 'Quiz Examen');
                        itemname = itemname.replace(/AggregationCourse/g, 'Aggregation Course');

                        let grade: number | string | null = null;
                        if (item.grade && typeof item.grade.content === 'string') {
                            // Limpia etiquetas HTML en el contenido de grade
                            const cleanGradeContent = item.grade.content.replace(/<[^>]*>/g, '').replace(',', '.').trim();
                            if (cleanGradeContent === '-' || cleanGradeContent === '') {
                                grade = '-';
                            } else {
                                const parsed = parseFloat(cleanGradeContent);
                                grade = isNaN(parsed) ? cleanGradeContent : parsed;
                            }
                        }
                        let cleanGrade = '';
                        if (item.grade && typeof item.grade.content === 'string') {
                            cleanGrade = item.grade.content
                                .replace(/<[^>]*>/g, '')
                                .replace(/\s+/g, ' ')
                                .replace(',', '.')
                                .trim();
                        }
                        if (cleanGrade === '-' || cleanGrade === '') {
                            grade = '-';
                        } else {
                            const match = cleanGrade.match(/-?\d+(\.\d+)?/);
                            if (match) {
                                grade = parseFloat(match[0]);
                            } else {
                                grade = cleanGrade;
                            }
                        }
                        return {
                            itemname: itemname.trim(),
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

    mapReportOfAllGrades(
        rawReportOfGrades: any,
        { courseidnumber, useremail }: { courseidnumber: string; useremail: string }
    ): ReportStudentGrades[] {
        if (!rawReportOfGrades || !Array.isArray(rawReportOfGrades.tables)) {
            return [];
        }

        return rawReportOfGrades.tables.map((table: any) => {
            const { courseid, userid, userfullname, tabledata } = table;

            const gradeItems = Array.isArray(tabledata)
                ? tabledata
                    .filter((item: any) => item.itemname && item.itemname.content)
                    .map((item: any) => {
                        let itemname = item.itemname.content
                            ? item.itemname.content.replace(/<[^>]*>/g, '').trim()
                            : '';
                        itemname = itemname.replace(/\s+/g, ' ');
                        itemname = itemname.replace(/^Assignment\s*/, 'Assignment ');
                        itemname = itemname.replace(/^Calculated grade\s*/, 'Calculated grade ');
                        itemname = itemname.replace(/^Course total\s*/, 'Course total ');
                        itemname = itemname.replace(/^Calculated grade\s*Course total$/, 'Calculated grade Course total');

                        let grade: number | string | null = null;
                        if (item.grade && typeof item.grade.content === 'string') {
                            const cleanGrade = item.grade.content.replace(',', '.').trim();
                            if (cleanGrade === '-' || cleanGrade === '') {
                                grade = '-';
                            } else {
                                const parsed = parseFloat(cleanGrade);
                                grade = isNaN(parsed) ? cleanGrade : parsed;
                            }
                        }
                        return {
                            itemname: itemname.trim(),
                            grade
                        };
                    })
                : [];

            return {
                courseid,
                userid,
                userfullname,
                useremail,
                courseidnumber,
                gradeItems,
            };
        });
    }

    mapToGradeInfo(rawData: any, userFullname: string): GradeInfo {
        return {
            courseid: rawData.courseid,
            userid: rawData.userid,
            userfullname: userFullname,
            gradeItems: this.extractGradeItems(rawData.tabledata)
        };
    }

    private extractGradeItems(tabledata: any[]): { itemname: string; grade: string | number | null }[] {
        if (!Array.isArray(tabledata)) return [];

        return tabledata
            .filter(item => item.itemname?.content)
            .map(item => ({
                itemname: this.cleanItemName(item.itemname.content),
                grade: this.parseGradeValue(item.grade?.content)
            }));
    }

    private cleanItemName(name: string): string {
        let cleaned = name.replace(/<[^>]*>/g, '').trim();
        cleaned = cleaned.replace(/\s+/g, ' ');
        cleaned = cleaned.replace(/^Assignment\s*/, 'Assignment ');
        cleaned = cleaned.replace(/^QuizExamen/g, 'Quiz Examen');
        return cleaned;
    }

    private parseGradeValue(gradeContent: string): string | number | null {
        if (!gradeContent) return null;
        let clean = gradeContent.replace(/<[^>]*>/g, '').replace(',', '.').replace(/\s+/g, ' ').trim();
        clean = clean.replace(/-?\s*Grade analysis\s*$/i, '-').trim();
        if (clean === '-' || clean === '') return '-';
        const match = clean.match(/-?\d+(\.\d+)?/);
        if (match) {
            const numeric = parseFloat(match[0]);
            return isNaN(numeric) ? clean : numeric;
        }
        return clean;
    }

    private formatDate(date: Date): string {
        const pad = (num: number) => num.toString().padStart(2, '0');

        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` +
            `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    }

    private unixTimestampMapper(timestamp: number): string {
        const date = new Date(timestamp * 1000);
        return this.formatDate(date);
    }

}