package br.com.sgr.mb;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.Comentario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.OpcQuestao;
import br.com.sgr.bean.Questao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.AssembleiaException;
import br.com.sgr.exception.ComentarioException;
import br.com.sgr.exception.QuestaoException;
import br.com.sgr.facade.AssembleiaFacade;
import br.com.sgr.facade.ComentarioFacade;
import br.com.sgr.facade.MoradorFacade;
import br.com.sgr.facade.QuestaoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;

@ManagedBean
@Named(value = "assembleiaMB")
@ViewScoped
public class AssembleiaMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Boolean cadastroConcluido;
    private Assembleia assembleia;
    private Questao questao;
    private Comentario comentario;
    private List<Morador> moradores;
    private List<Morador> moradoresEleicao;
    private List<Morador> moradoresCandidatos;
    private List<UploadedFile> upImagens;
    private List<byte[]> previewImagens;

    private LazyDataModel<Assembleia> assembleias;
    private FiltroBD filtroAssembleia;

    @PostConstruct
    public void init() {
        try {
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoAssembleia.xhtml":
                    this.moradores = MoradorFacade.listaMoradoresResponsaveis();
                    this.assembleia = new Assembleia();
                    this.assembleia.setTipo(1);
                    this.filtroAssembleia = new FiltroBD();
                    iniciaListaAssembleias();
                    break;
                case "/sindico/SindicoAssembleiaVer.xhtml":
                    if (FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idAssembleia") != null) {
                        int idAssembleia = (int) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("idAssembleia");
                        this.assembleia = AssembleiaFacade.assembleiaPorId(idAssembleia);
                        this.questao = new Questao();
                        this.comentario = new Comentario();
                    } else {
                        ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/sindico/SindicoAssembleia.jsf");
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaAssembleias() {
        assembleias = new LazyDataModel<Assembleia>() {
            private static final long serialVersionUID = 1L;
            List<Assembleia> listaAssembleiaFiltro = null;

            @Override
            public List<Assembleia> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroAssembleia.setPrimeiroRegistro(first);
                filtroAssembleia.setQntdRegistros(Long.valueOf(pageSize));
                filtroAssembleia.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroAssembleia.setPropriedadeOrdenacao(sortField);
                setRowCount(AssembleiaFacade.totalAssembleia());
                listaAssembleiaFiltro = AssembleiaFacade.assembleiaFiltro(filtroAssembleia);
                return listaAssembleiaFiltro;
            }
        };
    }

    public void novaAssembleia() {
        try {
            this.assembleia.setSindico(this.login.getFuncionario());
            AssembleiaFacade.novaAssembleia(this.assembleia);
            this.cadastroConcluido = true;
        } catch (AssembleiaException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova assembléia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar nova assembléia");
        }
    }

    public void apagarAssembleia(Assembleia assembleiaDelete) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            AssembleiaFacade.apagarAssembleia(assembleiaDelete);
            ctx.addMessage(null, SgrUtil.emiteMsg("Assembléia removída com sucesso", 1));
        } catch (AssembleiaException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar assembléia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar assembléia");
        }
    }

    public void salvarQuestao() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            QuestaoFacade.novaQuestao(this.questao, this.upImagens);
            this.cadastroConcluido = true;
            this.upImagens = null;
            this.previewImagens = null;
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar questão");
            }
        } catch (ArquivoException e) {
            e.printStackTrace(System.out);
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
                this.cadastroConcluido = true;
            } else {
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar questão");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar questão");
        }
    }

    public void salvarEleicao() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            Questao questaoEleicao = null;

            for (Questao q : this.assembleia.getQuestoes()) {
                if (q.getTitulo().equals("ELEIÇÃO DE SÍNDICO")) {
                    questaoEleicao = q;
                }
            }
            if (questaoEleicao == null) {
                QuestaoFacade.salvarEleicao(this.assembleia, this.moradoresEleicao);
            } else {
                QuestaoFacade.editarEleicao(this.assembleia, this.moradoresEleicao);
            }
            this.cadastroConcluido = true;
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            this.moradores = MoradorFacade.listaMoradoresResponsaveis();
            this.moradoresEleicao = new ArrayList<>();
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar eleição");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar eleição");
        }
    }

    public void verificaEleicao() {
        try {
            this.moradores = MoradorFacade.listaMoradoresResponsaveis();
            Questao questaoEleicao = null;
            this.moradoresEleicao = new ArrayList<>();
            this.moradoresCandidatos = null;

            for (Questao q : this.assembleia.getQuestoes()) {
                if (q.getTitulo().equals("ELEIÇÃO DE SÍNDICO")) {
                    questaoEleicao = q;
                }
            }

            if (questaoEleicao != null) {
                this.moradoresCandidatos = new ArrayList<>();
                for (OpcQuestao o : questaoEleicao.getOpcoes()) {
                    for (Morador m : this.getMoradores()) {
                        if (o.getDescricao().equals(m.getNome())) {
                            this.moradoresCandidatos.add(m);
                        }
                    }
                }
                this.moradores.removeAll(this.moradoresCandidatos);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de eleição");
        }
    }

    public void apagarCandidato(Morador morador) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            QuestaoFacade.apagarCandidato(this.assembleia, morador);
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            verificaEleicao();
            ctx.addMessage(null, SgrUtil.emiteMsg("Candidato removído", 1));
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar candidato");
            }
        }
    }

    public void verAssembleia(int id) {
        try {
            ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("idAssembleia", id);
            ctxExt.redirect(ctxExt.getRequestContextPath() + "/sindico/SindicoAssembleiaVer.jsf");
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ver detalhes de assembléia");
        }
    }

    public void novaQuestao() {
        try {
            this.cadastroConcluido = false;
            this.questao = new Questao();
            this.upImagens = null;
            this.previewImagens = null;
            this.questao.setAssembleia(this.assembleia);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de nova questão");
        }
    }

    public void novaOpcQuestao() {
        try {
            if ((this.questao.getOpcoes() == null) || (this.questao.getOpcoes().isEmpty())) {
                this.questao.setOpcoes(new ArrayList());
            } else {
                for (OpcQuestao opc : this.questao.getOpcoes()) {
                    opc.setDescricao(opc.getDescricao());
                }
            }
            if (this.questao.getOpcoes().size() >= 5) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage msg = SgrUtil.emiteMsg("Máximo de opções atingido [5]", 2);
                ctx.addMessage(null, msg);
            } else {
                OpcQuestao opc = new OpcQuestao();
                opc.setQuestao(this.questao);
                this.questao.getOpcoes().add(opc);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de nova opção de questão");
        }
    }

    public void removerOpcQuestao(OpcQuestao opcQuestao) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            for (OpcQuestao opc : this.questao.getOpcoes()) {
                opc.setDescricao(opc.getDescricao());
            }
            if (opcQuestao.getId() != 0) {
                QuestaoFacade.apagarOpcQuestao(opcQuestao);
            }
            this.questao.getOpcoes().remove(opcQuestao);
            ctx.addMessage(null, SgrUtil.emiteMsg("Opção deletada com sucesso", 1));
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar opção de questão");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar opção de questão");
        }
    }

    public void apagarImagem(Arquivo imagem) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            for (Arquivo a : this.questao.getArquivos()) {
                a.setDescricao(a.getDescricao());
            }
            if (imagem.getId() != 0) {
                QuestaoFacade.apagarImagem(imagem);
            }
            int indice = this.questao.getArquivos().indexOf(imagem);
            this.previewImagens.remove(indice);
            this.upImagens.remove(indice);
            this.questao.getArquivos().remove(imagem);
            ctx.addMessage(null, SgrUtil.emiteMsg("Imagem deletada com sucesso", 1));
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar imagem de questão");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar imagem de questão");
        }
    }

    public void apagarQuestao(Questao questao) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            QuestaoFacade.apagarQuestao(questao);
            ctx.addMessage(null, SgrUtil.emiteMsg("Questão deletada com sucesso", 1));
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
        } catch (QuestaoException | ArquivoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao apagar questão");
            }
        }
    }

    public void setEdicaoQuestao(Questao questao) {
        try {
            this.questao = questao;
            if ((this.questao.getArquivos() != null) && (!this.questao.getArquivos().isEmpty())) {
                this.upImagens = new ArrayList<>();
                this.previewImagens = new ArrayList<>();
                File objFile;
                for (Arquivo a : this.questao.getArquivos()) {
                    objFile = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemQuestao\\" + a.getId() + a.getExtensao());
                    if (objFile.isFile()) {
                        this.upImagens.add(null);
                        this.previewImagens.add(Files.readAllBytes(objFile.toPath()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de questão");
        }
    }

    public DonutChartModel graficoDonut(Questao questaoGrafico) {
        try {
            DonutChartModel donutModel = new DonutChartModel();
            ChartData data = new ChartData();

            DonutChartDataSet dataSet = new DonutChartDataSet();
            List<Number> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (OpcQuestao o : questaoGrafico.getOpcoes()) {
                values.add(o.getVotos().size());
                labels.add((o.getDescricao().length() > 15) ? o.getDescricao().substring(0, 15) + "." : o.getDescricao());
            }
            dataSet.setData(values);
            data.setLabels(labels);

            List<String> bgColors = new ArrayList<>();
            bgColors.add("rgb(255, 99, 132)");
            bgColors.add("rgb(54, 162, 235)");
            bgColors.add("rgb(255, 205, 86)");
            bgColors.add("rgb(0, 255, 0)");
            bgColors.add("rgb(75, 0, 130)");
            dataSet.setBackgroundColor(bgColors);

            data.addChartDataSet(dataSet);
            donutModel.setData(data);
            return donutModel;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
            return null;
        }
    }

    public BarChartModel graficoBar(Questao questaoGrafico) {
        try {
            BarChartModel barModel = new BarChartModel();
            ChartData data = new ChartData();

            BarChartDataSet dataSet = new BarChartDataSet();
            dataSet.setLabel(questaoGrafico.getTitulo());
            List<Number> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (OpcQuestao o : questaoGrafico.getOpcoes()) {
                values.add(o.getVotos().size());
                labels.add((o.getDescricao().length() > 15) ? o.getDescricao().substring(0, 15) + "." : o.getDescricao());
            }
            dataSet.setData(values);
            data.setLabels(labels);

            List<String> bgColors = new ArrayList<>();
            bgColors.add("rgb(255, 99, 132, 0.5)");
            bgColors.add("rgb(54, 162, 235, 0.5)");
            bgColors.add("rgb(255, 205, 86, 0.5)");
            bgColors.add("rgb(0, 255, 0, 0.5)");
            bgColors.add("rgb(75, 0, 130, 0.5)");
            dataSet.setBackgroundColor(bgColors);

            List<String> borderColors = new ArrayList<>();
            borderColors.add("rgb(255, 99, 132)");
            borderColors.add("rgb(54, 162, 235)");
            borderColors.add("rgb(255, 205, 86)");
            borderColors.add("rgb(0, 255, 0)");
            borderColors.add("rgb(75, 0, 130)");
            dataSet.setBorderColor(borderColors);
            dataSet.setBorderWidth(1);

            data.addChartDataSet(dataSet);
            barModel.setData(data);

            //Options
            BarChartOptions options = new BarChartOptions();
            CartesianScales cScales = new CartesianScales();
            CartesianLinearAxes linearAxes = new CartesianLinearAxes();
            CartesianLinearTicks ticks = new CartesianLinearTicks();
            ticks.setBeginAtZero(true);
            linearAxes.setTicks(ticks);
            cScales.addYAxesData(linearAxes);
            options.setScales(cScales);

            Legend legend = new Legend();
            legend.setDisplay(true);
            legend.setPosition("top");
            LegendLabel legendLabels = new LegendLabel();
            legendLabels.setFontStyle("bold");
            legendLabels.setFontColor("#2980B9");
            legendLabels.setFontSize(24);
            legend.setLabels(legendLabels);
            options.setLegend(legend);

            barModel.setOptions(options);

            return barModel;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
            return null;
        }
    }

    public String percentualVoto(OpcQuestao opc) {
        try {
            double total = 0;
            DecimalFormat df = new DecimalFormat("#0.00");
            for (OpcQuestao o : this.questao.getOpcoes()) {
                total = total + o.getVotos().size();
            }
            if (total > 0) {
                total = (opc.getVotos().size() / total) * 100;
            }
            return df.format(total);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar percentual de votos");
            return null;
        }
    }

    public int totalVotos(Questao questaoVotos) {
        try {
            int total = 0;
            if (questaoVotos.getOpcoes() != null) {
                for (OpcQuestao opc : questaoVotos.getOpcoes()) {
                    total = total + opc.getVotos().size();
                }
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar total de votos");
            return 0;
        }
    }

    public void novoComentario() {
        try {
            this.comentario.setQuestao(this.questao);
            this.comentario.setTipo(2);
            this.comentario.setPessoa(this.login.getFuncionario());
            if (this.login.getFuncionario().getFuncao().getId() == 1) {
                this.comentario.setTipoPessoa("S");
            }
            ComentarioFacade.novoComentario(this.comentario);
            this.comentario.setDescricao("");
            this.comentario.setId(0);
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            for (Questao q : this.assembleia.getQuestoes()) {
                if (q.getId() == this.questao.getId()) {
                    this.questao = q;
                }
            }
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

    public void apagarComentario(Comentario comentario) {
        try {
            ComentarioFacade.apagarComentario(comentario);
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            for (Questao q : this.assembleia.getQuestoes()) {
                if (q.getId() == this.questao.getId()) {
                    this.questao = q;
                }
            }
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

    public void pollComentarios() {
        try {
            if (this.assembleia != null) {
                this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
                for (Questao q : this.assembleia.getQuestoes()) {
                    if (q.getId() == this.questao.getId()) {
                        this.questao = q;
                    }
                }
                PrimeFaces.current().ajax().update("verForm:dataViewComentarios");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar comentários");
        }
    }

    public String statusAssembleia() {
        try {
            String rtn = null;

            if (this.assembleia != null) {
                if ((this.assembleia.getEncerramento() == 0) && (this.assembleia.getParecer() == null)) {
                    rtn = "Finalizar";
                } else if ((this.assembleia.getEncerramento() == 1) && (this.assembleia.getParecer() == null)) {
                    rtn = "Aguardando";
                } else if ((this.assembleia.getEncerramento() == 1) && (this.assembleia.getParecer() != null)) {
                    rtn = "Encerrar";
                } else if ((this.assembleia.getEncerramento() == 2) && (this.assembleia.getParecer() != null)) {
                    rtn = "Finalizado";
                }
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de assembléia");
            return null;
        }
    }

    public void redirecionar() {
        try {
            String pagina = null;
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/sindico/SindicoAssembleia.xhtml":
                    pagina = "SindicoAssembleia.jsf";
                    break;
                case "/sindico/SindicoAssembleiaVer.xhtml":
                    pagina = "SindicoAssembleia.jsf";
                    break;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar");
        }
    }

    public void atualizarStatusAssembleia() {
        try {
            FacesMessage msg;
            FacesContext ctx = FacesContext.getCurrentInstance();
            String mensagem = AssembleiaFacade.atualizarStatusAssembleia(this.assembleia);
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            msg = SgrUtil.emiteMsg(mensagem, 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar status de assembléia");
        }
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public Assembleia getAssembleia() {
        return assembleia;
    }

    public void setAssembleia(Assembleia assembleia) {
        this.assembleia = assembleia;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }

    public List<Morador> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Morador> moradores) {
        this.moradores = moradores;
    }

    public Boolean getCadastroConcluido() {
        return cadastroConcluido;
    }

    public void setCadastroConcluido(Boolean cadastroConcluido) {
        this.cadastroConcluido = cadastroConcluido;
    }

    public LazyDataModel<Assembleia> getAssembleias() {
        return assembleias;
    }

    public void setAssembleias(LazyDataModel<Assembleia> assembleias) {
        this.assembleias = assembleias;
    }

    public List<UploadedFile> getUpImagens() {
        return upImagens;
    }

    public void setUpImagens(List<UploadedFile> upImagens) {
        this.upImagens = upImagens;
    }

    public void uploadImagem(FileUploadEvent event) {
        try {
            if (this.upImagens == null) {
                this.upImagens = new ArrayList<>();
            }
            if (this.previewImagens == null) {
                this.previewImagens = new ArrayList<>();
            }
            if (this.questao.getArquivos() == null) {
                this.questao.setArquivos(new ArrayList<>());
            }

            if (this.questao.getArquivos().size() >= 5) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage msg = SgrUtil.emiteMsg("Máximo de imagens atingido [5]", 2);
                ctx.addMessage(null, msg);
            } else {
                this.upImagens.add(event.getFile());
                this.previewImagens.add(event.getFile().getContents());

                Arquivo arq = new Arquivo();
                arq.setQuestao(this.questao);
                arq.setExtensao(event.getFile().getFileName().toUpperCase());
                arq.setExtensao(arq.getExtensao().substring(arq.getExtensao().lastIndexOf("."),
                        arq.getExtensao().length()));
                this.questao.getArquivos().add(arq);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao realizar upload de imagem");
        }
    }

    public List<byte[]> getPreviewImagens() {
        return previewImagens;
    }

    public void setPreviewImagens(List<byte[]> previewImagens) {
        this.previewImagens = previewImagens;
    }

    public String verImagemBase64(byte[] imagem) {
        return Base64.getEncoder().encodeToString(imagem);
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public List<Morador> getMoradoresEleicao() {
        return moradoresEleicao;
    }

    public void setMoradoresEleicao(List<Morador> moradoresEleicao) {
        this.moradoresEleicao = moradoresEleicao;
    }

    public List<Morador> getMoradoresCandidatos() {
        return moradoresCandidatos;
    }

    public void setMoradoresCandidatos(List<Morador> moradoresCandidatos) {
        this.moradoresCandidatos = moradoresCandidatos;
    }
}
