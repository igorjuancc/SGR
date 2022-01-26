package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.Seguranca;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
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
@Named(value = "moradorMB")
@ViewScoped
public class MoradorMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private String senhaAtual;
    private String senhaAtualBkp;
    private String confirmaSenha;
    private Boolean cadastroConcluido;
    private Morador morador;
    private Pessoa pessoaTemp;
    private UploadedFile imagem;
    private byte[] previewImagem;
    private List<String> blocos;
    private List<Apartamento> listaApartamento;

    @PostConstruct
    public void init() {
        try {
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/CadastroMorador.xhtml":
                    if (this.login.getMorador().getNome() == null) {
                        this.morador = new Morador();
                        this.blocos = ApartamentoFacade.blocosApartamentos();
                        morador.getApartamento().setBloco(blocos.get(0));
                        buscaApartamentoBloco();
                        File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
                        this.previewImagem = Files.readAllBytes(objFile.toPath());
                    }
                    break;
                case "/morador/MoradorNovaSenha.xhtml":
                    this.senhaAtualBkp = this.login.getMorador().getSenha();
                    this.morador = this.login.getMorador();
                    this.pessoaTemp = new Morador();
                    break;
                case "/morador/MoradorDados.xhtml":
                    this.morador = MoradorFacade.moradorPorCpf(this.login.getMorador().getCpf());
                    this.senhaAtualBkp = this.morador.getSenha();
                    this.pessoaTemp = new Pessoa();

                    File objFile = null;
                    if (this.morador.getImagem() == null) {
                        objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
                    } else {
                        objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\" + Integer.toString(this.morador.getImagem().getId()) + this.morador.getImagem().getExtensao());
                    }
                    this.previewImagem = Files.readAllBytes(objFile.toPath());
                    break;
                case "/MoradorEsqSenha.xhtml":
                    this.morador = new Morador();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Problemas ao inicializar página "
                    + FacesContext.getCurrentInstance().getViewRoot().getViewId());
        }
    }

    public void buscaPessoaCpf() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            this.pessoaTemp = MoradorFacade.verificaCpfMorador(this.morador.getCpf());

            if (this.pessoaTemp != null) {
                PrimeFaces.current().ajax().update("formMorador:dialogConfirma");
                PrimeFaces.current().executeScript("PF('dialogConfirma').show()");
            } else {
                ctx.addMessage(null, SgrUtil.emiteMsg("CPF Valido", 1));
            }
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar CPF de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar CPF de morador");
        }
    }

    //Prosseguir cadastro de morador no qual objeto pessoa ja existe no BD
    public void prosseguirCadastroMorador() {
        try {
            this.morador.setCelular(this.pessoaTemp.getCelular());
            this.morador.setCpf(this.pessoaTemp.getCpf());
            this.morador.setDataNascimento(this.pessoaTemp.getDataNascimento());
            this.morador.setEmail(this.pessoaTemp.getEmail());
            this.morador.setFone(this.pessoaTemp.getFone());
            this.morador.setId(this.pessoaTemp.getId());
            this.morador.setNome(this.pessoaTemp.getNome());
            this.morador.setSexo(this.pessoaTemp.getSexo());
            this.morador.setImagem(this.pessoaTemp.getImagem());

            if (this.morador.getImagem() != null) {
                File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\"
                        + this.morador.getImagem().getId() + this.morador.getImagem().getExtensao());
                this.previewImagem = Files.readAllBytes(objFile.toPath());
            }
            this.cadastroConcluido = null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Problemas ao cadastrar pessoa já existente");
        }
    }

    //Cancela cadastro de morador no qual objeto pessoa ja existe no BD
    public void cancelarNovoMorador() {
        this.morador.setCpf("");
        this.pessoaTemp = new Pessoa();
        this.cadastroConcluido = null;
    }

    public void cadastrarMorador() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            if (!this.morador.getSenha().equals(confirmaSenha)) {
                throw new MoradorException("Senha e confirmação não conferem");
            } else {
                MoradorFacade.cadastrarMorador(this.morador, this.imagem);
                this.cadastroConcluido = true;
            }
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de morador");
        }
    }

    public void atualizarMorador() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.atualizarMorador(this.morador, this.imagem);
            ctx.addMessage(null, SgrUtil.emiteMsg("Dados de morador atualizado com sucesso", 1));
            this.login.setMorador(this.morador);
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de morador");
        }
    }

    public void alterarSenha() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            this.senhaAtual = Seguranca.md5(this.senhaAtual);
            this.morador.setSenha(Seguranca.md5(this.morador.getSenha()));
            this.confirmaSenha = Seguranca.md5(confirmaSenha);

            if (!(this.senhaAtual.equals(this.senhaAtualBkp))) {
                ctx.addMessage(null, SgrUtil.emiteMsg("Senha atual não confere!", 3));
            } else if (!this.morador.getSenha().equals(confirmaSenha)) {
                ctx.addMessage(null, SgrUtil.emiteMsg("Senha e Confirmação não conferem!", 3));
            } else {
                MoradorFacade.alterarSenhaMorador(this.morador);
                this.cadastroConcluido = true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar senha de morador");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de morador");
        }
    }

    public void recuperarAcesso() {
        try {
            MoradorFacade.recuperarAcesso(this.morador, 1);
            this.cadastroConcluido = true;
        } catch (MoradorException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao recuperar acesso de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao recuperar acesso de morador");
        }
    }

    public void buscaApartamentoBloco() {
        this.listaApartamento = ApartamentoFacade.apartamentosVagosPorBloco(morador.getApartamento().getBloco());
    }

    public void redirecionar(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String imprimeCPF(String cpf) {
        try {
            return SgrUtil.imprimeCPF(cpf);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao imprimir CPF de morador");
            return null;
        }
    }

    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }

    public String getConfirmaSenha() {
        return confirmaSenha;
    }

    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public List<String> getBlocos() {
        return blocos;
    }

    public void setBlocos(List<String> blocos) {
        this.blocos = blocos;
    }

    public List<Apartamento> getListaApartamento() {
        return listaApartamento;
    }

    public void setListaApartamento(List<Apartamento> listaApartamento) {
        this.listaApartamento = listaApartamento;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public UploadedFile getImagem() {
        return imagem;
    }

    public void setImagem(UploadedFile imagem) {
        this.imagem = imagem;
    }

    public void cancelaImagem() {
        this.imagem = null;
    }

    public void uploadImagem(FileUploadEvent event) {
        try {
            Arquivo arq = (this.morador.getImagem() == null) ? new Arquivo()
                    : this.morador.getImagem();

            this.imagem = event.getFile();
            this.previewImagem = event.getFile().getContents();

            arq.setExtensao(event.getFile().getFileName().toUpperCase());
            arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                    arq.getExtensao().length()));
            this.morador.setImagem(arq);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar upload de imagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public Pessoa getPessoaTemp() {
        return pessoaTemp;
    }

    public void setPessoaTemp(Pessoa pessoaTemp) {
        this.pessoaTemp = pessoaTemp;
    }

    public byte[] getPreviewImagem() {
        return previewImagem;
    }

    public void setPreviewImagem(byte[] previewImagem) {
        this.previewImagem = previewImagem;
    }
}
