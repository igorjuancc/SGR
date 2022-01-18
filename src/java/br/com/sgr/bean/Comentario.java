package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="tb_comentario")
@SequenceGenerator(name="seq_comentario", sequenceName="tb_comentario_id_comentario_seq")
public class Comentario implements Serializable {
    private int id;
    private int tipo;
    private Date data;
    private String descricao;
    private Pessoa pessoa;    
    private Atendimento atendimento;
    private Questao questao;
    private String tipoPessoa;

    public Comentario() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_comentario")
    @Column(name="id_comentario")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="tipo_comentario")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="data_comentario")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
    @Column(name="desc_comentario")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_pessoa_comentario")
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
    
    @ManyToOne
    @JoinColumn(name="id_atendimento_comentario")
    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }
    
    @ManyToOne
    @JoinColumn(name="id_questao_comentario")
    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }
    
    @Column(name="tipo_pessoa_comentario")
    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
}
