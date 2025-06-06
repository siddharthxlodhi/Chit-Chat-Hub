import {ChatResponse} from "../../services/models/chat-response";
import {Component, input, InputSignal, OnInit, output} from "@angular/core";
import {DatePipe, NgClass, NgForOf, UpperCasePipe} from "@angular/common";
import {UserResponse} from "../../services/models/user-response";
import {UserService} from "../../services/services/user.service";
import {KeycloakService} from "../../utils/keycloak/keycloak.service";
import {ChatService} from "../../services/services/chat.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    UpperCasePipe,
    NgClass
  ],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss'
})
export class ChatListComponent implements OnInit {

  loginUser: string | undefined;

  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  chatSelected = output<ChatResponse>();

  searchNewContact = false;  //show chats if false
  contacts: Array<UserResponse> = [];


  constructor(
    private userService: UserService,
    private keycloakService: KeycloakService,
    private chatService: ChatService,
    private toastrService: ToastrService
  ) {
  }

  logout() {
    this.keycloakService.logout();
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }


  searchContact() {
    this.userService.getAllUsers().subscribe({
      next: (res) => {
        this.contacts = res;
        this.searchNewContact = true;
      }
    })
  }

  chatClicked(chat: ChatResponse) { //Emitting the selected chat to show in right panel
    this.chatSelected.emit(chat);
  }


  contactClicked(contact: UserResponse) {
    this.chatService.createChat({
      'sender-id': this.keycloakService.userId as string,
      'receiver-id': contact.id as string
    }).subscribe({
      next: (res) => {
        console.log("Chat created with:", contact.firstName); // Debug
        this.toastrService.info("Created chat with " + contact.firstName);

        const chat: ChatResponse = {
          id: res.response,
          name: contact.firstName + ' ' + contact.lastName,
          recipientOnline: contact.online,
          senderId: this.keycloakService.userId,
          receiverId: contact.id,
          createdDate: new Date().toString()
        };

        this.chats().unshift(chat);
        this.searchNewContact = false;
        this.chatSelected.emit(chat);
      },
      error: (err) => {
        this.toastrService.error(err.error?.error || "Something went wrong");
        this.searchNewContact = false;
      }
    });
  }

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;

    }
    return lastMessage?.substring(0, 17) + '...';
  }

  protected readonly moveBy = moveBy;

  ngOnInit(): void {
    this.loginUser = this.keycloakService.fullName;
  }
}
