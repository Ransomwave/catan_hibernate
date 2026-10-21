package org.example.dao.jugador;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.dao.GameRulesHelper;
import org.example.dao.GenericDao;
import org.example.dao.partida.PartidaDao;
import org.example.relationships.classes.Aresta;
import org.example.relationships.classes.Carretera;
import org.example.relationships.classes.CartaDesenvolupament;
import org.example.relationships.classes.Ciutat;
import org.example.relationships.classes.Hexagon;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.Partida;
import org.example.relationships.classes.Poblat;
import org.example.relationships.classes.RecursJugador;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.EstatPartida;
import org.example.relationships.enums.TipusCarta;
import org.example.relationships.enums.TipusRecurs;
import org.hibernate.Session;

public class JugadorDao extends GenericDao<Jugador, Integer> implements IJugadorDao {

	private final Random random = new Random();

	private <E> E buscarPerId(Session session, Class<E> classe, int id) {
		return session.createQuery("FROM " + classe.getName() + " e WHERE e.id = :id", classe)
				.setParameter("id", id)
				.uniqueResult();
	}

	@Override
	public void mostrarRecursos(int idJugador) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			String text = mostrarRecursos(session, idJugador);
			session.getTransaction().commit();
			System.out.println(text);
		} catch (RuntimeException e) {
			handleException(session, e);
			System.out.println("Error: " + e.getMessage());
		}
	}

	private String mostrarRecursos(Session session, int idJugador) {
		Jugador v = buscarPerId(session, Jugador.class, idJugador);
		if (v == null) {
			throw new IllegalArgumentException(
					"No existeix el jugador " + idJugador);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Inventari de ").append(v.getNom()).append(" (").append(v.getColor()).append(")\n");
		for (RecursJugador r : v.getInventari()) {
			sb.append("\t").append(r.getTipus()).append(": ").append(r.getQuantitat()).append("\n");
		}

		return sb.toString();
	}

	@Override
	public void construirCarretera(int idJugador, int idAresta) {
		var session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();

			var jugador = session.get(Jugador.class, idJugador);
			if (jugador == null)
				throw new IllegalArgumentException("Jugador not found with id: " + idJugador);

			GameRulesHelper.checkAction(jugador, true);
			var partida = jugador.getPartida();

			var aresta = session.get(Aresta.class, idAresta);
			if (aresta == null || !aresta.getPartida().getId().equals(partida.getId()))
				throw new IllegalArgumentException("Aresta " + idAresta + " isn't part of this game");

			var roadsOnAresta = session.createQuery(
					"select count(c) from Carretera c where c.aresta = :aresta", Long.class)
					.setParameter("aresta", aresta)
					.getSingleResult();
			if (roadsOnAresta > 0)
				throw new IllegalStateException("There's already a road on this aresta");

			var ownRoads = session.createQuery(
					"select count(c) from Carretera c where c.propietari = :jugador", Long.class)
					.setParameter("jugador", jugador)
					.getSingleResult();
			if (ownRoads >= 15)
				throw new IllegalStateException("You've already built 15 roads");

			var vertexA = aresta.getVertexA();
			var vertexB = aresta.getVertexB();

			if (partida.getEstat() == EstatPartida.PREPARACIO) {
				// the road must touch the poblat placed this turn, which is the player's
				// only poblat that doesn't have one of their roads next to it yet
				var newPoblats = session.createQuery("""
						select count(p) from Poblat p
						where p.propietari = :jugador
						  and (p.vertex = :vertexA or p.vertex = :vertexB)
						  and not exists (
						      select c from Carretera c
						      where c.propietari = :jugador
						        and (c.aresta.vertexA = p.vertex or c.aresta.vertexB = p.vertex))
						""", Long.class)
						.setParameter("jugador", jugador)
						.setParameter("vertexA", vertexA)
						.setParameter("vertexB", vertexB)
						.getSingleResult();
				if (newPoblats == 0)
					throw new IllegalStateException("The road must touch the poblat you just placed");
			} else {
				// the road must touch one of the player's poblats/ciutats or another of
				// their roads
				var ownAssentaments = session.createQuery("""
						select count(a) from Assentament a
						where a.propietari = :jugador
						  and (a.vertex = :vertexA or a.vertex = :vertexB)
						""", Long.class)
						.setParameter("jugador", jugador)
						.setParameter("vertexA", vertexA)
						.setParameter("vertexB", vertexB)
						.getSingleResult();
				var ownConnectedRoads = session.createQuery("""
						select count(c) from Carretera c
						where c.propietari = :jugador
						  and (c.aresta.vertexA = :vertexA or c.aresta.vertexB = :vertexA
						    or c.aresta.vertexA = :vertexB or c.aresta.vertexB = :vertexB)
						""", Long.class)
						.setParameter("jugador", jugador)
						.setParameter("vertexA", vertexA)
						.setParameter("vertexB", vertexB)
						.getSingleResult();
				if (ownAssentaments == 0 && ownConnectedRoads == 0)
					throw new IllegalStateException("The road must be connected to one of your buildings or roads");

				GameRulesHelper.pay(jugador, GameRulesHelper.COST_CARRETERA);
			}

			var carretera = new Carretera();
			carretera.setPropietari(jugador);
			carretera.setAresta(aresta);
			session.persist(carretera);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void construirPoblat(int idJugador, int idVertex) {
		var session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();

			var jugador = session.get(Jugador.class, idJugador);
			if (jugador == null)
				throw new IllegalArgumentException("Jugador not found with id: " + idJugador);

			GameRulesHelper.checkAction(jugador, true);
			var partida = jugador.getPartida();
			var preparacio = partida.getEstat() == EstatPartida.PREPARACIO;

			var vertex = session.get(Vertex.class, idVertex);
			if (vertex == null || !vertex.getPartida().getId().equals(partida.getId()))
				throw new IllegalArgumentException("Vertex " + idVertex + " isn't part of this game");

			var buildingsOnVertex = session.createQuery(
					"select count(a) from Assentament a where a.vertex = :vertex", Long.class)
					.setParameter("vertex", vertex)
					.getSingleResult();
			if (buildingsOnVertex > 0)
				throw new IllegalStateException("There's already a building on this vertex");

			// don't allow building if there's another building on an adjacent vertex
			// (distance rule)
			var buildingsNextTo = session.createQuery("""
					select count(a) from Assentament a, Aresta ar
					where (ar.vertexA = :vertex and a.vertex = ar.vertexB)
					   or (ar.vertexB = :vertex and a.vertex = ar.vertexA)
					""", Long.class)
					.setParameter("vertex", vertex)
					.getSingleResult();
			if (buildingsNextTo > 0)
				throw new IllegalStateException("Too close to another building (distance rule)");

			var ownPoblats = session.createQuery(
					"select count(p) from Poblat p where p.propietari = :jugador", Long.class)
					.setParameter("jugador", jugador)
					.getSingleResult();
			if (ownPoblats >= 5)
				throw new IllegalStateException("You've already built 5 poblats");

			if (preparacio) {
				if (ownPoblats >= partida.getRonda()) // the player can only place 1 poblat per turn during the
														// preparation phase
					throw new IllegalStateException("You've already placed your poblat this turn");
			} else {
				var ownRoadsHere = session.createQuery("""
						select count(c) from Carretera c
						where c.propietari = :jugador
						  and (c.aresta.vertexA = :vertex or c.aresta.vertexB = :vertex)
						""", Long.class)
						.setParameter("jugador", jugador)
						.setParameter("vertex", vertex)
						.getSingleResult();
				if (ownRoadsHere == 0)
					throw new IllegalStateException("The poblat must be connected to one of your roads");

				GameRulesHelper.pay(jugador, GameRulesHelper.COST_POBLAT);
			}

			var poblat = new Poblat();
			poblat.setPropietari(jugador);
			poblat.setVertex(vertex);
			session.persist(poblat);

			// the second poblat of the preparation phase gives 1 resource for each
			// hexagon it touches (desert gives nothing)
			if (preparacio && ownPoblats == 1) {
				var hexagons = session.createQuery(
						"select hv.hexagon from HexagonVertex hv where hv.vertex = :vertex", Hexagon.class)
						.setParameter("vertex", vertex)
						.getResultList();
				for (var hexagon : hexagons) {
					var resource = hexagon.getTerreny().getResource();
					if (resource != null)
						GameRulesHelper.addResource(jugador, resource, 1);
				}
			}

			PartidaDao.comprovarVictoria(session, partida);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void millorarACiutat(int idJugador, int idVertex) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);
			if (jugador == null) {
				throw new IllegalArgumentException(
						"No existeix el jugador.");
			}
			GameRulesHelper.checkAction(jugador, false);
			Partida partida = jugador.getPartida();
			Vertex vertex = buscarPerId(session, Vertex.class, idVertex);
			if (vertex == null) {
				throw new IllegalArgumentException(
						"No existeix el vertex.");
			}
			if (!vertex.getPartida().getId().equals(partida.getId())) {
				throw new IllegalArgumentException(
						"El vertex no pertany a aquesta partida.");
			}
			Poblat poblat = session.createQuery(
					"FROM Poblat p WHERE p.vertex.id = :idVertex",
					Poblat.class).setParameter("idVertex", idVertex)
					.uniqueResult();
			if (poblat == null) {
				throw new IllegalStateException(
						"No hi ha cap poblat en aquest vertex.");
			}
			if (!poblat.getPropietari().getId().equals(idJugador)) {
				throw new IllegalStateException(
						"El poblat no pertany a aquest jugador.");
			}
			Long nombreCiutats = session.createQuery(
					"SELECT COUNT(c) FROM Ciutat c WHERE c.propietari.id = :idJugador",
					Long.class).setParameter("idJugador", idJugador)
					.uniqueResult();
			if (nombreCiutats >= 4) {
				throw new IllegalStateException(
						"Ja tens el maxim de 4 ciutats.");
			}
			RecursJugador blat = null;
			RecursJugador mineral = null;
			for (RecursJugador recurs : jugador.getInventari()) {
				if (recurs.getTipus() == TipusRecurs.BLAT) {
					blat = recurs;
				}

				if (recurs.getTipus() == TipusRecurs.MINERAL) {
					mineral = recurs;
				}
			}
			if (blat == null || mineral == null
					|| blat.getQuantitat() < 2
					|| mineral.getQuantitat() < 3) {
				throw new IllegalStateException(
						"No tens prou recursos per construir una ciutat.");
			}
			blat.setQuantitat(blat.getQuantitat() - 2);
			mineral.setQuantitat(mineral.getQuantitat() - 3);
			Ciutat ciutat = new Ciutat();
			ciutat.setVertex(vertex);
			ciutat.setPropietari(jugador);
			session.remove(poblat);
			session.flush(); // remove it first or it won't work
			session.persist(ciutat);
			PartidaDao.comprovarVictoria(session, partida);
			session.getTransaction().commit();

		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}

			throw e;
		}
	}

	@Override
	public void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);
			if (jugador == null) {
				throw new IllegalArgumentException(
						"El jugador no existeix.");
			}
			GameRulesHelper.checkAction(jugador, false);
			if (dona == null || rep == null || dona == rep) {
				throw new IllegalArgumentException(
						"Els tipus de recursos han de ser diferents.");
			}
			RecursJugador recursDonat = null;
			RecursJugador recursRebut = null;
			for (RecursJugador recurs : jugador.getInventari()) {
				if (recurs.getTipus() == dona) {
					recursDonat = recurs;
				}

				if (recurs.getTipus() == rep) {
					recursRebut = recurs;
				}
			}
			if (recursDonat == null || recursRebut == null) {
				throw new IllegalStateException(
						"No s'han trobat els recursos necessaris.");
			}
			if (recursDonat.getQuantitat() < 4) {
				throw new IllegalStateException(
						"No tens prou recursos per fer l'intercanvi.");
			}
			recursDonat.setQuantitat(recursDonat.getQuantitat() - 4);
			recursRebut.setQuantitat(recursRebut.getQuantitat() + 1);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}

	@Override
	public int calcularPunts(int idJugador) {
		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();

			Jugador jugador = session.get(Jugador.class, idJugador);

			if (jugador == null) {
				throw new RuntimeException("No existeix aquest jugador");
			}

			int punts = calcularPuntsEnSessio(session, jugador);

			session.getTransaction().commit();
			return punts;

		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}

	public static int calcularPuntsEnSessio(Session session, Jugador jugador) {
		Long poblats = session.createQuery(
				"select count(p) from Poblat p " +
						"where p.propietari.id = :id",
				Long.class)
				.setParameter("id", jugador.getId())
				.getSingleResult();

		Long ciutats = session.createQuery(
				"select count(c) from Ciutat c " +
						"where c.propietari.id = :id",
				Long.class)
				.setParameter("id", jugador.getId())
				.getSingleResult();

		Long cartes = session.createQuery(
				"select count(c) from CartaDesenvolupament c " +
						"where c.propietari.id = :id and c.tipus = :tipus",
				Long.class)
				.setParameter("id", jugador.getId())
				.setParameter("tipus", TipusCarta.PUNT_VICTORIA)
				.getSingleResult();

		int punts = poblats.intValue()
				+ ciutats.intValue() * 2
				+ cartes.intValue();

		Jugador exercit = PartidaDao.exercitMesGranEnSessio(
				session, jugador.getPartida().getId());

		if (exercit != null
				&& exercit.getId().equals(jugador.getId())) {
			punts += 2;
		}

		return punts;
	}

	@Override
	public void passarTorn(int idJugador) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);
			if (jugador == null) {
				throw new IllegalArgumentException(
						"No existeix el jugador " + idJugador);
			}
			Partida partida = jugador.getPartida();
			if (partida.getActiu() == null
					|| !partida.getActiu().getId().equals(idJugador)) {
				throw new IllegalStateException(
						"No és el torn d'aquest jugador.");
			}
			if (partida.isLladrePendent()) {
				throw new IllegalStateException(
						"No es pot passar torn: cal moure el lladre.");
			}
			if (partida.getEstat() == EstatPartida.EN_CURS) {
				if (!partida.isDausTirats()) {
					throw new IllegalStateException(
							"Has de tirar els daus abans de passar torn.");
				}
				passarTornEnCurs(partida);
			} else if (partida.getEstat() == EstatPartida.PREPARACIO) {
				comprovarPecesPreparacio(session, jugador, partida);
				passarTornPreparacio(session, partida);
			} else {
				throw new IllegalStateException(
						"No es pot passar torn en aquest estat de partida.");
			}
			partida.setDausTirats(false);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	private void passarTornEnCurs(Partida partida) {
		List<Jugador> jugadors = partida.getJugadors();
		int ordreActual = partida.getActiu().getOrdreTorn();
		int ordreSeguent = ordreActual + 1;
		if (ordreSeguent > jugadors.size()) {
			ordreSeguent = 1;
			partida.setRonda(partida.getRonda() + 1);
		}
		for (Jugador jugador : jugadors) {
			if (jugador.getOrdreTorn() == ordreSeguent) {
				partida.setActiu(jugador);
				return;
			}
		}

		throw new IllegalStateException(
				"No s'ha trobat el jugador següent.");
	}

	private void passarTornPreparacio(Session session, Partida partida) {
		List<Jugador> jugadors = partida.getJugadors();
		int ordreActual = partida.getActiu().getOrdreTorn();
		int maxJugadors = jugadors.size();
		int ronda = partida.getRonda();
		int ordreSeguent;
		if (ronda == 1) {
			ordreSeguent = ordreActual + 1;
			if (ordreSeguent > maxJugadors) {
				partida.setRonda(2);
				ordreSeguent = maxJugadors;
			}
		} else {
			ordreSeguent = ordreActual - 1;
			if (ordreSeguent < 1) {
				for (Jugador jugador : jugadors) {
					Long poblats = session.createQuery(
							"SELECT COUNT(p) FROM Poblat p WHERE p.propietari.id = :idJugador",
							Long.class)
							.setParameter("idJugador", jugador.getId())
							.uniqueResult();
					Long carreteres = session.createQuery(
							"SELECT COUNT(c) FROM Carretera c WHERE c.propietari.id = :idJugador",
							Long.class)
							.setParameter("idJugador", jugador.getId())
							.uniqueResult();

					if (poblats < 2 || carreteres < 2) {
						throw new IllegalStateException(
								"El jugador " + jugador.getNom() + " no té dos poblats i dues carreteres.");
					}
				}
				partida.setEstat(EstatPartida.EN_CURS);
				partida.setRonda(1);
				for (Jugador jugador : jugadors) {
					if (jugador.getOrdreTorn() == 1) {
						partida.setActiu(jugador);
						return;
					}
				}

				throw new IllegalStateException(
						"No s'ha trobat el jugador d'ordre 1.");
			}
		}
		for (Jugador jugador : jugadors) {
			if (jugador.getOrdreTorn() == ordreSeguent) {
				partida.setActiu(jugador);
				return;
			}
		}

		throw new IllegalStateException(
				"No s'ha trobat el jugador següent.");
	}

	private void comprovarPecesPreparacio(Session session, Jugador jugador, Partida partida) {
		int ronda = partida.getRonda();
		Long poblats = session.createQuery(
				"SELECT COUNT(p) FROM Poblat p WHERE p.propietari.id = :idJugador",
				Long.class)
				.setParameter("idJugador", jugador.getId())
				.uniqueResult();
		Long carreteres = session.createQuery(
				"SELECT COUNT(c) FROM Carretera c WHERE c.propietari.id = :idJugador",
				Long.class)
				.setParameter("idJugador", jugador.getId())
				.uniqueResult();
		if (poblats < ronda || carreteres < ronda) {
			if (ronda == 1) {
				throw new IllegalStateException(
						"Has de tenir " + ronda + " poblat i " + ronda + " carretera abans de passar torn.");
			} else {
				throw new IllegalStateException(
						"Has de tenir " + ronda + " poblats i " + ronda + " carreteres abans de passar torn.");
			}
		}
	}

	@Override
	public void moureLladre(int idJugador, int idHexagon, Integer idJugadorVictima) {
		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();

			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);

			if (jugador == null) {
				throw new IllegalArgumentException("El jugador no existeix.");
			}

			Partida partida = jugador.getPartida();

			if (partida.getActiu() == null
					|| !partida.getActiu().getId().equals(idJugador)) {
				throw new IllegalStateException("No és el torn d'aquest jugador.");
			}

			if (!partida.isLladrePendent()) {
				throw new IllegalStateException("No hi ha cap moviment del lladre pendent.");
			}

			Hexagon hexagonNou = session.createQuery(
					"FROM Hexagon h WHERE h.id = :idHexagon "
							+ "AND h.partida.id = :idPartida",
					Hexagon.class)
					.setParameter("idHexagon", idHexagon)
					.setParameter("idPartida", partida.getId())
					.uniqueResult();

			if (hexagonNou == null) {
				throw new IllegalArgumentException(
						"L'hexàgon no existeix en aquesta partida.");
			}
			Hexagon hexagonActual = partida.getLladre();
			if (hexagonActual != null
					&& hexagonActual.getId().equals(hexagonNou.getId())) {
				throw new IllegalArgumentException(
						"El lladre s'ha de moure a un hexàgon diferent.");
			}
			partida.setLladre(hexagonNou);
			if (idJugadorVictima != null) {
				Jugador victima = buscarPerId(
						session, Jugador.class, idJugadorVictima);

				if (victima == null) {
					throw new IllegalArgumentException(
							"El jugador víctima no existeix.");
				}

				if (victima.getId().equals(jugador.getId())) {
					throw new IllegalArgumentException(
							"No et pots robar recursos a tu mateix.");
				}

				if (!victima.getPartida().getId().equals(partida.getId())) {
					throw new IllegalArgumentException(
							"La víctima no pertany a aquesta partida.");
				}
				Long assentaments = session.createQuery(
						"SELECT COUNT(a) FROM Assentament a "
								+ "WHERE a.propietari.id = :idVictima "
								+ "AND a.vertex.id IN "
								+ "(SELECT hv.vertex.id FROM HexagonVertex hv "
								+ "WHERE hv.hexagon.id = :idHexagon)",
						Long.class)
						.setParameter("idVictima", idJugadorVictima)
						.setParameter("idHexagon", idHexagon)
						.getSingleResult();

				if (assentaments == 0) {
					throw new IllegalArgumentException(
							"La víctima no té cap assentament en aquest hexàgon.");
				}

				List<RecursJugador> recursosDisponibles = new ArrayList<>();

				for (RecursJugador recurs : victima.getInventari()) {
					if (recurs.getQuantitat() > 0) {
						recursosDisponibles.add(recurs);
					}
				}
				if (!recursosDisponibles.isEmpty()) {
					int index = random.nextInt(
							recursosDisponibles.size());

					RecursJugador recursRobat = recursosDisponibles.get(index);
					TipusRecurs tipus = recursRobat.getTipus();

					RecursJugador recursJugador = null;

					for (RecursJugador recurs : jugador.getInventari()) {
						if (recurs.getTipus() == tipus) {
							recursJugador = recurs;
							break;
						}
					}

					if (recursJugador == null) {
						throw new IllegalStateException(
								"El jugador no té registrat aquest tipus de recurs.");
					}

					recursRobat.setQuantitat(recursRobat.getQuantitat() - 1);
					recursJugador.setQuantitat(recursJugador.getQuantitat() + 1);
				}
			}
			partida.setLladrePendent(false);

			session.getTransaction().commit();

		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			throw e;
		}
	}

	@Override
	public void comprarCarta(int idJugador) {
		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();
			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);
			if (jugador == null) {
				throw new IllegalArgumentException(
						"No existeix el jugador.");
			}
			GameRulesHelper.checkAction(jugador, false);
			Partida partida = jugador.getPartida();
			var pila = new ArrayList<CartaDesenvolupament>();
			for (var c : partida.getPila()) {
				if (c.getPropietari() == null) {
					pila.add(c);
				}
			}
			if (pila.isEmpty()) {
				throw new IllegalStateException(
						"La pila de cartes de desenvolupament és buida.");
			}
			RecursJugador llana = null;
			RecursJugador blat = null;
			RecursJugador mineral = null;
			for (RecursJugador recurs : jugador.getInventari()) {
				if (recurs.getTipus() == TipusRecurs.LLANA) {
					llana = recurs;
				}

				if (recurs.getTipus() == TipusRecurs.BLAT) {
					blat = recurs;
				}

				if (recurs.getTipus() == TipusRecurs.MINERAL) {
					mineral = recurs;
				}
			}
			if (llana == null || blat == null || mineral == null || llana.getQuantitat() < 1 || blat.getQuantitat() < 1
					|| mineral.getQuantitat() < 1) {
				throw new IllegalStateException(
						"No tens prou recursos per comprar una carta.");
			}

			llana.setQuantitat(llana.getQuantitat() - 1);
			blat.setQuantitat(blat.getQuantitat() - 1);
			mineral.setQuantitat(mineral.getQuantitat() - 1);
			int index = random.nextInt(pila.size());
			CartaDesenvolupament carta = pila.get(index);
			carta.setPropietari(jugador);
			carta.setRondaCompra(partida.getRonda());
			PartidaDao.comprovarVictoria(session, partida);
			session.getTransaction().commit();

		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}

			throw e;
		}
	}

	@Override
	public void jugarCarta(int idJugador, int idCarta, TipusRecurs recurs) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			Jugador jugador = buscarPerId(session, Jugador.class, idJugador);
			if (jugador == null) {
				throw new IllegalArgumentException(
						"No existeix el jugador.");
			}
			GameRulesHelper.checkAction(jugador, false);
			Partida partida = jugador.getPartida();
			CartaDesenvolupament carta = buscarPerId(
					session, CartaDesenvolupament.class, idCarta);
			if (carta == null) {
				throw new IllegalArgumentException(
						"No existeix la carta.");
			}
			if (carta.getPropietari() == null
					|| !carta.getPropietari().getId().equals(idJugador)) {
				throw new IllegalStateException(
						"Aquesta carta no pertany al jugador.");
			}
			if (carta.getUsada()) {
				throw new IllegalStateException(
						"Aquesta carta ja s'ha utilitzat.");
			}
			if (carta.getRondaCompra() == null
					|| carta.getRondaCompra() >= partida.getRonda()) {
				throw new IllegalStateException(
						"No pots jugar una carta durant la mateixa ronda en què l'has comprat.");
			}
			if (carta.getTipus() == TipusCarta.PUNT_VICTORIA) {
				throw new IllegalStateException(
						"Les cartes de punt de victòria es compten directament a CalcularPunts.");
			}
			if (carta.getTipus() == TipusCarta.MONOPOLI && recurs == null) {
				throw new IllegalArgumentException(
						"Has d'indicar el recurs del monopoli.");
			}
			if (carta.getTipus() == TipusCarta.CAVALLER) {
				jugador.setCavallersJugats(
						jugador.getCavallersJugats() + 1);
				partida.setLladrePendent(true);
				PartidaDao.comprovarVictoria(session, partida);
			} else if (carta.getTipus() == TipusCarta.MONOPOLI) {
				RecursJugador recursJugador = null;
				for (RecursJugador r : jugador.getInventari()) {
					if (r.getTipus() == recurs) {
						recursJugador = r;
						break;
					}
				}
				if (recursJugador == null) {
					throw new IllegalStateException(
							"No s'ha trobat el recurs a l'inventari del jugador.");
				}
				for (Jugador altreJugador : partida.getJugadors()) {

					if (altreJugador.getId().equals(idJugador)) {
						continue;
					}

					for (RecursJugador r : altreJugador.getInventari()) {

						if (r.getTipus() == recurs) {
							recursJugador.setQuantitat(
									recursJugador.getQuantitat() + r.getQuantitat());

							r.setQuantitat(0);
							break;
						}
					}
				}
			}
			carta.setUsada(true);
			session.getTransaction().commit();

		} catch (RuntimeException e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}

			throw e;
		}
	}

	@Override
	public List<Vertex> vertexsDisponibles(int idJugador) {
		var session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();

			var jugador = session.get(Jugador.class, idJugador);
			if (jugador == null)
				throw new IllegalArgumentException("Jugador not found with id: " + idJugador);

			var partida = jugador.getPartida();
			var preparacio = partida.getEstat() == EstatPartida.PREPARACIO;

			// all vertexs that don't have a building on them and don't have a building
			// on an adjacent vertex
			var jpql = """
					select v from Vertex v
					where v.partida = :partida
					  and not exists (select a from Assentament a where a.vertex = v)
					  and not exists (
					      select a from Assentament a, Aresta ar
					      where (ar.vertexA = v and a.vertex = ar.vertexB)
					         or (ar.vertexB = v and a.vertex = ar.vertexA))
					""";

			// if we're not in the preparation phase, only allow vertexs that are connected
			// to one of the player's roads
			if (!preparacio) {
				jpql += """
						  and exists (
						      select c from Carretera c
						      where c.propietari = :jugador
						        and (c.aresta.vertexA = v or c.aresta.vertexB = v))
						""";
			}

			var query = session.createQuery(jpql + " order by v.codi", Vertex.class)
					.setParameter("partida", partida);
			if (!preparacio)
				query.setParameter("jugador", jugador);
			var vertices = query.getResultList();

			session.getTransaction().commit();
			return vertices;
		} catch (RuntimeException e) {
			handleException(session, e);
			return List.of();
		}
	}
}
