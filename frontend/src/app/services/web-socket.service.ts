import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { ChatMessageDto } from '../models/dtos/ChatMessageDto';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private client: Client;
  private connected = false;
  private connectCallbacks: (() => void)[] = [];

  constructor() {
    const username = 'deniz';

    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws-chat'),
      connectHeaders: {
        username: username,
      },
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected = true;
        this.connectCallbacks.forEach((cb) => cb());
        this.connectCallbacks = [];
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
      this.client.publish({
        destination: '/app/chat.private.send',
        body: JSON.stringify(message),
      });
    }
  }

  subscribeToPrivateMessages(callback: (msg: ChatMessageDto) => void) {
    const subscribeFn = () => {
      this.client.subscribe('/user/queue/private', (message: Message) => {
        callback(JSON.parse(message.body));
      });
    };

    if (this.connected) {
      subscribeFn();
    } else {
      this.connectCallbacks.push(subscribeFn);
    }
  }
}
