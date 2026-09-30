package org.example.classes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "recurs_jugador")
public class RecursJugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    @Column(nullable = false)
    String nom;

    @Column(nullable = false)
    Integer quantitat;

    public RecursJugador(@NotBlank String nom, Integer quantitat) {
        this.nom = nom;
        this.quantitat = quantitat;
    }

    public RecursJugador() {
    }
}