package br.com.sgr.mb;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Salao;
import br.com.sgr.exception.SalaoException;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.SalaoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "salaoMB")
@ViewScoped
public class SalaoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Salao reservaSalao;
    private List<Date> datasReservadas;
    private List<Morador> moradores;

    private LazyDataModel<Salao> datasSalaoReservadas;
    private FiltroBD filtroSalao;

    @PostConstruct
    public void init() {
        try {
            this.filtroSalao = new FiltroBD();
            iniciaListas();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaSalao() {
        datasSalaoReservadas = new LazyDataModel<Salao>() {
            private static final long serialVersionUID = 1L;
            List<Salao> listaSalaoFiltro = null;

            @Override
            public List<Salao> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroSalao.setPrimeiroRegistro(first);
                filtroSalao.setQntdRegistros(Long.valueOf(pageSize));
                filtroSalao.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroSalao.setPropriedadeOrdenacao(sortField);
                setRowCount(SalaoFacade.totalReservaSalao());
                listaSalaoFiltro = SalaoFacade.listaReservaSalaoFiltro(filtroSalao);
                return listaSalaoFiltro;
            }
        };
    }

    public void iniciaListas() {
        try {
            this.reservaSalao = new Salao();
            this.moradores = MoradorFacade.responsaveisAtivos();
            iniciaListaSalao();
            this.datasReservadas = SalaoFacade.datasReservaSalao();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de reservas");
        }
    }

    public void novaReserva() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            this.reservaSalao.setFuncionario(this.login.getFuncionario());
            SalaoFacade.novoRegistroSalao(this.reservaSalao);
            ctx.addMessage(null, SgrUtil.emiteMsg("Salão de festas reservado com sucesso", 1));
            iniciaListas();
            PrimeFaces.current().ajax().update("divFormReserva");
        } catch (SalaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PrimeFaces.current().ajax().update("novoReservaForm");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao registrar nova reserva");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao registrar nova reserva");
        }
    }

    public void atualizarReserva() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            SalaoFacade.editarReservaSalao(this.reservaSalao);
            ctx.addMessage(null, SgrUtil.emiteMsg("Salão de festas reservado com sucesso", 1));
            iniciaListas();
            PrimeFaces.current().ajax().update("divFormReserva");
        } catch (SalaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PrimeFaces.current().ajax().update("novoReservaForm");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao registrar reserva de salão");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao registrar reserva de salão");
        }
    }

    public void apagarReserva(Salao salao) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            SalaoFacade.apagarReservaSalao(salao);
            ctx.addMessage(null, SgrUtil.emiteMsg("Reserva removída com sucesso!", 1));
            iniciaListas();
        } catch (SalaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover reserva de salão");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover reserva de salão");
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Salao getReservaSalao() {
        return reservaSalao;
    }

    public void setReservaSalao(Salao reservaSalao) {
        this.reservaSalao = reservaSalao;
    }

    public List<Date> getDatasReservadas() {
        return datasReservadas;
    }

    public void setDatasReservadas(List<Date> datasReservadas) {
        this.datasReservadas = datasReservadas;
    }

    public LazyDataModel<Salao> getDatasSalaoReservadas() {
        return datasSalaoReservadas;
    }

    public void setDatasSalaoReservadas(LazyDataModel<Salao> datasSalaoReservadas) {
        this.datasSalaoReservadas = datasSalaoReservadas;
    }

    public List<Morador> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Morador> moradores) {
        this.moradores = moradores;
    }
}
