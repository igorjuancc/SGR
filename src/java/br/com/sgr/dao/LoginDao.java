package br.com.sgr.dao;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class LoginDao {

    public Morador loginMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador WHERE email_pessoa = :email AND senha_morador = :senha AND tipo_morador = 1");
            select.setParameter("email", morador.getEmail());
            select.setParameter("senha", morador.getSenha());
            if (select.uniqueResult() != null) {
                morador = (Morador) select.uniqueResult();
            }
            return morador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao realizar login de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao realizar login de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Funcionario loginFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Funcionario WHERE cpf_pessoa = :cpf AND senha_funcionario = :senha");
            select.setParameter("cpf", funcionario.getCpf());
            select.setParameter("senha", funcionario.getSenha());
            if (select.uniqueResult() != null) {
                funcionario = (Funcionario) select.uniqueResult();
            }
            return funcionario;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao realizar login de funcionário [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao realizar login de funcionário [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
