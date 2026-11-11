package br.edu.ifc.bikes.dto.mapper;

import br.edu.ifc.bikes.dto.UsuarioRequestDTO;
import br.edu.ifc.bikes.dto.UsuarioResponseDTO;
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
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if ( usuarioRequestDTO == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setUsername( usuarioRequestDTO.username() );
        usuario.setPassword( usuarioRequestDTO.password() );

        return usuario;
    }

    @Override
    public UsuarioResponseDTO toResponse(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String role = null;

        id = usuario.getId();
        username = usuario.getUsername();
        if ( usuario.getRole() != null ) {
            role = usuario.getRole().name();
        }

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO( id, username, role );

        return usuarioResponseDTO;
    }

    @Override
    public List<UsuarioResponseDTO> toResponse(List<Usuario> usuarios) {
        if ( usuarios == null ) {
            return null;
        }

        List<UsuarioResponseDTO> list = new ArrayList<UsuarioResponseDTO>( usuarios.size() );
        for ( Usuario usuario : usuarios ) {
            list.add( toResponse( usuario ) );
        }

        return list;
    }
}
