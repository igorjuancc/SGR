package br.com.sgr.dao;

import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.CategoriaFinanceiro;
import br.com.sgr.bean.Financeiro;
import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class FinanceiroDao {

    public void novoRegistroFinanceiro(Financeiro financeiro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(financeiro);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo registro financeiro [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo registro financeiro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarRegistroFinanceiro(Financeiro financeiro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(financeiro);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar registro financeiro [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar registro financeiro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarRegistroFinanceiro(Financeiro financeiro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(financeiro);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar registro financeiro [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar registro financeiro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Financeiro> mesAnoRegistroFinanceiro(int ano, int mes) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT f.id, f.tipo, f.valor, f.descricao, f.dataRegistro, c.id, c.descricao, func.id, func.nome ";
            Query select = session.createQuery(dadosBusca + " FROM Financeiro f LEFT JOIN f.categoria c LEFT JOIN f.funcionario func WHERE year(f.dataRegistro) = :ano AND month(f.dataRegistro) = :mes ORDER BY f.dataRegistro DESC");
            select.setParameter("ano", ano);
            select.setParameter("mes", mes);
            List<Object[]> listSelect = select.list(); 
            List<Financeiro> listaFinanceiro = new ArrayList<>();
            for (Object[] regFinanceiro : listSelect) {
                Financeiro f = new Financeiro();
                CategoriaFinanceiro c = new CategoriaFinanceiro();
                Funcionario func = new Funcionario();
                f.setId((int) regFinanceiro[0]);
                f.setTipo((int) regFinanceiro[1]);
                f.setValor((double) regFinanceiro[2]);
                f.setDescricao((String) regFinanceiro[3]);
                f.setDataRegistro((Date) regFinanceiro[4]);
                c.setId((int) regFinanceiro[5]);
                c.setDescricao((String) regFinanceiro[6]);
                func.setId((int) regFinanceiro[7]);
                func.setNome((String) regFinanceiro[8]);
                f.setCategoria(c);
                f.setFuncionario(func);
                listaFinanceiro.add(f);
            }
            return listaFinanceiro;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar registros financeiro por ano e mes [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar registros financeiro por ano e mes [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Integer> listaAnoRegistro(int tipoReceita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT DISTINCT extract(year FROM data_financeiro) AS ano FROM tb_financeiro WHERE tipo_financeiro = :tipoReceita GROUP BY 1 ORDER BY ano DESC");
            select.setParameter("tipoReceita", tipoReceita);
            List<Double> resultado = select.list();
            List<Integer> listaAno = new ArrayList<>();

            for (Double o : resultado) {
                int ano = o.intValue();
                listaAno.add(ano);
            }
            return listaAno;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar ano de registros financeiros [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar ano de registros financeiros [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Integer> listaMesAnoRegistro(int ano, int tipoReceita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT DISTINCT extract(month FROM data_financeiro) AS mes FROM tb_financeiro WHERE extract (year FROM data_financeiro) = :ano AND tipo_financeiro = :tipoReceita GROUP BY 1 ORDER BY mes");
            select.setParameter("ano", ano);
            select.setParameter("tipoReceita", tipoReceita);
            List<Double> resultado = select.list();
            List<Integer> listaMes = new ArrayList<>();

            for (Double o : resultado) {
                int mes = o.intValue();
                listaMes.add(mes);
            }
            return listaMes;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar mes de registros financeiros [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problema ao listar mes de registros financeiros [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar mes de registros financeiros [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalRegistroFinanceiroTipo(int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM Financeiro f WHERE f.tipo = :tipo");
            count.setParameter("tipo", tipo);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de registros financeiros por tipo [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de registros financeiros por tipo [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de registros financeiros por tipo [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Financeiro> listaRegistroFinanceiroFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Object[]> resultado = select.list();
            List<Financeiro> listaFinanceiro = new ArrayList<>();
            for (Object[] o : resultado) {
                Financeiro f = new Financeiro();
                BoletoSGR b = new BoletoSGR();
                Morador m = new Morador();
                
                f.setId((int) o[0]);
                f.setDescricao(o[1].toString());
                f.setDataRegistro((Date) o[2]);
                f.setValor((double) o[3]);
                Funcionario func = (Funcionario) o[4];
                CategoriaFinanceiro c = (CategoriaFinanceiro) o[5];
                
                b.setId((int) o[6]);
                b.setTipo((int) o[7]);
                b.setDataEmissao((Date) o[8]);
                b.setDataVencimento((Date) o[9]);
                b.setDataReferencia((Date) o[10]);
                b.setValorBoleto((double) o[11]);
                b.setValorMulta((double) o[12]);         
                
                m.setId((int) o[13]);
                m.setNome((String) o[14]);
                
                b.setMorador(m);
                b.setFinanceiro(f);
                f.setCategoria(c);
                f.setFuncionario(func);
                f.setBoleto(b);
                listaFinanceiro.add(f);
            }
            return listaFinanceiro;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar registros financeiros com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar registros financeiros com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar registros financeiros com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Total financeiro (Receitas e Despesas) para relatorio [Total/Categoria/Ano]
    public List<Object[]> totalFinanceiro(int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT c.desc_categoria AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "INNER JOIN tb_categoria_financeiro AS c ON c.id_categoria_financeiro = f.id_categoria_financeiro "
                    + "WHERE f.tipo_financeiro = :tipo GROUP BY c.desc_categoria "
                    + "UNION "
                    + "SELECT TO_CHAR(EXTRACT(YEAR FROM f.data_financeiro),'9999') AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo GROUP BY EXTRACT(YEAR FROM f.data_financeiro) "
                    + "UNION "
                    + "SELECT 'TOTAL' AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo "
                    + "ORDER BY descricao ");
            select.setParameter("tipo", tipo);
            List<Object[]> resultado = select.list();
            return resultado;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar total financeiro [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar total financeiro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Total financeiro ano(Receitas e Despesas) para relatorio 
    public List<Object[]> totalFinanceiroAno(int tipo, int ano) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT c.desc_categoria AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "INNER JOIN tb_categoria_financeiro AS c ON c.id_categoria_financeiro = f.id_categoria_financeiro "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) = :ano) "
                    + "GROUP BY c.desc_categoria "
                    + "UNION "
                    + "SELECT 'T1' AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) <= :ano) "
                    + "UNION "
                    + "SELECT 'T2' AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) = :ano) "
                    + "UNION "
                    + "SELECT TO_CHAR(EXTRACT(MONTH FROM f.data_financeiro),'99') AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) = :ano) "
                    + "GROUP BY EXTRACT(MONTH FROM f.data_financeiro) "
                    + "ORDER BY descricao ");
            select.setParameter("tipo", tipo);
            select.setParameter("ano", ano);
            List<Object[]> resultado = select.list();
            return resultado;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar total financeiro ano [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar total financeiro ano [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Total financeiro ano/mes (Receitas e Despesas) para relatorio 
    public List<Object[]> totalFinanceiroAnoMes(int tipo, int ano, int mes) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT c.desc_categoria AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "INNER JOIN tb_categoria_financeiro AS c ON c.id_categoria_financeiro = f.id_categoria_financeiro "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) = :ano) AND (EXTRACT(MONTH FROM f.data_financeiro) = :mes) "
                    + "GROUP BY c.desc_categoria "
                    + "UNION "
                    + "SELECT 'TOTAL' AS descricao, SUM(f.valor_financeiro) AS total FROM tb_financeiro AS f "
                    + "WHERE f.tipo_financeiro = :tipo AND (EXTRACT(YEAR FROM f.data_financeiro) = :ano) AND (EXTRACT(MONTH FROM f.data_financeiro) = :mes) "
                    + "ORDER BY descricao ");
            select.setParameter("tipo", tipo);
            select.setParameter("ano", ano);
            select.setParameter("mes", mes);
            List<Object[]> resultado = select.list();
            return resultado;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar total financeiro ano/mes [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar total financeiro ano/mes [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
