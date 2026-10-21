package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.example.dao.jugador.JugadorDao;
import org.example.dao.partida.PartidaDao;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.TipusRecurs;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final PartidaDao partidaDao = new PartidaDao();
    private static final JugadorDao jugadorDao = new JugadorDao();

    private static Integer idPartida = null;

    public static void main(String[] args) {

        int opcio = -1;

        while (opcio != 0) {
            IO.println("\n===== PROVES DAO =====");
            IO.println("Partida actual: " + idPartida);
            IO.println("1. Crear partida");
            IO.println("2. Mostrar recursos");
            IO.println("3. Construir carretera");
            IO.println("4. Construir poblat");
            IO.println("5. Millorar a ciutat");
            IO.println("6. Comerciar amb la banca");
            IO.println("7. Calcular punts");
            IO.println("8. Passar torn");
            IO.println("9. Moure lladre");
            IO.println("10. Comprar carta");
            IO.println("11. Jugar carta");
            IO.println("12. Mostrar vèrtexs disponibles");
            IO.println("13. Tirar daus");
            IO.println("14. Mostrar tauler");
            IO.println("15. Mostrar partida");
            IO.println("16. Comprovar victòria");
            IO.println("17. Exèrcit més gran");
            IO.println("0. Sortir");
            IO.print("Escull una opció: ");

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
                    case 13:
                        tirarDaus();
                        break;
                    case 14:
                        mostrarTauler();
                        break;
                    case 15:
                        mostrarPartida();
                        break;
                    case 16:
                        comprovarVictoria();
                        break;
                    case 17:
                        exercitMesGran();
                        break;
                    case 0:
                        IO.println("Sortint...");
                        break;
                    default:
                        IO.println("Opció incorrecta.");
                }

            } catch (NumberFormatException e) {
                IO.println("Has d'introduir un número vàlid.");
            } catch (UnsupportedOperationException e) {
                IO.println(
                        "Aquesta funció encara no està implementada.");
            } catch (RuntimeException e) {
                IO.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }

    private static int llegirEnter(String missatge) {
        IO.print(missatge);
        return Integer.parseInt(sc.nextLine());
    }

    private static void comprovarPartida() {
        if (idPartida == null) {
            throw new IllegalStateException(
                    "Primer has de crear una partida.");
        }
    }

    private static void crearPartida() {
        List<String> noms = Arrays.asList(
                "Xavi", "Anna", "Marc", "Laia");

        Integer nouId = partidaDao.iniciarPartida(noms);

        if (nouId == null) {
            IO.println("No s'ha pogut crear la partida.");
            return;
        }

        idPartida = nouId;

        IO.println(
                "Partida creada correctament. ID: " + idPartida);
        IO.println(
                "Jugadors: " + noms);
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
        IO.println("Carretera construïda.");
    }

    private static void construirPoblat() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idVertex = llegirEnter("ID del vèrtex: ");

        jugadorDao.construirPoblat(idJugador, idVertex);
        IO.println("Poblat construït.");
    }

    private static void millorarACiutat() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idVertex = llegirEnter("ID del vèrtex del poblat: ");

        jugadorDao.millorarACiutat(idJugador, idVertex);
        IO.println("Ciutat construïda.");
    }

    private static void comerciarBanca() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        TipusRecurs dona = llegirRecurs("Recurs que dones:");
        TipusRecurs rep = llegirRecurs("Recurs que reps:");

        jugadorDao.comerciarBanca(idJugador, dona, rep);
        IO.println("Intercanvi realitzat.");
    }

    private static TipusRecurs llegirRecurs(String missatge) {
        IO.println(missatge);

        TipusRecurs[] recursos = TipusRecurs.values();

        for (int i = 0; i < recursos.length; i++) {
            IO.println((i + 1) + ". " + recursos[i]);
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
        IO.println("Punts: " + punts);
    }

    private static void passarTorn() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador actiu: ");

        jugadorDao.passarTorn(idJugador);
        IO.println("Torn passat.");
    }

    private static void moureLladre() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador actiu: ");
        int idHexagon = llegirEnter("ID del nou hexàgon: ");

        IO.print(
                "ID de la víctima (Enter si no n'hi ha): ");

        String entrada = sc.nextLine();
        Integer idVictima = null;

        if (!entrada.trim().isEmpty()) {
            idVictima = Integer.parseInt(entrada);
        }

        jugadorDao.moureLladre(
                idJugador, idHexagon, idVictima);

        IO.println("Lladre mogut.");
    }

    private static void comprarCarta() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        jugadorDao.comprarCarta(idJugador);
        IO.println("Carta comprada.");
    }

    private static void jugarCarta() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");
        int idCarta = llegirEnter("ID de la carta: ");

        IO.print(
                "Tipus de recurs per al monopoli "
                        + "(Enter si no cal): ");

        String entrada = sc.nextLine();
        TipusRecurs recurs = null;

        if (!entrada.trim().isEmpty()) {
            recurs = TipusRecurs.valueOf(
                    entrada.trim().toUpperCase());
        }

        jugadorDao.jugarCarta(idJugador, idCarta, recurs);
        IO.println("Carta jugada.");
    }

    private static void vertexsDisponibles() {
        comprovarPartida();
        int idJugador = llegirEnter("ID del jugador: ");

        List<Vertex> vertexs = jugadorDao.vertexsDisponibles(idJugador);

        if (vertexs == null || vertexs.isEmpty()) {
            IO.println("No hi ha vèrtexs disponibles.");
            return;
        }

        for (Vertex vertex : vertexs) {
            IO.println(
                    "ID: " + vertex.getId()
                            + " | Codi: " + vertex.getCodi());
        }
    }

    private static void tirarDaus() {
        comprovarPartida();

        var resum = partidaDao.tirarDaus(idPartida);
        IO.println(resum);
    }

    private static void mostrarTauler() {
        comprovarPartida();
        partidaDao.mostrarTauler(idPartida);
    }

    private static void mostrarPartida() {
        comprovarPartida();
        partidaDao.mostrarPartida(idPartida);
    }

    private static void comprovarVictoria() {
        comprovarPartida();
        partidaDao.comprovarVictoria(idPartida);

        var partida = partidaDao.get(idPartida);

        if (partida.getGuanyador() == null) {
            IO.println("Encara no ha guanyat ningú.");
        } else {
            IO.println(
                    "Ha guanyat " + partida.getGuanyador().getNom() + "!");
        }
    }

    private static void exercitMesGran() {
        comprovarPartida();

        var jugador = partidaDao.exercitMesGran(idPartida);

        if (jugador == null) {
            IO.println("Ningú no té l'exèrcit més gran.");
        } else {
            IO.println(
                    "Exèrcit més gran: " + jugador.getNom()
                            + " (" + jugador.getCavallersJugats() + " cavallers)");
        }
    }
}
