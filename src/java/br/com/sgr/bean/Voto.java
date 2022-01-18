package br.com.sgr.bean;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_voto")
@SequenceGenerator(name="seq_voto", sequenceName="tb_voto_id_voto_seq")
public class Voto implements Serializable {
    private int id;
    private Morador morador;
    private OpcQuestao opcao;

    public Voto() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_voto")
    @Column(name="id_voto")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToOne
    @JoinColumn(name="id_morador")
    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
    
    @ManyToOne
    @JoinColumn(name="id_opc_questao")
    public OpcQuestao getOpcao() {
        return opcao;
    }

    public void setOpcao(OpcQuestao opcao) {
        this.opcao = opcao;
    }
}
