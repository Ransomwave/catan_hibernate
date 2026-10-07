package org.example.relationships.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("carretera")
public class Carretera extends Construccio {

	@OneToOne
	@JoinColumn(name = "aresta_id", unique = true)
	private Aresta aresta;

}
