package org.example.relationships.classes;

import java.util.ArrayList;
import java.util.List;

import org.example.relationships.enums.TipusTerreny;
import org.hibernate.annotations.Check;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagon", uniqueConstraints = @UniqueConstraint(columnNames = { "partida_id", "codi" }))
public class Hexagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    @Max(19)
    private Integer codi;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipusTerreny terreny;

    @Min(2)
    @Max(12)
    @Check(constraints = "numero <> 7")
    private Integer numero;

    @Column(nullable = false)
    @Min(-2)
    @Max(2)
    private int q;

    @Column(nullable = false)
    @Min(-2)
    @Max(2)
    private int r;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Partida partida;

    @OneToMany(mappedBy = "hexagon", cascade = CascadeType.ALL)
    private List<HexagonVertex> vertexs = new ArrayList<>();

    public Hexagon() {
    }

    public Hexagon(Integer codi, TipusTerreny terreny, Integer numero, int q, int r, Partida partida) {
        this.codi = codi;
        this.terreny = terreny;
        this.numero = numero;
        this.q = q;
        this.r = r;
        this.partida = partida;
    }

}