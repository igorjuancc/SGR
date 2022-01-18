package br.com.sgr.mb;

import br.com.sgr.facade.BalancoFacade;
import br.com.sgr.facade.FinanceiroFacade;
import br.com.sgr.facade.LogBDCadastroMoradorFacade;
import br.com.sgr.util.SgrUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

@ManagedBean
@Named(value = "balancoMB")
@ViewScoped
public class BalancoMB implements Serializable {

    private int anoFiltro;
    private int anoFiltro2;
    private int mesFiltro;
    private List<Integer> anoLista;
    private List<Integer> mesLista;
    private List<Object[]> listaTotalReceita;
    private List<Object[]> listaTotalDespesa;
    private List<Object[]> listaTotalPrevisto;
    private PieChartModel graficoTotalReceita;
    private PieChartModel graficoTotalDespesa;
    private PieChartModel graficoTotalAnoReceita;
    private PieChartModel graficoTotalAnoDespesa;
    private PieChartModel graficoTotalAnoMesReceita;
    private PieChartModel graficoTotalAnoMesDespesa;
    private BarChartModel graficoTotal;
    private BarChartModel graficoTotalFiltroAno;
    private BarChartModel graficoMesFiltroAno;
    private BarChartModel graficoTotalFiltroAnoMes;
    private LineChartModel graficoTotalAno;
    private HorizontalBarChartModel graficoTotalPrevistoFiltroAnoMes;

    @PostConstruct
    public void init() {
        try {
            this.anoLista = BalancoFacade.listaAnoBalanco();
            if (!this.anoLista.isEmpty()) {
                this.anoFiltro = this.anoLista.get(0);
                this.anoFiltro2 = this.anoLista.get(0);
                this.mesLista = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
                this.mesFiltro = this.mesLista.get(0);
                this.listaTotalReceita = FinanceiroFacade.totalFinanceiro(0);
                this.listaTotalDespesa = FinanceiroFacade.totalFinanceiro(1);
                iniciaGrafGeral();
                iniciaGrafAnual();
                iniciaGrafMensal(0);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    //Grafico total [Geral]
    public void graficoTotal() {
        try {
            ChartData data = new ChartData();
            this.graficoTotal = new BarChartModel();
            BarChartDataSet dataSet = new BarChartDataSet();
            List<Number> values = Arrays.asList(0, 0, 0);
            List<String> labels = Arrays.asList("Receita", "Despesa", "Saldo");
            BigDecimal valor;

            if (this.listaTotalReceita.size() > 1) {
                for (int i = this.listaTotalReceita.size(); i > 0; i--) {
                    if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[0].toString().equals("TOTAL"))) {
                        valor = (BigDecimal) this.listaTotalReceita.get(i - 1)[1];
                        values.set(0, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }
            if (this.listaTotalDespesa.size() > 1) {
                for (int i = this.listaTotalDespesa.size(); i > 0; i--) {
                    if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[0].toString().equals("TOTAL"))) {
                        valor = (BigDecimal) this.listaTotalDespesa.get(i - 1)[1];
                        values.set(1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }

            valor = new BigDecimal((values.get(0).doubleValue() - values.get(1).doubleValue()), MathContext.DECIMAL64);
            values.set(2, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            dataSet.setData(values);
            data.setLabels(labels);
            dataSet.setLabel("Saldo");

            List<String> bgColors = new ArrayList<>();
            bgColors.add("rgb(54, 162, 235, 0.5)");
            bgColors.add("rgb(255, 205, 86, 0.5)");

            dataSet.setBackgroundColor(bgColors);

            List<String> borderColors = new ArrayList<>();
            borderColors.add("rgb(54, 162, 235)");
            borderColors.add("rgb(255, 205, 86)");

            if (values.get(2).doubleValue() < 0) {
                bgColors.add("rgb(255, 99, 132, 0.5)");
                borderColors.add("rgb(255, 99, 132)");
            } else {
                bgColors.add("rgb(0, 255, 127, 0.5)");
                borderColors.add("rgb(0, 255, 127)");
            }

            dataSet.setBorderColor(borderColors);
            dataSet.setBorderWidth(1);

            data.addChartDataSet(dataSet);

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
            legendLabels.setFontSize(15);
            legend.setLabels(legendLabels);
            options.setLegend(legend);

            if (values.size() > 0) {
                this.graficoTotal.setOptions(options);
                this.graficoTotal.setData(data);
            } else {
                this.graficoTotal = null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total ano a ano [Geral]
    public void graficoTotalAno() {
        try {
            LineChartDataSet dataSet3 = new LineChartDataSet();
            LineChartDataSet dataSet2 = new LineChartDataSet();
            LineChartDataSet dataSet = new LineChartDataSet();
            this.graficoTotalAno = new LineChartModel();
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            List<Number> values2 = new ArrayList<>();
            List<Number> values3 = new ArrayList<>();
            ChartData data = new ChartData();
            String dado;
            BigDecimal valor;
            int j;

            for (int i = this.anoLista.size(); i > 0; i--) {
                labels.add(Integer.toString(this.anoLista.get(i - 1)));
                dado = this.anoLista.get(i - 1).toString();
                valor = BigDecimal.ZERO;

                for (j = this.listaTotalReceita.size(); j > 0; j--) {
                    if (dado.equals(this.listaTotalReceita.get(j - 1)[0].toString().trim())) {
                        valor = (BigDecimal) this.listaTotalReceita.get(j - 1)[1];
                    }
                }
                values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                valor = BigDecimal.ZERO;

                for (j = this.listaTotalDespesa.size(); j > 0; j--) {
                    if (dado.equals(this.listaTotalDespesa.get(j - 1)[0].toString().trim())) {
                        valor = (BigDecimal) this.listaTotalDespesa.get(j - 1)[1];
                    }
                }
                values2.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                j = values.size() - 1;
                valor = new BigDecimal((values.get(j).doubleValue() - values2.get(j).doubleValue()), MathContext.DECIMAL64);
                values3.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            }

            dataSet.setData(values);
            dataSet.setFill(false);
            dataSet.setLabel("Receitas");
            dataSet.setBorderColor("rgb(54, 162, 235)");
            dataSet.setLineTension(0.1);

            dataSet2.setData(values2);
            dataSet2.setFill(false);
            dataSet2.setLabel("Despesas");
            dataSet2.setBorderColor("rgb(255, 205, 86)");
            dataSet2.setLineTension(0.1);

            dataSet3.setData(values3);
            dataSet3.setFill(false);
            dataSet3.setLabel("Saldo");
            dataSet3.setBorderColor("rgb(0, 250, 154)");
            dataSet3.setLineTension(0.1);

            data.addChartDataSet(dataSet);
            data.addChartDataSet(dataSet2);
            data.addChartDataSet(dataSet3);
            data.setLabels(labels);

            if (values.size() > 0) {
                this.graficoTotalAno.setData(data);
            } else {
                this.graficoTotalAno = null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total por categoria [Geral]
    public void graficoTotalCategoria(int tipo, String seq) {
        try {
            PieChartDataSet dataSet = new PieChartDataSet();
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            ChartData data = new ChartData();
            BigDecimal valor;
            int j;
            String label;

            if (tipo == 1) {
                for (j = this.listaTotalReceita.size(); j > 0; j--) {
                    label = this.listaTotalReceita.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("TOTAL"))) {
                        labels.add(label);
                        valor = (BigDecimal) this.listaTotalReceita.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalReceita.size();
            } else {
                for (j = this.listaTotalDespesa.size(); j > 0; j--) {
                    label = this.listaTotalDespesa.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("TOTAL"))) {
                        labels.add(this.listaTotalDespesa.get(j - 1)[0].toString());
                        valor = (BigDecimal) this.listaTotalDespesa.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalDespesa.size();
            }

            dataSet.setData(values);
            dataSet.setBackgroundColor(SgrUtil.listaCores(j, seq));
            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            if (tipo == 1) {
                if (values.size() > 0) {
                    this.graficoTotalReceita = new PieChartModel();
                    this.graficoTotalReceita.setData(data);
                } else {
                    this.graficoTotalReceita = null;
                }
            } else {
                if (values.size() > 0) {
                    this.graficoTotalDespesa = new PieChartModel();
                    this.graficoTotalDespesa.setData(data);
                } else {
                    this.graficoTotalDespesa = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total filtro ano [Anual]
    public void graficoTotalFiltroAno() {
        try {
            BigDecimal valor = new BigDecimal("0");
            ChartData data = new ChartData();
            this.graficoTotalFiltroAno = new BarChartModel();
            BarChartDataSet dataSet = new BarChartDataSet();
            BarChartDataSet dataSet2 = new BarChartDataSet();
            List<Number> values = Arrays.asList(0, 0, 0);
            List<Number> values2 = Arrays.asList(0, 0, 0);
            List<String> labels = Arrays.asList("Receita", "Despesa", "Saldo");

            this.listaTotalReceita = FinanceiroFacade.totalFinanceiroAno(0, this.anoFiltro);
            this.listaTotalDespesa = FinanceiroFacade.totalFinanceiroAno(1, this.anoFiltro);

            if (this.listaTotalReceita.size() > 1) {
                for (int i = this.listaTotalReceita.size(); i > 0; i--) {
                    if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[0].toString().equals("T1"))) {
                        if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[1] != null)) {
                            valor = (BigDecimal) this.listaTotalReceita.get(i - 1)[1];
                        }
                        values.set(0, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                    valor = (valor.doubleValue() > 0) ? new BigDecimal("0") : valor;
                    if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[0].toString().equals("T2"))) {
                        if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[1] != null)) {
                            valor = (BigDecimal) this.listaTotalReceita.get(i - 1)[1];
                        }
                        values2.set(0, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }

            if (this.listaTotalDespesa.size() > 1) {
                for (int i = this.listaTotalDespesa.size(); i > 0; i--) {
                    if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[0].toString().equals("T1"))) {
                        if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[1] != null)) {
                            valor = (BigDecimal) this.listaTotalDespesa.get(i - 1)[1];
                        }
                        values.set(1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                    valor = (valor.doubleValue() > 0) ? new BigDecimal("0") : valor;
                    if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[0].toString().equals("T2"))) {
                        if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[1] != null)) {
                            valor = (BigDecimal) this.listaTotalDespesa.get(i - 1)[1];
                        }
                        values2.set(1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }

            //Dados 1
            List<String> bgColors = new ArrayList<>();
            List<String> borderColors = new ArrayList<>();
            valor = new BigDecimal((values.get(0).doubleValue() - values.get(1).doubleValue()), MathContext.DECIMAL64);
            values.set(2, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            bgColors.add("rgb(0, 0, 139, 0.5)");
            bgColors.add("rgb(255, 215, 0, 0.5)");
            borderColors.add("rgb(0, 0, 139)");
            borderColors.add("rgb(255, 215, 0)");
            if (values.get(2).doubleValue() < 0) {
                bgColors.add("rgb(139, 0, 0, 0.5)");
                borderColors.add("rgb(139, 0, 0)");
            } else {
                bgColors.add("rgb(0, 100, 0, 0.5)");
                borderColors.add("rgb(0, 100, 0)");
            }
            dataSet.setData(values);
            dataSet.setLabel("Até " + Integer.toString(this.anoFiltro));
            dataSet.setBackgroundColor(bgColors);
            dataSet.setBorderColor(borderColors);
            dataSet.setBorderWidth(1);

            //Dados 2             
            List<String> bgColors2 = new ArrayList<>();
            List<String> borderColors2 = new ArrayList<>();
            valor = new BigDecimal((values2.get(0).doubleValue() - values2.get(1).doubleValue()), MathContext.DECIMAL64);
            values2.set(2, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            bgColors2.add("rgb(54, 162, 235, 0.5)");
            bgColors2.add("rgb(255, 205, 86, 0.5)");
            borderColors2.add("rgb(54, 162, 235)");
            borderColors2.add("rgb(255, 205, 86)");
            if (values2.get(2).doubleValue() < 0) {
                bgColors2.add("rgb(255, 99, 132, 0.5)");
                borderColors2.add("rgb(255, 99, 132)");
            } else {
                bgColors2.add("rgb(0, 255, 127, 0.5)");
                borderColors2.add("rgb(0, 255, 127)");
            }
            dataSet2.setData(values2);
            dataSet2.setLabel(Integer.toString(this.anoFiltro));
            dataSet2.setBackgroundColor(bgColors2);
            dataSet2.setBorderColor(borderColors2);
            dataSet2.setBorderWidth(1);

            data.setLabels(labels);
            data.addChartDataSet(dataSet);
            data.addChartDataSet(dataSet2);

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
            legendLabels.setFontSize(15);
            legend.setLabels(legendLabels);
            options.setLegend(legend);

            if (values.size() > 0) {
                this.graficoTotalFiltroAno.setData(data);
                this.graficoTotalFiltroAno.setOptions(options);
            } else {
                this.graficoTotalFiltroAno = null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico mensal filtro ano [Anual]
    public void graficoMesFiltroAno() {
        try {
            BigDecimal valor;
            ChartData data = new ChartData();
            this.graficoMesFiltroAno = new BarChartModel();
            BarChartDataSet dataSet = new BarChartDataSet();
            BarChartDataSet dataSet2 = new BarChartDataSet();
            List<Number> values = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            List<Number> values2 = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            List<String> labels = Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez");
            String mes;
            Double posicao;

            if (this.listaTotalReceita.size() > 1) {
                for (int i = this.listaTotalReceita.size(); i > 0; i--) {
                    if (this.listaTotalReceita.get(i - 1)[0] != null) {
                        mes = this.listaTotalReceita.get(i - 1)[0].toString().trim();
                        if ((SgrUtil.isNumeric(mes)) && ((Double.parseDouble(mes) > 0) && (Double.parseDouble(mes) <= 12))) {
                            valor = (BigDecimal) this.listaTotalReceita.get(i - 1)[1];
                            posicao = Double.parseDouble(mes);
                            values.set(posicao.intValue() - 1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                        }
                    }
                }
            }

            if (this.listaTotalDespesa.size() > 1) {
                for (int i = this.listaTotalDespesa.size(); i > 0; i--) {
                    if (this.listaTotalDespesa.get(i - 1)[0] != null) {
                        mes = this.listaTotalDespesa.get(i - 1)[0].toString().trim();
                        if ((SgrUtil.isNumeric(mes)) && ((Double.parseDouble(mes) > 0) && (Double.parseDouble(mes) <= 12))) {
                            valor = (BigDecimal) this.listaTotalDespesa.get(i - 1)[1];
                            posicao = Double.parseDouble(mes);
                            values2.set(posicao.intValue() - 1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                        }
                    }
                }
            }
            //Dados 1
            dataSet.setData(values);
            dataSet.setLabel("Receita");
            dataSet.setBackgroundColor("rgb(54, 162, 235, 0.5)");
            dataSet.setBorderColor("rgb(54, 162, 235)");
            dataSet.setBorderWidth(1);

            //Dados 2             
            dataSet2.setData(values2);
            dataSet2.setLabel("Despesa");
            dataSet2.setBackgroundColor("rgb(255, 205, 86, 0.5)");
            dataSet2.setBorderColor("rgb(255, 205, 86)");
            dataSet2.setBorderWidth(1);

            data.setLabels(labels);
            data.addChartDataSet(dataSet);
            data.addChartDataSet(dataSet2);

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
            legendLabels.setFontSize(15);
            legend.setLabels(legendLabels);
            options.setLegend(legend);

            if (values.size() > 0) {
                this.graficoMesFiltroAno.setData(data);
                this.graficoMesFiltroAno.setOptions(options);
            } else {
                this.graficoMesFiltroAno = null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total por categoria filtro ano [Anual]
    public void graficoTotalAnoCategoria(int tipo, String seq) {
        try {
            PieChartDataSet dataSet = new PieChartDataSet();
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            ChartData data = new ChartData();
            BigDecimal valor;
            int j;
            String label;

            if (tipo == 1) {
                for (j = this.listaTotalReceita.size(); j > 0; j--) {
                    label = this.listaTotalReceita.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("T1")) && (!label.equals("T2"))) {
                        labels.add(label);
                        valor = (BigDecimal) this.listaTotalReceita.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalReceita.size();
            } else {
                for (j = this.listaTotalDespesa.size(); j > 0; j--) {
                    label = this.listaTotalDespesa.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("T1") && (!label.equals("T2")))) {
                        labels.add(this.listaTotalDespesa.get(j - 1)[0].toString());
                        valor = (BigDecimal) this.listaTotalDespesa.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalDespesa.size();
            }

            dataSet.setData(values);
            dataSet.setBackgroundColor(SgrUtil.listaCores(j, seq));
            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            if (tipo == 1) {
                if (values.size() > 0) {
                    this.graficoTotalAnoReceita = new PieChartModel();
                    this.graficoTotalAnoReceita.setData(data);
                } else {
                    this.graficoTotalAnoReceita = null;
                }
            } else {
                if (values.size() > 0) {
                    this.graficoTotalAnoDespesa = new PieChartModel();
                    this.graficoTotalAnoDespesa.setData(data);
                } else {
                    this.graficoTotalAnoDespesa = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total filtro ano e mes [Mensal]
    public void graficoTotalFiltroAnoMes() {
        try {
            ChartData data = new ChartData();
            this.graficoTotalFiltroAnoMes = new BarChartModel();
            BarChartDataSet dataSet = new BarChartDataSet();
            List<Number> values = Arrays.asList(0, 0, 0);
            List<String> labels = Arrays.asList("Receita", "Despesa", "Saldo");
            BigDecimal valor;

            this.listaTotalReceita = FinanceiroFacade.totalFinanceiroAnoMes(0, this.anoFiltro2, this.mesFiltro);
            this.listaTotalDespesa = FinanceiroFacade.totalFinanceiroAnoMes(1, this.anoFiltro2, this.mesFiltro);

            if (this.listaTotalReceita.size() > 1) {
                for (int i = this.listaTotalReceita.size(); i > 0; i--) {
                    if ((this.listaTotalReceita.get(i - 1)[0] != null) && (this.listaTotalReceita.get(i - 1)[0].toString().equals("TOTAL"))) {
                        valor = (BigDecimal) this.listaTotalReceita.get(i - 1)[1];
                        values.set(0, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }
            if (this.listaTotalDespesa.size() > 1) {
                for (int i = this.listaTotalDespesa.size(); i > 0; i--) {
                    if ((this.listaTotalDespesa.get(i - 1)[0] != null) && (this.listaTotalDespesa.get(i - 1)[0].toString().equals("TOTAL"))) {
                        valor = (BigDecimal) this.listaTotalDespesa.get(i - 1)[1];
                        values.set(1, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }

            valor = new BigDecimal((values.get(0).doubleValue() - values.get(1).doubleValue()), MathContext.DECIMAL64);
            values.set(2, valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            dataSet.setData(values);
            data.setLabels(labels);
            dataSet.setLabel("Saldo");

            List<String> bgColors = new ArrayList<>();
            bgColors.add("rgb(54, 162, 235, 0.5)");
            bgColors.add("rgb(255, 205, 86, 0.5)");

            dataSet.setBackgroundColor(bgColors);

            List<String> borderColors = new ArrayList<>();
            borderColors.add("rgb(54, 162, 235)");
            borderColors.add("rgb(255, 205, 86)");

            if (values.get(2).doubleValue() < 0) {
                bgColors.add("rgb(255, 99, 132, 0.5)");
                borderColors.add("rgb(255, 99, 132)");
            } else {
                bgColors.add("rgb(0, 255, 127, 0.5)");
                borderColors.add("rgb(0, 255, 127)");
            }

            dataSet.setBorderColor(borderColors);
            dataSet.setBorderWidth(1);

            data.addChartDataSet(dataSet);

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
            legendLabels.setFontSize(15);
            legend.setLabels(legendLabels);
            options.setLegend(legend);

            if (values.size() > 0) {
                this.graficoTotalFiltroAnoMes.setOptions(options);
                this.graficoTotalFiltroAnoMes.setData(data);
            } else {
                this.graficoTotalFiltroAnoMes = null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total por categoria filtro ano e mes [Mensal]
    public void graficoTotalAnoMesCategoria(int tipo, String seq) {
        try {
            PieChartDataSet dataSet = new PieChartDataSet();
            List<String> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            ChartData data = new ChartData();
            BigDecimal valor;
            int j;
            String label;

            if (tipo == 1) {
                for (j = this.listaTotalReceita.size(); j > 0; j--) {
                    label = this.listaTotalReceita.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("TOTAL"))) {
                        labels.add(label);
                        valor = (BigDecimal) this.listaTotalReceita.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalReceita.size();
            } else {
                for (j = this.listaTotalDespesa.size(); j > 0; j--) {
                    label = this.listaTotalDespesa.get(j - 1)[0].toString();
                    if ((label != null) && (!SgrUtil.isNumeric(label)) && (!label.equals("TOTAL"))) {
                        labels.add(this.listaTotalDespesa.get(j - 1)[0].toString());
                        valor = (BigDecimal) this.listaTotalDespesa.get(j - 1)[1];
                        values.add(valor.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
                j = this.listaTotalDespesa.size();
            }

            dataSet.setData(values);
            dataSet.setBackgroundColor(SgrUtil.listaCores(j, seq));
            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            if (tipo == 1) {
                if (values.size() > 0) {
                    this.graficoTotalAnoMesReceita = new PieChartModel();
                    this.graficoTotalAnoMesReceita.setData(data);
                } else {
                    this.graficoTotalAnoMesReceita = null;
                }
            } else {
                if (values.size() > 0) {
                    this.graficoTotalAnoMesDespesa = new PieChartModel();
                    this.graficoTotalAnoMesDespesa.setData(data);
                } else {
                    this.graficoTotalAnoMesDespesa = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    //Grafico total previsto filtro ano e mes [Mensal]
    public void graficoTotalPrevistoFiltroAnoMes() {
        try {
            ChartData data = new ChartData();
            this.graficoTotalPrevistoFiltroAnoMes = new HorizontalBarChartModel();
            HorizontalBarChartDataSet dataSet = new HorizontalBarChartDataSet();
            List<Number> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            Double valor;
            Double total = 0.0;
            int j;
            String label;

            this.listaTotalPrevisto = LogBDCadastroMoradorFacade.previsaoOrcamentoMes(this.anoFiltro2, this.mesFiltro);

            for (j = this.listaTotalPrevisto.size(); j > 0; j--) {
                label = (this.listaTotalPrevisto.get(j - 1)[0] != null) ? this.listaTotalPrevisto.get(j - 1)[0].toString() : "";
                valor = (this.listaTotalPrevisto.get(j - 1)[1] != null) ? (Double) this.listaTotalPrevisto.get(j - 1)[1] : 0;
                if (!(label.equals("") || valor <= 0)) {
                    labels.add(label);
                    values.add(valor);
                }
                total = total + valor;
            }
            labels.add(0, "TOTAL");
            values.add(0, total);
            dataSet.setLabel("Receita Prevista");
            dataSet.setData(values);
            data.setLabels(labels);

            List<String> bgColors = SgrUtil.listaCores(labels.size(), "A");
            SgrUtil.coresRgb(bgColors);
            List<String> borderColors = SgrUtil.listaCores(labels.size(), "A");

            dataSet.setBackgroundColor(bgColors);
            dataSet.setBorderColor(borderColors);
            dataSet.setBorderWidth(1);

            data.addChartDataSet(dataSet);

            //Options
            BarChartOptions options = new BarChartOptions();
            CartesianScales cScales = new CartesianScales();
            CartesianLinearAxes linearAxes = new CartesianLinearAxes();
            linearAxes.setOffset(true);
            CartesianLinearTicks ticks = new CartesianLinearTicks();
            ticks.setBeginAtZero(true);
            linearAxes.setTicks(ticks);
            cScales.addXAxesData(linearAxes);
            options.setScales(cScales);

            if (values.size() > 0) {
                this.graficoTotalPrevistoFiltroAnoMes.setData(data);
                this.graficoTotalPrevistoFiltroAnoMes.setOptions(options);
            } else {
                this.graficoTotalPrevistoFiltroAnoMes = null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    public void iniciaGrafGeral() {
        try {
            graficoTotal();
            graficoTotalAno();
            graficoTotalCategoria(1, "A");
            graficoTotalCategoria(2, "D");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    public void iniciaGrafAnual() {
        try {
            graficoTotalFiltroAno();
            graficoMesFiltroAno();
            graficoTotalAnoCategoria(1, "A");
            graficoTotalAnoCategoria(2, "D");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    public void iniciaGrafMensal(int iniciaFiltro) {
        try {
            if (iniciaFiltro == 0) {
                filtroAno();
            }
            graficoTotalFiltroAnoMes();
            graficoTotalAnoMesCategoria(1, "A");
            graficoTotalAnoMesCategoria(2, "D");
            graficoTotalPrevistoFiltroAnoMes();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar gráfico");
        }
    }

    public void filtroAno() {
        this.mesFiltro = (!this.mesLista.isEmpty()) ? this.mesLista.get(0) : 0;
    }

    public String nomeMesAbrv(int mes) {
        return SgrUtil.nomeMesAbrv(mes);
    }

    public String nomeMes(int mes) {
        return (mes > 0) ? SgrUtil.nomeMes(mes - 1) : "";
    }

    public int getAnoFiltro() {
        return anoFiltro;
    }

    public void setAnoFiltro(int anoFiltro) {
        this.anoFiltro = anoFiltro;
    }

    public int getAnoFiltro2() {
        return anoFiltro2;
    }

    public void setAnoFiltro2(int anoFiltro2) {
        this.anoFiltro2 = anoFiltro2;
    }

    public int getMesFiltro() {
        return mesFiltro;
    }

    public void setMesFiltro(int mesFiltro) {
        this.mesFiltro = mesFiltro;
    }

    public List<Integer> getAnoLista() {
        return anoLista;
    }

    public void setAnoLista(List<Integer> anoLista) {
        this.anoLista = anoLista;
    }

    public List<Integer> getMesLista() {
        return mesLista;
    }

    public void setMesLista(List<Integer> mesLista) {
        this.mesLista = mesLista;
    }

    public PieChartModel getGraficoTotalReceita() {
        return graficoTotalReceita;
    }

    public void setGraficoTotalReceita(PieChartModel graficoTotalReceita) {
        this.graficoTotalReceita = graficoTotalReceita;
    }

    public PieChartModel getGraficoTotalDespesa() {
        return graficoTotalDespesa;
    }

    public void setGraficoTotalDespesa(PieChartModel graficoTotalDespesa) {
        this.graficoTotalDespesa = graficoTotalDespesa;
    }

    public PieChartModel getGraficoTotalAnoReceita() {
        return graficoTotalAnoReceita;
    }

    public void setGraficoTotalAnoReceita(PieChartModel graficoTotalAnoReceita) {
        this.graficoTotalAnoReceita = graficoTotalAnoReceita;
    }

    public PieChartModel getGraficoTotalAnoDespesa() {
        return graficoTotalAnoDespesa;
    }

    public void setGraficoTotalAnoDespesa(PieChartModel graficoTotalAnoDespesa) {
        this.graficoTotalAnoDespesa = graficoTotalAnoDespesa;
    }

    public PieChartModel getGraficoTotalAnoMesReceita() {
        return graficoTotalAnoMesReceita;
    }

    public void setGraficoTotalAnoMesReceita(PieChartModel graficoTotalAnoMesReceita) {
        this.graficoTotalAnoMesReceita = graficoTotalAnoMesReceita;
    }

    public PieChartModel getGraficoTotalAnoMesDespesa() {
        return graficoTotalAnoMesDespesa;
    }

    public void setGraficoTotalAnoMesDespesa(PieChartModel graficoTotalAnoMesDespesa) {
        this.graficoTotalAnoMesDespesa = graficoTotalAnoMesDespesa;
    }

    public BarChartModel getGraficoTotal() {
        return graficoTotal;
    }

    public void setGraficoTotal(BarChartModel graficoTotal) {
        this.graficoTotal = graficoTotal;
    }

    public BarChartModel getGraficoTotalFiltroAno() {
        return graficoTotalFiltroAno;
    }

    public void setGraficoTotalFiltroAno(BarChartModel graficoTotalFiltroAno) {
        this.graficoTotalFiltroAno = graficoTotalFiltroAno;
    }

    public BarChartModel getGraficoMesFiltroAno() {
        return graficoMesFiltroAno;
    }

    public void setGraficoMesFiltroAno(BarChartModel graficoMesFiltroAno) {
        this.graficoMesFiltroAno = graficoMesFiltroAno;
    }

    public BarChartModel getGraficoTotalFiltroAnoMes() {
        return graficoTotalFiltroAnoMes;
    }

    public void setGraficoTotalFiltroAnoMes(BarChartModel graficoTotalFiltroAnoMes) {
        this.graficoTotalFiltroAnoMes = graficoTotalFiltroAnoMes;
    }

    public LineChartModel getGraficoTotalAno() {
        return graficoTotalAno;
    }

    public void setGraficoTotalAno(LineChartModel graficoTotalAno) {
        this.graficoTotalAno = graficoTotalAno;
    }

    public HorizontalBarChartModel getGraficoTotalPrevistoFiltroAnoMes() {
        return graficoTotalPrevistoFiltroAnoMes;
    }

    public void setGraficoTotalPrevistoFiltroAnoMes(HorizontalBarChartModel graficoTotalPrevistoFiltroAnoMes) {
        this.graficoTotalPrevistoFiltroAnoMes = graficoTotalPrevistoFiltroAnoMes;
    }
}
