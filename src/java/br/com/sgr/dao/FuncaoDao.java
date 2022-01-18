package br.com.sgr.dao;

import br.com.sgr.bean.Funcao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class FuncaoDao {

    public List<Funcao> funcaoLista() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT f FROM Funcao f ORDER BY f.id");
            List<Funcao> listaFuncao = select.list();
            return listaFuncao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar funções de funcionários [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar funções de funcionários [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Funcao funcaoPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Funcao f WHERE f.id = :id");
            select.setParameter("id", id);
            Funcao funcao = (Funcao) select.uniqueResult();
            return funcao;
        } catch (HibernateException e) {
            throw new DaoException("****Problema buscar função por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema buscar função por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
