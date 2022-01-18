package br.com.sgr.validator;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.NotificacaoException;
import java.util.Date;

public class NotificacaoValidator {

    public static void validaNotificacao(Notificacao notificacao) throws NotificacaoException {
        if (notificacao == null) {
            throw new NotificacaoException("Notificação inválida");
        } else {
            String mensagem = "";
            Date hoje = new Date();

            if ((notificacao.getTipo() < 0) && (notificacao.getTipo() > 1)) {
                mensagem += "Tipo de notificação inválido<br/>";
            }
            if ((notificacao.getDataReferencia() == null) || (notificacao.getDataReferencia().after(hoje))) {
                mensagem += "Data da ocorrência inválida<br/>";
            }
            if ((notificacao.getDataEmissao() == null) || (notificacao.getDataEmissao().before(notificacao.getDataReferencia()))) {
                mensagem += "Problemas com a data de emissão<br/>";
            }
            if (notificacao.getMorador() == null) {
                mensagem += "Morador inválido<br/>";
            }
            if ((notificacao.getFuncionario() == null) || (notificacao.getFuncionario().getFuncao().getId() != 1)) {
                mensagem += "Funcionário inválido<br/>";
            }
            if (notificacao.getInfracao() == null) {
                mensagem += "Infração inválida<br/>";
            }
            if ((notificacao.getDescricao() != null) && (!notificacao.getDescricao().trim().isEmpty())) {
                if (notificacao.getDescricao().length() > 200) {
                    mensagem += "Descrição de notificação não pode exceder a 200 caracteres<br/>";
                }
            }
            if (notificacao.getArquivos() != null) {
                if (notificacao.getArquivos().size() > 3) {
                    mensagem += "Notificação não pode ter mais do que [3] arquivos<br/>";
                } else {
                    for (Arquivo a : notificacao.getArquivos()) {
                        try {
                            ArquivoValidator.filtroExtencaoArquivo(a);
                        } catch (ArquivoException ex) {
                            mensagem += "Arquivo n°" + Integer.toString(notificacao.getArquivos().indexOf(a) + 1)
                                    + " inválido: " + ex.getMessage() + "<br/>";
                        }
                    }
                }
            }

            if (!mensagem.equals("")) {
                throw new NotificacaoException(mensagem);
            }
        }
    }
}
