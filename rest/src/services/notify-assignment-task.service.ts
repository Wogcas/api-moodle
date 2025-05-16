import { HttpException, Injectable, Logger } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { Cron } from "@nestjs/schedule";
import { MoodleService } from "src/config/moodle.service";
import { RabbitMQService } from "src/config/rabbitmq.service";
import { AssigmentSubmition } from "src/dtos/assignment-submission.dto";

@Injectable()
export class NotifyAssignmentTaskService {
private readonly logger = new Logger(NotifyAssignmentTaskService.name);
  private lastPollTime = new Date(Date.now() - 15 * 60 * 1000);

  constructor(
    private readonly moodleService: MoodleService,
    private readonly rabbitMQService: RabbitMQService, 
    private readonly configService: ConfigService,
  ) {}

  @Cron('*/10 * * * * *') 
  async checkNewSubmissions() {
    try {
      this.logger.log('====== STARTING SUBMISSION CHECK ======');
      const assignments = await this.getActiveAssignments();
      await this.processAssignments(assignments);
      this.lastPollTime = new Date();
      this.logger.log('====== SUBMISSION CHECK COMPLETED ======');
    } catch (error) {
      this.logger.error('====== ERROR IN SUBMISSION CHECK ======');
      if (error instanceof HttpException) {
        throw error;
      } else {
        throw new HttpException(
          'Failed to poll Moodle API',
          error.message,
        );
      }
    }
  }

  /**
   * Método público para permitir la invocación manual desde un controlador REST
   */
  manualCheck() {
    this.logger.log('Manual check requested');
    this.checkNewSubmissions();
  }

  private async getActiveAssignments(): Promise<any[]> {
    const params: Record<string, any> = {};
    try {
      const response: any = await this.moodleService.executeGetRequest(
        'mod_assign_get_assignments',
        params,
      );

      const result: any[] = [];

      if (response && response.courses) {
        for (const course of response.courses) {
          if (course.assignments) {
            result.push(...course.assignments);
          }
        }
      }
      return result;
    } catch (error) {
      this.logger.error('Error retrieving assignments: {}', error.message, error);
      throw new HttpException('Failed to get active assignments', error.message);
    }
  }

  private async processAssignments(assignments: any[]) {
    for (const assignment of assignments) {
      try {
        const sendNotifications = assignment.sendnotifications ?? 0;

        if (sendNotifications === 1) {
          await this.checkRecentSubmissions(assignment);
        }
      } catch (error) {
        this.logger.error(
          `Error processing assignment ${assignment.name}: ${error.message}`,
          error,
        );
      }
    }
  }

  private async checkRecentSubmissions(assignment: any) {
    try {
      const assignmentId: number = assignment.id;
      const params: Record<string, string> = {
        'assignmentids[0]': assignmentId.toString(),
      };

      const response: any = await this.moodleService.executePostRequest(
        'mod_assign_get_submissions',
        params,
      );

      let submissions: any[] = [];
      if (
        response &&
        response.assignments &&
        Array.isArray(response.assignments) &&
        response.assignments.length > 0 &&
        response.assignments[0].submissions &&
        Array.isArray(response.assignments[0].submissions)
      ) {
        submissions = response.assignments[0].submissions;
      }

      if (submissions.length === 0) {
        this.logger.log(`No submissions found for assignment '${assignment.name}'.`);
      } else {
        for (const submission of submissions) {
          await this.processSubmission(assignment, submission);
        }
      }
    } catch (error) {
      this.logger.error(
        `Error checking submissions for assignment ${assignment.name}: ${error.message}`,
        error,
      );
    }
  }

  private async processSubmission(assignment: any, submission: any) {
    try {
      const timeModified: number = parseInt(submission.timemodified ?? '0', 10);
      const submissionTime = new Date(timeModified * 1000);

      if (submissionTime > this.lastPollTime) {
        this.logger.log(
          `Found new submission for assignment '${assignment.name}' by user ${submission.userid}`,
        );
        this.sendToRabbitMQ(assignment, submission);
      }
    } catch (error) {
      this.logger.error('Error processing submission: {}', error.message, error);
    }
  }

  private async sendToRabbitMQ(assignment: any, submission: any) {
    try {
      const messagePayload = {
        assignmentId: assignment.id,
        assignmentName: assignment.name,
        submissionId: submission.id,
        userId: submission.userid,
        timeModified: submission.timemodified,
      };

      this.rabbitMQService.publishMessage(
        '#',
        messagePayload,
      );

      this.logger.log(
        "Sent new submission for assignment '{}' (ID: {}) by user {} to RabbitMQ.",
        assignment.name,
        assignment.id,
        submission.userid,
      );
    } catch (error) {
      this.logger.error('Error sending submission to RabbitMQ: {}', error.message, error);
    }
  }
}