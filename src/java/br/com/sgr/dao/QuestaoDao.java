package br.com.sgr.dao;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.OpcQuestao;
import br.com.sgr.bean.Questao;
import br.com.sgr.bean.Voto;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class QuestaoDao {

    public void novaQuestao(Questao questao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(questao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova questao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova questao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarQuestao(Questao questao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(questao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar questao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar questao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarQuestao(Questao questao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(questao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar questao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar questao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarOpcQuestao(OpcQuestao opcQuestao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(opcQuestao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar opção de questao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar opção de questao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Questao> questoesAssembleia(int idAssembleia) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Questao q WHERE q.assembleia.id = :idAssembleia");
            select.setParameter("idAssembleia", idAssembleia);
            List<Questao> questoes = select.list();
            return questoes;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar questoes de assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar questoes de assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
    
    public Questao questaoPorId(int idQuestao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Questao q WHERE q.id = :idQuestao");
            select.setParameter("idQuestao", idQuestao);
            Questao questao = (Questao) select.uniqueResult();
            return questao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar questao por id [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar questao por id [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<OpcQuestao> listaOpcQuestao(Questao questao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT o.id, o.descricao FROM OpcQuestao o WHERE o.questao.id = :idQuestao");
            select.setParameter("idQuestao", questao.getId());
            List<Object[]> listSelect = select.list();
            List<OpcQuestao> opcQuestao = new ArrayList<>();
            for (Object[] regOpcQuestao : listSelect) {
                OpcQuestao opc = new OpcQuestao();
                opc.setId((int)regOpcQuestao[0]);
                opc.setDescricao((String)regOpcQuestao[1]);
                opc.setQuestao(questao);
                opcQuestao.add(opc);
            }
            return opcQuestao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar opções de questoes de assembleia [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar opções de questoes de assembleia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void novoVoto(Voto voto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query insert = session.createSQLQuery("INSERT INTO tb_voto (id_morador, id_opc_questao) VALUES (:idMorador, :idOpcQuestao)");
            insert.setParameter("idMorador", voto.getMorador().getId());
            insert.setParameter("idOpcQuestao", voto.getOpcao().getId());
            insert.executeUpdate();            
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo voto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo voto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarVoto(Voto voto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createSQLQuery("UPDATE tb_voto SET id_opc_questao = :idOpcQuestao WHERE id_voto = :idVoto");
            update.setParameter("idVoto", voto.getId());
            update.setParameter("idOpcQuestao", voto.getOpcao().getId());
            update.executeUpdate();  
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar voto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar voto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
    
    public List<Voto> listaVotosOpcQuestao(OpcQuestao opc) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT v.id, v.morador.id, v.morador.status FROM Voto v WHERE v.opcao.id = :idOpcQuestao");
            select.setParameter("idOpcQuestao", opc.getId());
            List<Object[]> listSelect = select.list();
            List<Voto> votos = new ArrayList<>();
            for (Object[] regVoto : listSelect) {
                Voto v = new Voto();
                Morador m = new Morador();
                v.setId((int)regVoto[0]);
                m.setId((int)regVoto[1]);
                m.setStatus((int)regVoto[2]);
                v.setMorador(m);
                v.setOpcao(opc);
                votos.add(v);
            }
            return votos;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar votos de opções de questoes [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar votos de opções de questoes [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
