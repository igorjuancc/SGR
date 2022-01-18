package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Salao;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class SalaoDao {

    public void novoRegistroSalao(Salao salao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(salao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo registro de salão de festas [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo registro de salão de festas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Salao reservaSalaoPorData(Date dataReserva) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Salao s WHERE s.dataReserva = :data");
            select.setParameter("data", dataReserva);
            Salao salao = (Salao) select.uniqueResult();
            return salao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar reserva de salao por data [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar reserva de salao por data [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Salao> reservasSalao() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Salao s ORDER BY s.dataReserva");
            return select.list();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar reservas de salao de festas [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar reservas de salao de festas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
    
    public List<Date> datasReservaSalao() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT s.dataReserva FROM Salao s ORDER BY s.dataReserva");
            return select.list();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar datas de reservas de salao de festas [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar datas de reservas de salao de festas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarReservaSalao(Salao salao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(salao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar registro de salão [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar registro de salão [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarReservaSalao(Salao salao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(salao);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar reserva de salão de festas [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar reserva de salão de festas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalReservaSalao() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT s) FROM Salao s");
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de reservas de salão de festas [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de reservas de salão de festas [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de reservas de salão de festas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Salao> listaReservaSalaoFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Salao> listaSalao = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regSalao : listSelect) {
                Salao salao = new Salao();                
                salao.setId((int)regSalao[0]);
                salao.setDataReserva((Date) regSalao[1]);
                if (regSalao[2] != null) {
                    Morador morador = new Morador();
                    morador.setId((int) regSalao[2]);
                    if (regSalao[3] != null) {
                        morador.setNome((String) regSalao[3]);
                    }
                    if (regSalao[4] != null) {
                        Apartamento apto = new Apartamento();
                        apto.setId((int) regSalao[4]);
                        if (regSalao[5] != null) {
                            apto.setBloco((String) regSalao[5]);
                        }                
                        morador.setApartamento(apto);                        
                    }
                    salao.setMorador(morador);
                }
                if (regSalao[6] != null) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId((int) regSalao[6]);
                    if (regSalao[7] != null) {
                        funcionario.setNome((String) regSalao[7]);
                    }
                    salao.setFuncionario(funcionario);
                }
                listaSalao.add(salao);
            }            
            return listaSalao;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar reserva de salão com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar reserva de salão com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar reserva de salão com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
