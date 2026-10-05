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
@DynamicInsert
public class Partida {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	// Integer i no int: mentre l'objecte no s'ha guardat, id és null (i no 0)
	private Integer id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 15, unique = false)
	private EstatPartida estat;
	
	
	@Column(name = "data_inici", nullable = false, unique = false)
	private Date dataInici;
	
	@Min(value = 0, message = "La nota de Metacritic ha d'estar entre 0 i 100")
	@Max(value = 100, message = "La nota de Metacritic ha d'estar entre 0 i 100")
	@Column(name = "ronda" )
	private Integer  ronda;
	
	
	@Column(name = "ultima_tirada", nullable = true)
	private Integer  ultimaTirada;
	
	@Min(value = 3)
	@Max(value = 100)
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
	public Partida(Integer id, @NotNull EstatPartida estat, Date dataInici, Integer ronda, Integer ultimaTirada) {
		super();
		this.id = id;
		this.estat = estat;
		this.dataInici = dataInici;
		this.ronda = ronda;
		this.ultimaTirada = ultimaTirada;
		this.puntsPerGuanyar = 10;
		this.dausTirats = false;
		this.lladrePendent = false;
	}
	

	
	
	
}

