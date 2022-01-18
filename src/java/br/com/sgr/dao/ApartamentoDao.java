package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ApartamentoDao {

    public List<Apartamento> apartamentosVagosPorBloco(String bloco) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT apto FROM Apartamento apto WHERE apto.bloco = :bloco AND apto.moradores IS EMPTY ORDER BY apto.id");
            select.setParameter("bloco", bloco);
            List<Apartamento> listaApartamento = select.list();
            return listaApartamento;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar apartamentos vagos por bloco [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar apartamentos vagos por bloco [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Apartamento> apartamentosOcupadosPorBloco(String bloco) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT apto FROM Apartamento apto WHERE apto.bloco = :bloco AND apto.moradores IS NOT EMPTY ORDER BY apto.id");
            select.setParameter("bloco", bloco);
            List<Apartamento> listaApartamento = select.list();
            return listaApartamento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar apartamentos ocupados por bloco [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar apartamentos ocupados por bloco [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Apartamento> apartamentosOcupados() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT apto FROM Apartamento apto JOIN FETCH apto.moradores m WHERE apto.moradores IS NOT EMPTY AND m.tipo = 1 ORDER BY apto.id");
            List<Apartamento> listaApartamento = select.list();
            return listaApartamento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar apartamentos ocupados [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar apartamentos ocupados [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Apartamento apartamentoPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Apartamento WHERE id_apartamento = :apto");
            select.setParameter("apto", id);
            Apartamento apartamento = (Apartamento) select.uniqueResult();
            return apartamento;
        } catch (HibernateException e) {
            throw new DaoException("****Problema buscar apartamento por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema buscar apartamento por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<String> blocosApartamentos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT apto.bloco FROM Apartamento apto ORDER BY apto.bloco");
            List<String> blocos = select.list();
            return blocos;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar blocos dos apartamentos [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar blocos dos apartamentos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
