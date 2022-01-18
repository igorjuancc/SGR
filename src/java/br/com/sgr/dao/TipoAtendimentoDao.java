package br.com.sgr.dao;

import br.com.sgr.bean.TipoAtendimento;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class TipoAtendimentoDao {

    public List<TipoAtendimento> listaTipoAtendimento() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM TipoAtendimento");
            List<TipoAtendimento> listaTipoAtendimento = select.list();
            return listaTipoAtendimento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar tipo de atendimento [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar tipo de atendimento [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public TipoAtendimento tipoAtendimentoPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM TipoAtendimento t WHERE t.id = :id");
            select.setParameter("id", id);
            TipoAtendimento tipoAtendimento = (TipoAtendimento) select.uniqueResult();
            return tipoAtendimento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar tipo atendimento por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar tipo atendimento por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
