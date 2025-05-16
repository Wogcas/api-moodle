import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';
import { MoodleSiteInfo } from './dtos/site-info.dto';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';

@Controller('api/moodle')
export class AppController {
  constructor(
    private readonly appService: AppService,
    private readonly notifyAssignmentTaskService: NotifyAssignmentTaskService, 
  ) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Get('site-info')
  async testConnection(): Promise<MoodleSiteInfo> {
    const params: Record<string, string> = {};
    const response = await this.appService.getSiteInfo(params);
    return response;
  }

  @Get('check')
  async checkNewSubmissions() {
    const response = await this.notifyAssignmentTaskService.manualCheck();
    return response;
  }

}
