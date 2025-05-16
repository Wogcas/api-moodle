import { Injectable } from '@nestjs/common';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { MoodleConfig } from './config/moodle.config';

@Injectable()
export class AppService {
  constructor(private readonly moodleConfig: MoodleConfig) {}

  getHello(): string {
    return 'Hello World!';
  }

  async getSiteInfo(params: Record<string, string> ): Promise<MoodleSiteInfo> {
    return this.moodleConfig.executeGetRequest('core_webservice_get_site_info', params);
  }
}
