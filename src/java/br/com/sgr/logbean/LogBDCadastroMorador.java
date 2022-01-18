/*

Tipo Morador                -- [0]Responsavel [1]Dependente
Tipo Pessoa Desligamento    -- [0]Sindico [1]Morador Responsavel

*/

package br.com.sgr.logbean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_log_cadastro_morador")
@Table(name = "tb_log_cadastro_morador")
@SequenceGenerator(name="seq_log", sequenceName="tb_log_cadastro_morador_id_log_cadastro_morador_seq")
public class LogBDCadastroMorador implements Serializable{
    private int id;
    private int idMorador;
    private int tipoMorador;
    private int idPessoaAprovacao;
    private int tipoPessoaAprovacao;
    private Date dataAprovacao;
    private Integer idPessoaDesligamento;
    private Integer tipoPessoaDesligamento;
    private Date dataDesligamento;       

    public LogBDCadastroMorador() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_log")
    @Column(name="id_log_cadastro_morador")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="id_morador_log_cadastro_morador")
    public int getIdMorador() {
        return idMorador;
    }

    public void setIdMorador(int idMorador) {
        this.idMorador = idMorador;
    }
    
    @Column(name="tipo_morador_log_cadastro_morador")
    public int getTipoMorador() {
        return tipoMorador;
    }

    public void setTipoMorador(int tipoMorador) {
        this.tipoMorador = tipoMorador;
    }
    
    @Column(name="id_pessoa_aprovacao_log_cadastro_morador")
    public int getIdPessoaAprovacao() {
        return idPessoaAprovacao;
    }

    public void setIdPessoaAprovacao(int idPessoaAprovacao) {
        this.idPessoaAprovacao = idPessoaAprovacao;
    }
    
    @Column(name="tipo_pessoa_aprovacao_log_cadastro_morador")
    public int getTipoPessoaAprovacao() {
        return tipoPessoaAprovacao;
    }

    public void setTipoPessoaAprovacao(int tipoPessoaAprovacao) {
        this.tipoPessoaAprovacao = tipoPessoaAprovacao;
    }
    
    @Column(name="data_aprovacao_log_cadastro_morador")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
    
    @Column(name="id_pessoa_desligamento_log_cadastro_morador")
    public Integer getIdPessoaDesligamento() {
        return idPessoaDesligamento;
    }

    public void setIdPessoaDesligamento(Integer idPessoaDesligamento) {
        this.idPessoaDesligamento = idPessoaDesligamento;
    }
    
    @Column(name="tipo_pessoa_desligamento_log_cadastro_morador")
    public Integer getTipoPessoaDesligamento() {
        return tipoPessoaDesligamento;
    }

    public void setTipoPessoaDesligamento(Integer tipoPessoaDesligamento) {
        this.tipoPessoaDesligamento = tipoPessoaDesligamento;
    }
    
    @Column(name="data_desligamento_log_cadastro_morador")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataDesligamento() {
        return dataDesligamento;
    }

    public void setDataDesligamento(Date dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }
}
