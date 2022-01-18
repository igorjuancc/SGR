package br.com.sgr.exception;

public class RegistroESException extends Exception {

    public RegistroESException(String message) {
        super(message);
    }
    
    public RegistroESException(String message, Throwable cause){
        super(message, cause);
    }
            
}
