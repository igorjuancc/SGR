package br.com.sgr.validator;

import br.com.sgr.bean.Financeiro;
import br.com.sgr.exception.FinanceiroException;

public class FinanceiroValidator {

    public static void validaFinanceiro(Financeiro financeiro) throws FinanceiroException {
        if (financeiro == null) {
            throw new FinanceiroException("Registro financeiro inválido");
        } else {
            String mensagem = "";

            if ((financeiro.getCategoria() == null) || (financeiro.getCategoria().getId() == 0)) {
                mensagem += "Categoria inválida<br/>";
            }
            if (financeiro.getDataRegistro() == null) {
                mensagem += "Data de registro inválida<br/>";
            }
            if ((financeiro.getFuncionario() == null) || (financeiro.getFuncionario().getId() == 0)) {
                mensagem += "Funcionário inválido<br/>";
            }
            if ((financeiro.getTipo() != 0) && (financeiro.getTipo() != 1)) {
                mensagem += "Tipo de registro inválido<br/>";
            }
            if (financeiro.getValor() == 0) {
                mensagem += "Necessário indicar o valor do registro<br/>";
            }
            if (financeiro.getDescricao() == null) {
                mensagem += "Necessário indicar a descrição do novo registro<br/>";
            } else {
                if ((financeiro.getDescricao().equals("")) || (financeiro.getDescricao().isEmpty())) {
                    mensagem += "Necessário indicar uma descrição válida para o novo registro<br/>";
                } else if (financeiro.getDescricao().length() > 50) {
                    mensagem += "O número de caracteres da descrição não pode exceder a 50<br/>";
                }
            }
            if (!mensagem.equals("")) {
                throw new FinanceiroException(mensagem);
            }
        }
    }
}
