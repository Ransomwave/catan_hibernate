package org.example.dao.jugador;

import java.util.List;

import org.example.dao.IGenericDao;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.TipusRecurs;

public interface IJugadorDao extends IGenericDao<Jugador, Integer> {
    String mostrarRecursos(int idJugador);

    void construirCarretera(int idJugador, int idAresta);

    void construirPoblat(int idJugador, int idVertex);

    void millorarACiutat(int idJugador, int idVertex);

    void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep);

    int calcularPunts(int idJugador);

    void passarTorn(int idJugador);

    void moureLladre(int idJugador, int idHexagon, Integer idJugadorVictima);

    void comprarCarta(int idJugador);

    void jugarCarta(int idJugador, int idCarta, TipusRecurs recurs);

    List<Vertex> vertexsDisponibles(int idJugador);
}
