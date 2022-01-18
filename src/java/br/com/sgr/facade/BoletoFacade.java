package br.com.sgr.facade;

import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Morador;
import br.com.sgr.dao.BoletoDao;
import br.com.sgr.dao.NotificacaoDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.campolivre.NotSupportedBancoException;
import org.jrimum.bopepo.campolivre.NotSupportedCampoLivreException;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;

public class BoletoFacade {

    private static final BoletoDao boletoDao = new BoletoDao();

    //Todos boletos do ano do morador
    public static List<BoletoSGR> listaBoletoAnoMorador(int ano, Morador morador) {
        try {
            return boletoDao.listaBoletoAnoMorador(ano, morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar boletos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Todos boletos de taxa de condominio do morador
    public static List<BoletoSGR> listaBoletoTaxaAnoMorador(int ano, Morador morador) {
        try {
            return boletoDao.listaBoletoTaxaAnoMorador(ano, morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar boletos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarBoleto(BoletoSGR boleto) throws DaoException {
        try {
            boletoDao.apagarBoleto(boleto);
        } catch (DaoException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("****Problemas ao apagar boleto [Facade]****" + e);
            e.printStackTrace();
            throw e;
        }
    }

    public static Boleto novoBoleto(BoletoSGR novoBoleto) {
        try {
            Date hoje = new Date();
            if (!((novoBoleto.getId() != 0) && ((novoBoleto.getDataVencimento() == hoje) || novoBoleto.getDataVencimento().after(hoje)))) {
                int dias;
                novoBoleto.setDataEmissao(new Date());
                Calendar dataReferencia = Calendar.getInstance();
                Calendar dataEmissao = Calendar.getInstance();
                Calendar dataCadastro = Calendar.getInstance();
                dataReferencia.setTime(novoBoleto.getDataReferencia());
                dataEmissao.setTime(novoBoleto.getDataEmissao());
                dataCadastro.setTime(novoBoleto.getMorador().getDataCadastro());

                if (novoBoleto.getTipo() == 1) {
                    //Verifica Ano e Mes de cadastro para pagamento referente aos dias de ocupação
                    if ((dataReferencia.get(Calendar.MONTH) == dataCadastro.get(Calendar.MONTH))
                            && (dataReferencia.get(Calendar.YEAR) == dataCadastro.get(Calendar.YEAR))) {
                        dias = dataCadastro.getActualMaximum(Calendar.DAY_OF_MONTH) - dataCadastro.get(Calendar.DAY_OF_MONTH);
                        novoBoleto.setValorBoleto(dias * 8.06);
                    } else {
                        novoBoleto.setValorBoleto(250);
                    }

                    if (novoBoleto.getDataEmissao().after(novoBoleto.getDataReferencia())) {
                        dataEmissao.add(Calendar.DATE, 1);
                        novoBoleto.setDataVencimento(dataEmissao.getTime());
                        calculaJurosTaxa(novoBoleto);
                    }
                } else {
                    Calendar dataTeste = Calendar.getInstance();
                    dataTeste.setTime(novoBoleto.getDataReferencia());
                    dataTeste.add(Calendar.DATE, 15);

                    switch (novoBoleto.getNotificacao().getInfracao().getClassificacao()) {
                        case 0:
                            novoBoleto.setValorBoleto(32);
                            break;
                        case 1:
                            novoBoleto.setValorBoleto(63);
                            break;
                        case 2:
                            novoBoleto.setValorBoleto(125);
                            break;
                        default:
                            novoBoleto.setValorBoleto(0);
                            break;
                    }

                    if (novoBoleto.getDataEmissao().after(dataTeste.getTime())) {
                        dataEmissao.add(Calendar.DATE, 1);
                        novoBoleto.setDataVencimento(dataEmissao.getTime());
                        calculaJurosTaxa(novoBoleto);
                    } else {
                        novoBoleto.setDataVencimento(dataTeste.getTime());
                    }
                }

                if (novoBoleto.getId() == 0) {
                    boletoDao.novoBoleto(novoBoleto);
                    LogBDFacade.inserirLog(1, novoBoleto);
                    if (novoBoleto.getTipo() == 2) {
                        NotificacaoDao notificacaoDao = new NotificacaoDao();
                        notificacaoDao.atualizarNotificacao(novoBoleto.getNotificacao());
                        LogBDFacade.inserirLog(3, novoBoleto.getNotificacao());
                    }
                } else {
                    boletoDao.atualizaBoleto(novoBoleto);
                    LogBDFacade.inserirLog(3, novoBoleto);
                }
            }
            LogBDFacade.inserirLog(8, novoBoleto);
            return emiteBoleto(novoBoleto);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar boleto";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Boleto emiteBoleto(BoletoSGR boletoSGR) {
        try {
            //Quem emite o boleto
            Cedente cedente = new Cedente("Condominio Azteca", "39.273.933/0001-98");

            //Quem paga o boleto
            Sacado sacado = new Sacado(boletoSGR.getMorador().getNome(), SgrUtil.imprimeCPF(boletoSGR.getMorador().getCpf()));

            //Endereço (Servira para os dois)
            Endereco endereco = new Endereco();
            endereco.setUF(UnidadeFederativa.PR);
            endereco.setLocalidade("Curitiba");
            endereco.setCep(new CEP("01234-555"));
            endereco.setBairro("Jardim Schaffer");
            endereco.setLogradouro("Rua Ângelo Zeni");
            endereco.setNumero("555");
            sacado.addEndereco(endereco);

            // Informando dados sobre a conta bancária do título.
            ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.BANCO_DO_BRASIL.create());
            contaBancaria.setNumeroDaConta(new NumeroDaConta(123456, "0"));
            contaBancaria.setCarteira(new Carteira(30));
            contaBancaria.setAgencia(new Agencia(1234, "1"));

            Titulo titulo = new Titulo(contaBancaria, sacado, cedente);
            titulo.setNumeroDoDocumento(Integer.toString(boletoSGR.getId()));
            titulo.setNossoNumero("12345678901234567");
            titulo.setDigitoDoNossoNumero("5");
            titulo.setValor(BigDecimal.valueOf(boletoSGR.getValorBoleto()));
            titulo.setDataDoDocumento(boletoSGR.getDataReferencia());
            titulo.setDataDoVencimento(boletoSGR.getDataVencimento());
            titulo.setTipoDeDocumento(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
            titulo.setAceite(Aceite.A);
            titulo.setDeducao(BigDecimal.ZERO);
            titulo.setMora(BigDecimal.valueOf(boletoSGR.getValorMulta()));
            titulo.setAcrecimo(BigDecimal.ZERO);
            titulo.setValorCobrado(BigDecimal.valueOf(boletoSGR.getValorBoleto() - boletoSGR.getValorMulta()));

            Boleto boleto = new Boleto(titulo);
            boleto.setLocalPagamento("PREFERENCIALMENTE NAS AGÊNCIAS DO BANCO DO BRASIL");
            boleto.setInstrucaoAoSacado((boletoSGR.getTipo() == 1) ? "Referênte a taxa de condomino de " + SgrUtil.formataData(boletoSGR.getDataReferencia())
                    : "Referênte a multa de condomino do dia: " + SgrUtil.formataData(boletoSGR.getDataReferencia()));
            boleto.setInstrucao1("Não receber após o vencimento");
            boleto.setInstrucao2("Juros Mora de 1% A.M");

            return boleto;
        } catch (IllegalArgumentException | NotSupportedBancoException | NotSupportedCampoLivreException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao emitir boleto";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    private static void calculaJurosTaxa(BoletoSGR boleto) {
        Calendar dataReferencia = Calendar.getInstance();
        Calendar dataEmissao = Calendar.getInstance();
        Calendar dataCadastro = Calendar.getInstance();
        dataReferencia.setTime(boleto.getDataReferencia());
        dataEmissao.setTime(boleto.getDataEmissao());
        dataCadastro.setTime(boleto.getMorador().getDataCadastro());

        long dias = ((boleto.getDataEmissao().getTime() - boleto.getDataReferencia().getTime()) / 1000 / 60 / 60 / 24);

        if (boleto.getTipo() == 1) {
            if ((dataReferencia.get(Calendar.MONTH) == dataCadastro.get(Calendar.MONTH))
                    && (dataReferencia.get(Calendar.YEAR) == dataCadastro.get(Calendar.YEAR))) {
                dias = dias + 10;
            }
            boleto.setValorMulta(0.082 * dias);
        } else {
            boleto.setValorMulta(((boleto.getValorBoleto() / 100) / 30) * dias);
        }

        boleto.setValorBoleto(boleto.getValorBoleto() + boleto.getValorMulta());
    }

    public static int totalBoletosSemRegistro() {
        try {
            return boletoDao.totalBoletosSemRegistro();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de boleto";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<BoletoSGR> boletoFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT b.id, b.tipo, b.dataEmissao, b.dataVencimento, b.dataReferencia, b.valorBoleto, b.valorMulta, "
                    + "b.morador.id, b.morador.nome, f.id, f.tipo, f.dataRegistro, b.morador.apartamento.id, b.morador.apartamento.bloco";

            switch (verFiltro) {
                case "id":
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.id ");
                    break;
                case "tipo":
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.tipo ");
                    break;
                case "morador.nome":
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.morador.nome ");
                    break;
                case "morador.apartamento":
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.morador.apartamento.bloco, b.morador.apartamento.id ");
                    break;
                case "valorBoleto":
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.morador.apartamento.bloco, b.valorBoleto ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL ORDER BY b.dataEmissao ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return boletoDao.boletoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de boletos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
