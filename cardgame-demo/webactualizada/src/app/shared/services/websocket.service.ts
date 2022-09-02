import { Injectable } from '@angular/core';
import { webSocket } from 'rxjs/webSocket';
import { environment } from 'src/environments/environment';

export interface EventHandle{
  (event: any): void;
}
@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  subject: any;
  constructor() {}

  listener(callback:EventHandle) {
    this.subject.subscribe({
      next: (msg: any) => callback(msg), // Called whenever there isamassage from the server.
      error: (err: any) => console.log(err), // Called if at any point WebSocket API signals some kind of error.
      complete: () => console.log('complete'), // Called when connection is closed(for whatever reason).
    });
  }
  open(juegoId:any){
    this.subject = webSocket( environment.socketurl + juegoId);
    return this.subject
  }

  close(){
    this.subject.unsubscribe();
    // if(this.subject){
      
    // }
  }
}