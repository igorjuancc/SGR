package br.com.sgr.facade;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Email;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.Vaga;
import br.com.sgr.dao.MoradorDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.EmailException;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.exception.VagaException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.JavaMailApp;
import br.com.sgr.util.Seguranca;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.PessoaValidator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import org.primefaces.model.UploadedFile;

public class MoradorFacade {

    private static final MoradorDao moradorDao = new MoradorDao();

    public static void cadastrarMorador(Morador morador, UploadedFile imagem) throws MoradorException {
        try {
            Pessoa verificarPessoa;

            if (morador.getEmail() != null) {
                morador.setEmail(morador.getEmail().trim());
            }
            morador.setNome(morador.getNome().trim().toUpperCase());
            morador.setCpf(morador.getCpf().replace(".", "").replace("-", ""));
            morador.setStatus(0);
            morador.setTipo(1);

            if ((morador.getFone() != null) && (!morador.getFone().isEmpty())) {
                morador.setFone(morador.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((morador.getCelular() != null) && (!morador.getCelular().isEmpty())) {
                morador.setCelular(morador.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaMorador(morador);

            verificarPessoa = PessoaFacade.pessoaPorEmail(morador.getEmail());
            if ((verificarPessoa != null) && (verificarPessoa.getId() != morador.getId())) {
                throw new MoradorException("Email já cadastrado");
            }
            if (moradorDao.moradorPorCpf(morador.getCpf()) != null) {
                throw new MoradorException("CPF já cadastrado para morador");
            } else {
                verificarPessoa = PessoaFacade.pessoaPorCpf(morador.getCpf());
            }

            morador.setSenha(Seguranca.md5(morador.getSenha()));

            if (verificarPessoa == null) {
                moradorDao.cadastrarMorador(morador);
            } else {
                moradorDao.cadastrarTbMorador(morador);
                if (!cadastrarTbMorador(morador)) {
                    moradorDao.apagarTbMorador(morador);
                    SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de morador");
                }
            }

            if (imagem != null) {
                gravarImagemMorador(imagem, morador.getImagem());
            }
        } catch (DaoException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar os dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            apagarNovoMorador(morador);
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static boolean cadastrarTbMorador(Morador morador) {
        try {
            moradorDao.atualizarEmailMorador(morador);
            if (morador.getImagem().getId() == 0) {
                ArquivoFacade.salvarArquivo(morador.getImagem());
                moradorDao.atualizarImagemMorador(morador);
            }
            return true;
        } catch (DaoException | ArquivoException e) {
            System.out.println("****Problemas ao cadastrar morador TB [Facade]****");
            e.printStackTrace(System.out);
            return false;
        }
    }

    private static void apagarNovoMorador(Morador morador) {
        try {
            if ((VisitanteFacade.visitantePorId(morador.getId()) == null)
                    && (FuncionarioFacade.funcionarioPorId(morador.getId()) == null)) {
                moradorDao.apagarMorador(morador);
            } else {
                moradorDao.apagarTbMorador(morador);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar os dados de novo morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarMorador(Morador morador) throws MoradorException {
        try {
            if (morador.getStatus() != 0) {
                throw new MoradorException("Morador não pode ser removído");
            } else {
                if ((VisitanteFacade.visitantePorId(morador.getId()) == null)
                        && (FuncionarioFacade.funcionarioPorId(morador.getId()) == null)) {
                    moradorDao.apagarMorador(morador);
                } else {
                    moradorDao.apagarTbMorador(morador);
                }
                LogBDFacade.inserirLog(2, morador);
                removerImagemMorador(morador);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar os dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao remover imagem de morador do disco";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarMorador(Morador morador, UploadedFile imagem) throws MoradorException {
        try {
            Pessoa verificarPessoa;

            morador.setNome(morador.getNome().trim().toUpperCase());
            if (morador.getEmail() != null) {
                morador.setEmail(morador.getEmail().trim());
            }
            if ((morador.getFone() != null) && (!morador.getFone().isEmpty())) {
                morador.setFone(morador.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((morador.getCelular() != null) && (!morador.getCelular().isEmpty())) {
                morador.setCelular(morador.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaMorador(morador);

            verificarPessoa = PessoaFacade.pessoaPorEmail(morador.getEmail());
            if ((verificarPessoa != null) && (verificarPessoa.getId() != morador.getId())) {
                throw new MoradorException("Email já cadastrado");
            }

            moradorDao.atualizarMorador(morador);
            LogBDFacade.inserirLog(3, morador);

            if (imagem != null) {
                gravarImagemMorador(imagem, morador.getImagem());
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar os dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Morador moradorPorId(int id) {
        try {
            return moradorDao.moradorPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Morador moradorPorCpf(String cpf) {
        try {
            return moradorDao.moradorPorCpf(cpf);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Morador> listaMoradorPorApto(Apartamento apto) {
        try {
            return moradorDao.listaMoradorPorApto(apto);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Morador> listaMoradoresResponsaveis() {
        try {
            return moradorDao.listaMoradoresResponsaveis();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Morador> responsaveisAtivos() {
        try {
            return moradorDao.responsaveisAtivos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void atualizarStatusMorador(Morador morador) throws MoradorException {
        try {
            if ((morador.getStatus() < 0) || (morador.getStatus() > 4)) {
                throw new MoradorException("Status de morador inválido");
            } else {
                if (morador.getTipo() == 2) {
                    if ((morador.getStatus() == 1) || (morador.getStatus() == 4)) {
                        moradorDao.atualizarStatusMorador(morador);
                    } else {
                        throw new MoradorException("Status inválido para dependente");
                    }
                } else {
                    if (morador.getStatus() == 0) {
                        if (LogBDCadastroMoradorFacade.ultimoLogMorador(morador) == null) {
                            morador.setStatus(3);
                            morador.setDataCadastro(new Date());
                            moradorDao.atualizarMorador(morador);  //Aprovação novo morador
                            LogBDFacade.inserirLog(4, morador);
                            LogBDCadastroMoradorFacade.logCadastroNovoMorador(morador);
                        } else {
                            throw new MoradorException("Morador possuí um registro ativo, não pode ser aprovado");
                        }
                    } else {
                        moradorDao.atualizarStatusMorador(morador);
                        LogBDFacade.inserirLog(6, morador);
                    }
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao modificar status de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void desativarMorador(Morador morador) throws MoradorException {
        try {
            if (morador.getTipo() != 1) {
                throw new MoradorException("Atributo tipo do morador inválido");
            } else {
                String mensagem;
                List<Integer> idMorador = new ArrayList<>();
                idMorador.add(morador.getId());
                List<Object[]> listaDebitosMorador = MoradorFacade.listaDebitosMorador(idMorador);

                if (!listaDebitosMorador.isEmpty()) {
                    mensagem = "O(A) MORADOR(A) "
                            + morador.getNome()
                            + " NÃO PODE SER DESATIVADO"
                            + " DEVIDO AO(S) SEGUINTE(S) DÉBITO(S):<br/>";

                    if ((listaDebitosMorador.get(0)[4] != null) && (int) listaDebitosMorador.get(0)[4] > 0) {
                        mensagem += "- MULTA <br/>";
                    }
                    if ((listaDebitosMorador.get(0)[6] != null) && (int) listaDebitosMorador.get(0)[6] > 0) {
                        mensagem += "- TAXA DE CONDOMINIO <br/>";
                    }
                    if ((listaDebitosMorador.get(0)[5] != null) && (int) listaDebitosMorador.get(0)[5] > 0) {
                        mensagem += "- OUTROS <br/>";
                    }
                    throw new MoradorException(mensagem);
                } else if (LogBDCadastroMoradorFacade.ultimoLogMorador(morador) == null) {
                    System.out.println("Não foi possível encontrar o ultimo log do morador cadastrado");
                    mensagem = "Houve um problema ao desativar morador";
                    SgrUtil.mensagemErroRedirecionamento(mensagem);
                } else {
                    List<Morador> moradores = moradorDao.listaMoradorPorApto(morador.getApartamento());
                    for (Morador m : moradores) {
                        m.setSenha(null);
                        m.setApartamento(null);
                        m.setStatus(4);
                        m.setDataCadastro(null);
                        moradorDao.atualizarMorador(m);
                        m.setApartamento(morador.getApartamento());
                        m.getApartamento().setVagas(VagaFacade.vagasDeApto(morador.getApartamento().getId()));
                        LogBDFacade.inserirLog(5, m);
                        if (m.getTipo() == 1) {
                            LogBDCadastroMoradorFacade.logDesligaMorador(morador);
                        }
                        for (Vaga v : morador.getApartamento().getVagas()) {
                            v.setApartamento(null);
                            VagaFacade.editarVaga(v, 2);
                        }
                    }
                }
            }
        } catch (DaoException | VagaException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao desativar morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void reativarMorador(Morador morador) throws MoradorException, MessagingException, EmailException {
        try {
            Pessoa verificarPessoa = null;
            String novaSenha = null;
            if (morador.getEmail() != null) {
                morador.setEmail(morador.getEmail().trim());
            }

            morador.getApartamento().setMoradores(moradorDao.listaMoradorPorApto(morador.getApartamento()));
            for (int i = 0; morador.getApartamento().getMoradores().size() > i; i++) {
                if (morador.getApartamento().getMoradores().get(i).getTipo() == 1) {
                    verificarPessoa = morador.getApartamento().getMoradores().get(i);
                    i = morador.getApartamento().getMoradores().size();
                }
            }

            if (morador.getTipo() == 1) {
                novaSenha = Seguranca.gerarSenha(5);
                morador.setStatus(5);
                morador.setDataCadastro(new Date());
                morador.setSenha(novaSenha);
                PessoaValidator.validaMorador(morador);
                if (verificarPessoa != null) {
                    throw new MoradorException("Apartamento já possuí responsável");
                }
                morador.setSenha(Seguranca.md5(novaSenha));
            } else {
                morador.setStatus(1);
                morador.setDataCadastro(null);
                morador.setSenha(null);
                PessoaValidator.validaDependente(morador);
                if (verificarPessoa == null) {
                    throw new MoradorException("Apartamento não possuí responsável");
                } else {
                    Morador responsavel = moradorPorId(verificarPessoa.getId());
                    if ((responsavel.getStatus() != 2) && (responsavel.getStatus() != 3)) {
                        throw new MoradorException("Responsável do apartamento está desativado");
                    }
                }
            }

            if ((morador.getEmail() != null)
                    && (!morador.getEmail().isEmpty())
                    && (!morador.getEmail().equals(""))) {
                verificarPessoa = PessoaFacade.pessoaPorEmail(morador.getEmail());
                if ((verificarPessoa != null) && (verificarPessoa.getId() != morador.getId())) {
                    throw new MoradorException("Email já cadastrado");
                }
            }

            moradorDao.atualizarMorador(morador);
            LogBDCadastroMoradorFacade.logCadastroNovoMorador(morador);
            LogBDFacade.inserirLog(7, morador);

            if (morador.getTipo() == 1) {
                /*Envio de email com link e nova senha*/
                Email novoEmail = new Email();
                novoEmail.setAssunto("Reativação de morador");
                novoEmail.setMensagem("Nova senha para aceeso ao sistema SGR: " + novaSenha + ". Acesse a página de login com seu email"
                        + " e a nova senha para iniciar o processo de recuperação do acesso.");

                List<String> listaEmail = new ArrayList<>();
                //listaEmail.add(morador.getEmail());
                listaEmail.add("igor_juan@live.com");
                novoEmail.setDestinatario(listaEmail);
                if (!JavaMailApp.enviaEmail(novoEmail)) {
                    //Erro ao enviar email
                    throw new EmailException("Problemas ao enviar nova senha por email.");
                }
            }

            morador.setSenha(novaSenha);
        } catch (DaoException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao reativar morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void alterarSenhaMorador(Morador morador) {
        try {
            moradorDao.alterarSenhaMorador(morador);
            LogBDFacade.inserirLog(3, morador);
            if (morador.getStatus() == 5) {
                if (verificaDebitosMorador(morador)) {
                    morador.setStatus(2);
                } else {
                    morador.setStatus(3);
                }
                moradorDao.atualizarStatusMorador(morador);
                LogBDFacade.inserirLog(6, morador);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao alterar senha de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Pessoa verificaCpfMorador(String cpf) throws MoradorException {
        try {
            cpf = cpf.replace(".", "").replace("-", "");
            if ((!SgrUtil.isCPF(cpf))) {
                throw new MoradorException("Número de CPF inválido");
            } else if (moradorDao.moradorPorCpf(cpf) != null) {
                throw new MoradorException("CPF já cadastrado para morador");
            } else {
                return PessoaFacade.pessoaPorCpf(cpf);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de cpf do morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalMoradoresAtivos() {
        try {
            return moradorDao.totalMoradoresAtivos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalMoradoresDesativados() {
        try {
            return moradorDao.totalMoradoresDesativados();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalMoradoresPorStatus(int status) {
        try {
            return moradorDao.totalMoradoresPorStatus(status);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar o total de moradores por status";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Morador> moradorStatusFiltro(FiltroBD filtro, int status) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("FROM Morador m WHERE m.status = " + Integer.toString(status) + " ORDER BY m.nome ");
                    break;
                case "apartamento":
                    filtro.setDescricao("FROM Morador m WHERE m.status = " + Integer.toString(status) + " ORDER BY m.apartamento.bloco, m.apartamento.id ");
                    break;
                case "cpf":
                    filtro.setDescricao("FROM Morador m WHERE m.status = " + Integer.toString(status) + " ORDER BY m.cpf ");
                    break;
                case "email":
                    filtro.setDescricao("FROM Morador m WHERE m.status = " + Integer.toString(status) + " ORDER BY m.email ");
                    break;
                default:
                    filtro.setDescricao("FROM Morador m WHERE m.status = " + Integer.toString(status) + " ORDER BY m.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return moradorDao.listaMoradorFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar moradores por status";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Morador> moradorAtvFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status != 0 AND m.status != 4 ORDER BY m.nome ");
                    break;
                case "cpf":
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status != 0 AND m.status != 4 ORDER BY m.cpf ");
                    break;
                default:
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status != 0 AND m.status != 4 ORDER BY m.nome ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return moradorDao.listaMoradorFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar moradores ativos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Morador> moradorDstvFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status = 0 OR m.status = 4 ORDER BY m.nome ");
                    break;
                case "cpf":
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status = 0 OR m.status = 4 ORDER BY m.cpf ");
                    break;
                default:
                    filtro.setDescricao("SELECT DISTINCT m FROM Morador m WHERE m.status = 0 OR m.status = 4 ORDER BY m.nome ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return moradorDao.listaMoradorFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar moradores desativados";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalListaDebitos() {
        try {
            return moradorDao.totalListaDebitos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar debitos de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Object[]> listaDebitosMorador(List<Integer> listaId) {
        try {
            String idMoradores = "";
            if ((listaId != null) && (!listaId.isEmpty())) {
                if (listaId.size() == 1) {
                    idMoradores = Integer.toString(listaId.get(0));
                } else {
                    for (int id : listaId) {
                        idMoradores = idMoradores + Integer.toString(id) + ",";
                    }
                    idMoradores = idMoradores.substring(0, idMoradores.length() - 1);
                }
            } else {
                return null;
            }
            return moradorDao.listaDebitosMoradores(idMoradores);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar lista de débitos dos moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    /*
      Lista de moradores com débitos de condomínio    
        - Id de moradores e quantidade de meses atrasados
        - Não contabiliza mês atual se dia menor ou igual a 10  
        - Todas as multas não pagas
        - Demais boletos não pagos (Outros)
     */
    public static List<Object[]> listaMoradoresDebitos(FiltroBD filtro) {
        try {
            String query = "SELECT id, nome, bloco, apto, notificacao, outros, debitos_taxa "
                    + "FROM ( "
                    + "(SELECT m.id_morador AS id, p.nome_pessoa AS nome, a.bloco_apartamento AS bloco, a.id_apartamento AS apto "
                    + "FROM tb_morador AS m "
                    + "INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "INNER JOIN tb_apartamento AS a ON m.apartamento_morador = a.id_apartamento "
                    + "WHERE m.tipo_morador = 1 AND (m.status_morador = 2 OR m.status_morador = 3)) AS morador "
                    + "INNER JOIN "
                    + "(SELECT id_morador, COUNT(notificacao) AS notificacao, COUNT(boleto) AS outros, SUM(debitos_taxa) AS debitos_taxa FROM ( "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "INNER JOIN tb_boleto AS b ON n.id_boleto_notificacao = b.id_boleto "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE n.tipo_notificacao = 1 AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "WHERE n.tipo_notificacao = 1 AND n.id_boleto_notificacao IS NULL "
                    + "UNION "
                    + "SELECT id_morador_boleto AS morador, NULL AS notificacao, id_boleto AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_boleto AS b "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE (b.tipo_boleto != 1 AND b.tipo_boleto != 2) AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador, CAST(NULL AS INTEGER) AS notificacao, CAST(NULL AS INTEGER) AS outros, SUM(debitos_taxa) FROM ( "
                    + "SELECT id_morador, ((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END + 1 AS meses, "
                    + "CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END, "
                    + "(((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END + 1) "
                    + "- (CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END) AS debitos_taxa "
                    + "FROM "
                    + "( "
                    + "  SELECT "
                    + "	id_morador_log_cadastro_morador AS id_morador, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'YY') AS INTEGER) AS ANOS, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'MM') AS INTEGER) AS MESES, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'DD') AS INTEGER) AS DIAS "
                    + "  FROM tb_log_cadastro_morador AS log_m "
                    + "  INNER JOIN tb_morador AS m ON log_m.id_morador_log_cadastro_morador = m.id_morador "
                    + "  INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "  WHERE tipo_morador = 1 AND (status_morador = 2 OR status_morador = 3) "
                    + "  ORDER BY p.nome_pessoa "
                    + ") AS mse "
                    + "LEFT JOIN ( "
                    + "SELECT id_morador_boleto, COUNT(id_financeiro) AS numero_boletos FROM tb_financeiro AS f "
                    + "INNER JOIN tb_boleto AS b ON f.id_boleto_financeiro = b.id_boleto "
                    + "WHERE tipo_boleto = 1 "
                    + "GROUP BY id_morador_boleto) AS blt "
                    + "ON mse.id_morador = blt.id_morador_boleto) AS t WHERE debitos_taxa > 0 GROUP BY id_morador) AS debitos GROUP BY id_morador) AS d "
                    + "ON d.id_morador = morador.id) AS moradores_debitos ";
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.nome ");
                    break;
                case "apartamento":
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.bloco, moradores_debitos.apto ");
                    break;
                case "taxa":
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.debitos_taxa ");
                    break;
                case "multa":
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.notificacao ");
                    break;
                case "outros":
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.outros ");
                    break;
                default:
                    filtro.setDescricao(query + " ORDER BY moradores_debitos.nome ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return moradorDao.listaMoradoresDebitos(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void recuperarAcesso(Morador morador, int opc) throws MoradorException {
        try {
            if ((morador.getEmail() == null) || (morador.getEmail().trim().equals("")) || (morador.getEmail().trim().isEmpty())) {
                throw new MoradorException("Email inválido");
            } else if ((morador.getCpf() == null)) {
                throw new MoradorException("CPF inválido");
            } else {
                morador.setCpf(morador.getCpf().replace(".", "").replace("-", "").trim());
                if ((morador.getCpf().equals("")) || (morador.getCpf().isEmpty()) || (!SgrUtil.isCPF(morador.getCpf()))) {
                    throw new MoradorException("CPF inválido");
                }
            }
            Morador moradorBusca = moradorDao.buscaPorCpfEmailMorador(morador);
            if ((moradorBusca == null) || (moradorBusca.getId() == 0)) {
                throw new MoradorException("Morador não localizado");
            } else if (moradorBusca.getTipo() == 2) {
                throw new MoradorException("Morador cadastrado como dependente");
            } else {
                switch (moradorBusca.getStatus()) {
                    case 0:
                        throw new MoradorException("Morador desativado, aguarde aprovação do síndico");
                    case 1:
                        throw new MoradorException("Morador com acesso de dependente, entre em contato com o síndico para verificar situação");
                    case 4:
                        throw new MoradorException("Morador com acesso desabilitado, entre em contato com o síndico para verificar situação");
                }

                morador = (opc == 1) ? moradorBusca : morador;
                String novaSenha = Seguranca.gerarSenha(6);
                morador.setSenha(Seguranca.md5(novaSenha));
                morador.setStatus(5);
                moradorDao.atualizarMorador(morador);
                LogBDFacade.inserirLog(9, morador);

                Email novoEmail = new Email();
                novoEmail.setAssunto("Recuperação de acesso");
                novoEmail.setMensagem("Nova senha para aceeso ao sistema SGR: " + novaSenha + ". Acesse a página de login com seu email"
                        + " e a nova senha para iniciar o processo de recuperação do acesso.");

                List<String> listaEmail = new ArrayList<>();
                //listaEmail.add(morador.getEmail());
                listaEmail.add("igor_juan@live.com");
                novoEmail.setDestinatario(listaEmail);
                if (!JavaMailApp.enviaEmail(novoEmail)) {
                    //Erro ao enviar email
                    throw new MoradorException("Problemas ao enviar nova senha por email. Entre em contato com o síndico ou tente novamente mais tarde");
                }
                morador.setSenha(novaSenha);

            }
        } catch (DaoException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao recuperar acesso de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Boolean verificaDebitosMorador(Morador morador) {
        try {
            List<Object[]> debitos = moradorDao.listaDebitosMoradores(Integer.toString(morador.getId()));
            return !(debitos == null || debitos.isEmpty());
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao verificar débitos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    private static void removerImagemMorador(Morador morador) throws ArquivoException {
        String caminho = SgrUtil.caminhoProjeto() + "ImagemPessoa\\";
        String nomeArquivo = Integer.toString(morador.getImagem().getId()) + morador.getImagem().getExtensao();
        SgrUtil.apagarArquivo(caminho, nomeArquivo);
    }

    private static void gravarImagemMorador(UploadedFile imagem, Arquivo arquivo) throws ArquivoException {
        String caminho = SgrUtil.caminhoProjeto() + "ImagemPessoa\\";
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        SgrUtil.gravarArquivo(imagem, caminho, nomeArquivo);
    }
}
