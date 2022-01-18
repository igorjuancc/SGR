/*

Operação    - [1]Adicionar [2]Remover [3]Atualizar [4]Aprovar [5]Desligar [6]Modificar Acesso
Tipo objeto - [1]Morador [2]Funcionario [3]Visitante [4]Noticia

*/

package br.com.sgr.logbean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@PrimaryKeyJoinColumn(name="id_log_sistema")
@Table(name = "tb_log_sistema")
@SequenceGenerator(name="seq_log", sequenceName="tb_log_sistema_id_log_sistema_seq")
public class LogBD implements Serializable{
    private int id;
    private int operacao;
    private int idUsuario;
    private int idObjeto;
    private int tipoObjeto;
    private String detalhe;
    private Date dataLog;
    

    public LogBD() {
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seq_log")
    @Column(name="id_log_sistema")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="id_operacao_log_sistema")
    public int getOperacao() {
        return operacao;
    }

    public void setOperacao(int operacao) {
        this.operacao = operacao;
    }
    
    @Column(name="id_usuario_log_sistema")
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    @Column(name="id_objeto_log_sistema")
    public int getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }
    
    @Column(name="tipo_objeto_log_sistema")
    public int getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(int tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }
    
    @Column(name="detalhe_log_sistema")
    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
    
    @Column(name="data_log_sistema")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDataLog() {
        return dataLog;
    }

    public void setDataLog(Date dataLog) {
        this.dataLog = dataLog;
    }
}
