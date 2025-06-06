export interface Notification {
  chatId: string;
  content: string;
  senderId: string;
  receiverId: string;
  chatName: string;
  messageType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO'; // Adjust as needed
  type: 'MESSAGE' | 'IMAGE' | 'SEEN' | string; // Adjust as needed
  media: string// For byte[] equivalent in TS
}
