package br.com.sgr.exception;

public class AtendimentoException extends Exception {

    public AtendimentoException(String message) {
        super(message);
    }
    
    public AtendimentoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
