package br.com.sgr.converter;

import br.com.sgr.bean.Infracao;
import br.com.sgr.facade.NotificacaoFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="infracaoConverter", forClass=Infracao.class)
public class InfracaoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {        
        try { 
            return NotificacaoFacade.infracaoPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {            
            ex.printStackTrace(System.out);
            throw new ConverterException (SgrUtil.emiteMsg("Notificação Inválida", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((Infracao) value).getId());
    }    
}
