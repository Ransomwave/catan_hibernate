package org.example.dao.partida;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.dao.GenericDao;
import org.example.relationships.classes.Hexagon;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;
import org.example.relationships.enums.ColorJugador;
import org.example.relationships.enums.EstatPartida;
import org.example.relationships.enums.TipusTerreny;

public class PartidaDao extends GenericDao<Partida, Long> implements IPartidaDao {

    public void iniciarPartida(List<String> playerNames) {
        var session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            if (playerNames.size() < 2 || playerNames.size() > 4)
                throw new IllegalArgumentException("Hi ha d'haver entre 2 i 4 jugadors");

            // create partida
            var partida = new Partida(EstatPartida.PREPARACIO, 1, 10);
            session.persist(partida);

            // shuffle names and instanciate players with random turn order
            var noms = new ArrayList<>(playerNames);
            Collections.shuffle(noms); // random turn order
            for (var i = 0; i < noms.size(); i++) {
                var jugador = new Jugador(noms.get(i), ColorJugador.values()[i], i + 1, 0, partida);
                session.persist(jugador); // also saves its 5 RecursJugador (cascade)
            }

            // TODO: load from CSV!

            session.persist(partida);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            handleException(session, e);
        }
    }
}
