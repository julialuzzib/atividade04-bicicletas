package br.edu.ifc.bikes.dto.mapper;

import br.edu.ifc.bikes.dto.BicicletaRequestDTO;
import br.edu.ifc.bikes.dto.BicicletaResponseDTO;
import br.edu.ifc.bikes.entity.Bicicleta;
import br.edu.ifc.bikes.entity.BicicletaStatus;
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
public class BicicletaMapperImpl implements BicicletaMapper {

    @Override
    public Bicicleta toBicicleta(BicicletaRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        Bicicleta bicicleta = new Bicicleta();

        bicicleta.setCodigo( request.codigo() );
        bicicleta.setStatus( request.status() );

        return bicicleta;
    }

    @Override
    public BicicletaResponseDTO toResponse(Bicicleta bicicleta) {
        if ( bicicleta == null ) {
            return null;
        }

        Long id = null;
        String codigo = null;
        BicicletaStatus status = null;

        id = bicicleta.getId();
        codigo = bicicleta.getCodigo();
        status = bicicleta.getStatus();

        BicicletaResponseDTO bicicletaResponseDTO = new BicicletaResponseDTO( id, codigo, status );

        return bicicletaResponseDTO;
    }

    @Override
    public List<BicicletaResponseDTO> toResponse(List<Bicicleta> bicicletas) {
        if ( bicicletas == null ) {
            return null;
        }

        List<BicicletaResponseDTO> list = new ArrayList<BicicletaResponseDTO>( bicicletas.size() );
        for ( Bicicleta bicicleta : bicicletas ) {
            list.add( toResponse( bicicleta ) );
        }

        return list;
    }
}
