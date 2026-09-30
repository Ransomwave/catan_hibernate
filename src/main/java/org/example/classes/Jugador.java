package org.example.classes;

import java.util.Set;

import org.example.enums.ColorJugador;
import org.example.enums.TipusRecurs;

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
    @Column(nullable = false, unique = false)
    String nom;

    @Column(nullable = false, unique = false)
    ColorJugador color;

    @Column(nullable = false, unique = false)
    Integer orderTorn;

    @Column(nullable = false, unique = false)
    Integer cavallersJugats;

    @Column(unique = false)
    Set<RecursJugador> inventari;

    public Jugador(@NotBlank String nom, ColorJugador color, Integer orderTorn, Integer cavallersJugats) {
        this.nom = nom;
        this.color = color;
        this.orderTorn = orderTorn;
        this.cavallersJugats = cavallersJugats;
        this.inventari = Set.of(
                new RecursJugador(0, TipusRecurs.FUSTA),
                new RecursJugador(0, TipusRecurs.MAO),
                new RecursJugador(0, TipusRecurs.LLANA),
                new RecursJugador(0, TipusRecurs.BLAT),
                new RecursJugador(0, TipusRecurs.MINERAL));

    }

    public Jugador() {
    }

}
