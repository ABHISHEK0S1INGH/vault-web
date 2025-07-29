import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ChatMessageDto } from '../../models/dtos/ChatMessageDto';
import { WebSocketService } from '../../services/web-socket.service';
import { FormsModule } from '@angular/forms';
import { PrivateChatService } from '../../services/private-chat.service';

@Component({
  selector: 'app-private-chat-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './private-chat-dialog.component.html',
  styleUrl: './private-chat-dialog.component.scss',
})
export class PrivateChatDialogComponent implements OnInit {
  @Input() username!: string;
  @Input() currentUsername!: string | null;
  @Input() privateChatId!: number;
  @Output() close = new EventEmitter<void>();

  messages: ChatMessageDto[] = [];
  newMessage = '';

  constructor(
    private wsService: WebSocketService,
    private chatService: PrivateChatService,
  ) {}

  ngOnInit(): void {
    this.chatService.getMessages(this.privateChatId).subscribe({
      next: (msgs) => {
        this.messages = msgs;
      },
      error: () => {
        console.error('Error loading messages for private chat');
      },
    });

    this.wsService.subscribeToPrivateMessages((msg: ChatMessageDto) => {
      if (msg.privateChatId === this.privateChatId) {
        this.messages.push(msg);
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim()) return;

    const message: ChatMessageDto = {
      content: this.newMessage,
      timestamp: new Date().toISOString(),
      senderUsername: this.currentUsername ? this.currentUsername : 'Unknown',
      privateChatId: this.privateChatId,
    };

    this.wsService.sendPrivateMessage(message);

    this.messages.push({
      ...message,
    });

    this.newMessage = '';
  }

  onClose(): void {
    this.close.emit();
  }
}
