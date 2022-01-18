package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.dao.ArquivoDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class ArquivoFacade {

    private static final ArquivoDao arquivoDao = new ArquivoDao();

    public static void apagarArquivo(Arquivo arquivo) throws ArquivoException {
        try {
            arquivoDao.apagarArquivo(arquivo);
         } catch (DaoException e) {
            throw new ArquivoException("Houve um problema ao apagar arquivo do banco de dados", e);
        }
    }

    public static void salvarArquivo(Arquivo arquivo) throws ArquivoException {
        try {
            arquivoDao.salvarArquivo(arquivo);
        } catch (DaoException e) {
            throw new ArquivoException("Houve um problema ao salvar arquivo no banco de dados", e);
        }
    }

    public static List<Arquivo> listaArquivoQuestao(int idQuestao) {
        try {
            return arquivoDao.listaArquivoQuestao(idQuestao);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar buscar arquivos de questão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
    
    public static List<Arquivo> listaArquivoNotificacao(int idNotificacao) {
        try {
            return arquivoDao.listaArquivoNotificacao(idNotificacao);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar buscar arquivos de notificacao";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
