package br.com.sgr.dao;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class FuncionarioDao {

    public Funcionario funcionarioPorCpf(String cpf) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Funcionario WHERE cpf_pessoa = :cpf");
            select.setParameter("cpf", cpf);
            Funcionario funcionario = (Funcionario) select.uniqueResult();
            return funcionario;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar funcionario por CPF no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar funcionario por CPF no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Funcionario funcionarioPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Funcionario f WHERE f.id = :id");
            select.setParameter("id", id);
            Funcionario funcionario = (Funcionario) select.uniqueResult();
            return funcionario;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar funcionario por ID no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar funcionario por ID no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(funcionario);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo funcionario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Cadastrar funcionario que também é morador ou visitante
    public Boolean cadastrarTbFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query inserir = session.createSQLQuery("INSERT INTO tb_funcionario (id_funcionario,id_funcao,empresa_funcionario,senha_funcionario,status_funcionario) VALUES (:id,:funcao,:empresa,:senha,:status)");
            inserir.setParameter("id", funcionario.getId());
            inserir.setParameter("funcao", funcionario.getFuncao().getId());
            inserir.setParameter("empresa", funcionario.getEmpresa());
            inserir.setParameter("senha", funcionario.getSenha());
            inserir.setParameter("status", funcionario.getStatus());
            int rows = inserir.executeUpdate();
            return rows != 0;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo funcionario em TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo funcionario em TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarTbFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query delete = session.createSQLQuery("DELETE FROM tb_funcionario WHERE id_funcionario = :id");
            delete.setParameter("id", funcionario.getId());
            delete.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao apagar funcionario TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao apagar funcionario TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarFuncionario(Funcionario funcionario) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.update(funcionario);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar dados de funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar dados de funcionario [DAO]****", e);
        }
    }

    public Boolean atualizarProfissionalFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Funcionario f SET f.funcao = :funcao, f.empresa = :empresa, f.status = :status, f.senha = :senha WHERE f.id = :id");
            update.setParameter("funcao", funcionario.getFuncao());
            update.setParameter("empresa", funcionario.getEmpresa());
            update.setParameter("status", funcionario.getStatus());
            update.setParameter("senha", funcionario.getSenha());
            update.setParameter("id", funcionario.getId());
            int rows = update.executeUpdate();
            return rows != 0;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar dados profissionais funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar dados profissionais funcionario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Boolean alterarSenhaFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("UPDATE Funcionario f SET f.senha = :senha WHERE f.id = :id");
            select.setParameter("senha", funcionario.getSenha());
            select.setParameter("id", funcionario.getId());
            int rows = select.executeUpdate();
            return rows != 0;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar senha de funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar senha de funcionario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Boolean atualizarStatusFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("UPDATE Funcionario f SET f.status = :status WHERE f.id = :id");
            select.setParameter("status", funcionario.getStatus());
            select.setParameter("id", funcionario.getId());
            int rows = select.executeUpdate();
            return rows != 0;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar status de funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar status de funcionario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Funcionario> listaFuncionario() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Funcionario f ORDER BY f.funcao");
                List<Funcionario> listaFuncionario = select.list();
                return listaFuncionario;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar funcionarios [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar funcionarios [DAO]****", e);
        }
    }

    public void removerFuncionario(Funcionario funcionario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(funcionario);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar funcionario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar funcionario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalFuncionarioStatus(int status) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Funcionario f WHERE f.status = :status");
            count.setParameter("status", status);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de funcionarios por status [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de funcionarios por status [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de funcionarios por status [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Funcionario> funcionarioFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Funcionario> listaFuncionario = select.list();
            return listaFuncionario;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar funcionarios com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar funcionarios com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar funcionarios com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
