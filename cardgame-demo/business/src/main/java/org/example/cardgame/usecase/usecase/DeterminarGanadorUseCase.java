package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;

import org.example.cardgame.domain.Juego;
import org.example.cardgame.domain.events.RondaTerminada;
import org.example.cardgame.domain.values.JuegoId;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class DeterminarGanadorUseCase  extends UseCaseForEvent<RondaTerminada> {
    private final JuegoDomainEventRepository repository;
    public DeterminarGanadorUseCase(JuegoDomainEventRepository repository){
        this.repository = repository;
    }
    @Override
    public Flux<DomainEvent> apply(Mono<RondaTerminada> rondaTerminadaMono) {
        return rondaTerminadaMono.flatMapMany((event) -> repository
                .obtenerEventosPor(event.aggregateRootId())
                .collectList()
                .flatMapIterable(events -> {
                    var juego = Juego.from(JuegoId.of(event.aggregateRootId()), events);
                    var jugadores = juego.jugadores().values().stream()
                            .filter(jugador -> jugador.mazo().value().cantidad() > 0)
                            .collect(Collectors.toList());
                    var competidores = event.getJugadorIds();
                    if(jugadores.size()  == 1){
                        var jugador = jugadores.get(0);
                        juego.finalizarJuego(jugador.identity(), jugador.alias());
                    } else if (competidores.size() == 1) {
                        competidores.stream().findFirst().ifPresent(jugadorId -> {
                            var jugador = juego.jugadores().get(jugadorId);
                            juego.finalizarJuego(jugador.identity(), jugador.alias());
                        });
                    }
                    return juego.getUncommittedChanges();
                }));
    }
}

