package org.example.relationships.classes;

import org.example.relationships.enums.TipusRecurs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "recurs_jugador")
public class RecursJugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Min(0)
    @Column(nullable = false, unique = false)
    Integer quantitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    TipusRecurs tipus;

    @ManyToOne
    @JoinColumn(nullable = false, unique = false)
    Jugador jugador;

    public RecursJugador(Integer quantitat, TipusRecurs tipus) {
        this.quantitat = quantitat;
        this.tipus = tipus;
    }

    public RecursJugador() {
    }
}