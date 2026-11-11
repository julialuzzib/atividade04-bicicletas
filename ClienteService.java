package br.edu.ifc.bikes.service;

import br.edu.ifc.bikes.dto.ClienteRequestDTO;
import br.edu.ifc.bikes.dto.ClienteResponseDTO;
import br.edu.ifc.bikes.dto.mapper.ClienteMapper;
import br.edu.ifc.bikes.entity.Cliente;
import br.edu.ifc.bikes.entity.Usuario;
import br.edu.ifc.bikes.exception.CpfUniqueViolationException;
import br.edu.ifc.bikes.exception.EntityNotFoundException;
import br.edu.ifc.bikes.repository.ClienteRepository;
import br.edu.ifc.bikes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO clienteRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new EntityNotFoundException("Usuário autenticado não encontrado");
        }

        if (clienteRepository.existsByCpf(clienteRequestDTO.cpf())) {
            throw new CpfUniqueViolationException(String.format("O CPF '%s' já está cadastrado", clienteRequestDTO.cpf()));
        }

        Long usuarioId = getAuthenticatedUserId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Usuário com id=%d não encontrado", usuarioId)));

        Cliente cliente = clienteMapper.toCliente(clienteRequestDTO);
        cliente.setUsuario(usuario);

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO getById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cliente com id=%d não encontrado", id)));

        return clienteMapper.toResponse(cliente);
    }

    @Transactional
    public List<ClienteResponseDTO> getAll() {
        return clienteMapper.toResponse(clienteRepository.findAll());
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof br.edu.ifc.bikes.jwt.JwtUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new EntityNotFoundException("Usuário autenticado não encontrado");
    }
}
