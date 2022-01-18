package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Comentario;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ComentarioDao {

    public void novoComentario(Comentario comentario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String tipoComentario = comentario.getAtendimento() != null ? "id_atendimento_comentario," : "id_questao_comentario,";
            Query insert = session.createSQLQuery("INSERT INTO "
                    + "tb_comentario (id_pessoa_comentario, tipo_comentario, data_comentario, desc_comentario, " + tipoComentario + " tipo_pessoa_comentario) "
                    + "VALUES (:idPessoa, :tipo, :data, :desc, :dadoTipoComentario, :tipoPessoa)");
            insert.setParameter("idPessoa", comentario.getPessoa().getId());
            insert.setParameter("tipo", comentario.getTipo());
            insert.setParameter("data", comentario.getData());
            insert.setParameter("desc", comentario.getDescricao());
            insert.setParameter("tipoPessoa", comentario.getTipoPessoa());
            insert.setParameter("dadoTipoComentario", (comentario.getAtendimento() != null) ? comentario.getAtendimento().getId() : comentario.getQuestao().getId());
            insert.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao inserir novo comentario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao inserir novo comentario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarComentario(Comentario comentario) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(comentario);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao apagar comentario [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao apagar comentario [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Comentario> listaComentarioQuestao(int idQuestao) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT c.id, c.tipo, c.descricao, c.pessoa.id, c.pessoa.nome, c.tipoPessoa, c.pessoa.imagem.id, c.pessoa.imagem.extensao, c.data";
            Query select = session.createQuery(dadosBusca + " FROM Comentario c WHERE c.questao.id = :idQuestao AND c.tipo = 2");
            select.setParameter("idQuestao", idQuestao);
            List<Object[]> listSelect = select.list();
            List<Comentario> comentarios = new ArrayList<>();
            for (Object[] regComentario : listSelect) {
                Comentario c = new Comentario();
                Pessoa p = new Pessoa();
                c.setId((int) regComentario[0]);
                c.setTipo((int) regComentario[1]);
                c.setDescricao((String) regComentario[2]);
                p.setId((int) regComentario[3]);
                p.setNome((String) regComentario[4]);
                c.setPessoa(p);
                c.setTipoPessoa((String) regComentario[5]);
                if (regComentario[6] != null) {
                    Arquivo imagem = new Arquivo();
                    imagem.setId((int) regComentario[6]);
                    imagem.setExtensao((String) regComentario[7]);
                    p.setImagem(imagem);
                }
                c.setData((Date) regComentario[8]);
                comentarios.add(c);
            }
            return comentarios;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao listar comentarios de questão [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao listar comentarios de questão [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Comentario> comentariosDeAtendimento(int idAtendimento) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String dadosBusca = "SELECT c.id, c.tipo, c.descricao, c.pessoa.id, c.pessoa.nome, c.tipoPessoa, c.pessoa.imagem.id, c.pessoa.imagem.extensao, c.data";
            Query select = session.createQuery(dadosBusca + " FROM Comentario c WHERE c.atendimento.id = :idAtendimento");
            select.setParameter("idAtendimento", idAtendimento);
            List<Object[]> listSelect = select.list();
            List<Comentario> comentarios = new ArrayList<>();
            for (Object[] regComentario : listSelect) {
                Comentario c = new Comentario();
                Pessoa p = new Pessoa();
                c.setId((int) regComentario[0]);
                c.setTipo((int) regComentario[1]);
                c.setDescricao((String) regComentario[2]);
                p.setId((int) regComentario[3]);
                p.setNome((String) regComentario[4]);
                c.setPessoa(p);
                c.setTipoPessoa((String) regComentario[5]);
                if (regComentario[6] != null) {
                    Arquivo imagem = new Arquivo();
                    imagem.setId((int) regComentario[6]);
                    imagem.setExtensao((String) regComentario[7]);
                    p.setImagem(imagem);
                }
                c.setData((Date) regComentario[8]);
                comentarios.add(c);
            }
            return comentarios;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar comentarios de atendimento [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar comentarios de atendimento [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
