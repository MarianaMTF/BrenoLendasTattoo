package com.projecttattoo.BrenoLendaTattoo.dto.orcamento;

import org.springframework.web.multipart.MultipartFile;

public record RequestOrcamentoDto(MultipartFile imagem, Double largura, Double altura, String parteCorpo, String descricao, Integer produtoId, Integer artistaId) {
	
}
