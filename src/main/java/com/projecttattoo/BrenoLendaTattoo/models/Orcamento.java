package com.projecttattoo.BrenoLendaTattoo.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "orcamentos")
public class Orcamento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String imagem;
	
	@Column(nullable = false)
	private Double largura;
	
	@Column(nullable = false)
	private Double altura;
	
	@Column(name = "parte_corpo", nullable = false)
	private String parteCorpo;
	
	@Column(nullable = false)
	private String descricao;
	
	@Column(name = "valor")
	private Double valor;
	
	@Column(name = "status_orcamento", nullable = false)
	private String statusOrcamento;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@OneToOne(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Agendamento agendamento;
	
	@ManyToOne
	@JoinColumn(name = "id_artista", nullable = false)
	private Artista artista;

}