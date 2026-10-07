package org.example.relationships.classes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.relationships.enums.ColorJugador;
import org.example.relationships.enums.TipusRecurs;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL)
    Set<RecursJugador> inventari = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    Partida partida;

    @OneToMany(mappedBy = "propietari")
    List<CartaDesenvolupament> ma;

    public Jugador(@NotBlank String nom, ColorJugador color, Integer orderTorn, Integer cavallersJugats) {
        this.nom = nom;
        this.color = color;
        this.orderTorn = orderTorn;
        this.cavallersJugats = cavallersJugats;

        // initialize the inventory with all resource types and a quantity of 0
        for (var tipus : TipusRecurs.values()) {
            inventari.add(new RecursJugador(0, tipus, this));
        }
    }

    public Jugador() {
    }

}
