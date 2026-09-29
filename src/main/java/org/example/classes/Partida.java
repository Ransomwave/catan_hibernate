package org.example.classes;

import java.util.Date;

import org.example.enums.EstatPartida;
import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "partida")
// @DynamicInsert: l'INSERT només inclou les columnes que no són null.
// Així, si valoracio és null, MySQL hi posa el DEFAULT (5.0).
@DynamicInsert
public class Partida {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	// Integer i no int: mentre l'objecte no s'ha guardat, id és null (i no 0)
	private Integer id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 10)
	private EstatPartida estat;
	
	
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	
	
	@Column(name = "ronda")
	private Integer  ronda;
	
	
	@Column(name = "ultima_tirada")
	private Integer  ultimaTirada;
	
	
	@Column(name = "punts_per_guanyar")
	private Integer  puntsPerGuanyar;
	
	@Column(name = "daus_tirats", nullable = false)
	private boolean dausTirats;
	
	@Column(name = "lladre_pendent", nullable = false)
	private boolean lladrePendent;

	public Partida() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Partida(Integer id, @NotNull EstatPartida estat, Date dataInici, Integer ronda, Integer ultimaTirada,
			Integer puntsPerGuanyar, boolean dausTirats, boolean lladrePendent) {
		super();
		this.id = id;
		this.estat = estat;
		this.dataInici = dataInici;
		this.ronda = ronda;
		this.ultimaTirada = ultimaTirada;
		this.puntsPerGuanyar = puntsPerGuanyar;
		this.dausTirats = dausTirats;
		this.lladrePendent = lladrePendent;
	}
	
	
}

