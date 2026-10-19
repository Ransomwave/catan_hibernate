package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.example.dao.jugador.JugadorDao;
import org.example.dao.partida.PartidaDao;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.TipusRecurs;
import org.hibernate.cfg.Configuration;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final PartidaDao partidaDao = new PartidaDao();
    private static final JugadorDao jugadorDao = new JugadorDao();

    private static Integer idPartida = null;

    public static void main(String[] args) {

        int opcio = -1;

        while (opcio != 0) {
            System.out.println("\n===== PROVES DAO =====");
            System.out.println("Partida actual: " + idPartida);
            System.out.println("1. Crear partida");
            System.out.println("2. Mostrar recursos");
            System.out.println("3. Construir carretera");
            System.out.println("4. Construir poblat");
            System.out.println("5. Millorar a ciutat");
            System.out.println("6. Comerciar amb la banca");
            System.out.println("7. Calcular punts");
            System.out.println("8. Passar torn");
            System.out.println("9. Moure lladre");
            System.out.println("10. Comprar carta");
            System.out.println("11. Jugar carta");
            System.out.println("12. Mostrar vèrtexs disponibles");
            System.out.println("0. Sortir");
            System.out.print("Escull una opció: ");

            try {
                opcio = Integer.parseInt(sc.nextLine());

                switch (opcio) {
                    case 1:
                        crearPartida();
                        break;
                    case 2:
                        mostrarRecursos();
                        break;
                    case 3:
                        construirCarretera();
                        break;
                    case 4:
                        construirPoblat();
                        break;
                    case 5:
                        millorarACiutat();
                        break;
                    case 6:
                        comerciarBanca();
                        break;
                    case 7:
                        calcularPunts();
                        break;
                    case 8:
                        passarTorn();
                        break;
                    case 9:
                        moureLladre();
                        break;
                    case 10:
                        comprarCarta();
                        break;
                    case 11:
                        jugarCarta();
                        break;
                    case 12:
                        vertexsDisponibles();
                        break;
                    case 0:
                        System.out.println("Sortint...");
                        break;
                    default:
                        System.out.println("Opció incorrecta.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Has d'introduir un número vàlid.");
            } catch (UnsupportedOperationException e) {
                System.out.println(
                    "Aquesta funció encara no està implementada."
                );
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }

    private static int llegirEnter(String missatge) {
        System.out.print(missatge);
        return Integer.parseInt(sc.nextLine());
    }

    private static void comprovarPartida() {
        if (idPartida == null) {
            throw new IllegalStateException(
                "Primer has de crear una partida."
            );
        }
    }

    private static void crearPartida() {
        List<String> noms = Arrays.asList(
            "Xavi", "Anna", "Marc", "Laia"
        );

        Integer nouId = partidaDao.iniciarPartida(noms);

        if (nouId == null) {
            System.out.println("No s'ha pogut crear la partida.");
            return;
        }

        idPartida = nouId;

        System.out.println(
            "Partida creada correctament. ID: " + idPartida
        );
        System.out.println(
            "Jugadors: " + noms
        );
    }

    private static void mostrarRecursos() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        jugadorDao.mostrarRecursos(idJugador);
    }

    private static void construirCarretera() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idAresta = llegirEnter("ID de l'aresta: ");

        jugadorDao.construirCarretera(idJugador, idAresta);
        System.out.println("Carretera construïda.");
    }

    private static void construirPoblat() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idVertex = llegirEnter("ID del vèrtex: ");

        jugadorDao.construirPoblat(idJugador, idVertex);
        System.out.println("Poblat construït.");
    }

    private static void millorarACiutat() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idVertex = llegirEnter("ID del vèrtex del poblat: ");

        jugadorDao.millorarACiutat(idJugador, idVertex);
        System.out.println("Ciutat construïda.");
    }

    private static void comerciarBanca() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        TipusRecurs dona = llegirRecurs("Recurs que dones:");
        TipusRecurs rep = llegirRecurs("Recurs que reps:");

        jugadorDao.comerciarBanca(idJugador, dona, rep);
        System.out.println("Intercanvi realitzat.");
    }

    private static TipusRecurs llegirRecurs(String missatge) {
        System.out.println(missatge);

        TipusRecurs[] recursos = TipusRecurs.values();

        for (int i = 0; i < recursos.length; i++) {
            System.out.println((i + 1) + ". " + recursos[i]);
        }

        int opcio = llegirEnter("Escull el recurs: ");

        if (opcio < 1 || opcio > recursos.length) {
            throw new IllegalArgumentException("Recurs incorrecte.");
        }

        return recursos[opcio - 1];
    }

    private static void calcularPunts() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        int punts = jugadorDao.calcularPunts(idJugador);
        System.out.println("Punts: " + punts);
    }

    private static void passarTorn() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador actiu: ");

        jugadorDao.passarTorn(idJugador);
        System.out.println("Torn passat.");
    }

    private static void moureLladre() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador actiu: ");
        int idHexagon = llegirEnter("ID del nou hexàgon: ");

        System.out.print(
            "ID de la víctima (Enter si no n'hi ha): "
        );

        String entrada = sc.nextLine();
        Integer idVictima = null;

        if (!entrada.trim().isEmpty()) {
            idVictima = Integer.parseInt(entrada);
        }

        jugadorDao.moureLladre(
            idJugador, idHexagon, idVictima
        );

        System.out.println("Lladre mogut.");
    }

    private static void comprarCarta() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        jugadorDao.comprarCarta(idJugador);
        System.out.println("Carta comprada.");
    }

    private static void jugarCarta() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idCarta = llegirEnter("ID de la carta: ");

        System.out.print(
            "Tipus de recurs per al monopoli "
            + "(Enter si no cal): "
        );

        String entrada = sc.nextLine();
        TipusRecurs recurs = null;

        if (!entrada.trim().isEmpty()) {
            recurs = TipusRecurs.valueOf(
                entrada.trim().toUpperCase()
            );
        }

        jugadorDao.jugarCarta(idJugador, idCarta, recurs);
        System.out.println("Carta jugada.");
    }

    private static void vertexsDisponibles() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        List<Vertex> vertexs =
            jugadorDao.vertexsDisponibles(idJugador);

        if (vertexs == null || vertexs.isEmpty()) {
            System.out.println("No hi ha vèrtexs disponibles.");
            return;
        }

        for (Vertex vertex : vertexs) {
            System.out.println(
                "ID: " + vertex.getId()
                + " | Codi: " + vertex.getCodi()
            );
        }
    }
}
