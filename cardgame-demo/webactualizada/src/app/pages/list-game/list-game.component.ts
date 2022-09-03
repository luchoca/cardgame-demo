import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { JuegoModel } from 'src/app/shared/model/juego';
import { ApiService } from 'src/app/shared/services/api.service';
import { AuthService } from 'src/app/shared/services/auth.service';
import { WebsocketService } from 'src/app/shared/services/websocket.service';



@Component({
  selector: 'app-list-game',
  templateUrl: './list-game.component.html',
  styleUrls: ['./list-game.component.scss']
})
export class ListGameComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['alias', 'cantidad', 'iniciado', 'id'];
  dataSource: JuegoModel[] = [];
  constructor(
    public api: ApiService,
    public authService: AuthService,
    public webSocket: WebsocketService,
    public router: Router) {

  }
  ngOnDestroy(): void {
    this.webSocket.close();
  }

  ngOnInit(): void {
    this.api.getAllJuegos().subscribe((juegos)=>{
      this.dataSource = juegos.filter((juego)=>this.authService.user.uid in juego.jugadores)

    })
    // this.api.getMisJuegos(this.authService.user.uid).subscribe((elements) => {
    //   this.dataSource = elements;
    // });
  }

  entrar(id: string) {
    this.router.navigate(['board', id]);
  }
  
  iniciar(id: string) {
    this.webSocket.open(id);
    this.webSocket.listener((event) => {
      console.log(event);
      if (event.type == 'cardgame.tablerocreado') {
        this.api.crearRonda({
          juegoId: id,
          tiempo: 10,
          jugadores: event.jugadorIds.map((it: any) => it.uuid)
        }).subscribe();
      }
      if (event.type == 'cardgame.rondacreada') {
         this.router.navigate(['board',id]);
        }
      });
      this.api.iniciar({ juegoId: id }).subscribe();
    }

}