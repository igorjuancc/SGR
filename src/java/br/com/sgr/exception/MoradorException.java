package br.com.sgr.exception;

public class MoradorException extends Exception {

    public MoradorException(String message) {
        super(message);
    }
    
    public MoradorException(String message, Throwable cause){
        super(message, cause);
    }
            
}
