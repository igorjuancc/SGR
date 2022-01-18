package br.com.sgr.converter;

import br.com.sgr.bean.Vaga;
import br.com.sgr.facade.VagaFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "vagaEstacionamentoConverter", forClass = Vaga.class)
public class VagaEstacionamentoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return VagaFacade.vagaPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.out);
            throw new ConverterException(SgrUtil.emiteMsg("Vaga de estacionamento inválida", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((Vaga) value).getId());
    }
}
