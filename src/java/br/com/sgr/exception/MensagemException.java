package br.com.sgr.exception;

public class MensagemException extends Exception {

    public MensagemException(String message) {
        super(message);
    }
    
    public MensagemException(String message, Throwable cause){
        super(message, cause);
    }
            
}
