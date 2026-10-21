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
import java.util.Random;

import org.example.dao.GameRulesHelper;
import org.example.dao.GenericDao;
import org.example.dao.jugador.JugadorDao;
import org.example.relationships.classes.Aresta;
import org.example.relationships.classes.Assentament;
import org.example.relationships.classes.CartaDesenvolupament;
import org.example.relationships.classes.Ciutat;
import org.example.relationships.classes.Hexagon;
import org.example.relationships.classes.HexagonVertex;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.ColorJugador;
import org.example.relationships.enums.EstatPartida;
import org.example.relationships.enums.TipusCarta;
import org.example.relationships.enums.TipusRecurs;
import org.example.relationships.enums.TipusTerreny;
import org.hibernate.Session;

public class PartidaDao extends GenericDao<Partida, Integer> implements IPartidaDao {

    final private Random random = new Random();

    public Integer iniciarPartida(List<String> playerNames) {
        var session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            if (playerNames.size() < 2 || playerNames.size() > 4)
                throw new IllegalArgumentException("There must be between 2 and 4 players");

            // create partida
            var partida = new Partida(EstatPartida.PREPARACIO, 1, 10);
            session.persist(partida); // persisting makes the object managed, Hibernate will take care of it for us

            // shuffle names and instantiate players with random turn order
            var names = new ArrayList<>(playerNames);
            Collections.shuffle(names);
            for (var i = 0; i < names.size(); i++) {
                var player = new Jugador(names.get(i), ColorJugador.values()[i], i + 1, 0, partida);
                session.persist(player); // also saves its 5 RecursJugador (cascade)
                if (i == 0)
                    partida.setActiu(player); // set the first player as the active one
            }

            loadBoard(session, partida);

            // create the development card pile
            // we don't need to collect the objects because Hibernate associates them with
            // the game automatically, as the pile field is "mappedBy"
            for (var i = 0; i < 14; i++)
                session.persist(new CartaDesenvolupament(TipusCarta.CAVALLER, false, null, partida));
            for (var i = 0; i < 5; i++)
                session.persist(new CartaDesenvolupament(TipusCarta.PUNT_VICTORIA, false, null, partida));
            for (var i = 0; i < 2; i++)
                session.persist(new CartaDesenvolupament(TipusCarta.MONOPOLI, false, null, partida));

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

    @Override

    public void mostrarTauler(int idPartida) {
    Session session = sessionFactory.getCurrentSession();

    try {
        session.beginTransaction();

        Partida partida = session.get(Partida.class, idPartida);

        if (partida == null) {
            throw new RuntimeException("No existeix aquesta partida");
        }

        List<Hexagon> hexagons = session.createQuery(
                "from Hexagon h where h.partida.id = :id " +
                "order by h.codi",
                Hexagon.class)
                .setParameter("id", idPartida)
                .getResultList();

        System.out.println("=== TAULER DE LA PARTIDA "
                + idPartida + " ===");

        for (Hexagon hexagon : hexagons) {
            System.out.println();
            System.out.println("Hexàgon " + hexagon.getCodi());
            System.out.println("Terreny: " + hexagon.getTerreny());
            System.out.println("Número: "
                    + (hexagon.getNumero() == null
                    ? "Sense número" : hexagon.getNumero()));

            boolean hiHaLladre = partida.getLladre() != null
                    && partida.getLladre().getId()
                    .equals(hexagon.getId());

            System.out.println("Lladre: "
                    + (hiHaLladre ? "Sí" : "No"));

            List<HexagonVertex> vertexs = session.createQuery(
                    "from HexagonVertex hv " +
                    "where hv.hexagon.id = :id order by hv.posicio",
                    HexagonVertex.class)
                    .setParameter("id", hexagon.getId())
                    .getResultList();

            for (HexagonVertex hv : vertexs) {
                Vertex vertex = hv.getVertex();

                Assentament assentament = session.createQuery(
                        "from Assentament a where a.vertex.id = :id",
                        Assentament.class)
                        .setParameter("id", vertex.getId())
                        .uniqueResult();

                if (assentament != null) {
                    System.out.println(
                            "  Vèrtex " + vertex.getCodi()
                            + ": "
                            + assentament.getClass().getSimpleName()
                            + " de "
                            + assentament.getPropietari().getNom());
                }
            }
        }

        session.getTransaction().commit();

    } catch (RuntimeException e) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
        throw e;
    }
}


    @Override

    public void mostrarPartida(int idPartida) {
    Session session = sessionFactory.getCurrentSession();

    try {
        session.beginTransaction();

        Partida partida = session.get(Partida.class, idPartida);

        if (partida == null) {
            throw new RuntimeException("No existeix aquesta partida");
        }

        System.out.println("=== PARTIDA " + partida.getId() + " ===");
        System.out.println("Estat: " + partida.getEstat());
        System.out.println("Ronda: " + partida.getRonda());
        System.out.println("Jugador actiu: "
                + (partida.getActiu() == null
                ? "Cap" : partida.getActiu().getNom()));
        System.out.println("Última tirada: "
                + (partida.getUltimaTirada() == null
                ? "Sense tirada" : partida.getUltimaTirada()));

        List<Jugador> jugadors = session.createQuery(
                "from Jugador j where j.partida.id = :id " +
                "order by j.ordreTorn",
                Jugador.class)
                .setParameter("id", idPartida)
                .getResultList();

        for (Jugador jugador : jugadors) {
            Long poblats = session.createQuery(
                    "select count(p) from Poblat p " +
                    "where p.propietari.id = :id", Long.class)
                    .setParameter("id", jugador.getId())
                    .getSingleResult();

            Long ciutats = session.createQuery(
                    "select count(c) from Ciutat c " +
                    "where c.propietari.id = :id", Long.class)
                    .setParameter("id", jugador.getId())
                    .getSingleResult();

            Long carreteres = session.createQuery(
                    "select count(c) from Carretera c " +
                    "where c.propietari.id = :id", Long.class)
                    .setParameter("id", jugador.getId())
                    .getSingleResult();

            Long recursos = session.createQuery(
                    "select coalesce(sum(r.quantitat), 0) " +
                    "from RecursJugador r where r.jugador.id = :id",
                    Long.class)
                    .setParameter("id", jugador.getId())
                    .getSingleResult();

            int punts = JugadorDao.calcularPuntsEnSessio(session, jugador);

            System.out.println();
            System.out.println("Jugador: " + jugador.getNom());
            System.out.println("Punts: " + punts);
            System.out.println("Poblats: " + poblats);
            System.out.println("Ciutats: " + ciutats);
            System.out.println("Carreteres: " + carreteres);
            System.out.println("Recursos totals: " + recursos);
        }

        session.getTransaction().commit();

    } catch (RuntimeException e) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
        throw e;
    }
}


    @Override
    public String tirarDaus(int idPartida) {
        var session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();

            var partida = session.get(Partida.class, idPartida);
            if (partida == null)
                throw new IllegalArgumentException("Partida not found with id: " + idPartida);

            if (partida.getEstat() != EstatPartida.EN_CURS)
                throw new IllegalStateException("The dice can only be rolled while the game is in progress");
            if (partida.isDausTirats())
                throw new IllegalStateException("The dice have already been rolled this turn");

            var die1 = random.nextInt(1, 7);
            var die2 = random.nextInt(1, 7);
            var roll = die1 + die2;

            partida.setUltimaTirada(roll);
            partida.setDausTirats(true);

            var summary = new StringBuilder("Roll: " + die1 + " + " + die2 + " = " + roll + "\n");

            if (roll == 7) {
                // each player with more than 7 resources must discard half of them (rounded
                // down)
                summary.append("No production, the robber must be moved\n");
                for (var jugador : partida.getJugadors()) {
                    var total = GameRulesHelper.totalResources(jugador);
                    if (total <= 7)
                        continue;

                    // put one entry per resource card in a list and shuffle it, so the
                    // discarded cards are random
                    var cards = new ArrayList<TipusRecurs>();
                    for (var recurs : jugador.getInventari()) {
                        for (var i = 0; i < recurs.getQuantitat(); i++)
                            cards.add(recurs.getTipus());
                    }
                    Collections.shuffle(cards);

                    var toDiscard = total / 2;
                    for (var i = 0; i < toDiscard; i++)
                        GameRulesHelper.addResource(jugador, cards.get(i), -1);

                    summary.append(jugador.getNom() + " discards " + toDiscard + " resources\n");
                }
                partida.setLladrePendent(true);
            } else {
                // if it's not 7, each hexagon with that number produces resources for the
                // players with settlements or cities on its vertices, unless the robber is on
                // that hexagon
                var rows = session.createQuery("""
                        select hv.hexagon.terreny, a
                        from HexagonVertex hv
                        join Assentament a on a.vertex = hv.vertex
                        where hv.hexagon.partida = :partida
                          and hv.hexagon.numero = :roll
                          and hv.hexagon <> :lladre
                        """, Object[].class)
                        .setParameter("partida", partida)
                        .setParameter("roll", roll)
                        .setParameter("lladre", partida.getLladre())
                        .getResultList();

                if (rows.isEmpty())
                    summary.append("Nobody gets anything\n");

                // for each settlement or city on a hexagon with the rolled number, give the
                // owner the corresponding resource (1 for settlement, 2 for city)
                for (var row : rows) {
                    var terreny = (TipusTerreny) row[0];
                    var assentament = (Assentament) row[1];
                    var jugador = assentament.getPropietari();

                    var amount = assentament instanceof Ciutat ? 2 : 1;

                    GameRulesHelper.addResource(jugador, terreny.getResource(), amount);
                    summary.append(jugador.getNom() + " gets " + amount + " " + terreny.getResource() + "\n");
                }
            }

            session.getTransaction().commit();
            return summary.toString();
        } catch (RuntimeException e) {
            handleException(session, e);
            return e.getMessage();
        }
    }

    @Override
    public void comprovarVictoria(int idPartida) {
        // TODO: ALL THIS SHOULD DO IS CALL comprovarVictoria(Session, Partida) AND WRAP
        // IT IN A TRANSACTION!!!
        throw new UnsupportedOperationException("Unimplemented method 'comprovarVictoria'");
    }

    public static void comprovarVictoria(Session session, Partida partida) {
        List<Jugador> jugadors = session.createQuery(
                "from Jugador j where j.partida.id = :id",
                Jugador.class)
                .setParameter("id", partida.getId())
                .getResultList();

        for (Jugador jugador : jugadors) {
            int punts = JugadorDao.calcularPuntsEnSessio(session, jugador);

            if (punts >= partida.getPuntsPerGuanyar()) {
                partida.setGuanyador(jugador);
                partida.setEstat(EstatPartida.FINALITZADA);
                return;
            }
        }
    }


public Jugador exercitMesGran(int idPartida) {
    Session session = sessionFactory.getCurrentSession();

    try {
        session.beginTransaction();

        Jugador jugador = exercitMesGranEnSessio(session, idPartida);

        session.getTransaction().commit();
        return jugador;

    } catch (RuntimeException e) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
        throw e;
    }
}

public static Jugador exercitMesGranEnSessio(
        Session session, int idPartida) {

    List<Jugador> jugadors = session.createQuery(
            "from Jugador j where j.partida.id = :id " +
            "order by j.cavallersJugats desc",
            Jugador.class)
            .setParameter("id", idPartida)
            .getResultList();

    if (jugadors.isEmpty()) {
        return null;
    }

    Jugador primer = jugadors.get(0);

    if (primer.getCavallersJugats() < 3) {
        return null;
    }

    if (jugadors.size() > 1) {
        Jugador segon = jugadors.get(1);

        if (primer.getCavallersJugats()
                .equals(segon.getCavallersJugats())) {
            return null;
        }
    }

    return primer;
}

}
