package br.com.sgr.exception;

public class InfracaoException extends Exception {

    public InfracaoException(String message) {
        super(message);
    }
    
    public InfracaoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
