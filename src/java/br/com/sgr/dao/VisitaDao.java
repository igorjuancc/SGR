package br.com.sgr.dao;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Visita;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class VisitaDao {

    public void cadastraVisita(Visita visita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(visita);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova visita [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova visita [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalVisitaPorApto(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT v) FROM Visita v WHERE v.apartamento = :apto AND v.dataInicio >= :dataInicio");
            select.setParameter("apto", morador.getApartamento());
            select.setParameter("dataInicio", morador.getDataCadastro());
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de visitas por apto [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de visitas por apto [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de visitas por apto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visita> listaVisitaFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Visita> listaVisita = select.list();
            return listaVisita;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visita com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar visita com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visita com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visita> listaVisitaPorVisitante(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT v FROM Visita v INNER JOIN v.visitantes vis WHERE vis = :visitante AND CURRENT_DATE >= v.dataInicio AND CURRENT_DATE <= v.dataFim ORDER BY v.dataInicio DESC");
            select.setParameter("visitante", visitante);
            select.setMaxResults(5);
            List<Visita> listaVisita = select.list();
            return listaVisita;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitas de visitante [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar visitas de visitante [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitas de visitante [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Boolean apagarVisita(Visita visita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query delete = session.createQuery("DELETE Visita v WHERE v.id = :id");
            delete.setParameter("id", visita.getId());
            int rows = delete.executeUpdate();
            return rows != 0;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar visita [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao apagar visita [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar visita [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizaVisita(Visita visita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(visita);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar visita [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar visita [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
