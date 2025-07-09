package com.projecttattoo.BrenoLendaTattoo.controllers;

import com.projecttattoo.BrenoLendaTattoo.dto.artista.RequestArtistaDTO;
import com.projecttattoo.BrenoLendaTattoo.services.ArtistaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/artistas")
public class ArtistaController {

    @Autowired
    private ArtistaService artistaService;

    // 1) Exibe o formulário em GET
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String formNovoArtista(Model model) {
        model.addAttribute("artista", new RequestArtistaDTO(null, null, null, null, null, null, null));
        return "admin_novo_artista";       // nome do seu template sem .html
    }

    // 2) Recebe o POST do formulário
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/novo")
    public String novoArtista(
            @Validated @ModelAttribute("artista") RequestArtistaDTO dto,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            // Validações falharam: volta pro form (pode mostrar mensagens)
            return "admin_novo_artista";
        }

        var response = artistaService.cadastrarArtista(dto);
        if (response.getStatusCode().isError()) {
            // exibe erro (por exemplo, e‑mail duplicado)
            model.addAttribute("erro", response.getBody());
            return "admin_novo_artista";
        }

        // Sucesso: redireciona para lista ou catálogo
        return "redirect:/admin/catalogo";
    }
}
