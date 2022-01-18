package br.com.sgr.exception;

public class RelatorioException extends Exception {

    public RelatorioException(String message) {
        super(message);
    }
    
    public RelatorioException(String message, Throwable cause){
        super(message, cause);
    }
            
}
