package org.example.relationships.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.relationships.enums.EstatPartida;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.*;
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

	@Min(1)
	@Column(name = "ronda", nullable = false, unique = false)
	private Integer ronda;
	
	@Min(2)
	@Max(12)
	@Column(name = "ultima_tirada", nullable = true, unique = false)
	private Integer ultimaTirada;

	@Min(3)
	@Max(20)
	@Column(name = "punts_per_guanyar", nullable = true, unique = false)
	private Integer puntsPerGuanyar;
	
	@NotNull
	@Column(name = "daus_tirats", nullable = true, unique = false)
	private boolean dausTirats;
	
	@NotNull
	@Column(name = "lladre_pendent", nullable = true, unique = false)
	private boolean lladrePendent;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lladre_hexagon_id")
    private Hexagon lladre;
	
	@ManyToOne()
	@JoinColumn(name = "actiu_jugador_id", nullable=true)
	private Jugador actiu;
	
	@ManyToOne()
	@JoinColumn(name = "guanyador_jugador_id", nullable=true)
	private Jugador guanyador;
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Hexagon> tauler = new ArrayList<>();
	
	@OneToMany(mappedBy = "jugadors", cascade = CascadeType.ALL)
    private List<Jugador> jugadors = new ArrayList<>(); //TODO: Mirar si es set
	
	@OneToMany(mappedBy = "arestes", cascade = CascadeType.ALL)
    private List<Jugador> arestes = new ArrayList<>(); //TODO: Mirar si es set
	
	@OneToMany(mappedBy = "pila", cascade = CascadeType.ALL)
    private List<Jugador> pila = new ArrayList<>(); //TODO: Mirar si es set
	

	public Partida() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public Partida(Integer id, @NotNull EstatPartida estat, Date dataInici, @Min(1) Integer ronda,
			@Min(2) @Max(12) Integer ultimaTirada, @Min(3) @Max(20) Integer puntsPerGuanyar,
			@NotNull boolean dausTirats, @NotNull boolean lladrePendent, Hexagon lladre, Jugador actiu,
			Jugador guanyador, List<Hexagon> tauler, List<Jugador> jugadors, List<Jugador> arestes,
			List<Jugador> pila) {
		super();
		this.id = id;
		this.estat = estat;
		this.dataInici = dataInici;
		this.ronda = ronda;
		this.ultimaTirada = ultimaTirada;
		this.puntsPerGuanyar = puntsPerGuanyar;
		this.dausTirats = dausTirats;
		this.lladrePendent = lladrePendent;
		this.lladre = lladre;
		this.actiu = actiu;
		this.guanyador = guanyador;
		this.tauler = tauler;
		this.jugadors = jugadors;
		this.arestes = arestes;
		this.pila = pila;
	}



	public Partida(Integer id, @NotNull EstatPartida estat, Date dataInici, Integer ronda, Integer ultimaTirada, Hexagon lladre, Jugador actiu,
			Jugador guanyador, List<Hexagon> tauler, List<Jugador> jugadors, List<Jugador> arestes,
			List<Jugador> pila) {
		super();
		this.id = id;
		this.estat = estat;
		this.dataInici = dataInici;
		this.ronda = ronda;
		this.ultimaTirada = ultimaTirada;
		this.puntsPerGuanyar = 10;
		this.dausTirats = false;
		this.lladrePendent = false;
		this.lladre = lladre;
		this.actiu = actiu;
		this.guanyador = guanyador;
		this.tauler = tauler;
		this.jugadors = jugadors;
		this.arestes = arestes;
		this.pila = pila;
	}

}
