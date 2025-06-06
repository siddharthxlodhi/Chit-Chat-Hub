import {
  AfterViewChecked,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  input,
  InputSignal,
  OnInit,
  ViewChild
} from '@angular/core';
import {ChatResponse} from "../../services/models/chat-response";
import {MessageResponse} from "../../services/models/message-response";
import {MessageService} from "../../services/services/message.service";
import {DatePipe, NgIf} from "@angular/common";
import {KeycloakService} from "../../utils/keycloak/keycloak.service";
import {PickerComponent} from "@ctrl/ngx-emoji-mart";
import {FormsModule} from "@angular/forms";
import {EmojiData} from "@ctrl/ngx-emoji-mart/ngx-emoji";
import {MessageRequest} from "../../services/models/message-request";

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
export class ChatWindowComponent implements OnInit, AfterViewChecked {

  showEmoji = false;
  @Input() chat!: ChatResponse;
  messageContent = '';
  @Input() chatMessages!: MessageResponse[];

  @ViewChild('scrollAnchor') private scrollAnchor!: ElementRef;

  constructor(
    private messageService: MessageService,
    private keyCloakService: KeycloakService,
    private cdr: ChangeDetectorRef
  ) {
  }


  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      this.scrollAnchor?.nativeElement?.scrollIntoView({ behavior: 'auto' });

    } catch (err) {
      console.warn('Scrolling failed', err);
    }
  }


  selfMessage(message: MessageResponse) {
    return message.senderId == this.keyCloakService.userId;
  }

  ngOnInit(): void {

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
          media: res.response, // You can update this with a real media URL if returned
          createdAt: new Date().toISOString()
        };
        this.chatMessages.push(message);
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

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
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
    if (this.messageContent) {
      const msgReq: MessageRequest = {
        chatId: this.chat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT'
      }
      this.messageService.savemessage({
        body: msgReq
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.chat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmoji = false;

        }
      })
    }
  }

  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      "chat-id": this.chat.id as string
    }).subscribe({
      next: () => {
      }
    })
  }

  private getSenderId() {
    if (this.chat.senderId === this.keyCloakService.userId) {
      return this.chat.senderId as string;
    }
    return this.chat.receiverId;
  }

  private getReceiverId() {
    if (this.chat.senderId === this.keyCloakService.userId) {
      return this.chat.receiverId as string;
    }
    return this.chat.senderId;
  }


}

