package org.example.dao.jugador;

import java.util.List;

import org.example.dao.GenericDao;
import org.example.relationships.classes.Jugador;
import org.example.relationships.classes.RecursJugador;
import org.example.relationships.classes.Vertex;
import org.example.relationships.enums.TipusCarta;
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
		throw new UnsupportedOperationException("Not implemented yet");
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
	  private int calcularPunts(Session session, Jugador jugador) {
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

        Jugador exercit = exercitMesGranEnSessio(
                session, jugador.getPartida().getId());

        if (exercit != null && exercit.getId().equals(jugador.getId())) {
            punts += 2;
        }

        return punts;
    }
	

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

private int calcularPuntsEnSessio(Session session, Jugador jugador) {
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

    Jugador exercit = exercitMesGranEnSessio(
            session, jugador.getPartida().getId());

    if (exercit != null
            && exercit.getId().equals(jugador.getId())) {
        punts += 2;
    }

    return punts;
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
