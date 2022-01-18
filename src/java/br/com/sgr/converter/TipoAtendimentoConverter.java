package br.com.sgr.converter;

import br.com.sgr.bean.TipoAtendimento;
import br.com.sgr.facade.TipoAtendimentoFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "tipoAtendimentoConverter", forClass = TipoAtendimento.class)
public class TipoAtendimentoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return TipoAtendimentoFacade.tipoAtendimentoPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.out);
            throw new ConverterException(SgrUtil.emiteMsg("Tipo de Atendimento Inválido", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((TipoAtendimento) value).getId());
    }
}
