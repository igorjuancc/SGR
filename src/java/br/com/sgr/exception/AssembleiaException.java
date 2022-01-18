package br.com.sgr.exception;

public class AssembleiaException extends Exception {

    public AssembleiaException(String message) {
        super(message);
    }
    
    public AssembleiaException(String message, Throwable cause){
        super(message, cause);
    }
            
}
