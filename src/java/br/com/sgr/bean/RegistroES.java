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
@Table(name = "tb_reg_entrada_saida_pessoa")
@SequenceGenerator(name="seq_entrada_saida", sequenceName="tb_reg_entrada_saida_pessoa_id_reg_entrada_saida_pessoa_seq")
public class RegistroES implements Serializable {
    private int id;
    private Pessoa pessoa;
    private Date dataEntrada;
    private Date dataSaida;
    private String tipoPessoa;  
    private Funcionario funcionarioEntrada;
    private Funcionario funcionarioSaida;

    public RegistroES() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_entrada_saida")
    @Column(name="id_reg_entrada_saida_pessoa")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToOne
    @JoinColumn(name="id_pessoa_reg_entrada_saida_pessoa")
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
    
    @Column(name = "data_entrada_reg_entrada_saida_pessoa")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }
    
    @Column(name = "data_saida_reg_entrada_saida_pessoa")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }
    
    @Column(name = "tipo_pessoa_reg_entrada_saida_pessoa")
    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_reg_entrada_pessoa")
    public Funcionario getFuncionarioEntrada() {
        return funcionarioEntrada;
    }

    public void setFuncionarioEntrada(Funcionario funcionarioEntrada) {
        this.funcionarioEntrada = funcionarioEntrada;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_reg_saida_pessoa")
    public Funcionario getFuncionarioSaida() {
        return funcionarioSaida;
    }

    public void setFuncionarioSaida(Funcionario funcionarioSaida) {
        this.funcionarioSaida = funcionarioSaida;
    }
}
