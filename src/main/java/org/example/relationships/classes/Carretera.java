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
@DiscriminatorValue("carretera")
public class Carretera extends Construccio {

	@Column(name = "aresta", unique = true)
	private Aresta aresta;

}
