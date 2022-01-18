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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;


@Entity
@Table(name="tb_mensagem")
@SequenceGenerator(name="seq_mensagem", sequenceName="tb_mensagem_id_mensagem_seq")
public class Mensagem implements Serializable {
    private int id;
    private int statusAutor;
    private int statusReceptor;
    private String assunto;
    private String descricao;
    private Date data;
    private Pessoa autor;
    private Pessoa receptor;
    private Mensagem origemResposta;   
    private List<Mensagem> respostas;
    
    public Mensagem() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_mensagem")
    @Column(name="id_mensagem")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="status_autor_mensagem")
    public int getStatusAutor() {
        return statusAutor;
    }

    public void setStatusAutor(int statusAutor) {
        this.statusAutor = statusAutor;
    }
    
    @Column(name="status_receptor_mensagem")
    public int getStatusReceptor() {
        return statusReceptor;
    }

    public void setStatusReceptor(int statusReceptor) {
        this.statusReceptor = statusReceptor;
    }
    
    @Column(name="assunto_mensagem")
    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }
    
    @Column(name="desc_mensagem")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Column(name="data_mensagem")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
    @ManyToOne
    @JoinColumn(name="id_autor_mensagem")
    public Pessoa getAutor() {
        return autor;
    }

    public void setAutor(Pessoa autor) {
        this.autor = autor;
    }
    
    @ManyToOne
    @JoinColumn(name="id_receptor_mensagem")
    public Pessoa getReceptor() {
        return receptor;
    }

    public void setReceptor(Pessoa receptor) {
        this.receptor = receptor;
    }
    
    @ManyToOne
    @JoinColumn(name="id_resposta_mensagem")
    public Mensagem getOrigemResposta() {
        return origemResposta;
    }

    public void setOrigemResposta(Mensagem origemResposta) {
        this.origemResposta = origemResposta;
    }
    
    @OneToMany(mappedBy = "origemResposta", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    public List<Mensagem> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Mensagem> respostas) {
        this.respostas = respostas;
    }
}
