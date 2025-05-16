import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as amqplib from 'amqplib';

@Injectable()
export class RabbitMQService implements OnModuleInit {

  private readonly rabbitMQURL: string;
  private readonly exchangeName: string;
  private readonly logger = new Logger(RabbitMQService.name);
  private connection: amqplib.ChannelModel | null = null;
  private channel: amqplib.Channel | null = null;

  constructor(private readonly configService: ConfigService) { 
    this.rabbitMQURL = this.configService.get<string>('RABBITMQ_URL')!;
    this.exchangeName = this.configService.get<string>('RABBITMQ_EXCHANGE_TASK_SUBMIT')!;
  }

  async onModuleInit() {
    await this.connect();
  }

  async connect(): Promise<void> {
    try {
      this.connection = await amqplib.connect(this.rabbitMQURL);
      this.channel = await this.connection.createChannel();
      await this.channel.assertExchange(this.exchangeName, 'topic', { durable: true });

      console.log(`RabbitMQService conectado a RabbitMQ. Exchange: ${this.exchangeName}`);


      this.connection.on('error', (err) => console.error('Error en la conexión RabbitMQ (RabbitMQService):', err));
      this.connection.on('close', () => console.log('Conexión RabbitMQ cerrada (RabbitMQService), intentando reconectar...'));

    } catch (error) {
      console.error('Error al conectar RabbitMQService:', error);
      setTimeout(() => this.connect(), 5000);
    }
  }

  async publishMessage(routingKey: string, message: any): Promise<boolean> {
    if (!this.channel) {
      console.error('Canal de RabbitMQService no inicializado.');
      return false;
    }
    try {
      await this.channel.publish(
        this.exchangeName,
        routingKey,
        Buffer.from(JSON.stringify(message)),
        { persistent: true }
      );
      console.log(`Notification publicada al exchange "${this.exchangeName}" con routing key "${routingKey}":`, message);
      return true;
    } catch (error) {
      console.error('Error al publicar notificación:', error);
      return false;
    }
  }

  async close(): Promise<void> {
    if (this.channel) await this.channel.close();
    if (this.connection) await this.connection.close();
    console.log('RabbitMQService desconectado.');
  }
}

