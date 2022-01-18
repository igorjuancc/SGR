/*
    Status mensagens autor: [0] Deletada [1] Enviada [2] Lixeira
    Status mensagens receptor: [0] Deletada [1] Nova [2] Aberta [3] Lixeira(Nova) [4] Lixeira(Aberto)
 */
package br.com.sgr.facade;

import br.com.sgr.bean.Mensagem;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.MensagemDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.MensagemException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.util.Date;
import java.util.List;

public class MensagemFacade {

    private static final MensagemDao mensagemDao = new MensagemDao();

    public static void novaMensagem(Mensagem mensagem, List<Pessoa> receptores) throws MensagemException {
        try {
            if ((receptores == null) || (receptores.isEmpty())) {
                throw new MensagemException("Necessário inserir ao menos um destinatário para mensagem");
            }
            if ((mensagem.getAssunto() == null) || (mensagem.getAssunto().trim().equals(""))) {
                throw new MensagemException("Necessário inserir o assunto da mensagem");
            } else if (mensagem.getAssunto().length() > 30) {
                throw new MensagemException("Assunto não pode exceder 30 caracteres");
            }
            if ((mensagem.getDescricao() == null) || (mensagem.getDescricao().trim().equals(""))) {
                throw new MensagemException("Necessário digitar uma mensagem");
            }

            mensagem.setAssunto(mensagem.getAssunto().trim());
            mensagem.setDescricao(mensagem.getDescricao().trim());
            mensagem.setData(new Date());
            mensagem.setStatusAutor(1);
            mensagem.setStatusReceptor(1);

            for (Pessoa p : receptores) {
                mensagem.setReceptor(p);
                mensagemDao.novaMensagem(mensagem);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void verMensagem(Mensagem mensagem, int opc) {
        try {
            if (opc == 2) {
                switch (mensagem.getStatusReceptor()) {
                    case 1:
                        mensagem.setStatusReceptor(2);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                    case 3:
                        mensagem.setStatusReceptor(4);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void enviarLixeiraMensagem(Mensagem mensagem, int opc) {
        try {
            if (opc == 1) {
                mensagem.setStatusAutor(2);
                mensagemDao.editarStatusAutorMensagem(mensagem);
            } else {
                switch (mensagem.getStatusReceptor()) {
                    case 1:
                        mensagem.setStatusReceptor(3);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                    case 2:
                        mensagem.setStatusReceptor(4);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void restaurarMensagem(Mensagem mensagem, int opc) {
        try {
            if (opc == 1) {
                mensagem.setStatusAutor(1);
                mensagemDao.editarStatusAutorMensagem(mensagem);
            } else {
                switch (mensagem.getStatusReceptor()) {
                    case 3:
                        mensagem.setStatusReceptor(1);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                    case 4:
                        mensagem.setStatusReceptor(2);
                        mensagemDao.editarStatusReceptorMensagem(mensagem);
                        break;
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarMensagem(Mensagem mensagem, int opc) {
        try {
            if (opc == 1) {
                if (mensagem.getStatusReceptor() == 0) {
                    for (Mensagem m : mensagem.getRespostas()) {
                        m.setOrigemResposta(null);
                        mensagemDao.editarMensagem(m);
                    }
                    mensagemDao.apagarMensagemSql(mensagem);
                } else {
                    mensagem.setStatusAutor(0);
                    mensagemDao.editarStatusAutorMensagem(mensagem);
                }
            } else {
                if (mensagem.getStatusAutor() == 0) {
                    for (Mensagem m : mensagem.getRespostas()) {
                        m.setOrigemResposta(null);
                        mensagemDao.editarMensagem(m);
                    }
                    mensagemDao.apagarMensagemSql(mensagem);
                } else {
                    mensagem.setStatusReceptor(0);
                    mensagemDao.editarStatusReceptorMensagem(mensagem);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagem";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static int totalMensagens(Pessoa pessoa, int tipo) {
        try {
            return mensagemDao.totalMensagens(pessoa, tipo);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar mensagens";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Mensagem> mensagemFiltro(FiltroBD filtro, Pessoa pessoa, int tipo) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            String query;

            String dadosMensagem = "SELECT m.id, m.statusAutor, m.statusReceptor, m.assunto, m.descricao, m.data, ";
            String dadosAutor = "m.autor.id, m.autor.nome, m.autor.cpf, m.autor.email, m.autor.dataNascimento, m.autor.sexo, m.autor.fone, m.autor.celular, m.autor.imagem.id, m.autor.imagem.extensao, ";
            String dadosReceptor = "m.receptor.id, m.receptor.nome, m.receptor.cpf, m.receptor.email, m.receptor.dataNascimento, m.receptor.sexo, m.receptor.fone, m.receptor.celular, m.receptor.imagem.id, m.receptor.imagem.extensao, ";
            String dadosOrigemResposta = "o.id, o.statusAutor, o.statusReceptor, o.assunto, o.descricao, o.data ";

            query = dadosMensagem + dadosAutor + dadosReceptor + dadosOrigemResposta;

            switch (tipo) {
                case 1:     //Enviadas
                    query = query + " FROM Mensagem m LEFT JOIN m.origemResposta o WHERE m.autor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusAutor = 1) ";
                    break;
                case 2:     //Recebidas   
                    query = query + " FROM Mensagem m LEFT JOIN m.origemResposta o WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 1 OR m.statusReceptor = 2) ";
                    break;
                case 3:     //Enviadas Lixeira 
                    query = query + " FROM Mensagem m LEFT JOIN m.origemResposta o WHERE m.autor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusAutor = 2) ";
                    break;
                case 4:     //Recebidas Lixeira 
                    query = query + " FROM Mensagem m LEFT JOIN m.origemResposta o WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 3 OR m.statusReceptor = 4) ";
                    break;
                default:    //Enviadas
                    query = query + " FROM Mensagem m LEFT JOIN m.origemResposta o WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 1 OR m.statusReceptor = 2) ";
                    break;
            }

            switch (verFiltro) {
                case "autor.id":
                    filtro.setDescricao(query + " ORDER BY m.autor.id ");
                    break;
                case "assunto":
                    filtro.setDescricao(query + " ORDER BY m.descricao ");
                    break;
                case "data":
                    filtro.setDescricao(query + " ORDER BY m.data ");
                    break;
                case "receptor.id":
                    filtro.setDescricao(query + " ORDER BY m.receptor.id ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao(query + " ORDER BY m.data ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return mensagemDao.mensagemFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar mensagens";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
