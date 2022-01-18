package br.com.sgr.converter;

import br.com.sgr.bean.Funcao;
import br.com.sgr.facade.FuncaoFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "funcaoConverter", forClass = Funcao.class)
public class FuncaoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return FuncaoFacade.funcaoPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.out);
            throw new ConverterException(SgrUtil.emiteMsg("Função Inválida", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((Funcao) value).getId());
    }
}
