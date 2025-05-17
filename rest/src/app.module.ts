import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ConfigModule } from '@nestjs/config';
import { HttpModule } from '@nestjs/axios';
import { MoodleService } from './config/moodle.service';
import { enviroment } from './config/enviroment';
import { ScheduleModule } from '@nestjs/schedule';
import { RabbitMQService } from './config/rabbitmq.service';
import { NotifyAssignmentTaskService } from './services/notify-assignment-task.service';
import { MapperService } from './utils/mapper-dto.service';

@Module({
  imports: [
    ConfigModule.forRoot({
            envFilePath: '.env', 
            isGlobal: true, 
    }),
    HttpModule.register({
      baseURL: enviroment.moodleurl
    }),
    ScheduleModule.forRoot(), 
  ],
  controllers: [AppController],
  providers: [AppService, MoodleService, RabbitMQService, NotifyAssignmentTaskService, MapperService],
})
export class AppModule {}
