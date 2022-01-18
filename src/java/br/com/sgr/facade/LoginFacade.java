package br.com.sgr.facade;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.dao.LoginDao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.SgrUtil;

public class LoginFacade {

    private static final LoginDao loginDao = new LoginDao();

    public static Morador loginMorador(Morador morador) {
        try {
            return loginDao.loginMorador(morador);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar login de morador";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Funcionario LoginFuncionario(Funcionario funcionario) {
        try {
            return loginDao.loginFuncionario(funcionario);
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao realizar login de funcionário";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }
}
