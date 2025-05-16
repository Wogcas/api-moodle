import { Injectable } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleService } from './config/moodle.service';

@Injectable()
export class AppService {
  constructor(private readonly moodleService: MoodleService) {}

  getHello(): string {
    return 'Hello World!';
  }

  async getSiteInfo(params: Record<string, string>): Promise<MoodleSiteInfo> {
    return this.moodleService.executeGetRequest('core_webservice_get_site_info', params);
  }
}
