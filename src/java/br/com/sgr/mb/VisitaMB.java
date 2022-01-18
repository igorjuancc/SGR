package br.com.sgr.mb;

import br.com.sgr.bean.Visita;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.VisitaException;
import br.com.sgr.facade.RegistroESFacade;
import br.com.sgr.facade.VisitaFacade;
import br.com.sgr.facade.VisitanteFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "visitaMB")
@ViewScoped
public class VisitaMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Visita visita;
    private Visitante visitante;
    private Boolean cadastroConcluido;
    private Date hoje;
    private List<Visitante> visitantesPresentes;
    private List<Visitante> visitantesAusentes;
    private List<Visitante> visitantesDisponiveis;

    private LazyDataModel<Visita> listaVisitaApto;
    private FiltroBD filtroVisita;

    @PostConstruct
    public void init() {
        try {
            this.hoje = new Date();
            this.filtroVisita = new FiltroBD();
            iniciaListaVisitaAndamento();
            this.visitantesPresentes = new ArrayList<>();
            this.visitantesAusentes = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaVisitaAndamento() {
        listaVisitaApto = new LazyDataModel<Visita>() {
            private static final long serialVersionUID = 1L;
            List<Visita> listaVisitaFiltro = null;

            @Override
            public List<Visita> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroVisita.setPrimeiroRegistro(first);
                filtroVisita.setQntdRegistros(Long.valueOf(pageSize));
                filtroVisita.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroVisita.setPropriedadeOrdenacao(sortField);
                setRowCount(VisitaFacade.totalVisitaPorApto(login.getMorador()));
                listaVisitaFiltro = VisitaFacade.listaVisitaFiltro(filtroVisita, login.getMorador());
                return listaVisitaFiltro;
            }
        };
    }

    public void setNovaVisita() {
        try {
            this.visita = new Visita();
            this.visita.setDataInicio(this.hoje);
            this.visita.setApartamento(this.login.getMorador().getApartamento());
            this.visitantesDisponiveis = VisitanteFacade.visitantesDisponiveis();
            this.visitantesPresentes = new ArrayList<>();
            this.visitantesAusentes = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visita");
        }
    }

    public void addVisitante(Visitante visitante) {
        try {
            if (this.visita.getVisitantes() == null) {
                this.visita.setVisitantes(new ArrayList());
            }
            this.visita.getVisitantes().add(visitante);
            this.visitantesDisponiveis.remove(visitante);
            Collections.sort(this.visita.getVisitantes(), (Visitante v1, Visitante v2) -> v1.getNome().compareTo(v2.getNome()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visita");
        }
    }

    public void removerVisitante(Visitante visitante) {
        try {
            this.visita.getVisitantes().remove(visitante);
            this.visitantesDisponiveis.add(visitante);
            Collections.sort(this.visitantesDisponiveis, (Visitante v1, Visitante v2) -> v1.getNome().compareTo(v2.getNome()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de visita");
        }
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf.replaceAll(cpf.substring(0, 6), "******"));
    }

    public void cancelarVisita() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("MoradorVisita.jsf");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
        }
    }

    public String imprimeData(Date data) {
        return SgrUtil.formataData(data);
    }

    public void cadastrarVisita() {
        try {
            VisitaFacade.cadastrarVisita(this.visita);
            this.cadastroConcluido = true;
        } catch (VisitaException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
        }
    }

    public void atualizarVisita() {
        try {
            VisitaFacade.atualizarVisita(this.visita);
            this.cadastroConcluido = true;
        } catch (VisitaException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
        }
    }

    public void apagarVisita(Visita visita) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            VisitaFacade.apagarVisita(visita);
            iniciaListaVisitaAndamento();
            ctx.addMessage(null, SgrUtil.emiteMsg("Visita removída com sucesso", 1));
        } catch (VisitaException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
        }
    }

    //Set dados para visualizar e editar visita
    public void verVisita(Visita visita, int opc) {
        try {
            this.visita = visita;
            this.visitantesAusentes.clear();
            this.visitantesPresentes.clear();

            for (Visitante vis : this.visita.getVisitantes()) {
                vis.setVisitas(new ArrayList<>());
                Visita verifica = new Visita();
                verifica.setDataFim((Date) this.visita.getDataFim().clone());
                verifica.setDataInicio((Date) this.visita.getDataInicio().clone());
                vis.getVisitas().add(verifica);
                vis.setRegEntradaSaida(RegistroESFacade.listaRegistroDataPessoa(vis));
            }
            for (Visitante vst : this.visita.getVisitantes()) {
                if ((vst.getRegEntradaSaida() != null) && (!vst.getRegEntradaSaida().isEmpty())) {
                    this.visitantesPresentes.add(vst);
                } else {
                    this.visitantesAusentes.add(vst);
                }
            }
            if (opc == 1) {
                this.visitantesDisponiveis = VisitanteFacade.visitantesDisponiveis();
                visitantesDisponiveis.removeAll(visitantesAusentes);
                visitantesDisponiveis.removeAll(visitantesPresentes);
            }
            Collections.sort(this.visitantesPresentes, (Visitante v1, Visitante v2) -> v1.getNome().compareTo(v2.getNome()));
            Collections.sort(this.visitantesAusentes, (Visitante v1, Visitante v2) -> v1.getNome().compareTo(v2.getNome()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar visita");
        }
    }

    public String imprimeFone(String fone) {
        return SgrUtil.imprimeFone(fone);
    }

    public String imprimeCel(String cel) {
        return SgrUtil.imprimeCel(cel);
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Visita getVisita() {
        return visita;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
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

    public Date getHoje() {
        return hoje;
    }

    public void setHoje(Date hoje) {
        this.hoje = hoje;
    }

    public List<Visitante> getVisitantesPresentes() {
        return visitantesPresentes;
    }

    public void setVisitantesPresentes(List<Visitante> visitantesPresentes) {
        this.visitantesPresentes = visitantesPresentes;
    }

    public List<Visitante> getVisitantesAusentes() {
        return visitantesAusentes;
    }

    public void setVisitantesAusentes(List<Visitante> visitantesAusentes) {
        this.visitantesAusentes = visitantesAusentes;
    }

    public List<Visitante> getVisitantesDisponiveis() {
        return visitantesDisponiveis;
    }

    public void setVisitantesDisponiveis(List<Visitante> visitantesDisponiveis) {
        this.visitantesDisponiveis = visitantesDisponiveis;
    }

    public LazyDataModel<Visita> getListaVisitaApto() {
        return listaVisitaApto;
    }

    public void setListaVisitaApto(LazyDataModel<Visita> listaVisitaApto) {
        this.listaVisitaApto = listaVisitaApto;
    }
}
