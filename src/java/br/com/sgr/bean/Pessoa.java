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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_pessoa")
@SequenceGenerator(name = "seq_pessoa", sequenceName = "tb_pessoa_id_pessoa_seq")
public class Pessoa implements Serializable {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private Date dataNascimento;
    private String sexo = "M";
    private String fone;
    private String celular;
    private Arquivo imagem;
    private List<RegistroES> regEntradaSaida;

    public Pessoa() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
    @Column(name = "id_pessoa")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "nome_pessoa")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "cpf_pessoa")
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Column(name = "email_pessoa")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "data_pessoa")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Column(name = "sexo_pessoa")
    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    @Column(name = "fone_pessoa")
    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    @Column(name = "cel_pessoa")
    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_arquivo_pessoa", updatable = true)
    public Arquivo getImagem() {
        return imagem;
    }

    public void setImagem(Arquivo imagem) {
        this.imagem = imagem;
    }
    
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<RegistroES> getRegEntradaSaida() {
        return regEntradaSaida;
    }

    public void setRegEntradaSaida(List<RegistroES> regEntradaSaida) {
        this.regEntradaSaida = regEntradaSaida;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
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
        final Pessoa other = (Pessoa) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }    
}
