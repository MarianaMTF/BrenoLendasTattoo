package com.projecttattoo.BrenoLendaTattoo.services;

import com.projecttattoo.BrenoLendaTattoo.dto.LoginDto;
import com.projecttattoo.BrenoLendaTattoo.dto.ResponseConfirmedDto;
import com.projecttattoo.BrenoLendaTattoo.dto.ResponseDto;
import com.projecttattoo.BrenoLendaTattoo.dto.cliente.ClienteDto;
import com.projecttattoo.BrenoLendaTattoo.dto.cliente.RequestRegisterDto;
import com.projecttattoo.BrenoLendaTattoo.enums.Roles;
import com.projecttattoo.BrenoLendaTattoo.interfaces.ClienteInterfaceService;
import com.projecttattoo.BrenoLendaTattoo.models.Cliente;
import com.projecttattoo.BrenoLendaTattoo.models.Logins;
import com.projecttattoo.BrenoLendaTattoo.repositories.ClienteRepository;
import com.projecttattoo.BrenoLendaTattoo.repositories.LoginsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClienteService implements ClienteInterfaceService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticatedService authenticatedService;

    @Override
    public ResponseEntity<ResponseDto> register(RequestRegisterDto body) {
    	if (clienteRepository.existsByEmail(body.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }

        Cliente newCliente = new Cliente();
        newCliente.setNomeCompleto(body.nomeCompleto());
        newCliente.setEmail(body.email());
        newCliente.setTelefone(body.telefone());
        newCliente.setCpf(body.cpf());
        newCliente.setSenha(encoder.encode(body.senha()));

        Logins logins = new Logins();
        logins.setEmail(newCliente.getEmail());
        logins.setNome(newCliente.getNomeCompleto());
        logins.setSenha(newCliente.getSenha());
        logins.setUserRole(Roles.USER);

        clienteRepository.save(newCliente);
        System.out.println("Adicionei na tabela cliente");
        loginsRepository.save(logins);
        System.out.println("Adicionei na tabela logins");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(newCliente.getId(), "Cliente registrado com sucesso", newCliente.getEmail()));
    }

    @Override
    public ResponseEntity<String> resetPassword(String email, LoginDto senha) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        cliente.setSenha(encoder.encode(senha.senha()));
        clienteRepository.save(cliente);

        return ResponseEntity.ok("Senha redefinida com sucesso");
    }

    @Override
    public ResponseEntity<String> getClientes(String email) {
        Logins cliente = loginsRepository.findByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        return ResponseEntity.ok(cliente.getNome());
    }

    @Override
    public ResponseEntity<ResponseConfirmedDto> redifinedPassword(String body) {
        Logins cliente = loginsRepository.findByEmail(body);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseConfirmedDto("Cliente não encontrado"));
        }

        return ResponseEntity.ok(new ResponseConfirmedDto("Senha redefinida com sucesso"));
    }

    @Override
    public ResponseEntity<Integer> delete(Integer id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        clienteRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @Override
    public ResponseEntity<RequestRegisterDto> update(Integer id, RequestRegisterDto body) {
    	Optional<Cliente> clienteOpt = clienteRepository.findById(id);
    	
    	System.out.println("Cheguei aqui no service");
    	
        if (clienteOpt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        Cliente cliente = clienteOpt.get();

        cliente.setNomeCompleto(body.nomeCompleto());
        cliente.setEmail(body.email());
        cliente.setSenha(body.senha());
        cliente.setTelefone(body.telefone());
        cliente.setCpf(body.cpf());

        clienteRepository.save(cliente);
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<List<ClienteDto>> getAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<ClienteDto> clientesDtos = clientes.stream()
                .map(cliente -> new ClienteDto(
                        cliente.getId(),
                        cliente.getNomeCompleto(),
                        cliente.getEmail(),
                        cliente.getTelefone(),
                        cliente.getCpf()
                ))
                .toList();

        return ResponseEntity.ok(clientesDtos);
    }

    @Override
    public ResponseEntity<ClienteDto> getById(Integer id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Cliente cliente = clienteOpt.get();
        ClienteDto clienteDto = new ClienteDto(
                cliente.getId(),
                cliente.getNomeCompleto(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpf()
        );

        return ResponseEntity.ok(clienteDto);
    }

	@Override
	public ResponseEntity<ResponseConfirmedDto> confirmEmail(String code) {
		// TODO Auto-generated method stub
		return null;
	}
}