package com.projecttattoo.BrenoLendaTattoo.dto.orcamento;

import com.projecttattoo.BrenoLendaTattoo.models.Agendamento;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;

public record ResponseOrcamentoDto(Integer id, String imagem, Double altura, Double largura, String descricao, String parteCorpo, Double valor, String statusOrcamento, Agendamento agendamento, Cliente cliente, Artista artista) {

}

// tentar fazer encontrar o id do orcamento para adicionar um agendamento