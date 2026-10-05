package org.example.classes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagonvertex")
public class HexagonVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hexagon_id", nullable = false)
    @NotNull
    private Hexagon hexagon;

    @ManyToOne
    @JoinColumn(name = "vertex_id", nullable = false)
    @NotNull
    private Vertex vertex;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    @Max(5)
    private Integer posicio;

    public HexagonVertex() {
    }

    public HexagonVertex(Hexagon hexagon, Vertex vertex, Integer posicio) {
        this.hexagon = hexagon;
        this.vertex = vertex;
        this.posicio = posicio;
    }
}