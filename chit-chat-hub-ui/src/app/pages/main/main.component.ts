import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {ChatResponse} from "../../services/models/chat-response";
import {ChatService} from "../../services/services/chat.service";
import {ChatListComponent} from "../../components/chat-list/chat-list.component";
import {MessageService} from "../../services/services/message.service";
import {ChatWindowComponent} from "../../components/chat-window/chat-window.component";
import {KeycloakService} from "../../utils/keycloak/keycloak.service";
import * as Stomp from 'stompjs';
import SockJS from "sockjs-client";
import {Notification} from "./notification";
import {MessageResponse} from "../../services/models/message-response";
import {NgIf, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    ChatListComponent,
    ChatWindowComponent,
    NgIf,
    NgOptimizedImage
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy {

  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse | undefined;

  chatMessages: MessageResponse[] = [];

  socketClient: any = null;

  private notificationSubscription: any;

  constructor(
    private chatService: ChatService,
    private messageService: MessageService,
    private keyCloakService: KeycloakService,
    private ngZone: NgZone
  ) {
  }

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }

  getAllChats() {
    this.chatService.getChatByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      }
    })
  }

  private getAllChatMessages(chatId: string) {
    this.messageService.getMessages({
      "chat-id": chatId
    }).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
      }
    })
  }

  chatSelectedMethod(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    // Update selectedChat's unreadCount immutably
    if (this.selectedChat) {
      this.selectedChat = {...this.selectedChat, unreadCount: 0};
      // Also update chats array so unreadCount zero is reflected in list
      this.chats = this.chats.map(c =>
        c.id === this.selectedChat?.id ? this.selectedChat! : c
      );
    }
  }

  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      'chat-id': this.selectedChat?.id as string
    }).subscribe({
      next: () => {
        // Optionally update local messages state here if needed
      }
    });
  }

  private initWebSocket() {
    if (this.keyCloakService.keycloak.tokenParsed?.sub) {
      let ws = new SockJS('http://localhost:8080/ws');
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keyCloakService.keycloak.tokenParsed?.sub}/notification`;
      this.socketClient.connect({'Authorization': 'Bearer ' + this.keyCloakService.keycloak.token},
        () => {
          this.notificationSubscription = this.socketClient.subscribe(subUrl,
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);
              console.log(notification.content);

              // Run inside Angular zone for change detection
              this.ngZone.run(() => {
                this.handleNotification(notification);
              });
            },
            () => console.error('Error while connecting to webSocket')
          );
        }
      );
    }
  }

  private handleNotification(notification: Notification) {
    if (!notification) return;

    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const message: MessageResponse = {
            senderId: notification.senderId,
            receiverId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString()
          };

          const updatedSelectedChat: ChatResponse = {
            ...this.selectedChat,
            lastMessage: notification.type === 'IMAGE' ? 'Attachment' : notification.content,
            lastMessageTime: new Date().toString()
          };
          this.selectedChat = updatedSelectedChat;

          this.chats = this.chats.map(c =>
            c.id === updatedSelectedChat.id ? updatedSelectedChat : c
          );

          this.chatMessages = [...this.chatMessages, message];
          break;

        case 'SEEN':
          this.chatMessages = this.chatMessages.map(m => ({...m, state: 'SEEN'}));
          break;
      }
    } else {
      const destChat = this.chats.find(c => c.id === notification.chatId);
      if (destChat && notification.type !== 'SEEN') {
        const updatedChat: ChatResponse = {
          ...destChat,
          lastMessage: notification.type === 'IMAGE' ? 'Attachment' : notification.content,
          lastMessageTime: new Date().toString(),
          unreadCount: (destChat.unreadCount || 0) + 1
        };

        this.chats = this.chats.map(c =>
          c.id === updatedChat.id ? updatedChat : c
        );
      } else if (notification.type === 'MESSAGE') {
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          receiverId: notification.receiverId,
          lastMessage: notification.content,
          name: notification.chatName,
          unreadCount: 1,
          lastMessageTime: new Date().toString()
        };
        this.chats = [newChat, ...this.chats];
      }
    }
  }

}
