package org.example.relationships.classes;

import java.util.ArrayList;
import java.util.List;

import org.example.relationships.enums.TipusTerreny;
import org.hibernate.annotations.Check;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagon")
public class Hexagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(0) 
    @Max(18)
    private Integer codi;

    @Column(nullable = false, length = 15)
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipusTerreny terreny;

    @Column
    @Min(2)
    @Max(12)
    @Check(constraints="numero <> 7")
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
    @JoinColumn(nullable=false)
    private Partida partida;
    
    @OneToMany(mappedBy = "vertexs", cascade = CascadeType.ALL)
    private List<HexagonVertex> vertexs = new ArrayList<>(); //TODO: no tengo ni idea socio
    
    public Hexagon() {
    }

	public Hexagon(Long id, @Min(0) @Max(18) Integer codi, @NotNull TipusTerreny terreny,
			@Min(2) @Max(12) Integer numero, @Min(-2) @Max(2) int q, @Min(-2) @Max(2) int r, Partida partida,
			List<HexagonVertex> vertexs) {
		super();
		this.id = id;
		this.codi = codi;
		this.terreny = terreny;
		this.numero = numero;
		this.q = q;
		this.r = r;
		this.partida = partida;
		this.vertexs = vertexs;
	}

    
}