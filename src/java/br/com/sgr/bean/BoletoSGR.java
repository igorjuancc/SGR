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
@Table(name="tb_boleto")
@SequenceGenerator(name="seq_boleto", sequenceName="tb_boleto_id_boleto_seq")
public class BoletoSGR implements Serializable{
    private int id;
    private int tipo;
    private Date dataEmissao;
    private Date dataVencimento;
    private Date dataReferencia;
    private Morador morador;
    private double valorBoleto;
    private double valorMulta;
    private Financeiro financeiro;
    private Notificacao notificacao;

    public BoletoSGR() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_boleto")
    @Column(name="id_boleto")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="tipo_boleto")
    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    @Column(name="data_emissao_boleto")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
    
    @Column(name="data_vencimento_boleto")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
        
    @ManyToOne
    @JoinColumn(name="id_morador_boleto")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @Column(name="valor_boleto")
    public double getValorBoleto() {
        return valorBoleto;
    }

    public void setValorBoleto(double valorBoleto) {
        this.valorBoleto = valorBoleto;
    }
    
    @Column(name="valor_multa_boleto")
    public double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta;
    }
    
    @Column(name="data_referencia_boleto")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    @OneToOne(mappedBy = "boleto")    
    public Financeiro getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(Financeiro financeiro) {
        this.financeiro = financeiro;
    }
    
    @OneToOne(mappedBy = "boleto")  
    public Notificacao getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.id;
        hash = 53 * hash + this.tipo;
        hash = 53 * hash + Objects.hashCode(this.dataEmissao);
        hash = 53 * hash + Objects.hashCode(this.dataVencimento);
        hash = 53 * hash + Objects.hashCode(this.dataReferencia);
        hash = 53 * hash + Objects.hashCode(this.morador);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.valorBoleto) ^ (Double.doubleToLongBits(this.valorBoleto) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.valorMulta) ^ (Double.doubleToLongBits(this.valorMulta) >>> 32));
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
        final BoletoSGR other = (BoletoSGR) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.tipo != other.tipo) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorBoleto) != Double.doubleToLongBits(other.valorBoleto)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorMulta) != Double.doubleToLongBits(other.valorMulta)) {
            return false;
        }
        if (!Objects.equals(this.dataEmissao, other.dataEmissao)) {
            return false;
        }
        if (!Objects.equals(this.dataVencimento, other.dataVencimento)) {
            return false;
        }
        if (!Objects.equals(this.dataReferencia, other.dataReferencia)) {
            return false;
        }
        if (!Objects.equals(this.morador, other.morador)) {
            return false;
        }
        return true;
    }    
}
