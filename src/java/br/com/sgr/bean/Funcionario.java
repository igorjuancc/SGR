package br.com.sgr.bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name="id_funcionario")
@Table (name="tb_funcionario")
public class Funcionario extends Pessoa implements Serializable{
    private Funcao funcao;
    private String empresa;
    private int status;
    private String senha;
    private List<Noticia> noticias;
    private List<Atendimento> atendimentos;
    private List<Financeiro> financeiros;
    private List<Salao> reservaSalao;
    private List<RegistroES> regEntradaFuncionario;
    private List<RegistroES> regSaidaFuncionario;
    private List<Notificacao> notificacoes;

    public Funcionario() {
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcao")
    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }
    
    @Column(updatable=true, name="empresa_funcionario")
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    
    @Column(updatable=true, name="status_funcionario")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    @Column(updatable=true, name="senha_funcionario")
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    } 
    
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }
    
    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }
    
    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Financeiro> getFinanceiros() {
        return financeiros;
    }

    public void setFinanceiros(List<Financeiro> financeiros) {
        this.financeiros = financeiros;
    }
    
    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Salao> getReservaSalao() {
        return reservaSalao;
    }

    public void setReservaSalao(List<Salao> reservaSalao) {
        this.reservaSalao = reservaSalao;
    }
    
    @OneToMany(mappedBy = "funcionarioEntrada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<RegistroES> getRegEntradaFuncionario() {
        return regEntradaFuncionario;
    }

    public void setRegEntradaFuncionario(List<RegistroES> regEntradaFuncionario) {
        this.regEntradaFuncionario = regEntradaFuncionario;
    }
    
    @OneToMany(mappedBy = "funcionarioSaida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<RegistroES> getRegSaidaFuncionario() {
        return regSaidaFuncionario;
    }

    public void setRegSaidaFuncionario(List<RegistroES> regSaidaFuncionario) {
        this.regSaidaFuncionario = regSaidaFuncionario;
    }
    
    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }
}
