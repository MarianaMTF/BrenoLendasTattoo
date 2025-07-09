package com.projecttattoo.BrenoLendaTattoo.models;

import org.hibernate.annotations.ManyToAny;

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
@Table(name = "produto")
public class Produto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, name = "imagem")
	private String imagem;
	
	@Column(nullable = false, name = "nome")
	private String nome;
	
	@Column(nullable = true, name = "largura")
	private Double largura;
	
	@Column(nullable = true, name = "altura")
	private Double altura;
	
	private String descricao;
	
	@Column(nullable = false, name = "valor")
	private Double valor;
	
	@Column(nullable = true, name = "estilo")
	private String estilo;
	
	@ManyToOne
	@JoinColumn(name = "id_artista")
	private Artista artista;
}
