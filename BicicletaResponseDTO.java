package br.edu.ifc.bikes.dto;

import br.edu.ifc.bikes.entity.BicicletaStatus;

public record BicicletaResponseDTO(
        Long id,
        String codigo,
        BicicletaStatus status
) {
}
