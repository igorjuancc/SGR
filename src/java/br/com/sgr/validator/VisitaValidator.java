package br.com.sgr.validator;

import br.com.sgr.bean.Visita;
import br.com.sgr.exception.VisitaException;
import br.com.sgr.util.SgrUtil;

public class VisitaValidator {

    public static void validaVisita(Visita visita) throws VisitaException {
        if (visita == null) {
            throw new VisitaException("Visita inválida");
        } else {
            String mensagem = "";
            if (visita.getDataInicio() == null) {
                mensagem += "Data inicial inválida<br/>";
            }
            if (visita.getDataFim() == null) {
                mensagem += "Data final inválida<br/>";
            }
            if ((visita.getDataInicio() != null) && (visita.getDataFim() != null)) {
                if (visita.getDataInicio().after(visita.getDataFim())) {
                    mensagem += "A data final da visita não pode ser anterior a data inicial: " + SgrUtil.formataData(visita.getDataInicio()) + "<br/>";
                } else {
                    long dias = ((visita.getDataFim().getTime() - visita.getDataInicio().getTime()) / 1000 / 60 / 60 / 24);
                    if (dias > 3) {
                        mensagem += "O tempo máximo de duração de uma visita não pode ser maior que 3 dias<br/>";
                    }
                }
            }
            if ((visita.getVisitantes() == null) || (visita.getVisitantes().isEmpty())) {
                mensagem += "Necessário adicionar um ou mais visitantes<br/>";
            } else if (visita.getVisitantes().size() > 30) {
                mensagem += "Número de visitantes não pode ser superior há [30]<br/>";
            }
            if ((visita.getApartamento() == null) || (visita.getApartamento().getId() == 0)) {
                mensagem += "Necessário indicar o apartamento da visita<br/>";
            }
            if (!mensagem.equals("")) {
                throw new VisitaException(mensagem);
            }
        }
    }
}
