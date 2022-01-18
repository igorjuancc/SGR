package br.com.sgr.exception;

public class ArquivoException extends Exception {

    public ArquivoException(String message) {
        super(message);
    }
    
    public ArquivoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
