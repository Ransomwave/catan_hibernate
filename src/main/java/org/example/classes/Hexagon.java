package org.example.classes;

import org.example.enums.TipusTerreny;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagon")
public class Hexagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Integer codi;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipusTerreny terreny;

    @Column
    @Min(2)
    @Max(12)
    private Integer numero;

    @Column(nullable = false)
    private int q;

    @Column(nullable = false)
    private int r;

    public Hexagon() {
    }

    public Hexagon(Integer codi, TipusTerreny terreny,
            Integer numero, int q, int r) {

        this.codi = codi;
        this.terreny = terreny;
        this.numero = numero;
        this.q = q;
        this.r = r;
    }
}