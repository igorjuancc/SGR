package br.com.sgr.dao;

import br.com.sgr.bean.Vaga;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class VagaDao {

    public void editarVaga(Vaga vaga) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(vaga);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar vaga de garagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar vaga de garagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Vaga> vagas() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Vaga v ORDER BY v.id");
            List<Vaga> listaVagas = select.list();
            return listaVagas;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar vagas [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar vagas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Vaga> vagasDeApto(int idApto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT v FROM Vaga v JOIN v.apartamento a WHERE a.id = :idApto ORDER BY v.id");
            select.setParameter("idApto", idApto);
            List<Vaga> listaVagas = select.list();
            return listaVagas;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar vagas de apto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar vagas de apto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Vaga> vagasDisponiveis() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Vaga v WHERE v.apartamento = NULL ORDER BY v.id");
            List<Vaga> listaVagas = select.list();
            return listaVagas;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar vagas disponiveis [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar vagas disponiveis [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Vaga vagaPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Vaga v WHERE v.id = :id");
            select.setParameter("id", id);
            Vaga vaga = (Vaga) select.uniqueResult();
            return vaga;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar vaga por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar vaga por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
