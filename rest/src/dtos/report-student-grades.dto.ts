export class ReportStudentGrades {
    courseid: number;
    userid: number;
    userfullname: string;
    gradeItems: {
        itemname: string;
        grade: number | string | null;
    }[];
}
