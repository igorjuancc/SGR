package br.com.sgr.mb;

import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.Comentario;
import br.com.sgr.bean.OpcQuestao;
import br.com.sgr.bean.Questao;
import br.com.sgr.bean.Voto;
import br.com.sgr.exception.AssembleiaException;
import br.com.sgr.exception.ComentarioException;
import br.com.sgr.exception.QuestaoException;
import br.com.sgr.facade.AssembleiaFacade;
import br.com.sgr.facade.ComentarioFacade;
import br.com.sgr.facade.QuestaoFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
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
@Named(value = "votoMB")
@ViewScoped
public class VotoMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Questao questao;
    private Comentario comentario;
    private Assembleia assembleia;
    private Voto voto;

    private LazyDataModel<Assembleia> assembleias;
    private FiltroBD filtroAssembleia;

    @PostConstruct
    public void init() {
        try {
            this.filtroAssembleia = new FiltroBD();
            iniciaListaAssembleias();
            this.comentario = new Comentario();
        } catch (Exception e) {
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

    public void verAssembleia(int idAssembleia) {
        try {
            this.assembleia = AssembleiaFacade.assembleiaPorId(idAssembleia);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de assembléia");
        }
    }

    public void verQuestao(Questao questao) {
        try {
            this.voto = null;
            this.questao = QuestaoFacade.questaoPorId(questao.getId());
            for (OpcQuestao o : this.questao.getOpcoes()) {
                for (Voto v : o.getVotos()) {
                    if (v.getMorador().getId() == login.getMorador().getId()) {
                        this.voto = v;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de questão");
        }
    }

    public void executarVoto(OpcQuestao opc) {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            if (this.voto == null) {
                this.voto = new Voto();
                this.voto.setMorador(login.getMorador());
                this.voto.setOpcao(opc);
                QuestaoFacade.novoVoto(this.voto);
            } else {
                this.voto.setOpcao(opc);
                QuestaoFacade.atualizarVoto(this.voto);
            }
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
            for (Questao q : this.assembleia.getQuestoes()) {
                if (q.getId() == this.questao.getId()) {
                    this.questao = q;
                }
            }
            ctx.addMessage(null, SgrUtil.emiteMsg("Voto computado com sucesso", 1));
        } catch (QuestaoException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar voto");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar voto");
        }
    }

    public void novoComentario() {
        try {
            this.comentario.setQuestao(this.questao);
            this.comentario.setTipo(2);
            this.comentario.setPessoa(this.login.getMorador());
            this.comentario.setTipoPessoa("M");
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
            if (comentario.getPessoa().getId() == this.login.getMorador().getId()) {
                if (this.assembleia.getEncerramento() != 2) {
                    ComentarioFacade.apagarComentario(comentario);
                    this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
                    for (Questao q : this.assembleia.getQuestoes()) {
                        if (q.getId() == this.questao.getId()) {
                            this.questao = q;
                        }
                    }
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
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar comentários");
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

    public void novoParecer() {
        FacesContext ctx = null;
        try {
            ctx = FacesContext.getCurrentInstance();
            AssembleiaFacade.atualizarParecerAssembleia(this.assembleia);
            ctx.addMessage(null, SgrUtil.emiteMsg("Parecer atualizado com sucesso", 1));
            this.assembleia = AssembleiaFacade.assembleiaPorId(this.assembleia.getId());
        } catch (AssembleiaException e) {
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 3));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar parecer de assembléia");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao atualizar parecer de assembléia");
        }
    }

    public void redirecionar() {
        try {
            String pagina = null;
            switch (FacesContext.getCurrentInstance().getViewRoot().getViewId()) {
                case "/morador/MoradorAssembleia.xhtml":
                    pagina = "MoradorAssembleia.jsf";
                    break;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao redirecionar página");
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
                labels.add((o.getDescricao().length() > 15) ? o.getDescricao().substring(0,15) + "." : o.getDescricao());
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
                labels.add((o.getDescricao().length() > 15) ? o.getDescricao().substring(0,15) + "." : o.getDescricao());
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

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public LazyDataModel<Assembleia> getAssembleias() {
        return assembleias;
    }

    public void setAssembleias(LazyDataModel<Assembleia> assembleias) {
        this.assembleias = assembleias;
    }

    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public Assembleia getAssembleia() {
        return assembleia;
    }

    public void setAssembleia(Assembleia assembleia) {
        this.assembleia = assembleia;
    }

    public Voto getVoto() {
        return voto;
    }

    public void setVoto(Voto voto) {
        this.voto = voto;
    }
}
