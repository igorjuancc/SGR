package br.com.sgr.mb;

import br.com.sgr.exception.RelatorioException;
import br.com.sgr.facade.RelatorioFacade;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PF;

@ManagedBean
@Named(value = "relatorioMB")
@ViewScoped
public class RelatorioMB implements Serializable {

    private byte[] previewRelatorio;
    private int infoRelatorio;
    private Date dataInicial;
    private Date dataFinal;

    public void relatorioEntradaSaidaVisitantes() {
        try {
            this.previewRelatorio = RelatorioFacade.relatorioEntradaSaidaVisitantes(dataInicial, dataFinal);
            if (this.previewRelatorio.length <= 961) {
                throw new RelatorioException("Não há dados para as datas informadas");
            }
            PF.current().executeScript("PF('divViewFormRelatorio').hide()");
            PF.current().executeScript("PF('divViewRelatorio').show()");
        } catch (RelatorioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PF.current().ajax().update("formRelatorio");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
        }
    }
    
    public void relatorioAlteracaoDadosPessoais() {
        try {
            this.previewRelatorio = RelatorioFacade.relatorioAlteracaoDadosPessoais(dataInicial, dataFinal);
            if (this.previewRelatorio.length <= 961) {
                throw new RelatorioException("Não há dados para as datas informadas");
            }
            PF.current().executeScript("PF('divViewFormRelatorio').hide()");
            PF.current().executeScript("PF('divViewRelatorio').show()");
        } catch (RelatorioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PF.current().ajax().update("formRelatorio");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
        }
    }
    
    public void relatorioAlteracaoAcesso() {
        try {
            this.previewRelatorio = RelatorioFacade.relatorioAlteracaoAcesso(dataInicial, dataFinal);
            if (this.previewRelatorio.length <= 961) {
                throw new RelatorioException("Não há dados para as datas informadas");
            }
            PF.current().executeScript("PF('divViewFormRelatorio').hide()");
            PF.current().executeScript("PF('divViewRelatorio').show()");
        } catch (RelatorioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PF.current().ajax().update("formRelatorio");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
        }
    }
    
    public void relatorioExclusaoPessoa() {
        try {
            this.previewRelatorio = RelatorioFacade.relatorioExclusaoPessoa(dataInicial, dataFinal);
            if (this.previewRelatorio.length <= 961) {
                throw new RelatorioException("Não há dados para as datas informadas");
            }
            PF.current().executeScript("PF('divViewFormRelatorio').hide()");
            PF.current().executeScript("PF('divViewRelatorio').show()");
        } catch (RelatorioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
                PF.current().ajax().update("formRelatorio");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar relatório");
        }
    }

    public String verRelatorioBase64(byte[] boleto) {
        return Base64.getEncoder().encodeToString(boleto);
    }

    public byte[] getPreviewRelatorio() {
        return previewRelatorio;
    }

    public void setPreviewRelatorio(byte[] previewRelatorio) {
        this.previewRelatorio = previewRelatorio;
    }

    public int getInfoRelatorio() {
        return infoRelatorio;
    }

    public void setInfoRelatorio(int infoRelatorio) {
        this.infoRelatorio = infoRelatorio;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }
}
