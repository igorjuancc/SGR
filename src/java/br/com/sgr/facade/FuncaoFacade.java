package br.com.sgr.facade;

import br.com.sgr.bean.Funcao;
import br.com.sgr.dao.FuncaoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class FuncaoFacade {

    private static final FuncaoDao funcaoDao = new FuncaoDao();

    public static List<Funcao> funcaoLista() {
        try {
            return funcaoDao.funcaoLista();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar funções de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Funcao funcaoPorId(int id) {
        try {
            return funcaoDao.funcaoPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar função de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
