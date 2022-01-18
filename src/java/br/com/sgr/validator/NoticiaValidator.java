package br.com.sgr.validator;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Noticia;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.NoticiaException;
import br.com.sgr.util.SgrUtil;
import java.util.Date;

public class NoticiaValidator {

    public static void validaNoticia(Noticia noticia) throws NoticiaException {
        if (noticia == null) {
            throw new NoticiaException("Notícia inválida");
        } else {
            String mensagem = "";
            Date hoje = new Date();

            if ((noticia.getAutor() == null) || (noticia.getAutor().getId() == 0)) {
                mensagem += "Necessário indicar o autor da notícia<br/>";
            }
            if ((noticia.getFuncionarioAlteracao() == null) || (noticia.getFuncionarioAlteracao().getId() == 0)) {
                mensagem += "Necessário indicar o responsável por alteração da notícia<br/>";
            }
            if (noticia.getDescricao() == null) {
                mensagem += "Necessário inserir a descrição da notícia<br/>";
            } else {
                if ((noticia.getDescricao().trim().equals("")) || (noticia.getDescricao().trim().isEmpty())) {
                    mensagem += "Necessário inserir uma descrição válida para notícia<br/>";
                }
            }
            if (noticia.getTitulo() == null) {
                mensagem += "Necessário inserir o titulo da nova notícia<br/>";
            } else {
                if ((noticia.getTitulo().trim().equals("")) || (noticia.getTitulo().trim().isEmpty())) {
                    mensagem += "Necessário inserir um titulo válido para notícia<br/>";
                } else if (noticia.getTitulo().length() > 50) {
                    mensagem += "O titulo da notícia não pode exceder a [50] caracteres<br/>";
                }
            }
            if (noticia.getData() == null) {
                mensagem += "Data da notícia inválida<br/>";
            } else if (noticia.getData().after(hoje)) {
                mensagem += "Data da notícia não pode ser superior a data de hoje "
                        + "[" + SgrUtil.formataData(hoje) + "]<br/>";
            }
            if (noticia.getUltimaAlteracao() == null) {
                mensagem += "Data da ultima alteração da notícia inválida<br/>";
            } else if (noticia.getData().after(hoje)) {
                mensagem += "Data da ultima alteração da notícia não pode ser superior a data de hoje "
                        + "[" + SgrUtil.formataData(hoje) + "]<br/>";
            }

            if (noticia.getArquivos() != null) {
                if (noticia.getArquivos().size() > 5) {
                    mensagem += "Arquivos da notícia não podem exceder a [5]<br/>";
                } else {
                    for (Arquivo a : noticia.getArquivos()) {
                        try {
                            ArquivoValidator.validaImagem(a);
                        } catch (ArquivoException ex) {
                            mensagem += "Arquivo n°" + Integer.toString(noticia.getArquivos().indexOf(a) + 1)
                                    + " inválido: " + ex.getMessage() + "<br/>";
                        }
                    }
                }
            }
            if (!mensagem.equals("")) {
                throw new NoticiaException(mensagem);
            }
        }
    }
}
