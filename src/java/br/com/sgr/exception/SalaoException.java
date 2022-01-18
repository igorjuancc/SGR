package br.com.sgr.exception;

public class SalaoException extends Exception {

    public SalaoException(String message) {
        super(message);
    }
    
    public SalaoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
