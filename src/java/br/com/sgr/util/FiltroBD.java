package br.com.sgr.util;

import java.io.Serializable;

public class FiltroBD implements Serializable {
    private String descricao;
    private int primeiroRegistro;
    private Long qntdRegistros;
    private boolean asc;
    private String propriedadeOrdenacao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPrimeiroRegistro() {
        return primeiroRegistro;
    }

    public void setPrimeiroRegistro(int primeiroRegistro) {
        this.primeiroRegistro = primeiroRegistro;
    }

    public Long getQntdRegistros() {
        return qntdRegistros;
    }

    public void setQntdRegistros(Long qntdRegistros) {
        this.qntdRegistros = qntdRegistros;
    }
    
    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public String getPropriedadeOrdenacao() {
        return propriedadeOrdenacao;
    }

    public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
        this.propriedadeOrdenacao = propriedadeOrdenacao;
    }   
}
