package br.com.sgr.facade;

import br.com.sgr.bean.Comentario;
import br.com.sgr.dao.ComentarioDao;
import br.com.sgr.exception.ComentarioException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.ComentarioValidator;
import java.util.Date;
import java.util.List;

public class ComentarioFacade {

    private static final ComentarioDao comentarioDao = new ComentarioDao();

    public static void novoComentario(Comentario comentario) throws ComentarioException {
        try {
            if (comentario == null) {
                throw new ComentarioException("Comentario não encontrado");
            } else {
                comentario.setData(new Date());
                comentario.setDescricao(comentario.getDescricao().trim());
                ComentarioValidator.validaComentario(comentario);
                comentarioDao.novoComentario(comentario);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao inserir novo comentário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarComentario(Comentario comentario) throws ComentarioException {
        try {
            comentarioDao.apagarComentario(comentario);
        } catch (DaoException e) {
            throw new ComentarioException("Houve um problema ao apagar comentario", e);
        }
    }

    public static List<Comentario> listaComentarioQuestao(int idQuestao) {
        try {
            return comentarioDao.listaComentarioQuestao(idQuestao);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar buscar comentarios de questão";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Comentario> comentariosDeAtendimento(int idAtendimento) {
        try {
            return comentarioDao.comentariosDeAtendimento(idAtendimento);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar buscar comentarios de atendimento";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
