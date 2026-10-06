package org.example.relationships.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipus", discriminatorType = DiscriminatorType.STRING, length = 10)
@DiscriminatorValue("assentament")
public abstract class Assentament extends Construccio {

	@Column(name = "vertex", unique = true)
	private Vertex vertex;

	public Assentament() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Assentament(Vertex vertex) {
		this.vertex = vertex;
	}

}
