package br.com.sgr.validator;

import br.com.sgr.bean.Infracao;
import br.com.sgr.exception.InfracaoException;

public class InfracaoValidator {

    public static void validaInfracao(Infracao infracao) throws InfracaoException {
        if (infracao == null) {
            throw new InfracaoException("Infração inválida");
        } else {
            String mensagem = "";
            
            if ((infracao.getDescricao() == null)
                    || infracao.getDescricao().trim().equals("")
                    || infracao.getDescricao().isEmpty()) {
                mensagem += "Descrição inválida<br/>";                
            } else if (infracao.getDescricao().trim().length() > 100){
                mensagem += "Descrição não pode ultrapassar [100] caracteres<br/>";                
            } else if (infracao.getDescricao().trim().length() < 3) {
                mensagem += "Descrição não pode ter menos que [3] caracteres<br/>";                
            }            
            if ((infracao.getClassificacao() < 0) || (infracao.getClassificacao() > 3)) {
                mensagem += "Classificação de descrição inválida<br/>";                
            }
            
            if (!mensagem.equals("")) {
                throw new InfracaoException(mensagem);
            }                       
        }
    }
}
