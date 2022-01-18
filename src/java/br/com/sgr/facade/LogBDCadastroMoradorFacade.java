package br.com.sgr.facade;

import br.com.sgr.bean.Morador;
import br.com.sgr.dao.LogBDCadastroMoradorDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.logbean.LogBDCadastroMorador;
import br.com.sgr.util.SgrUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class LogBDCadastroMoradorFacade {

    private static final LogBDCadastroMoradorDao logBDCadastroMoradorDao = new LogBDCadastroMoradorDao();

    /*Log de cadastro de novo morador responsável*/
    public static void logCadastroNovoMorador(Morador morador) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);

            LogBDCadastroMorador novoLog = new LogBDCadastroMorador();
            novoLog.setIdMorador(morador.getId());
            novoLog.setDataAprovacao(new Date());

            if (usuario.getFuncionario().getId() != 0) {
                novoLog.setIdPessoaAprovacao(usuario.getFuncionario().getId());
                novoLog.setTipoPessoaAprovacao(0);
            } else {
                novoLog.setIdPessoaAprovacao(usuario.getMorador().getId());
                novoLog.setTipoPessoaAprovacao(1);
            }

            if (morador.getTipo() == 1) {
                novoLog.setTipoMorador(0);
            } else {
                novoLog.setTipoMorador(1);
            }
            logBDCadastroMoradorDao.inserirLog(novoLog);
        } catch (DaoException | ELException e) {
            System.out.println("****Problema ao salvar log de novo morador [Facade]****");
            e.printStackTrace(System.out);
        }
    }

    public static void logDesligaMorador(Morador morador) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);

            LogBDCadastroMorador log = logBDCadastroMoradorDao.ultimoLogMorador(morador.getId());

            if (log != null) {
                log.setDataDesligamento(new Date());
                if (usuario.getFuncionario().getId() != 0) {
                    log.setIdPessoaDesligamento(usuario.getFuncionario().getId());
                    log.setTipoPessoaDesligamento(0);
                } else {
                    log.setIdPessoaDesligamento(usuario.getMorador().getId());
                    log.setTipoPessoaDesligamento(1);
                }
                logBDCadastroMoradorDao.atualizarLogBDCadastro(log);
            } else {
                System.out.println("Não foi possível encontrar o ultimo log do morador cadastrado");
            }
        } catch (DaoException | ELException e) {
            System.out.println("****Problema ao salvar log de desativação de morador [Facade]****");
            e.printStackTrace(System.out);
        }
    }

    public static LogBDCadastroMorador ultimoLogMorador(Morador morador) {
        try {
            return logBDCadastroMoradorDao.ultimoLogMorador(morador.getId());
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados log de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Object[]> previsaoOrcamentoMes(int ano, int mes) {
        try {
            Calendar data = Calendar.getInstance();
            data.set(ano, mes - 1, 1, 0, 0, 0);
            Date dataInicial = data.getTime();
            data.set(ano, mes - 1, data.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date dataFinal = data.getTime();
            return logBDCadastroMoradorDao.previsaoOrcamentoMes(0, dataInicial, dataFinal);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de orçamento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Double anoPrimeiroMoradorCadastro(int tipo) {
        try {
            Double ano = logBDCadastroMoradorDao.anoPrimeiroMoradorCadastro(tipo);
            return (ano != null && ano >= 1500) ? ano : 0.0;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de filtro [ANO]";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<LogBDCadastroMorador> listaLogCadastroMoradorTipo(int idMorador, int tipoMorador) {
        try {
            return logBDCadastroMoradorDao.listaLogCadastroMoradorTipo(idMorador, tipoMorador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Integer> listaAnoCadastroMorador(List<LogBDCadastroMorador> logs) {
        List<Integer> anos = new ArrayList<>();
        Calendar dataLogInicial = Calendar.getInstance();
        Calendar dataLogFinal = Calendar.getInstance();

        if ((logs != null) && (logs.size() > 0)) {
            for (LogBDCadastroMorador l : logs) {
                dataLogInicial.setTime(l.getDataAprovacao());
                if (l.getDataDesligamento() == null) {
                    dataLogFinal = Calendar.getInstance();
                } else {
                    dataLogFinal.setTime(l.getDataDesligamento());
                }

                do {
                    anos.add(dataLogFinal.get(Calendar.YEAR));
                    dataLogFinal.set(dataLogFinal.get(Calendar.YEAR) - 1, 1, 1);
                } while (dataLogFinal.get(Calendar.YEAR) >= dataLogInicial.get(Calendar.YEAR));
            }
        }
        anos = anos.stream().distinct().collect(Collectors.toList());
        Collections.sort(anos);
        Collections.reverse(anos);
        return anos;
    }
}
