package br.com.sgr.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
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
@Table(name="tb_tipo_atendimento")
@SequenceGenerator(name="seq_tipo_atendimento", sequenceName="tb_tipo_atendimento_id_tipo_atendimento_seq")
public class TipoAtendimento implements Serializable {
    private int id;
    private String descricao;
    private List<Atendimento> atendimentos;

    public TipoAtendimento() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_tipo_atendimento")
    @Column(name="id_tipo_atendimento")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="desc_tipo_atendimento")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @OneToMany(mappedBy="tipo", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.descricao);
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
        final TipoAtendimento other = (TipoAtendimento) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        return true;
    }    
}
