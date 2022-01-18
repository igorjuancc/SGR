package br.com.sgr.facade;

import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.RegistroES;
import br.com.sgr.bean.Visitante;
import br.com.sgr.dao.RegistroESDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.RegistroESException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.SgrUtil;
import java.util.Date;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class RegistroESFacade {

    private static final RegistroESDao registroESDao = new RegistroESDao();

    public static void novoRegistroEntradaVisitante(Visitante visitante) throws RegistroESException {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);

            if (visitante == null) {
                throw new RegistroESException("Visitante não encontrado");
            } else if ((visitante.getImagem() == null) || (visitante.getImagem().getId() == 0)) {
                throw new RegistroESException("Necessário inserir uma imagem para o visitante");
            } else if ((usuario == null) || (usuario.getFuncionario() == null) || (usuario.getFuncionario().getId() == 0)) {
                throw new RegistroESException("Funcionário não encontrado");
            } else {
                visitante.setRegEntradaSaida(registroESDao.listaUltimosRegistroPessoa(visitante));
                if ((visitante.getRegEntradaSaida() != null) && (!visitante.getRegEntradaSaida().isEmpty())) {
                    if (visitante.getRegEntradaSaida().get(0).getDataEntrada() == null) {
                        throw new RegistroESException("Existe um registro de entrada em aberto para visitante");
                    } else if (visitante.getRegEntradaSaida().get(0).getDataSaida() == null) {
                        throw new RegistroESException("Existe um registro de saida em aberto para visitante");
                    }
                }
            }
            RegistroES registro = new RegistroES();
            registro.setFuncionarioEntrada(usuario.getFuncionario());
            registro.setDataEntrada(new Date());
            registro.setPessoa(visitante);
            registro.setTipoPessoa("V");
            registroESDao.salvarRegistroPessoa(registro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar entrada de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void registroSaidaVisitante(Visitante visitante) throws RegistroESException {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application app = ctx.getApplication();
            LoginMB usuario = app.evaluateExpressionGet(ctx, "#{LoginMB}", LoginMB.class);

            if (visitante == null) {
                throw new RegistroESException("Visitante não encontrado");
            } else if ((visitante.getRegEntradaSaida() == null) || (visitante.getRegEntradaSaida().isEmpty())) {
                throw new RegistroESException("Não encontrado registros do visitante");
            } else if ((usuario == null) || (usuario.getFuncionario() == null) || (usuario.getFuncionario().getId() == 0)) {
                throw new RegistroESException("Funcionário não encontrado");
            } else {
                if ((visitante.getRegEntradaSaida().get(0) == null) || (visitante.getRegEntradaSaida().get(0).getId() == 0)) {
                    throw new RegistroESException("Não encontrado registro de entrada do visitante");
                } else if (visitante.getRegEntradaSaida().get(0).getDataEntrada() == null) {
                    throw new RegistroESException("Entrada do visitante não foi registrada");
                } else {
                    visitante.getRegEntradaSaida().get(0).setDataSaida(new Date());
                    visitante.getRegEntradaSaida().get(0).setFuncionarioSaida(usuario.getFuncionario());
                    registroESDao.atualizarRegistroPessoa(visitante.getRegEntradaSaida().get(0));
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar saída de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<RegistroES> listaUltimosRegistroPessoa(Pessoa pessoa) {
        try {
            return registroESDao.listaUltimosRegistroPessoa(pessoa);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar registros de entrada e saída";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<RegistroES> listaRegistroDataPessoa(Visitante visitante) {
        try {
            if (visitante.getVisitas().get(0).getDataFim() != null) {
                visitante.getVisitas().get(0).getDataFim().setTime(visitante.getVisitas().get(0).getDataFim().getTime() + 86399000);
            }
            return registroESDao.listaRegistroDataPessoa(visitante);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar registros de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
