package com.projecttattoo.BrenoLendaTattoo.controllers;

import java.security.Principal;
import java.util.List;

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

import com.projecttattoo.BrenoLendaTattoo.dto.ResponseDto;
import com.projecttattoo.BrenoLendaTattoo.dto.cliente.ClienteDto;
import com.projecttattoo.BrenoLendaTattoo.dto.cliente.RequestRegisterDto;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;
import com.projecttattoo.BrenoLendaTattoo.repositories.ClienteRepository;
import com.projecttattoo.BrenoLendaTattoo.services.ClienteService;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	@GetMapping("/cadastro")
	public String exibirTeladCadastro(Model model) {
		model.addAttribute("cliente", new RequestRegisterDto(null, null, null, null, null));
		return "cliente/cadastro";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute("cliente") RequestRegisterDto request, Model model) {
		System.out.println("Cheguei aqui");
		
	    ResponseEntity<ResponseDto> response = clienteService.register(request);
	    
	    
	    
	    if (response.getStatusCode().is2xxSuccessful()) {
	        model.addAttribute("message", "Cliente salvo com sucesso!");
	    } else {
	        model.addAttribute("error", "Erro ao salvar o cliente.");
	    }

	    return "redirect:/auth/login";
	}
	
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/minha-conta")
	public String getById(Model model, Principal principal){
		String email = principal.getName();
		Cliente cliente = clienteRepository.findByEmail(email);
		
		model.addAttribute("cliente", cliente);
		
		return "cliente/minha_conta";
	}
	
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PostMapping("/{id}/atualizar-conta")
	public String update(@PathVariable("id") Integer id, @ModelAttribute RequestRegisterDto body, Model model){
		ResponseEntity<RequestRegisterDto> response = clienteService.update(id, body);
		
		System.out.println("Cheguei aqui no controller");
		
		if (response.getStatusCode().is2xxSuccessful()) {
			System.out.println("Consegui atualizar!");
			model.addAttribute("Consegui", "Atualizou!");
		} else {
			model.addAttribute("Erro", "Não consegui atualizar");
		}
		
		return "redirect:/cliente/minha-conta";
	}
	
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PostMapping("/{id}/deletar")
	public String delete(@PathVariable("id") Integer id, Model model){
		ResponseEntity<Integer> response = clienteService.delete(id);
		
		if (response.getStatusCode().is2xxSuccessful()) {
			System.out.println("Consegui atualizar!");
			model.addAttribute("Consegui", "Deletei!");
		} else {
			model.addAttribute("Erro", "Não consegui Deletar");
		}
		
		return "redirect:/home";
	}
}
