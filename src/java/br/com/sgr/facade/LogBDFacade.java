/*

Operação    - [1]Adicionar [2]Remover [3]Atualizar [4]Aprovar [5]Desligar [6]Modificar Acesso [7]Religar [8]Emitir [9]Recuperar acesso
Tipo objeto - [0]Responsavel [1]Dependente [2]Funcionario [3]Visitante [4]Noticia [5]Arquivo [6]Veiculo [7]Vaga Estacionamento 
              [8]Notificacao [9]Boleto [10]Infração [11]Assembleia

 */
package br.com.sgr.facade;

import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Infracao;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Noticia;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.bean.Vaga;
import br.com.sgr.bean.Veiculo;
import br.com.sgr.bean.Visitante;
import br.com.sgr.dao.LogBDDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.logbean.LogBD;
import java.util.Date;
import java.util.List;
import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class LogBDFacade {

    private static final LogBDDao logBDDao = new LogBDDao();

    public static void inserirLog(int operacao, Object objeto) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);

            LogBD novoLog = new LogBD();
            novoLog.setOperacao(operacao);
            novoLog.setDataLog(new Date());
            if (usuario.getFuncionario().getId() != 0) {
                novoLog.setIdUsuario(usuario.getFuncionario().getId());
            } else {
                novoLog.setIdUsuario(usuario.getMorador().getId());
            }

            switch (objeto.getClass().getSimpleName()) {
                case "Morador":
                    Morador m = (Morador) objeto;
                    novoLog.setIdObjeto(m.getId());
                    novoLog.setTipoObjeto(1);
                    if (operacao == 2) {
                        novoLog.setDetalhe(m.getCpf()); //CPF Morador deletado
                    }
                    if ((operacao == 5) || (operacao == 7)) {
                        novoLog.setDetalhe("APTO" + Integer.toString(m.getApartamento().getId())); //Apartamento do morador desligado
                    }
                    if (m.getTipo() == 1) {
                        novoLog.setTipoObjeto(0);
                    } else {
                        novoLog.setTipoObjeto(1);
                    }
                    break;
                case "Funcionario":
                    Funcionario f = (Funcionario) objeto;
                    novoLog.setIdObjeto(f.getId());
                    novoLog.setTipoObjeto(2);
                    novoLog.setDetalhe(f.getCpf());
                    break;
                case "Visitante":
                    Visitante vs = (Visitante) objeto;
                    novoLog.setIdObjeto(vs.getId());
                    novoLog.setTipoObjeto(3);
                    if (operacao == 2) {
                        novoLog.setDetalhe(vs.getCpf()); //CPF Visitante deletado
                    }
                    break;
                case "Noticia":
                    Noticia n = (Noticia) objeto;
                    novoLog.setTipoObjeto(4);
                    novoLog.setIdObjeto(n.getId());
                    break;
                case "Veiculo":
                    Veiculo v = (Veiculo) objeto;
                    novoLog.setIdObjeto(v.getId());
                    novoLog.setTipoObjeto(6);
                    novoLog.setDetalhe(v.getPlaca());
                    break;
                case "Vaga":
                    Vaga vg = (Vaga) objeto;
                    if (vg.getApartamento() != null) {
                        novoLog.setDetalhe("APTO" + Integer.toString(vg.getApartamento().getId()));
                    }
                    novoLog.setIdObjeto(vg.getId());
                    novoLog.setTipoObjeto(7);
                    break;
                case "Notificacao":
                    Notificacao not = (Notificacao) objeto;
                    if (not.getTipo() == 0) {                   //Notificação ou multa
                        novoLog.setDetalhe("N");
                    } else {
                        novoLog.setDetalhe("M");
                    }
                    if (not.getMorador() != null) {
                        novoLog.setDetalhe(novoLog.getDetalhe() + Integer.toString(not.getMorador().getId()));
                    }
                    novoLog.setIdObjeto(not.getId());
                    novoLog.setTipoObjeto(8);
                    break;
                case "BoletoSGR":
                    BoletoSGR boleto = (BoletoSGR) objeto;
                    if (boleto.getTipo() == 1) {
                        novoLog.setDetalhe("TC");                //Taxa de Condominio ou Multa                    
                    } else {
                        novoLog.setDetalhe("M");
                    }
                    novoLog.setIdObjeto(boleto.getId());
                    novoLog.setTipoObjeto(9);
                    break;
                case "Infracao":
                    Infracao i = (Infracao) objeto;
                    novoLog.setIdObjeto(i.getId());
                    novoLog.setTipoObjeto(10);
                    break;
                case "Assembleia":
                    Assembleia a = (Assembleia) objeto;
                    novoLog.setIdObjeto(a.getId());
                    novoLog.setTipoObjeto(11);
                    novoLog.setDetalhe(a.getTipo() != 1 ? "AGE" : "AGO");
                    break;
                default:
                    novoLog.setTipoObjeto(0);
                    novoLog.setIdObjeto(0);
                    novoLog.setDetalhe("OBJETO NAO INFORMADO");
                    break;
            }
            logBDDao.inserirLog(novoLog);        
        } catch (DaoException | ELException e) {
            System.out.println("****Problema ao salvar log em BD [Facade]****");
            e.printStackTrace(System.out);
        }
    }

    public static List<Integer> listaAnoObjetoOp(int obj, int op) throws DaoException {
        try {
            return logBDDao.listaAnoObjetoOp(obj, op);
        } catch (DaoException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("****Problema ao listar ano de LogBD objeto/operacao [Facade]****" + e);
            e.printStackTrace();
            throw e;
        }
    }
}
