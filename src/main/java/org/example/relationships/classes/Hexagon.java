package org.example.relationships.classes;

import org.example.relationships.enums.TipusTerreny;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagon",
       uniqueConstraints = @UniqueConstraint(columnNames = {"partida_id", "codi"}))
public class Hexagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer codi;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipusTerreny terreny;

    @Min(2)
    @Max(12)
    private Integer numero;

    @Column(nullable = false)
    private int q;

    @Column(nullable = false)
    private int r;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    public Hexagon() {
    }

    public Hexagon(Integer codi, TipusTerreny terreny,
            Integer numero, int q, int r, Partida partida) {

        this.codi = codi;
        this.terreny = terreny;
        this.numero = numero;
        this.q = q;
        this.r = r;
        this.partida = partida;
    }
}