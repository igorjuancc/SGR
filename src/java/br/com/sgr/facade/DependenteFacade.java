package br.com.sgr.facade;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.MoradorDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.mb.LoginMB;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.PessoaValidator;
import java.util.List;
import javax.faces.context.FacesContext;
import org.primefaces.model.UploadedFile;

public class DependenteFacade {

    private static final MoradorDao dependenteDao = new MoradorDao();

    public static List<Morador> listaDependentes(Morador morador) {
        try {
            return dependenteDao.listaDependentes(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de dependentes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalDependentes(Morador morador) {
        try {
            return dependenteDao.totalDependentes(morador);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de dependentes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Morador> dependenteFiltro(FiltroBD filtro, Morador resp) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("FROM Morador m WHERE m.tipo = 2 AND apartamento_morador = " + Integer.toString(resp.getApartamento().getId()) + " ORDER BY m.nome ");
                    break;
                case "status":
                    filtro.setDescricao("FROM Morador m WHERE m.tipo = 2 AND apartamento_morador = " + Integer.toString(resp.getApartamento().getId()) + " ORDER BY m.status ");
                    break;
                default:
                    filtro.setDescricao("FROM Morador m WHERE m.tipo = 2 AND apartamento_morador = " + Integer.toString(resp.getApartamento().getId()) + " ORDER BY m.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return dependenteDao.listaMoradorFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de dependentes";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Pessoa verificaCpfDependente(String cpf) throws MoradorException {
        try {
            cpf = cpf.replace(".", "").replace("-", "");
            if ((!SgrUtil.isCPF(cpf))) {
                throw new MoradorException("Número de CPF inválido");
            } else if (dependenteDao.moradorPorCpf(cpf) != null) {
                throw new MoradorException("CPF já cadastrado para morador");
            } else {
                return PessoaFacade.pessoaPorCpf(cpf);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de cpf do dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void cadastrarDependente(Morador dependente, UploadedFile imagem) throws MoradorException {
        try {
            Pessoa verificarPessoa;
            LoginMB usuario = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{LoginMB}", LoginMB.class);

            if (DependenteFacade.totalDependentes(usuario.getMorador()) > 15) {
                throw new MoradorException("Máximo de dependentes cadastrados [15]");
            }

            if (dependente.getEmail() != null) {
                dependente.setEmail(dependente.getEmail().trim());
                if (dependente.getEmail().equals("")) {
                    dependente.setEmail(null);
                }
            }
            dependente.setNome(dependente.getNome().trim().toUpperCase());
            dependente.setCpf(dependente.getCpf().replace(".", "").replace("-", ""));
            dependente.setStatus(1);
            dependente.setTipo(2);

            if ((dependente.getFone() != null) && (!dependente.getFone().isEmpty())) {
                dependente.setFone(dependente.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((dependente.getCelular() != null) && (!dependente.getCelular().isEmpty())) {
                dependente.setCelular(dependente.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaDependente(dependente);

            verificarPessoa = PessoaFacade.pessoaPorEmail(dependente.getEmail());
            if ((verificarPessoa != null) && (verificarPessoa.getId() != dependente.getId())) {
                throw new MoradorException("Email já cadastrado");
            }
            if (dependenteDao.moradorPorCpf(dependente.getCpf()) != null) {
                throw new MoradorException("CPF já cadastrado para morador");
            } else {
                verificarPessoa = PessoaFacade.pessoaPorCpf(dependente.getCpf());
            }
            if (verificarPessoa == null) {
                dependenteDao.cadastrarMorador(dependente);
                LogBDFacade.inserirLog(1, dependente);
            } else {
                if (FuncionarioFacade.funcionarioPorCpf(dependente.getCpf()) != null) {
                    if (SgrUtil.idade(dependente.getDataNascimento()) < 18) {
                        throw new MoradorException("Funcionário não pode ser menor de idade");
                    }
                }
                if (verificarPessoa.getClass().getSimpleName().equals("Morador")) {
                    dependenteDao.atualizarMorador(dependente);
                    LogBDFacade.inserirLog(7, dependente);
                } else {
                    dependenteDao.cadastrarTbMorador(dependente);
                    if (!cadastrarTbMorador(dependente)) {
                        dependenteDao.apagarTbMorador(dependente);
                        SgrUtil.mensagemErroRedirecionamento("Houve um problema ao cadastrar os dados de dependente");
                    }
                    LogBDFacade.inserirLog(1, dependente);
                }
            }
            if (imagem != null) {
                PessoaFacade.salvarImagemPessoa(imagem, dependente.getImagem());
            }
            LogBDCadastroMoradorFacade.logCadastroNovoMorador(dependente);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar os dados de dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            apagarNovoDependente(dependente);
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static boolean cadastrarTbMorador(Morador morador) {
        try {
            dependenteDao.atualizarEmailMorador(morador);
            if (morador.getImagem().getId() == 0) {
                ArquivoFacade.salvarArquivo(morador.getImagem());
                dependenteDao.atualizarImagemMorador(morador);
            }
            return true;
        } catch (DaoException | ArquivoException e) {
            System.out.println("****Problemas ao cadastrar dependente TB [Facade]****");
            e.printStackTrace(System.out);
            return false;
        }
    }

    private static void apagarNovoDependente(Morador dependente) {
        try {
            if ((VisitanteFacade.visitantePorId(dependente.getId()) == null)
                    && (FuncionarioFacade.funcionarioPorId(dependente.getId()) == null)) {
                dependenteDao.apagarMorador(dependente);
            } else {
                dependenteDao.apagarTbMorador(dependente);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar os dados de novo dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarDependente(Morador dependente, UploadedFile imagem) throws MoradorException {
        try {
            Pessoa verificarPessoa;

            dependente.setNome(dependente.getNome().trim().toUpperCase());
            dependente.setCpf(dependente.getCpf().replace(".", "").replace("-", ""));
            if (dependente.getEmail() != null) {
                dependente.setEmail(dependente.getEmail().trim());
                if (dependente.getEmail().equals("")) {
                    dependente.setEmail(null);
                }
            }
            if ((dependente.getFone() != null) && (!dependente.getFone().isEmpty())) {
                dependente.setFone(dependente.getFone().replace("(", "").replace(")", "").replace("-", ""));
            }
            if ((dependente.getCelular() != null) && (!dependente.getCelular().isEmpty())) {
                dependente.setCelular(dependente.getCelular().replace("(", "").replace(")", "").replace("-", ""));
            }

            PessoaValidator.validaDependente(dependente);

            verificarPessoa = dependenteDao.moradorPorCpf(dependente.getCpf());
            if ((verificarPessoa != null) && (verificarPessoa.getId() != dependente.getId())) {
                throw new MoradorException("CPF cadastrado para outro morador");
            }
            if (FuncionarioFacade.funcionarioPorCpf(dependente.getCpf()) != null) {
                if (SgrUtil.idade(dependente.getDataNascimento()) < 18) {
                    throw new MoradorException("Funcionário não pode ser menor de idade");
                }
            }
            verificarPessoa = PessoaFacade.pessoaPorEmail(dependente.getEmail());
            if ((verificarPessoa != null) && (verificarPessoa.getId() != dependente.getId())) {
                throw new MoradorException("Email já cadastrado");
            }
            dependenteDao.atualizarMorador(dependente);
            LogBDFacade.inserirLog(3, dependente);

            if (imagem != null) {
                PessoaFacade.salvarImagemPessoa(imagem, dependente.getImagem());
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar os dados de dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void removerDependente(Morador dependente) {
        try {
            Apartamento apto = dependente.getApartamento();
            dependente.setApartamento(null);
            dependente.setStatus(4);
            dependenteDao.atualizarMorador(dependente);
            dependente.setApartamento(apto);
            LogBDFacade.inserirLog(5, dependente);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar dados de dependente";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }
}
