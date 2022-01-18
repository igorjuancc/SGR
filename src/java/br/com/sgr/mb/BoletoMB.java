package br.com.sgr.mb;

import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.facade.BoletoFacade;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.jrimum.bopepo.view.BoletoViewer;

@ManagedBean
@Named(value = "boletoMB")
@ViewScoped
public class BoletoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private int anoFiltro;
    private List<BoletoSGR> listaBoletoTaxa;
    private List<BoletoSGR> listaBoletoMulta;
    private List<BoletoSGR> boletosPagos;
    private List<Integer> listaAnoBoleto;
    private Date hoje;
    private byte[] previewBoleto;

    @PostConstruct
    public void init() {
        try {
            this.listaAnoBoleto = new ArrayList<>();
            this.listaBoletoTaxa = new ArrayList<>();
            this.hoje = new Date();
            iniciaFiltroAno();
            iniciaListaBoleto();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaFiltroAno() {
        try {
            Calendar hojeCalendar = Calendar.getInstance();
            Calendar dataCadastro = Calendar.getInstance();
            dataCadastro.setTime(login.getMorador().getDataCadastro());
            do {
                listaAnoBoleto.add(hojeCalendar.get(Calendar.YEAR));
                hojeCalendar.set(hojeCalendar.get(Calendar.YEAR) - 1, 1, 1);
            } while (hojeCalendar.get(Calendar.YEAR) >= dataCadastro.get(Calendar.YEAR));
            this.anoFiltro = this.listaAnoBoleto.get(0);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
        }
    }

    public void iniciaListaBoleto() {
        try {
            iniciaListaBaseBoleto();
            this.listaBoletoMulta = BoletoFacade.listaBoletoAnoMorador(this.anoFiltro, this.login.getMorador());
            this.boletosPagos = BoletoFacade.listaBoletoTaxaAnoMorador(this.anoFiltro, this.login.getMorador());

            for (int i = 0; i < this.listaBoletoMulta.size(); i++) {
                if (this.listaBoletoMulta.get(i).getTipo() == 1) {
                    Calendar dataBoleto = Calendar.getInstance();
                    dataBoleto.setTime(this.listaBoletoMulta.get(i).getDataReferencia());
                    this.listaBoletoTaxa.set(dataBoleto.get(Calendar.MONTH), this.listaBoletoMulta.get(i));
                }
            }
            this.listaBoletoMulta.removeAll(this.listaBoletoTaxa);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
        }
    }

    //Preenche lista de boletos(taxa) para exibição, id's zerados
    public void iniciaListaBaseBoleto() {
        try {
            this.listaBoletoTaxa.clear();
            Calendar dataCadastro = Calendar.getInstance();
            dataCadastro.setTime(this.login.getMorador().getDataCadastro());

            for (int i = 1; i <= 12; i++) {
                BoletoSGR boletoLista = new BoletoSGR();
                Calendar novaData = Calendar.getInstance();
                novaData.set(this.anoFiltro, i - 1, 1);
                boletoLista.setDataReferencia(novaData.getTime());

                if ((this.hoje.after(novaData.getTime())) || (this.hoje == novaData.getTime())) {
                    if ((dataCadastro.get(Calendar.YEAR) == novaData.get(Calendar.YEAR))
                            && (dataCadastro.get(Calendar.MONTH) == novaData.get(Calendar.MONTH))) {
                        novaData.set(this.anoFiltro, i - 1, dataCadastro.get(Calendar.DAY_OF_MONTH) + 10);
                    } else {
                        novaData.set(this.anoFiltro, i - 1, 10);
                    }

                    boletoLista.setDataVencimento(novaData.getTime());
                    boletoLista.setTipo(1);
                    this.listaBoletoTaxa.add(boletoLista);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
        }
    }

    /* [1]-Pago [2]-Não cadastrado [3]-Atrasado [4]-Em aberto */
    public int statusPagamento(BoletoSGR boleto) {
        try {
            int rtn = 0;

            for (BoletoSGR b : this.boletosPagos) {
                if (boleto.getId() == b.getId()) {
                    rtn = 1;
                }
            }

            if (rtn == 0) {
                if (this.login.getMorador().getDataCadastro().after(boleto.getDataReferencia())
                        && this.login.getMorador().getDataCadastro().after(boleto.getDataVencimento())) {
                    rtn = 2;
                } else {
                    if (this.hoje.after(boleto.getDataVencimento())) {
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

    public void novoBoleto(BoletoSGR boleto) {
        try {
            boleto.setMorador(this.login.getMorador());
            BoletoViewer boletoViewer = new BoletoViewer(BoletoFacade.novoBoleto(boleto));
            this.previewBoleto = boletoViewer.getPdfAsByteArray();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
        }
    }

    public void novoBoletoNotificacao(Notificacao notificacao) {
        try {
            if (notificacao.getBoleto() == null) {
                BoletoSGR boleto = new BoletoSGR();
                boleto.setDataReferencia(notificacao.getDataEmissao());
                boleto.setMorador(notificacao.getMorador());
                boleto.setNotificacao(notificacao);
                boleto.setTipo(2);
                notificacao.setBoleto(boleto);
            }
            BoletoViewer boletoViewer = new BoletoViewer(BoletoFacade.novoBoleto(notificacao.getBoleto()));
            this.previewBoleto = boletoViewer.getPdfAsByteArray();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de boletos");
        }
    }

    public String nomeMes(int mes) {
        return SgrUtil.nomeMes(mes);
    }

    public String verBoletoBase64(byte[] boleto) {
        return Base64.getEncoder().encodeToString(boleto);
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public List<BoletoSGR> getListaBoletoTaxa() {
        return listaBoletoTaxa;
    }

    public void setListaBoletoTaxa(List<BoletoSGR> listaBoletoTaxa) {
        this.listaBoletoTaxa = listaBoletoTaxa;
    }

    public List<Integer> getListaAnoBoleto() {
        return listaAnoBoleto;
    }

    public void setListaAnoBoleto(List<Integer> listaAnoBoleto) {
        this.listaAnoBoleto = listaAnoBoleto;
    }

    public int getAnoFiltro() {
        return anoFiltro;
    }

    public void setAnoFiltro(int anoFiltro) {
        this.anoFiltro = anoFiltro;
    }

    public List<BoletoSGR> getListaBoletoMulta() {
        return listaBoletoMulta;
    }

    public void setListaBoletoMulta(List<BoletoSGR> listaBoletoMulta) {
        this.listaBoletoMulta = listaBoletoMulta;
    }

    public Date getHoje() {
        return hoje;
    }

    public void setHoje(Date hoje) {
        this.hoje = hoje;
    }

    public byte[] getPreviewBoleto() {
        return previewBoleto;
    }

    public void setPreviewBoleto(byte[] previewBoleto) {
        this.previewBoleto = previewBoleto;
    }
}
