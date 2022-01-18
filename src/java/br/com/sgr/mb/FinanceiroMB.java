package br.com.sgr.mb;

import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.bean.Financeiro;
import br.com.sgr.exception.FinanceiroException;
import br.com.sgr.facade.BoletoFacade;
import br.com.sgr.facade.CategoriaFinanceiroFacade;
import br.com.sgr.facade.FinanceiroFacade;
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "financeiroMB")
@ViewScoped
public class FinanceiroMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private int anoFiltro;
    private int mesFiltro;
    private Boolean cadastroConcluido;
    private Financeiro financeiro;
    private BoletoSGR boleto;
    private List<CategoriaFinanceiro> categoriaFinanceiro;
    private List<Integer> anoRegistro;
    private List<Integer> mesRegistro;
    private List<Financeiro> listaReceita;
    private List<Financeiro> listaDespesa;

    private LazyDataModel<BoletoSGR> listaBoletoSemRegistro;
    private LazyDataModel<Financeiro> listaReceitaComRegistro;
    private FiltroBD filtroBoleto;

    @PostConstruct
    public void init() {
        try {
            this.categoriaFinanceiro = CategoriaFinanceiroFacade.categoriasFinanceiro();
            this.anoRegistro = FinanceiroFacade.listaAnoRegistro(1);
            if (!this.anoRegistro.isEmpty()) {
                this.anoFiltro = this.anoRegistro.get(0);
                this.mesRegistro = FinanceiroFacade.listaMesAnoRegistro(this.anoFiltro, 1);
                this.mesFiltro = this.mesRegistro.get(0);
                this.listaDespesa = new ArrayList<>();
                inicializaListaFinanceiro();
            }
            this.filtroBoleto = new FiltroBD();
            iniciaListaBoletoSemRegistro();
            iniciaListaBoletoComRegistro();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaBoletoSemRegistro() {
        listaBoletoSemRegistro = new LazyDataModel<BoletoSGR>() {
            private static final long serialVersionUID = 1L;
            List<BoletoSGR> listaBoletoFiltro = null;

            @Override
            public List<BoletoSGR> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroBoleto.setPrimeiroRegistro(first);
                filtroBoleto.setQntdRegistros(Long.valueOf(pageSize));
                filtroBoleto.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBoleto.setPropriedadeOrdenacao(sortField);
                setRowCount(BoletoFacade.totalBoletosSemRegistro());
                listaBoletoFiltro = BoletoFacade.boletoFiltro(filtroBoleto);
                return listaBoletoFiltro;
            }
        };
    }

    public void iniciaListaBoletoComRegistro() {
        listaReceitaComRegistro = new LazyDataModel<Financeiro>() {
            private static final long serialVersionUID = 1L;
            List<Financeiro> listaFinanceiroFiltro = null;

            @Override
            public List<Financeiro> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroBoleto.setPrimeiroRegistro(first);
                filtroBoleto.setQntdRegistros(Long.valueOf(pageSize));
                filtroBoleto.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBoleto.setPropriedadeOrdenacao(sortField);
                setRowCount(FinanceiroFacade.totalRegistroFinanceiroTipo(0));
                listaFinanceiroFiltro = FinanceiroFacade.listaRegistroFinanceiroFiltro(filtroBoleto);
                return listaFinanceiroFiltro;
            }
        };
    }

    //Tipo Financeiro: [0] Receita [1]Despesa
    public void novaReceita(BoletoSGR boleto) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            Financeiro receita = new Financeiro();
            receita.setBoleto(boleto);
            receita.setFuncionario(this.login.getFuncionario());
            receita.setTipo(0);
            FinanceiroFacade.novoRegistroFinanceiro(receita);
            ctx.addMessage(null, SgrUtil.emiteMsg("Receita registrada com sucesso", 1));
            iniciaListaBoletoSemRegistro();
        } catch (FinanceiroException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova receita");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova receita");
        }
    }

    public void apagarReceita(Financeiro financeiro) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FinanceiroFacade.apagarRegistroFinanceiro(financeiro);
            iniciaListaBoletoSemRegistro();
            iniciaListaBoletoComRegistro();
            ctx.addMessage(null, SgrUtil.emiteMsg("Registro removido com sucesso", 1));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar registro financeiro");
        }
    }

    public void novaDespesa() {
        try {
            if (this.financeiro.getBoleto() == null) {
                this.financeiro.setTipo(1);
            }
            this.financeiro.setFuncionario(this.login.getFuncionario());
            FinanceiroFacade.novoRegistroFinanceiro(this.financeiro);
            this.anoRegistro = FinanceiroFacade.listaAnoRegistro(1);
            if (!this.anoRegistro.isEmpty()) {
                this.anoFiltro = this.anoRegistro.get(0);
                this.mesRegistro = FinanceiroFacade.listaMesAnoRegistro(this.anoFiltro, 1);
                this.mesFiltro = this.mesRegistro.get(0);
                inicializaListaFinanceiro();
            } else {
                if (this.listaDespesa != null) {
                    this.listaDespesa.clear();
                }
                if (this.listaReceita != null) {
                    this.listaReceita.clear();
                }
                if (this.mesRegistro != null) {
                    this.mesRegistro.clear();
                }
                if (this.anoRegistro != null) {
                    this.anoRegistro.clear();
                }
            }
            this.cadastroConcluido = true;
        } catch (FinanceiroException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova despesa");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova despesa");
        }
    }

    public void apagarRegistroFinanceiro(Financeiro financeiro) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FinanceiroFacade.apagarRegistroFinanceiro(financeiro);
            this.anoRegistro = FinanceiroFacade.listaAnoRegistro(1);
            if (!this.anoRegistro.isEmpty()) {
                this.anoFiltro = this.anoRegistro.get(0);
                this.mesRegistro = FinanceiroFacade.listaMesAnoRegistro(this.anoFiltro, 1);
                this.mesFiltro = this.mesRegistro.get(0);
                inicializaListaFinanceiro();
            } else {
                this.listaDespesa.clear();
                this.listaReceita.clear();
                this.mesRegistro.clear();
                this.anoRegistro.clear();
            }
            ctx.addMessage(null, SgrUtil.emiteMsg("Registro removido com sucesso", 1));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar registro financeiro");
        }
    }

    public void filtroAno() {
        try {
            this.mesRegistro = FinanceiroFacade.listaMesAnoRegistro(this.anoFiltro, 1);
            this.mesFiltro = this.mesRegistro.get(0);
            inicializaListaFinanceiro();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de filtro ano");
        }
    }

    public void newFinanceiro() {
        this.financeiro = new Financeiro();
        this.financeiro.setDataRegistro(new Date());
        this.cadastroConcluido = false;
    }

    public void inicializaListaFinanceiro() {
        try {
            if ((this.listaDespesa != null) && (!this.listaDespesa.isEmpty())) {
                this.listaDespesa.clear();
            } else {
                this.listaDespesa = new ArrayList<>();
            }
            
            this.listaReceita = FinanceiroFacade.mesAnoRegistroFinanceiro(this.anoFiltro, this.mesFiltro);

            for (Financeiro f : this.listaReceita) {
                if (f.getTipo() == 1) {
                    this.listaDespesa.add(f);
                }
            }

            this.listaReceita.removeAll(this.listaDespesa);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados financeiros");
        }
    }

    public void setEditFinanceiro(Financeiro financeiro) {
        this.financeiro = financeiro;
        this.cadastroConcluido = false;
    }

    public String nomeMes(int mes) {
        return SgrUtil.nomeMesAbrv(mes);
    }

    public void setVerBoleto(BoletoSGR boletoVer) {
        this.boleto = boletoVer;
        this.financeiro = null;
    }

    public void setVerFinanceiro(Financeiro financ) {
        this.boleto = null;
        this.financeiro = financ;
    }

    public String imprimeValor(Double valor) {
        return SgrUtil.imprimeValor(valor);
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public int getAnoFiltro() {
        return anoFiltro;
    }

    public void setAnoFiltro(int anoFiltro) {
        this.anoFiltro = anoFiltro;
    }

    public int getMesFiltro() {
        return mesFiltro;
    }

    public void setMesFiltro(int mesFiltro) {
        this.mesFiltro = mesFiltro;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public Financeiro getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(Financeiro financeiro) {
        this.financeiro = financeiro;
    }

    public List<CategoriaFinanceiro> getCategoriaFinanceiro() {
        return categoriaFinanceiro;
    }

    public void setCategoriaFinanceiro(List<CategoriaFinanceiro> categoriaFinanceiro) {
        this.categoriaFinanceiro = categoriaFinanceiro;
    }

    public List<Integer> getAnoRegistro() {
        return anoRegistro;
    }

    public void setAnoRegisto(List<Integer> anoRegistro) {
        this.anoRegistro = anoRegistro;
    }

    public List<Integer> getMesRegistro() {
        return mesRegistro;
    }

    public void setMesRegistro(List<Integer> mesRegistro) {
        this.mesRegistro = mesRegistro;
    }

    public List<Financeiro> getListaReceita() {
        return listaReceita;
    }

    public void setListaReceita(List<Financeiro> listaReceita) {
        this.listaReceita = listaReceita;
    }

    public List<Financeiro> getListaDespesa() {
        return listaDespesa;
    }

    public void setListaDespesa(List<Financeiro> listaDespesa) {
        this.listaDespesa = listaDespesa;
    }

    public LazyDataModel<BoletoSGR> getListaBoletoSemRegistro() {
        return listaBoletoSemRegistro;
    }

    public void setListaBoletoSemRegistro(LazyDataModel<BoletoSGR> listaBoletoSemRegistro) {
        this.listaBoletoSemRegistro = listaBoletoSemRegistro;
    }

    public BoletoSGR getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoSGR boleto) {
        this.boleto = boleto;
    }

    public LazyDataModel<Financeiro> getListaReceitaComRegistro() {
        return listaReceitaComRegistro;
    }

    public void setListaReceitaComRegistro(LazyDataModel<Financeiro> listaReceitaComRegistro) {
        this.listaReceitaComRegistro = listaReceitaComRegistro;
    }
}
