package com.projecttattoo.BrenoLendaTattoo.dto.produto;

import org.springframework.web.multipart.MultipartFile;

import com.projecttattoo.BrenoLendaTattoo.models.Artista;

public record ResponseProdutoDto(Integer id, String imagem, String nome, Double largura, Double altura, String descricao, Double valor, String estilo, Artista artista) {

}
