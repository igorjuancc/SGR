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
@Table(name="tb_assembleia")
@SequenceGenerator(name="seq_assembleia", sequenceName="tb_assembleia_id_assembleia_seq")
public class Assembleia implements Serializable {
    private int id;
    private int tipo;
    private String parecer;
    private int encerramento;
    private Date dataInicio;
    private Date dataFim;    
    private Funcionario sindico;
    private Morador presidente;
    private List<Questao> questoes;

    public Assembleia() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_assembleia")
    @Column(name="id_assembleia")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="tipo_assembleia")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="parecer_assembleia")
    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }
    
    @Column(name="fim_assembleia")
    public int getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(int encerramento) {
        this.encerramento = encerramento;
    }
    
    @Column(name="data_inicio_assembleia")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    @Column(name="data_fim_assembleia")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
    
    @ManyToOne
    @JoinColumn(name="id_criador_assembleia")
    public Funcionario getSindico() {
        return sindico;
    }

    public void setSindico(Funcionario sindico) {
        this.sindico = sindico;
    }
    
    @ManyToOne
    @JoinColumn(name="id_presid_assembleia")
    public Morador getPresidente() {
        return presidente;
    }

    public void setPresidente(Morador presidente) {
        this.presidente = presidente;
    }    
    
    @OneToMany(mappedBy = "assembleia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }
}
