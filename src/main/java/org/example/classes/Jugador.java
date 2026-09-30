package org.example.classes;

import org.example.enums.ColorJugador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity(name = "jugador")
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    @Column(nullable = false)
    String nom;

    @Column(nullable = false)
    ColorJugador color;

    @Column(nullable = false)
    Integer orderTorn;

    @Column(nullable = false)
    Integer cavallersJugats;

    public Jugador() {
    }

}
