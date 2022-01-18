package br.com.sgr.exception;

public class VagaException extends Exception {

    public VagaException(String message) {
        super(message);
    }
    
    public VagaException(String message, Throwable cause){
        super(message, cause);
    }
            
}
