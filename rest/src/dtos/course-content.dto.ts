export class CourseContent {
    id: number;
    name: string;
    section: number;
    modules: {
        id: number;
        name: string;
        dates: {
            label: string;
            timestamp: number;
        }[];
    }[];
}