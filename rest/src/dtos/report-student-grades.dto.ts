export class ReportStudentGrades {
    courseid: number;
    userid: number;
    userfullname: string;
    useremail: string;
    courseidnumber: string;
    gradeItems: {
        itemname: string;
        grade: number | string | null;
    }[];
}
