package br.com.sgr.dao;

import br.com.sgr.bean.Apartamento;
import br.com.sgr.bean.BoletoSGR;
import br.com.sgr.bean.Financeiro;
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

public class BoletoDao {

    //Todos boletos do ano do morador
    public List<BoletoSGR> listaBoletoAnoMorador(int ano, Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT b.id, b.tipo, b.dataEmissao, b.dataVencimento, b.dataReferencia, b.valorBoleto, b.valorMulta, "
                    + "m.id, m.nome, f.id, f.tipo, f.dataRegistro";
            Query select = session.createQuery(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.morador m LEFT JOIN b.financeiro f"
                    + " WHERE year(b.dataReferencia) = :ano AND m.id = :idMorador ORDER BY b.dataReferencia DESC");
            select.setParameter("ano", ano);
            select.setParameter("idMorador", morador.getId());
            List<BoletoSGR> listaBoleto = new ArrayList<>();
            List<Object[]> listSelect = select.list();            
            for (Object[] regBoleto : listSelect) {
                BoletoSGR b = new BoletoSGR();
                Morador m = new Morador();
                b.setId((int) regBoleto[0]);
                b.setTipo((int) regBoleto[1]);
                b.setDataEmissao((regBoleto[2] != null) ? (Date) regBoleto[2] : null);
                b.setDataVencimento((regBoleto[3] != null) ? (Date) regBoleto[3] : null);
                b.setDataReferencia((regBoleto[4] != null) ? (Date) regBoleto[4] : null);
                b.setValorBoleto((regBoleto[5] != null) ? (double) regBoleto[5] : null);
                b.setValorMulta((regBoleto[6] != null) ? (double) regBoleto[6] : null);          
                m.setId((int) regBoleto[7]);
                m.setNome((String) regBoleto[8]);
                b.setMorador(morador);
                if (regBoleto[9] != null) {
                    Financeiro f = new Financeiro();
                    f.setId((int) regBoleto[9]);
                    f.setTipo((int) regBoleto[10]);
                    f.setDataRegistro((regBoleto[11] != null) ? (Date) regBoleto[11] : null);
                    b.setFinanceiro(f);
                }          
                listaBoleto.add(b);
            }
            return listaBoleto;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar boletos anuais de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar boletos anuais de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Todos boletos de taxa de condominio do morador
    public List<BoletoSGR> listaBoletoTaxaAnoMorador(int ano, Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT b.id, b.tipo, b.dataEmissao, b.dataVencimento, b.dataReferencia, b.valorBoleto, b.valorMulta, "
                    + "m.id, m.nome, f.id, f.tipo, f.dataRegistro";
            Query select = session.createQuery(dadosBusca + " FROM BoletoSGR b LEFT JOIN b.morador m JOIN b.financeiro f WHERE year(b.dataReferencia) = :ano AND m.id = :idMorador AND b.tipo = 1 ORDER BY b.dataReferencia DESC");
            select.setParameter("ano", ano);
            select.setParameter("idMorador", morador.getId());
            List<BoletoSGR> listaBoleto = new ArrayList<>();
            List<Object[]> listSelect = select.list();            
            for (Object[] regBoleto : listSelect) {
                BoletoSGR b = new BoletoSGR();
                Morador m = new Morador();
                b.setId((int) regBoleto[0]);
                b.setTipo((int) regBoleto[1]);
                b.setDataEmissao((regBoleto[2] != null) ? (Date) regBoleto[2] : null);
                b.setDataVencimento((regBoleto[3] != null) ? (Date) regBoleto[3] : null);
                b.setDataReferencia((regBoleto[4] != null) ? (Date) regBoleto[4] : null);
                b.setValorBoleto((regBoleto[5] != null) ? (double) regBoleto[5] : null);
                b.setValorMulta((regBoleto[6] != null) ? (double) regBoleto[6] : null);          
                m.setId((int) regBoleto[7]);
                m.setNome((String) regBoleto[8]);
                b.setMorador(morador);
                if (regBoleto[9] != null) {
                    Financeiro f = new Financeiro();
                    f.setId((int) regBoleto[9]);
                    f.setTipo((int) regBoleto[10]);
                    f.setDataRegistro((regBoleto[11] != null) ? (Date) regBoleto[11] : null);
                    b.setFinanceiro(f);
                }          
                listaBoleto.add(b);
            }
            return listaBoleto;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar boletos de taxa anual de morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar boletos de taxa anual de morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void novoBoleto(BoletoSGR novoBoleto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(novoBoleto);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar novo boleto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar novo boleto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizaBoleto(BoletoSGR novoBoleto) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(novoBoleto);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar boleto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar boleto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarBoleto(BoletoSGR boleto) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.delete(boleto);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao apagar boleto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao apagar boleto [DAO]****", e);
        }
    }

    public int totalBoletosSemRegistro() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query count = session.createQuery("SELECT COUNT(*) FROM BoletoSGR b LEFT JOIN b.financeiro f WHERE f.boleto IS NULL");
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar quantidade total de boletos sem registro financeiro [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar quantidade total de boletos sem registro financeiro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<BoletoSGR> boletoFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<BoletoSGR> listaBoleto = new ArrayList<>();
            List<Object[]> listSelect = select.list();            
            for (Object[] regBoleto : listSelect) {
                BoletoSGR b = new BoletoSGR();
                Morador m = new Morador();
                Apartamento a = new Apartamento();
                b.setId((int) regBoleto[0]);
                b.setTipo((int) regBoleto[1]);
                b.setDataEmissao((regBoleto[2] != null) ? (Date) regBoleto[2] : null);
                b.setDataVencimento((regBoleto[3] != null) ? (Date) regBoleto[3] : null);
                b.setDataReferencia((regBoleto[4] != null) ? (Date) regBoleto[4] : null);
                b.setValorBoleto((regBoleto[5] != null) ? (double) regBoleto[5] : null);
                b.setValorMulta((regBoleto[6] != null) ? (double) regBoleto[6] : null);          
                m.setId((int) regBoleto[7]);
                m.setNome((String) regBoleto[8]);
                a.setId((int) regBoleto[12]);
                a.setBloco((String) regBoleto[13]);
                m.setApartamento(a);
                b.setMorador(m);
                if (regBoleto[9] != null) {
                    Financeiro f = new Financeiro();
                    f.setId((int) regBoleto[9]);
                    f.setTipo((int) regBoleto[10]);
                    f.setDataRegistro((regBoleto[11] != null) ? (Date) regBoleto[11] : null);
                    b.setFinanceiro(f);
                }          
                listaBoleto.add(b);
            }
            return listaBoleto;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar boletos com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar boletos com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar boletos com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
