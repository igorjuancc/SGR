package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="tb_notificacao")
@SequenceGenerator(name="seq_notificacao", sequenceName="tb_notificacao_id_notificacao_seq")
public class Notificacao implements Serializable{
    private int id;
    private int tipo;
    private Date dataReferencia;
    private Date dataEmissao;
    private String descricao;
    private Morador morador;
    private Funcionario funcionario;
    private Infracao infracao;
    private BoletoSGR boleto;
    private List<Atendimento> atendimentos;
    private List<Arquivo> arquivos;
    
    public Notificacao() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_notificacao")
    @Column(name="id_notificacao")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="tipo_notificacao")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="data_referencia_notificacao")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }
    
    @Column(name="data_emissao_notificacao")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
    
    @Column(name="desc_notificacao")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_morador_notificacao")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_notificacao")
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    @ManyToOne
    @JoinColumn(name="id_infracao_notificacao")
    public Infracao getInfracao() {
        return infracao;
    }

    public void setInfracao(Infracao infracao) {
        this.infracao = infracao;
    }
    
    @OneToOne    
    @JoinColumn(name="id_boleto_notificacao")
    public BoletoSGR getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoSGR boleto) {
        this.boleto = boleto;
    }
    
    @ManyToMany(targetEntity=br.com.sgr.bean.Pessoa.class, cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinTable(name="tb_notificacao_atendimento", joinColumns={@JoinColumn(name="id_notificacao")},inverseJoinColumns={@JoinColumn(name="id_atendimento")})
    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }
    
    @OneToMany(mappedBy = "notificacao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("ext_arquivo")
    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }
}
