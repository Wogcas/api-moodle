export class UserInfo {
    id: number;
    username: string;
    firstname: string;
    lastname: string;
    fullname: string;
    email: string;
    enrolledcourses: {
        id: number;
        fullname: string;
        shortname: string;
    }[];
    roles: {
        roleid: number;
        shortname: string;
    }[];
}
