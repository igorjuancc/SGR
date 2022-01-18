package br.com.sgr.dao;

import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class CategoriaFinanceiroDao {

    public List<CategoriaFinanceiro> categoriasFinanceiro() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM CategoriaFinanceiro c ORDER BY c.descricao");
            List<CategoriaFinanceiro> listaCategoria = select.list();
            return listaCategoria;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar categorias financeiras [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar categorias financeiras [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public CategoriaFinanceiro categoriaFinanceiroPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM CategoriaFinanceiro c WHERE c.id = :id");
            select.setParameter("id", id);
            CategoriaFinanceiro categoria = (CategoriaFinanceiro) select.uniqueResult();
            return categoria;
        } catch (HibernateException e) {
            throw new DaoException("****Problema buscar categoria financeira por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema buscar categoria financeira por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
