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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="tb_atendimento")
@SequenceGenerator(name="seq_atendimento", sequenceName="tb_atendimento_id_atendimento_seq")
public class Atendimento implements Serializable {
    private int id;
    private int status;
    private String descricao;    
    private TipoAtendimento tipo;
    private Date dataAbertura;
    private Date dataFechamento;
    private Morador morador;
    private Funcionario funcionario; 
    private List<Comentario> comentarios;
    private List<Notificacao> notificacoes;

    public Atendimento() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_atendimento")
    @Column(name="id_atendimento")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="status_atendimento")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    @Column(name="desc_atendimento")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_tipo_atendimento")
    public TipoAtendimento getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtendimento tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="data_abertura_atendimento")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
    
    @Column(name="data_fechamento_atendimento")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
    
    @ManyToOne
    @JoinColumn(name="id_morador_atendimento")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_atendimento")
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    
    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @OrderBy("id_comentario")
    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "atendimentos", fetch = FetchType.LAZY)
    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }
}
