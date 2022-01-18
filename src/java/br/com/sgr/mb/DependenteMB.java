package br.com.sgr.mb;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.facade.DependenteFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@ManagedBean
@Named(value = "dependenteMB")
@ViewScoped
public class DependenteMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Morador dependente;
    private UploadedFile imagem;
    private Boolean cadastroConcluido;
    private Pessoa pessoaTemp;
    private byte[] previewImagem;

    private LazyDataModel<Morador> dependentes;
    private FiltroBD filtroDependentes;

    @PostConstruct
    public void init() {
        try {
            this.filtroDependentes = new FiltroBD();
            iniciaListaDependentes();
            this.dependente = new Morador();
            File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
            this.previewImagem = Files.readAllBytes(objFile.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaDependentes() {
        dependentes = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaDependentesFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroDependentes.setPrimeiroRegistro(first);
                filtroDependentes.setQntdRegistros(Long.valueOf(pageSize));
                filtroDependentes.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroDependentes.setPropriedadeOrdenacao(sortField);
                setRowCount(DependenteFacade.totalDependentes(login.getMorador()));
                listaDependentesFiltro = DependenteFacade.dependenteFiltro(filtroDependentes, login.getMorador());
                return listaDependentesFiltro;
            }
        };
    }

    public void cadastraDependente() {
        try {
            this.dependente.setApartamento(this.login.getMorador().getApartamento());
            DependenteFacade.cadastrarDependente(this.dependente, this.imagem);
            this.cadastroConcluido = true;
        } catch (MoradorException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de dependente");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de dependente");
        }
    }

    public void atualizarDependente() {
        try {
            DependenteFacade.atualizarDependente(this.dependente, this.imagem);
            this.cadastroConcluido = true;
        } catch (MoradorException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de dependente");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de dependente");
        }
    }

    public void atualizarStatusDependente(Morador dependente) {
        FacesContext ctx = null;
        String mensagem = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.atualizarStatusMorador(dependente);

            switch (dependente.getStatus()) {
                case 1:
                    mensagem = "Definido acesso para dependente: " + dependente.getNome();
                    break;
                case 2:
                    mensagem = "Definido acesso limitado ao sistema para: " + dependente.getNome();
                    break;
                case 3:
                    mensagem = "Definido acesso total ao sistema para: " + dependente.getNome();
                    break;
                case 4:
                    mensagem = "Morador " + dependente.getNome() + " desligado";
                    break;
            }
            iniciaListaDependentes();
            ctx.addMessage(null, SgrUtil.emiteMsg(mensagem, 1));
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao modificar status de dependente");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao modificar status de dependente");
        }
    }

    public void removerDependente(Morador dependente) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            DependenteFacade.removerDependente(dependente);
            FacesMessage msg = SgrUtil.emiteMsg("Depedente removído: " + dependente.getNome(), 1);
            ctx.addMessage(null, msg);
            iniciaListaDependentes();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover dependente");
        }
    }

    public void buscaPessoaCpf() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            this.pessoaTemp = DependenteFacade.verificaCpfDependente(this.dependente.getCpf());

            if (this.pessoaTemp != null) {
                PrimeFaces.current().ajax().update("dialogConfirma");
                PrimeFaces.current().executeScript("PF('dialogConfirma').show()");
            } else {
                ctx.addMessage(null, SgrUtil.emiteMsg("CPF Valido", 1));
            }
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar CPF de dependente");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar CPF de dependente");
        }
    }

    public void limparCampos() {
        try {
            this.dependente = new Morador();
            this.pessoaTemp = new Morador();
            this.dependente.setApartamento(this.login.getMorador().getApartamento());
            this.imagem = null;
            File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
            this.previewImagem = Files.readAllBytes(objFile.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de dependente");
        }
    }

    //Cancela cadastro de dependente no qual objeto pessoa ja existe no BD
    public void cancelarNovoDependente() {
        this.dependente.setCpf("");
        this.cadastroConcluido = null;
        this.pessoaTemp = new Pessoa();
    }

    //Prosseguir cadastro de dependente no qual objeto pessoa ja existe no BD
    public void prosseguirCadastroDependente() {
        try {
            this.dependente.setCelular(this.pessoaTemp.getCelular());
            this.dependente.setCpf(this.pessoaTemp.getCpf());
            this.dependente.setDataNascimento(this.pessoaTemp.getDataNascimento());
            this.dependente.setEmail(this.pessoaTemp.getEmail());
            this.dependente.setFone(this.pessoaTemp.getFone());
            this.dependente.setId(this.pessoaTemp.getId());
            this.dependente.setNome(this.pessoaTemp.getNome());
            this.dependente.setSexo(this.pessoaTemp.getSexo());
            this.dependente.setImagem(this.pessoaTemp.getImagem());
            if ((this.dependente.getImagem() != null) && (this.dependente.getImagem().getId() != 0)) {
                File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\"
                        + this.dependente.getImagem().getId() + this.dependente.getImagem().getExtensao());
                this.previewImagem = Files.readAllBytes(objFile.toPath());
            }
            this.imagem = null;
            this.cadastroConcluido = null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Problemas ao cadastrar pessoa já existente");
        }
    }

    public void setEditDependente(Morador dependente) {
        try {
            this.pessoaTemp = new Pessoa();
            this.dependente = dependente;
            this.pessoaTemp.setCpf(this.dependente.getCpf());
            if ((this.dependente.getImagem() != null) && (this.dependente.getImagem().getId() != 0)) {
                File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\"
                        + this.dependente.getImagem().getId() + this.dependente.getImagem().getExtensao());
                this.previewImagem = Files.readAllBytes(objFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de dependente");
        }
    }

    public void redirecionar(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
        }
    }

    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }

    public Morador getDependente() {
        return dependente;
    }

    public void setDependente(Morador dependente) {
        this.dependente = dependente;
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
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
            Arquivo arq = (this.dependente.getImagem() == null) ? new Arquivo()
                    : this.dependente.getImagem();

            this.imagem = event.getFile();
            this.previewImagem = event.getFile().getContents();

            arq.setExtensao(event.getFile().getFileName().toUpperCase());
            arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                    arq.getExtensao().length()));
            this.dependente.setImagem(arq);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar upload de imagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public LazyDataModel<Morador> getDependentes() {
        return dependentes;
    }

    public void setDependentes(LazyDataModel<Morador> dependentes) {
        this.dependentes = dependentes;
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
