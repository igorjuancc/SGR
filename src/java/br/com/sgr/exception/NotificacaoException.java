package br.com.sgr.exception;

public class NotificacaoException extends Exception {

    public NotificacaoException(String message) {
        super(message);
    }
    
    public NotificacaoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
