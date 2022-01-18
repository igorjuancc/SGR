package br.com.sgr.dao;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.DaoException;
import br.com.sgr.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class PessoaDao {

    public Pessoa pessoaPorCpf(String cpf) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Pessoa WHERE cpf_pessoa = :cpf");
            select.setParameter("cpf", cpf);
            Pessoa pessoa = (Pessoa) select.uniqueResult();
            return pessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar pessoa(CPF) no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar pessoa(CPF) no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Pessoa pessoaPorEmail(String email) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Pessoa p WHERE p.email = :email");
            select.setParameter("email", email);
            Pessoa pessoa = (Pessoa) select.uniqueResult();
            return pessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao buscar pessoa(Email) no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao buscar pessoa(Email) no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Pessoa pessoaPorId(int id) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createQuery("FROM Pessoa p WHERE p.id = :id");
            select.setParameter("id", id);
            Pessoa pessoa = (Pessoa) select.uniqueResult();
            return pessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar pessoa(ID) no BD [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar pessoa(ID) no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Busca moradores e funcionarios ativos por nome CONTATOS
    public List<Pessoa> buscaParteNomeContato(String nome) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo,ext_arquivo FROM "
                    + "(SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa FROM tb_pessoa AS p "
                    + "INNER JOIN tb_morador AS m ON p.id_pessoa = m.id_morador WHERE (m.status_morador = 2 OR m.status_morador = 3) AND (p.nome_pessoa LIKE :nome) "
                    + "UNION SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa FROM tb_pessoa AS p "
                    + "INNER JOIN tb_funcionario AS f ON p.id_pessoa = f.id_funcionario WHERE (f.status_funcionario = 1) AND (p.nome_pessoa LIKE :nome)) AS p "
                    + "LEFT JOIN tb_arquivo AS a ON p.id_arquivo_pessoa = a.id_arquivo ORDER BY p.nome_pessoa");
            select.setParameter("nome", "%" + nome + "%");
            List<Object[]> resultado = select.list();
            List<Pessoa> listaPessoa = new ArrayList<>();

            for (Object[] o : resultado) {
                Pessoa p = new Pessoa();
                p.setId(Integer.parseInt(o[0].toString()));
                p.setNome(o[1].toString());
                p.setCpf(o[2].toString());
                p.setEmail(o[3].toString());
                p.setDataNascimento((Date) o[4]);
                p.setSexo(o[5].toString());
                if (o[6] != null) {
                    p.setFone(o[6].toString());
                }
                if (o[7] != null) {
                    p.setCelular(o[7].toString());
                }
                if (o[8] != null) {
                    p.setImagem(new Arquivo());
                    p.getImagem().setId(Integer.parseInt(o[8].toString()));
                    p.getImagem().setExtensao(o[9].toString());
                }
                listaPessoa.add(p);
            }
            return listaPessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao buscar contatos por nome no BD [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao buscar contatos por nome no BD [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao buscar contatos por nome no BD [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Lista de pessoas ativas nos sistema CONTATOS
    public List<Pessoa> pessoasAtivas() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo,ext_arquivo FROM "
                    + "(SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa FROM tb_pessoa AS p "
                    + "INNER JOIN tb_morador AS m ON p.id_pessoa = m.id_morador WHERE (m.status_morador = 2 OR m.status_morador = 3) "
                    + "UNION SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo_pessoa FROM tb_pessoa AS p "
                    + "INNER JOIN tb_funcionario AS f ON p.id_pessoa = f.id_funcionario WHERE (f.status_funcionario = 1)) AS p "
                    + "LEFT JOIN tb_arquivo AS a ON p.id_arquivo_pessoa = a.id_arquivo ORDER BY p.nome_pessoa");
            List<Object[]> resultado = select.list();
            List<Pessoa> listaPessoa = new ArrayList<>();

            for (Object[] o : resultado) {
                Pessoa p = new Pessoa();
                p.setId(Integer.parseInt(o[0].toString()));
                p.setNome(o[1].toString());
                p.setCpf(o[2].toString());
                if (o[3] != null) {
                    p.setEmail(o[3].toString());
                }
                p.setDataNascimento((Date) o[4]);
                p.setSexo(o[5].toString());
                if (o[6] != null) {
                    p.setFone(o[6].toString());
                }
                if (o[7] != null) {
                    p.setCelular(o[7].toString());
                }
                if (o[8] != null) {
                    p.setImagem(new Arquivo());
                    p.getImagem().setId(Integer.parseInt(o[8].toString()));
                    p.getImagem().setExtensao(o[9].toString());
                }
                listaPessoa.add(p);
            }
            return listaPessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar pessoas ativas [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar pessoas ativas [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar pessoas ativas [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Lista de moradores ativos nos sistema CONTATOS
    public List<Pessoa> moradoresAtivos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo,ext_arquivo FROM tb_pessoa AS p "
                    + "INNER JOIN tb_morador AS m ON p.id_pessoa = m.id_morador "
                    + "LEFT JOIN tb_arquivo AS a ON p.id_arquivo_pessoa = a.id_arquivo WHERE (m.status_morador = 2 OR m.status_morador = 3)");
            List<Object[]> resultado = select.list();
            List<Pessoa> listaPessoa = new ArrayList<>();

            for (Object[] o : resultado) {
                Pessoa p = new Pessoa();
                p.setId(Integer.parseInt(o[0].toString()));
                p.setNome(o[1].toString());
                p.setCpf(o[2].toString());
                p.setEmail(o[3].toString());
                p.setDataNascimento((Date) o[4]);
                p.setSexo(o[5].toString());
                if (o[6] != null) {
                    p.setFone(o[6].toString());
                }
                if (o[7] != null) {
                    p.setCelular(o[7].toString());
                }
                if (o[8] != null) {
                    p.setImagem(new Arquivo());
                    p.getImagem().setId(Integer.parseInt(o[8].toString()));
                    p.getImagem().setExtensao(o[9].toString());
                }
                listaPessoa.add(p);
            }
            return listaPessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar moradores ativos [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar moradores ativos [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar moradores ativos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    //Lista de funcionarios ativos nos sistema CONTATOS
    public List<Pessoa> funcionariosAtivos() throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query select = session.createSQLQuery("SELECT id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,fone_pessoa,cel_pessoa,id_arquivo,ext_arquivo FROM tb_pessoa AS p "
                    + "INNER JOIN tb_funcionario AS f ON p.id_pessoa = f.id_funcionario "
                    + "LEFT JOIN tb_arquivo AS a ON p.id_arquivo_pessoa = a.id_arquivo WHERE (f.status_funcionario = 1)");
            List<Object[]> resultado = select.list();
            List<Pessoa> listaPessoa = new ArrayList<>();

            for (Object[] o : resultado) {
                Pessoa p = new Pessoa();
                p.setId(Integer.parseInt(o[0].toString()));
                p.setNome(o[1].toString());
                p.setCpf(o[2].toString());
                p.setEmail(o[3].toString());
                p.setDataNascimento((Date) o[4]);
                p.setSexo(o[5].toString());
                if (o[6] != null) {
                    p.setFone(o[6].toString());
                }
                if (o[7] != null) {
                    p.setCelular(o[7].toString());
                }
                if (o[8] != null) {
                    p.setImagem(new Arquivo());
                    p.getImagem().setId(Integer.parseInt(o[8].toString()));
                    p.getImagem().setExtensao(o[9].toString());
                }
                listaPessoa.add(p);
            }
            return listaPessoa;
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao listar funcionarios ativos [Hibernate]****", e);
        } catch (NumberFormatException e) {
            throw new DaoException("****Problemas ao listar funcionarios ativos [Number Format]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao listar funcionarios ativos [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void cadastrarPessoa(Pessoa pessoa) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                session.save(pessoa);
                session.getTransaction().commit();
            } finally {
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao cadastrar nova pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao cadastrar nova pessoa [DAO]****", e);
        }
    }

    public Boolean atualizarPessoa(Pessoa pessoa) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query update = session.createQuery("UPDATE Pessoa p SET p.nome = :nome, p.sexo = :sexo, p.email = :email, p.fone = :fone, "
                        + "p.celular = :celular, p.dataNascimento = :data WHERE p.id = :id");
                update.setParameter("nome", pessoa.getNome());
                update.setParameter("sexo", pessoa.getSexo());
                update.setParameter("email", pessoa.getEmail());
                update.setParameter("fone", pessoa.getFone());
                update.setParameter("celular", pessoa.getCelular());
                update.setParameter("data", pessoa.getDataNascimento());
                update.setParameter("id", pessoa.getId());
                int rows = update.executeUpdate();
                session.getTransaction().commit();
                return rows != 0;
            } finally {
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao atualizar pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao atualizar pessoa [DAO]****", e);
        }
    }

    public void atualizarEmailPessoa(Pessoa pessoa) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Pessoa p SET p.email = :email WHERE p.id = :id");
            update.setParameter("email", pessoa.getEmail());
            update.setParameter("id", pessoa.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar email de pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar email de pessoa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public void atualizarImagemPessoa(Pessoa pessoa) throws DaoException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query update = session.createQuery("UPDATE Pessoa p SET p.imagem.id = :idImagem WHERE p.id = :id");
            update.setParameter("idImagem", pessoa.getImagem().getId());
            update.setParameter("id", pessoa.getId());
            update.executeUpdate();
        } catch (HibernateException e) {
            throw new DaoException("****Problemas ao atualizar imagem de pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problemas ao atualizar imagem de pessoa [DAO]****", e);
        } finally {
            if (session != null) {
                session.getTransaction().commit();
                session.close();
            }
        }
    }

    public Boolean removerPessoa(Pessoa pessoa) throws DaoException {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                session.beginTransaction();
                Query delete = session.createQuery("DELETE Pessoa p WHERE p.id = :id");
                delete.setParameter("id", pessoa.getId());
                int rows = delete.executeUpdate();
                return rows != 0;
            } finally {
                session.getTransaction().commit();
                session.close();
            }
        } catch (HibernateException e) {
            throw new DaoException("****Problema ao remover pessoa [Hibernate]****", e);
        } catch (Exception e) {
            throw new DaoException("****Problema ao remover pessoa [DAO]****", e);
        }
    }
}
