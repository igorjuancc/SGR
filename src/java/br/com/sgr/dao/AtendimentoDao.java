package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Atendimento;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.TipoAtendimento;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class AtendimentoDao {

    public void novoAtendimento(Atendimento atendimento) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(atendimento);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo atendimento [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo atendimento [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarAtendimento(Atendimento atendimento) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.delete(atendimento);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao deletar atendimento [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao deletar atendimento [DAO]****", e);
        }
    }

    public List<Atendimento> atendimentosPorNotificacao(int idNotifi) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            Query select = session.createQuery(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f INNER JOIN a.notificacoes n WHERE n.id = :idNotifi ORDER BY a.dataAbertura DESC");
            select.setParameter("idNotifi", idNotifi);
            List<Atendimento> listaAtendimento = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regAtendimento : listSelect) {
                Atendimento atendimento = new Atendimento();
                TipoAtendimento tipo = new TipoAtendimento();
                Morador morador = new Morador();
                Apartamento apto = new Apartamento();
                atendimento.setId((int) regAtendimento[0]);
                atendimento.setStatus((int) regAtendimento[1]);
                atendimento.setDescricao((String) regAtendimento[2]);
                tipo.setId((int) regAtendimento[3]);
                tipo.setDescricao((String) regAtendimento[4]);
                atendimento.setTipo(tipo);
                atendimento.setDataAbertura((Date) regAtendimento[5]);
                if (regAtendimento[6] != null) {
                    atendimento.setDataFechamento((Date) regAtendimento[6]);
                }
                morador.setId((int) regAtendimento[7]);
                morador.setNome((String) regAtendimento[8]);
                apto.setId((int) regAtendimento[9]);
                apto.setBloco((String) regAtendimento[10]);
                morador.setApartamento(apto);
                if (regAtendimento[11] != null) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId((int) regAtendimento[11]);
                    funcionario.setNome((String) regAtendimento[12]);
                    atendimento.setFuncionario(funcionario);
                }
                atendimento.setMorador(morador);
                atendimento.setTipo(tipo);
                listaAtendimento.add(atendimento);
            }
            return listaAtendimento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar atendimentos de notificações [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar atendimentos de notificações [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarAtendimento(Atendimento atendimento) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(atendimento);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar atendimento [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar atendimento [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Atendimento atendimentoPorId(int idAtendimento) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT a.id, a.status, a.descricao, a.tipo.id, a.tipo.descricao, a.dataAbertura, a.dataFechamento, "
                    + "a.morador.id, a.morador.nome, a.morador.apartamento.id, a.morador.apartamento.bloco, f.id, f.nome";
            Query select = session.createQuery(dadosBusca + " FROM Atendimento a LEFT JOIN a.funcionario f WHERE a.id = :id");
            select.setParameter("id", idAtendimento);
            Atendimento atendimento = new Atendimento();
            Object[] atendSelect = (Object[]) select.uniqueResult();
            if (atendSelect != null) {
                TipoAtendimento tipo = new TipoAtendimento();
                Morador morador = new Morador();
                Apartamento apto = new Apartamento();
                atendimento.setId((int) atendSelect[0]);
                atendimento.setStatus((int) atendSelect[1]);
                atendimento.setDescricao((String) atendSelect[2]);
                tipo.setId((int) atendSelect[3]);
                tipo.setDescricao((String) atendSelect[4]);
                atendimento.setTipo(tipo);
                atendimento.setDataAbertura((Date) atendSelect[5]);
                if (atendSelect[6] != null) {
                    atendimento.setDataFechamento((Date) atendSelect[6]);
                }
                morador.setId((int) atendSelect[7]);
                morador.setNome((String) atendSelect[8]);
                apto.setId((int) atendSelect[9]);
                apto.setBloco((String) atendSelect[10]);
                morador.setApartamento(apto);
                if (atendSelect[11] != null) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId((int) atendSelect[11]);
                    funcionario.setNome((String) atendSelect[12]);
                    atendimento.setFuncionario(funcionario);
                }
                atendimento.setMorador(morador);
                atendimento.setTipo(tipo);
            }
            return atendimento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar atendimento por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar atendimento por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Atendimento buscaAbertoTipoAtendimentoMorador(Atendimento atendimento) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Atendimento a WHERE a.status = 0 AND a.tipo.id = :tipo AND a.morador.id = :idMorador");
                select.setParameter("tipo", atendimento.getTipo().getId());
                select.setParameter("idMorador", atendimento.getMorador().getId());
                Atendimento atendimentoBusca = (Atendimento) select.uniqueResult();
                return atendimentoBusca;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar atendimento aberto por tipo de morador[Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar atendimento aberto pot tipo de morador[DAO]****", e);
        }
    }

    public int totalAtendimentosPorStatus(int status) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Atendimento a WHERE a.status = :status");
            count.setParameter("status", status);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos por status [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos por status [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos por status [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalAtendimentosMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Atendimento a WHERE a.morador.id = :idMorador");
            count.setParameter("idMorador", morador.getId());
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalAtendimentosMoradorTipo(Morador morador, int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Atendimento a WHERE a.morador.id = :idMorador AND a.tipo.id = :tipo");
            count.setParameter("idMorador", morador.getId());
            count.setParameter("tipo", tipo);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador por tipo [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador por tipo [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de atendimentos de morador por tipo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalDeAtendimentosPorTipo(int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Atendimento a WHERE a.tipo.id = :tipo");
            count.setParameter("tipo", tipo);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de atendimentos por tipo [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de atendimentos por tipo [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de atendimentos por tipo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Atendimento> listaAtendimentoFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Atendimento> listaAtendimento = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regAtendimento : listSelect) {
                Atendimento atendimento = new Atendimento();
                TipoAtendimento tipo = new TipoAtendimento();
                Morador morador = new Morador();
                Apartamento apto = new Apartamento();
                atendimento.setId((int) regAtendimento[0]);
                atendimento.setStatus((int) regAtendimento[1]);
                atendimento.setDescricao((String) regAtendimento[2]);
                tipo.setId((int) regAtendimento[3]);
                tipo.setDescricao((String) regAtendimento[4]);
                atendimento.setTipo(tipo);
                atendimento.setDataAbertura((Date) regAtendimento[5]);
                if (regAtendimento[6] != null) {
                    atendimento.setDataFechamento((Date) regAtendimento[6]);
                }
                morador.setId((int) regAtendimento[7]);
                morador.setNome((String) regAtendimento[8]);
                apto.setId((int) regAtendimento[9]);
                apto.setBloco((String) regAtendimento[10]);
                morador.setApartamento(apto);
                if (regAtendimento[11] != null) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId((int) regAtendimento[11]);
                    funcionario.setNome((String) regAtendimento[12]);
                    atendimento.setFuncionario(funcionario);
                }
                atendimento.setMorador(morador);
                atendimento.setTipo(tipo);
                listaAtendimento.add(atendimento);
            }
            return listaAtendimento;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar atendimentos com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar atendimentos com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar atendimentos com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
