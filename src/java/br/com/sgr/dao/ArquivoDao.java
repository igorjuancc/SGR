package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ArquivoDao {

    public void apagarArquivo(Arquivo arquivo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(arquivo);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar arquivo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar arquivo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void salvarArquivo(Arquivo arquivo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(arquivo);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao salvar arquivo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao salvar arquivo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Arquivo> listaArquivoQuestao(int idQuestao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Arquivo a WHERE a.questao.id = :idQuestao");
            select.setParameter("idQuestao", idQuestao);
            List<Arquivo> arquivos = select.list();
            return arquivos;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar arquivos de questão [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar arquivos de questão [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Arquivo> listaArquivoNotificacao(int idNotificacao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT a.id, a.extensao, a.descricao FROM Arquivo a WHERE a.notificacao.id = :idNotificacao");
            select.setParameter("idNotificacao", idNotificacao);
            List<Object[]> listSelect = select.list();
            List<Arquivo> arquivos = new ArrayList<>();
            for (Object[] regArquivos : listSelect) {
                Arquivo a = new Arquivo();
                Notificacao n = new Notificacao();
                n.setId(idNotificacao);
                a.setId((int) regArquivos[0]);
                a.setExtensao((String) regArquivos[1]);
                a.setDescricao((String) regArquivos[2]);
                a.setNotificacao(n);
                arquivos.add(a);
            }
            return arquivos;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar arquivos de notificacao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar arquivos de notificacao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
