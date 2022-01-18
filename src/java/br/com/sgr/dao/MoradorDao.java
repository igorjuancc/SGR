package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.Morador;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.math.BigInteger;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class MoradorDao {

    public void cadastrarMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(morador);
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao cadastrar morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao cadastrar morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(morador);
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao apagar morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao apagar morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Cadastrar morador que também é funcionário ou visitante
    public void cadastrarTbMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query inserir = session.createSQLQuery("INSERT INTO tb_morador (id_morador,tipo_morador,senha_morador,apartamento_morador,status_morador) VALUES (:id,:tipo,:senha,:apartamento,:status)");
            inserir.setParameter("id", morador.getId());
            inserir.setParameter("tipo", morador.getTipo());
            inserir.setParameter("senha", morador.getSenha());
            inserir.setParameter("apartamento", morador.getApartamento().getId());
            inserir.setParameter("status", morador.getStatus());
            inserir.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao cadastrar morador TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao cadastrar morador TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarTbMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query delete = session.createSQLQuery("DELETE FROM tb_morador WHERE id_morador = :id");
            delete.setParameter("id", morador.getId());
            delete.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao apagar morador TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao apagar morador TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Morador moradorPorCpf(String cpf) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador m WHERE m.cpf = :cpf");
            select.setParameter("cpf", cpf);
            Morador morador = (Morador) select.uniqueResult();
            return morador;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar morador por CPF [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar morador por CPF [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Morador moradorPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador m WHERE m.id = :id");
            select.setParameter("id", id);
            Morador morador = (Morador) select.uniqueResult();
            return morador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar morador por ID [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar morador por ID [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Morador buscaPorCpfEmailMorador(Morador morador) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Morador m WHERE m.cpf = :cpf AND m.email = :email");
                select.setParameter("cpf", morador.getCpf());
                select.setParameter("email", morador.getEmail());
                morador = (Morador) select.uniqueResult();
                return morador;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar morador por CPF e email no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar morador por CPF e email no BD [DAO]****", e);
        }
    }

    public List<Morador> listaNovosMoradores() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Morador m WHERE m.tipo = 1 AND m.status = 0 ORDER BY m.nome");
                List<Morador> listaMorador = select.list();
                return listaMorador;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar novos moradores [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar novos moradores [DAO]****", e);
        }
    }

    public List<Morador> responsaveisAtivos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador m WHERE m.tipo = 1 AND m.status = 3 ORDER BY m.nome");
            List<Morador> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar moradores responsaveis ativos [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar moradores responsaveis ativos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Morador> listaDependentes(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador m WHERE m.tipo = 2 AND apartamento_morador = :apto ORDER BY m.status,m.nome");
            select.setParameter("apto", morador.getApartamento().getId());
            List<Morador> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar dependentes [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar dependentes [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Morador> listaMoradorPorApto(Apartamento apto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT m FROM Morador m JOIN m.apartamento WHERE m.apartamento.id = :idApto");
            select.setParameter("idApto", apto.getId());
            List<Morador> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar moradores do apto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar moradores do apto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Morador> listaAcessoMoradores() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Morador m WHERE m.tipo = 1 AND m.status != 0 ORDER BY m.nome");
                List<Morador> listaMorador = select.list();
                return listaMorador;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar acesso de moradores [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar acesso de moradores [DAO]****", e);
        }
    }

    public List<Morador> listaMoradoresDesativados() throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Morador m WHERE m.status = 4 and m.apartamento = null ORDER BY m.nome");
                List<Morador> listaMorador = select.list();
                return listaMorador;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar moradores desativados [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar moradores desativados [DAO]****", e);
        }
    }

    public List<Morador> listaMoradoresResponsaveis() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Morador m WHERE m.tipo = 1 AND m.status IN (2,3) ORDER BY m.nome");
            List<Morador> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar moradores responsaveis [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar moradores responsaveis [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarStatusMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Morador m SET m.status = :status WHERE m.id = :id");
            update.setParameter("status", morador.getStatus());
            update.setParameter("id", morador.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar status morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar status morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(morador);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarEmailMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Morador m SET m.email = :email WHERE m.id = :id");
            update.setParameter("email", morador.getEmail());
            update.setParameter("id", morador.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar email de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar email de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarImagemMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Morador m SET m.imagem = :imagem WHERE m.id = :id");
            update.setParameter("imagem", morador.getImagem());
            update.setParameter("id", morador.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar imagem de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar imagem de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void alterarSenhaMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("UPDATE Morador m SET m.senha = :senha WHERE m.id = :id");
            select.setParameter("senha", morador.getSenha());
            select.setParameter("id", morador.getId());
            select.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar senha de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar senha de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalMoradoresAtivos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT m) FROM Morador m WHERE m.status != 0 AND m.status != 4");
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de moradores ativos [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de moradores ativos [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de moradores ativos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalMoradoresDesativados() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT m) FROM Morador m WHERE m.status = 0 OR m.status = 4");
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de moradores desativados [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de moradores desativados [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de moradores desativados [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalMoradoresPorStatus(int status) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT m) FROM Morador m WHERE m.status = :status");
            select.setParameter("status", status);
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de moradores por status [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de moradores por status [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de moradores por status [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Morador> listaMoradorFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Morador> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar moradores com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problema ao listar moradores com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar moradores com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalDependentes(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Morador m WHERE m.tipo = 2 AND apartamento_morador = :apto");
            count.setParameter("apto", morador.getApartamento().getId());
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de dependentes [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar quantidade total de dependentes [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de dependentes [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalListaDebitos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createSQLQuery("SELECT COUNT (*) AS total FROM ( "
                    + "SELECT id "
                    + "FROM ( "
                    + "(SELECT m.id_morador AS id, p.nome_pessoa AS nome, CONCAT(a.bloco_apartamento,'-',a.id_apartamento) AS apto "
                    + "FROM tb_morador AS m "
                    + "INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "INNER JOIN tb_apartamento AS a ON m.apartamento_morador = a.id_apartamento "
                    + "WHERE m.tipo_morador = 1 AND (m.status_morador = 2 OR m.status_morador = 3)) AS morador "
                    + "INNER JOIN "
                    + "(SELECT id_morador, COUNT(notificacao) AS notificacao, COUNT(boleto) AS outros, SUM(debitos_taxa) AS debitos_taxa FROM ( "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "INNER JOIN tb_boleto AS b ON n.id_boleto_notificacao = b.id_boleto "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE n.tipo_notificacao = 1 AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "WHERE n.tipo_notificacao = 1 AND n.id_boleto_notificacao IS NULL "
                    + "UNION "
                    + "SELECT id_morador_boleto AS morador, NULL AS notificacao, id_boleto AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_boleto AS b "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE (b.tipo_boleto != 1 AND b.tipo_boleto != 2) AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador, CAST(NULL AS INTEGER) AS notificacao, CAST(NULL AS INTEGER) AS outros, debitos_taxa FROM ( "
                    + "SELECT id_morador, ((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END AS meses, "
                    + "CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END, "
                    + "(((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END) "
                    + "- (CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END) AS debitos_taxa "
                    + "FROM "
                    + "( "
                    + "  SELECT "
                    + "	id_morador, "
                    + "    CAST(TO_CHAR(AGE(CURRENT_DATE, data_aprovacao_morador),'YY') AS INTEGER) AS ANOS, "
                    + "    CAST(TO_CHAR(AGE(CURRENT_DATE, data_aprovacao_morador),'MM') AS INTEGER) AS MESES, "
                    + "    CAST(TO_CHAR(AGE(CURRENT_DATE, data_aprovacao_morador),'DD') AS INTEGER) AS DIAS "
                    + "  FROM tb_morador AS m "
                    + "  INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "  WHERE tipo_morador = 1 AND (status_morador = 2 OR status_morador = 3) "
                    + "  ORDER BY p.nome_pessoa "
                    + ") AS mse "
                    + "LEFT JOIN ( "
                    + "SELECT id_morador_boleto, COUNT(id_financeiro) AS numero_boletos FROM tb_financeiro AS f "
                    + "INNER JOIN tb_boleto AS b ON f.id_boleto_financeiro = b.id_boleto "
                    + "WHERE tipo_boleto = 1 "
                    + "GROUP BY id_morador_boleto) AS blt "
                    + "ON mse.id_morador = blt.id_morador_boleto) AS t WHERE debitos_taxa > 0) AS debitos GROUP BY id_morador) AS d "
                    + "ON d.id_morador = morador.id) AS moradores_debitos "
                    + ") AS tot");
            BigInteger rst = (BigInteger) count.uniqueResult();
            return rst.intValue();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de moradores com débitos [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de moradores com débitos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Object[]> listaMoradoresDebitos(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Object[]> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar moradores com débitos e filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar moradores com débitos e filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar moradores com débitos e filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Object[]> listaDebitosMoradores(String idMoradores) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id, nome, bloco, apto, CAST (notificacao AS INTEGER) AS notificacao, CAST(outros AS INTEGER) AS outros, CAST(debitos_taxa AS INTEGER) AS debitos_taxa "
                    + "FROM ( "
                    + "(SELECT m.id_morador AS id, p.nome_pessoa AS nome, a.bloco_apartamento AS bloco, a.id_apartamento AS apto "
                    + "FROM tb_morador AS m "
                    + "INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "INNER JOIN tb_apartamento AS a ON m.apartamento_morador = a.id_apartamento "
                    + "WHERE m.tipo_morador = 1 AND (m.status_morador = 2 OR m.status_morador = 3 OR m.status_morador = 5)) AS morador "
                    + "INNER JOIN "
                    + "(SELECT id_morador, COUNT(notificacao) AS notificacao, COUNT(boleto) AS outros, SUM(debitos_taxa) AS debitos_taxa FROM ( "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "INNER JOIN tb_boleto AS b ON n.id_boleto_notificacao = b.id_boleto "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE n.tipo_notificacao = 1 AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador_notificacao AS id_morador, id_notificacao AS notificacao, CAST(NULL AS INTEGER) AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_notificacao AS n "
                    + "WHERE n.tipo_notificacao = 1 AND n.id_boleto_notificacao IS NULL "
                    + "UNION "
                    + "SELECT id_morador_boleto AS morador, NULL AS notificacao, id_boleto AS boleto, CAST(NULL AS INTEGER) AS debitos_taxa "
                    + "FROM tb_boleto AS b "
                    + "LEFT JOIN tb_financeiro AS f ON b.id_boleto = f.id_boleto_financeiro "
                    + "WHERE (b.tipo_boleto != 1 AND b.tipo_boleto != 2) AND f.id_boleto_financeiro IS NULL "
                    + "UNION "
                    + "SELECT id_morador, CAST(NULL AS INTEGER) AS notificacao, CAST(NULL AS INTEGER) AS outros, SUM(debitos_taxa) FROM ( "
                    + "SELECT id_morador, ((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END + 1 AS meses, "
                    + "CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END, "
                    + "(((ANOS * 12) + MESES) - CASE WHEN extract(DAY FROM CURRENT_DATE) <= 10 THEN 1 ELSE 0 END + 1) "
                    + "- (CASE WHEN (numero_boletos IS NULL OR numero_boletos < 0) THEN 0 ELSE numero_boletos END) AS debitos_taxa "
                    + "FROM "
                    + "( "
                    + "  SELECT "
                    + "	id_morador_log_cadastro_morador AS id_morador, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'YY') AS INTEGER) AS ANOS, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'MM') AS INTEGER) AS MESES, "
                    + "    CAST(TO_CHAR(AGE(CASE WHEN data_desligamento_log_cadastro_morador IS NULL THEN CURRENT_DATE ELSE data_desligamento_log_cadastro_morador END, data_aprovacao_log_cadastro_morador),'DD') AS INTEGER) AS DIAS "
                    + "  FROM tb_log_cadastro_morador AS log_m "
                    + "  INNER JOIN tb_morador AS m ON log_m.id_morador_log_cadastro_morador = m.id_morador "
                    + "  INNER JOIN tb_pessoa AS p ON m.id_morador = p.id_pessoa "
                    + "  WHERE tipo_morador = 1 AND (status_morador = 2 OR status_morador = 3 OR status_morador = 5) "
                    + "  ORDER BY p.nome_pessoa "
                    + ") AS mse "
                    + "LEFT JOIN ( "
                    + "SELECT id_morador_boleto, COUNT(id_financeiro) AS numero_boletos FROM tb_financeiro AS f "
                    + "INNER JOIN tb_boleto AS b ON f.id_boleto_financeiro = b.id_boleto "
                    + "WHERE tipo_boleto = 1 "
                    + "GROUP BY id_morador_boleto) AS blt "
                    + "ON mse.id_morador = blt.id_morador_boleto) AS t WHERE debitos_taxa > 0 GROUP BY id_morador) AS debitos GROUP BY id_morador) AS d "
                    + "ON d.id_morador = morador.id) AS moradores_debitos WHERE id IN (" + idMoradores + ")");
            List<Object[]> listaMorador = select.list();
            return listaMorador;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar lista de débitos de moradores [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar lista de débitos de moradores [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar lista de débitos de moradores [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
