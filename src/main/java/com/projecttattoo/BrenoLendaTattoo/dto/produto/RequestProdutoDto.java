package com.projecttattoo.BrenoLendaTattoo.dto.produto;

import org.springframework.web.multipart.MultipartFile;

import com.projecttattoo.BrenoLendaTattoo.models.Artista;

public record RequestProdutoDto(MultipartFile imagem, String nome, Artista artista, String estilo, Double largura, Double altura, String descricao, Double valor) {

}
