package br.com.sgr.dao;

import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import br.com.sgr.logbean.LogBD;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class LogBDDao {

    public void inserirLog(LogBD log) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.save(log);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao salvar log em BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao salvar log em BD [DAO]****", e);
        }
    }

    public List<Integer> listaAnoObjetoOp(int obj, int op) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createSQLQuery("SELECT DISTINCT extract(year FROM data_log_sistema) AS ano FROM tb_log_sistema WHERE tipo_objeto_log_sistema = :obj AND id_operacao_log_sistema = :op GROUP BY 1 ORDER BY ano DESC");
                select.setParameter("obj", obj);
                select.setParameter("op", op);
                List<Double> resultado = select.list();
                List<Integer> listaAno = new ArrayList<>();

                for (Double o : resultado) {
                    int ano = o.intValue();
                    listaAno.add(ano);
                }
                return listaAno;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar ano de LogBD objeto/operacao [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problema ao listar ano de LogBD objeto/operacao [DAO]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar ano de LogBD objeto/operacao [DAO]****", e);
        }
    }
}
