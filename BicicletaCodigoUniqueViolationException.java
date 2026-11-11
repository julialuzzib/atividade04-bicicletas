package br.edu.ifc.bikes.exception;

public class BicicletaCodigoUniqueViolationException extends RuntimeException {
    public BicicletaCodigoUniqueViolationException(String message) {
        super(message);
    }
}
