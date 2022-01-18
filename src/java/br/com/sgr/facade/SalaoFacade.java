package br.com.sgr.facade;

import br.com.sgr.bean.Salao;
import br.com.sgr.dao.SalaoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.SalaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.util.Date;
import java.util.List;

public class SalaoFacade {

    private static final SalaoDao salaoDao = new SalaoDao();

    public static void novoRegistroSalao(Salao salao) throws SalaoException {
        try {
            if (salao.getMorador() == null) {
                throw new SalaoException("Morador inválido");                
            }
            if (salao.getMorador().getStatus() != 3) {
                throw new SalaoException("Morador com acesso limitado");
            }
            Date hoje = new Date();
            if ((salao.getDataReserva().before(hoje)) || salao.getDataReserva().equals(hoje)) {
                throw new SalaoException("Data do agendamento não pode ser antes ou igual a data de hoje");
            } else {
                if (salaoDao.reservaSalaoPorData(salao.getDataReserva()) != null) {
                    throw new SalaoException("Data solicitada já foi reservada");
                } else {
                    salaoDao.novoRegistroSalao(salao);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de reserva de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Salao> reservasSalao() {
        try {
            return salaoDao.reservasSalao();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de reservas de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
    
    public static List<Date> datasReservaSalao() {
        try {
            return salaoDao.datasReservaSalao();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de datas de reservas de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarReservaSalao(Salao salao) throws SalaoException {
        try {
            Date hoje = new Date();
            if ((salao.getDataReserva().before(hoje)) || salao.getDataReserva().equals(hoje)) {
                throw new SalaoException("Data do agendamento ocorrida");
            } else {
                salaoDao.apagarReservaSalao(salao);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao remover dados de reservas de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void editarReservaSalao(Salao salao) throws SalaoException {
        try {
            if (salao.getMorador().getStatus() != 3) {
                throw new SalaoException("Morador com acesso limitado");
            }
            Date hoje = new Date();
            if ((salao.getDataReserva().before(hoje)) || salao.getDataReserva().equals(hoje)) {
                throw new SalaoException("Data do agendamento não pode ser antes ou igual a data de hoje");
            }
            Salao salaoBusca = salaoDao.reservaSalaoPorData(salao.getDataReserva());
            if ((salaoBusca == null) || (salaoBusca.getId()) == salao.getId()) {
                salaoDao.editarReservaSalao(salao);
            } else {
                throw new SalaoException("Data solicitada já foi reservada");
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de reserva de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static int totalReservaSalao() {
        try {
            return salaoDao.totalReservaSalao();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de reservas de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Salao> listaReservaSalaoFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT s.id, s.dataReserva, s.morador.id, s.morador.nome, s.morador.apartamento.id, s.morador.apartamento.bloco, s.funcionario.id, s.funcionario.nome";
            switch (verFiltro) {
                case "id":
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.id ");
                    break;
                case "morador.nome":
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.morador.nome ");
                    break;
                case "morador.apartamento":
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.morador.apartamento.bloco, s.morador.apartamento.id ");
                    break;
                case "funcionario.nome":
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.funcionario.nome ");
                    break;
                case "dataReserva":
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.dataReserva ");
                    break;
                default:
                    filtro.setDescricao(dadosBusca + " FROM Salao s ORDER BY s.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return salaoDao.listaReservaSalaoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de reservas de salão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
