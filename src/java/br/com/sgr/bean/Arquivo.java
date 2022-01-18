package br.com.sgr.bean;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_arquivo")
@SequenceGenerator(name="seq_arquivo", sequenceName="tb_arquivo_id_arquivo_seq")
public class Arquivo implements Serializable {
    private int id;
    private String extensao;
    private String descricao;
    private Questao questao;
    private Noticia noticia;
    private Notificacao notificacao;

    public Arquivo() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_arquivo")
    @Column(name="id_arquivo")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="ext_arquivo")
    public String getExtensao() {
        return extensao;
    }

    public void setExtensao(String extensao) {
        this.extensao = extensao;
    }
    
    @Column(name="desc_arquivo")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }    
    
    @ManyToOne
    @JoinColumn(name="id_questao_arquivo")
    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }    
    
    @ManyToOne
    @JoinColumn(name="id_noticia_arquivo")
    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }     
        
    @ManyToOne
    @JoinColumn(name="id_notificacao_arquivo")
    public Notificacao getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }
}
