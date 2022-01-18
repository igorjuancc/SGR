package br.com.sgr.mb;

import br.com.sgr.bean.Assembleia;
import br.com.sgr.facade.AssembleiaFacade;
import br.com.sgr.facade.AtendimentoFacade;
import br.com.sgr.facade.BoletoFacade;
import br.com.sgr.facade.MensagemFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@ManagedBean
@Named(value = "menuMB")
@ViewScoped
public class MenuMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Assembleia assembleia;
    private int totalMensagem;
    private int totalNovosMoradores;
    private int totalMoradoresDebitos;
    private int totalAtendimentosAbertos;
    private int totalAtendimentosReabertos;
    private int totalAtendimentosProcesso;
    private int totalNovasReceitas;
    private Boolean renderizar;

    @PostConstruct
    public void init() {
        try {
            this.assembleia = AssembleiaFacade.assembleiaAberta();
            this.renderizar = (this.assembleia != null);
            if (this.login.getFuncionario().getId() != 0) {
                this.totalMensagem = MensagemFacade.totalMensagens(this.login.getFuncionario(), 5);
                if (this.login.getFuncionario().getFuncao().getId() == 1) {
                    int total;
                    this.totalAtendimentosAbertos = AtendimentoFacade.totalAtendimentosPorStatus(0);
                    this.totalAtendimentosReabertos = AtendimentoFacade.totalAtendimentosPorStatus(3);
                    this.totalAtendimentosProcesso = AtendimentoFacade.totalAtendimentosPorStatus(2);
                    this.totalNovosMoradores = MoradorFacade.totalMoradoresPorStatus(0);
                    this.totalMoradoresDebitos = MoradorFacade.totalListaDebitos();
                    this.totalNovasReceitas = BoletoFacade.totalBoletosSemRegistro();

                    total = this.totalAtendimentosAbertos
                            + this.totalAtendimentosReabertos
                            + this.totalAtendimentosProcesso
                            + this.totalNovosMoradores
                            + this.totalMoradoresDebitos
                            + this.totalNovasReceitas;

                    this.renderizar = total > 0;
                }
            } else {
                this.totalMensagem = MensagemFacade.totalMensagens(this.login.getMorador(), 5);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar alertas";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Assembleia getAssembleia() {
        return assembleia;
    }

    public void setAssembleia(Assembleia assembleia) {
        this.assembleia = assembleia;
    }

    public int getTotalMensagem() {
        return totalMensagem;
    }

    public void setTotalMensagem(int totalMensagem) {
        this.totalMensagem = totalMensagem;
    }

    public Boolean getRenderizar() {
        return renderizar;
    }

    public void setRenderizar(Boolean renderizar) {
        this.renderizar = renderizar;
    }

    public int getTotalNovosMoradores() {
        return totalNovosMoradores;
    }

    public void setTotalNovosMoradores(int totalNovosMoradores) {
        this.totalNovosMoradores = totalNovosMoradores;
    }

    public int getTotalMoradoresDebitos() {
        return totalMoradoresDebitos;
    }

    public void setTotalMoradoresDebitos(int totalMoradoresDebitos) {
        this.totalMoradoresDebitos = totalMoradoresDebitos;
    }

    public int getTotalAtendimentosAbertos() {
        return totalAtendimentosAbertos;
    }

    public void setTotalAtendimentosAbertos(int totalAtendimentosAbertos) {
        this.totalAtendimentosAbertos = totalAtendimentosAbertos;
    }

    public int getTotalAtendimentosReabertos() {
        return totalAtendimentosReabertos;
    }

    public void setTotalAtendimentosReabertos(int totalAtendimentosReabertos) {
        this.totalAtendimentosReabertos = totalAtendimentosReabertos;
    }

    public int getTotalAtendimentosProcesso() {
        return totalAtendimentosProcesso;
    }

    public void setTotalAtendimentosProcesso(int totalAtendimentosProcesso) {
        this.totalAtendimentosProcesso = totalAtendimentosProcesso;
    }

    public int getTotalNovasReceitas() {
        return totalNovasReceitas;
    }

    public void setTotalNovasReceitas(int totalNovasReceitas) {
        this.totalNovasReceitas = totalNovasReceitas;
    }
}
