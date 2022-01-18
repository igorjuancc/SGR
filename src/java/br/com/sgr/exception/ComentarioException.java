package br.com.sgr.exception;

public class ComentarioException extends Exception {

    public ComentarioException(String message) {
        super(message);
    }
    
    public ComentarioException(String message, Throwable cause){
        super(message, cause);
    }
            
}
