package br.edu.ifc.bikes.service;

import br.edu.ifc.bikes.dto.BicicletaRequestDTO;
import br.edu.ifc.bikes.dto.BicicletaResponseDTO;
import br.edu.ifc.bikes.dto.mapper.BicicletaMapper;
import br.edu.ifc.bikes.entity.Bicicleta;
import br.edu.ifc.bikes.exception.BicicletaCodigoUniqueViolationException;
import br.edu.ifc.bikes.exception.EntityNotFoundException;
import br.edu.ifc.bikes.repository.BicicletaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BicicletaService {

    private final BicicletaRepository bicicletaRepository;
    private final BicicletaMapper bicicletaMapper;

    @Transactional
    public BicicletaResponseDTO create(BicicletaRequestDTO request) {
        if (bicicletaRepository.existsByCodigo(request.codigo())) {
            throw codigoJaCadastrado(request.codigo());
        }
        return bicicletaMapper.toResponse(bicicletaRepository.save(bicicletaMapper.toBicicleta(request)));
    }

    @Transactional
    public BicicletaResponseDTO getByCodigo(String codigo) {
        return bicicletaMapper.toResponse(findByCodigo(codigo));
    }

    @Transactional
    public List<BicicletaResponseDTO> getAll() {
        return bicicletaMapper.toResponse(bicicletaRepository.findAll());
    }

    @Transactional
    public BicicletaResponseDTO update(String codigoAtual, BicicletaRequestDTO request) {
        Bicicleta bicicleta = findByCodigo(codigoAtual);
        if (!bicicleta.getCodigo().equals(request.codigo())
                && bicicletaRepository.existsByCodigo(request.codigo())) {
            throw codigoJaCadastrado(request.codigo());
        }
        bicicleta.setCodigo(request.codigo());
        bicicleta.setStatus(request.status());
        return bicicletaMapper.toResponse(bicicletaRepository.save(bicicleta));
    }

    @Transactional
    public void delete(String codigo) {
        bicicletaRepository.delete(findByCodigo(codigo));
    }

    private Bicicleta findByCodigo(String codigo) {
        return bicicletaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Bicicleta com código '%s' não encontrada", codigo)));
    }

    private BicicletaCodigoUniqueViolationException codigoJaCadastrado(String codigo) {
        return new BicicletaCodigoUniqueViolationException(
                String.format("O código '%s' já está cadastrado", codigo));
    }
}
