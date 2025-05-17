import { Injectable } from "@nestjs/common";
import { CourseInfo } from "src/dtos/course.dto";
import { MoodleSiteInfo } from "src/dtos/site-info.dto";

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
        return response.map((course: any) => {
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
                timecreated,
                timemodified
            } as CourseInfo;
        });
    }

}