package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Financeiro;
import br.com.sgr.bean.Funcao;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Infracao;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Notificacao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class NotificacaoDao {

    public List<Infracao> listaInfracoes() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Infracao i ORDER BY i.descricao");
            List<Infracao> listaInfracao = select.list();
            return listaInfracao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar infrações [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar infrações [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Infracao infracaoPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Infracao i WHERE i.id = :id");
            select.setParameter("id", id);
            Infracao infracao = (Infracao) select.uniqueResult();
            return infracao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar infração por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar infração por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Notificacao> listaNotificacaoMorador(int idMorador) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Notificacao n WHERE n.morador.id = :idMorador ORDER BY n.dataEmissao");
                select.setParameter("idMorador", idMorador);
                List<Notificacao> listaNotificacao = select.list();
                return listaNotificacao;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar notificações de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar notificações de morador [DAO]****", e);
        }
    }

    public void cadastrarInfracao(Infracao infracao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(infracao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova infracao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova infracao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarInfracao(Infracao infracao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(infracao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar infracao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar infracao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarInfracao(Infracao infracao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(infracao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar infracao [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar infracao [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int numNotifiPorInfracao(int idInfracao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT (n.id) FROM Notificacao n INNER JOIN n.infracao i WHERE i.id = :idInfracao ");
            count.setParameter("idInfracao", idInfracao);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar numero de notificações por infração [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar numero de notificações por infração [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar numero de notificações por infração [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void novaNotificacao(Notificacao notificacao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(notificacao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar nova notificação [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar nova notificação [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarNotificacao(Notificacao notificacao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(notificacao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar notificação [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar notificação [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarNotificacao(Notificacao notificacao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(notificacao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar notificação [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar notificação [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalNotificacao() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Notificacao");
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalNotificacaoMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Notificacao n WHERE n.morador.id = :idMorador");
            count.setParameter("idMorador", morador.getId());
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações de morador [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações de morador [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de notificações de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Notificacao> notificacaoFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Object[]> listSelect = select.list();
            List<Notificacao> listaNotificacao = new ArrayList<>();
            for (Object[] regNot : listSelect) {
                Notificacao n = new Notificacao();
                Morador m = new Morador();
                Funcionario f = new Funcionario();
                Infracao i = new Infracao();
                Apartamento a = new Apartamento();
                Funcao fun = new Funcao();
                n.setId((int) regNot[0]);
                n.setTipo((int) regNot[1]);
                n.setDataReferencia((Date) regNot[2]);
                n.setDataEmissao((Date) regNot[3]);
                n.setDescricao((String) regNot[4]);
                m.setId((int) regNot[5]);
                m.setNome((String) regNot[6]);
                m.setDataCadastro((Date) regNot[22]);
                m.setCpf((String) regNot[23]);
                a.setId((int) regNot[20]);
                a.setBloco((String) regNot[21]);
                f.setId((int) regNot[7]);
                f.setNome((String) regNot[8]);
                i.setId((int) regNot[9]);
                i.setClassificacao((int) regNot[10]);
                i.setDescricao((String) regNot[11]);
                if (regNot[12] != null) {
                    BoletoSGR b = new BoletoSGR();
                    b.setId((int) regNot[12]);
                    b.setTipo((int) regNot[13]);
                    b.setDataEmissao((regNot[14] != null) ? (Date) regNot[14] : null);
                    b.setDataVencimento((regNot[15] != null) ? (Date) regNot[15] : null);
                    b.setDataReferencia((regNot[16] != null) ? (Date) regNot[16] : null);
                    b.setValorBoleto((regNot[17] != null) ? (double) regNot[17] : null);
                    b.setValorMulta((regNot[18] != null) ? (double) regNot[18] : null);
                    if (regNot[19] != null) {
                        Financeiro fin = new Financeiro();
                        fin.setId((int) regNot[19]);
                        b.setFinanceiro(fin);
                    }
                    b.setMorador(m);
                    b.setNotificacao(n);
                    n.setBoleto(b);
                }
                fun.setId(1);
                f.setFuncao(fun);
                m.setApartamento(a);
                n.setMorador(m);
                n.setFuncionario(f);
                n.setInfracao(i);
                listaNotificacao.add(n);
            }
            return listaNotificacao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar notificações com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar notificações com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar notificações com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
