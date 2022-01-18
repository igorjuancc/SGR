package br.com.sgr.mb;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Noticia;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.NoticiaException;
import br.com.sgr.facade.NoticiaFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@ManagedBean
@Named(value = "noticiaMB")
@ViewScoped
public class NoticiaMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Boolean cadastroConcluido;
    private Noticia noticia;
    private UploadedFile imagem;
    private List<UploadedFile> upImagens;
    private List<byte[]> previewImagens;

    private LazyDataModel<Noticia> noticiasFiltro;
    private final FiltroBD filtroNoticia = new FiltroBD();

    @PostConstruct
    public void init() {
        iniciaListaNoticia();
    }

    public void iniciaListaNoticia() {
        noticiasFiltro = new LazyDataModel<Noticia>() {
            private static final long serialVersionUID = 1L;
            List<Noticia> listaNoticaFiltro = null;

            @Override
            public List<Noticia> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroNoticia.setPrimeiroRegistro(first);
                filtroNoticia.setQntdRegistros(Long.valueOf(pageSize));
                filtroNoticia.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroNoticia.setPropriedadeOrdenacao(sortField);
                setRowCount(NoticiaFacade.totalNoticias());
                listaNoticaFiltro = NoticiaFacade.listaNoticiaFiltro(filtroNoticia);
                return listaNoticaFiltro;
            }
        };
    }

    public void gravarNoticia() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NoticiaFacade.cadastrarNoticia(this.noticia, this.upImagens);
            this.cadastroConcluido = true;
        } catch (NoticiaException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notícia");
            }
        } catch (ArquivoException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notícia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notícia");
        }
    }

    public void atualizarNoticia() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NoticiaFacade.atualizarNoticia(this.noticia, this.upImagens);
            this.cadastroConcluido = true;
        } catch (NoticiaException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar notícia");
            }
        } catch (ArquivoException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar notícia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar notícia");
        }
    }

    public void apagarNoticia(Noticia noticia) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NoticiaFacade.apagarNoticia(noticia);
            ctx.addMessage(null, SgrUtil.emiteMsg("Notícia apagada com sucesso", 1));
        } catch (ArquivoException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg("Notícia apagada com sucesso", 1));
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar notícia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar notícia");
        }
    }

    public void removerImagem(byte[] imagem) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            int indice = this.previewImagens.indexOf(imagem);
            if (this.noticia.getArquivos().get(indice).getId() != 0) {
                NoticiaFacade.apagarImagem(this.noticia.getArquivos().get(indice));
            }
            this.upImagens.remove(indice);
            this.previewImagens.remove(indice);
            this.noticia.getArquivos().remove(indice);
            FacesMessage msg = SgrUtil.emiteMsg("Imagem removída com sucesso", 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar imagem de notícia");
        }
    }

    public void setEdicaoNoticia(Noticia noticia) {
        try {
            this.noticia = noticia;
            this.noticia.setFuncionarioAlteracao(this.login.getFuncionario());
            if ((this.noticia.getArquivos() != null) && (!this.noticia.getArquivos().isEmpty())) {
                this.upImagens = new ArrayList<>();
                this.previewImagens = new ArrayList<>();
                File objFile;
                for (Arquivo a : this.noticia.getArquivos()) {
                    objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemNoticia\\" + a.getId() + a.getExtensao());
                    if (objFile.isFile()) {
                        this.upImagens.add(null);
                        this.previewImagens.add(Files.readAllBytes(objFile.toPath()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inicializar edição de notícia");
        }
    }

    public void novaNoticia() {
        try {
            this.noticia = new Noticia();
            this.noticia.setAutor(login.getFuncionario());
            this.noticia.setFuncionarioAlteracao(this.login.getFuncionario());
            this.previewImagens = null;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inicializar cadastro de nova notícia");
        }
    }

    public void uploadImagem(FileUploadEvent event) {
        try {
            if (this.upImagens == null) {
                this.upImagens = new ArrayList<>();
            }
            if (this.previewImagens == null) {
                this.previewImagens = new ArrayList<>();
            }
            if (this.noticia.getArquivos() == null) {
                this.noticia.setArquivos(new ArrayList<>());
            }

            if (this.noticia.getArquivos().size() >= 5) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage msg = SgrUtil.emiteMsg("Máximo de imagens atingido [5]", 2);
                ctx.addMessage(null, msg);
            } else {
                this.upImagens.add(event.getFile());
                this.previewImagens.add(event.getFile().getContents());

                Arquivo arq = new Arquivo();
                arq.setNoticia(this.noticia);
                arq.setExtensao(event.getFile().getFileName().toUpperCase());
                arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                        arq.getExtensao().length()));
                this.noticia.getArquivos().add(arq);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao realizar upload de imagem");
        }
    }

    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
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

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public List<byte[]> getPreviewImagens() {
        return previewImagens;
    }

    public void setPreviewImagens(List<byte[]> previewImagens) {
        this.previewImagens = previewImagens;
    }

    public LazyDataModel<Noticia> getNoticiasFiltro() {
        return noticiasFiltro;
    }

    public void setNoticiasFiltro(LazyDataModel<Noticia> noticiasFiltro) {
        this.noticiasFiltro = noticiasFiltro;
    }

    public List<UploadedFile> getUpImagens() {
        return upImagens;
    }

    public void setUpImagens(List<UploadedFile> upImagens) {
        this.upImagens = upImagens;
    }
}
