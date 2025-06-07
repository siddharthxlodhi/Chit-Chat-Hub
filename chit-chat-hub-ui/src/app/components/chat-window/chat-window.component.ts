import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { ChatResponse } from '../../services/models/chat-response';
import { MessageResponse } from '../../services/models/message-response';
import { MessageService } from '../../services/services/message.service';
import { DatePipe, NgIf } from '@angular/common';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { MessageRequest } from '../../services/models/message-request';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [
    NgIf,
    DatePipe,
    PickerComponent,
    FormsModule
  ],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.scss'
})
export class ChatWindowComponent implements OnInit, AfterViewInit, AfterViewChecked, OnChanges {

  showEmoji = false;
  messageContent = '';

  @Input() chat!: ChatResponse;
  @Input() chatMessages!: MessageResponse[];

  @ViewChild('scrollAnchor') private scrollAnchor!: ElementRef;

  constructor(
    private messageService: MessageService,
    private keyCloakService: KeycloakService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chat'] && changes['chat'].currentValue) {
      setTimeout(() => this.scrollToBottom(), 0);
    }
  }

  private scrollToBottom(): void {
    try {
      this.scrollAnchor?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
    } catch (err) {
      console.warn('Scrolling failed', err);
    }
  }

  selfMessage(message: MessageResponse) {
    return message.senderId === this.keyCloakService.userId;
  }

  uploadMedia(target: EventTarget | null): void {
    const file = this.extractFileFromInput(target);
    if (!file) return;

    this.messageService.uploadMedia({
      "chat-id": this.chat.id as string,
      body: { file }
    }).subscribe({
      next: (res) => {
        const message: MessageResponse = {
          senderId: this.getSenderId(),
          receiverId: this.getReceiverId(),
          content: 'Attachment',
          type: 'IMAGE',
          state: 'SENT',
          media: res.response,
          createdAt: new Date().toISOString()
        };
        this.chatMessages.push(message);
        setTimeout(() => this.scrollToBottom(), 0);
      },
      error: (err) => {
        console.error('Upload failed', err);
      }
    });
  }

  private extractFileFromInput(target: EventTarget | null): File | null {
    const input = target as HTMLInputElement;
    return input?.files?.[0] ?? null;
  }

  onSelectEmojis(event: any): void {
    const emoji: EmojiData = event.emoji;
    this.messageContent += emoji.native;
  }

  KeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  onClick() {
    this.setMessagesToSeen();
  }

  sendMessage() {
    if (this.messageContent.trim()) {
      const msgReq: MessageRequest = {
        chatId: this.chat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT'
      };

      this.messageService.savemessage({ body: msgReq }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toISOString()
          };
          this.chat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmoji = false;
          setTimeout(() => this.scrollToBottom(), 0);
        }
      });
    }
  }

  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      "chat-id": this.chat.id as string
    }).subscribe();
  }

  private getSenderId() {
    return this.chat.senderId === this.keyCloakService.userId
      ? this.chat.senderId as string
      : this.chat.receiverId;
  }

  private getReceiverId() {
    return this.chat.senderId === this.keyCloakService.userId
      ? this.chat.receiverId as string
      : this.chat.senderId;
  }
}
