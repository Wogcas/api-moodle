/**
    export interface MoodleClient {
    
        executeGetRequest<T>(wsfunction: string, params: Record<string, any>): Promise<T>;
    
        executeGetRequestForList<T>(wsfunction: string, params: Record<string, any>): Promise<T[]>;
    
        executePostRequest<T>(wsfunction: string, params: Record<string, any>): Promise<T>;
    
    }
*/