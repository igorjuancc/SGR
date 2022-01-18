package br.com.sgr.bean;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name="id_veiculo")
@Table(name = "tb_veiculo")
@SequenceGenerator(name="seq_veiculo", sequenceName="tb_veiculo_id_veiculo_seq")
public class Veiculo implements Serializable{
    private int id;
    private String placa;
    private String cor;
    private int ano;
    private String marca;
    private String modelo;
    private Morador morador;

    public Veiculo() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_veiculo")
    @Column(name="id_veiculo")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="placa_veiculo")
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    @Column(name="cor_veiculo")
    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
    
    @Column(name="ano_veiculo")
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
    
    @ManyToOne
    @JoinColumn(name="id_morador")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @Column(name="marca_veiculo")
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    @Column(name="modelo_veiculo")
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
