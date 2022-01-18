package br.com.sgr.facade;

import br.com.sgr.bean.Atendimento;
import br.com.sgr.bean.Mensagem;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.AtendimentoDao;
import br.com.sgr.exception.AtendimentoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.MensagemException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

public class AtendimentoFacade {

    private static final AtendimentoDao atendimentoDao = new AtendimentoDao();

    public static void novoAtendimento(Atendimento atendimento) throws AtendimentoException {
        try {
            Date hoje = new Date();
            atendimento.setDataAbertura(hoje);

            switch (atendimento.getTipo().getId()) {
                case 4:
                    if (atendimentoDao.buscaAbertoTipoAtendimentoMorador(atendimento) != null) {
                        throw new AtendimentoException("Uma solicitação para vaga de garagem ja foi aberta, aguarde a resposta");
                    }
                    break;
                case 5:
                    if (atendimentoDao.buscaAbertoTipoAtendimentoMorador(atendimento) != null) {
                        throw new AtendimentoException("Uma solicitação de agendamento do salão de festas ja foi aberta, aguarde a resposta");
                    }
                    break;
            }

            if (atendimento.getDescricao() == null) {
                throw new AtendimentoException("Necessário inserir uma descrição para o atendimento");
            } else {
                atendimento.setDescricao(atendimento.getDescricao().trim());
                if ((atendimento.getDescricao().equals("")) || (atendimento.getDescricao().isEmpty())) {
                    throw new AtendimentoException("Necessário inserir uma descrição válida para o atendimento");
                } else if (atendimento.getDescricao().length() > 255) {
                    throw new AtendimentoException("O número de caracteres da descrição não pode exceder a 255");
                }
            }
            atendimentoDao.novoAtendimento(atendimento);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void editarAtendimento(Atendimento atendimento) throws AtendimentoException {
        try {
            Atendimento atendimentoBusca;

            if (atendimento.getStatus() == 0) {
                Date hoje = new Date();
                atendimento.setDataAbertura(hoje);
            }

            switch (atendimento.getTipo().getId()) {
                case 4:
                    atendimentoBusca = atendimentoDao.buscaAbertoTipoAtendimentoMorador(atendimento);
                    if ((atendimentoBusca != null) && (atendimentoBusca.getId() != atendimento.getId())) {
                        throw new AtendimentoException("Uma solicitação para vaga de garagem ja foi aberta, aguarde a resposta");
                    }
                    break;
                case 5:
                    atendimentoBusca = atendimentoDao.buscaAbertoTipoAtendimentoMorador(atendimento);
                    if ((atendimentoBusca != null) && (atendimentoBusca.getId() != atendimento.getId())) {
                        throw new AtendimentoException("Uma solicitação de agendamento do salão de festas ja foi aberta, aguarde a resposta");
                    }
                    break;
            }

            if (atendimento.getDescricao() == null) {
                throw new AtendimentoException("Necessário inserir uma descrição para o atendimento");
            } else {
                atendimento.setDescricao(atendimento.getDescricao().trim());
                if ((atendimento.getDescricao().equals("")) || (atendimento.getDescricao().isEmpty())) {
                    throw new AtendimentoException("Necessário inserir uma descrição válida para o atendimento");
                } else if (atendimento.getDescricao().length() > 255) {
                    throw new AtendimentoException("O número de caracteres da descrição não pode exceder a 255");
                }
            }
            atendimentoDao.editarAtendimento(atendimento);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarAtendimento(Atendimento atendimento) {
        try {
            LoginMB usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);
            atendimentoDao.editarAtendimento(atendimento);
            if (usuario.getFuncionario().getId() != 0) {
                Mensagem novaMensagem = new Mensagem();
                List<Pessoa> receptores = new ArrayList<>();
                String mensagem;
                novaMensagem.setAutor(usuario.getFuncionario());
                receptores.add(atendimento.getMorador());
                if (atendimento.getStatus() == 1) {
                    novaMensagem.setAssunto("Atendimento encerrado");
                    mensagem = "Seu atendimento nº" + Integer.toString(atendimento.getId())
                            + " foi encerrado, acesse a "
                            + "<a href=\"http://localhost:8080/SGR/morador/MoradorAtendimento.jsf\">página</a> para mais detalhes.";
                    novaMensagem.setDescricao(mensagem);
                    MensagemFacade.novaMensagem(novaMensagem, receptores);
                }
                if (atendimento.getStatus() == 2) {
                    novaMensagem.setAssunto("Atendimento inicializado");
                    mensagem = "Seu atendimento nº" + Integer.toString(atendimento.getId())
                            + " foi inicializado, acesse a "
                            + "<a href=\"http://localhost:8080/SGR/morador/MoradorAtendimento.jsf\">página</a>  para mais detalhes.";
                    novaMensagem.setDescricao(mensagem);
                    MensagemFacade.novaMensagem(novaMensagem, receptores);
                }
            }
        } catch (DaoException | MensagemException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Atendimento> atendimentosPorNotificacao(int idNotifi) {
        try {
            return atendimentoDao.atendimentosPorNotificacao(idNotifi);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao listar atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Atendimento atendimentoPorId(int idAtendimento) {
        try {
            Atendimento a = atendimentoDao.atendimentoPorId(idAtendimento);
            if (a != null) {
                a.setComentarios(ComentarioFacade.comentariosDeAtendimento(idAtendimento));
            }
            return a;
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarAtendimento(Atendimento atendimento) throws AtendimentoException {
        try {
            if (atendimento.getStatus() != 0) {
                throw new AtendimentoException("O atendimento não pode ser apagado");
            } else {
                atendimentoDao.apagarAtendimento(atendimento);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Atendimento> atendimentosFiltro(FiltroBD filtro, int status) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            switch (verFiltro) {
                case "status":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.status ");
                    break;
                case "morador.nome":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.morador.nome ");
                    break;
                case "descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.descricao ");
                    break;
                case "dataAbertura":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.dataAbertura ");
                    break;
                case "funcionario.nome":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.funcionario.nome ");
                    break;
                case "dataFechamento":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.dataFechamento ");
                    break;
                case "tipo.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.tipo.descricao ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.status = " + Integer.toString(status) + " ORDER BY a.dataAbertura ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return atendimentoDao.listaAtendimentoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Atendimento> atendimentosFiltroMorador(FiltroBD filtro, int idMorador) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            switch (verFiltro) {
                case "tipo.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " ORDER BY a.tipo.descricao ");
                    break;
                case "status":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " ORDER BY a.status ");
                    break;
                case "descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " ORDER BY a.descricao ");
                    break;
                case "dataAbertura":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " ORDER BY a.dataAbertura ");
                    break;
                default:
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " ORDER BY a.dataAbertura ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return atendimentoDao.listaAtendimentoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar atendimentos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Atendimento> atendimentosFiltroMoradorTipo(FiltroBD filtro, int idMorador, int tipo) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            switch (verFiltro) {
                case "tipo.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " AND a.tipo = " + Integer.toString(tipo) + " ORDER BY a.tipo.descricao ");
                    break;
                case "descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " AND a.tipo = " + Integer.toString(tipo) + " ORDER BY a.descricao ");
                    break;
                case "dataAbertura":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " AND a.tipo = " + Integer.toString(tipo) + " ORDER BY a.dataAbertura ");
                    break;
                default:
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.morador.id = " + Integer.toString(idMorador) + " AND a.tipo = " + Integer.toString(tipo) + " ORDER BY a.dataAbertura ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return atendimentoDao.listaAtendimentoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar atendimentos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Atendimento> atendimentosFiltroTipo(FiltroBD filtro, int tipo) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            switch (verFiltro) {
                case "status":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.status ");
                    break;
                case "morador.nome":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.morador.nome ");
                    break;
                case "descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.descricao ");
                    break;
                case "dataAbertura":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.dataAbertura ");
                    break;
                case "dataFechamento":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.dataFechamento ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = " + Integer.toString(tipo) + " ORDER BY a.dataAbertura ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return atendimentoDao.listaAtendimentoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar atendimentos por tipo";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Atendimento> atendimentosFiltroNot(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            switch (verFiltro) {
                case "dataAbertura":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = 3 OR a.tipo = 7 ORDER BY a.dataAbertura ");
                    break;
                case "descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = 3 OR a.tipo = 7 ORDER BY a.descricao ");
                    break;
                case "tipo.descricao":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = 3 OR a.tipo = 7 ORDER BY a.tipo.descricao ");
                    break;
                case "morador.nome":
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = 3 OR a.tipo = 7 ORDER BY a.morador.nome ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.tipo = 3 OR a.tipo = 7 ORDER BY a.dataAbertura ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return atendimentoDao.listaAtendimentoFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalAtendimentosPorStatus(int status) {
        try {
            return atendimentoDao.totalAtendimentosPorStatus(status);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalAtendimentosMorador(Morador morador) {
        try {
            return atendimentoDao.totalAtendimentosMorador(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalAtendimentosMoradorTipo(Morador morador, int tipo) {
        try {
            return atendimentoDao.totalAtendimentosMoradorTipo(morador, tipo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimentos de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int totalDeAtendimentosPorTipo(int tipo) {
        try {
            return atendimentoDao.totalDeAtendimentosPorTipo(tipo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de atendimentos";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }
}
