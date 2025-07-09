package com.projecttattoo.BrenoLendaTattoo.services;

import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.RequestOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.ResponseOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.interfaces.OrcamentoInterfaceService;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;
import com.projecttattoo.BrenoLendaTattoo.models.Orcamento;
import com.projecttattoo.BrenoLendaTattoo.models.Produto;
import com.projecttattoo.BrenoLendaTattoo.repositories.OrcamentoRespository;
import com.projecttattoo.BrenoLendaTattoo.repositories.ProdutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrcamentoService implements OrcamentoInterfaceService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private OrcamentoRespository orcamentoRespository;

	@Override
	public ResponseEntity<ResponseOrcamentoDto> register(RequestOrcamentoDto body, Cliente cliente, Artista artista) {
		try {
			Orcamento newOrcamento = new Orcamento();

			String imagem = settarImagem(body.imagem());
			newOrcamento.setImagem(imagem);
			newOrcamento.setAltura(body.altura());
			newOrcamento.setLargura(body.largura());
			newOrcamento.setDescricao(body.descricao());
			newOrcamento.setParteCorpo(body.parteCorpo());
			newOrcamento.setValor(calculateValue(newOrcamento));
			newOrcamento.setStatusOrcamento("Em análise!");
			newOrcamento.setAgendamento(null);
			newOrcamento.setCliente(cliente);
			newOrcamento.setArtista(artista);

			orcamentoRespository.save(newOrcamento);

			ResponseOrcamentoDto orcamentoDto = new ResponseOrcamentoDto(newOrcamento.getId(), newOrcamento.getImagem(),
					newOrcamento.getAltura(), newOrcamento.getLargura(), newOrcamento.getDescricao(),
					newOrcamento.getParteCorpo(), newOrcamento.getValor(), newOrcamento.getStatusOrcamento(),
					newOrcamento.getAgendamento(), newOrcamento.getCliente(), newOrcamento.getArtista());

			return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoDto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	private Double calculateValue(Orcamento orcamento) {
		final double valorPorCm = 2;
		double altura = orcamento.getAltura(), largura = orcamento.getLargura();

		Map<String, Double> fatorPorParteCorpo = Map.of("Braço", 0.3, "Costela", 0.25, "Antebraco", 0.4, "Perna", 0.55,
				"Costas", 0.5, "Peito", 0.9, "Pescoço", 0.1, "Pé", 0.4, "Outro", 0.5);

		double fatorLocalizacao = fatorPorParteCorpo.getOrDefault(orcamento.getParteCorpo(), 1.0);
		double calculo = altura * largura * valorPorCm * fatorLocalizacao;
		BigDecimal bd = new BigDecimal(calculo).setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	private String settarImagem(MultipartFile imagem) throws IOException {
		if (imagem.isEmpty()) {
			throw new IOException("Arquivo vazio!");
		}

		Path uploadPath = Paths.get("src/main/resources/static/uploads/");
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		String nomeArquivo = System.currentTimeMillis() + "_" + imagem.getOriginalFilename();
		Path filePath = uploadPath.resolve(nomeArquivo);
		Files.copy(imagem.getInputStream(), filePath);

		return "/uploads/" + nomeArquivo;
	}

	@Override
	public ResponseEntity<ResponseOrcamentoDto> getById(Integer id) {
		Optional<Orcamento> orcamentoOpt = orcamentoRespository.findById(id);

		if (orcamentoOpt.isPresent()) {
			Orcamento orcamento = orcamentoOpt.get();
			ResponseOrcamentoDto orcamentoDto = new ResponseOrcamentoDto(orcamento.getId(), orcamento.getImagem(),
					orcamento.getAltura(), orcamento.getLargura(), orcamento.getDescricao(), orcamento.getParteCorpo(),
					orcamento.getValor(), orcamento.getStatusOrcamento(), orcamento.getAgendamento(), orcamento.getCliente(), orcamento.getArtista());

			return ResponseEntity.ok(orcamentoDto);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	@Override
	public ResponseEntity<List<ResponseOrcamentoDto>> getAll() {
		List<Orcamento> orcamentos = orcamentoRespository.findAll();

		if (!orcamentos.isEmpty()) {
			List<ResponseOrcamentoDto> orcamentosDto = orcamentos.stream()
					.map(orcamento -> new ResponseOrcamentoDto(orcamento.getId(), orcamento.getImagem(),
							orcamento.getAltura(), orcamento.getLargura(), orcamento.getDescricao(), orcamento.getParteCorpo(),
							orcamento.getValor(), orcamento.getStatusOrcamento(), orcamento.getAgendamento(), orcamento.getCliente(), orcamento.getArtista()))
					.toList();

			return ResponseEntity.ok(orcamentosDto);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	@Override
	public ResponseEntity<ResponseOrcamentoDto> update(Integer id, RequestOrcamentoDto body) {
		Optional<Orcamento> orcamentoOpt = orcamentoRespository.findById(id);

		if (orcamentoOpt.isPresent()) {
			System.out.println("Esse orcamento é do: " + orcamentoOpt.get().getCliente().getNomeCompleto());
			Orcamento orcamento = orcamentoOpt.get();

			if (body.imagem() != null && !body.imagem().isEmpty()) {
				try {
					String imagemPath = settarImagem(body.imagem());
					orcamento.setImagem(imagemPath);
				} catch (IOException e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				}
			}

			orcamento.setAltura(body.altura());
			orcamento.setLargura(body.largura());
			orcamento.setDescricao(body.descricao());
			orcamento.setParteCorpo(body.parteCorpo());
			orcamento.setValor(calculateValue(orcamento));
			orcamento.setStatusOrcamento("Em análise!");

			orcamentoRespository.save(orcamento);

			System.out.println("Salvo no BD");

			ResponseOrcamentoDto orcamentoDto = new ResponseOrcamentoDto(orcamento.getId(), orcamento.getImagem(),
					orcamento.getAltura(), orcamento.getLargura(), orcamento.getDescricao(), orcamento.getParteCorpo(),
					orcamento.getValor(), orcamento.getStatusOrcamento(), orcamento.getAgendamento(), orcamento.getCliente(), orcamento.getArtista());

			System.out.println("Retornando o card com os dados atuaizados");

			return ResponseEntity.ok(orcamentoDto);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	@Override
	public ResponseEntity<String> delete(Integer id) {
		if (orcamentoRespository.existsById(id)) {
			orcamentoRespository.deleteById(id);
			return ResponseEntity.ok("Orçamento deletado com sucesso!");
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orçamento não encontrado.");
	}

	@Override
	public ResponseEntity<ResponseOrcamentoDto> updateStatus(Integer id, String newStatus) {
		Optional<Orcamento> orcamentoOpt = orcamentoRespository.findById(id);

		if (orcamentoOpt.isPresent()) {
			Orcamento orcamento = orcamentoOpt.get();

			orcamento.setStatusOrcamento(newStatus);
			orcamentoRespository.save(orcamento);

			ResponseOrcamentoDto orcamentoDto = new ResponseOrcamentoDto(orcamento.getId(), orcamento.getImagem(),
					orcamento.getAltura(), orcamento.getLargura(), orcamento.getDescricao(), orcamento.getParteCorpo(),
					orcamento.getValor(), orcamento.getStatusOrcamento(), orcamento.getAgendamento(), orcamento.getCliente(), orcamento.getArtista());

			return ResponseEntity.ok(orcamentoDto);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	@Override
	public ResponseEntity<ResponseOrcamentoDto> regiterByProduto(RequestOrcamentoDto body, Integer produtoId, Cliente cliente) {
		try {
			Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
			if (produtoOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			Produto produto = produtoOpt.get();
			Orcamento newOrcamento = new Orcamento();

			newOrcamento.setImagem(produto.getImagem());
			newOrcamento.setAltura(produto.getAltura());
			newOrcamento.setLargura(produto.getLargura());
			newOrcamento.setDescricao(produto.getDescricao());
			newOrcamento.setValor(produto.getValor());
			newOrcamento.setParteCorpo(body.parteCorpo());
			newOrcamento.setStatusOrcamento("Em análise!");
			newOrcamento.setAgendamento(null);
			newOrcamento.setCliente(cliente);

			orcamentoRespository.save(newOrcamento);

			ResponseOrcamentoDto orcamentoDto = new ResponseOrcamentoDto(newOrcamento.getId(), newOrcamento.getImagem(),
					newOrcamento.getAltura(), newOrcamento.getLargura(), newOrcamento.getDescricao(),
					newOrcamento.getParteCorpo(), newOrcamento.getValor(), newOrcamento.getStatusOrcamento(),
					newOrcamento.getAgendamento(), newOrcamento.getCliente(), newOrcamento.getArtista());

			return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoDto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<List<ResponseOrcamentoDto>> getOrcamentoByClienteId(Integer id) {
		List<Orcamento> orcamentos = orcamentoRespository.findByClienteId(id);
		List<ResponseOrcamentoDto> orcamentosDto = orcamentos.stream().map(this::mapToResponseOrcamentoDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(orcamentosDto);
	}

	private ResponseOrcamentoDto mapToResponseOrcamentoDto(Orcamento orcamento) {
		return new ResponseOrcamentoDto(orcamento.getId(), orcamento.getImagem(), orcamento.getAltura(),
				orcamento.getLargura(), orcamento.getDescricao(), orcamento.getParteCorpo(), orcamento.getValor(),
				orcamento.getStatusOrcamento(), orcamento.getAgendamento(), orcamento.getCliente(), orcamento.getArtista());

	}
}