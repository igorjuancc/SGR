package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.Visita;
import br.com.sgr.bean.Visitante;
import br.com.sgr.dao.VisitanteDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.VisitanteException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.PessoaValidator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import org.primefaces.model.UploadedFile;

public class VisitanteFacade {

    private static final VisitanteDao visitanteDao = new VisitanteDao();
    private static LoginMB usuario;

    public static void cadastrarVisitante(Visitante visitante, UploadedFile imagem) throws VisitanteException {
        try {
            Pessoa verificarPessoa;

            visitante.setCpf(visitante.getCpf().replace(".", "").replace("-", ""));
            visitante.setNome(visitante.getNome().trim().toUpperCase());

            if (visitante.getEmail() != null) {
                visitante.setEmail(visitante.getEmail().trim());
            }
            if (visitante.getEmail().isEmpty()) {
                visitante.setEmail(null);
            }
            if ((visitante.getFone() != null) && (!visitante.getFone().isEmpty())) {
                visitante.setFone(visitante.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((visitante.getCelular() != null) && (!visitante.getCelular().isEmpty())) {
                visitante.setCelular(visitante.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaVisitante(visitante);

            if (FuncionarioFacade.funcionarioPorCpf(visitante.getCpf()) != null) {
                if (SgrUtil.idade(visitante.getDataNascimento()) < 18) {
                    throw new VisitanteException("Funcionário não pode ser menor de idade");
                }
            }
            if (visitante.getEmail() != null) {
                verificarPessoa = PessoaFacade.pessoaPorEmail(visitante.getEmail());
                if ((verificarPessoa != null) && (verificarPessoa.getId() != visitante.getId())) {
                    throw new VisitanteException("Email já cadastrado");
                }
            }

            verificarPessoa = PessoaFacade.pessoaPorCpf(visitante.getCpf());

            if (verificarPessoa != null) {
                Morador buscaMorador = MoradorFacade.moradorPorCpf(visitante.getCpf());
                if ((buscaMorador != null) && !((buscaMorador.getStatus() == 0)
                        || (buscaMorador.getStatus() == 4 && buscaMorador.getApartamento() == null))) {
                    throw new VisitanteException("CPF cadastrado para morador");
                }
                if (visitanteDao.visitantePorCpf(visitante.getCpf()) != null) {
                    visitanteDao.atualizarVisitante(visitante);
                    LogBDFacade.inserirLog(3, visitante);
                } else {
                    visitante.setDataCadastro(new Date());
                    visitanteDao.cadastrarTbVisitante(visitante);
                    LogBDFacade.inserirLog(1, visitante);
                }
            } else {
                visitanteDao.cadastrarVisitante(visitante);
                LogBDFacade.inserirLog(1, visitante);
            }
            if (imagem != null) {
                PessoaFacade.salvarImagemPessoa(imagem, visitante.getImagem());
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar imagem de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    //Cadastro de pessoa via visita, sem edição de dados
    public static void cadastrarVisitanteVisita(Visitante visitante) throws VisitanteException {
        try {
            Pessoa verificarPessoa;

            visitante.setCpf(visitante.getCpf().replace(".", "").replace("-", ""));
            visitante.setNome(visitante.getNome().trim().toUpperCase());

            if (visitante.getEmail() != null) {
                visitante.setEmail(visitante.getEmail().trim());
            }
            if ((visitante.getEmail() != null) && (visitante.getEmail().isEmpty())) {
                visitante.setEmail(null);
            }
            if ((visitante.getFone() != null) && (!visitante.getFone().isEmpty())) {
                visitante.setFone(visitante.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((visitante.getCelular() != null) && (!visitante.getCelular().isEmpty())) {
                visitante.setCelular(visitante.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaVisitante(visitante);

            if (FuncionarioFacade.funcionarioPorCpf(visitante.getCpf()) != null) {
                if (SgrUtil.idade(visitante.getDataNascimento()) < 18) {
                    throw new VisitanteException("Funcionário não pode ser menor de idade");
                }
            }

            if (visitante.getEmail() != null) {
                verificarPessoa = PessoaFacade.pessoaPorEmail(visitante.getEmail());
                if ((verificarPessoa != null) && (verificarPessoa.getId() != visitante.getId())) {
                    throw new VisitanteException("Email já cadastrado");
                }
            }

            verificarPessoa = PessoaFacade.pessoaPorCpf(visitante.getCpf());

            if (verificarPessoa != null) {
                Morador buscaMorador = MoradorFacade.moradorPorCpf(visitante.getCpf());
                if ((buscaMorador != null) && !((buscaMorador.getStatus() == 0)
                        || (buscaMorador.getStatus() == 4 && buscaMorador.getApartamento() == null))) {
                    throw new VisitanteException("CPF cadastrado para morador");
                }
                if (visitanteDao.visitantePorCpf(visitante.getCpf()) != null) {
                    visitanteDao.atualizarVisitante(visitante);
                    LogBDFacade.inserirLog(3, visitante);
                } else {
                    visitante.setDataCadastro(new Date());
                    visitanteDao.cadastrarTbVisitante(visitante);
                    LogBDFacade.inserirLog(1, visitante);
                }
            } else {
                visitanteDao.cadastrarVisitante(visitante);
                LogBDFacade.inserirLog(1, visitante);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar visitante (Via visita)";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static Pessoa verificaCpfVisitante(String cpf) throws VisitanteException {
        cpf = cpf.replace(".", "").replace("-", "");
        if ((!SgrUtil.isCPF(cpf))) {
            throw new VisitanteException("Número de CPF inválido");
        } else {
            Morador buscaMorador = MoradorFacade.moradorPorCpf(cpf);
            if ((buscaMorador != null) && ((buscaMorador.getStatus() != 4) || (buscaMorador.getStatus() == 4 && buscaMorador.getApartamento() != null))) {
                throw new VisitanteException("CPF cadastrado para morador");
            }
            Funcionario buscaFuncionario = FuncionarioFacade.funcionarioPorCpf(cpf);
            if ((buscaFuncionario != null) && (buscaFuncionario.getStatus() != 2)) {
                throw new VisitanteException("CPF cadastrado para funcionário");
            }
            return PessoaFacade.pessoaPorCpf(cpf);
        }
    }

    //Lista de visitantes cadastrados por morador editaveis
    public static List<Visitante> visitantesEditCadastroMorador(Morador morador) {
        try {
            List<Visitante> visitantes = visitanteDao.visitantesCadastroMorador(morador);
            List<Visitante> visitantesRemover = new ArrayList<>();
            Morador m;
            Funcionario f;
            for (Visitante v : visitantes) {
                m = MoradorFacade.moradorPorId(v.getId());
                f = FuncionarioFacade.funcionarioPorId(v.getId());
                if (((f != null) && (f.getStatus() != 2))
                        || ((m != null) && ((m.getStatus() != 4) || (m.getStatus() == 4 && m.getApartamento() != null)))) {
                    visitantesRemover.add(v);
                }
            }
            visitantes.removeAll(visitantesRemover);
            return visitantes;
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void deletarVisitante(Visitante visitante) throws VisitanteException {
        try {
            usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);

            if ((visitante == null) || (visitante.getMoradorCadastro().getId() != usuario.getMorador().getId())) {
                throw new VisitanteException("Visitante não pode ser removído");
            } else {
                visitante.setVisitas(VisitaFacade.listaVisitaPorVisitante(visitante));
                for (Visita visita : visitante.getVisitas()) {
                    if (visita.getApartamento().getId() == usuario.getMorador().getApartamento().getId()) {
                        throw new VisitanteException("Visitante possuí registro de visita, não pode ser removído");
                    }
                }

                if ((visitante.getVisitas() != null) && (!visitante.getVisitas().isEmpty())) {
                    for (Visita visita : visitante.getVisitas()) {
                        if (visita.getApartamento().getId() != usuario.getMorador().getApartamento().getId()) {
                            visita.getApartamento().setMoradores(MoradorFacade.listaMoradorPorApto(visita.getApartamento()));
                            //Passando visitante para outro morador que agendou visita
                            for (int i = 0; i < visita.getApartamento().getMoradores().size(); i++) {
                                if (visita.getApartamento().getMoradores().get(i).getTipo() == 1) {
                                    visitante.setMoradorCadastro(visita.getApartamento().getMoradores().get(i));
                                    i = visita.getApartamento().getMoradores().size();
                                }
                            }
                            visitanteDao.atualizarVisitante(visitante);
                            LogBDFacade.inserirLog(3, visitante);
                        }
                    }
                } else {
                    if ((MoradorFacade.moradorPorId(visitante.getId()) != null)
                            || (FuncionarioFacade.funcionarioPorId(visitante.getId()) != null)) {
                        visitanteDao.deletarVisitanteTb(visitante);
                    } else {
                        visitanteDao.deletarVisitante(visitante);
                        if (visitante.getImagem() != null) {
                            PessoaFacade.removerImagemPessoa(visitante);
                        }
                    }
                    LogBDFacade.inserirLog(2, visitante);
                }

            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao remover imagem de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Visitante> visitantesDisponiveis() {
        try {
            return visitanteDao.visitantesDisponiveis();
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Boolean verificaVisitaDataVisitante(Visitante visitante) {
        try {
            return visitanteDao.verificaVisitaDataVisitante(visitante);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao verificar visita por data visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Visitante> visitantesVisita(Visita visita) {
        try {
            return visitanteDao.visitantesDeVisita(visita);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Lista de visitantes de apto desde registro morador
    public static List<Visitante> visitantesVisitaPrazo(Morador morador) {
        try {
            return visitanteDao.visitantesVisitaPrazo(morador);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int visitantesTotalDia() {
        try {
            int totalDia = visitanteDao.totalVisitantesDia(new Date());
            int totalSaida = visitanteDao.totalVisitantesNaoSairam();
            return (totalDia - totalSaida) < 0 ? 1 : (totalDia - totalSaida);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static int visitantesTotalNaoSairam() {
        try {
            return visitanteDao.totalVisitantesNaoSairam();
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Visitante> visitantesAgendadoFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("SELECT DISTINCT v FROM Visitante v RIGHT JOIN v.visitas vs WHERE CURRENT_DATE BETWEEN vs.dataInicio AND vs.dataFim ORDER BY v.nome ");
                    break;
                default:
                    filtro.setDescricao("SELECT DISTINCT v FROM Visitante v RIGHT JOIN v.visitas vs WHERE CURRENT_DATE BETWEEN vs.dataInicio AND vs.dataFim ORDER BY v.nome ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            List<Visitante> visitantesDia = visitanteDao.listaVisitanteFiltro(filtro);
            List<Visitante> visitantesSaida = visitanteDao.visitantesNaoSairam();
            visitantesDia.removeAll(visitantesSaida);
            for (Visitante v : visitantesDia) {
                v.setVisitas(VisitaFacade.listaVisitaPorVisitante(v));
                v.setRegEntradaSaida(RegistroESFacade.listaUltimosRegistroPessoa(v));
            }
            return visitantesDia;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Visitante> visitantesSaidaFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("SELECT DISTINCT v FROM Visitante v JOIN v.regEntradaSaida reg WHERE reg.dataSaida = NULL ORDER BY v.nome ");
                    break;
                default:
                    filtro.setDescricao("SELECT DISTINCT v FROM Visitante v JOIN v.regEntradaSaida reg WHERE reg.dataSaida = NULL ORDER BY v.nome ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            List<Visitante> visitantesSaida = visitanteDao.listaVisitanteFiltro(filtro);
            for (Visitante v : visitantesSaida) {
                v.setVisitas(VisitaFacade.listaVisitaPorVisitante(v));
                v.setRegEntradaSaida(RegistroESFacade.listaUltimosRegistroPessoa(v));
            }
            return visitantesSaida;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de visitantes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Visitante visitantePorId(int id) throws DaoException {
        try {
            return visitanteDao.visitantePorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void novaImagemViaPorteiro(Visitante visitante, byte[] imagem) throws VisitanteException {
        try {
            if ((visitante == null) || (visitante.getId() == 0)) {
                throw new VisitanteException("Visitante não encontrado");
            } else if (imagem == null) {
                throw new VisitanteException("Imagem não encontrada");
            } else {
                Arquivo a = new Arquivo();
                a.setExtensao(".PNG");
                ArquivoFacade.salvarArquivo(a);
                visitante.setImagem(a);
                visitanteDao.atualizarVisitante(visitante);
                LogBDFacade.inserirLog(3, visitante);
                String newFileName = SgrUtil.caminhoProjeto() + "ImagemPessoa\\" + Integer.toString(a.getId()) + a.getExtensao();
                FileImageOutputStream imageOutput;
                imageOutput = new FileImageOutputStream(new File(newFileName));
                imageOutput.write(imagem, 0, imagem.length);
                imageOutput.close();
            }
        } catch (ArquivoException | DaoException | IOException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar imagem de visitante";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }
}
