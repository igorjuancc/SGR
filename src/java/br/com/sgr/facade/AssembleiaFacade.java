package br.com.sgr.facade;

import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Mensagem;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.AssembleiaDao;
import br.com.sgr.exception.AssembleiaException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.MensagemException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

public class AssembleiaFacade {

    private static final AssembleiaDao assembleiaDao = new AssembleiaDao();

    public static void novaAssembleia(Assembleia assembleia) throws AssembleiaException {
        try {
            if (assembleiaDao.assembleiaAberta() != null) {
                throw new AssembleiaException("Uma assembléia aberta foi encontrada, necessária a finalização para abertura de uma nova");
            } else {
                if (assembleia == null) {
                    throw new AssembleiaException("Assembléia inválida");
                } else {
                    if ((assembleia.getTipo() != 1) && (assembleia.getTipo() != 2)) {
                        throw new AssembleiaException("Tipo de assembléia inválido");
                    }
                    if (assembleia.getSindico() == null) {
                        throw new AssembleiaException("Necessário indicar o sindico criador da assembleia");
                    }
                    if (assembleia.getPresidente() == null) {
                        throw new AssembleiaException("Necessário indicar o presidente da assembleia");
                    } else {
                        Funcionario funcionario = FuncionarioFacade.funcionarioPorCpf(assembleia.getPresidente().getCpf());
                        if ((funcionario != null) && ((funcionario.getFuncao().getId() == 1) && (funcionario.getStatus() == 1))) {
                            throw new AssembleiaException("O presidente da assembleia não pode ser um síndico em exercício da função");
                        } else if (assembleia.getPresidente().getStatus() != 3) {
                            throw new AssembleiaException("O presidente da assembleia deve ser um morador sem restrições de acesso");
                        }
                    }
                }
            }

            assembleia.setDataInicio(new Date());
            Calendar dt = Calendar.getInstance();
            dt.add(Calendar.DATE, 10);
            assembleia.setDataFim(dt.getTime());
            assembleia.setEncerramento(0);
            assembleiaDao.novaAssembleia(assembleia);
            LogBDFacade.inserirLog(1, assembleia);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar dados de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarAssembleia(Assembleia assembleia) throws AssembleiaException {
        try {
            if ((assembleia == null) || (assembleia.getId() == 0)) {
                throw new AssembleiaException("Assembleia invalida");
            } else {
                if (assembleia.getEncerramento() != 0) {
                    throw new AssembleiaException("Assembléia em processo de encerramento, não pode ser apagada");
                } else {
                    assembleia.setQuestoes(QuestaoFacade.questoesAssembleia(assembleia.getId()));
                    if (assembleia.getQuestoes() != null && assembleia.getQuestoes().size() > 0) {
                        throw new AssembleiaException("Necessário apagar as questões antes de deletar a assembléia");
                    }
                }
            }
            assembleiaDao.apagarAssembleia(assembleia);
            LogBDFacade.inserirLog(2, assembleia);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar dados de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Assembleia assembleiaPorId(int id) {
        try {
            Assembleia assembleia = assembleiaDao.assembleiaPorId(id);
            if (assembleia != null) {
                assembleia.setQuestoes(QuestaoFacade.questoesCompletasAssembleia(id));
            }
            return assembleia;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static String atualizarStatusAssembleia(Assembleia assembleia) {
        try {
            String rtn = null;
            if (assembleia.getDataFim().after(new Date())) {
                rtn = "A assembleia só podera ser encerrada em sua data fim";
            } else if ((assembleia.getEncerramento() == 0) && (assembleia.getParecer() == null)) {
                assembleia.setEncerramento(1);
                rtn = "Assembleia aguardando parecer do presidente";
            } else if ((assembleia.getEncerramento() == 1) && (assembleia.getParecer() != null)) {
                assembleia.setEncerramento(2);
                assembleia.setDataFim(new Date());
                rtn = "Assembleia encerrada com sucesso";
            }

            if (rtn != null) {
                assembleiaDao.atualizarStatusAssembleia(assembleia);
            } else {
                rtn = "Não foi possivel alterar status da assembleia";
            }
            return rtn;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar status de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void atualizarParecerAssembleia(Assembleia assembleia) throws AssembleiaException {
        try {
            LoginMB usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);
            assembleia.setParecer(assembleia.getParecer().trim());
            if (assembleia.getEncerramento() != 1) {
                throw new AssembleiaException("Não é possível salvar o parecer de encerramento");
            } else if ((assembleia.getParecer() == null) || (assembleia.getParecer().trim().equals(""))) {
                throw new AssembleiaException("Parecer inválido");
            } else if (assembleia.getPresidente().getId() != usuario.getMorador().getId()) {
                throw new AssembleiaException("Usuário não é o presidende da assembléia");
            } else {
                assembleiaDao.atualizarParecerAssembleia(assembleia);
                //Envio de mensagem ao síndico após emissão do parecer                
                Mensagem novaMensagem = new Mensagem();
                List<Pessoa> receptores = new ArrayList<>();
                String mensagem = "Parecer emitido para assembléia nº" + Integer.toString(assembleia.getId())
                        + " , acesse a "
                        + "<a href=\"http://localhost:8080/SGR/sindico/SindicoAssembleia.jsf\">página</a> para mais detalhes.";
                novaMensagem.setAutor(usuario.getMorador());
                receptores.add(assembleia.getSindico());
                novaMensagem.setAssunto("Emissão de parecer");
                novaMensagem.setDescricao(mensagem);
                MensagemFacade.novaMensagem(novaMensagem, receptores);
            }
        } catch (DaoException | MensagemException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar parecer de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Assembleia assembleiaAberta() {
        try {
            return assembleiaDao.assembleiaAberta();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalAssembleia() {
        try {
            return assembleiaDao.totalAssembleia();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de assembléias";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Assembleia> assembleiaFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "id":
                    filtro.setDescricao("FROM Assembleia a ORDER BY a.id ");
                    break;
                case "encerramento":
                    filtro.setDescricao("FROM Assembleia a ORDER BY a.encerramento ");
                    break;
                case "dataInicio":
                    filtro.setDescricao("FROM Assembleia a ORDER BY a.dataInicio ");
                    break;
                case "dataFim":
                    filtro.setDescricao("FROM Assembleia a ORDER BY a.dataFim ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao("FROM Assembleia a ORDER BY a.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return assembleiaDao.assembleiaFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar assembléias";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
