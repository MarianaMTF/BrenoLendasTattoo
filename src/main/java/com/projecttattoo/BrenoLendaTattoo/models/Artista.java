package com.projecttattoo.BrenoLendaTattoo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String sobrenome;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(unique = true)
    private String email;

    private String senha;

    private String estilo;

    private String telefone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}

