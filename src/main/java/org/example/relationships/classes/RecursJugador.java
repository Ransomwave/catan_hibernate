package org.example.relationships.classes;

import org.example.relationships.enums.TipusRecurs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recurs_jugador")
public class RecursJugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, unique = false)
    Integer quantitat;

    @Column(nullable = false, unique = true)
    TipusRecurs tipus;

    public RecursJugador(Integer quantitat, TipusRecurs tipus) {
        this.quantitat = quantitat;
        this.tipus = tipus;
    }

    public RecursJugador() {
    }
}