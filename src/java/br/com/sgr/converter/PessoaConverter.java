package br.com.sgr.converter;

import br.com.sgr.bean.Pessoa;
import br.com.sgr.facade.PessoaFacade;
import br.com.sgr.util.SgrUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "pessoaConverter", forClass = Pessoa.class)
public class PessoaConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return PessoaFacade.pessoaPorId(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.out);
            throw new ConverterException(SgrUtil.emiteMsg("Pessoa Inválida", 3));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((Pessoa) value).getId());
    }
}
