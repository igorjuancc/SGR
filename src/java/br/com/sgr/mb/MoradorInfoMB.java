package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.facade.BoletoFacade;
import br.com.sgr.facade.DependenteFacade;
import br.com.sgr.facade.LogBDCadastroMoradorFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.NotificacaoFacade;
import br.com.sgr.facade.VeiculoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.logbean.LogBDCadastroMorador;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "moradorInfoMB")
@ViewScoped
public class MoradorInfoMB implements Serializable {

    private Morador morador;
    private int anoFiltro;
    private Date hoje;
    private List<Integer> anos;
    private List<LogBDCadastroMorador> logsCadastroMorador;
    private List<BoletoSGR> listaBoletoTaxa;
    private List<BoletoSGR> listaBoleto;
    private List<Apartamento> aptoComMorador;
    private List<Object[]> listaDebitosMorador;
    private List<Integer> listaIdMoradorDebitos;

    private LazyDataModel<Morador> listaMoradorAtv;
    private LazyDataModel<Morador> listaMoradorDst;
    private LazyDataModel<Object[]> listaMoradoresDebitos;
    private LazyDataModel<Notificacao> listaNotificacoes;
    private FiltroBD filtroBD;

    @PostConstruct
    public void init() {
        try {
            this.hoje = new Date();
            this.filtroBD = new FiltroBD();
            this.listaBoletoTaxa = new ArrayList<>();
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoMoradores.xhtml":
                    this.listaIdMoradorDebitos = new ArrayList<>();
                    this.aptoComMorador = ApartamentoFacade.apartamentosOcupados();
                    iniciaListaMoradorAtv();
                    iniciaListaMoradorDstv();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaMoradoresDebitos() {
        listaMoradoresDebitos = new LazyDataModel<Object[]>() {
            private static final long serialVersionUID = 1L;
            List<Object[]> listaMoradorFiltro = null;

            @Override
            public List<Object[]> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroBD.setPrimeiroRegistro(first);
                filtroBD.setQntdRegistros(Long.valueOf(pageSize));
                filtroBD.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBD.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalListaDebitos());
                listaMoradorFiltro = MoradorFacade.listaMoradoresDebitos(filtroBD);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaNotificacoesMorador(Morador moradorLista) {
        listaNotificacoes = new LazyDataModel<Notificacao>() {
            private static final long serialVersionUID = 1L;
            List<Notificacao> listaNotificacaoFiltro = null;

            @Override
            public List<Notificacao> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroBD.setPrimeiroRegistro(first);
                filtroBD.setQntdRegistros(Long.valueOf(pageSize));
                filtroBD.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBD.setPropriedadeOrdenacao(sortField);
                setRowCount(NotificacaoFacade.totalNotificacaoMorador(moradorLista));
                listaNotificacaoFiltro = NotificacaoFacade.notificacaoFiltroMorador(filtroBD, moradorLista);
                return listaNotificacaoFiltro;
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
                filtroBD.setPrimeiroRegistro(first);
                filtroBD.setQntdRegistros(Long.valueOf(pageSize));
                filtroBD.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBD.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresAtivos());
                listaMoradorFiltro = MoradorFacade.moradorAtvFiltro(filtroBD);
                listaIdMoradorDebitos.clear();
                for (Morador m : listaMoradorFiltro) {
                    listaIdMoradorDebitos.add(m.getId());
                }
                listaDebitosMorador = MoradorFacade.listaDebitosMorador(listaIdMoradorDebitos);
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
                filtroBD.setPrimeiroRegistro(first);
                filtroBD.setQntdRegistros(Long.valueOf(pageSize));
                filtroBD.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroBD.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresDesativados());
                listaMoradorFiltro = MoradorFacade.moradorDstvFiltro(filtroBD);
                return listaMoradorFiltro;
            }
        };
    }

    public void setSindicoMoradorDados(int idMorador) {
        try {
            this.morador = MoradorFacade.moradorPorId(idMorador);
            if ((this.morador.getStatus() == 2)
                    || (this.morador.getStatus() == 3)
                    || (this.morador.getStatus() == 5)) {
                this.morador.getApartamento().setMoradores(DependenteFacade.listaDependentes(this.morador));
            } else if (this.morador.getApartamento() != null) {
                this.morador.getApartamento().setMoradores(new ArrayList<>());
            }
            this.morador.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(this.morador));
            this.logsCadastroMorador = LogBDCadastroMoradorFacade.listaLogCadastroMoradorTipo(idMorador, 0);
            this.anos = LogBDCadastroMoradorFacade.listaAnoCadastroMorador(this.logsCadastroMorador);
            this.anoFiltro = (this.anos.size() > 0) ? this.anos.get(0) : 0;
            iniciaListaBoleto();
            iniciaListaNotificacoesMorador(this.morador);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de morador");
        }
    }

    public void setSindicoMoradorAptoDados(Apartamento apto) {
        try {
            setSindicoMoradorDados(apto.getMoradores().get(0).getId());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de morador");
        }
    }

    public void iniciaListaBoleto() {
        try {
            iniciaListaBaseBoleto();
            this.listaBoleto = BoletoFacade.listaBoletoAnoMorador(this.anoFiltro, this.morador);

            for (int i = 0; i < this.listaBoleto.size(); i++) {
                if (this.listaBoleto.get(i).getTipo() == 1) {
                    Calendar dataBoleto = Calendar.getInstance();
                    dataBoleto.setTime(this.listaBoleto.get(i).getDataReferencia());
                    this.listaBoletoTaxa.set(dataBoleto.get(Calendar.MONTH), this.listaBoleto.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar boletos de morador");
        }
    }

    //Preenche lista de boletos(taxa) para exibição, id's zerados
    public void iniciaListaBaseBoleto() {
        try {
            this.listaBoletoTaxa.clear();

            for (int i = 1; i <= 12; i++) {
                Calendar novaData = Calendar.getInstance();
                novaData.set(this.anoFiltro, i - 1, 1);
                BoletoSGR boletoLista = new BoletoSGR();
                boletoLista.setDataReferencia(novaData.getTime());
                if (verificaPeriodoLog(novaData)) {
                    boletoLista.setDataVencimento(verificaVencimentoLog(novaData));
                    if (boletoLista.getDataVencimento() == null) {
                        boletoLista.setDataVencimento(novaData.getTime());
                    }
                } else {
                    boletoLista.setDataVencimento(null);
                }
                this.listaBoletoTaxa.add(boletoLista);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar boletos de morador");
        }
    }

    /* Validar se morador estava presente no mês e ano solicitado */
    public Boolean verificaPeriodoLog(Calendar data) {
        try {
            Boolean rtn = false;
            Calendar dataInicio = Calendar.getInstance();
            Calendar dataFim = Calendar.getInstance();
            LogBDCadastroMorador log;

            for (int i = 0; i < this.logsCadastroMorador.size(); i++) {
                log = this.logsCadastroMorador.get(i);
                dataInicio.setTime(log.getDataAprovacao());
                dataInicio.set(dataInicio.get(Calendar.YEAR), dataInicio.get(Calendar.MONTH), 1);

                if (log.getDataDesligamento() != null) {
                    dataFim.setTime(log.getDataDesligamento());
                    dataFim.set(dataFim.get(Calendar.YEAR), dataFim.get(Calendar.MONTH), dataFim.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else {
                    dataFim.setTime(this.hoje);
                }

                if (!((data.before(dataInicio)) || (data.after(dataFim)))) {
                    rtn = true;
                    i = this.logsCadastroMorador.size();
                }
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de morador");
            return null;
        }
    }

    /* Verifica mês e ano cadastrado para vencimento */
    public Date verificaVencimentoLog(Calendar data) {
        try {
            Date rtn = null;
            Calendar dataInicio = Calendar.getInstance();
            Calendar dataRtn = Calendar.getInstance();
            LogBDCadastroMorador log;

            for (int i = 0; i < this.logsCadastroMorador.size(); i++) {
                log = this.logsCadastroMorador.get(i);
                dataInicio.setTime(log.getDataAprovacao());
                if ((dataInicio.get(Calendar.YEAR) == data.get(Calendar.YEAR))
                        && (dataInicio.get(Calendar.MONTH) == data.get(Calendar.MONTH))) {
                    dataRtn.set(dataInicio.get(Calendar.YEAR), dataInicio.get(Calendar.MONTH), dataInicio.get(Calendar.DAY_OF_MONTH) + 10);
                    i = this.logsCadastroMorador.size();
                    rtn = dataRtn.getTime();
                }
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar débitos de morador");
            return null;
        }
    }

    /* [1]-Pago [2]-Não cadastrado [3]-Atrasado [4]-Em aberto */
    public int statusPagamento(BoletoSGR boleto) {
        try {
            int rtn = 0;

            if (boleto.getFinanceiro() != null) {
                rtn = 1;
            }

            if (rtn == 0) {
                Calendar data = Calendar.getInstance();
                data.setTime(boleto.getDataReferencia());
                if (!verificaPeriodoLog(data)) {
                    rtn = 2;
                } else {
                    if ((this.hoje.after(boleto.getDataReferencia())) && (this.hoje.after(boleto.getDataVencimento()))) {
                        rtn = 3;
                    } else {
                        rtn = 4;
                    }
                }
            }

            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
            return 0;
        }
    }

    public String alertaDebito(int idMorador) {
        try {
            String rtn = null;
            int tot = 0;
            for (int i = 0; this.listaDebitosMorador.size() > i; i++) {
                if (idMorador == (int) this.listaDebitosMorador.get(i)[0]) {
                    if ((this.listaDebitosMorador.get(i)[4] != null) 
                            &&(int) this.listaDebitosMorador.get(i)[4] > 0) {
                        tot++;
                    }
                    if ((this.listaDebitosMorador.get(i)[5] != null) 
                            &&(int) this.listaDebitosMorador.get(i)[5] > 0) {
                        tot++;
                    }
                    if ((this.listaDebitosMorador.get(i)[6] != null) 
                            &&(int) this.listaDebitosMorador.get(i)[6] > 0) {
                        tot = tot + (int) this.listaDebitosMorador.get(i)[6];
                    }
                    i = this.listaDebitosMorador.size();
                }
            }
            if (tot == 2) {
                rtn = "#DAA520";
            }
            if (tot > 2) {
                rtn = "#FF0000";
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar débitos de morador");
            return null;
        }
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }

    public String nomeMes(int mes) {
        return SgrUtil.nomeMes(mes);
    }

    public String statusNotificacao(Notificacao notifi) {
        if (notifi.getTipo() == 0) {
            return "-";
        } else {
            if (notifi.getBoleto() == null) {
                return "EM ABERTO";
            } else if ((notifi.getBoleto().getFinanceiro() == null)) {
                return "EMITIDO";
            } else {
                return "PAGO";
            }
        }
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public int getAnoFiltro() {
        return anoFiltro;
    }

    public void setAnoFiltro(int anoFiltro) {
        this.anoFiltro = anoFiltro;
    }

    public List<Integer> getAnos() {
        return anos;
    }

    public void setAnos(List<Integer> anos) {
        this.anos = anos;
    }

    public List<LogBDCadastroMorador> getLogsCadastroMorador() {
        return logsCadastroMorador;
    }

    public void setLogsCadastroMorador(List<LogBDCadastroMorador> logsCadastroMorador) {
        this.logsCadastroMorador = logsCadastroMorador;
    }

    public List<BoletoSGR> getListaBoletoTaxa() {
        return listaBoletoTaxa;
    }

    public void setListaBoletoTaxa(List<BoletoSGR> listaBoletoTaxa) {
        this.listaBoletoTaxa = listaBoletoTaxa;
    }

    public LazyDataModel<Notificacao> getListaNotificacoes() {
        return listaNotificacoes;
    }

    public void setListaNotificacoes(LazyDataModel<Notificacao> listaNotificacoes) {
        this.listaNotificacoes = listaNotificacoes;
    }

    public List<BoletoSGR> getListaBoleto() {
        return listaBoleto;
    }

    public void setListaBoleto(List<BoletoSGR> listaBoleto) {
        this.listaBoleto = listaBoleto;
    }

    public LazyDataModel<Object[]> getListaMoradoresDebitos() {
        return listaMoradoresDebitos;
    }

    public void setListaMoradoresDebitos(LazyDataModel<Object[]> listaMoradoresDebitos) {
        this.listaMoradoresDebitos = listaMoradoresDebitos;
    }

    public List<Apartamento> getAptoComMorador() {
        return aptoComMorador;
    }

    public void setAptoComMorador(List<Apartamento> aptoComMorador) {
        this.aptoComMorador = aptoComMorador;
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
}
