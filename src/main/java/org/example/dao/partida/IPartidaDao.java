package org.example.dao.partida;

import java.util.List;

import org.example.dao.IGenericDao;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;

public interface IPartidaDao extends IGenericDao<Partida, Integer> {
    Integer iniciarPartida(List<String> playerNames);

    void mostrarTauler(int idPartida);

    void mostrarPartida(int idPartida);

    void tirarDaus(int idPartida);

    void comprovarVictoria(int idPartida);

    Jugador exercitMesGran(int idPartida);
}
