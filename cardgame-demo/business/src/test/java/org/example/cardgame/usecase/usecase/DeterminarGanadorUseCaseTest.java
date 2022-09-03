package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.domain.events.*;
import org.example.cardgame.domain.values.*;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import org.example.cardgame.usecase.gateway.ListaDeCartaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeterminarGanadorUseCaseTest {

    @Mock
    private JuegoDomainEventRepository service;

    @InjectMocks
    private DeterminarGanadorUseCase useCase;

    @Test
    void rondaTerminada() {
        //ARRANGE
        var event = new RondaTerminada(TableroId.of("tableroid"),
                Set.of(JugadorId.of("jugadorid1")));
        event.setAggregateRootId("agregadorootid");

        when(service.obtenerEventosPor("agregadorootid"))
                .thenReturn(historico());

        StepVerifier
                .create(useCase.apply(Mono.just(event)))
                .expectNextMatches(domainEvent -> {
                    var event2 = (JuegoFinalizado) domainEvent;
                    return event2.aggregateRootId().equals("agregadorootid");
                })
                .expectComplete()
                .verify();

    }

    private Flux<DomainEvent> historico() {
        var event = new JuegoCreado(JugadorId.of("jugadorid1"));
        event.setAggregateRootId("agregadorootid");

        var event1 = new JugadorAgregado( JugadorId.of("jugadorid1"),
                "Raul",
                new Mazo(Set.of(
                        new Carta(CartaMaestraId.of("CARTAÑERI"),999,false,true),
                        new Carta(CartaMaestraId.of("cartamaestraid"),800,false,true),
                        new Carta(CartaMaestraId.of("cartamaestraid1"),701,false,true),
                        new Carta(CartaMaestraId.of("cartamaestraid2"),500,false,true),
                        new Carta(CartaMaestraId.of("cartamaestraid3"),700,false,true),
                        new Carta(CartaMaestraId.of("cartamaestraid4"),1,false,true)
                )));
        event1.setAggregateRootId("agregadorootid");
        return Flux.just(event,event1);
    }
}