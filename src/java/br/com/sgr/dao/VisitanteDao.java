package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.Visita;
import br.com.sgr.bean.Visitante;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class VisitanteDao {

    public void cadastrarVisitante(Visitante visitante) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.save(visitante);
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao cadastrar novo visitante [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao cadastrar novo visitante [DAO]****", e);
        }
    }

    //Cadastrar visitante que também é funcionario ou morador
    public void cadastrarTbVisitante(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query inserir = session.createSQLQuery("INSERT INTO tb_visitante (id_visitante,id_morador_cadastro_visitante,data_cadastro_visitante) VALUES (:id,:morador,:dataCadastro)");
            inserir.setParameter("id", visitante.getId());
            inserir.setParameter("morador", visitante.getMoradorCadastro().getId());
            inserir.setParameter("dataCadastro", visitante.getDataCadastro());
            inserir.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao cadastrar visitante TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao cadastrar visitante TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarVisitante(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(visitante);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar visitante [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar visitante [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Visitante visitantePorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Visitante v WHERE v.id = :id");
            select.setParameter("id", id);
            Visitante visitante = (Visitante) select.uniqueResult();
            return visitante;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar visitante por ID no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar visitante por ID no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Visitante visitantePorCpf(String cpf) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Visitante v WHERE v.cpf = :cpf");
            select.setParameter("cpf", cpf);
            Visitante visitante = (Visitante) select.uniqueResult();
            return visitante;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar visitante por CPF no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar visitante por CPF no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void deletarVisitante(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(visitante);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar visitante [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar visitante [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Deletar visitante que é tambem morador e/ou funcionario
    public void deletarVisitanteTb(Visitante visitante) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query delete = session.createSQLQuery("DELETE FROM tb_visitante WHERE id_visitante = :id");
            delete.setParameter("id", visitante.getId());
            delete.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar visitante TB [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar visitante TB [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visitante> visitantesDisponiveis() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_morador_cadastro_visitante,data_cadastro_visitante,id_arquivo,ext_arquivo FROM "
                    + "(SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa,id_morador_cadastro_visitante,data_cadastro_visitante FROM "
                    + "(SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa FROM tb_pessoa AS p "
                    + "LEFT JOIN (SELECT id_morador FROM tb_morador AS m WHERE m.status_morador != 4 AND m.status_morador != 0) AS m "
                    + "ON id_pessoa = id_morador WHERE id_morador IS NULL) AS p "
                    + "LEFT JOIN tb_visitante AS v ON p.id_pessoa = v.id_visitante) AS v "
                    + "LEFT JOIN tb_arquivo AS a ON v.id_arquivo_pessoa = a.id_arquivo ORDER BY v.nome_pessoa");
            List<Object[]> resultado = select.list();
            List<Visitante> listaVisitantes = new ArrayList<>();

            for (Object[] o : resultado) {
                Visitante v = new Visitante();
                v.setId(Integer.parseInt(o[0].toString()));
                v.setNome(o[1].toString());
                v.setCpf(o[2].toString());
                if (o[3] != null) {
                    v.setEmail(o[3].toString());
                }
                v.setDataNascimento((Date) o[4]);
                v.setSexo(o[5].toString());
                if (o[6] != null) {
                    v.setFone(o[6].toString());
                }
                if (o[7] != null) {
                    v.setCelular(o[7].toString());
                }
                if (o[8] != null) {
                    v.setMoradorCadastro(new Morador());
                    v.getMoradorCadastro().setId(Integer.parseInt(o[8].toString()));
                }
                if (o[9] != null) {
                    v.setDataCadastro((Date) o[4]);
                }
                if (o[10] != null) {
                    v.setImagem(new Arquivo());
                    v.getImagem().setId(Integer.parseInt(o[10].toString()));
                    v.getImagem().setExtensao(o[11].toString());
                }
                listaVisitantes.add(v);
            }
            return listaVisitantes;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitantes disponiveis [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitantes disponiveis [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Verificar visitas de um determinado visitante em um intervalo de tempo
    public Boolean verificaVisitaDataVisitante(Visitante visitante) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query select = session.createQuery("FROM Visitante v INNER JOIN v.visitas vs WHERE v.id = :id AND (vs.dataInicio <= :dataFim AND vs.dataFim >= :dataInicio)");
                select.setParameter("id", visitante.getId());
                select.setParameter("dataInicio", visitante.getVisitas().get(0).getDataInicio());
                select.setParameter("dataFim", visitante.getVisitas().get(0).getDataFim());
                return !select.list().isEmpty();
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao verificar visita por data visitante [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao verificar visita por data visitante [DAO]****", e);
        }
    }

    //Lista de visitantes cadastrados por morador
    public List<Visitante> visitantesCadastroMorador(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Visitante v WHERE v.moradorCadastro = :morador");
            select.setParameter("morador", morador);
            List<Visitante> visitantes = select.list();
            return visitantes;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitantes cadastrados por morador [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitantes cadastrados por morador [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Lista de visitantes de apto desde registro morador
    public List<Visitante> visitantesVisitaPrazo(Morador morador) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT v FROM Visitante v JOIN v.visitas vs WHERE vs.apartamento = :apto AND vs.dataInicio >= :data");
            select.setParameter("apto", morador.getApartamento());
            select.setParameter("data", morador.getDataCadastro());
            List<Visitante> listaVisitante = select.list();
            return listaVisitante;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitantes apto [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitantes apto [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visitante> visitantesDeVisita(Visita visita) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT v FROM Visitante v JOIN v.visitas vs WHERE vs.id = :idVisita ORDER BY v.nome");
            select.setParameter("idVisita", visita.getId());
            List<Visitante> listaVisitante = select.list();
            return listaVisitante;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitantes de visita [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitantes de visita [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalVisitantesDia(Date hoje) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT v) FROM Visitante v JOIN v.visitas vs WHERE CURRENT_DATE BETWEEN vs.dataInicio AND vs.dataFim");
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de visitantes do dia [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de visitantes do dia [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de visitantes do dia [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalVisitantesNaoSairam() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT COUNT (DISTINCT v) FROM Visitante v JOIN v.regEntradaSaida reg WHERE reg.dataSaida = NULL");
            return Integer.valueOf(((Long) select.uniqueResult()).toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar total de visitantes que não sairam [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar total de visitantes que não sairam [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar total de visitantes que não sairam [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visitante> visitantesNaoSairam() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("SELECT DISTINCT v FROM Visitante v JOIN v.regEntradaSaida reg WHERE reg.dataSaida = NULL");
            return select.list();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar visitantes que não sairam [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar visitantes que não sairam [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Visitante> listaVisitanteFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Visitante> listaVisitante = select.list();
            return listaVisitante;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar visitantes com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar visitantes com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar visitantes com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
