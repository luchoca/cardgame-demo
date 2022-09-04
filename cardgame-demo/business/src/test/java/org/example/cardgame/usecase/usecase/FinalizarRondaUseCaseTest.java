package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.domain.Juego;
import org.example.cardgame.domain.command.FinalizarRondaCommand;
import org.example.cardgame.domain.events.*;
import org.example.cardgame.domain.values.*;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
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
class FinalizarRondaUseCaseTest {

    @Mock
    private JuegoDomainEventRepository repository;

    @InjectMocks

    private FinalizarRondaUseCase useCase;

    @Test
    void finalizarRonda() {
        var command = new FinalizarRondaCommand();
        command.setJuegoId("juegoid");

        when(repository.obtenerEventosPor("juegoid"))
                .thenReturn(historico());

        StepVerifier
                .create(useCase.apply(Mono.just(command)))
                .expectNextMatches(domainEvent -> {
                    var event = (CartasAsignadasAJugador) domainEvent;

                    return event.aggregateRootId().equals("jugadorootid")
                            && event.getGanadorId().equals(JugadorId.of("jugadorid"))
                            && event.getPuntos().equals(1000)
                            && event.getCartasApuesta().equals(Set.of(new Carta(CartaMaestraId.of("CARTAÑERY"), 1000, true, true),
                            new Carta(CartaMaestraId.of("CARTAPUNGA"), 999, true, true)));
                }).expectNextMatches(domainEvent -> {
                    var event = (RondaTerminada) domainEvent;
                    return event.aggregateRootId().equals("jugadorootid")
                            && event.getTableroId().equals(TableroId.of("tableroid"))
                            && event.getJugadorIds().equals(Set.of(JugadorId.of("jugadorid"), JugadorId.of("jugadorid2")));
                })
                .expectComplete()
                .verify();

    }

    private Flux<DomainEvent> historico() {
        var event = new JuegoCreado(JugadorId.of("jugadorid"));
        event.setAggregateRootId("jugadorootid");

        var event2 = new JugadorAgregado(JugadorId.of("jugadorid"),
                "alias1",
                new Mazo(Set.of(new Carta(CartaMaestraId.of("CARTAÑERI"), 1000, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid"), 800, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid1"), 701, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid2"), 500, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid3"), 700, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid4"), 1, false, true))));
        event2.setAggregateRootId("jugadorootid");

        var event3 = new JugadorAgregado(JugadorId.of("jugadorid2"),
                "alias2",
                new Mazo(Set.of(new Carta(CartaMaestraId.of("CARTAPUNGA"), 999, true, true),
                        new Carta(CartaMaestraId.of("cartamaestraid11"), 33, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid12"), 701, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid23"), 500, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid32"), 123, false, true),
                        new Carta(CartaMaestraId.of("cartamaestraid47"), 333, false, true))));
        event3.setAggregateRootId("jugadorootid");

        var event4 = new TableroCreado(TableroId.of("tableroid"),
                Set.of(JugadorId.of("jugadorid"),
                        JugadorId.of("jugadorid2")));
        event4.setAggregateRootId("jugadorootid");

        var event5 = new RondaCreada(
                new Ronda(1,
                        Set.of(JugadorId.of("jugadorid"),
                                JugadorId.of("jugadorid2"))),
                80);
        event5.setAggregateRootId("jugadorootid");

        var event6 = new RondaIniciada();
        event6.setAggregateRootId("jugadorootid");

        var event7 = new CartaPuestaEnTablero(event4.getTableroId(), event2.getJugadorId(), new Carta(CartaMaestraId.of("CARTAÑERY"), 1000, true, true));
        event7.setAggregateRootId("jugadorootid");

        var event8 = new CartaQuitadaDelMazo(event2.getJugadorId(), new Carta(CartaMaestraId.of("CARTAÑERY"), 1000, true, true));
        event8.setAggregateRootId("jugadorootid");

        var event9 = new CartaPuestaEnTablero(event4.getTableroId(), event2.getJugadorId(), new Carta(CartaMaestraId.of("CARTAPUNGA"), 999, true, true));
        event7.setAggregateRootId("jugadorootid");

        var event10 = new CartaQuitadaDelMazo(event2.getJugadorId(), new Carta(CartaMaestraId.of("CARTAPUNGA"), 999, true, true));
        event8.setAggregateRootId("jugadorootid");


        return Flux.just(event, event2, event3, event4, event5, event6, event7, event8, event9, event10);
    }
}