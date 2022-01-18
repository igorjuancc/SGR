package br.com.sgr.facade;

import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.dao.CategoriaFinanceiroDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class CategoriaFinanceiroFacade {

    private static final CategoriaFinanceiroDao categoriaFinanceiroDao = new CategoriaFinanceiroDao();

    public static List<CategoriaFinanceiro> categoriasFinanceiro() {
        try {
            return categoriaFinanceiroDao.categoriasFinanceiro();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de categoria financeira";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static CategoriaFinanceiro categoriaFinanceiroPorId(int id) {
        try {
            return categoriaFinanceiroDao.categoriaFinanceiroPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de categoria financeira";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
