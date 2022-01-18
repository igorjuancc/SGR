package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_visita")
@Table(name="tb_visita")
@SequenceGenerator(name="seq_visita", sequenceName="tb_visita_id_visita_seq")
public class Visita implements Serializable {
    private int id;
    private Apartamento apartamento;
    private Date dataInicio;
    private Date dataFim;
    private List<Visitante> visitantes;
    
    public Visita() {        
    }     
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_visita")
    @Column(name="id_visita")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    
    
    @ManyToOne
    @JoinColumn(name="id_apartamento")
    public Apartamento getApartamento() {
        return apartamento;
    }

    public void setApartamento(Apartamento apartamento) {
        this.apartamento = apartamento;
    }
    
    @Column(name="data_inicio")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    @Column(name="data_fim")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
        
    @ManyToMany(targetEntity=br.com.sgr.bean.Pessoa.class, cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinTable(name="tb_visita_pessoa", joinColumns={@JoinColumn(name="id_visita")},inverseJoinColumns={@JoinColumn(name="id_visitante")})   
    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(List<Visitante> visitantes) {
        this.visitantes = visitantes;
    }  
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.id;
        hash = 31 * hash + Objects.hashCode(this.apartamento);
        hash = 31 * hash + Objects.hashCode(this.dataInicio);
        hash = 31 * hash + Objects.hashCode(this.dataFim);
        hash = 31 * hash + Objects.hashCode(this.visitantes);
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
        final Visita other = (Visita) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
