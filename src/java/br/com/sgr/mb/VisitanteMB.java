package br.com.sgr.mb;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.VisitanteException;
import br.com.sgr.facade.VisitanteFacade;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@Named(value = "visitanteMB")
@ViewScoped
public class VisitanteMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Visitante visitante;
    private Pessoa pessoaTemp;
    private Boolean cadastroConcluido;
    private UploadedFile imagem;
    private List<Visitante> visitantesCadastrados;
    private List<Visitante> visitantesPrazo;
    private byte[] previewImagem;

    @PostConstruct
    public void init() {
        try {
            this.visitantesCadastrados = VisitanteFacade.visitantesEditCadastroMorador(this.login.getMorador());
            this.visitantesPrazo = VisitanteFacade.visitantesVisitaPrazo(this.login.getMorador());
            if ((this.visitantesCadastrados != null) && (this.visitantesPrazo != null)) {
                this.visitantesPrazo.removeAll(this.visitantesCadastrados);
            }
            this.visitante = new Visitante();
            File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
            this.previewImagem = Files.readAllBytes(objFile.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void cadastraVisitante() {
        try {
            this.visitante.setMoradorCadastro(this.login.getMorador());
            VisitanteFacade.cadastrarVisitante(this.visitante, this.imagem);
            this.cadastroConcluido = true;
        } catch (VisitanteException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public void deletarVisitante(Visitante visitante) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            VisitanteFacade.deletarVisitante(visitante);
            ctx.addMessage(null, SgrUtil.emiteMsg(visitante.getNome() + " Removído com sucesso!", 1));
            this.visitantesCadastrados = VisitanteFacade.visitantesEditCadastroMorador(this.login.getMorador());
            this.visitantesPrazo = VisitanteFacade.visitantesVisitaPrazo(this.login.getMorador());
        } catch (VisitanteException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public void buscaPessoaCpf() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            this.pessoaTemp = VisitanteFacade.verificaCpfVisitante(this.visitante.getCpf());
            if ((this.pessoaTemp != null) && (this.pessoaTemp.getId() != 0)) {
                PrimeFaces.current().ajax().update("dialogConfirma");
                PrimeFaces.current().executeScript("PF('dialogConfirma').show()");
            } else {
                ctx.addMessage(null, SgrUtil.emiteMsg("CPF Valido", 1));
                this.pessoaTemp = null;
            }
        } catch (VisitanteException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public void limparCampos() {
        try {
            this.visitante = new Visitante();
            this.pessoaTemp = new Pessoa();
            this.imagem = null;
            File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
            this.previewImagem = Files.readAllBytes(objFile.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    //Cancela cadastro de visitante no qual objeto pessoa ja existe no BD
    public void cancelarNovoVisitante() {
        this.visitante.setCpf(null);
        this.cadastroConcluido = null;
        this.pessoaTemp = new Visitante();
    }

    //Prosseguir cadastro de visitante no qual objeto pessoa ja existe no BD
    public void prosseguirCadastroVisitante() {
        try {
            this.visitante.setCelular(this.pessoaTemp.getCelular());
            this.visitante.setCpf(this.pessoaTemp.getCpf());
            this.visitante.setDataNascimento(this.pessoaTemp.getDataNascimento());
            this.visitante.setEmail(this.pessoaTemp.getEmail());
            this.visitante.setFone(this.pessoaTemp.getFone());
            this.visitante.setId(this.pessoaTemp.getId());
            this.visitante.setNome(this.pessoaTemp.getNome());
            this.visitante.setSexo(this.pessoaTemp.getSexo());
            this.visitante.setImagem(this.pessoaTemp.getImagem());
            if ((this.visitante.getImagem() != null) && (this.visitante.getImagem().getId() != 0)) {
                File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\"
                        + this.visitante.getImagem().getId() + this.visitante.getImagem().getExtensao());
                this.previewImagem = Files.readAllBytes(objFile.toPath());
            }
            this.imagem = null;
            this.cadastroConcluido = null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public void setEditarVisitante(Visitante visitante) {
        try {
            this.visitante = visitante;
            if ((this.visitante.getImagem() != null) && (this.visitante.getImagem().getId() != 0)) {
                File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\"
                        + this.visitante.getImagem().getId() + this.visitante.getImagem().getExtensao());
                this.previewImagem = Files.readAllBytes(objFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public Pessoa getPessoaTemp() {
        return pessoaTemp;
    }

    public void setPessoaTemp(Pessoa pessoaTemp) {
        this.pessoaTemp = pessoaTemp;
    }

    public UploadedFile getImagem() {
        return imagem;
    }

    public void setImagem(UploadedFile imagem) {
        this.imagem = imagem;
    }

    public void uploadImagem(FileUploadEvent event) {
        try {
            Arquivo arq = (this.visitante.getImagem() == null) ? new Arquivo()
                    : this.visitante.getImagem();

            this.imagem = event.getFile();
            this.previewImagem = event.getFile().getContents();

            arq.setExtensao(event.getFile().getFileName().toUpperCase());
            arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                    arq.getExtensao().length()));
            this.visitante.setImagem(arq);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar upload de imagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void cancelaImagem() {
        this.imagem = null;
    }

    public List<Visitante> getVisitantesCadastrados() {
        return visitantesCadastrados;
    }

    public void setVisitantesCadastrados(List<Visitante> visitantesCadastrados) {
        this.visitantesCadastrados = visitantesCadastrados;
    }

    public List<Visitante> getVisitantesPrazo() {
        return visitantesPrazo;
    }

    public void setVisitantesPrazo(List<Visitante> visitantesPrazo) {
        this.visitantesPrazo = visitantesPrazo;
    }

    public byte[] getPreviewImagem() {
        return previewImagem;
    }

    public void setPreviewImagem(byte[] previewImagem) {
        this.previewImagem = previewImagem;
    }
}
