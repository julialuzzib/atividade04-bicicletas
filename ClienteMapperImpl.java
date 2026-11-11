package br.edu.ifc.bikes.dto.mapper;

import br.edu.ifc.bikes.dto.ClienteRequestDTO;
import br.edu.ifc.bikes.dto.ClienteResponseDTO;
import br.edu.ifc.bikes.entity.Cliente;
import br.edu.ifc.bikes.entity.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-10T21:06:41-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Oracle Corporation)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public Cliente toCliente(ClienteRequestDTO clienteRequestDTO) {
        if ( clienteRequestDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setNome( clienteRequestDTO.nome() );
        cliente.setCpf( clienteRequestDTO.cpf() );

        return cliente;
    }

    @Override
    public ClienteResponseDTO toResponse(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        Long usuarioId = null;
        Long id = null;
        String nome = null;
        String cpf = null;

        usuarioId = clienteUsuarioId( cliente );
        id = cliente.getId();
        nome = cliente.getNome();
        cpf = cliente.getCpf();

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO( id, nome, cpf, usuarioId );

        return clienteResponseDTO;
    }

    @Override
    public List<ClienteResponseDTO> toResponse(List<Cliente> clientes) {
        if ( clientes == null ) {
            return null;
        }

        List<ClienteResponseDTO> list = new ArrayList<ClienteResponseDTO>( clientes.size() );
        for ( Cliente cliente : clientes ) {
            list.add( toResponse( cliente ) );
        }

        return list;
    }

    private Long clienteUsuarioId(Cliente cliente) {
        Usuario usuario = cliente.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }
}
