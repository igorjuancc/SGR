package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.dao.PessoaDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;
import java.util.List;
import org.primefaces.model.UploadedFile;

public class PessoaFacade {

    private static final PessoaDao pessoaDao = new PessoaDao();

    public static Pessoa pessoaPorCpf(String cpf) {
        try {
            return pessoaDao.pessoaPorCpf(cpf);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de pessoa por CPF";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Pessoa pessoaPorEmail(String email) {
        try {
            return pessoaDao.pessoaPorEmail(email);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de pessoa por email";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Pessoa pessoaPorId(int id) {
        try {
            return pessoaDao.pessoaPorId(id);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar informações de pessoa por ID";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Busca moradores e funcionarios ativos por nome CONTATOS
    public static List<Pessoa> buscaParteNomeContato(String nome) {
        try {
            return pessoaDao.buscaParteNomeContato(nome.toUpperCase());
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de pessoas por nome";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Lista de pessoas ativas nos sistema
    public static List<Pessoa> pessoasAtivas() {
        try {
            return pessoaDao.pessoasAtivas();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de pessoas";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Lista de moradores ativos nos sistema
    public static List<Pessoa> moradoresAtivos() {
        try {
            return pessoaDao.moradoresAtivos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de moradores";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Lista de funcionarios ativos nos sistema
    public static List<Pessoa> funcionariosAtivos() {
        try {
            return pessoaDao.funcionariosAtivos();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar dados de funcionarios";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    //Criado para inserir email de visitante que esta sendo cadastrado como morador
    public static void atualizaEmailPessoa(Pessoa pessoa) throws DaoException {
        try {
            pessoaDao.atualizarEmailPessoa(pessoa);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar email de pessoa";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarImagemPessoa(Pessoa pessoa) throws DaoException {
        try {
            pessoaDao.atualizarImagemPessoa(pessoa);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao atualizar imagem de pessoa";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void removerImagemPessoa(Pessoa pessoa) throws ArquivoException {
        String caminho = SgrUtil.caminhoProjeto() + "ImagemPessoa\\";
        String nomeArquivo = Integer.toString(pessoa.getImagem().getId()) + pessoa.getImagem().getExtensao();
        SgrUtil.apagarArquivo(caminho, nomeArquivo);
    }

    public static void salvarImagemPessoa(UploadedFile imagem, Arquivo arquivo) throws ArquivoException {
        String caminho = SgrUtil.caminhoProjeto() + "ImagemPessoa\\";
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        SgrUtil.gravarArquivo(imagem, caminho, nomeArquivo);
    }
}
