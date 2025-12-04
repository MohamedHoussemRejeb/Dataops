Chatbot Angular snippet

This folder contains a minimal Angular component you can drop into your Angular app to call the new backend endpoints.

Files:
- `chatbot-dialog.component.ts` : Angular component (uses HttpClient and ngModel). Add to a module and include `FormsModule` and `HttpClientModule`.

Usage:
1. Copy `chatbot-dialog.component.ts` into your Angular project's component folder.
2. Declare the component in a module and ensure `FormsModule` and `HttpClientModule` are imported.
3. Place `<app-chatbot-dialog></app-chatbot-dialog>` where you want the button/modal to open.

Notes:
- The component calls `POST http://localhost:8090/api/chatbot/ask` and expects plain text.
- You might want to wire this component to a proper modal/dialog framework (Angular Material, NG Bootstrap, etc.).
