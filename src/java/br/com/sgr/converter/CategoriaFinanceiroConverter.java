package br.com.sgr.converter;

import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.facade.CategoriaFinanceiroFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "categoriaFinanceiroConverter", forClass = CategoriaFinanceiro.class)
public class CategoriaFinanceiroConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return CategoriaFinanceiroFacade.categoriaFinanceiroPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.out);
            throw new ConverterException(SgrUtil.emiteMsg("Categoira financeira Inválida", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return Integer.toString(((CategoriaFinanceiro) value).getId());
    }
}
