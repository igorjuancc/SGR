package br.com.sgr.dao;

import br.com.sgr.bean.Noticia;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class NoticiaDao {

    public void cadastrarNoticia(Noticia noticia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(noticia);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova notícia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova notícia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Lista limitada para tela lista noticias
    public List<Noticia> listaNoticiaFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Noticia> listaNoticia = select.list();
            return listaNoticia;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar notícias com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar notícias com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar notícias com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalNoticias() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Noticia");
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problema buscar quantidade total de noticias [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problema buscar quantidade total de noticias [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema buscar quantidade total de noticias [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarNoticia(Noticia noticia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(noticia);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar notícia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar notícia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Noticia buscarNoticiaId(int id) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Noticia WHERE id_noticia = :id");
                select.setParameter("id", id);
                Noticia noticia = (Noticia) select.uniqueResult();
                return noticia;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar notícia por id no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar notícia por id no BD [DAO]****", e);
        }
    }

    public void atualizarNoticia(Noticia noticia) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.update(noticia);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar notícia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar notícia [DAO]****", e);
        }
    }
}
