package br.com.sgr.validator;

import br.com.sgr.bean.Comentario;
import br.com.sgr.exception.ComentarioException;
import java.util.Date;

public class ComentarioValidator {

    public static void validaComentario(Comentario comentario) throws ComentarioException {
        if (comentario == null) {
            throw new ComentarioException("Comentário inválido");
        } else {
            String mensagem = "";
            Date hoje = new Date();

            if ((comentario.getData() == null) || (comentario.getData().after(hoje))) {
                mensagem = "Data de comentário inválida";
            }
            if ((comentario.getPessoa() == null) || (comentario.getPessoa().getId() == 0)) {
                mensagem = "Necessário indicar o autor do comentario";
            } else if ((comentario.getTipo() != 1) && (comentario.getTipo() != 2)) {
                mensagem = "Tipo indicado do comentário é inválido";
            } else if ((comentario.getAtendimento() == null) && (comentario.getQuestao() == null)) {
                mensagem = "Necessário indicar o objeto relacionado ao comentário";
            } else {
                if ((comentario.getTipoPessoa() == null)) {
                    mensagem = "Necessário indicar o tipo da pessoa criadora do comentário";
                } else if ((!comentario.getTipoPessoa().equals("S")) && (!comentario.getTipoPessoa().equals("P")) && (!comentario.getTipoPessoa().equals("M"))) {
                    mensagem = "Tipo da pessoa criadora do comentário inválido";
                }
                if ((comentario.getDescricao() == null)) {
                    mensagem = "Comentario não encontrado";
                } else {
                    if ((comentario.getDescricao().trim().equals("")) || (comentario.getDescricao().trim().isEmpty())) {
                        mensagem = "Comentario inválido";
                    } else if (comentario.getDescricao().length() > 200) {
                        mensagem = "O número de caracteres do comentario não pode exceder a 200";
                    }
                }
            }

            if (!mensagem.equals("")) {
                throw new ComentarioException(mensagem);
            }
        }
    }
}
