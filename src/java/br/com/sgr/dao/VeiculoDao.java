package br.com.sgr.dao;

import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Veiculo;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class VeiculoDao {

    public void cadastrarVeiculo(Veiculo veiculo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(veiculo);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao salvar novo veiculo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao salvar novo veiculo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Veiculo veiculoPorPlaca(Veiculo veiculo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Veiculo WHERE placa_veiculo = :placa");
            select.setParameter("placa", veiculo.getPlaca());
            veiculo = (Veiculo) select.uniqueResult();
            return veiculo;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar veiculo por placa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar veiculo por placa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Veiculo> listaVeiculosDeMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Veiculo v WHERE id_morador = :idMorador");
            select.setParameter("idMorador", morador.getId());
            List<Veiculo> listaVeiculo = select.list();
            return listaVeiculo;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar veiculos do morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar veiculos do morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void removerVeiculo(Veiculo veiculo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(veiculo);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao remover veiculo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao remover veiculo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarVeiculo(Veiculo veiculo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(veiculo);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar veiculo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar veiculo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalVeiculosMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Veiculo v WHERE v.morador.id = :idMorador");
            count.setParameter("idMorador", morador.getId());
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de veiculos por morador [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de veiculos por morador [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de veiculos por morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalDeVeiculos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Veiculo v");
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de veiculos [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de veiculos [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de veiculos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Veiculo> listaVeiculoFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Veiculo> listaVeiculos = select.list();
            return listaVeiculos;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar veiculos com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar veiculos com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar veiculos com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
