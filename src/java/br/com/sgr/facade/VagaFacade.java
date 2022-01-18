package br.com.sgr.facade;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Vaga;
import br.com.sgr.dao.VagaDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.VagaException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class VagaFacade {

    private static final VagaDao vagaDao = new VagaDao();

    public static List<Vaga> vagas() {
        try {
            return vagaDao.vagas();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar vagas";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Vaga> vagasDisponiveis() {
        try {
            return vagaDao.vagasDisponiveis();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar vagas disponiveis";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Vaga> vagasDeApto(int idApto) {
        try {
            return vagaDao.vagasDeApto(idApto);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar vagas de apto";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void editarVaga(Vaga vaga, int opc) throws VagaException {
        try {
            if (opc == 1) {
                vaga.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(vaga.getApartamento()));
                for (Morador m : vaga.getApartamento().getMoradores()) {
                    if ((m.getTipo() == 1) && (m.getStatus() == 2)) {
                        throw new VagaException("Morador com acesso limitado");
                    }
                }
            }
            vagaDao.editarVaga(vaga);
            LogBDFacade.inserirLog(3, vaga);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao editar vaga de garagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Vaga vagaPorId(int id) {
        try {
            return vagaDao.vagaPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar vaga por ID";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
