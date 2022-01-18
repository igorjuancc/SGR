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
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_noticia")
@Table(name = "tb_noticia")
@SequenceGenerator(name="seq_noticia", sequenceName="tb_noticia_id_noticia_seq")
public class Noticia implements Serializable{
    private int id;
    private String titulo;
    private String descricao;
    private Date data;
    private Date ultimaAlteracao;
    private Funcionario autor;
    private Funcionario funcionarioAlteracao;
    private List<Arquivo> arquivos;

    public Noticia() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_noticia")
    @Column(name="id_noticia")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="titulo_noticia")
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    @Column(name="desc_noticia")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Column(name="data_noticia")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
        
    @ManyToOne
    @JoinColumn(name="id_funcionario_noticia")
    public Funcionario getAutor() {
        return autor;
    }

    public void setAutor(Funcionario autor) {
        this.autor = autor;
    }
    
    @Column(name="data_edit_noticia")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getUltimaAlteracao() {
        return ultimaAlteracao;
    }

    public void setUltimaAlteracao(Date ultimaAlteracao) {
        this.ultimaAlteracao = ultimaAlteracao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_func_edit_noticia")
    public Funcionario getFuncionarioAlteracao() {
        return funcionarioAlteracao;
    }

    public void setFuncionarioAlteracao(Funcionario funcionarioAlteracao) {
        this.funcionarioAlteracao = funcionarioAlteracao;
    }
    
    @OneToMany(mappedBy = "noticia", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("id_arquivo")
    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }    
}
