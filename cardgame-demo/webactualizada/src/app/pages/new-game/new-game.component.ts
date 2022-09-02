import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { FormGroup } from '@angular/forms';
import { Jugador } from 'src/app/shared/model/juego';
import { ApiService } from 'src/app/shared/services/api.service';
import { AuthService } from 'src/app/shared/services/auth.service';
import { v1 as uuidv1 } from 'uuid';
import { Router } from '@angular/router';
import { WebsocketService } from 'src/app/shared/services/websocket.service';



@Component({
  selector: 'app-new-game',
  templateUrl: './new-game.component.html',
  styleUrls: ['./new-game.component.scss']
})
export class NewGameComponent implements OnInit, OnDestroy {
  form: FormGroup;
  juegoId: string;
  jugadores?: Jugador[]

  constructor(private api: ApiService, private auth: AuthService,
    private router: Router,private webSocket: WebsocketService) {
    this.form = new FormGroup({
      jugador: new FormControl()
    });
    this.juegoId = uuidv1();
     
    api.getJugadores().subscribe((jugadores) => {
      this.jugadores = jugadores;
     });
     webSocket.open(this.juegoId)
   }
 

  ngOnInit(): void {

    this.webSocket.listener((event:any)=>{
      console.log(event)
      if(event.type=="cardgame.juegocreado"){
        this.router.navigate(['list'])
      }
    })
  }

  ngOnDestroy(): void {
    this.webSocket.close();
  }

  onSubmit(){
    console.log("estamos en vivo")
    const jugadores: any = {};
    this.form.value.jugador.forEach((user:any) => {
      jugadores[user.uid] = user.alias;
    })

    this.api.crearJuego({
      jugadorPrincipalId: this.auth.user.uid,
      juegoId: this.juegoId,
      jugadores:jugadores
    }).subscribe();
  }
  onClick() {
    this.router.navigate(['list']);
  }
}
