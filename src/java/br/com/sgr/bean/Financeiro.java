package br.com.sgr.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="tb_financeiro")
@SequenceGenerator(name="seq_financeiro", sequenceName="tb_financeiro_id_financeiro_seq")
public class Financeiro implements Serializable {
    private int id;
    private int tipo;
    private double valor;
    private String descricao; 
    private Date dataRegistro;
    private CategoriaFinanceiro categoria;    
    private Funcionario funcionario;
    private BoletoSGR boleto;
    
    public Financeiro() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_financeiro")
    @Column(name="id_financeiro")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="tipo_financeiro")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="valor_financeiro")
    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
           
    @Column(name="desc_financeiro")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Column(name="data_financeiro")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
    
    @ManyToOne
    @JoinColumn(name="id_categoria_financeiro")
    public CategoriaFinanceiro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFinanceiro categoria) {
        this.categoria = categoria;
    }
    
    @ManyToOne
    @JoinColumn(name="id_funcionario_financeiro")
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }  
        
    @OneToOne    
    @JoinColumn(name="id_boleto_financeiro")
    public BoletoSGR getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoSGR boleto) {
        this.boleto = boleto;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.id;
        hash = 47 * hash + this.tipo;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.valor) ^ (Double.doubleToLongBits(this.valor) >>> 32));
        hash = 47 * hash + Objects.hashCode(this.descricao);
        hash = 47 * hash + Objects.hashCode(this.dataRegistro);
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
        final Financeiro other = (Financeiro) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.tipo != other.tipo) {
            return false;
        }
        if (Double.doubleToLongBits(this.valor) != Double.doubleToLongBits(other.valor)) {
            return false;
        }
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (!Objects.equals(this.dataRegistro, other.dataRegistro)) {
            return false;
        }
        return true;
    }
}
