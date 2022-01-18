package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Mensagem;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class MensagemDao {

    public void novaMensagem(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(mensagem);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao enviar mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao enviar mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarMensagem(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(mensagem);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao deletar mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao deletar mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void apagarMensagemSql(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createSQLQuery("DELETE FROM tb_mensagem WHERE id_mensagem = :id");
            update.setParameter("id", mensagem.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar status receptor mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar status receptor mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarMensagem(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(mensagem);
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarStatusReceptorMensagem(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createSQLQuery("UPDATE tb_mensagem SET status_receptor_mensagem = :status WHERE id_mensagem = :id");
            update.setParameter("status", mensagem.getStatusReceptor());
            update.setParameter("id", mensagem.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar status receptor mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar status receptor mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void editarStatusAutorMensagem(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createSQLQuery("UPDATE tb_mensagem SET status_autor_mensagem = :status WHERE id_mensagem = :id");
            update.setParameter("status", mensagem.getStatusAutor());
            update.setParameter("id", mensagem.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao editar status autor mensagem [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao editar status autor mensagem [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public int totalMensagens(Pessoa pessoa, int tipo) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String query;
            switch (tipo) {
                case 1:     //Enviadas
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.autor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusAutor = 1) ";
                    break;
                case 2:     //Recebidas  
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 1 OR m.statusReceptor = 2) ";
                    break;
                case 3:     //Enviadas Lixeira 
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.autor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusAutor = 2) ";
                    break;
                case 4:     //Recebidas Lixeira 
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 3 OR m.statusReceptor = 4) ";
                    break;
                case 5:     //Não lidas 
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 1) ";
                    break;
                default:    //Enviadas
                    query = "SELECT COUNT(*) FROM Mensagem m WHERE m.receptor.id = " + Integer.toString(pessoa.getId()) + " AND (m.statusReceptor = 1 OR m.statusReceptor = 2) ";
                    break;
            }
            Query count = session.createQuery(query);
            return Integer.valueOf(count.uniqueResult().toString());
        } catch (HibernateException e) {
            throw new DaoException("****Problemas buscar quantidade total de mensagens [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas buscar quantidade total de mensagens [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas buscar quantidade total de mensagens [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Mensagem> mensagemFiltro(FiltroBD filtro) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery(filtro.getDescricao());
            select.setFirstResult(filtro.getPrimeiroRegistro());
            select.setMaxResults(Integer.valueOf(filtro.getQntdRegistros().toString()));
            List<Mensagem> listaMensagem = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regMsg : listSelect) {
                Mensagem msg = new Mensagem();
                Pessoa autor = new Pessoa();
                Pessoa receptor = new Pessoa();
                Arquivo imgAutor = new Arquivo();
                Arquivo imgReceptor = new Arquivo();
                Mensagem origemResposta = new Mensagem();
                autor.setImagem(imgAutor);
                receptor.setImagem(imgReceptor);
                msg.setAutor(autor);
                msg.setReceptor(receptor);
                msg.setOrigemResposta(origemResposta);
                msg.setId((regMsg[0] != null) ? (int) regMsg[0] : 0);
                msg.setStatusAutor((regMsg[1] != null) ? (int) regMsg[1] : 0);
                msg.setStatusReceptor((regMsg[2] != null) ? (int) regMsg[2] : 0);
                msg.setAssunto((regMsg[3] != null) ? (String) regMsg[3] : null);
                msg.setDescricao((regMsg[4] != null) ? (String) regMsg[4] : null);
                msg.setData((regMsg[5] != null) ? (Date) regMsg[5] : null);
                msg.getAutor().setId((regMsg[6] != null) ? (int) regMsg[6] : 0);
                msg.getAutor().setNome((regMsg[7] != null) ? (String) regMsg[7] : null);
                msg.getAutor().setCpf((regMsg[8] != null) ? (String) regMsg[8] : null);
                msg.getAutor().setEmail((regMsg[9] != null) ? (String) regMsg[9] : null);
                msg.getAutor().setDataNascimento((regMsg[10] != null) ? (Date) regMsg[10] : null);
                msg.getAutor().setSexo((regMsg[11] != null) ? (String) regMsg[11] : null);
                msg.getAutor().setFone((regMsg[12] != null) ? (String) regMsg[12] : null);
                msg.getAutor().setCelular((regMsg[13] != null) ? (String) regMsg[13] : null);
                msg.getAutor().getImagem().setId((regMsg[14] != null) ? (int) regMsg[14] : 0);
                msg.getAutor().getImagem().setExtensao((regMsg[15] != null) ? (String) regMsg[15] : null);
                msg.getReceptor().setId((regMsg[16] != null) ? (int) regMsg[16] : 0);
                msg.getReceptor().setNome((regMsg[17] != null) ? (String) regMsg[17] : null);
                msg.getReceptor().setCpf((regMsg[18] != null) ? (String) regMsg[18] : null);
                msg.getReceptor().setEmail((regMsg[19] != null) ? (String) regMsg[19] : null);
                msg.getReceptor().setDataNascimento((regMsg[20] != null) ? (Date) regMsg[20] : null);
                msg.getReceptor().setSexo((regMsg[21] != null) ? (String) regMsg[21] : null);
                msg.getReceptor().setFone((regMsg[22] != null) ? (String) regMsg[22] : null);
                msg.getReceptor().setCelular((regMsg[23] != null) ? (String) regMsg[23] : null);
                msg.getReceptor().getImagem().setId((regMsg[24] != null) ? (int) regMsg[24] : 0);
                msg.getReceptor().getImagem().setExtensao((regMsg[25] != null) ? (String) regMsg[25] : null);
                msg.getOrigemResposta().setId((regMsg[26] != null) ? (int) regMsg[26] : 0);
                msg.getOrigemResposta().setStatusAutor((regMsg[27] != null) ? (int) regMsg[27] : 0);
                msg.getOrigemResposta().setStatusReceptor((regMsg[28] != null) ? (int) regMsg[28] : 0);
                msg.getOrigemResposta().setAssunto((regMsg[29] != null) ? (String) regMsg[29] : null);
                msg.getOrigemResposta().setDescricao((regMsg[30] != null) ? (String) regMsg[30] : null);
                msg.getOrigemResposta().setData((regMsg[31] != null) ? (Date) regMsg[31] : null);
                msg.setRespostas(mensagensResposta(msg));
                listaMensagem.add(msg);
            }
            return listaMensagem;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar mensagens com filtro [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar mensagens com filtro [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar mensagens com filtro [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public List<Mensagem> mensagensResposta(Mensagem mensagem) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
            String dadosMensagem = "SELECT m.id, m.statusAutor, m.statusReceptor, m.assunto, m.descricao, m.data, ";
            String dadosAutor = "m.autor.id, m.autor.nome, m.autor.cpf, m.autor.email, m.autor.dataNascimento, m.autor.sexo, m.autor.fone, m.autor.celular, m.autor.imagem.id, m.autor.imagem.extensao, ";
            String dadosReceptor = "m.receptor.id, m.receptor.nome, m.receptor.cpf, m.receptor.email, m.receptor.dataNascimento, m.receptor.sexo, m.receptor.fone, m.receptor.celular, m.receptor.imagem.id, m.receptor.imagem.extensao ";
            String query = dadosMensagem + dadosAutor + dadosReceptor;
            query = query + " FROM Mensagem m WHERE m.origemResposta.id = :idMensagem";
            
            Query select = session.createQuery(query);
            select.setParameter("idMensagem", mensagem.getId());
            
            List<Mensagem> listaMensagem = new ArrayList<>();
            List<Object[]> listSelect = select.list();
            for (Object[] regMsg : listSelect) {
                Mensagem msg = new Mensagem();
                Pessoa autor = new Pessoa();
                Pessoa receptor = new Pessoa();
                Arquivo imgAutor = new Arquivo();
                Arquivo imgReceptor = new Arquivo();
                autor.setImagem(imgAutor);
                receptor.setImagem(imgReceptor);
                msg.setAutor(autor);
                msg.setReceptor(receptor);
                msg.setId((regMsg[0] != null) ? (int) regMsg[0] : 0);
                msg.setStatusAutor((regMsg[1] != null) ? (int) regMsg[1] : 0);
                msg.setStatusReceptor((regMsg[2] != null) ? (int) regMsg[2] : 0);
                msg.setAssunto((regMsg[3] != null) ? (String) regMsg[3] : null);
                msg.setDescricao((regMsg[4] != null) ? (String) regMsg[4] : null);
                msg.setData((regMsg[5] != null) ? (Date) regMsg[5] : null);
                msg.getAutor().setId((regMsg[6] != null) ? (int) regMsg[6] : 0);
                msg.getAutor().setNome((regMsg[7] != null) ? (String) regMsg[7] : null);
                msg.getAutor().setCpf((regMsg[8] != null) ? (String) regMsg[8] : null);
                msg.getAutor().setEmail((regMsg[9] != null) ? (String) regMsg[9] : null);
                msg.getAutor().setDataNascimento((regMsg[10] != null) ? (Date) regMsg[10] : null);
                msg.getAutor().setSexo((regMsg[11] != null) ? (String) regMsg[11] : null);
                msg.getAutor().setFone((regMsg[12] != null) ? (String) regMsg[12] : null);
                msg.getAutor().setCelular((regMsg[13] != null) ? (String) regMsg[13] : null);
                msg.getAutor().getImagem().setId((regMsg[14] != null) ? (int) regMsg[14] : 0);
                msg.getAutor().getImagem().setExtensao((regMsg[15] != null) ? (String) regMsg[15] : null);
                msg.getReceptor().setId((regMsg[16] != null) ? (int) regMsg[16] : 0);
                msg.getReceptor().setNome((regMsg[17] != null) ? (String) regMsg[17] : null);
                msg.getReceptor().setCpf((regMsg[18] != null) ? (String) regMsg[18] : null);
                msg.getReceptor().setEmail((regMsg[19] != null) ? (String) regMsg[19] : null);
                msg.getReceptor().setDataNascimento((regMsg[20] != null) ? (Date) regMsg[20] : null);
                msg.getReceptor().setSexo((regMsg[21] != null) ? (String) regMsg[21] : null);
                msg.getReceptor().setFone((regMsg[22] != null) ? (String) regMsg[22] : null);
                msg.getReceptor().setCelular((regMsg[23] != null) ? (String) regMsg[23] : null);
                msg.getReceptor().getImagem().setId((regMsg[24] != null) ? (int) regMsg[24] : 0);
                msg.getReceptor().getImagem().setExtensao((regMsg[25] != null) ? (String) regMsg[25] : null);
                listaMensagem.add(msg);
            }
            return listaMensagem;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar mensagens resposta [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar mensagens resposta [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar mensagens resposta [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }
}
