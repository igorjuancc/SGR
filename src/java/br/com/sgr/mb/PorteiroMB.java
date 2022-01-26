package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Veiculo;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.RegistroESException;
import br.com.sgr.exception.VisitanteException;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.RegistroESFacade;
import br.com.sgr.facade.VeiculoFacade;
import br.com.sgr.facade.VisitanteFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "porteiroMB")
@ViewScoped
public class PorteiroMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Date hoje;
    private Visitante visitante;
    private Boolean exec;
    private byte[] previewImagem;
    private Apartamento aptoMorador;
    private List<Apartamento> aptoComMorador;
    private Morador morador;

    private LazyDataModel<Visitante> listaVisitanteAndamento;
    private LazyDataModel<Visitante> listaVisitanteSaida;
    private LazyDataModel<Morador> listaMoradorAtv;
    private LazyDataModel<Morador> listaMoradorDst;
    private LazyDataModel<Veiculo> listaVeiculos;
    private FiltroBD filtroVisitante;
    private FiltroBD filtroMoradorAtv;
    private FiltroBD filtroMoradorDstv;
    private FiltroBD filtroVeiculos;

    @PostConstruct
    public void init() {
        try {
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/porteiro/PorteiroVisitaHoje.xhtml":
                    this.filtroVisitante = new FiltroBD();
                    this.hoje = new Date();
                    iniciaListaVisitaAndamento();
                    iniciaListaVisitanteSaida();
                    File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
                    this.previewImagem = Files.readAllBytes(objFile.toPath());
                    break;
                case "/porteiro/PorteiroMorador.xhtml":
                    this.aptoComMorador = ApartamentoFacade.apartamentosOcupados();
                    this.filtroMoradorAtv = new FiltroBD();
                    this.filtroMoradorDstv = new FiltroBD();
                    iniciaListaMoradorAtv();
                    iniciaListaMoradorDstv();
                    break;
                case "/porteiro/PorteiroVeiculos.xhtml":
                    this.filtroVeiculos = new FiltroBD();
                    iniciaListaVeiculos();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaVisitaAndamento() {
        listaVisitanteAndamento = new LazyDataModel<Visitante>() {
            private static final long serialVersionUID = 1L;
            List<Visitante> listaVisitanteFiltro = null;

            @Override
            public List<Visitante> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroVisitante.setPrimeiroRegistro(first);
                filtroVisitante.setQntdRegistros(Long.valueOf(pageSize));
                filtroVisitante.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroVisitante.setPropriedadeOrdenacao(sortField);
                setRowCount(VisitanteFacade.visitantesTotalDia());
                listaVisitanteFiltro = VisitanteFacade.visitantesAgendadoFiltro(filtroVisitante);
                return listaVisitanteFiltro;
            }
        };
    }

    public void iniciaListaVisitanteSaida() {
        listaVisitanteSaida = new LazyDataModel<Visitante>() {
            private static final long serialVersionUID = 1L;
            List<Visitante> listaVisitanteFiltro = null;

            @Override
            public List<Visitante> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroVisitante.setPrimeiroRegistro(first);
                filtroVisitante.setQntdRegistros(Long.valueOf(pageSize));
                filtroVisitante.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroVisitante.setPropriedadeOrdenacao(sortField);
                setRowCount(VisitanteFacade.visitantesTotalNaoSairam());
                listaVisitanteFiltro = VisitanteFacade.visitantesSaidaFiltro(filtroVisitante);
                return listaVisitanteFiltro;
            }
        };
    }

    public void iniciaListaMoradorAtv() {
        listaMoradorAtv = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradorAtv.setPrimeiroRegistro(first);
                filtroMoradorAtv.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradorAtv.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradorAtv.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresAtivos());
                listaMoradorFiltro = MoradorFacade.moradorAtvFiltro(filtroMoradorAtv);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaMoradorDstv() {
        listaMoradorDst = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradorDstv.setPrimeiroRegistro(first);
                filtroMoradorDstv.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradorDstv.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradorDstv.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresDesativados());
                listaMoradorFiltro = MoradorFacade.moradorDstvFiltro(filtroMoradorDstv);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaVeiculos() {
        listaVeiculos = new LazyDataModel<Veiculo>() {
            private static final long serialVersionUID = 1L;
            List<Veiculo> listaVeiculoFiltro = null;

            @Override
            public List<Veiculo> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroVeiculos.setPrimeiroRegistro(first);
                filtroVeiculos.setQntdRegistros(Long.valueOf(pageSize));
                filtroVeiculos.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroVeiculos.setPropriedadeOrdenacao(sortField);
                setRowCount(VeiculoFacade.totalDeVeiculos());
                listaVeiculoFiltro = VeiculoFacade.veiculosFiltro(filtroVeiculos);
                return listaVeiculoFiltro;
            }
        };
    }

    public void novaCapturaFoto(CaptureEvent captureEvent) {
        try {
            byte[] data = captureEvent.getData();
            String newFileName = SgrUtil.caminhoProjeto() + "ImagemPessoa\\NovaCaptura.PNG";
            FileImageOutputStream imageOutput;

            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            this.previewImagem = data;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao capturar imagem de visitante");
        }
    }

    public void cancelarCapturaFoto() {
        try {
            File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
            this.previewImagem = Files.readAllBytes(objFile.toPath());
            this.exec = null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cancelar captura de imagem");
        }
    }

    public void salvarCapturaFoto() {
        try {
            VisitanteFacade.novaImagemViaPorteiro(this.visitante, this.previewImagem);
            PrimeFaces.current().ajax().update("formVisitas");
            PrimeFaces.current().executeScript("PF('verVisitante').show()");
        } catch (VisitanteException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar imagem");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar imagem");
        }
    }

    public Boolean confirmaSalvarFoto() {
        try {
            File objFile = new File(SgrUtil.caminhoProjeto() + "ImagemPessoa\\SemFoto.PNG");
            byte[] data = Files.readAllBytes(objFile.toPath());
            return Arrays.equals(data, this.previewImagem);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar imagem");
            return false;
        }
    }

    public void novoRegistroEntradaVisitante(Visitante visitante) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            if ((visitante.getImagem() == null) || (visitante.getImagem().getId() == 0)) {
                setVisitante(visitante);
                PrimeFaces.current().ajax().update("formVisitas:verVisitante");
                PrimeFaces.current().executeScript("PF('verVisitante').show()");
            } else {
                RegistroESFacade.novoRegistroEntradaVisitante(visitante);
                iniciaListaVisitaAndamento();
                iniciaListaVisitanteSaida();
                PrimeFaces.current().ajax().update("formVisitas");
                ctx.addMessage(null, SgrUtil.emiteMsg("Entrada registrada com sucesso", 1));
            }
        } catch (RegistroESException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar entrada");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar entrada");
        }
    }

    public void registroSaidaVisitante(Visitante visitante) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            RegistroESFacade.registroSaidaVisitante(visitante);
            iniciaListaVisitaAndamento();
            iniciaListaVisitanteSaida();
            ctx.addMessage(null, SgrUtil.emiteMsg("Saida registrada com sucesso", 1));
        } catch (RegistroESException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar saída");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar saída");
        }
    }

    public void setVerMorador(Morador morador) {
        this.morador = morador;
        this.aptoMorador = null;
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

    public Date getHoje() {
        return hoje;
    }

    public void setHoje(Date hoje) {
        this.hoje = hoje;
    }

    public LazyDataModel<Visitante> getListaVisitanteAndamento() {
        return listaVisitanteAndamento;
    }

    public void setListaVisitanteAndamento(LazyDataModel<Visitante> listaVisitanteAndamento) {
        this.listaVisitanteAndamento = listaVisitanteAndamento;
    }

    public LazyDataModel<Visitante> getListaVisitanteSaida() {
        return listaVisitanteSaida;
    }

    public void setListaVisitanteSaida(LazyDataModel<Visitante> listaVisitanteSaida) {
        this.listaVisitanteSaida = listaVisitanteSaida;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        try {
            File objFile;
            String caminho = SgrUtil.caminhoProjeto() + "ImagemPessoa\\";
            if (visitante.getImagem() != null) {
                objFile = new File(caminho + Integer.toString(visitante.getImagem().getId()) + visitante.getImagem().getExtensao());
            } else {
                objFile = new File(caminho + "SemFoto.PNG");
            }
            this.previewImagem = Files.readAllBytes(objFile.toPath());
            this.visitante = visitante;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visitante");
        }
    }

    public Boolean getExec() {
        return exec;
    }

    public void setExec(Boolean exec) {
        this.exec = exec;
    }

    public byte[] getPreviewImagem() {
        return previewImagem;
    }

    public void setPreviewImagem(byte[] previewImagem) {
        this.previewImagem = previewImagem;
    }

    public List<Apartamento> getAptoComMorador() {
        return aptoComMorador;
    }

    public void setAptoComMorador(List<Apartamento> aptoComMorador) {
        this.aptoComMorador = aptoComMorador;
    }

    public Apartamento getAptoMorador() {
        return aptoMorador;
    }

    public void setAptoMorador(Apartamento aptoMorador) {
        try {
            aptoMorador.setMoradores(MoradorFacade.listaMoradorPorApto(aptoMorador));

            for (int i = 0; i < aptoMorador.getMoradores().size(); i++) {
                Morador m = aptoMorador.getMoradores().get(i);
                if (m.getTipo() == 1) {
                    m.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(m));
                    this.morador = m;
                }
                if (m.getStatus() == 4) {
                    aptoMorador.getMoradores().remove(m);
                }
            }
            this.aptoMorador = aptoMorador;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de apartamento");
        }
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public LazyDataModel<Morador> getListaMoradorAtv() {
        return listaMoradorAtv;
    }

    public void setListaMoradorAtv(LazyDataModel<Morador> listaMoradorAtv) {
        this.listaMoradorAtv = listaMoradorAtv;
    }

    public LazyDataModel<Morador> getListaMoradorDst() {
        return listaMoradorDst;
    }

    public void setListaMoradorDst(LazyDataModel<Morador> listaMoradorDst) {
        this.listaMoradorDst = listaMoradorDst;
    }

    public LazyDataModel<Veiculo> getListaVeiculos() {
        return listaVeiculos;
    }

    public void setListaVeiculos(LazyDataModel<Veiculo> listaVeiculos) {
        this.listaVeiculos = listaVeiculos;
    }
}
