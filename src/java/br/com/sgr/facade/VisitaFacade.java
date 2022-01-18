package br.com.sgr.facade;

import br.com.sgr.bean.RegistroES;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Visita;
import br.com.sgr.bean.Visitante;
import br.com.sgr.dao.VisitaDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.VisitaException;
import br.com.sgr.exception.VisitanteException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.VisitaValidator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

public class VisitaFacade {

    private static final VisitaDao visitaDao = new VisitaDao();

    public static void cadastrarVisita(Visita visita) throws VisitaException {
        try {
            LoginMB usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);
            VisitaValidator.validaVisita(visita);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date hoje = cal.getTime();

            if (hoje.after(visita.getDataInicio())) {
                throw new VisitaException("A data inicial da visita não pode ser anterior a data de hoje: " + SgrUtil.formataData(hoje));
            }
            for (Visitante v : visita.getVisitantes()) {
                if (v.getMoradorCadastro() == null) {
                    v.setMoradorCadastro(usuario.getMorador());
                    VisitanteFacade.cadastrarVisitanteVisita(v);
                }
            }
            verificaVisitaDataVisitante(visita);
            visitaDao.cadastraVisita(visita);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitas";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (VisitanteException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarVisita(Visita visita) throws VisitaException {
        try {
            LoginMB usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);
            List<Date> datasEntradas = new ArrayList<>();

            for (Visitante vis : visita.getVisitantes()) {
                if (vis.getRegEntradaSaida() != null) {
                    for (RegistroES e : vis.getRegEntradaSaida()) {
                        datasEntradas.add(e.getDataEntrada());
                    }
                }
            }

            VisitaValidator.validaVisita(visita);

            if ((!datasEntradas.isEmpty()) && (visita.getDataFim().before(Collections.max(datasEntradas)))) {
                throw new VisitaException("A data final não pode ser anterior ou igual a: "
                        + SgrUtil.formataData(Collections.max(datasEntradas)));
            }
            for (Visitante v : visita.getVisitantes()) {
                if (v.getMoradorCadastro() == null) {
                    v.setMoradorCadastro(usuario.getMorador());
                    VisitanteFacade.cadastrarVisitanteVisita(v);
                }
            }
            verificaVisitaDataVisitante(visita);
            visitaDao.atualizaVisita(visita);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitas";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (VisitanteException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void verificaVisitaDataVisitante(Visita visita) throws VisitaException {
        try {
            String mensagem = "";
            for (Visitante v : visita.getVisitantes()) {
                if (v.getVisitas() == null) {
                    v.setVisitas(new ArrayList());
                } else {
                    v.getVisitas().clear();
                }
                v.getVisitas().add(visita);
                if (VisitanteFacade.verificaVisitaDataVisitante(v)) {
                    v.setVisitas(listaVisitaPorVisitante(v));
                }

                if ((v.getVisitas() != null) && (!v.getVisitas().contains(visita))) {
                    int ultmVisita = v.getVisitas().size() - 1;
                    mensagem += v.getNome() + " "
                            + SgrUtil.formataData(v.getVisitas().get(ultmVisita).getDataInicio())
                            + " - " + SgrUtil.formataData(v.getVisitas().get(ultmVisita).getDataFim());

                }
            }
            if (!mensagem.equals("")) {
                mensagem = "O(s) Seguinte(s) Morador(es) já possuem vistas entres os dias:<br/>"
                        + mensagem;
                throw new VisitaException(mensagem);
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitas";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static int totalVisitaPorApto(Morador morador) {
        try {
            return visitaDao.totalVisitaPorApto(morador);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visita";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Visita> listaVisitaFiltro(FiltroBD filtro, Morador morador) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            switch (verFiltro) {
                case "dataInicio":
                    filtro.setDescricao("SELECT DISTINCT v FROM Visita v WHERE v.apartamento.id = " + Integer.toString(morador.getApartamento().getId()) + " AND v.dataInicio >= '" + fmt.format(morador.getDataCadastro()) + "' ORDER BY v.dataInicio ");
                    break;
                case "dataFim":
                    filtro.setDescricao("SELECT DISTINCT v FROM Visita v WHERE v.apartamento.id = " + Integer.toString(morador.getApartamento().getId()) + " AND v.dataInicio >= '" + fmt.format(morador.getDataCadastro()) + "' ORDER BY v.dataFim ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao("SELECT DISTINCT v FROM Visita v WHERE v.apartamento.id = " + Integer.toString(morador.getApartamento().getId()) + " AND v.dataInicio >= '" + fmt.format(morador.getDataCadastro()) + "' ORDER BY v.dataInicio ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            List<Visita> visitas = visitaDao.listaVisitaFiltro(filtro);
            for (Visita v : visitas) {
                v.setVisitantes(VisitanteFacade.visitantesVisita(v));
            }
            return visitas;
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visita";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Visita> listaVisitaPorVisitante(Visitante vistante) {
        try {
            return visitaDao.listaVisitaPorVisitante(vistante);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visita";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarVisita(Visita visita) throws VisitaException {
        try {
            Boolean apagar = true;

            for (int i = 0; i < visita.getVisitantes().size(); i++) {
                visita.getVisitantes().get(i).setVisitas(new ArrayList<>());
                Visita verifica = new Visita();
                verifica.setDataFim((Date) visita.getDataFim().clone());
                verifica.setDataInicio((Date) visita.getDataInicio().clone());
                visita.getVisitantes().get(i).getVisitas().add(verifica);
                if (!RegistroESFacade.listaRegistroDataPessoa(visita.getVisitantes().get(i)).isEmpty()) {
                    apagar = false;
                    i = visita.getVisitantes().size();
                }
            }
            if (!apagar) {
                throw new VisitaException("Registro de visita já inciado não pode ser apagado");
            } else if (!visitaDao.apagarVisita(visita)) {
                throw new VisitaException("Problemas ao remover visita");
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visita";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }
}
