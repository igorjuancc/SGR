package br.com.sgr.converter;

import br.com.sgr.bean.Morador;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="moradorConverter", forClass=Morador.class)
public class MoradorConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {        
        try { 
            return MoradorFacade.moradorPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {            
            ex.printStackTrace(System.out);
            throw new ConverterException (SgrUtil.emiteMsg("Morador Inválido", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((Morador) value).getId());
    }    
}
