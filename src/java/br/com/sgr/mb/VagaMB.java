package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Atendimento;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Vaga;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.VagaException;
import br.com.sgr.facade.ApartamentoFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.VagaFacade;
import br.com.sgr.facade.VeiculoFacade;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

@ManagedBean
@Named(value = "vagaMB")
@ViewScoped
public class VagaMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Boolean cadastroConcluido;
    private Vaga vagaEstacionamento;
    private Morador moradorResponsavel;
    private List<Vaga> vagas;
    private List<Vaga> vagas2;
    private List<Vaga> vagasDisponiveis;
    private List<String> blocos;
    private List<Apartamento> listaApartamento;
    private List<Morador> listaMoradores;

    @PostConstruct
    public void init() {
        try {
            this.vagaEstacionamento = new Vaga();
            iniciaListas();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListas() {
        try {
            this.vagas = VagaFacade.vagas();
            this.vagas2 = new ArrayList<>(this.vagas.subList(10, 20));
            this.vagas.removeAll(this.vagas2);

            if (this.login.getFuncionario().getId() != 0) {
                this.blocos = ApartamentoFacade.blocosApartamentos();
                this.listaApartamento = ApartamentoFacade.apartamentosOcupadosPorBloco(this.blocos.get(0));
                this.vagasDisponiveis = VagaFacade.vagasDisponiveis();
                if (!this.listaApartamento.isEmpty()) {
                    this.vagaEstacionamento.setApartamento(this.listaApartamento.get(0));
                    this.vagaEstacionamento.getApartamento().setVagas(VagaFacade.vagasDeApto(this.vagaEstacionamento.getApartamento().getId()));
                    this.vagaEstacionamento.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(this.vagaEstacionamento.getApartamento()));
                    this.listaMoradores = this.vagaEstacionamento.getApartamento().getMoradores();
                    for (Morador m : this.vagaEstacionamento.getApartamento().getMoradores()) {
                        if (m.getTipo() == 1) {
                            this.moradorResponsavel = m;
                            this.moradorResponsavel.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(m));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de vagas");
        }
    }

    public void buscaApartamentoBloco() throws DaoException {
        try {
            if (this.vagaEstacionamento.getApartamento() != null) {
                this.listaApartamento = ApartamentoFacade.apartamentosOcupadosPorBloco(this.vagaEstacionamento.getApartamento().getBloco());
                if (!this.listaApartamento.isEmpty()) {
                    this.vagaEstacionamento.setApartamento(this.listaApartamento.get(0));
                    this.vagaEstacionamento.getApartamento().setVagas(VagaFacade.vagasDeApto(this.vagaEstacionamento.getApartamento().getId()));
                    this.vagaEstacionamento.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(this.vagaEstacionamento.getApartamento()));
                    this.listaMoradores = this.vagaEstacionamento.getApartamento().getMoradores();
                    for (Morador m : this.vagaEstacionamento.getApartamento().getMoradores()) {
                        if (m.getTipo() == 1) {
                            this.moradorResponsavel = m;
                            this.moradorResponsavel.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(m));
                        }
                    }
                }
            } else {
                FacesMessage msg = SgrUtil.emiteMsg("Lista de apartamentos indisponível", 1);
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, msg);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de apartamento");
        }
    }

    //Opc 1 - Definir Opc 2 - Desvincular
    public void definirVaga(int opc) {
        try {
            if (opc == 1) {
                VagaFacade.editarVaga(this.vagaEstacionamento, opc);
                this.cadastroConcluido = true;
            } else {
                this.vagaEstacionamento.setApartamento(null);
                VagaFacade.editarVaga(this.vagaEstacionamento, opc);
                iniciaListas();
                FacesMessage msg = SgrUtil.emiteMsg("Vaga desvinculada de apartamento", 1);
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, msg);
            }
            PrimeFaces.current().ajax().update("dialogFormVaga");
        } catch (VagaException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                PrimeFaces.current().ajax().update("formVagas:msg");
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de vaga");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de vaga");
        }
    }

    public void setVagaApto(Vaga vagaSet) {
        try {
            this.vagaEstacionamento = vagaSet;
            if (this.vagaEstacionamento.getApartamento() == null) {
                this.cadastroConcluido = null;
                if (!this.listaApartamento.isEmpty()) {
                    this.vagaEstacionamento.setApartamento(this.listaApartamento.get(0));
                } else {
                    this.vagaEstacionamento.setApartamento(new Apartamento());
                    this.vagaEstacionamento.getApartamento().setBloco("-");
                }
            } else {
                this.vagaEstacionamento.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(this.vagaEstacionamento.getApartamento()));
                this.listaMoradores = this.vagaEstacionamento.getApartamento().getMoradores();
                this.cadastroConcluido = false;
            }
            if (!this.listaApartamento.isEmpty()) {
                this.vagaEstacionamento.getApartamento().setVagas(VagaFacade.vagasDeApto(this.vagaEstacionamento.getApartamento().getId()));
                for (Morador m : this.vagaEstacionamento.getApartamento().getMoradores()) {
                    if (m.getTipo() == 1) {
                        this.moradorResponsavel = m;
                        this.moradorResponsavel.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(m));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de vagas");
        }
    }

    public String statusAtendimento(Atendimento atendimento) {
        try {
            String status = null;
            switch (atendimento.getStatus()) {
                case 0:
                    status = "ABERTO";
                    break;
                case 1:
                    status = "FECHADO";
                    break;
                case 2:
                    status = "EM ATENDIMENTO";
                    break;
                case 3:
                    status = "REABERTO";
                    break;
                case 4:
                    status = "REABERTURA";
                    break;
            }
            return status;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de atendimento");
            return null;
        }
    }

    public void redirecionar(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException ex) {
            Logger.getLogger(VagaMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setListaMoradores() {
        try {
            this.vagaEstacionamento.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(this.vagaEstacionamento.getApartamento()));
            this.vagaEstacionamento.getApartamento().setVagas(VagaFacade.vagasDeApto(this.vagaEstacionamento.getApartamento().getId()));
            this.listaMoradores = this.vagaEstacionamento.getApartamento().getMoradores();
            for (Morador m : this.vagaEstacionamento.getApartamento().getMoradores()) {
                if (m.getTipo() == 1) {
                    this.moradorResponsavel = m;
                    this.moradorResponsavel.setVeiculos(VeiculoFacade.listaVeiculosDeMorador(m));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de moradores");
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public Vaga getVagaEstacionamento() {
        return vagaEstacionamento;
    }

    public void setVagaEstacionamento(Vaga vagaEstacionamento) {
        this.vagaEstacionamento = vagaEstacionamento;
    }

    public List<Vaga> getVagas() {
        return vagas;
    }

    public void setVagas(List<Vaga> vagas) {
        this.vagas = vagas;
    }

    public List<Vaga> getVagas2() {
        return vagas2;
    }

    public void setVagas2(List<Vaga> vagas2) {
        this.vagas2 = vagas2;
    }

    public List<Vaga> getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(List<Vaga> vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
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

    public Morador getMoradorResponsavel() {
        return moradorResponsavel;
    }

    public void setMoradorResponsavel(Morador moradorResponsavel) {
        this.moradorResponsavel = moradorResponsavel;
    }

    public List<Morador> getListaMoradores() {
        return listaMoradores;
    }

    public void setListaMoradores(List<Morador> listaMoradores) {
        this.listaMoradores = listaMoradores;
    }
}
