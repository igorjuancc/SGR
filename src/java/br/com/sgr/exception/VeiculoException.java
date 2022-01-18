package br.com.sgr.exception;

public class VeiculoException extends Exception {

    public VeiculoException(String message) {
        super(message);
    }
    
    public VeiculoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
