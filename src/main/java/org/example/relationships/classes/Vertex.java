package org.example.relationships.classes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "vertex",
       uniqueConstraints = @UniqueConstraint(columnNames = {"partida_id", "codi"}))
public class Vertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Min(0)
    @Max(53)
    @Column(nullable = false)
    private Integer codi;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;
    
    
}