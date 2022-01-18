package br.com.sgr.bean;

import java.util.List;

public class Email {
    private String mensagem;
    private String assunto;
    private String remetente;
    private List<String> destinatario;
    
    public Email() {
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public List<String> getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(List<String> destinatario) {
        this.destinatario = destinatario;
    }
}
