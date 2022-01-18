package br.com.sgr.exception;

public class NoticiaException extends Exception {

    public NoticiaException(String message) {
        super(message);
    }
    
    public NoticiaException(String message, Throwable cause){
        super(message, cause);
    }
            
}
