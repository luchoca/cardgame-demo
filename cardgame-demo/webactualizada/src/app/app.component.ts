import { Component } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { AuthGuard } from './shared/guard/auth.guard';
import { ApiService } from './shared/services/api.service';
import { AuthService } from './shared/services/auth.service';
import { WebsocketService } from './shared/services/websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})


export class AppComponent {
  title = 'web';
  isExpanded:boolean = true;
  constructor(
    public afAuth: AngularFireAuth,
    public api: ApiService,
    public authService: AuthService,
    public webSocket: WebsocketService,
    public router: Router) {
  
  }
  signOut() {
    return this.afAuth.signOut().then(() => {
      localStorage.removeItem('user');
      this.router.navigate(['login']);
    });
  }
}
