import { Component, NgZone, OnDestroy, OnInit, HostListener } from '@angular/core';
import { ChatResponse } from "../../services/models/chat-response";
import { ChatService } from "../../services/services/chat.service";
import { ChatListComponent } from "../../components/chat-list/chat-list.component";
import { MessageService } from "../../services/services/message.service";
import { ChatWindowComponent } from "../../components/chat-window/chat-window.component";
import { KeycloakService } from "../../utils/keycloak/keycloak.service";
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from "sockjs-client";
import { Notification } from "./notification";
import { MessageResponse } from "../../services/models/message-response";
import { NgIf, NgOptimizedImage } from "@angular/common";

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
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit, OnDestroy {

  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse | undefined;
  chatMessages: MessageResponse[] = [];

  private socketClient!: Client;
  private notificationSubscription: any;

  isDesktopView = window.innerWidth >= 768;

  constructor(
    private chatService: ChatService,
    private messageService: MessageService,
    private keyCloakService: KeycloakService,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
  }

  ngOnDestroy(): void {
    if (this.socketClient && this.socketClient.active) {
      this.socketClient.deactivate();
    }
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
    }
  }

  @HostListener('window:resize')
  onResize() {
    this.isDesktopView = window.innerWidth >= 768;
    // If resized to desktop view, keep chat list and chat window visible
    // If resized to mobile view and a chat is selected, keep only chat window visible
    if(this.isDesktopView) {
      // On desktop, keep selected chat as is (both panels visible)
    } else {
      // On mobile, if no chat selected, show chat list panel
      if(!this.selectedChat) {
        // nothing special, chat list visible by *ngIf logic in template
      }
    }
  }

  getAllChats() {
    this.chatService.getChatByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      }
    });
  }

  private getAllChatMessages(chatId: string) {
    this.messageService.getMessages({
      "chat-id": chatId
    }).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
      }
    });
  }

  chatSelectedMethod(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    // this.setMessagesToSeen();

    if (this.selectedChat) {
      this.selectedChat = { ...this.selectedChat, unreadCount: 0 };
      this.chats = this.chats.map(c =>
        c.id === this.selectedChat?.id ? this.selectedChat! : c
      );
    }
  }

  backToChatList() {
    this.selectedChat = undefined;
  }

  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      'chat-id': this.selectedChat?.id as string
    }).subscribe({
      next: () => {
        // Optional local update
      }
    });
  }

  private initWebSocket() {
    const userId = this.keyCloakService.keycloak.tokenParsed?.sub;
    const token = this.keyCloakService.keycloak.token;

    if (!userId || !token) return;

    // const wsUrl = 'http://localhost:8080/ws';
    const wsUrl = 'https://chit-chat-hub-vzan.onrender.com/ws';
    const subUrl = `/user/${userId}/notification`;

    this.socketClient = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      onConnect: () => {
        this.notificationSubscription = this.socketClient.subscribe(subUrl, (message: IMessage) => {
          const notification: Notification = JSON.parse(message.body);

          this.ngZone.run(() => {
            this.handleNotification(notification);
          });
        });
      },
      onStompError: frame => {
        console.error('Broker reported error: ', frame.headers['message']);
        console.error('Details: ', frame.body);
      }
    });

    this.socketClient.activate();
  }

  private handleNotification(notification: Notification) {
    if (!notification) return;

    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE': {
          const message: MessageResponse = {
            senderId: notification.senderId,
            receiverId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toISOString()
          };

          const updatedSelectedChat: ChatResponse = {
            ...this.selectedChat,
            lastMessage: notification.type === 'IMAGE' ? 'Attachment' : notification.content,
            lastMessageTime: new Date().toISOString()
          };
          this.selectedChat = updatedSelectedChat;

          this.chats = this.chats.map(c =>
            c.id === updatedSelectedChat.id ? updatedSelectedChat : c
          );

          this.chatMessages = [...this.chatMessages, message];
          break;
        }

        case 'SEEN':
          this.chatMessages = this.chatMessages.map(m => ({ ...m, state: 'SEEN' }));
          break;
      }
    } else {
      const destChat = this.chats.find(c => c.id === notification.chatId);
      if (destChat && notification.type !== 'SEEN') {
        const updatedChat: ChatResponse = {
          ...destChat,
          lastMessage: notification.type === 'IMAGE' ? 'Attachment' : notification.content,
          lastMessageTime: new Date().toISOString(),
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
          lastMessageTime: new Date().toISOString()
        };
        this.chats = [newChat, ...this.chats];
      }
    }
  }
}
