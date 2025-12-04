import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-chatbot-dialog',
  template: `
  <div class="chatbot-modal">
    <h3>🤖 DataOps Chatbot</h3>
    <div class="history">
      <div *ngFor="let m of history">
        <div [class]="m.role">{{m.text}}</div>
      </div>
    </div>
    <div class="composer">
      <input [(ngModel)]="prompt" placeholder="Pose ta question..." (keyup.enter)="send()" />
      <button (click)="send()">Send</button>
    </div>
  </div>
  `,
  styles: [
    `.chatbot-modal { width: 420px; padding: 12px; background: #fff; border-radius:8px; box-shadow:0 6px 20px rgba(0,0,0,0.12);} .history{height:300px;overflow:auto;margin-bottom:8px;} .user{color:#0b5; text-align:right;} .bot{color:#036;text-align:left;} .composer{display:flex;gap:8px;} input{flex:1;padding:8px}`
  ]
})
export class ChatbotDialogComponent {
  prompt = '';
  history: { role: 'user'|'bot', text: string }[] = [];

  constructor(private http: HttpClient) {}

  send() {
    if (!this.prompt || !this.prompt.trim()) return;
    const text = this.prompt.trim();
    this.history.push({ role: 'user', text });
    this.prompt = '';

    this.http.post('http://localhost:8090/api/chatbot/ask', { prompt: text }, { responseType: 'text' })
      .subscribe({ next: (res) => {
        this.history.push({ role: 'bot', text: res as string });
      }, error: (err) => {
        this.history.push({ role: 'bot', text: 'Erreur: ' + (err?.message || 'unknown') });
      }});
  }
}
