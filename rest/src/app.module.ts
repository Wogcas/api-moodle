import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ConfigModule } from '@nestjs/config';
import { HttpModule } from '@nestjs/axios';
import { MoodleConfig } from './config/moodle.config';
import { enviroment } from './config/enviroment';


@Module({
  imports: [
    ConfigModule.forRoot({
            envFilePath: '.env', 
            isGlobal: true, 
    }),
    HttpModule.register({
      baseURL: enviroment.moodleurl
    })
  ],
  controllers: [AppController],
  providers: [AppService, MoodleConfig],
})
export class AppModule {}
