package br.com.sgr.facade;

import br.com.sgr.dao.RelatorioDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.RelatorioException;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

public class RelatorioFacade {

    private static final RelatorioDao relatorioDao = new RelatorioDao();

    public static byte[] relatorioEntradaSaidaVisitantes(Date dataInicial, Date dataFinal) throws RelatorioException {
        try {
            if ((dataInicial == null)) {
                throw new RelatorioException("Data inicial inválida");
            } else if ((dataFinal == null) || (dataFinal.before(dataInicial))) {
                throw new RelatorioException("Data de saída não pode ser anterior a data de entrada");
            } else {
                String jasper = "Relatorio/RelatorioEntradaSaida.jasper";
                String host = "http://localhost:8080/SGR/";
                URL jasperURL = new URL(host + jasper);
                HashMap params = new HashMap();
                params.put("dataInicio", dataInicial);
                params.put("dataFim", dataFinal);
                return JasperRunManager.runReportToPdf(jasperURL.openStream(),
                        params,
                        connectionHibernate());
            }
        } catch (IOException | JRException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao gerar relatório";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        } finally {
            finalizaSessaoHibernate();
        }
    }
    
    public static byte[] relatorioAlteracaoDadosPessoais(Date dataInicial, Date dataFinal) throws RelatorioException {
        try {
            if ((dataInicial == null)) {
                throw new RelatorioException("Data inicial inválida");
            } else if ((dataFinal == null) || (dataFinal.before(dataInicial))) {
                throw new RelatorioException("Data inicial não pode ser anterior a data final");
            } else {
                String jasper = "Relatorio/RelatorioAlteracaoDados.jasper";
                String host = "http://localhost:8080/SGR/";
                URL jasperURL = new URL(host + jasper);
                HashMap params = new HashMap();
                params.put("dataInicio", dataInicial);
                params.put("dataFim", dataFinal);
                return JasperRunManager.runReportToPdf(jasperURL.openStream(),
                        params,
                        connectionHibernate());
            }
        } catch (IOException | JRException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao gerar relatório";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        } finally {
            finalizaSessaoHibernate();
        }
    }
    
    public static byte[] relatorioAlteracaoAcesso(Date dataInicial, Date dataFinal) throws RelatorioException {
        try {
            if ((dataInicial == null)) {
                throw new RelatorioException("Data inicial inválida");
            } else if ((dataFinal == null) || (dataFinal.before(dataInicial))) {
                throw new RelatorioException("Data inicial não pode ser anterior a data final");
            } else {
                String jasper = "Relatorio/RelatorioAlteracaoAcesso.jasper";
                String host = "http://localhost:8080/SGR/";
                URL jasperURL = new URL(host + jasper);
                HashMap params = new HashMap();
                params.put("dataInicio", dataInicial);
                params.put("dataFim", dataFinal);
                return JasperRunManager.runReportToPdf(jasperURL.openStream(),
                        params,
                        connectionHibernate());
            }
        } catch (IOException | JRException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao gerar relatório";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        } finally {
            finalizaSessaoHibernate();
        }
    }
    
    public static byte[] relatorioExclusaoPessoa(Date dataInicial, Date dataFinal) throws RelatorioException {
        try {
            if ((dataInicial == null)) {
                throw new RelatorioException("Data inicial inválida");
            } else if ((dataFinal == null) || (dataFinal.before(dataInicial))) {
                throw new RelatorioException("Data inicial não pode ser anterior a data final");
            } else {
                String jasper = "Relatorio/RelatorioExclusaoPessoa.jasper";
                String host = "http://localhost:8080/SGR/";
                URL jasperURL = new URL(host + jasper);
                HashMap params = new HashMap();
                params.put("dataInicio", dataInicial);
                params.put("dataFim", dataFinal);
                return JasperRunManager.runReportToPdf(jasperURL.openStream(),
                        params,
                        connectionHibernate());
            }
        } catch (IOException | JRException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao gerar relatório";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        } finally {
            finalizaSessaoHibernate();
        }
    }

    private static Connection connectionHibernate() {
        try {
            return relatorioDao.connectionHibernate();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema de conexão com o banco de dados";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    private static void finalizaSessaoHibernate() {
        try {
            relatorioDao.finalizaSessaoHibernate();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao finalizar conexão com o banco de dados";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }
}
