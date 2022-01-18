package br.com.sgr.validator;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.FuncionarioException;
import br.com.sgr.exception.MoradorException;
import br.com.sgr.exception.VisitanteException;
import br.com.sgr.util.Seguranca;
import br.com.sgr.util.SgrUtil;
import java.util.Date;

public class PessoaValidator {

    private static String validaPessoa(Pessoa pessoa) {
        String mensagem = "";
        Date hoje = new Date();

        if ((pessoa.getNome() == null)
                || (pessoa.getNome().isEmpty())
                || (pessoa.getNome().trim().equals(""))) {
            mensagem += "Nome inválido<br/>";
        } else if (pessoa.getNome().length() > 100) {
            mensagem += "O nome não pode ultrapassar [100] caracteres<br/>";
        }
        if (!SgrUtil.isCPF(pessoa.getCpf())) {
            mensagem += "CPF inválido<br/>";
        }
        if ((pessoa.getEmail() != null)
                && (!pessoa.getEmail().isEmpty())
                && (!pessoa.getEmail().trim().equals(""))) {
            if (!Seguranca.isEmail(pessoa.getEmail())) {
                mensagem += "Email inválido<br/>";
            }
        }
        if (pessoa.getDataNascimento() == null) {
            mensagem += "Data de nascimento inválida<br/>";
        } else if (pessoa.getDataNascimento().after(hoje)) {
            mensagem += "Data de nascimento não pode ser superior a data de hoje "
                    + "[" + SgrUtil.formataData(hoje) + "]<br/>";
        }
        if ((pessoa.getSexo() == null)
                || (pessoa.getSexo().trim().equals("")
                || ((!pessoa.getSexo().equals("M") && !pessoa.getSexo().equals("F"))))) {
            mensagem += "Sexo inválido<br/>";
        }
        if ((pessoa.getFone() != null) && (!pessoa.getFone().trim().isEmpty())) {
            if ((pessoa.getFone().length() < 10) && (pessoa.getFone().length() > 11)) {
                mensagem += "Telefone inválido<br/>";
            }
        }
        if ((pessoa.getCelular() != null) && (!pessoa.getCelular().trim().isEmpty())) {
            if ((pessoa.getCelular().length() != 11)) {
                mensagem += "Celular inválido<br/>";
            }
        }
        if (pessoa.getImagem() != null) {
            try {
                ArquivoValidator.validaImagem(pessoa.getImagem());
            } catch (ArquivoException ex) {
                mensagem += ex.getMessage();
            }
        }
        return mensagem;
    }

    public static void validaMorador(Morador morador) throws MoradorException {
        if (morador == null) {
            throw new MoradorException("Morador inválido");
        } else {
            String mensagem = validaPessoa(morador);

            if (morador.getTipo() != 1) {
                mensagem += "Tipo de morador inválido<br/>";
            }
            if ((morador.getStatus() != 0)
                    && (morador.getStatus() != 2)
                    && (morador.getStatus() != 3)
                    && (morador.getStatus() != 4)
                    && (morador.getStatus() != 5)) {
                mensagem += "Status de morador inválido<br/>";
            }
            if ((morador.getSenha() == null)
                    || (morador.getSenha().isEmpty())
                    || (morador.getSenha().trim().equals(""))) {
                mensagem += "Senha inválida<br/>";
            } else {
                if (morador.getSenha().length() < 5) {
                    mensagem += "Senha inválida, Mínimo [5] caracteres<br/>";
                }
                if (morador.getSenha().length() > 100) {
                    mensagem += "Senha inválida, Máximo [100] caracteres<br/>";
                }
            }
            if (morador.getApartamento() == null) {
                mensagem += "Necessário indicar o apartamento do morador<br/>";
            }
            if (morador.getImagem() == null) {
                mensagem += "Necessário inserir uma imagem do morador<br/>";
            }
            if ((morador.getDataNascimento() != null)
                    && (SgrUtil.idade(morador.getDataNascimento()) < 18)) {
                mensagem += "O morador não pode ser menor de idade<br/>";
            }
            if ((morador.getEmail() == null)
                    || (morador.getEmail().isEmpty())
                    || (morador.getEmail().equals(""))) {
                mensagem += "Necessário indicar o email do morador<br/>";
            }

            if (!mensagem.equals("")) {
                throw new MoradorException(mensagem);
            }
        }
    }

    public static void validaDependente(Morador morador) throws MoradorException {
        if (morador == null) {
            throw new MoradorException("Dependente inválido");
        } else {
            String mensagem = validaPessoa(morador);

            if (morador.getTipo() != 2) {
                mensagem += "Tipo de dependente inválido<br/>";
            }
            if ((morador.getStatus() != 0)
                    && (morador.getStatus() != 1)
                    && (morador.getStatus() != 4)) {
                mensagem += "Status de dependete inválido<br/>";
            }
            if (morador.getSenha() != null) {
                mensagem += "Dependente não deve possuir senha<br/>";
            }
            if (morador.getApartamento() == null) {
                mensagem += "Necessário indicar o apartamento do dependente<br/>";
            }
            if (morador.getImagem() == null) {
                mensagem += "Necessário inserir uma imagem do dependente<br/>";
            }

            if (!mensagem.equals("")) {
                throw new MoradorException(mensagem);
            }
        }
    }

    public static void validaFuncionario(Funcionario funcionario) throws FuncionarioException {
        if (funcionario == null) {
            throw new FuncionarioException("Morador inválido");
        } else {
            String mensagem = validaPessoa(funcionario);

            if ((funcionario.getDataNascimento() != null)
                    && (SgrUtil.idade(funcionario.getDataNascimento())) < 18) {
                mensagem += "O funcionário não pode ser menor de idade<br/>";
            }
            if (funcionario.getImagem() == null) {
                mensagem += "Necessário inserir uma imagem do novo funcionário<br/>";
            }
            if (funcionario.getFuncao() == null || funcionario.getFuncao().getId() == 0) {
                mensagem += "Necessário indicar uma função válida para o novo funcionário<br/>";
            }
            if ((funcionario.getSenha() == null)
                    || (funcionario.getSenha().isEmpty())
                    || (funcionario.getSenha().trim().equals(""))) {
                mensagem += "Senha inválida<br/>";
            } else {
                if (funcionario.getSenha().length() < 5) {
                    mensagem += "Senha inválida, Mínimo [5] caracteres<br/>";
                }
                if (funcionario.getSenha().length() > 100) {
                    mensagem += "Senha inválida, Máximo [100] caracteres<br/>";
                }
            }

            if (!mensagem.equals("")) {
                throw new FuncionarioException(mensagem);
            }
        }
    }
    
    public static void validaVisitante(Visitante visitante) throws VisitanteException {
        if (visitante == null) {
            throw new VisitanteException("Visitante inválido");
        } else {
            String mensagem = validaPessoa(visitante);
            
            if ((visitante.getMoradorCadastro() == null) 
                    || (visitante.getMoradorCadastro().getId() == 0)) {
                 mensagem += "Morador responsável inválido<br/>";                                
            }

            if (!mensagem.equals("")) {
                throw new VisitanteException(mensagem);
            }
        }
    }
}
