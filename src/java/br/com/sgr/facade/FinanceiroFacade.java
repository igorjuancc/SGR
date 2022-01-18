package br.com.sgr.facade;

import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.bean.Financeiro;
import br.com.sgr.dao.FinanceiroDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.FinanceiroException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.FinanceiroValidator;
import java.util.Date;
import java.util.List;

public class FinanceiroFacade {

    private static final FinanceiroDao financeiroDao = new FinanceiroDao();

    public static void novoRegistroFinanceiro(Financeiro financeiro) throws FinanceiroException {
        try {
            if ((financeiro.getTipo() == 0) && (financeiro.getId() == 0)) {
                if (financeiro.getBoleto() == null) {
                    throw new FinanceiroException("Boleto de nova receita não encontrado");
                } else {
                    CategoriaFinanceiro categoria = new CategoriaFinanceiro();
                    //Tipo Boleto: [1]Taxa de condominio [2]Multa [3]Taxa de Mudança
                    switch (financeiro.getBoleto().getTipo()) {
                        case 1:
                            categoria.setId(1);
                            financeiro.setDescricao("TAXA DE CONDOMINIO: " + SgrUtil.formataData(financeiro.getBoleto().getDataReferencia()));
                            break;
                        case 2:
                            financeiro.setDescricao("MULTA DATA: " + SgrUtil.formataData(financeiro.getBoleto().getDataReferencia()));
                            categoria.setId(8);
                            break;
                        case 3:
                            financeiro.setDescricao("TAXA DE MUDANÇA: " + SgrUtil.formataData(financeiro.getBoleto().getDataReferencia()));
                            categoria.setId(9);
                            break;
                        default:
                            categoria.setId(0);
                            break;
                    }
                    financeiro.setCategoria(categoria);
                    financeiro.setDataRegistro(new Date());
                    financeiro.setValor(financeiro.getBoleto().getValorBoleto());
                }
            }
            financeiro.setDescricao(financeiro.getDescricao().trim().toUpperCase());
            FinanceiroValidator.validaFinanceiro(financeiro);

            if (financeiro.getId() == 0) {
                financeiroDao.novoRegistroFinanceiro(financeiro);
            } else {
                financeiroDao.editarRegistroFinanceiro(financeiro);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de registro financeiro [ANO]";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarRegistroFinanceiro(Financeiro financeiro) {
        try {
            financeiroDao.apagarRegistroFinanceiro(financeiro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar registro financeiro";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Integer> listaAnoRegistro(int tipoReceita) {
        try {
            return financeiroDao.listaAnoRegistro(tipoReceita);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de filtro [ANO]";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Integer> listaMesAnoRegistro(int ano, int tipoReceita) {
        try {
            return financeiroDao.listaMesAnoRegistro(ano, tipoReceita);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de filtro [MES/ANO]";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Financeiro> mesAnoRegistroFinanceiro(int ano, int mes) {
        try {
            return financeiroDao.mesAnoRegistroFinanceiro(ano, mes);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de filtro [MES/ANO]";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalRegistroFinanceiroTipo(int tipo) {
        try {
            return financeiroDao.totalRegistroFinanceiroTipo(tipo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar quantidade total de registros financeiros por tipo";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Financeiro> listaRegistroFinanceiroFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "descricao":
                    filtro.setDescricao("SELECT f.id, f.descricao, f.dataRegistro, f.valor, f.funcionario, f.categoria,"
                            + " f.boleto.id, f.boleto.tipo, f.boleto.dataEmissao, f.boleto.dataVencimento, f.boleto.dataReferencia, f.boleto.valorBoleto, f.boleto.valorMulta,"
                            + " f.boleto.morador.id, f.boleto.morador.nome "
                            + " FROM Financeiro f WHERE f.tipo = 0 ORDER BY f.descricao ");
                    break;
                case "categoria.descricao":
                    filtro.setDescricao("SELECT f.id, f.descricao, f.dataRegistro, f.valor, f.funcionario, f.categoria,"
                            + " f.boleto.id, f.boleto.tipo, f.boleto.dataEmissao, f.boleto.dataVencimento, f.boleto.dataReferencia, f.boleto.valorBoleto, f.boleto.valorMulta,"
                            + " f.boleto.morador.id, f.boleto.morador.nome "
                            + " FROM Financeiro f WHERE f.tipo = 0 ORDER BY f.categoria.descricao ");
                    break;
                case "dataRegistro":
                    filtro.setDescricao("SELECT f.id, f.descricao, f.dataRegistro, f.valor, f.funcionario, f.categoria,"
                            + " f.boleto.id, f.boleto.tipo, f.boleto.dataEmissao, f.boleto.dataVencimento, f.boleto.dataReferencia, f.boleto.valorBoleto, f.boleto.valorMulta,"
                            + " f.boleto.morador.id, f.boleto.morador.nome "
                            + " FROM Financeiro f WHERE f.tipo = 0 ORDER BY f.dataRegistro ");
                    break;
                case "valor":
                    filtro.setDescricao("SELECT f.id, f.descricao, f.dataRegistro, f.valor, f.funcionario, f.categoria,"
                            + " f.boleto.id, f.boleto.tipo, f.boleto.dataEmissao, f.boleto.dataVencimento, f.boleto.dataReferencia, f.boleto.valorBoleto, f.boleto.valorMulta,"
                            + " f.boleto.morador.id, f.boleto.morador.nome "
                            + " FROM Financeiro f WHERE f.tipo = 0 ORDER BY f.valor ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao("SELECT f.id, f.descricao, f.dataRegistro, f.valor, f.funcionario, f.categoria, "
                            + " f.boleto.id, f.boleto.tipo, f.boleto.dataEmissao, f.boleto.dataVencimento, f.boleto.dataReferencia, f.boleto.valorBoleto, f.boleto.valorMulta,"
                            + " f.boleto.morador.id, f.boleto.morador.nome "
                            + "FROM Financeiro f WHERE f.tipo = 0 ORDER BY f.dataRegistro ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return financeiroDao.listaRegistroFinanceiroFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de registros financeiros";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Object[]> totalFinanceiro(int tipo) {
        try {
            return financeiroDao.totalFinanceiro(tipo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de registros financeiros";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Object[]> totalFinanceiroAno(int tipo, int ano) {
        try {
            return financeiroDao.totalFinanceiroAno(tipo, ano);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de registros financeiros";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Object[]> totalFinanceiroAnoMes(int tipo, int ano, int mes) {
        try {
            return financeiroDao.totalFinanceiroAnoMes(tipo, ano, mes);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de registros financeiros";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
