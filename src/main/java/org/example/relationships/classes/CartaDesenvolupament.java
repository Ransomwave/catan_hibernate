package org.example.relationships.classes;

import org.example.relationships.enums.TipusCarta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "carta_desenvolupament")
public class CartaDesenvolupament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, unique = false)
    TipusCarta tipus;

    @Column(nullable = false, unique = false)
    boolean usada;

    @Column(nullable = false, unique = false)
    Integer rondaCompra;

    public CartaDesenvolupament(TipusCarta tipus, boolean usada, Integer rondaCompra) {
        this.tipus = tipus;
        this.usada = usada;
        this.rondaCompra = rondaCompra;
    }

    public CartaDesenvolupament() {
    }
}
