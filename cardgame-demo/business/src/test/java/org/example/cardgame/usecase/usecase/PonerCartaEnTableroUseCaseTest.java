package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.domain.command.PonerCartaEnTablero;
import org.example.cardgame.domain.events.*;
import org.example.cardgame.domain.values.*;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import org.example.cardgame.usecase.gateway.ListaDeCartaService;
import org.example.cardgame.usecase.gateway.model.CartaMaestra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PonerCartaEnTableroUseCaseTest {
    @InjectMocks
    private PonerCartaEnTableroUseCase useCase;

    @Mock
    private JuegoDomainEventRepository repository;

    @Test
    void ponerCartaEnTablero() {
        //ARRANGE
        var command = new PonerCartaEnTablero();
        command.setJuegoId("XXXX");
        command.setJugadorId("MESSI");
        command.setCartaId("CARAÑERY");


        when(repository.obtenerEventosPor("XXXX")).thenReturn(history());

        //ACT & ASSERT
        StepVerifier
                .create(useCase.apply(Mono.just(command)))
                .expectNextMatches(domainEvent -> {
                    var event = (CartaPuestaEnTablero) domainEvent;
                    return event.aggregateRootId().equals("XXXX")
                            && event.getCarta().value().cartaId().value().equals("CARAÑERY")
                            && event.getJugadorId().value().equals("MESSI")
                            && event.getTableroId().value().equals("XXXX");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaQuitadaDelMazo) domainEvent;
                    return event.aggregateRootId().equals("XXXX")
                            && event.getJugadorId().value().equals("MESSI")
                            && event.getCarta().value().cartaId().value().equals("CARAÑERY");
                })
                .expectComplete()
                .verify();
    }

    private Flux<DomainEvent> history() {
        var event = new JuegoCreado(JugadorId.of("MESSI"));

        var event2 = new JugadorAgregado(
                JugadorId.of("MESSI"),"GIANNI",
                new Mazo(Set.of(
                        new Carta(CartaMaestraId.of("CARAÑERY"), 10,true,true),
                        new Carta(CartaMaestraId.of("bbb"), 102,true,true),
                        new Carta(CartaMaestraId.of("ccc"), 101,true,true),
                        new Carta(CartaMaestraId.of("ddd"), 104,true,true),
                        new Carta(CartaMaestraId.of("fff"), 150,true,true),
                        new Carta(CartaMaestraId.of("ggg"), 160,true,true)
                )));

        var event3 = new TableroCreado(TableroId.of("XXXX"),
                Set.of(
                        JugadorId.of("MESSI"),
                        JugadorId.of("GGGG"),
                        JugadorId.of("HHHH")
                )
        );

        var event4 = new RondaCreada(
                new Ronda(1,
                        Set.of(JugadorId.of("Gianni"),
                                JugadorId.of("Mati"),
                                JugadorId.of("Joaco"))),
                30);

        var event5 = new RondaIniciada();

        event.setAggregateRootId("XXXX");
        event2.setAggregateRootId("XXXX");
        event3.setAggregateRootId("XXXX");
        event4.setAggregateRootId("XXXX");
        event5.setAggregateRootId("XXXX");

        return Flux.just(event, event2, event3, event4, event5);
    }
}