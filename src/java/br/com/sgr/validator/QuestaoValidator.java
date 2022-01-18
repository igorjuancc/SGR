package br.com.sgr.validator;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.OpcQuestao;
import br.com.sgr.bean.Questao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.QuestaoException;
import java.util.Date;
import java.util.List;

public class QuestaoValidator {

    public static void validaQuestao(Questao questao) throws QuestaoException {
        if (questao == null) {
            throw new QuestaoException("Notícia inválida");
        } else {
            String mensagem = "";
            Date hoje = new Date();

            if ((questao.getAssembleia() == null)
                    || (questao.getAssembleia().getId() == 0)) {
                mensagem += "Necessário indicar a assembleia referente a questão<br/>";
            } else if ((questao.getAssembleia().getEncerramento() != 0) || (questao.getAssembleia().getDataFim().before(hoje))) {
                mensagem += "A assembleia está encerrada<br/>";
            } else {
                if ((questao.getTitulo() == null)
                        || (questao.getTitulo().trim().isEmpty())
                        || (questao.getTitulo().trim().equals(""))) {
                    mensagem += "Necessário inserir o titulo da questão<br/>";
                } else if (questao.getTitulo().length() > 100) {
                    mensagem += "O número de caracteres do titulo não pode exceder a 100<br/>";
                } else if (questao.getTitulo().equals("ELEIÇÃO DE SÍNDICO")) {
                    mensagem += "Eleição síndical deve ser definida em AGO em formulario especifico<br/>";
                }
            }
            if ((questao.getDescricao() == null)
                    || (questao.getDescricao().trim().isEmpty())
                    || (questao.getDescricao().trim().equals(""))) {
                mensagem += "Necessário inserir a descrição da questão<br/>";
            } else if (questao.getDescricao().length() > 300) {
                mensagem += "O número de caracteres da descrição não pode exceder a 300<br/>";
            }
            if (questao.getOpcoes() != null) {
                if (questao.getOpcoes().size() > 5) {
                    mensagem += "Opçoes de questão não pode exceder a 5<br/>";
                } else {
                    for (OpcQuestao o : questao.getOpcoes()) {
                        if ((o.getDescricao() == null)
                                || (o.getDescricao().trim().isEmpty())
                                || (o.getDescricao().trim().equals(""))) {
                            mensagem += "Necessário inserir a descrição da opção " + Integer.toString(questao.getOpcoes().indexOf(o) + 1);
                        } else {
                            o.setDescricao(o.getDescricao().trim().toUpperCase());
                            if ((o.getDescricao().isEmpty()) || (o.getDescricao().equals(""))) {
                                mensagem += "Necessário inserir uma descrição válida para a opção " + Integer.toString(questao.getOpcoes().indexOf(o) + 1) + " <br/>";
                            } else if (o.getDescricao().length() > 100) {
                                mensagem += "O número de caracteres da descrição não pode exceder a 100, opção: " + Integer.toString(questao.getOpcoes().indexOf(o) + 1) + " <br/>";
                            }
                        }
                    }
                }
            }
            if (questao.getArquivos() != null) {
                if (questao.getArquivos().size() > 5) {
                    mensagem += "Arquivos de questão não pode exceder a 5<br/>";
                } else {
                    for (Arquivo a : questao.getArquivos()) {
                        try {
                            ArquivoValidator.validaImagem(a);
                        } catch (ArquivoException ex) {
                            mensagem += "Arquivo n°" + Integer.toString(questao.getArquivos().indexOf(a) + 1)
                                    + " inválido: " + ex.getMessage() + "<br/>";
                        }
                    }
                }
            }
            if (!mensagem.equals("")) {
                throw new QuestaoException(mensagem);
            }
        }
    }

    public static void validaEleicao(Assembleia assembleia, List<Morador> moradores) throws QuestaoException {
        Date hoje = new Date();

        if ((moradores == null) || (moradores.isEmpty()) || (moradores.size() < 2)) {
            throw new QuestaoException("Necessário indicar ao menos dois candidatos");
        } else if ((assembleia == null) || (assembleia.getId() == 0)) {
            throw new QuestaoException("Assembléia inválida");
        } else if (assembleia.getTipo() != 2) {
            throw new QuestaoException("Eleição sindical deve ser definida em AGO");
        } else if ((assembleia.getEncerramento() != 0) || (assembleia.getDataFim().before(hoje))) {
            throw new QuestaoException("A assembleia está encerrada");
        } else {
            for (int i = 0; moradores.size() > i; i++) {
                if (moradores.get(i).getImagem() == null) {
                    throw new QuestaoException("Necessário que morador possua imagem: "
                            + moradores.get(i).getNome());
                }
            }
            for (Questao q : assembleia.getQuestoes()) {
                if (q.getTitulo().equals("ELEIÇÃO DE SÍNDICO")) {
                    throw new QuestaoException("Já existe uma eleição de síndico em andamento");
                }
            }
        }
    }
}
