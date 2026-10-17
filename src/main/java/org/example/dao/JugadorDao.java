package org.example.dao;

import java.util.ArrayList;
import java.util.List;

import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.RecursJugador;
import org.hibernate.Session;

public class JugadorDao extends GenericDao<Jugador, Integer> implements IJugadorDao {
	private <E> E buscarPerId(Session session, Class<E> classe, int id) {
		return session.createQuery("FROM " + classe.getName() + " e WHERE e.id = :id", classe)
				.setParameter("id", id)
				.uniqueResult();
	}
	
	public String MostrarRecursos(int idJugador) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			String text = MostrarRecursos(session, idJugador);
			session.getTransaction().commit();
			return text;
		} catch (RuntimeException e) {
			handleException(session, e);
			return "Error base de datos";
		}
	}
	
	private String MostrarRecursos(Session session, int idJugador) {
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


}
