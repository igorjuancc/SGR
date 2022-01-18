package br.com.sgr.bean;

import java.io.Serializable;
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

@Entity
@Table(name="tb_opc_questao")
@SequenceGenerator(name="seq_opc_questao", sequenceName="tb_opc_questao_id_opc_questao_seq")
public class OpcQuestao implements Serializable {
    private int id;
    private String descricao;
    private Questao questao;
    private List<Voto> votos;

    public OpcQuestao() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_opc_questao")
    @Column(name="id_opc_questao")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="desc_opc_questao")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @ManyToOne
    @JoinColumn(name="id_questao")
    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }
    
    @OneToMany(mappedBy = "opcao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Voto> getVotos() {
        return votos;
    }

    public void setVotos(List<Voto> votos) {
        this.votos = votos;
    }
}
