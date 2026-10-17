package org.example.dao.partida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.dao.GenericDao;
import org.example.relationships.classes.Aresta;
import org.example.relationships.classes.Hexagon;
import org.example.relationships.classes.HexagonVertex;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.ColorJugador;
import org.example.relationships.enums.EstatPartida;
import org.example.relationships.enums.TipusTerreny;
import org.hibernate.Session;

public class PartidaDao extends GenericDao<Partida, Long> implements IPartidaDao {

    public Integer iniciarPartida(List<String> playerNames) {
        var session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            if (playerNames.size() < 2 || playerNames.size() > 4)
                throw new IllegalArgumentException("There must be between 2 and 4 players");

            // create partida
            var partida = new Partida(EstatPartida.PREPARACIO, 1, 10);
            session.persist(partida);

            // shuffle names and instantiate players with random turn order
            var names = new ArrayList<>(playerNames);
            Collections.shuffle(names);
            for (var i = 0; i < names.size(); i++) {
                var player = new Jugador(names.get(i), ColorJugador.values()[i], i + 1, 0, partida);
                session.persist(player); // also saves its 5 RecursJugador (cascade)
            }

            loadBoard(session, partida);

            session.getTransaction().commit();
            return partida.getId();
        } catch (RuntimeException e) {
            handleException(session, e);
            return null;
        }
    }

    /**
     * Creates the board for a partida by reading the CSV files in /board and
     * persisting the entities.
     */
    private void loadBoard(Session session, Partida partida) {
        var vertices = new HashMap<Integer, Vertex>();
        for (var row : readCsv("vertexs.csv")) {
            var code = Integer.parseInt(row[0]);
            var vertex = new Vertex(code, partida);
            session.persist(vertex);
            vertices.put(code, vertex);
        }

        // add the terrains and numbers in the right quantities, then shuffle them
        var terrains = new ArrayList<TipusTerreny>();
        terrains.addAll(Collections.nCopies(4, TipusTerreny.BOSC)); // 4 boscos, 3 turons, 4 pastures, 4 camps, 3
                                                                    // mountains, 1 desert
        terrains.addAll(Collections.nCopies(3, TipusTerreny.TURONS));
        terrains.addAll(Collections.nCopies(4, TipusTerreny.PASTURES));
        terrains.addAll(Collections.nCopies(4, TipusTerreny.CAMPS));
        terrains.addAll(Collections.nCopies(3, TipusTerreny.MUNTANYES));
        terrains.add(TipusTerreny.DESERT);
        Collections.shuffle(terrains);

        // add the numbers for the hexagons (except for the desert) in the right
        // quantities, then shuffle them
        var numbers = new ArrayList<>(List.of(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
        Collections.shuffle(numbers);

        var hexagons = new HashMap<Integer, Hexagon>();
        // create hexagons and assign them a terrain and number (except for the desert)
        for (var row : readCsv("hexagons.csv")) {
            var code = Integer.parseInt(row[0]);
            var terrain = terrains.removeLast();
            var number = terrain == TipusTerreny.DESERT ? null : numbers.removeLast();
            var hexagon = new Hexagon(code, terrain, number, Integer.parseInt(row[1]), Integer.parseInt(row[2]),
                    partida);
            session.persist(hexagon);
            hexagons.put(code, hexagon);
            if (terrain == TipusTerreny.DESERT)
                partida.setLladre(hexagon);
        }

        // create the HexagonVertex entities that link hexagons and vertexes
        for (var row : readCsv("hexagon_vertex.csv")) {
            var hexagon = hexagons.get(Integer.parseInt(row[0]));
            var vertex = vertices.get(Integer.parseInt(row[1]));
            session.persist(new HexagonVertex(hexagon, vertex, Integer.parseInt(row[2])));
        }

        // create the Aresta entities that link vertexes
        for (var row : readCsv("arestes.csv")) {
            var vertexA = vertices.get(Integer.parseInt(row[1]));
            var vertexB = vertices.get(Integer.parseInt(row[2]));
            session.persist(new Aresta(Integer.parseInt(row[0]), partida, vertexA, vertexB));
        }
    }

    /**
     * Reads a CSV from /board and returns its rows (without the header) split by
     * commas.
     */
    private List<String[]> readCsv(String fileName) {
        var stream = getClass().getResourceAsStream("/board/" + fileName);
        if (stream == null)
            throw new IllegalStateException("File not found: /board/" + fileName);
        try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            var rows = new ArrayList<String[]>();
            reader.readLine(); // skip the header

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank())
                    rows.add(line.split(","));
            }
            return rows;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
