package com.projecttattoo.BrenoLendaTattoo.services;

import com.projecttattoo.BrenoLendaTattoo.dto.artista.RequestArtistaDTO;
import com.projecttattoo.BrenoLendaTattoo.dto.artista.ResponseArtistaDTO;
import com.projecttattoo.BrenoLendaTattoo.enums.Roles;
import com.projecttattoo.BrenoLendaTattoo.models.Artista;
import com.projecttattoo.BrenoLendaTattoo.models.Logins;
import com.projecttattoo.BrenoLendaTattoo.repositories.ArtistaRepository;
import com.projecttattoo.BrenoLendaTattoo.repositories.LoginsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private PasswordEncoder encoder;

    public ResponseEntity<String> cadastrarArtista(RequestArtistaDTO dto) {
        if (artistaRepository.existsByEmail(dto.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um artista ou login com este e-mail");
        }

        Artista artista = new Artista();
        artista.setNome(dto.nome());
        artista.setSobrenome(dto.sobrenome());
        artista.setCpf(dto.cpf());
        artista.setEmail(dto.email());
        artista.setSenha(encoder.encode(dto.senha()));
        artista.setEstilo(dto.estilo());
        artista.setTelefone(dto.telefone());

        artistaRepository.save(artista);

        Logins login = new Logins();
        login.setNome(dto.nome());
        login.setEmail(dto.email());
        login.setSenha(encoder.encode(dto.senha()));
        login.setUserRole(Roles.ADMIN);
        login.setArtista(artista);

        loginsRepository.save(login);

        return ResponseEntity.status(HttpStatus.CREATED).body("Artista cadastrado com sucesso");
    }

    public ResponseEntity<List<ResponseArtistaDTO>> listarTodos() {
        List<Artista> artistas = artistaRepository.findAll();
        List<ResponseArtistaDTO> dtos = artistas.stream()
                .map(art -> new ResponseArtistaDTO(
                        art.getId(),
                        art.getNome(),
                        art.getSobrenome(),
                        art.getCpf(),
                        art.getEmail(),
                        art.getSenha(),
                        art.getTelefone(),
                        art.getEstilo()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<ResponseArtistaDTO> buscarPorId(Integer id) {
        Optional<Artista> artistaOpt = artistaRepository.findById(id);
        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Artista artista = artistaOpt.get();
        ResponseArtistaDTO dto = new ResponseArtistaDTO(
        		artista.getId(),
                artista.getNome(),
                artista.getSobrenome(),
                artista.getCpf(),
                artista.getEmail(),
                artista.getSenha(),
                artista.getTelefone(),
                artista.getEstilo()
        );
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<String> deletar(Integer id) {
        if (!artistaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado");
        }
        artistaRepository.deleteById(id);
        return ResponseEntity.ok("Artista removido com sucesso");
    }

    public ResponseEntity<String> atualizar(Integer id, RequestArtistaDTO dto) {
        Optional<Artista> artistaOpt = artistaRepository.findById(id);
        if (artistaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista não encontrado");
        }

        Artista artista = artistaOpt.get();
        artista.setNome(dto.nome());
        artista.setSobrenome(dto.sobrenome());
        artista.setCpf(dto.cpf());
        artista.setEmail(dto.email());
        artista.setTelefone(dto.telefone());
        artista.setEstilo(dto.estilo());
        artista.setSenha(encoder.encode(dto.senha()));

        artistaRepository.save(artista);
        return ResponseEntity.ok("Artista atualizado com sucesso");
    }
}
