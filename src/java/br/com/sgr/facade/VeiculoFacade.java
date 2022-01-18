package br.com.sgr.facade;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Veiculo;
import br.com.sgr.dao.VeiculoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.VeiculoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.VeiculoValidator;
import java.util.List;

public class VeiculoFacade {

    private static final VeiculoDao veiculoDao = new VeiculoDao();

    public static void cadastrarVeiculo(Veiculo veiculo) throws VeiculoException {
        try {
            veiculo.setPlaca(veiculo.getPlaca().trim().toUpperCase());
            veiculo.setMarca(veiculo.getMarca().trim().toUpperCase());
            veiculo.setModelo(veiculo.getModelo().trim().toUpperCase());
            veiculo.setCor(veiculo.getCor().trim().toUpperCase());

            VeiculoValidator.validaVeiculo(veiculo);

            if (veiculoPorPlaca(veiculo) != null) {
                throw new VeiculoException("Placa de veícula já cadastrada!");
            } else if (totalVeiculosMorador(veiculo.getMorador()) > 5) {
                throw new VeiculoException("Número máximo de veículos[5] ja cadastrado!");
            }
            veiculoDao.cadastrarVeiculo(veiculo);
            LogBDFacade.inserirLog(1, veiculo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de veiculo";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Veiculo veiculoPorPlaca(Veiculo veiculo) {
        try {
            return veiculoDao.veiculoPorPlaca(veiculo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de veiculo";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Veiculo> listaVeiculosDeMorador(Morador morador) {
        try {
            return veiculoDao.listaVeiculosDeMorador(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de veiculos do morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void removerVeiculo(Veiculo veiculo) {
        try {
            veiculoDao.removerVeiculo(veiculo);
            LogBDFacade.inserirLog(2, veiculo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de veiculo";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarVeiculo(Veiculo veiculo) throws VeiculoException {
        try {
            veiculo.setPlaca(veiculo.getPlaca().trim().toUpperCase());
            veiculo.setMarca(veiculo.getMarca().trim().toUpperCase());
            veiculo.setModelo(veiculo.getModelo().trim().toUpperCase());
            veiculo.setCor(veiculo.getCor().trim().toUpperCase());

            VeiculoValidator.validaVeiculo(veiculo);

            if (veiculo.getId() == 0) {
                throw new VeiculoException("Veiculo não cadastrado");
            }
            Veiculo verificaVeiculo = veiculoPorPlaca(veiculo);
            if ((verificaVeiculo != null) && (verificaVeiculo.getId() != veiculo.getId())) {
                throw new VeiculoException("Placa de veícula já cadastrada!");
            }
            veiculoDao.atualizarVeiculo(veiculo);
            LogBDFacade.inserirLog(3, veiculo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de veiculo";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static int totalVeiculosMorador(Morador morador) {
        try {
            return veiculoDao.totalVeiculosMorador(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de veiculos do morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalDeVeiculos() {
        try {
            return veiculoDao.totalDeVeiculos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de veiculos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Veiculo> veiculosFiltroMorador(FiltroBD filtro, int idMorador) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "placa":
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.placa ");
                    break;
                case "marca":
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.marca ");
                    break;
                case "modelo":
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.modelo ");
                    break;
                case "ano":
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.ano ");
                    break;
                case "cor":
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.cor ");
                    break;
                default:
                    filtro.setDescricao("FROM Veiculo v WHERE v.morador.id = " + Integer.toString(idMorador) + " ORDER BY v.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return veiculoDao.listaVeiculoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de veiculos do morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Veiculo> veiculosFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "morador":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.morador.nome ");
                    break;
                case "placa":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.placa ");
                    break;
                case "marca":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.marca ");
                    break;
                case "modelo":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.modelo ");
                    break;
                case "ano":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.ano ");
                    break;
                case "cor":
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.cor ");
                    break;
                default:
                    filtro.setDescricao("FROM Veiculo v ORDER BY v.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return veiculoDao.listaVeiculoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de veiculos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
