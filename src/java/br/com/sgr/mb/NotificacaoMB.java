package br.com.sgr.mb;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Atendimento;
import br.com.sgr.bean.Infracao;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.InfracaoException;
import br.com.sgr.exception.NotificacaoException;
import br.com.sgr.facade.ArquivoFacade;
import br.com.sgr.facade.AtendimentoFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.NotificacaoFacade;
import br.com.sgr.facade.VeiculoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@ManagedBean
@Named(value = "notificacaoMB")
@ViewScoped
public class NotificacaoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private int verDiv;
    private Boolean cadastroConcluido;
    private Infracao infracao;
    private Notificacao notificacao;
    private List<Morador> moradores;
    private List<Infracao> infracoes;
    private List<UploadedFile> upArquivos;
    private List<byte[]> previewArquivos;

    private LazyDataModel<Atendimento> atendimentos;
    private LazyDataModel<Notificacao> listaNotificacoes;
    private FiltroBD filtroNotificacoes;

    @PostConstruct
    public void init() {
        try {
            this.filtroNotificacoes = new FiltroBD();
            if (FacesContext.getCurrentInstance().getViewRoot().getViewId().equals("/sindico/SindicoAdvertencia.xhtml")) {
                this.infracoes = NotificacaoFacade.listaInfracoes();
                iniciaListaNotificacoes();
            } else {
                iniciaListaNotificacoesMorador(this.login.getMorador());
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaNotificacoes() {
        listaNotificacoes = new LazyDataModel<Notificacao>() {
            private static final long serialVersionUID = 1L;
            List<Notificacao> listaNotificacaoFiltro = null;

            @Override
            public List<Notificacao> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroNotificacoes.setPrimeiroRegistro(first);
                filtroNotificacoes.setQntdRegistros(Long.valueOf(pageSize));
                filtroNotificacoes.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroNotificacoes.setPropriedadeOrdenacao(sortField);
                setRowCount(NotificacaoFacade.totalNotificacao());
                listaNotificacaoFiltro = NotificacaoFacade.notificacaoFiltro(filtroNotificacoes);
                return listaNotificacaoFiltro;
            }
        };
    }

    public void iniciaListaNotificacoesMorador(Morador moradorLista) {
        listaNotificacoes = new LazyDataModel<Notificacao>() {
            private static final long serialVersionUID = 1L;
            List<Notificacao> listaNotificacaoFiltro = null;

            @Override
            public List<Notificacao> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroNotificacoes.setPrimeiroRegistro(first);
                filtroNotificacoes.setQntdRegistros(Long.valueOf(pageSize));
                filtroNotificacoes.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroNotificacoes.setPropriedadeOrdenacao(sortField);
                setRowCount(NotificacaoFacade.totalNotificacaoMorador(moradorLista));
                listaNotificacaoFiltro = NotificacaoFacade.notificacaoFiltroMorador(filtroNotificacoes, moradorLista);
                return listaNotificacaoFiltro;
            }
        };
    }

    public void iniciaListaAtendimentos() {
        atendimentos = new LazyDataModel<Atendimento>() {
            private static final long serialVersionUID = 1L;
            List<Atendimento> listaAtendimentoFiltro = null;

            @Override
            public List<Atendimento> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroNotificacoes.setPrimeiroRegistro(first);
                filtroNotificacoes.setQntdRegistros(Long.valueOf(pageSize));
                filtroNotificacoes.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroNotificacoes.setPropriedadeOrdenacao(sortField);
                int totalAtendimento = AtendimentoFacade.totalDeAtendimentosPorTipo(3)
                        + AtendimentoFacade.totalDeAtendimentosPorTipo(7);
                setRowCount(totalAtendimento - notificacao.getAtendimentos().size());
                listaAtendimentoFiltro = AtendimentoFacade.atendimentosFiltroNot(filtroNotificacoes);
                if (notificacao.getAtendimentos() != null && notificacao.getAtendimentos().size() > 0) {
                    for (int i = 0; i <= notificacao.getAtendimentos().size() - 1; i++) {
                        for (int j = 0; j <= listaAtendimentoFiltro.size() - 1; j++) {
                            if (listaAtendimentoFiltro.get(j).getId() == notificacao.getAtendimentos().get(i).getId()) {
                                listaAtendimentoFiltro.remove(j);
                            }
                        }
                    }
                }
                return listaAtendimentoFiltro;
            }
        };
    }

    public void setNovaInfracao() {
        try {
            if (this.infracao == null) {
                this.infracao = new Infracao();
            } else {
                this.infracao.setClassificacao(0);
                this.infracao.setDescricao(" ");
                this.infracao.setId(0);
            }
            PrimeFaces.current().ajax().update("divFormNvInf");
            PrimeFaces.current().ajax().update("formInf:tabInf");
            PrimeFaces.current().executeScript("PF('divFormInf').hide()");
            PrimeFaces.current().executeScript("PF('divFormNvInf').show()");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de infração");
        }
    }

    public void setNovaNotificacao() {
        try {
            this.notificacao = new Notificacao();
            this.notificacao.setAtendimentos(new ArrayList<>());
            this.moradores = MoradorFacade.listaMoradoresResponsaveis();
            this.infracoes = NotificacaoFacade.listaInfracoes();
            this.notificacao.setFuncionario(this.login.getFuncionario());
            if (!this.moradores.isEmpty()) {
                this.notificacao.setMorador(this.moradores.get(0));
            }
            if (!this.infracoes.isEmpty()) {
                this.notificacao.setInfracao(this.infracoes.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar nova notificação");
        }
    }

    public void setDadosMorador(Morador morador) {
        try {
            this.notificacao.setMorador(MoradorFacade.moradorPorId(this.notificacao.getMorador().getId()));
            Apartamento apto = this.notificacao.getMorador().getApartamento();
            apto.setMoradores(MoradorFacade.listaMoradorPorApto(apto));

            for (int i = 0; i < apto.getMoradores().size(); i++) {
                Morador m = apto.getMoradores().get(i);
                if (m.getTipo() == 1) {
                    this.notificacao.getMorador().getApartamento().setMoradores(apto.getMoradores());
                    this.notificacao.getMorador().setVeiculos(VeiculoFacade.listaVeiculosDeMorador(this.notificacao.getMorador()));
                    iniciaListaNotificacoesMorador(this.notificacao.getMorador());
                }
                if (m.getStatus() == 4) {
                    apto.getMoradores().remove(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de morador");
        }
    }

    public void cancelaNovaInfracao() {
        try {
            if (this.infracao.getId() == 0) {
                this.infracao.setClassificacao(0);
                this.infracao.setDescricao(" ");
            } else {
                this.infracoes = NotificacaoFacade.listaInfracoes();
            }
            PrimeFaces.current().ajax().update("formInf:panelInf");
            PrimeFaces.current().executeScript("PF('divFormInf').show()");
            PrimeFaces.current().executeScript("PF('divFormNvInf').hide()");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de infração");
        }
    }

    public void novaInfracao() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NotificacaoFacade.cadastrarInfracao(this.infracao);
            this.infracoes = NotificacaoFacade.listaInfracoes();
            ctx.addMessage(null, SgrUtil.emiteMsg("Infração inserida com sucesso", 1));
            cancelaNovaInfracao();
            PrimeFaces.current().ajax().update("formInf:tabInf");
        } catch (InfracaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar infração");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar infração");
        }
    }

    public void editarInfracao() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NotificacaoFacade.editarInfracao(this.infracao);
            this.infracoes = NotificacaoFacade.listaInfracoes();
            ctx.addMessage(null, SgrUtil.emiteMsg("Infração modificada com sucesso", 1));
            cancelaNovaInfracao();
            PrimeFaces.current().ajax().update("formInf:tabInf");
        } catch (InfracaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar infração");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar infração");
        }
    }

    public void apagarInfracao(Infracao infDelete) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            NotificacaoFacade.apagarInfracao(infDelete);
            this.infracoes = NotificacaoFacade.listaInfracoes();
            ctx.addMessage(null, SgrUtil.emiteMsg("Infração removida com sucesso", 1));
        } catch (InfracaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar infração");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar infração");
        }
    }

    public void setDetalhes(int tpDet) {
        try {
            if (tpDet == 1) {
                iniciaListaAtendimentos();
            }
            this.verDiv = tpDet;
            PrimeFaces.current().ajax().update("formDetalhes");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao buscar dados de notificações");
        }
    }

    public void inserirAtendimento(Atendimento atendimento) {
        try {
            if (this.notificacao.getAtendimentos() == null) {
                this.notificacao.setAtendimentos(new ArrayList<>());
            }
            if (this.notificacao.getAtendimentos().size() >= 10) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage msg = SgrUtil.emiteMsg("Máximo de atendimentos atingido [10]", 2);
                ctx.addMessage(null, msg);
            } else {
                this.notificacao.getAtendimentos().add(atendimento);
                iniciaListaAtendimentos();
                Collections.sort(this.notificacao.getAtendimentos(), (Atendimento a1, Atendimento a2) -> a1.getDataAbertura().compareTo(a2.getDataAbertura()));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao inserir um atendimento em notificação");
        }
    }

    public void removerAtendimento(Atendimento atendimento) {
        try {
            this.notificacao.getAtendimentos().remove(atendimento);
            iniciaListaAtendimentos();
            Collections.sort(this.notificacao.getAtendimentos(), (Atendimento a1, Atendimento a2) -> a1.getDataAbertura().compareTo(a2.getDataAbertura()));
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage msg = SgrUtil.emiteMsg("Atendimento removído", 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao remover atendimento de notificação");
        }
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            if (this.upArquivos == null) {
                this.upArquivos = new ArrayList<>();
            }
            if (this.previewArquivos == null) {
                this.previewArquivos = new ArrayList<>();
            }
            if (this.notificacao.getArquivos() == null) {
                this.notificacao.setArquivos(new ArrayList<>());
            }

            if (this.notificacao.getArquivos().size() >= 3) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage msg = SgrUtil.emiteMsg("Máximo de arquivos atingido [3]", 2);
                ctx.addMessage(null, msg);
            } else {
                this.upArquivos.add(event.getFile());
                this.previewArquivos.add(event.getFile().getContents());

                Arquivo arq = new Arquivo();
                arq.setNotificacao(this.notificacao);
                arq.setExtensao(event.getFile().getFileName().toUpperCase());
                arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                        arq.getExtensao().length()));
                this.notificacao.getArquivos().add(arq);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao realizar upload de arquivo");
        }
    }

    public void apagarArquivo(Arquivo arquivo) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            for (Arquivo a : this.notificacao.getArquivos()) {
                a.setDescricao(a.getDescricao());
            }
            if (arquivo.getId() != 0) {
                NotificacaoFacade.apagarArquivo(arquivo);
            }
            int indice = this.notificacao.getArquivos().indexOf(arquivo);
            this.previewArquivos.remove(indice);
            this.upArquivos.remove(indice);
            this.notificacao.getArquivos().remove(arquivo);
            ctx.addMessage(null, SgrUtil.emiteMsg("Arquivo deletado com sucesso", 1));
        } catch (NotificacaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar arquivo de notificação");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar arquivo de notificação");
        }
    }

    public void salvarNotificacao() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            int idNotificacao = this.notificacao.getId();
            NotificacaoFacade.novaNotificacao(this.notificacao, this.upArquivos);
            this.cadastroConcluido = true;
            this.notificacao.setId(idNotificacao);
        } catch (NotificacaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notificação");
            }
        } catch (ArquivoException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notificação");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova notificação");
        }
    }

    public void apagarNotificacao(Notificacao notifi) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            notifi.setArquivos(ArquivoFacade.listaArquivoNotificacao(notifi.getId()));
            NotificacaoFacade.apagarNotificacao(notifi);
            iniciaListaNotificacoes();
            ctx.addMessage(null, SgrUtil.emiteMsg("Notificação deletada com sucesso", 1));
        } catch (NotificacaoException e) {

        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg("Notificação deletada com sucesso", 1));
                ctx.addMessage(null, SgrUtil.emiteMsg(ex.getMessage(), 3));
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar notificação");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar notificação");
        }
    }

    public void setVerNotificacao(Notificacao notifi) {
        try {
            this.notificacao = notifi;
            this.notificacao.setArquivos(ArquivoFacade.listaArquivoNotificacao(notifi.getId()));
            this.notificacao.setAtendimentos(AtendimentoFacade.atendimentosPorNotificacao(this.notificacao.getId()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de notificação");
        }
    }

    public void setEdicaoNotificacao(Notificacao notifi) {
        try {
            this.moradores = MoradorFacade.listaMoradoresResponsaveis();
            this.infracoes = NotificacaoFacade.listaInfracoes();
            this.notificacao = notifi;
            this.notificacao.setArquivos(ArquivoFacade.listaArquivoNotificacao(notifi.getId()));
            this.notificacao.setAtendimentos(AtendimentoFacade.atendimentosPorNotificacao(this.notificacao.getId()));

            for (int i = 0; i < this.moradores.size(); i++) {
                if (this.moradores.get(i).getId() == this.notificacao.getMorador().getId()) {
                    this.moradores.remove(i);
                }
            }
            this.moradores.add(0, this.notificacao.getMorador());

            if ((this.notificacao.getArquivos() != null) && (!this.notificacao.getArquivos().isEmpty())) {
                this.upArquivos = new ArrayList<>();
                this.previewArquivos = new ArrayList<>();
                File objFile;
                for (Arquivo a : this.notificacao.getArquivos()) {
                    objFile = new File(SgrUtil.caminhoProjeto() + "AnexoAdvertencia\\" + a.getId() + a.getExtensao());
                    if (objFile.isFile()) {
                        this.upArquivos.add(null);
                        this.previewArquivos.add(Files.readAllBytes(objFile.toPath()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de notificação");
        }
    }

    public String statusNotificacao(Notificacao notifi) {
        if (notifi.getTipo() == 0) {
            return "-";
        } else {
            if (notifi.getBoleto() == null) {
                return "EM ABERTO";
            } else if ((notifi.getBoleto().getFinanceiro() == null)) {
                return "EMITIDO";
            } else {
                return "PAGO";
            }
        }
    }

    public String imprimeCpf(String cpf) {
        return SgrUtil.imprimeCPF(cpf);
    }

    public String verArquivoBase64(byte[] arquivo) {
        return Base64.getEncoder().encodeToString(arquivo);
    }

    public String dataAssinatura(Date dataNotifi) {
        Calendar data = Calendar.getInstance();
        data.setTime(dataNotifi);

        return "CURITIBA, "
                + Integer.toString(data.get(Calendar.DAY_OF_MONTH))
                + " DE " + SgrUtil.nomeMes(data.get(Calendar.MONTH)).toUpperCase()
                + " DE " + Integer.toString(data.get(Calendar.YEAR));
    }

    public String valorMulta() {
        switch (this.notificacao.getInfracao().getClassificacao()) {
            case 0:
                return "R$ 32,00 (TRINTA E DOIS REAIS)";
            case 1:
                return "R$ 63,00 (SESSENTA E TRÊS REAIS)";
            case 2:
                return "R$ 125,00 (CENTO E VINTE E CINCO REAIS)";
            default:
                return "VALOR NÃO INFORMADO";
        }
    }

    public String classificacaoMulta() {
        switch (this.notificacao.getInfracao().getClassificacao()) {
            case 0:
                return "LEVE";
            case 1:
                return "MÉDIA";
            case 2:
                return "GRAVE";
            default:
                return "NÃO INFORMADA";
        }
    }

    public List<Infracao> getInfracoes() {
        return infracoes;
    }

    public void setInfracoes(List<Infracao> infracoes) {
        this.infracoes = infracoes;
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Infracao getInfracao() {
        return infracao;
    }

    public void setInfracao(Infracao infracao) {
        this.infracao = infracao;
    }

    public Notificacao getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }

    public List<Morador> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Morador> moradores) {
        this.moradores = moradores;
    }

    public LazyDataModel<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(LazyDataModel<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public FiltroBD getFiltroNotificacoes() {
        return filtroNotificacoes;
    }

    public void setFiltroNotificacoes(FiltroBD filtroNotificacoes) {
        this.filtroNotificacoes = filtroNotificacoes;
    }

    public int getVerDiv() {
        return verDiv;
    }

    public void setVerDiv(int verDiv) {
        this.verDiv = verDiv;
    }

    public List<UploadedFile> getUpArquivos() {
        return upArquivos;
    }

    public void setUpArquivos(List<UploadedFile> upArquivos) {
        this.upArquivos = upArquivos;
    }

    public List<byte[]> getPreviewArquivos() {
        return previewArquivos;
    }

    public void setPreviewArquivos(List<byte[]> previewArquivos) {
        this.previewArquivos = previewArquivos;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public LazyDataModel<Notificacao> getListaNotificacoes() {
        return listaNotificacoes;
    }

    public void setListaNotificacoes(LazyDataModel<Notificacao> listaNotificacoes) {
        this.listaNotificacoes = listaNotificacoes;
    }
}
