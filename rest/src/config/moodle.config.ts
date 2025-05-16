import { HttpException, HttpStatus, Injectable } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { lastValueFrom, Observable } from "rxjs";
import { HttpService } from '@nestjs/axios';
//import { MoodleClient } from "src/interfaces/moodle-client.interface";

@Injectable()
export class MoodleConfig  {
    private readonly MoodleURL: string;
    private readonly MoodleToken: string;

    constructor(
        private readonly configService: ConfigService,
        private readonly httpService: HttpService,
    ) {
        this.MoodleURL = this.configService.get<string>('MOODLE_WEB_URL')!;
        this.MoodleToken = this.configService.get<string>('MOODLE_TOKEN')!;
    }

    async executeGetRequest<T>(wsfunction: string, params: Record<string, any>): Promise<T> {
        const uri = this.buildUri(wsfunction, params);
        try {
            const response = await lastValueFrom(this.httpService.get(uri));
            return response.data as T;
        } catch (error) {
            throw new HttpException(
                `Moodle API GET request failed: ${error.message}`,
                HttpStatus.INTERNAL_SERVER_ERROR,
            );
        }
    }
    async executeGetRequestForList<T>(wsfunction: string, params: Record<string, any>): Promise<T[]> {
         const uri = this.buildUri(wsfunction, params);
         try {
            const response = await lastValueFrom(this.httpService.get(uri));
            return response.data as T[];
        } catch (error) {
             throw new HttpException(
                `Moodle API GET request failed: ${error.message}`,
                HttpStatus.INTERNAL_SERVER_ERROR,
            );
        }
    }
    async executePostRequest<T>(wsfunction: string, params: Record<string, any>): Promise<T> {
        const body = new URLSearchParams();
        body.set('wstoken', this.MoodleToken);
        body.set('wsfunction', wsfunction);
        body.set('moodlewsrestformat', 'json');
        for (const key in params) {
            if (params.hasOwnProperty(key)) {
                body.set(key, params[key]);
            }
        }
        try {
            const response = await lastValueFrom(
                this.httpService.post(this.MoodleURL, body.toString(), {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                }),
            );
            return response.data as T;
        } catch (error) {
            throw new HttpException(
                `Moodle API POST request failed: ${error.message}`,
                HttpStatus.INTERNAL_SERVER_ERROR,
            );
        }
    }

    private buildUri(wsfunction: string, params: Record<string, any> = {}): string {
        const baseUri = `${this.MoodleURL}?wstoken=${this.MoodleToken}&wsfunction=${wsfunction}&moodlewsrestformat=json`;
        let fullUri = baseUri;
        for (const key in params) {
            if (params.hasOwnProperty(key)) {
                fullUri += `&${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`;
            }
        }
        return fullUri;
    }



}