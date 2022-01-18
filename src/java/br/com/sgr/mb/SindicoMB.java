/*
    Status [0] - Desativado (Cadastro sem aprovação / Pode ser excluído)
    Status [1] - Acesso ao predio/Sem sistema (Dependentes)
    Status [2] - Acesso ao predio/Limitado sistema (Responsavel com debito)
    Status [3] - Acesso ao predio/Total sistema (Responsavel acesso total)
    Status [4] - Desligado (Morador/Dependente/Funcionario ja aprovados não devem ser removídos)
    Status [5] - Reativado (Responsavel reativado/Redefinida Senha) 
 */
package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Morador;
import br.com.sgr.exception.EmailException;
import br.com.sgr.exception.SalaoException;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.MessagingException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "sindicoMB")
@ViewScoped
public class SindicoMB implements Serializable {

    private Morador morador;
    private Boolean cadastroConcluido;
    private List<String> blocos;
    private List<Apartamento> listaApartamento;
    private List<Object[]> listaDebitosMorador;
    private List<Integer> listaIdMoradorDebitos;

    private LazyDataModel<Morador> listaMoradoresTotal;
    private LazyDataModel<Morador> listaMoradoresLimitado;
    private LazyDataModel<Morador> listaMoradoresDesativado;
    private LazyDataModel<Morador> listaMoradoresReativados;
    private LazyDataModel<Morador> listaNovosMoradores;
    private FiltroBD filtroMoradores;

    @PostConstruct
    public void init() {
        try {
            this.filtroMoradores = new FiltroBD();
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoNovoMorador.xhtml":
                    iniciaListaNovosMoradores();
                    break;
                case "/sindico/SindicoMoradores.xhtml":
                    this.cadastroConcluido = false;
                    break;
                default:
                    this.listaIdMoradorDebitos = new ArrayList<>();
                    iniciaListasAcessos();
                    break;
            }
        } catch (Exception e) {
            SgrUtil.mensagemErroRedirecionamento("depois remover isso aqui****");
        }
    }

    public void iniciaListaMoradoresTotal() {
        listaMoradoresTotal = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradores.setPrimeiroRegistro(first);
                filtroMoradores.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradores.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradores.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresPorStatus(3));
                listaMoradorFiltro = MoradorFacade.moradorStatusFiltro(filtroMoradores, 3);
                listaIdMoradorDebitos.clear();
                for (Morador m : listaMoradorFiltro) {
                    listaIdMoradorDebitos.add(m.getId());
                }
                listaDebitosMorador = MoradorFacade.listaDebitosMorador(listaIdMoradorDebitos);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaMoradoresLimitado() {
        listaMoradoresLimitado = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradores.setPrimeiroRegistro(first);
                filtroMoradores.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradores.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradores.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresPorStatus(2));

                listaMoradorFiltro = MoradorFacade.moradorStatusFiltro(filtroMoradores, 2);

                listaIdMoradorDebitos.clear();
                for (Morador m : listaMoradorFiltro) {
                    listaIdMoradorDebitos.add(m.getId());
                }
                listaDebitosMorador = MoradorFacade.listaDebitosMorador(listaIdMoradorDebitos);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaMoradoresDesligado() {
        listaMoradoresDesativado = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {

                filtroMoradores.setPrimeiroRegistro(first);
                filtroMoradores.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradores.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradores.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresPorStatus(4));

                listaMoradorFiltro = MoradorFacade.moradorStatusFiltro(filtroMoradores, 4);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaMoradoresReativado() {
        listaMoradoresReativados = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradores.setPrimeiroRegistro(first);
                filtroMoradores.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradores.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradores.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresPorStatus(5));
                listaMoradorFiltro = MoradorFacade.moradorStatusFiltro(filtroMoradores, 5);
                return listaMoradorFiltro;
            }
        };
    }

    public void iniciaListaNovosMoradores() {
        listaNovosMoradores = new LazyDataModel<Morador>() {
            private static final long serialVersionUID = 1L;
            List<Morador> listaMoradorFiltro = null;

            @Override
            public List<Morador> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMoradores.setPrimeiroRegistro(first);
                filtroMoradores.setQntdRegistros(Long.valueOf(pageSize));
                filtroMoradores.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMoradores.setPropriedadeOrdenacao(sortField);
                setRowCount(MoradorFacade.totalMoradoresPorStatus(0));
                listaMoradorFiltro = MoradorFacade.moradorStatusFiltro(filtroMoradores, 0);
                return listaMoradorFiltro;
            }
        };
    }

    public void atualizarStatusMorador(Morador morador) {
        FacesContext ctx = null;
        String mensagem = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.atualizarStatusMorador(morador);

            switch (morador.getStatus()) {
                case 1:
                    mensagem = "Definido acesso para dependente: " + morador.getNome();
                    break;
                case 2:
                    mensagem = "Definido acesso limitado ao sistema para: " + morador.getNome();
                    break;
                case 3:
                    mensagem = "Definido acesso total ao sistema para: " + morador.getNome();
                    break;
                case 4:
                    mensagem = "Morador " + morador.getNome() + " desligado";
                    break;
            }

            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoNovoMorador.xhtml":
                    iniciaListaNovosMoradores();
                    break;
                default:
                    iniciaListasAcessos();
                    break;
            }
            ctx.addMessage(null, SgrUtil.emiteMsg(mensagem, 1));
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao modificar status de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao modificar status de morador");
        }
    }

    public void removerMorador(Morador morador) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.apagarMorador(morador);
            String mensagem = "Morador removído: " + morador.getNome();
            FacesMessage msg = SgrUtil.emiteMsg(mensagem, 1);
            ctx.addMessage(null, msg);
            iniciaListaNovosMoradores();
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover morador");
        }
    }

    public void desativarMorador(Morador morador) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.desativarMorador(morador);
            String mensagem = "Morador desativado: " + morador.getNome();
            FacesMessage msg = SgrUtil.emiteMsg(mensagem, 1);
            ctx.addMessage(null, msg);
            iniciaListasAcessos();
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao desativar morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao desativar morador");
        }
    }

    public void reativarMorador() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            MoradorFacade.reativarMorador(this.morador);
            String mensagem = "Morador reativado: " + morador.getNome();
            FacesMessage msg = SgrUtil.emiteMsg(mensagem, 1);
            ctx.addMessage(null, msg);
            this.cadastroConcluido = true;
        } catch (MoradorException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao reativar morador");
            }
        } catch (EmailException | MessagingException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao reativar morador");
        }
    }

    public String responsavelApto(Apartamento apto) {
        String rtn = "";
        apto.setMoradores(MoradorFacade.listaMoradorPorApto(apto));
        for (Morador m : apto.getMoradores()) {
            if (m.getTipo() == 1) {
                rtn = m.getNome();
            }
        }
        return rtn;
    }

    public void iniciaListasAcessos() {
        iniciaListaMoradoresTotal();
        iniciaListaMoradoresLimitado();
        iniciaListaMoradoresDesligado();
        iniciaListaMoradoresReativado();
        this.cadastroConcluido = false;
    }

    public void setEditMorador(Morador moradorEdit) {
        try {
            this.morador = moradorEdit;
            this.morador.setApartamento(new Apartamento());
            this.blocos = ApartamentoFacade.blocosApartamentos();
            this.morador.getApartamento().setBloco(blocos.get(0));
            buscaApartamentoBloco();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao iniciar processo de reativação de morador");
        }
    }

    public void buscaApartamentoBloco() {
        if (morador.getTipo() == 1) {
            this.listaApartamento = ApartamentoFacade.apartamentosVagosPorBloco(morador.getApartamento().getBloco());
        } else {
            this.listaApartamento = ApartamentoFacade.apartamentosOcupadosPorBloco(morador.getApartamento().getBloco());
        }
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }

    public String alertaDebito(int idMorador) {
        try {
            String rtn = null;
            int tot = 0;
            for (int i = 0; this.listaDebitosMorador.size() > i; i++) {
                if (idMorador == (int) this.listaDebitosMorador.get(i)[0]) {
                    if ((this.listaDebitosMorador.get(i)[4] != null) 
                            && (int) this.listaDebitosMorador.get(i)[4] > 0) {
                        tot++;
                    }
                    if ((this.listaDebitosMorador.get(i)[5] != null) 
                            && (int) this.listaDebitosMorador.get(i)[5] > 0) {
                        tot++;
                    }
                    if ((this.listaDebitosMorador.get(i)[6] != null) 
                            && ((int) this.listaDebitosMorador.get(i)[6] > 0)) {
                        tot = tot + (int) this.listaDebitosMorador.get(i)[6];
                    }
                    i = this.listaDebitosMorador.size();
                }
            }
            if (tot == 2) {
                rtn = "#DAA520";
            }
            if (tot > 2) {
                rtn = "#FF0000";
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao verificar débitos de morador");
            return null;
        }
    }

    //Nova senha via SindicoMoradores.jsf
    public void recuperarAcessoMorador(Morador moradorRec) {
        try {
            this.morador = moradorRec;
            MoradorFacade.recuperarAcesso(this.morador, 0);
        } catch (MoradorException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao recuperar acesso de morador");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao recuperar acesso de morador");
        }
    }

    public LazyDataModel<Morador> getListaNovosMoradores() {
        return listaNovosMoradores;
    }

    public void setListaNovosMoradores(LazyDataModel<Morador> listaNovosMoradores) {
        this.listaNovosMoradores = listaNovosMoradores;
    }

    public LazyDataModel<Morador> getListaMoradoresTotal() {
        return listaMoradoresTotal;
    }

    public void setListaMoradoresTotal(LazyDataModel<Morador> listaMoradoresTotal) {
        this.listaMoradoresTotal = listaMoradoresTotal;
    }

    public LazyDataModel<Morador> getListaMoradoresLimitado() {
        return listaMoradoresLimitado;
    }

    public void setListaMoradoresLimitado(LazyDataModel<Morador> listaMoradoresLimitado) {
        this.listaMoradoresLimitado = listaMoradoresLimitado;
    }

    public LazyDataModel<Morador> getListaMoradoresDesativado() {
        return listaMoradoresDesativado;
    }

    public void setListaMoradoresDesativado(LazyDataModel<Morador> listaMoradoresDesativado) {
        this.listaMoradoresDesativado = listaMoradoresDesativado;
    }

    public LazyDataModel<Morador> getListaMoradoresReativados() {
        return listaMoradoresReativados;
    }

    public void setListaMoradoresReativados(LazyDataModel<Morador> listaMoradoresReativados) {
        this.listaMoradoresReativados = listaMoradoresReativados;
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public List<String> getBlocos() {
        return blocos;
    }

    public void setBlocos(List<String> blocos) {
        this.blocos = blocos;
    }

    public List<Apartamento> getListaApartamento() {
        return listaApartamento;
    }

    public void setListaApartamento(List<Apartamento> listaApartamento) {
        this.listaApartamento = listaApartamento;
    }

    public List<Object[]> getListaDebitosMorador() {
        return listaDebitosMorador;
    }

    public void setListaDebitosMorador(List<Object[]> listaDebitosMorador) {
        this.listaDebitosMorador = listaDebitosMorador;
    }
}
