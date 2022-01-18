package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_morador")
@Table(name="tb_morador")
public class Morador extends Pessoa implements Serializable{
    private int tipo;
    private int status;
    private String senha;
    private Apartamento apartamento;    
    private Date dataCadastro;
    private List<Veiculo> veiculos;
    private List<Atendimento> atendimentos;   
    private List<Salao> reservaSalao;
    private List<Voto> votos;
    private List<Visitante> visitantes;
    private List<BoletoSGR> boletos;
    private List<Notificacao> notificacoes;

    public Morador() {
        this.apartamento = new Apartamento();
    }
    
    @Column(name="tipo_morador")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="senha_morador")
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    @ManyToOne
    @JoinColumn(name="apartamento_morador")
    public Apartamento getApartamento() {
        return apartamento;
    }

    public void setApartamento(Apartamento apartamento) {
        this.apartamento = apartamento;
    }
    
    @Column(name="status_morador")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }
    
    @Column(name="data_aprovacao_morador")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }   
    
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }
        
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Salao> getReservaSalao() {
        return reservaSalao;
    }

    public void setReservaSalao(List<Salao> reservaSalao) {
        this.reservaSalao = reservaSalao;
    }    
    
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Voto> getVotos() {
        return votos;
    }

    public void setVotos(List<Voto> votos) {
        this.votos = votos;
    }
    
    @OneToMany(mappedBy = "moradorCadastro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(List<Visitante> visitantes) {
        this.visitantes = visitantes;
    }
    
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<BoletoSGR> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<BoletoSGR> boletos) {
        this.boletos = boletos;
    }  
    
    @OneToMany(mappedBy = "morador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }
}