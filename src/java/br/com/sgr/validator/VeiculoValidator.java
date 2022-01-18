package br.com.sgr.validator;

import br.com.sgr.bean.Veiculo;
import br.com.sgr.exception.VeiculoException;

public class VeiculoValidator {

    public static void validaVeiculo(Veiculo veiculo) throws VeiculoException {
        if (veiculo == null) {
            throw new VeiculoException("Veículo inválido");
        } else {
            String mensagem = "";

            if (veiculo.getPlaca() == null) {
                mensagem += "Necessário indicar a placa do veiculo<br/>";
            } else {
                if ((veiculo.getPlaca().trim().equals("")) || (veiculo.getPlaca().trim().isEmpty())) {
                    mensagem += "Placa de veiculo inválida<br/>";
                }
            }

            if (veiculo.getMarca() == null) {
                mensagem += "Necessário indicar a marca do veiculo<br/>";
            } else {
                if ((veiculo.getMarca().trim().equals("")) || (veiculo.getMarca().trim().isEmpty())) {
                    mensagem += "Marca de veiculo inválida<br/>";
                }
            }

            if (veiculo.getModelo() == null) {
                mensagem += "Necessário indicar o modelo do veiculo<br/>";
            } else {
                if ((veiculo.getModelo().trim().equals("")) || (veiculo.getModelo().trim().isEmpty())) {
                    mensagem += "Modelo de veiculo inválido<br/>";
                }
            }

            if (veiculo.getCor() == null) {
                mensagem += "Necessário indicar a cor do veiculo<br/>";
            } else {
                if ((veiculo.getCor().trim().equals("")) || (veiculo.getCor().trim().isEmpty())) {
                    mensagem += "Cor do veiculo inválida<br/>";
                }
            }

            if ((veiculo.getAno() == 0) || (veiculo.getAno() < 1807) || (veiculo.getAno() > 2030)) {
                mensagem += "Ano do veiculo inválido<br/>";
            }

            if ((veiculo.getMorador() == null) || veiculo.getMorador().getId() == 0) {
                mensagem += "Morador inválido<br/>";
            }

            if (!mensagem.equals("")) {
                throw new VeiculoException(mensagem);
            }
        }
    }
}
