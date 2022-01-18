package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="tb_salao_festa")
@SequenceGenerator(name="seq_salao", sequenceName="tb_salao_festa_id_req_salao_seq")
public class Salao implements Serializable {
    private int id;
    private Date dataReserva;
    private Morador morador;
    private Funcionario funcionario;

    public Salao() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_salao")
    @Column(name="id_req_salao")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="data_salao")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }
    
    @ManyToOne
    @JoinColumn(name="id_morador_salao")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_salao")
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
