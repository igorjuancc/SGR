package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Infracao;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.dao.NotificacaoDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.InfracaoException;
import br.com.sgr.exception.NotificacaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.InfracaoValidator;
import br.com.sgr.validator.NotificacaoValidator;
import java.util.Date;
import java.util.List;
import org.primefaces.model.UploadedFile;

public class NotificacaoFacade {

    private static final NotificacaoDao notificacaoDao = new NotificacaoDao();

    public static List<Infracao> listaInfracoes() {
        try {
            return notificacaoDao.listaInfracoes();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de infrações";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Infracao infracaoPorId(int id) {
        try {
            return notificacaoDao.infracaoPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de infração";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Notificacao> listaNotificacaoMorador(int idMorador) {
        try {
            return notificacaoDao.listaNotificacaoMorador(idMorador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao listar notificações de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }   
    }

    public static void cadastrarInfracao(Infracao infracao) throws InfracaoException {
        try {
            InfracaoValidator.validaInfracao(infracao);
            if (infracao.getId() != 0) {
                throw new InfracaoException("Id de nova infração inválido");
            } else {
                infracao.setDescricao(infracao.getDescricao().trim().toUpperCase());
                notificacaoDao.cadastrarInfracao(infracao);
                LogBDFacade.inserirLog(1, infracao);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar dados de infração";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void editarInfracao(Infracao infracao) throws InfracaoException {
        try {
            InfracaoValidator.validaInfracao(infracao);
            if (infracao.getId() == 0) {
                throw new InfracaoException("Id de infração inválido");
            } else {
                infracao.setDescricao(infracao.getDescricao().trim().toUpperCase());
                notificacaoDao.atualizarInfracao(infracao);
                LogBDFacade.inserirLog(3, infracao);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar infração";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarInfracao(Infracao infracao) throws InfracaoException {
        try {
            if ((infracao == null) || (infracao.getId() == 0)) {
                throw new InfracaoException("Infração inválida");
            } else if (notificacaoDao.numNotifiPorInfracao(infracao.getId()) > 0) {
                throw new InfracaoException("Infração presente em notificações, não pode ser removída");
            } else {
                notificacaoDao.apagarInfracao(infracao);
                LogBDFacade.inserirLog(2, infracao);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao deletar infração";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void novaNotificacao(Notificacao notificacao, List<UploadedFile> upArquivos) throws NotificacaoException, ArquivoException {
        try {
            Date hoje = new Date();

            if (notificacao == null) {
                throw new NotificacaoException("Notificação inválida");
            } else {
                notificacao.setDataEmissao(hoje);
                if ((notificacao.getBoleto() != null)) {
                    throw new NotificacaoException("Boleto ja emitido, notificação não pode ser modificada");
                } else {
                    notificacao.setDescricao(notificacao.getDescricao().toUpperCase().trim());
                    if (notificacao.getArquivos() != null) {
                        for (Arquivo a : notificacao.getArquivos()) {
                            if ((a.getDescricao() != null) && (!a.getDescricao().isEmpty())) {
                                a.setDescricao(a.getDescricao().trim().toUpperCase());
                                if ((a.getDescricao().equals("")) && (!a.getDescricao().isEmpty())) {
                                    a.setDescricao(null);
                                }
                            } else {
                                a.setDescricao(null);
                            }
                        }
                    }
                    NotificacaoValidator.validaNotificacao(notificacao);

                    if (notificacao.getId() == 0) {
                        notificacaoDao.novaNotificacao(notificacao);
                        LogBDFacade.inserirLog(1, notificacao);
                    } else {
                        notificacaoDao.atualizarNotificacao(notificacao);
                        LogBDFacade.inserirLog(3, notificacao);
                    }

                    if (upArquivos != null) {
                        salvarArquivoNotificacao(notificacao, upArquivos);
                    }
                }
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar nova notificação";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static void salvarArquivoNotificacao(Notificacao notificacao, List<UploadedFile> upArquivos) throws ArquivoException {
        String mensagem = "";
        for (UploadedFile img : upArquivos) {
            Arquivo arq = notificacao.getArquivos().get(upArquivos.indexOf(img));
            try {
                if (img != null) {
                    salvarArquivoNotificacao(img, arq);
                }
            } catch (ArquivoException ex) {
                ArquivoFacade.apagarArquivo(arq);
                mensagem += "Problemas ao salvar arquivo n°" + Integer.toString(notificacao.getArquivos().indexOf(arq) + 1)
                        + " em disco<br/>";
            }
        }
        if (!mensagem.equals("")) {
            throw new ArquivoException(mensagem);
        }
    }

    public static int totalNotificacao() {
        try {
            return notificacaoDao.totalNotificacao();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de notificações";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalNotificacaoMorador(Morador morador) {
        try {
            return notificacaoDao.totalNotificacaoMorador(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de notificações de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Notificacao> notificacaoFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT n.id, n.tipo, n.dataReferencia, n.dataEmissao, n.descricao, "
                    + "n.morador.id, n.morador.nome, n.funcionario.id, n.funcionario.nome, n.infracao.id, n.infracao.classificacao, n.infracao.descricao, "
                    + "b.id, b.tipo, b.dataEmissao, b.dataVencimento, b.dataReferencia, b.valorBoleto, b.valorMulta, f.id, "
                    + "n.morador.apartamento.id, n.morador.apartamento.bloco, n.morador.dataCadastro, n.morador.cpf";
            switch (verFiltro) {
                case "id":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY n.id ");
                    break;
                case "infracao.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY i.descricao ");
                    break;
                case "dataEmissao":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY n.dataEmissao ");
                    break;
                case "tipo":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY n.tipo ");
                    break;
                case "morador.id":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY m.id ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f ORDER BY n.dataEmissao ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return notificacaoDao.notificacaoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de notificações";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Notificacao> notificacaoFiltroMorador(FiltroBD filtro, Morador morador) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT n.id, n.tipo, n.dataReferencia, n.dataEmissao, n.descricao, "
                    + "n.morador.id, n.morador.nome, n.funcionario.id, n.funcionario.nome, n.infracao.id, n.infracao.classificacao, n.infracao.descricao, "
                    + "b.id, b.tipo, b.dataEmissao, b.dataVencimento, b.dataReferencia, b.valorBoleto, b.valorMulta, f.id, "
                    + "n.morador.apartamento.id, n.morador.apartamento.bloco, n.morador.dataCadastro, n.morador.cpf";
            switch (verFiltro) {
                case "id":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.id ");
                    break;
                case "infracao.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.infracao.descricao ");
                    break;
                case "dataEmissao":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.dataEmissao ");
                    break;
                case "tipo":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.tipo ");
                    break;
                case "morador.id":
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.morador.id ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM Notificacao n LEFT JOIN n.boleto b LEFT JOIN b.financeiro f WHERE n.morador.id = " + Integer.toString(morador.getId()) + " ORDER BY n.dataEmissao ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return notificacaoDao.notificacaoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar notificações de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarArquivo(Arquivo arquivo) throws NotificacaoException {
        try {
            if (arquivo.getNotificacao().getBoleto() != null) {
                throw new NotificacaoException("Arquivo não pode ser deletado, notificação já emitida");
            } else {
                ArquivoFacade.apagarArquivo(arquivo);
                apagarArquivoNotificacao(arquivo);
                LogBDFacade.inserirLog(3, arquivo.getNotificacao());
            }
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar arquivo de notificação";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarNotificacao(Notificacao notificacao) throws NotificacaoException, ArquivoException {
        try {
            if ((notificacao.getBoleto() != null) && (notificacao.getBoleto().getFinanceiro() != null)) {
                throw new NotificacaoException("Notificação já emitida, não pode ser deletada");
            } else {
                String mensagem = "";
                notificacaoDao.apagarNotificacao(notificacao);
                LogBDFacade.inserirLog(2, notificacao);
                for (Arquivo a : notificacao.getArquivos()) {
                    try {
                        apagarArquivoNotificacao(a);
                    } catch (ArquivoException ex) {
                        mensagem += "Problemas ao apagar arquivo n°"
                                + Integer.toString(notificacao.getArquivos().indexOf(a) + 1)
                                + " do disco<br/>";
                    }
                }
                if (!mensagem.equals("")) {
                    throw new ArquivoException(mensagem);
                }
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar notificação";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static void salvarArquivoNotificacao(UploadedFile upArquivo, Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = SgrUtil.caminhoProjeto() + "AnexoAdvertencia\\";
        SgrUtil.gravarArquivo(upArquivo, caminho, nomeArquivo);
    }

    private static void apagarArquivoNotificacao(Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = SgrUtil.caminhoProjeto() + "AnexoAdvertencia\\";
        SgrUtil.apagarArquivo(caminho, nomeArquivo);
    }
}
