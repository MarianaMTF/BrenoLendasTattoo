package com.projecttattoo.BrenoLendaTattoo.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.RequestOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.ResponseOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;
import com.projecttattoo.BrenoLendaTattoo.models.Orcamento;

public interface OrcamentoInterfaceService {
	public ResponseEntity<ResponseOrcamentoDto> register(RequestOrcamentoDto body, Cliente cliente, Artista artista);
	public ResponseEntity<ResponseOrcamentoDto> getById(Integer id);
	public ResponseEntity<List<ResponseOrcamentoDto>> getAll();
	public ResponseEntity<ResponseOrcamentoDto> update(Integer id, RequestOrcamentoDto body);
	public ResponseEntity<ResponseOrcamentoDto> updateStatus(Integer id, String newStatus);
	public ResponseEntity<String> delete(Integer id);
	public ResponseEntity<ResponseOrcamentoDto> regiterByProduto(RequestOrcamentoDto body, Integer id, Cliente cliente);
	public ResponseEntity<List<ResponseOrcamentoDto>> getOrcamentoByClienteId(Integer id);
}
