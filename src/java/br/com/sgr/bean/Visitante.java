package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_visitante")
@Table (name="tb_visitante") 
public class Visitante extends Pessoa implements Serializable  { 
    private Morador moradorCadastro;
    private Date dataCadastro;
    private List<Visita> visitas;
    
    @ManyToOne
    @JoinColumn(name="id_morador_cadastro_visitante")
    public Morador getMoradorCadastro() {
        return moradorCadastro;
    }

    public void setMoradorCadastro(Morador moradorCadastro) {
        this.moradorCadastro = moradorCadastro;
    }
    
    @Column(name = "data_cadastro_visitante")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "visitantes", fetch = FetchType.LAZY)
    public List<Visita> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas = visitas;
    }    
}
