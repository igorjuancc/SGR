/*
    Status funcionario 
    [0] - Novo funcionario (Primeiro Acesso) - Pode ser excluído	
    [1] - Ativado	
    [2] - Desativado	
    [3] - Reativação (Primeiro acesso apos religação)
 */
package br.com.sgr.facade;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.FuncionarioDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.FuncionarioException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.Seguranca;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.PessoaValidator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.primefaces.model.UploadedFile;

public class FuncionarioFacade {

    private static final FuncionarioDao funcionarioDao = new FuncionarioDao();

    public static Funcionario funcionarioPorCpf(String cpf) {
        try {
            return funcionarioDao.funcionarioPorCpf(cpf);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Funcionario funcionarioPorId(int id) {
        try {
            return funcionarioDao.funcionarioPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void verificaCpfMorador(Funcionario funcionario) throws FuncionarioException {
        try {
            if ((funcionario == null) || (funcionario.getCpf() == null) || (funcionario.getCpf().isEmpty())) {
                throw new FuncionarioException("Problemas ao verificar CPF de novo funcionário");
            } else {
                funcionario.setCpf(funcionario.getCpf().replace(".", "").replace("-", ""));
                if (!SgrUtil.isCPF(funcionario.getCpf())) {
                    throw new FuncionarioException("CPF Inválido");
                } else {
                    Funcionario buscaFuncionario = funcionarioDao.funcionarioPorCpf(funcionario.getCpf());
                    if ((buscaFuncionario != null) && (buscaFuncionario.getStatus() != 0)) {
                        throw new FuncionarioException("CPF já cadastrado para funcionário");
                    } else {
                        Pessoa buscaPessoa = PessoaFacade.pessoaPorCpf(funcionario.getCpf());
                        if (buscaPessoa != null) {
                            funcionario.setId(buscaPessoa.getId());
                            funcionario.setNome(buscaPessoa.getNome());
                            funcionario.setCpf(buscaPessoa.getCpf());
                            funcionario.setEmail(buscaPessoa.getEmail());
                            funcionario.setDataNascimento(buscaPessoa.getDataNascimento());
                            funcionario.setSexo(buscaPessoa.getSexo());
                            funcionario.setFone(buscaPessoa.getFone());
                            funcionario.setCelular(buscaPessoa.getCelular());
                            funcionario.setImagem(buscaPessoa.getImagem());
                        }
                    }
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void cadastrarFuncionario(Funcionario funcionario, UploadedFile imagem) throws FuncionarioException {
        try {
            Pessoa verificarPessoa;
            String senha;

            funcionario.setStatus(0);
            funcionario.setNome(funcionario.getNome().trim().toUpperCase());
            funcionario.setCpf(funcionario.getCpf().replace(".", "").replace("-", ""));
            funcionario.setSenha(Seguranca.gerarSenha(5));

            PessoaValidator.validaFuncionario(funcionario);

            if ((funcionario.getEmpresa() != null) && (!funcionario.getEmpresa().isEmpty())) {
                funcionario.setEmpresa(funcionario.getEmpresa().trim().toUpperCase());
                if (funcionario.getEmpresa().length() > 50) {
                    throw new FuncionarioException("O número de caracteres de empresa não pode exceder a 50");
                }
            } else {
                funcionario.setEmpresa(null);
            }
            if (funcionario.getEmail() != null) {
                funcionario.setEmail(funcionario.getEmail().trim());
                if (funcionario.getEmail().equals("")) {
                    funcionario.setEmail(null);
                } else {
                    verificarPessoa = PessoaFacade.pessoaPorEmail(funcionario.getEmail());
                    if ((verificarPessoa != null) && (verificarPessoa.getId() != funcionario.getId())) {
                        throw new FuncionarioException("Email já cadastrado");
                    }
                }
            }
            if (funcionarioDao.funcionarioPorCpf(funcionario.getCpf()) != null) {
                throw new FuncionarioException("CPF já cadastrado para funcionário");
            } else {
                verificarPessoa = PessoaFacade.pessoaPorCpf(funcionario.getCpf());
            }

            senha = funcionario.getSenha();
            funcionario.setSenha(Seguranca.md5(senha));

            if (verificarPessoa == null) {
                funcionarioDao.cadastrarFuncionario(funcionario);
            } else {
                funcionarioDao.cadastrarTbFuncionario(funcionario);
                if (!cadastrarTbFuncionario(funcionario)) {
                    funcionarioDao.apagarTbFuncionario(funcionario);
                }
            }
            if (imagem != null) {
                PessoaFacade.salvarImagemPessoa(imagem, funcionario.getImagem());
            }
            funcionario.setSenha(senha);
            LogBDFacade.inserirLog(1, funcionario);
        } catch (DaoException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar os dados de funcionario";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            apagarNovoFuncionario(funcionario);
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static boolean cadastrarTbFuncionario(Funcionario funcionario) {
        try {
            PessoaFacade.atualizaEmailPessoa(funcionario);
            if (funcionario.getImagem().getId() == 0) {
                ArquivoFacade.salvarArquivo(funcionario.getImagem());
                PessoaFacade.atualizarImagemPessoa(funcionario);
            }
            return true;
        } catch (DaoException | ArquivoException e) {
            System.out.println("****Problemas ao cadastrar morador TB [Facade]****");
            e.printStackTrace(System.out);
            return false;
        }
    }

    private static void apagarNovoFuncionario(Funcionario funcionario) {
        try {
            if ((VisitanteFacade.visitantePorId(funcionario.getId()) == null)
                    && (MoradorFacade.moradorPorId(funcionario.getId()) == null)) {
                funcionarioDao.removerFuncionario(funcionario);
            } else {
                funcionarioDao.apagarTbFuncionario(funcionario);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar os dados de novo funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarFuncionario(Funcionario funcionario, UploadedFile imagem) throws FuncionarioException {
        try {
            Pessoa verificarPessoa;
            Funcionario buscaFuncionario;

            funcionario.setNome(funcionario.getNome().trim().toUpperCase());
            funcionario.setCpf(funcionario.getCpf().replace(".", "").replace("-", ""));

            PessoaValidator.validaFuncionario(funcionario);

            if ((funcionario.getEmpresa() != null) && (!funcionario.getEmpresa().isEmpty())) {
                funcionario.setEmpresa(funcionario.getEmpresa().trim().toUpperCase());
                if (funcionario.getEmpresa().length() > 50) {
                    throw new FuncionarioException("O número de caracteres de empresa não pode exceder a 50");
                }
            } else {
                funcionario.setEmpresa(null);
            }
            if ((funcionario.getEmail() != null) && (!funcionario.getEmail().isEmpty())) {
                funcionario.setEmail(funcionario.getEmail().trim());
                if (funcionario.getEmail().equals("")) {
                    funcionario.setEmail(null);
                } else {
                    verificarPessoa = PessoaFacade.pessoaPorEmail(funcionario.getEmail());
                    if ((verificarPessoa != null) && (verificarPessoa.getId() != funcionario.getId())) {
                        throw new FuncionarioException("Email já cadastrado");
                    }
                }
            }
            buscaFuncionario = funcionarioDao.funcionarioPorCpf(funcionario.getCpf());
            if ((buscaFuncionario != null) && (buscaFuncionario.getId() != funcionario.getId())) {
                throw new FuncionarioException("CPF já cadastrado para outro funcionário");
            }
            funcionarioDao.atualizarFuncionario(funcionario);
            LogBDFacade.inserirLog(3, funcionario);

            if (imagem != null) {
                PessoaFacade.salvarImagemPessoa(imagem, funcionario.getImagem());
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar os dados de funcionario";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            try {
                ArquivoFacade.apagarArquivo(funcionario.getImagem());
            } catch (ArquivoException ex1) {
                ex1.printStackTrace(System.out);
            }
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar a imagem de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void removerFuncionario(Funcionario funcionario) throws FuncionarioException {
        try {
            //Somente funcionarios novos (Status 0) podem ser removidos
            if (funcionario.getStatus() == 0) {
                //Verificar se não é morador ou visitante
                if ((MoradorFacade.moradorPorId(funcionario.getId()) == null)
                        && (VisitanteFacade.visitantePorId(funcionario.getId()) == null)) {
                    funcionarioDao.removerFuncionario(funcionario);
                    LogBDFacade.inserirLog(2, funcionario);
                    PessoaFacade.removerImagemPessoa(funcionario);
                } else {
                    funcionarioDao.apagarTbFuncionario(funcionario);
                    LogBDFacade.inserirLog(2, funcionario);
                }
            } else {
                throw new FuncionarioException("Funcionario não pode ser removído");
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar os dados de funcionario";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar imagem de funcionario";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void alterarSenhaFuncionario(Funcionario funcionario) throws FuncionarioException {
        try {
            if (!funcionarioDao.alterarSenhaFuncionario(funcionario)) {
                throw new FuncionarioException("Não foi possivel alterar senha de funcionário");
            } else {
                LogBDFacade.inserirLog(3, funcionario);
                if ((funcionario.getStatus() == 0) || (funcionario.getStatus() == 3)) {
                    funcionario.setStatus(1);
                    if (funcionarioDao.atualizarStatusFuncionario(funcionario)) {
                        LogBDFacade.inserirLog(6, funcionario);
                    } else {
                        throw new FuncionarioException("Não foi possível alterar status de funcionário");
                    }
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao alterar dados de funcionario";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void gerarNovaSenha(Funcionario funcionario) throws FuncionarioException {
        try {
            String novaSenha = Seguranca.gerarSenha(5);
            funcionario.setSenha(Seguranca.md5(novaSenha));
            if (funcionario.getStatus() != 0) {
                funcionario.setStatus(3);
            }
            if (!funcionarioDao.alterarSenhaFuncionario(funcionario)) {
                throw new FuncionarioException("Não foi possível alterar a senha do funcionário");
            }
            if (!funcionarioDao.atualizarStatusFuncionario(funcionario)) {
                throw new FuncionarioException("Não foi possível alterar o status do funcionário");
            }
            funcionario.setSenha(novaSenha);
            LogBDFacade.inserirLog(3, funcionario);
            if (funcionario.getStatus() == 3) {
                LogBDFacade.inserirLog(6, funcionario);
            }
        } catch (NoSuchAlgorithmException | DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao gerar nova senha para funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarProfissionalFuncionario(Funcionario funcionario) throws FuncionarioException {
        try {
            if (funcionario.getFuncao() == null || funcionario.getFuncao().getId() == 0) {
                throw new FuncionarioException("Necessário indicar uma função válida para o funcionário");
            }
            if ((funcionario.getEmpresa() != null) && (!funcionario.getEmpresa().isEmpty())) {
                funcionario.setEmpresa(funcionario.getEmpresa().trim().toUpperCase());
                if (funcionario.getEmpresa().length() > 50) {
                    throw new FuncionarioException("O número de caracteres de empresa não pode exceder a 50");
                }
            } else {
                funcionario.setEmpresa(null);
            }
            if (!funcionarioDao.atualizarProfissionalFuncionario(funcionario)) {
                throw new FuncionarioException("Problemas ao atualizar dados de funcionário");
            } else {
                LogBDFacade.inserirLog(3, funcionario);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar dados profissionais de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Funcionario> listaFuncionario() throws DaoException {
        try {
            return funcionarioDao.listaFuncionario();
        } catch (DaoException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("****Problema ao listar funcionarios [Facade]****" + e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void atualizarStatusFuncionario(Funcionario funcionario) throws FuncionarioException {
        try {
            if (funcionarioDao.atualizarStatusFuncionario(funcionario)) {
                if (funcionario.getStatus() == 2) {
                    LogBDFacade.inserirLog(5, funcionario);
                }
            } else {
                throw new FuncionarioException("Não foi possível modificar o status do funcionário");
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao modificar status de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void reativarFuncionario(Funcionario funcionario) throws FuncionarioException {
        try {
            if (funcionario.getFuncao() == null || funcionario.getFuncao().getId() == 0) {
                throw new FuncionarioException("Necessário indicar uma função válida para o funcionário");
            } else if ((funcionario.getEmpresa() != null) && (!funcionario.getEmpresa().isEmpty())) {
                funcionario.setEmpresa(funcionario.getEmpresa().trim().toUpperCase());
                if (funcionario.getEmpresa().length() > 50) {
                    throw new FuncionarioException("O número de caracteres de empresa não pode exceder a 50");
                }
            } else {
                funcionario.setEmpresa(null);
            }

            String novaSenha = Seguranca.gerarSenha(5);
            funcionario.setSenha(Seguranca.md5(novaSenha));
            funcionario.setStatus(3);
            if (!funcionarioDao.atualizarProfissionalFuncionario(funcionario)) {
                throw new FuncionarioException("Problemas ao reativar funcionário");
            } else {
                funcionario.setSenha(novaSenha);
                LogBDFacade.inserirLog(3, funcionario);
                LogBDFacade.inserirLog(6, funcionario);
            }
        } catch (DaoException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao reativar funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static int totalFuncionarioStatus(int Status) {
        try {
            return funcionarioDao.totalFuncionarioStatus(Status);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema buscar dados de funcionários";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static List<Funcionario> funcionarioAtvFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = 1 OR f.status = 3 ORDER BY f.nome ");
                    break;
                case "funcao.descricao":
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = 1 OR f.status = 3 ORDER BY f.funcao.descricao ");
                    break;
                default:
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = 1 OR f.status = 3 ORDER BY f.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return funcionarioDao.funcionarioFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema buscar dados de funcionários";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Funcionario> funcionarioStatusFiltro(FiltroBD filtro, int status) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";

            switch (verFiltro) {
                case "nome":
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = " + Integer.toString(status) + " ORDER BY f.nome ");
                    break;
                case "funcao.descricao":
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = " + Integer.toString(status) + " ORDER BY f.funcao.descricao ");
                    break;
                default:
                    filtro.setDescricao("FROM Funcionario f WHERE f.status = " + Integer.toString(status) + " ORDER BY f.id ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return funcionarioDao.funcionarioFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema buscar dados de funcionários";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
