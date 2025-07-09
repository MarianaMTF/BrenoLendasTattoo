package com.projecttattoo.BrenoLendaTattoo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projecttattoo.BrenoLendaTattoo.dto.produto.RequestProdutoDto;
import com.projecttattoo.BrenoLendaTattoo.dto.produto.ResponseProdutoDto;
import com.projecttattoo.BrenoLendaTattoo.interfaces.ProdutoInterfaceService;
import com.projecttattoo.BrenoLendaTattoo.models.Produto;
import com.projecttattoo.BrenoLendaTattoo.repositories.ProdutoRepository;

@Service
public class ProdutoService implements ProdutoInterfaceService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Override
	public ResponseEntity<ResponseProdutoDto> register(RequestProdutoDto body) {
		try {
			Produto newProduto = new Produto();
			
			String imagem = settarImagem(body.imagem());
			newProduto.setImagem(imagem);
			newProduto.setNome(body.nome());
			newProduto.setArtista(body.artista());
			newProduto.setEstilo(body.estilo());
			newProduto.setLargura(body.largura());
			newProduto.setAltura(body.altura());
			newProduto.setDescricao(body.descricao());
			newProduto.setValor(body.valor());
			
			System.out.println(newProduto.getLargura());
			
			produtoRepository.save(newProduto);
			
			ResponseProdutoDto produtoDto = new ResponseProdutoDto(newProduto.getId(), newProduto.getImagem(),
					newProduto.getNome(), newProduto.getLargura(), newProduto.getAltura(), newProduto.getDescricao(),
					newProduto.getValor(), newProduto.getEstilo(), newProduto.getArtista());
			
			return ResponseEntity.ok(produtoDto);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
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
	public ResponseEntity<List<ResponseProdutoDto>> getAll() {
		List<Produto> produtos = produtoRepository.findAll();

		if (!produtos.isEmpty()) {
			List<ResponseProdutoDto> produtosDtos = produtos.stream()
					.map(produto -> new ResponseProdutoDto(produto.getId(), produto.getImagem(), produto.getNome(),
							produto.getLargura(), produto.getAltura(), produto.getDescricao(), produto.getValor(), produto.getEstilo(), produto.getArtista()))
					.toList();

			return ResponseEntity.ok(produtosDtos);
		}

		return ResponseEntity.badRequest().build();
	}

	@Override
	public ResponseEntity<ResponseProdutoDto> getById(Integer id) {
		Optional<Produto> produtoOpt = produtoRepository.findById(id);

		if (produtoOpt.isPresent()) {
			Produto produto = produtoOpt.get();
			ResponseProdutoDto produtoDto = new ResponseProdutoDto(produto.getId(), produto.getImagem(),
					produto.getNome(), produto.getLargura(), produto.getAltura(), produto.getDescricao(),
					produto.getValor(), produto.getEstilo(), produto.getArtista());

			return ResponseEntity.ok(produtoDto);
		}

		return ResponseEntity.badRequest().build();
	}

	@Override
	public ResponseEntity<ResponseProdutoDto> update(Integer id, RequestProdutoDto body) {
	    Optional<Produto> produtoOpt = produtoRepository.findById(id);
	    
	    if (produtoOpt.isPresent()) {
	        Produto produto = produtoOpt.get();
	        
	        produto.setNome(body.nome());
	        produto.setDescricao(body.descricao());
	        produto.setValor(body.valor());
	        produto.setEstilo(body.estilo());
	        produto.setArtista(body.artista());
	        
	        // Atualiza a imagem apenas se uma nova foi enviada
	        if (body.imagem() != null && !body.imagem().isEmpty()) {
	            try {
	                String novaImagem = settarImagem(body.imagem());
	                produto.setImagem(novaImagem);
	            } catch (IOException e) {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	            }
	        }
	        
	        produtoRepository.save(produto);
	        
	        ResponseProdutoDto produtoDto = new ResponseProdutoDto(
	            produto.getId(),
	            produto.getImagem(),
	            produto.getNome(),
	            produto.getLargura(),
	            produto.getAltura(),
	            produto.getDescricao(),
	            produto.getValor(),
	            produto.getEstilo(),
	            produto.getArtista()
	        );
	        
	        return ResponseEntity.ok(produtoDto);
	    }
	    
	    return ResponseEntity.notFound().build();
	}

	@Override
	public ResponseEntity<String> delete(Integer id) {
		if (produtoRepository.existsById(id)) {
			produtoRepository.deleteById(id);

			return ResponseEntity.ok().body("Produto deletado com sucesso!");
		}

		return ResponseEntity.badRequest().body("Não foi possivel deletar o produto!");
	}

}
