package br.com.sgr.mb;

import br.com.sgr.bean.Atendimento;
import br.com.sgr.bean.Comentario;
import br.com.sgr.bean.TipoAtendimento;
import br.com.sgr.bean.Vaga;
import br.com.sgr.exception.AtendimentoException;
import br.com.sgr.exception.ComentarioException;
import br.com.sgr.facade.AtendimentoFacade;
import br.com.sgr.facade.ComentarioFacade;
import br.com.sgr.facade.TipoAtendimentoFacade;
import br.com.sgr.facade.VagaFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "atendimentoMB")
@ViewScoped
public class AtendimentoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Boolean cadastroConcluido;
    private Date dataSalao;
    private Atendimento atendimento;
    private Comentario comentario;
    private Vaga vagaEstacionamento;
    private List<TipoAtendimento> listaTipoAtendimento;
    private List<Vaga> vagasDisponiveis;

    private LazyDataModel<Atendimento> listaAtendimentoAberto;
    private LazyDataModel<Atendimento> listaAtendimentoFechado;
    private LazyDataModel<Atendimento> listaAtendimentoReaberto;
    private LazyDataModel<Atendimento> listaAtendimentoEmAtendimento;
    private FiltroBD filtroAtendimento;

    @PostConstruct
    public void init() {
        try {
            this.atendimento = new Atendimento();
            this.comentario = new Comentario();
            this.filtroAtendimento = new FiltroBD();
            iniciaListas();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaAtendimentoAberto() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                //Aberto 0, Fechado 1, Em atendimento 2, Reaberto 3
                setRowCount(AtendimentoFacade.totalAtendimentosPorStatus(0));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltro(filtroAtendimento, 0);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoFechado() {
        listaAtendimentoFechado = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                //Aberto 0, Fechado 1, Em atendimento 2, Reaberto 3
                setRowCount(AtendimentoFacade.totalAtendimentosPorStatus(1));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltro(filtroAtendimento, 1);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoReaberto() {
        listaAtendimentoReaberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                //Aberto 0, Fechado 1, Em Atendimento 2, Reaberto 3
                setRowCount(AtendimentoFacade.totalAtendimentosPorStatus(3));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltro(filtroAtendimento, 3);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoEmAtendimento() {
        listaAtendimentoEmAtendimento = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                //Aberto 0, Fechado 1, Em Atendimento 2, Reaberto 3
                setRowCount(AtendimentoFacade.totalAtendimentosPorStatus(2));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltro(filtroAtendimento, 2);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoMorador() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                setRowCount(AtendimentoFacade.totalAtendimentosMorador(login.getMorador()));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroMorador(filtroAtendimento, login.getMorador().getId());
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoMoradorVaga() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                setRowCount(AtendimentoFacade.totalAtendimentosMoradorTipo(login.getMorador(), 4));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroMoradorTipo(filtroAtendimento, login.getMorador().getId(), 4);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoMoradorSalao() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                setRowCount(AtendimentoFacade.totalAtendimentosMoradorTipo(login.getMorador(), 5));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroMoradorTipo(filtroAtendimento, login.getMorador().getId(), 5);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoSindicoSalao() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                setRowCount(AtendimentoFacade.totalDeAtendimentosPorTipo(5));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroTipo(filtroAtendimento, 5);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentoSindicoVagas() {
        listaAtendimentoAberto = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAtendimento.setPrimeiroRegistro(first);
                filtroAtendimento.setQntdRegistros(Long.valueOf(pageSize));
                filtroAtendimento.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAtendimento.setPropriedadeOrdenacao(sortField);
                setRowCount(AtendimentoFacade.totalDeAtendimentosPorTipo(4));
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroTipo(filtroAtendimento, 4);
                return listaAtendimentoFiltro;
            }
        };
    }

    public void setVerAtendimento(Atendimento atendimento) {
        try {
            this.atendimento = AtendimentoFacade.atendimentoPorId(atendimento.getId());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de atendimento");
        }
    }

    public void pollComentarios() {
        try {
            if ((this.atendimento.getStatus() != 1) && (this.atendimento.getStatus() != 4)) {
                this.atendimento = AtendimentoFacade.atendimentoPorId(this.atendimento.getId());
                PrimeFaces.current().ajax().update("verAtendimentoForm:dataViewComentarios");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar comentários");
        }
    }

    public void novoComentario() {
        try {
            this.comentario.setAtendimento(this.atendimento);
            this.comentario.setTipo(1);
            if (this.login.getFuncionario().getId() != 0) {
                this.comentario.setPessoa(this.login.getFuncionario());
                this.comentario.setTipoPessoa("S");
            } else {
                this.comentario.setPessoa(this.login.getMorador());
                this.comentario.setTipoPessoa("M");
            }
            ComentarioFacade.novoComentario(this.comentario);
            this.comentario.setDescricao("");
            this.comentario.setId(0);
            this.atendimento = AtendimentoFacade.atendimentoPorId(this.atendimento.getId());
        } catch (ComentarioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inserir novo comentário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inserir novo comentário");
        }
    }

    public void iniciaListas() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            String pagina = ctx.getViewRoot().getViewId();

            switch (pagina) {
                case ("/sindico/SindicoVagas.xhtml"):
                    iniciaListaAtendimentoSindicoVagas();
                    this.listaTipoAtendimento = TipoAtendimentoFacade.listaTipoAtendimento();
                    this.vagasDisponiveis = VagaFacade.vagasDisponiveis();
                    this.vagaEstacionamento = new Vaga();
                    this.dataSalao = new Date();
                    break;
                case ("/morador/MoradorVagas.xhtml"):
                    iniciaListaAtendimentoMoradorVaga();
                    this.vagasDisponiveis = VagaFacade.vagasDisponiveis();
                    this.vagaEstacionamento = new Vaga();
                    this.listaTipoAtendimento = TipoAtendimentoFacade.listaTipoAtendimento();
                    break;
                case ("/sindico/SindicoAtendimento.xhtml"):
                    iniciaListaAtendimentoAberto();
                    iniciaListaAtendimentoFechado();
                    iniciaListaAtendimentoReaberto();
                    iniciaListaAtendimentoEmAtendimento();
                    break;
                case ("/morador/MoradorAtendimento.xhtml"):
                    iniciaListaAtendimentoMorador();
                    this.listaTipoAtendimento = TipoAtendimentoFacade.listaTipoAtendimento();
                    this.vagasDisponiveis = VagaFacade.vagasDisponiveis();
                    this.vagaEstacionamento = new Vaga();
                    this.dataSalao = new Date();
                    break;
                case ("/sindico/SindicoSalao.xhtml"):
                    iniciaListaAtendimentoSindicoSalao();
                    break;
                case ("/morador/MoradorSalao.xhtml"):
                    iniciaListaAtendimentoMoradorSalao();
                    this.dataSalao = new Date();
                    this.listaTipoAtendimento = TipoAtendimentoFacade.listaTipoAtendimento();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inicializar listas de atendimentos");
        }
    }

    public String corComentario(Comentario comentario) {
        try {
            int meuId;
            if (this.login.getFuncionario().getId() != 0) {
                meuId = this.login.getFuncionario().getId();
            } else {
                meuId = this.login.getMorador().getId();
            }

            if (comentario.getPessoa().getId() == meuId) {
                return "rgba(153,51,255,0.6)";
            } else {
                if (comentario.getTipoPessoa().equals("S")) {
                    return "rgba(100,149,237,0.6)";
                } else {
                    return "rgba(127,255,212,0.6)";
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar comentário");
            return null;
        }
    }

    public void apagarComentario(Comentario comentario) {
        try {
            int meuId = this.login.getMorador().getId() != 0 ? this.login.getMorador().getId() : this.login.getFuncionario().getId();
            if (comentario.getPessoa().getId() == meuId) {
                ComentarioFacade.apagarComentario(comentario);
            } else {
                FacesMessage msg = SgrUtil.emiteMsg("Comentario não pode ser removído", 2);
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, msg);
            }
            this.atendimento = AtendimentoFacade.atendimentoPorId(this.atendimento.getId());
            iniciaListas();
        } catch (ComentarioException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar comentário");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar comentário");
        }
    }

    public void novoAtendimento() {
        try {
            this.atendimento.setMorador(this.login.getMorador());
            String descricao;

            switch (this.atendimento.getTipo().getId()) {
                case 4:
                    descricao = "Solicitação de vaga n°: " + Integer.toString(this.vagaEstacionamento.getId())
                            + " para apartamento " + Integer.toString(this.login.getMorador().getApartamento().getId());
                    this.atendimento.setDescricao(descricao);
                    break;
                case 5:
                    Date hoje = new Date();
                    descricao = "Solicitação de reserva do salão de festas para dia: " + SgrUtil.formataData(this.dataSalao);
                    this.atendimento.setDescricao(descricao);

                    if ((this.dataSalao.before(hoje)) || this.dataSalao.equals(hoje)) {
                        throw new AtendimentoException("Data do agendamento não pode ser antes ou igual a data de hoje");
                    }
                    break;
            }

            AtendimentoFacade.novoAtendimento(this.atendimento);
            this.cadastroConcluido = true;
        } catch (AtendimentoException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar novo atendimento");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar novo atendimento");
        }
    }

    public void editarAtendimento() {
        try {
            String descricao;
            switch (this.atendimento.getTipo().getId()) {
                case 4:
                    descricao = "Solicitação de vaga n°: " + Integer.toString(this.vagaEstacionamento.getId())
                            + " para apartamento " + Integer.toString(this.login.getMorador().getApartamento().getId());
                    this.atendimento.setDescricao(descricao);
                    break;
                case 5:
                    Date hoje = new Date();
                    descricao = "Solicitação de reserva do salão de festas para dia: " + SgrUtil.formataData(this.dataSalao);
                    this.atendimento.setDescricao(descricao);

                    if ((this.dataSalao.before(hoje)) || this.dataSalao.equals(hoje)) {
                        throw new AtendimentoException("Data do agendamento não pode ser antes ou igual a data de hoje");
                    }
                    break;
            }

            AtendimentoFacade.editarAtendimento(this.atendimento);
            this.cadastroConcluido = true;
        } catch (AtendimentoException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar atendimento");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar atendimento");
        }
    }

    public void apagarAtendimento(Atendimento atendimento) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            AtendimentoFacade.apagarAtendimento(atendimento);
            ctx.addMessage(null, SgrUtil.emiteMsg("Atendimento removido com sucesso", 1));
        } catch (AtendimentoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar atendimento");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar atendimento");
        }
    }

    public void reabrirAtendimento() {
        try {
            this.comentario.setAtendimento(this.atendimento);
            this.comentario.setPessoa(this.login.getMorador());
            this.comentario.setTipo(1);
            this.comentario.setTipoPessoa("M");
            ComentarioFacade.novoComentario(this.comentario);
            this.atendimento.setStatus(3);
            AtendimentoFacade.editarAtendimento(this.atendimento);
            this.cadastroConcluido = true;
        } catch (AtendimentoException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            this.atendimento.setStatus(4);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
            }
        } catch (ComentarioException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
        }
    }

    public void setReabrirAtendimento(Atendimento atendimento) {
        try {
            atendimento.setStatus(4);
            this.comentario = new Comentario();
            this.atendimento = atendimento;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
        }
    }

    public void redirecionar() {
        try {
            String pagina = null;
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/morador/MoradorAtendimento.xhtml":
                    pagina = "MoradorAtendimento.jsf";
                    break;
                case "/morador/MoradorVagas.xhtml":
                    pagina = "MoradorVagas.jsf";
                    break;
                case "/morador/MoradorSalao.xhtml":
                    pagina = "MoradorSalao.jsf";
                    break;
                case "/sindico/SindicoVagas.xhtml":
                    pagina = "SindicoVagas.jsf";
                    break;
                case "/sindico/SindicoSalao.xhtml":
                    pagina = "SindicoSalao.jsf";
                    break;
                case "/sindico/SindicoAtendimento.xhtml":
                    pagina = "SindicoAtendimento.jsf";
                    break;
                default:
                    break;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
        }
    }

    public Boolean calcularDias(Atendimento atendimento) {
        try {
            Date dataHoje = new Date();
            long ms = (dataHoje.getTime() - atendimento.getDataFechamento().getTime()) + 360000;
            int dias = (int) (ms / (24 * 60 * 60 * 1000));

            return dias > 3;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
            return null;
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
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
            return null;
        }
    }

    public void newAtendimento(int tipo) {
        try {
            this.atendimento = new Atendimento();
            switch (tipo) {
                case 4:
                    for (TipoAtendimento t : this.listaTipoAtendimento) {
                        if (t.getId() == tipo) {
                            this.atendimento.setTipo(t);
                        }
                    }
                    break;
                case 5:
                    for (TipoAtendimento t : this.listaTipoAtendimento) {
                        if (t.getId() == tipo) {
                            this.atendimento.setTipo(t);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
        }
    }

    public void realizarAtendimento() {
        try {
            this.atendimento.setStatus(2);
            this.atendimento.setFuncionario(this.login.getFuncionario());
            AtendimentoFacade.atualizarAtendimento(atendimento);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
        }
    }

    public void finalizarAtendimento() {
        try {
            this.atendimento.setStatus(1);
            this.atendimento.setDataFechamento(new Date());
            AtendimentoFacade.atualizarAtendimento(atendimento);
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, SgrUtil.emiteMsg("Atendimento finalizado", 1));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar atendimento");
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }

    public List<TipoAtendimento> getListaTipoAtendimento() {
        return listaTipoAtendimento;
    }

    public void setListaTipoAtendimento(List<TipoAtendimento> listaTipoAtendimento) {
        this.listaTipoAtendimento = listaTipoAtendimento;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public List<Vaga> getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(List<Vaga> vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public Vaga getVagaEstacionamento() {
        return vagaEstacionamento;
    }

    public void setVagaEstacionamento(Vaga vagaEstacionamento) {
        this.vagaEstacionamento = vagaEstacionamento;
    }

    public Date getDataSalao() {
        return dataSalao;
    }

    public void setDataSalao(Date dataSalao) {
        this.dataSalao = dataSalao;
    }

    public LazyDataModel<Atendimento> getListaAtendimentoAberto() {
        return listaAtendimentoAberto;
    }

    public void setListaAtendimentoAberto(LazyDataModel<Atendimento> listaAtendimentoAberto) {
        this.listaAtendimentoAberto = listaAtendimentoAberto;
    }

    public LazyDataModel<Atendimento> getListaAtendimentoFechado() {
        return listaAtendimentoFechado;
    }

    public void setListaAtendimentoFechado(LazyDataModel<Atendimento> listaAtendimentoFechado) {
        this.listaAtendimentoFechado = listaAtendimentoFechado;
    }

    public LazyDataModel<Atendimento> getListaAtendimentoReaberto() {
        return listaAtendimentoReaberto;
    }

    public void setListaAtendimentoReaberto(LazyDataModel<Atendimento> listaAtendimentoReaberto) {
        this.listaAtendimentoReaberto = listaAtendimentoReaberto;
    }

    public LazyDataModel<Atendimento> getListaAtendimentoEmAtendimento() {
        return listaAtendimentoEmAtendimento;
    }

    public void setListaAtendimentoEmAtendimento(LazyDataModel<Atendimento> listaAtendimentoEmAtendimento) {
        this.listaAtendimentoEmAtendimento = listaAtendimentoEmAtendimento;
    }
}
