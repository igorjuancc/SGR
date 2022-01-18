package br.com.sgr.mb;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Funcao;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.exception.FuncionarioException;
import br.com.sgr.facade.FuncaoFacade;
import br.com.sgr.facade.FuncionarioFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.Seguranca;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
@Named(value = "funcionarioMB")
@ViewScoped
public class FuncionarioMB implements Serializable {
    
    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Funcionario funcionario;
    private Funcionario funcionarioTemp;
    private UploadedFile imagem;
    private byte[] imagemPreview;
    private Boolean cadastroConcluido;
    private Boolean atualizar;
    private String confirmaSenha;
    private String bkpSenha;
    private List<Funcao> listaFuncao;
    
    private LazyDataModel<Funcionario> listaFuncionarioAtv;
    private LazyDataModel<Funcionario> listaFuncionarioDstv;
    private LazyDataModel<Funcionario> listaFuncionarioNovo;
    private FiltroBD filtroFuncionario;
    
    @PostConstruct
    public void init() {
        try {
            File objFile;
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoFuncionario.xhtml":
                    this.funcionario = new Funcionario();
                    this.funcionarioTemp = new Funcionario();
                    objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
                    this.imagemPreview = Files.readAllBytes(objFile.toPath());
                    this.listaFuncao = FuncaoFacade.funcaoLista();
                    this.filtroFuncionario = new FiltroBD();
                    iniciaListaFuncionarioAtv();
                    iniciaListaFuncionarioDstv();
                    iniciaListaFuncionarioNovo();
                    break;
                case "/sindico/SindicoDados.xhtml":
                    this.funcionario = FuncionarioFacade.funcionarioPorId(this.login.getFuncionario().getId());
                    this.bkpSenha = this.funcionario.getSenha();
                    this.funcionarioTemp = new Funcionario();
                    
                    if (this.funcionario.getImagem() == null) {
                        objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
                    } else {
                        objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\" + Integer.toString(this.funcionario.getImagem().getId()) + this.funcionario.getImagem().getExtensao());
                    }
                    this.imagemPreview = Files.readAllBytes(objFile.toPath());
                    break;
                case "/porteiro/PorteiroDados.xhtml":
                    this.funcionario = FuncionarioFacade.funcionarioPorId(this.login.getFuncionario().getId());
                    this.bkpSenha = this.funcionario.getSenha();
                    this.funcionarioTemp = new Funcionario();
                    
                    if (this.funcionario.getImagem() == null) {
                        objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
                    } else {
                        objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\" + Integer.toString(this.funcionario.getImagem().getId()) + this.funcionario.getImagem().getExtensao());
                    }
                    this.imagemPreview = Files.readAllBytes(objFile.toPath());
                    break;
                case "/PrimeiroAcessoFuncionario.xhtml":
                    this.funcionarioTemp = new Funcionario();
                    this.funcionario = FuncionarioFacade.funcionarioPorId(this.login.getFuncionario().getId());
                    this.bkpSenha = this.funcionario.getSenha();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Problemas ao inicializar página "
                    + FacesContext.getCurrentInstance().getViewRoot().getViewId());
        }
    }
    
    public void iniciaListaFuncionarioAtv() {
        listaFuncionarioAtv = new LazyDataModel<Funcionario>() {
            private static final long serialVersionUID = 1L;
            List<Funcionario> listaFuncionarioFiltro = null;
            
            @Override
            public List<Funcionario> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroFuncionario.setPrimeiroRegistro(first);
                filtroFuncionario.setQntdRegistros(Long.valueOf(pageSize));
                filtroFuncionario.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroFuncionario.setPropriedadeOrdenacao(sortField);
                setRowCount(FuncionarioFacade.totalFuncionarioStatus(1)
                        + FuncionarioFacade.totalFuncionarioStatus(3));
                listaFuncionarioFiltro = FuncionarioFacade.funcionarioAtvFiltro(filtroFuncionario);
                return listaFuncionarioFiltro;
            }
        };
    }
    
    public void iniciaListaFuncionarioDstv() {
        listaFuncionarioDstv = new LazyDataModel<Funcionario>() {
            private static final long serialVersionUID = 1L;
            List<Funcionario> listaFuncionarioFiltro = null;
            
            @Override
            public List<Funcionario> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroFuncionario.setPrimeiroRegistro(first);
                filtroFuncionario.setQntdRegistros(Long.valueOf(pageSize));
                filtroFuncionario.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroFuncionario.setPropriedadeOrdenacao(sortField);
                setRowCount(FuncionarioFacade.totalFuncionarioStatus(2));
                listaFuncionarioFiltro = FuncionarioFacade.funcionarioStatusFiltro(filtroFuncionario, 2);
                return listaFuncionarioFiltro;
            }
        };
    }
    
    public void iniciaListaFuncionarioNovo() {
        listaFuncionarioNovo = new LazyDataModel<Funcionario>() {
            private static final long serialVersionUID = 1L;
            List<Funcionario> listaFuncionarioFiltro = null;
            
            @Override
            public List<Funcionario> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroFuncionario.setPrimeiroRegistro(first);
                filtroFuncionario.setQntdRegistros(Long.valueOf(pageSize));
                filtroFuncionario.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroFuncionario.setPropriedadeOrdenacao(sortField);
                setRowCount(FuncionarioFacade.totalFuncionarioStatus(0));
                listaFuncionarioFiltro = FuncionarioFacade.funcionarioStatusFiltro(filtroFuncionario, 0);
                return listaFuncionarioFiltro;
            }
        };
    }
    
    public void buscaPessoaCpf() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            this.funcionarioTemp.setCpf(this.funcionario.getCpf());
            FuncionarioFacade.verificaCpfMorador(this.funcionarioTemp);
            if (this.funcionarioTemp.getId() != 0) {
                this.cadastroConcluido = false; //Para exibir tela de confirmação
                PrimeFaces.current().ajax().update("novoFuncionarioForm:confPessoa");
                PrimeFaces.current().executeScript("$(document.getElementById('novoFuncionarioForm:formFuncionario')).hide()");
            } else {
                this.funcionarioTemp = new Funcionario();
                ctx.addMessage(null, SgrUtil.emiteMsg("CPF Valido", 1));
            }
            PrimeFaces.current().ajax().update("novoFuncionarioForm:msg");
        } catch (FuncionarioException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                this.funcionarioTemp.setCpf(null);
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                PrimeFaces.current().ajax().update("novoFuncionarioForm:msg");
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao buscar dados de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao buscar dados de funcionário");
        }
    }

    //Cancela cadastro de funcionario no qual objeto pessoa ja existe no BD
    public void cancelarNovoFuncionario() {
        try {
            this.funcionario.setCpf(null);
            this.cadastroConcluido = null;
            this.funcionarioTemp = new Funcionario();
            PrimeFaces.current().ajax().update("novoFuncionarioForm:confPessoa");
            PrimeFaces.current().ajax().update("novoFuncionarioForm:cpf");
            PrimeFaces.current().executeScript("$(document.getElementById('novoFuncionarioForm:formFuncionario')).show()");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cancelar o processo de dados do funcionário");
        }
    }

    //Prosseguir cadastro de funcionario no qual objeto pessoa ja existe no BD
    public void prosseguirCadastroFuncionario() {
        try {
            this.funcionario.setCelular(this.funcionarioTemp.getCelular());
            this.funcionario.setCpf(this.funcionarioTemp.getCpf());
            this.funcionario.setDataNascimento(this.funcionarioTemp.getDataNascimento());
            this.funcionario.setEmail(this.funcionarioTemp.getEmail());
            this.funcionario.setFone(this.funcionarioTemp.getFone());
            this.funcionario.setId(this.funcionarioTemp.getId());
            this.funcionario.setNome(this.funcionarioTemp.getNome());
            this.funcionario.setSexo(this.funcionarioTemp.getSexo());
            this.funcionario.setImagem(this.funcionarioTemp.getImagem());
            if ((this.funcionario.getImagem() != null) && (this.funcionario.getImagem().getId() != 0)) {
                File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\"
                        + this.funcionario.getImagem().getId() + this.funcionario.getImagem().getExtensao());
                this.imagemPreview = Files.readAllBytes(objFile.toPath());
            }
            this.imagem = null;
            this.cadastroConcluido = null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de funcionário");
        }
    }
    
    public void limparCampos() {
        try {
            this.funcionario = new Funcionario();
            this.funcionarioTemp = new Funcionario();
            this.imagem = null;
            File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\SemFoto.PNG");
            this.imagemPreview = Files.readAllBytes(objFile.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de funcionário");
        }
    }
    
    public void cadastrarFuncionario() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            FuncionarioFacade.cadastrarFuncionario(this.funcionario, this.imagem);
            this.cadastroConcluido = true;
            PrimeFaces.current().ajax().update("novoFuncionarioForm:boxSucesso");
        } catch (FuncionarioException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de funcionário");
        }
    }
    
    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }
    
    public void paginaInicio(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
        }
    }

    //Para edição de dados de novos funcionarios
    public void setEditFuncionario(Funcionario func) {
        try {
            this.atualizar = true;
            this.funcionario = func;
            Morador buscarMorador = MoradorFacade.moradorPorCpf(func.getCpf());
            if ((this.funcionario.getImagem() != null) && (this.funcionario.getImagem().getId() != 0)) {
                File objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\"
                        + this.funcionario.getImagem().getId() + this.funcionario.getImagem().getExtensao());
                this.imagemPreview = Files.readAllBytes(objFile.toPath());
            }
            if ((buscarMorador != null)
                    && (!((buscarMorador.getStatus() == 4) && (buscarMorador.getApartamento() == null)))) {
                this.funcionarioTemp = func;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de funcionário");
        }
    }
    
    public void atualizarDadosFuncionario() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            FuncionarioFacade.atualizarFuncionario(this.funcionario, this.imagem);
            this.cadastroConcluido = true;
            ctx.addMessage(null, SgrUtil.emiteMsg("Dados atualizados com sucesso", 1));
        } catch (FuncionarioException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar os dados de funcionário");
        }
    }
    
    public void removerFuncionario(Funcionario func) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            FuncionarioFacade.removerFuncionario(func);
            this.funcionario = new Funcionario();
            this.funcionarioTemp = new Funcionario();
            iniciaListaFuncionarioAtv();
            iniciaListaFuncionarioDstv();
            iniciaListaFuncionarioNovo();
            ctx.addMessage(null, SgrUtil.emiteMsg("Funcionario removído: " + func.getNome(), 1));
        } catch (FuncionarioException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar dados de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar dados de funcionário");
        }
    }
    
    public void reativarFuncionario() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            FuncionarioFacade.reativarFuncionario(this.funcionario);
            this.cadastroConcluido = true;
            iniciaListaFuncionarioAtv();
            iniciaListaFuncionarioDstv();
            iniciaListaFuncionarioNovo();
            ctx.addMessage(null, SgrUtil.emiteMsg("Funcionário ativado com sucesso!", 1));
        } catch (FuncionarioException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao reativar funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao reativar funcionário");
        }
    }
    
    public void desativarFuncionario(Funcionario func) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            func.setStatus(2);
            FuncionarioFacade.atualizarStatusFuncionario(func);
            iniciaListaFuncionarioAtv();
            iniciaListaFuncionarioDstv();
            iniciaListaFuncionarioNovo();
            ctx.addMessage(null, SgrUtil.emiteMsg(func.getNome() + " DESLIGADO", 1));
        } catch (FuncionarioException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar status de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar status de funcionário");
        }
    }
    
    public void gerarNovaSenha() {
        try {
            this.funcionarioTemp.setId(this.funcionario.getId());
            this.funcionarioTemp.setStatus(this.funcionario.getStatus());
            FuncionarioFacade.gerarNovaSenha(this.funcionarioTemp);
            this.cadastroConcluido = true;
        } catch (FuncionarioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao gerar senha de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao gerar senha de funcionário");
        }
    }
    
    public void atualizarDadosProfissional() {
        try {
            this.funcionario.setFuncao(this.funcionarioTemp.getFuncao());   //Formulario edição e reativação setando mesmo atributo
            FuncionarioFacade.atualizarProfissionalFuncionario(this.funcionario);
            this.cadastroConcluido = true;
        } catch (FuncionarioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar dados de funcionário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar dados de funcionário");
        }
    }
    
    public void alterarSenhaFuncionario() {
        try {
            //Referente ao campo "senha atual"
            this.funcionarioTemp.setSenha(Seguranca.md5(this.funcionarioTemp.getSenha()));
            this.funcionario.setSenha(Seguranca.md5(this.funcionario.getSenha()));
            this.confirmaSenha = Seguranca.md5(this.confirmaSenha);
            
            if (!this.funcionarioTemp.getSenha().equals(this.bkpSenha)) {
                throw new FuncionarioException("Senha atual não confere");
            } else if (!this.funcionario.getSenha().equals(this.confirmaSenha)) {
                throw new FuncionarioException("Senha e confirmação não conferem");
            } else {
                FuncionarioFacade.alterarSenhaFuncionario(this.funcionario);
                if (this.login.getFuncionario().getStatus() != this.funcionario.getStatus()) {
                    this.login.getFuncionario().setStatus(this.funcionario.getStatus());
                    PrimeFaces.current().executeScript("PF('divConclusao').show()");
                }
                this.cadastroConcluido = true;
            }
        } catch (FuncionarioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao alterar dados de funcionário");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao alterar dados de funcionário");
        }
    }
    
    public void setEditFuncionarioAtv(Funcionario funcionario) {
        this.funcionario = funcionario;
        this.funcionarioTemp.setFuncao(this.funcionario.getFuncao());
    }
    
    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }
    
    public Funcionario getFuncionario() {
        return funcionario;
    }
    
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
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
    
    public void uploadImagem(FileUploadEvent event) {
        try {
            Arquivo arq = (this.funcionario.getImagem() == null) ? new Arquivo()
                    : this.funcionario.getImagem();
            
            this.imagem = event.getFile();
            this.imagemPreview = event.getFile().getContents();
            
            arq.setExtensao(event.getFile().getFileName().toUpperCase());
            arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                    arq.getExtensao().length()));
            this.funcionario.setImagem(arq);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar upload de imagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }
    
    public List<Funcao> getListaFuncao() {
        return listaFuncao;
    }
    
    public void setListaFuncao(List<Funcao> listaFuncao) {
        this.listaFuncao = listaFuncao;
    }
    
    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }
    
    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }
    
    public Funcionario getFuncionarioTemp() {
        return funcionarioTemp;
    }
    
    public void setFuncionarioTemp(Funcionario funcionarioTemp) {
        this.funcionarioTemp = funcionarioTemp;
    }
    
    public void cancelaImagem() {
        this.imagem = null;
    }
    
    public String getConfirmaSenha() {
        return confirmaSenha;
    }
    
    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }
    
    public byte[] getImagemPreview() {
        return imagemPreview;
    }
    
    public void setImagemPreview(byte[] imagemPreview) {
        this.imagemPreview = imagemPreview;
    }
    
    public Boolean getAtualizar() {
        return atualizar;
    }
    
    public void setAtualizar(Boolean atualizar) {
        this.atualizar = atualizar;
    }
    
    public LazyDataModel<Funcionario> getListaFuncionarioAtv() {
        return listaFuncionarioAtv;
    }
    
    public void setListaFuncionarioAtv(LazyDataModel<Funcionario> listaFuncionarioAtv) {
        this.listaFuncionarioAtv = listaFuncionarioAtv;
    }
    
    public LazyDataModel<Funcionario> getListaFuncionarioDstv() {
        return listaFuncionarioDstv;
    }
    
    public void setListaFuncionarioDstv(LazyDataModel<Funcionario> listaFuncionarioDstv) {
        this.listaFuncionarioDstv = listaFuncionarioDstv;
    }
    
    public LazyDataModel<Funcionario> getListaFuncionarioNovo() {
        return listaFuncionarioNovo;
    }
    
    public void setListaFuncionarioNovo(LazyDataModel<Funcionario> listaFuncionarioNovo) {
        this.listaFuncionarioNovo = listaFuncionarioNovo;
    }
}
