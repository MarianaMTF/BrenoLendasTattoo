package com.projecttattoo.BrenoLendaTattoo.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projecttattoo.BrenoLendaTattoo.dto.artista.ResponseArtistaDTO;
import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.RequestOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.dto.orcamento.ResponseOrcamentoDto;
import com.projecttattoo.BrenoLendaTattoo.enums.Roles;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;
import com.projecttattoo.BrenoLendaTattoo.models.Logins;
import com.projecttattoo.BrenoLendaTattoo.models.Orcamento;
import com.projecttattoo.BrenoLendaTattoo.models.Produto;
import com.projecttattoo.BrenoLendaTattoo.repositories.ArtistaRepository;
import com.projecttattoo.BrenoLendaTattoo.repositories.ClienteRepository;
import com.projecttattoo.BrenoLendaTattoo.repositories.LoginsRepository;
import com.projecttattoo.BrenoLendaTattoo.repositories.ProdutoRepository;
import com.projecttattoo.BrenoLendaTattoo.services.ArtistaService;
import com.projecttattoo.BrenoLendaTattoo.services.OrcamentoService;


@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private OrcamentoService orcamentoService;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private LoginsRepository loginsRepository;
    
    @Autowired
    private ArtistaService artistaService;

    @Autowired
    private ArtistaRepository artistaRepository;
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/novo-orcamento")
    public String exibirFormularioNovoOrcamento(
        @RequestParam(value = "produtoId", required = false) Integer produtoId,
        Model model
    ) {
        Orcamento orcamento = new Orcamento();
        
        
        if (produtoId != null) {
            Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
            
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                
                orcamento.setImagem(produto.getImagem());
                orcamento.setAltura(produto.getAltura());
                orcamento.setLargura(produto.getLargura());
                orcamento.setDescricao(produto.getDescricao());
                orcamento.setValor(produto.getValor());
                model.addAttribute("orcamento", orcamento);
            }
        }

        model.addAttribute("orcamento", orcamento);
        
        ResponseEntity<List<ResponseArtistaDTO>> artistasResponse = artistaService.listarTodos();
        if (artistasResponse.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("artistas", artistasResponse.getBody());
        }
        return "cliente/novo_orcamento";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/novo-orcamento")
    public String exibirFormularioNovoOrcamentoPorProduto(
        @PathVariable Integer id,
        Model model
    ) {
    	Optional<Produto> produtoOpt = produtoRepository.findById(id);
        Orcamento orcamento = new Orcamento();
        
        if (produtoOpt != null) {
            
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                
                orcamento.setImagem(produto.getImagem());
                orcamento.setAltura(produto.getAltura());
                orcamento.setLargura(produto.getLargura());
                orcamento.setDescricao(produto.getDescricao());
                orcamento.setValor(produto.getValor());
                model.addAttribute("orcamento", orcamento);
            }
        }

        model.addAttribute("orcamento", orcamento);
        ResponseEntity<List<ResponseArtistaDTO>> artistasResponse = artistaService.listarTodos();
        if (artistasResponse.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("artistas", artistasResponse.getBody());
        }
        return "cliente/novo_orcamento";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/novo")
    public String criarOrcamento(
        @RequestParam("imagem") MultipartFile imagem,
        @RequestParam("largura") Double largura,
        @RequestParam("altura") Double altura,
        @RequestParam("parteCorpo") String parteCorpo,
        @RequestParam("descricao") String descricao,
        @RequestParam("artistaId") int artistaId,
        @RequestParam(value = "produtoId", required = false) Integer produtoId,
        Model model, Principal principal
    ) {
    	String email = principal.getName();
    	Cliente cliente = clienteRepository.findByEmail(email);
    	Artista artista = artistaRepository.findById(artistaId);
        RequestOrcamentoDto requestOrcamentoDto = new RequestOrcamentoDto(imagem, largura, altura, parteCorpo, descricao, produtoId, artistaId);
        ResponseEntity<ResponseOrcamentoDto> response;

        if (produtoId != null) {
            response = orcamentoService.regiterByProduto(requestOrcamentoDto, produtoId, cliente);
        } else {
            response = orcamentoService.register(requestOrcamentoDto, cliente);
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("message", "Orçamento salvo com sucesso!");
        } else {
            model.addAttribute("error", "Erro ao salvar o orçamento.");
        }

        return "redirect:/orcamentos/meus-orcamentos";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/meus-orcamentos")
    public String listarOrcamentos(Model model, Principal principal) {
        String email = principal.getName();
        System.out.println(email);

        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            model.addAttribute("error", "Cliente não encontrado.");
            return "cliente/meus_orcamentos";
        }

        ResponseEntity<List<ResponseOrcamentoDto>> response = orcamentoService.getOrcamentoByClienteId(cliente.getId());
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("orcamentos", response.getBody());
            System.out.println("Consegui recuperar com o cliente");
        } else {
            model.addAttribute("error", "Erro ao carregar orçamentos.");
            System.out.println("Não consegui recuperar com o cliente");
        }

        return "cliente/meus_orcamentos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-orcamentos")
    public String listarOrcamentosAdmin(Model model) {
        ResponseEntity<List<ResponseOrcamentoDto>> response = orcamentoService.getAll();
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("orcamentos", response.getBody());
        }
        return "admin_orcamentos";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Integer id, Model model) {
        ResponseEntity<ResponseOrcamentoDto> response = orcamentoService.getById(id);
        System.out.println("Achei o orcamento");
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("orcamento", response.getBody());
            return "atualizar_orcamento";
        }
        return "redirect:/orcamentos/meus-orcamentos";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/editar-orcamento")
    public String atualizarOrcamento(
        @PathVariable Integer id,
        @ModelAttribute RequestOrcamentoDto requestOrcamentoDto,
        Model model
    ) {
        ResponseEntity<ResponseOrcamentoDto> response = orcamentoService.update(id, requestOrcamentoDto);

        if (response.getStatusCode().is2xxSuccessful()) {
        	System.out.println("Consegui atualizar");
            model.addAttribute("message", "Orçamento atualizado com sucesso!");
        } else {
        	System.out.println("Não consegui atualizar");
            model.addAttribute("error", "Erro ao atualizar o orçamento.");
        }
        return "redirect:/orcamentos/meus-orcamentos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/concluir")
    public String concluirOrcamento(@PathVariable Integer id, Model model) {
        ResponseEntity<ResponseOrcamentoDto> response = orcamentoService.updateStatus(id, "Concluído");
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("message", "Orçamento concluído com sucesso!");
        } else {
            model.addAttribute("error", "Erro ao concluir o orçamento.");
        }
        return "redirect:/orcamentos/admin-orcamentos";
    }
    
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{id}/deletar")
    public String deletarOrcamento(@PathVariable Integer id, Model model, Principal principal) {
        ResponseEntity<String> response = orcamentoService.delete(id);
        String email = principal.getName();
        Logins logins = loginsRepository.findByEmail(email);
        
        if(logins.getUserRole() == Roles.ADMIN) {
        	return "redirect:/orcamentos/admin-orcamentos";
        }
        
        return "redirect:/orcamentos/meus-orcamentos";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/historico")
    public String historicoTattoo(Model model, Principal principal) {
    	String email = principal.getName();
    	Cliente cliente = clienteRepository.findByEmail(email);
    	
        ResponseEntity<List<ResponseOrcamentoDto>> response = orcamentoService.getOrcamentoByClienteId(cliente.getId());
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("orcamentos", response.getBody());
        }
        return "cliente/historico";
    }
}