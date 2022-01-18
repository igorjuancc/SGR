package br.com.sgr.util;

import br.com.sgr.mb.LoginMB;
import java.io.IOException;
import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class VerificarLogin implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            String pagina = ctx.getViewRoot().getViewId();                           //Nome da pagina atual
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);
            ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
            int opcCase = 0;

            if (usuario.getFuncionario().getFuncao() != null) {
                if ((usuario.getFuncionario().getStatus() == 0) || (usuario.getFuncionario().getStatus() == 3)) {
                    opcCase = 4;
                } else if (usuario.getFuncionario().getFuncao().getId() == 1) {
                    opcCase = 1; //Opção para síndico
                } else {
                    opcCase = 2; //Opção para porteiro
                }
            } else if (usuario.getMorador().getTipo() == 1) {
                opcCase = (usuario.getMorador().getStatus() == 5) ? 5 : 3;      //Opção para morador (1 - Responsavel pelo apto, 2 - Dependente)                       
            }

            switch (opcCase) {
                case 0:
                    if (!(pagina.equals("/index.xhtml"))
                            && !(pagina.equals("/MoradorLogin.xhtml"))
                            && !(pagina.equals("/FuncionarioLogin.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))
                            && !(pagina.equals("/MoradorEsqSenha.xhtml"))
                            && !(pagina.equals("/CadastroMorador.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/index.jsf");
                    }
                    break;
                case 1:
                    if (!(pagina.equals("/sindico/SindicoHome.xhtml"))
                            && !(pagina.equals("/sindico/SindicoNovoMorador.xhtml"))
                            && !(pagina.equals("/sindico/SindicoAcessoMorador.xhtml"))
                            && !(pagina.equals("/sindico/SindicoListaNoticia.xhtml"))
                            && !(pagina.equals("/sindico/SindicoFuncionario.xhtml"))
                            && !(pagina.equals("/sindico/SindicoDados.xhtml"))
                            && !(pagina.equals("/sindico/SindicoFinanceiro.xhtml"))
                            && !(pagina.equals("/sindico/SindicoMensagem.xhtml"))
                            && !(pagina.equals("/sindico/SindicoVagas.xhtml"))
                            && !(pagina.equals("/sindico/SindicoSalao.xhtml"))
                            && !(pagina.equals("/sindico/SindicoAssembleia.xhtml"))
                            && !(pagina.equals("/sindico/SindicoAssembleiaVer.xhtml"))
                            && !(pagina.equals("/sindico/SindicoAtendimento.xhtml"))
                            && !(pagina.equals("/sindico/SindicoAdvertencia.xhtml"))
                            && !(pagina.equals("/sindico/SindicoBalanco.xhtml"))
                            && !(pagina.equals("/sindico/SindicoMoradores.xhtml"))
                            && !(pagina.equals("/sindico/SindicoRelatorio.xhtml"))
                            && !(pagina.equals("/RelatorioView.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/sindico/SindicoHome.jsf");
                    }
                    break;
                case 2:
                    if (!(pagina.equals("/porteiro/PorteiroHome.xhtml"))
                            && !(pagina.equals("/porteiro/PorteiroDados.xhtml"))
                            && !(pagina.equals("/porteiro/PorteiroMensagem.xhtml"))
                            && !(pagina.equals("/porteiro/PorteiroVisitaHoje.xhtml"))
                            && !(pagina.equals("/porteiro/PorteiroMorador.xhtml"))
                            && !(pagina.equals("/porteiro/PorteiroVeiculos.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/porteiro/PorteiroHome.jsf");
                    }
                    break;
                case 3:
                    if (!(pagina.equals("/morador/MoradorHome.xhtml"))
                            && !(pagina.equals("/morador/MoradorDados.xhtml"))
                            && !(pagina.equals("/morador/MoradorDependente.xhtml"))
                            && !(pagina.equals("/morador/MoradorVeiculo.xhtml"))
                            && !(pagina.equals("/morador/MoradorVisitante.xhtml"))
                            && !(pagina.equals("/morador/MoradorVisita.xhtml"))
                            && !(pagina.equals("/morador/MoradorAtendimento.xhtml"))
                            && !(pagina.equals("/morador/MoradorVagas.xhtml"))
                            && !(pagina.equals("/morador/MoradorSalao.xhtml"))
                            && !(pagina.equals("/morador/MoradorMensagem.xhtml"))
                            && !(pagina.equals("/morador/MoradorAssembleia.xhtml"))
                            && !(pagina.equals("/morador/MoradorTaxa.xhtml"))
                            && !(pagina.equals("/morador/MoradorBalanco.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/morador/MoradorHome.jsf");
                    }
                    break;
                case 4:
                    if (!(pagina.equals("/PrimeiroAcessoFuncionario.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/PrimeiroAcessoFuncionario.jsf");
                    }
                    break;
                case 5:
                    if (!(pagina.equals("/morador/MoradorNovaSenha.xhtml"))
                            && !(pagina.equals("/Erro.xhtml"))) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/morador/MoradorNovaSenha.jsf");
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException | ELException e) {
            //Printa o Stacktrace numa mensagem e direcionar para página de erro com as informações
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}
