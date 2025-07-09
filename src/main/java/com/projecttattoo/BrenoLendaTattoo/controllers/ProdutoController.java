package com.projecttattoo.BrenoLendaTattoo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.projecttattoo.BrenoLendaTattoo.dto.artista.ResponseArtistaDTO;
import com.projecttattoo.BrenoLendaTattoo.dto.produto.RequestProdutoDto;
import com.projecttattoo.BrenoLendaTattoo.dto.produto.ResponseProdutoDto;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Produto;
import com.projecttattoo.BrenoLendaTattoo.repositories.ArtistaRepository;
import com.projecttattoo.BrenoLendaTattoo.services.ArtistaService;
import com.projecttattoo.BrenoLendaTattoo.services.ProdutoService;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/produto")
public class ProdutoController {
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ArtistaService artistaService;
	
	@Autowired
	private ArtistaRepository artistaRepository;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin-novo-produto")
	public String addNovoProduto(Model model) {
		model.addAttribute("produto", new ResponseProdutoDto(null, null, null, null, null, null, null, null, null));
		return "admin_novo_produto";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public String register(
			@RequestParam("imagem") MultipartFile imagem,
			@RequestParam("nome") String nome,
			@RequestParam("artista") int idArtista,
			@RequestParam("estilo") String estilo,
	        @RequestParam("descricao") String descricao,
	        @RequestParam("valor") Double valor,
	        Model model
			){
		Artista artista = artistaRepository.findById(idArtista);
		RequestProdutoDto requestProdutoDto = new RequestProdutoDto(imagem, nome, artista, estilo, null, null, descricao, valor);
		ResponseEntity<ResponseProdutoDto> response = produtoService.register(requestProdutoDto);
		
		if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("message", "Produto salvo com sucesso!");
        } else {
            model.addAttribute("error", "Erro ao salvar o produto.");
        }
        return "redirect:/produto/admin-catalogo";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin-catalogo")
	public String listarProdutosAdmin(Model model) {
		ResponseEntity<List<ResponseProdutoDto>> response = produtoService.getAll();
		ResponseEntity<List<ResponseArtistaDTO>> responseArtista = artistaService.listarTodos();
		
		if(response.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("produtos", response.getBody());	
			//model.addAttribute("produto", new RequestProdutoDto(null, null, null, null, null, null, null, null));
		}
		
		if(responseArtista.getStatusCode().is2xxSuccessful()) {
	        model.addAttribute("artistas", responseArtista.getBody());
	    }
		
		return "artista/catalogo";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/catalogo")
	public String listarProdutos(Model model){
		ResponseEntity<List<ResponseProdutoDto>> responseProduto = produtoService.getAll();
		if(responseProduto.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("produtos", responseProduto.getBody());			
		}
		
		ResponseEntity<List<ResponseArtistaDTO>> responseArtista = artistaService.listarTodos();
		if(responseArtista.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("artistas", responseArtista.getBody());
		}
		return "cliente/catalogo";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}/editar-produto")
	public String atualizarProduto(@PathVariable Integer id, Model model) {
		ResponseEntity<ResponseProdutoDto> response = produtoService.getById(id);
		if(response.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("produto", response.getBody());
			return "admin_atualizar_produto";
		}
		
		return "redirect:/produto/catalogo";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{id}/atualizar")
	public String update(
	        @PathVariable Integer id,
	        @RequestParam(value = "imagem", required = false) MultipartFile imagem,
	        @RequestParam("nome") String nome,
	        @RequestParam("artista") int idArtista,
	        @RequestParam("estilo") String estilo,
	        @RequestParam("descricao") String descricao,
	        @RequestParam("valor") Double valor,
	        Model model) {

	    Artista artista = artistaRepository.findById(idArtista);
	    RequestProdutoDto body = new RequestProdutoDto(imagem, nome, artista, estilo, null, null, descricao, valor);
	    
	    ResponseEntity<ResponseProdutoDto> response = produtoService.update(id, body);
	    
	    if (response.getStatusCode().is2xxSuccessful()) {
	        model.addAttribute("message", "Produto atualizado com sucesso!");
	    } else {
	        model.addAttribute("error", "Erro ao atualizar o produto.");
	    }
	    return "redirect:/produto/admin-catalogo";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{id}/deletar")
	public String delete(@PathVariable("id") Integer id, Model model){
		ResponseEntity<String> response = produtoService.delete(id);
		if(response.getStatusCode().is2xxSuccessful()) {
			model.addAttribute("produto", "Produto excluido com sucesso!");
		} 
		
		return "redirect:/produto/admin-catalogo";
	}
}
