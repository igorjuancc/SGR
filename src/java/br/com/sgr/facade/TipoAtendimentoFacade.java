package br.com.sgr.facade;

import br.com.sgr.bean.TipoAtendimento;
import br.com.sgr.dao.TipoAtendimentoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class TipoAtendimentoFacade {

    private static final TipoAtendimentoDao tipoAtendimentoDao = new TipoAtendimentoDao();

    public static List<TipoAtendimento> listaTipoAtendimento() {
        try {
            return tipoAtendimentoDao.listaTipoAtendimento();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static TipoAtendimento tipoAtendimentoPorId(int id) {
        try {
            return tipoAtendimentoDao.tipoAtendimentoPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de tipo de atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
