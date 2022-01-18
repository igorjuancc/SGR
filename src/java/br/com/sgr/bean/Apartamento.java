package br.com.sgr.bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tb_apartamento")
public class Apartamento implements Serializable {

    private int id;
    private String bloco;
    private int andar;
    private List<Morador> moradores;
    private List<Visita> visitas;
    private List<Vaga> vagas;

    public Apartamento() {
    }

    @Id
    @Column(name = "id_apartamento")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "bloco_apartamento")
    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    @Column(name = "andar_apartamento")
    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        this.andar = andar;
    }

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Morador> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Morador> moradores) {
        this.moradores = moradores;
    }

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Visita> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas = visitas;
    }
    
    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Vaga> getVagas() {
        return vagas;
    }

    public void setVagas(List<Vaga> vagas) {
        this.vagas = vagas;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.id;
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
        final Apartamento other = (Apartamento) obj;
        return this.id == other.id;
    }
}