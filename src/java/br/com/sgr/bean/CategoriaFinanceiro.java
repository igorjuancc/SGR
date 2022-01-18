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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="tb_categoria_financeiro")
@SequenceGenerator(name="seq_categoria_financeiro", sequenceName="tb_categoria_financeiro_id_categoria_financeiro_seq")
public class CategoriaFinanceiro implements Serializable {
    private int id;
    private String descricao;
    private List<Financeiro> financeiros;

    public CategoriaFinanceiro() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_categoria_financeiro")
    @Column(name="id_categoria_financeiro")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="desc_categoria")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @OneToMany(mappedBy="categoria", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Financeiro> getFinanceiros() {
        return financeiros;
    }

    public void setFinanceiros(List<Financeiro> financeiros) {
        this.financeiros = financeiros;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CategoriaFinanceiro other = (CategoriaFinanceiro) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }   
}
