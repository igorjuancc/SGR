package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.bean.RegistroES;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class RegistroESDao {

    public void salvarRegistroPessoa(RegistroES registro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(registro);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao salvar novo registro de pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao salvar novo registro de pessoa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarRegistroPessoa(RegistroES registro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(registro);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar registro de pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar registro de pessoa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<RegistroES> listaUltimosRegistroPessoa(Pessoa pessoa) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT r.pessoa.id, r.id, r.dataEntrada, r.dataSaida, r.tipoPessoa FROM RegistroES r WHERE r.pessoa.id = :idPessoa ORDER BY r.id DESC");
            select.setParameter("idPessoa", pessoa.getId());
            select.setMaxResults(5);
            List<RegistroES> listaRegistros = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regEs : listSelect) {
                RegistroES reg = new RegistroES();
                reg.setId((regEs[1] != null) ? (int) regEs[1] : 0);
                reg.setDataEntrada((regEs[2] != null) ? (Date) regEs[2] : null);
                reg.setDataSaida((regEs[3] != null) ? (Date) regEs[3] : null);
                reg.setTipoPessoa((regEs[4] != null) ? (String) regEs[4] : null);
                reg.setPessoa(pessoa);
                listaRegistros.add(reg);
            }
            return listaRegistros;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar ultimos registros de pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar ultimos registros de pessoa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<RegistroES> listaRegistroDataPessoa(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT r.id, r.dataEntrada, r.dataSaida, r.pessoa.nome, r.pessoa.imagem.id, r.pessoa.imagem.extensao "
                    + " FROM RegistroES r WHERE r.pessoa.id = :idPessoa AND (r.dataEntrada >= :inicioVisita AND (r.dataSaida <= :fimVisita OR r.dataSaida is NULL)) ORDER BY r.id DESC");
            select.setParameter("inicioVisita", visitante.getVisitas().get(0).getDataInicio());
            select.setParameter("fimVisita", visitante.getVisitas().get(0).getDataFim());
            select.setParameter("idPessoa", visitante.getId());
            List<RegistroES> listaRegistros = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] reg : listSelect) {
                Pessoa p = new Pessoa();
                Arquivo img = new Arquivo();
                RegistroES r = new RegistroES();                
                r.setId((reg[0] != null) ? (int) reg[0] : 0);
                r.setDataEntrada((reg[1] != null) ? (Date) reg[1] : null);
                r.setDataSaida((reg[2] != null) ? (Date) reg[2] : null);
                p.setNome((reg[3] != null) ? (String) reg[3] : null);
                img.setId((reg[4] != null) ? (int) reg[4] : 0);
                img.setExtensao((reg[5] != null) ? (String) reg[5] : null);
                p.setImagem(img);
                r.setPessoa(p);
                listaRegistros.add(r);        
            }
            return listaRegistros;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar registros de entrada do visitante em visita [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar registros de entrada do visitante em visita [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
