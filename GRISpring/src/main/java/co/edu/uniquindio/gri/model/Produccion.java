package co.edu.uniquindio.gri.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "PRODUCCIONES")
@Table(name = "PRODUCCIONES", schema = "gri")
public class Produccion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private long id;

	@Column(name = "AUTORES", length = 2000)
	private String autores;

	@Column(name = "ANIO", length = 10)
	private String anio;

	@Column(name = "REFERENCIA", length = 4000)
	private String referencia;

	@Column(name = "REPETIDO", length = 10)
	private String repetido;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TIPO_ID")
	private Tipo tipo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "INVESTIGADORES_ID")
	private Investigador investigador;

	public Produccion(long id, String autores, String anio, String referencia, Tipo tipo, String repetido,
			 Investigador investigador) {
		this.id = id;
		this.autores = autores;
		this.anio = anio;
		this.referencia = referencia;
		this.tipo = tipo;
		this.investigador = investigador;
	}

	public Produccion() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getRepetido() {
		return repetido;
	}

	public void setRepetido(String repetido) {
		this.repetido = repetido;
	}

	public Investigador getInvestigador() {
		return investigador;
	}

	public void setInvestigador(Investigador investigador) {
		this.investigador = investigador;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

}
