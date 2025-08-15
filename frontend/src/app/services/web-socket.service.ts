import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { ChatMessageDto } from '../models/dtos/ChatMessageDto';
import { environment } from '../../environments/environment.test';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private client: Client | undefined;
  private connected = false;
  private connectCallbacks: (() => void)[] = [];
  private privateSubscriptions: (() => void)[] = [];
  private hostUrl = environment.hostUrl;

  constructor(private auth: AuthService) {
    this.initClient();
  }

  private initClient() {
    const token = this.auth.getToken();

    this.client = new Client({
      webSocketFactory: () =>
        new SockJS(`${this.hostUrl}/ws-chat?token=${token}`),
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        this.connected = true;
        this.connectCallbacks.forEach((cb) => cb());
        this.connectCallbacks = [];
        this.privateSubscriptions.forEach((sub) => sub());
      },
      onDisconnect: () => {
        this.connected = false;
      },
      onStompError: (frame) => {
        console.error('Broker error: ', frame.headers['message']);
        console.error('Details: ', frame.body);
      },
    });

    this.client.activate();
  }

  sendPrivateMessage(message: ChatMessageDto) {
    if (this.connected) {
      this.client?.publish({
        destination: '/app/chat.private.send',
        body: JSON.stringify(message),
      });
    } else {
      console.warn('WebSocket not connected yet. Message not sent.');
    }
  }

  subscribeToPrivateMessages(): Observable<ChatMessageDto> {
    return new Observable((observer) => {
      if (!this.connected) {
        this.connectCallbacks.push(() => {
          this.subscribeInternal(observer);
        });
      } else {
        this.subscribeInternal(observer);
      }

      let unsubscribeFn: (() => void) | undefined;

      if (!this.connected) {
        const connectCallback = () => {
          unsubscribeFn = this.subscribeInternal(observer);
        };
        this.connectCallbacks.push(connectCallback);
      } else {
        unsubscribeFn = this.subscribeInternal(observer);
      }

      return () => {
        if (unsubscribeFn) {
          unsubscribeFn();
        }
      };
    });
  }

  private subscribeInternal(observer: any) {
    const subscription = this.client?.subscribe(
      '/user/queue/private',
      (message) => {
        const msg = JSON.parse(message.body) as ChatMessageDto;
        observer.next(msg);
      },
    );

    return () => subscription?.unsubscribe();
  }
}
