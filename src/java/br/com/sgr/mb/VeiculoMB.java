package br.com.sgr.mb;

import br.com.sgr.bean.Veiculo;
import br.com.sgr.exception.VeiculoException;
import br.com.sgr.facade.VeiculoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "veiculoMB")
@ViewScoped
public class VeiculoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Veiculo veiculo;
    private Boolean cadastroConcluido;
    private int tipoPlaca;

    private LazyDataModel<Veiculo> veiculosMorador;
    private FiltroBD filtroVeiculo;

    @PostConstruct
    public void init() {
        try {
            this.veiculo = new Veiculo();
            this.filtroVeiculo = new FiltroBD();
            iniciaListaVeiculo();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaVeiculo() {
        veiculosMorador = new LazyDataModel<Veiculo>() {
            private static final long serialVersionUID = 1L;
            List<Veiculo> listaVeiculosFiltro = null;

            @Override
            public List<Veiculo> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroVeiculo.setPrimeiroRegistro(first);
                filtroVeiculo.setQntdRegistros(Long.valueOf(pageSize));
                filtroVeiculo.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroVeiculo.setPropriedadeOrdenacao(sortField);
                setRowCount(VeiculoFacade.totalVeiculosMorador(login.getMorador()));
                listaVeiculosFiltro = VeiculoFacade.veiculosFiltroMorador(filtroVeiculo, login.getMorador().getId());
                return listaVeiculosFiltro;
            }
        };
    }

    public void novoVeiculo() {
        try {
            this.veiculo.setMorador(this.login.getMorador());
            VeiculoFacade.cadastrarVeiculo(this.veiculo);
            this.cadastroConcluido = true;
        } catch (VeiculoException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar veículo");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar veículo");
        }
    }

    public void removerVeiculo(Veiculo veiculo) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (veiculo.getMorador().getId() != this.login.getMorador().getId()) {
                ctx.addMessage(null, SgrUtil.emiteMsg("Veiculo não pode ser removído", 3));
            } else {
                VeiculoFacade.removerVeiculo(veiculo);
                ctx.addMessage(null, SgrUtil.emiteMsg("Veiculo removído: " + veiculo.getMarca() + " " + veiculo.getModelo(), 1));
                iniciaListaVeiculo();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover veículo");
        }
    }

    public void atualizarVeiculo() {
        try {
            VeiculoFacade.atualizarVeiculo(this.veiculo);
            this.cadastroConcluido = true;
        } catch (VeiculoException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar veículo");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar veículo");
        }
    }

    public void setEditVeiculo(Veiculo veiculo) {
        if (!veiculo.getPlaca().contains("-")) {
            this.tipoPlaca = 1;
        }
        this.veiculo = veiculo;
    }

    public void redirecionar(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public LazyDataModel<Veiculo> getVeiculosMorador() {
        return veiculosMorador;
    }

    public void setVeiculosMorador(LazyDataModel<Veiculo> veiculosMorador) {
        this.veiculosMorador = veiculosMorador;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public int getTipoPlaca() {
        return tipoPlaca;
    }

    public void setTipoPlaca(int tipoPlaca) {
        this.tipoPlaca = tipoPlaca;
    }
}
