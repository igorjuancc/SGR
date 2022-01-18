package br.com.sgr.exception;

public class VisitaException extends Exception {

    public VisitaException(String message) {
        super(message);
    }
    
    public VisitaException(String message, Throwable cause){
        super(message, cause);
    }
            
}
