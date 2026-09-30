package org.example.classes;

import org.example.enums.ColorJugador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity()
@Table(name = "jugador")
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

    public Jugador(@NotBlank String nom, ColorJugador color, Integer orderTorn, Integer cavallersJugats) {
        this.nom = nom;
        this.color = color;
        this.orderTorn = orderTorn;
        this.cavallersJugats = cavallersJugats;
    }

    public Jugador() {
    }

}
