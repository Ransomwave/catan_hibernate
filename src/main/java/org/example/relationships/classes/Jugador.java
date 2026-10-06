package org.example.relationships.classes;

import java.util.Set;

import org.example.relationships.enums.ColorJugador;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity()
@Table(name = "jugador")
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    @Column(nullable = false, unique = false, length = 50)
    String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = false)
    ColorJugador color;

    @Min(1)
    @Max(4)
    @Column(nullable = false, unique = false)
    Integer orderTorn;

    @Min(0)
    @Max(14)
    @Column(nullable = false, unique = false)
    Integer cavallersJugats;

    @ManyToOne
    @JoinColumn(nullable = false, unique = false)
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
