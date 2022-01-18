package br.com.sgr.facade;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.dao.ApartamentoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;

public class ApartamentoFacade {

    private static final ApartamentoDao apartamentoDao = new ApartamentoDao();

    public static Apartamento apartamentoPorId(int id) {
        try {
            return apartamentoDao.apartamentoPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações do apartamento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Apartamento> apartamentosVagosPorBloco(String bloco) {
        try {
            return apartamentoDao.apartamentosVagosPorBloco(bloco);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de apartamentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Apartamento> apartamentosOcupadosPorBloco(String bloco) {
        try {
            return apartamentoDao.apartamentosOcupadosPorBloco(bloco);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de apartamentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Apartamento> apartamentosOcupados() {
        try {
            return apartamentoDao.apartamentosOcupados();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de apartamentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<String> blocosApartamentos() {
        try {
            return apartamentoDao.blocosApartamentos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações dos blocos dos apartamentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
