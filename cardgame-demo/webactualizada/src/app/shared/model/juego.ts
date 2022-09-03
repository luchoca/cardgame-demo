export interface JuegoModel {
    id:string,
    iniciado: boolean,
    finalizado: boolean,
    cantidadJugadores: number,
    jugadores: Map<string,Jugador>
    uid:string;

}

export interface Jugador {
    alias:string,
    uid:string
}

