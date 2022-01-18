package br.com.sgr.converter;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="apartamentoConverter", forClass=Apartamento.class)
public class ApartamentoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {        
        try { 
            return ApartamentoFacade.apartamentoPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {            
            ex.printStackTrace(System.out);
            throw new ConverterException (SgrUtil.emiteMsg("Apartamento Inválido", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((Apartamento) value).getId());
    }    
}
