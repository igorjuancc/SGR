package br.com.sgr.exception;

public class QuestaoException extends Exception {

    public QuestaoException(String message) {
        super(message);
    }
    
    public QuestaoException(String message, Throwable cause){
        super(message, cause);
    }
            
}
