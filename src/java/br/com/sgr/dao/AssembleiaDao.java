package br.com.sgr.dao;

import br.com.sgr.bean.Assembleia;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class AssembleiaDao {

    public void novaAssembleia(Assembleia assembleia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(assembleia);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarAssembleia(Assembleia assembleia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(assembleia);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Assembleia assembleiaAberta() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Assembleia a WHERE a.encerramento != 2");
            Assembleia assembleia = (Assembleia) select.uniqueResult();
            return assembleia;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar assembleia em aberto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar assembleia em aberto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Assembleia assembleiaPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Assembleia a WHERE a.id = :id");
            select.setParameter("id", id);
            Assembleia assembleia = (Assembleia) select.uniqueResult();
            return assembleia;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar assembleia por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar assembleia por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarStatusAssembleia(Assembleia assembleia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Assembleia a SET a.encerramento = :status WHERE a.id = :id");
            update.setParameter("status", assembleia.getEncerramento());
            update.setParameter("id", assembleia.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar status de assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar status de assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarParecerAssembleia(Assembleia assembleia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Assembleia a SET a.parecer = :parecer WHERE a.id = :id");
            update.setParameter("parecer", assembleia.getParecer());
            update.setParameter("id", assembleia.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar parecer de assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar parecer de assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalAssembleia() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Assembleia");
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de assembleias [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de assembleias [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de assembleias [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Assembleia> assembleiaFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Assembleia> listaAssembleia = select.list();
            return listaAssembleia;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar assembleias com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar assembleias com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar assembleias com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
