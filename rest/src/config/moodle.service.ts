import { BadRequestException, HttpException, HttpStatus, Injectable } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { lastValueFrom } from "rxjs";
import { HttpService } from '@nestjs/axios';

@Injectable()
export class MoodleService {
    private readonly MoodleURL: string;
    private readonly MoodleToken: string;
    private readonly ALLOWED_MIME_TYPES = [
        'application/pdf',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'image/png',
        'image/jpeg'
    ];

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
        try {
            const baseUri = `${this.MoodleURL}?wstoken=${this.MoodleToken}&wsfunction=${wsfunction}&moodlewsrestformat=json`;
            let fullUri = baseUri;
            for (const key in params) {
                if (params.hasOwnProperty(key)) {
                    fullUri += `&${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`;
                }
            }
            return fullUri;
        } catch (error) {
            throw new HttpException(
                `Error building URI: ${error.message}`,
                HttpStatus.INTERNAL_SERVER_ERROR,
            );
        }
    }

    async downloadFile(fileUrl: string, expectedMimeType?: string): Promise<Buffer> {
        try {
            console.log(`Downloading file from URL: ${fileUrl}`);

            const authenticatedUrl = new URL(fileUrl);
            authenticatedUrl.searchParams.append('token', this.MoodleToken);

            const response = await lastValueFrom(
                this.httpService.get(authenticatedUrl.toString(), {
                    responseType: 'arraybuffer',
                    maxRedirects: 5,
                    headers: {
                        'Authorization': `Bearer ${this.MoodleToken}`,
                        'Accept': expectedMimeType || '*/*'
                    }
                })
            );

            if (!response.data || response.data.byteLength <= 0) {
                throw new Error('Empty file received');
            }

            const contentType = response.headers['content-type'];
            if (expectedMimeType && contentType && !contentType.includes(expectedMimeType)) {
                throw new BadRequestException(`El tipo de archivo recibido (${contentType}) no coincide con el esperado (${expectedMimeType})`);
            }

            if (contentType && !this.ALLOWED_MIME_TYPES.some(mime => contentType.includes(mime))) {
                throw new BadRequestException(`Tipo de archivo no soportado: ${contentType}`);
            }

            console.log(`[MoodleService] File downloaded successfully. Size: ${response.data.byteLength} bytes, Type: ${contentType}`);
            return Buffer.from(response.data);
        } catch (error) {
            console.error(`[MoodleService] Error downloading file: ${error.message}`);
            if (error instanceof BadRequestException) {
                throw error;
            }
            throw new HttpException(
                `Error downloading file: ${error.message}`,
                HttpStatus.INTERNAL_SERVER_ERROR,
            );
        }
    }
}