package org.example.relationships.classes;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aresta")
@DynamicInsert
public class Aresta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "codi")
	private Integer codi;

	@ManyToOne(optional = false)
	@JoinColumn(name = "partida_id", nullable = false)
	private Partida partida;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vertex_a_id", nullable = false)
	private Vertex vertexA;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vertex_b_id", nullable = false)
	private Vertex vertexB;

	public Aresta(Integer codi, Partida partida, Vertex vertexA, Vertex vertexB) {
		this.codi = codi;
		this.partida = partida;
		this.vertexA = vertexA;
		this.vertexB = vertexB;
	}

}
