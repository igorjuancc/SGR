package br.com.sgr.bean;

import java.io.Serializable;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_questao")
@SequenceGenerator(name="seq_questao", sequenceName="tb_questao_id_questao_seq")
public class Questao implements Serializable {
    private int id;
    private String titulo;
    private String descricao;
    private Assembleia assembleia;
    private List<OpcQuestao> opcoes;
    private List<Comentario> comentarios;
    private List<Arquivo> arquivos;

    public Questao() {
    }
            
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_questao")
    @Column(name="id_questao")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="titulo_questao")
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }      
    
    @Column(name="desc_questao")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_assembleia")
    public Assembleia getAssembleia() {
        return assembleia;
    }

    public void setAssembleia(Assembleia assembleia) {
        this.assembleia = assembleia;
    }
    
    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id_opc_questao")
    public List<OpcQuestao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<OpcQuestao> opcoes) {
        this.opcoes = opcoes;
    }
    
    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id_comentario")
    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    
    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id_arquivo")
    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    } 
}
