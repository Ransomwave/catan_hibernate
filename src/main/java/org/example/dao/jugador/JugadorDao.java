package org.example.dao.jugador;

import java.util.List;

import org.example.dao.GameRulesHelper;
import org.example.dao.GenericDao;
import org.example.relationships.classes.Aresta;
import org.example.relationships.classes.Carretera;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.RecursJugador;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.EstatPartida;
import org.example.relationships.enums.TipusRecurs;
import org.hibernate.Session;

public class JugadorDao extends GenericDao<Jugador, Integer> implements IJugadorDao {
	private <E> E buscarPerId(Session session, Class<E> classe, int id) {
		return session.createQuery("FROM " + classe.getName() + " e WHERE e.id = :id", classe)
				.setParameter("id", id)
				.uniqueResult();
	}

	@Override
	public String mostrarRecursos(int idJugador) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			String text = mostrarRecursos(session, idJugador);
			session.getTransaction().commit();
			return text;
		} catch (RuntimeException e) {
			handleException(session, e);
			return "Error base de datos";
		}
	}

	private String mostrarRecursos(Session session, int idJugador) {
		Jugador v = buscarPerId(session, Jugador.class, idJugador);
		if (v == null) {
			return "No existeix el jugador " + idJugador;
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
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void millorarACiutat(int idJugador, int idVertex) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public int calcularPunts(int idJugador) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void passarTorn(int idJugador) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void moureLladre(int idJugador, int idHexagon, Integer idJugadorVictima) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void comprarCarta(int idJugador) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void jugarCarta(int idJugador, int idCarta, TipusRecurs recurs) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public List<Vertex> vertexsDisponibles(int idJugador) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
