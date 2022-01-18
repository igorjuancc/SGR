package br.com.sgr.dao;

import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.sql.Connection;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;

public class RelatorioDao {

    public Connection connectionHibernate() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            SessionImpl sessionImpl = (SessionImpl) session;
            return sessionImpl.connection();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar conexão do hibernate [DAO]****", e);
        }
    }

    public void finalizaSessaoHibernate() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao finalizar sessão do hibernate [DAO]****", e);
        }
    }
}
