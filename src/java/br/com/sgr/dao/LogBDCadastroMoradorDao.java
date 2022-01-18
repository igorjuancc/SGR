package br.com.sgr.dao;

import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import br.com.sgr.logbean.LogBDCadastroMorador;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class LogBDCadastroMoradorDao {

    /*
       Previsão recebimento taxa de condomínio (Por mês)
       
       Total de:
        - Pessoas cadastradas antes ou no inicio do mês que permaneceram o mês todo. (Fixo: 250,00)
        - Pessoas cadastradas antes ou no inicio do mês que saíram no meio do mês. (Calculo de 8.06 por dia)
        - Pessoas cadastradas no meio do mês que não sairam ou saíram depois do fim do mês. (Calculo de 8.06 por dia)
        - Pessoas cadastradas no meio do mês e saíram antes do fim do mês. (Calculo de 8.06 por dia)
        - Multas durante o mês
    
       Filtro por: 
        - Tipo de morador [0]Responsavel [1]Dependente
        - Data inicial e data final do mês
     */
    public List<Object[]> previsaoOrcamentoMes(int tipo, Date dataInicio, Date dataFim) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT 'TAXA DE CONDOMÍNIO' AS descricao, SUM(valor) AS total FROM ( "
                    + "SELECT id_morador_log_cadastro_morador, 250.00 AS valor FROM tb_log_cadastro_morador "
                    + "WHERE tipo_morador_log_cadastro_morador = :tipoMorador "
                    + "AND (data_aprovacao_log_cadastro_morador <= :primDiaMes) "
                    + "AND ((data_desligamento_log_cadastro_morador IS NULL) OR (data_desligamento_log_cadastro_morador >= :ultDiaMes)) "
                    + "UNION "
                    + "SELECT id_morador_log_cadastro_morador, (DATE_PART('day',data_desligamento_log_cadastro_morador - :primDiaMes) * 8.06) AS valor FROM tb_log_cadastro_morador "
                    + "WHERE tipo_morador_log_cadastro_morador = :tipoMorador "
                    + "AND (data_aprovacao_log_cadastro_morador <= :primDiaMes) "
                    + "AND (data_desligamento_log_cadastro_morador BETWEEN :primDiaMes AND :ultDiaMes) "
                    + "UNION "
                    + "SELECT id_morador_log_cadastro_morador, (DATE_PART('day',:ultDiaMes - data_aprovacao_log_cadastro_morador) * 8.06) AS valor FROM tb_log_cadastro_morador "
                    + "WHERE tipo_morador_log_cadastro_morador = :tipoMorador "
                    + "AND (data_aprovacao_log_cadastro_morador BETWEEN :primDiaMes AND :ultDiaMes) "
                    + "AND ((data_desligamento_log_cadastro_morador IS NULL) OR (data_desligamento_log_cadastro_morador >= :ultDiaMes)) "
                    + "UNION "
                    + "SELECT id_morador_log_cadastro_morador, (DATE_PART('day',data_desligamento_log_cadastro_morador - data_aprovacao_log_cadastro_morador) * 8.06) AS valor FROM tb_log_cadastro_morador "
                    + "WHERE tipo_morador_log_cadastro_morador = :tipoMorador "
                    + "AND (data_aprovacao_log_cadastro_morador BETWEEN :primDiaMes AND :ultDiaMes) "
                    + "AND (data_desligamento_log_cadastro_morador BETWEEN :primDiaMes AND :ultDiaMes)) AS t "
                    + "UNION "
                    + "SELECT 'MULTA' AS descricao, SUM(valor) FROM ( "
                    + "SELECT id_notificacao, data_emissao_notificacao, i.desc_infracao, "
                    + "CASE "
                    + "  WHEN i.clas_infracao = 0 THEN 32.00 "
                    + "  WHEN i.clas_infracao = 1 THEN 63.00 "
                    + "  WHEN i.clas_infracao = 2 THEN 125.00 "
                    + "  ELSE 0.00 "
                    + "END AS valor "
                    + "FROM tb_notificacao AS n "
                    + "INNER JOIN tb_infracao AS i ON n.id_infracao_notificacao = i.id_infracao "
                    + "WHERE tipo_notificacao = 1 "
                    + "AND (data_emissao_notificacao) BETWEEN :primDiaMes AND :ultDiaMes) AS t ");
            select.setParameter("tipoMorador", tipo);
            select.setParameter("primDiaMes", dataInicio);
            select.setParameter("ultDiaMes", dataFim);
            List<Object[]> resultado = select.list();
            return resultado;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar total orçamento previsto mês [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar total orçamento previsto mês [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Double anoPrimeiroMoradorCadastro(int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT DISTINCT extract(year FROM data_aprovacao_log_cadastro_morador) AS ano FROM tb_log_cadastro_morador WHERE tipo_morador_log_cadastro_morador = :tipoMorador ORDER BY ano LIMIT 1");
            select.setParameter("tipoMorador", tipo);
            Double resultado = (Double) select.uniqueResult();
            return resultado;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar ano de cadastro do primeiro morador por tipo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar ano de cadastro do primeiro morador por tipo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<LogBDCadastroMorador> listaLogCadastroMoradorTipo(int idMorador, int tipoMorador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM LogBDCadastroMorador l WHERE l.idMorador = :idMorador AND l.tipoMorador = :tipoMorador");
            select.setParameter("idMorador", idMorador);
            select.setParameter("tipoMorador", tipoMorador);
            List<LogBDCadastroMorador> logs = select.list();
            return logs;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar logs de cadastro do morador por tipo [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar logs de cadastro do morador por tipo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void inserirLog(LogBDCadastroMorador novoLog) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(novoLog);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao salvar log cadastro em BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao salvar log cadastro em BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public LogBDCadastroMorador ultimoLogMorador(int idMorador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM LogBDCadastroMorador l WHERE l.idMorador = :idMorador AND l.dataDesligamento IS NULL ORDER BY l.id DESC");
            select.setParameter("idMorador", idMorador);
            select.setMaxResults(1);
            LogBDCadastroMorador log = (LogBDCadastroMorador) select.uniqueResult();
            return log;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar ultimo log de morador no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar ultimo log de morador no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarLogBDCadastro(LogBDCadastroMorador log) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.update(log);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar log de cadastro de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar log de cadastro de morador [DAO]****", e);
        }
    }
}
