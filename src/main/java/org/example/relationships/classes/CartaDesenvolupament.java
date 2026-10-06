package org.example.relationships.classes;

import org.example.relationships.enums.TipusCarta;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carta_desenvolupament")
public class CartaDesenvolupament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = false)
    TipusCarta tipus;

    @NotNull
    @Column(nullable = false, unique = false)
    boolean usada;

    @Min(1)
    @Column(nullable = true, unique = false)
    Integer rondaCompra;

    @ManyToOne
    @JoinColumn(nullable = false, unique = false)
    Partida partida;

    @ManyToOne
    @JoinColumn(nullable = true)
    Jugador propietari;

    public CartaDesenvolupament(TipusCarta tipus, boolean usada, Integer rondaCompra) {
        this.tipus = tipus;
        this.usada = usada;
        this.rondaCompra = rondaCompra;
    }

    public CartaDesenvolupament() {
    }
}
