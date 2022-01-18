package br.com.sgr.exception;

public class VisitanteException extends Exception {

    public VisitanteException(String message) {
        super(message);
    }
    
    public VisitanteException(String message, Throwable cause){
        super(message, cause);
    }
            
}
