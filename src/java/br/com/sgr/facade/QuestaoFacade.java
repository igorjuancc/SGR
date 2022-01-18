package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Assembleia;
import br.com.sgr.bean.Comentario;
import br.com.sgr.bean.Morador;
import br.com.sgr.bean.OpcQuestao;
import br.com.sgr.bean.Questao;
import br.com.sgr.bean.Voto;
import br.com.sgr.dao.QuestaoDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.ComentarioException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.QuestaoException;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.QuestaoValidator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.UploadedFile;

public class QuestaoFacade {

    private static final QuestaoDao questaoDao = new QuestaoDao();

    public static void novaQuestao(Questao questao, List<UploadedFile> upImagens) throws QuestaoException, ArquivoException {
        try {
            questao.setTitulo(questao.getTitulo().toUpperCase().trim());
            questao.setDescricao(questao.getDescricao().trim().toUpperCase());

            if (questao.getArquivos() != null) {
                for (Arquivo a : questao.getArquivos()) {
                    if ((a.getDescricao() != null)) {
                        a.setDescricao(a.getDescricao().trim().toUpperCase());
                    }
                }
            }
            if (questao.getOpcoes() != null) {
                for (OpcQuestao o : questao.getOpcoes()) {
                    if ((o.getDescricao() != null) && (!o.getDescricao().isEmpty())) {
                        o.setDescricao(o.getDescricao().trim().toUpperCase());
                    }
                }
            }
            QuestaoValidator.validaQuestao(questao);
            if (questao.getId() == 0) {
                questaoDao.novaQuestao(questao);
            } else {
                questaoDao.atualizarQuestao(questao);
            }

            if (upImagens != null) {
                salvarImagensQuestao(questao, upImagens);
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar questão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static void salvarImagensQuestao(Questao questao, List<UploadedFile> upImagens) throws ArquivoException {
        String mensagem = "";
        for (UploadedFile img : upImagens) {
            Arquivo arq = questao.getArquivos().get(upImagens.indexOf(img));
            try {
                if (img != null) {
                    salvarImagemQuestao(img, arq);
                }
            } catch (ArquivoException ex) {
                ArquivoFacade.apagarArquivo(arq);
                mensagem += "Problemas ao salvar arquivo n°" + Integer.toString(questao.getArquivos().indexOf(arq) + 1)
                        + " em disco<br/>";
            }
        }
        if (!mensagem.equals("")) {
            throw new ArquivoException(mensagem);
        }
    }

    public static List<Questao> questoesAssembleia(int idAssembleia) {
        try {
            return questaoDao.questoesAssembleia(idAssembleia);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar questões de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static List<Questao> questoesCompletasAssembleia(int idAssembleia) {
        try {
            List<Questao> questoes = questaoDao.questoesAssembleia(idAssembleia);
            for (Questao q : questoes) {
                q.setOpcoes(questaoDao.listaOpcQuestao(q));
                for (OpcQuestao opc : q.getOpcoes()) {
                    opc.setVotos(questaoDao.listaVotosOpcQuestao(opc));
                }
                q.setArquivos(ArquivoFacade.listaArquivoQuestao(q.getId()));
                q.setComentarios(ComentarioFacade.listaComentarioQuestao(q.getId()));
            }
            return questoes;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar questões de assembléia";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static Questao questaoPorId(int idQuestao) {
        try {
            Questao q = questaoDao.questaoPorId(idQuestao);
            q.setOpcoes(questaoDao.listaOpcQuestao(q));
            for (OpcQuestao opc : q.getOpcoes()) {
                opc.setVotos(questaoDao.listaVotosOpcQuestao(opc));
            }
            q.setArquivos(ArquivoFacade.listaArquivoQuestao(q.getId()));
            q.setComentarios(ComentarioFacade.listaComentarioQuestao(q.getId()));
            return q;
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar questao por id";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static void apagarQuestao(Questao questao) throws ArquivoException, QuestaoException {
        try {
            Date hoje = new Date();

            if (questao.getAssembleia().getEncerramento() != 0) {
                throw new QuestaoException("Questão não pode ser apagada devido ao encerramento da assembleia");
            } else if (questao.getAssembleia().getDataFim().before(hoje)) {
                throw new QuestaoException("Questão não pode ser apagada devido a data de encerramento da assembleia ja ter sido atingida");
            } else {
                String mensagem = "";
                for (Comentario c : questao.getComentarios()) {
                    try {
                        ComentarioFacade.apagarComentario(c);
                    } catch (ComentarioException ex) {
                        mensagem = "Problemas ao apagar comentario(s)<br/>";
                    }
                }
                for (Arquivo a : questao.getArquivos()) {
                    try {
                        apagarImagemQuestao(a);
                    } catch (ArquivoException ex) {
                        mensagem += "Problemas ao apagar arquivo n°"
                                + Integer.toString(questao.getArquivos().indexOf(a) + 1)
                                + " do disco<br/>";
                    }
                }
                questao.setComentarios(null);
                questaoDao.apagarQuestao(questao);
                if (!mensagem.equals("")) {
                    throw new ArquivoException(mensagem);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar questões";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarOpcQuestao(OpcQuestao opcQuestao) throws QuestaoException {
        try {
            Date hoje = new Date();
            if (opcQuestao.getQuestao().getAssembleia().getEncerramento() != 0) {
                throw new QuestaoException("Opção não pode ser apagada devido ao encerramento da assembleia");
            } else if (opcQuestao.getQuestao().getAssembleia().getDataFim().before(hoje)) {
                throw new QuestaoException("Opção não pode ser apagada devido ao encerramento da assembleia");
            } else {
                questaoDao.apagarOpcQuestao(opcQuestao);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar opção de questão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarImagem(Arquivo imagem) throws QuestaoException {
        try {
            Date hoje = new Date();
            if (imagem.getQuestao().getAssembleia().getEncerramento() != 0) {
                throw new QuestaoException("Imagem não pode ser apagada devido ao encerramento da assembleia");
            } else if (imagem.getQuestao().getAssembleia().getDataFim().before(hoje)) {
                throw new QuestaoException("Imagem não pode ser apagada devido a data de encerramento da assembleia ja ter sido atingida");
            } else {
                ArquivoFacade.apagarArquivo(imagem);
                apagarImagemQuestao(imagem);
            }
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar imagem de questão";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void novoVoto(Voto voto) throws QuestaoException {
        try {
            Voto verifica = null;
            Date hoje = new Date();
            Questao q = voto.getOpcao().getQuestao();

            if (voto.getMorador().getStatus() != 3) {
                throw new QuestaoException("Morador com acesso limitado, impossibilitado de votar");
            }
            if ((q.getAssembleia().getEncerramento() != 0) || (q.getAssembleia().getDataFim().before(hoje))) {
                throw new QuestaoException("A assembleia está encerrada");
            } else {
                q.setOpcoes(questaoDao.listaOpcQuestao(q));
                for (OpcQuestao o : q.getOpcoes()) {
                    o.setVotos(questaoDao.listaVotosOpcQuestao(o));
                    for (Voto v : o.getVotos()) {
                        if (v.getMorador().getId() == voto.getMorador().getId()) {
                            verifica = v;
                        }
                    }
                }

                if (verifica != null) {
                    throw new QuestaoException("Morador ja executou esse voto");
                } else if (voto.getId() != 0) {
                    throw new QuestaoException("Voto já existente");
                } else if (voto.getMorador() == null) {
                    throw new QuestaoException("Necessário indicar o morador votante");
                } else if (voto.getOpcao() == null) {
                    throw new QuestaoException("Necessário indicar a opção de voto");
                } else {
                    questaoDao.novoVoto(voto);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar voto";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void atualizarVoto(Voto voto) throws QuestaoException {
        try {
            Date hoje = new Date();
            Questao q = voto.getOpcao().getQuestao();

            if (voto.getMorador().getStatus() != 3) {
                throw new QuestaoException("Morador com acesso limitado, impossibilitado de votar");
            }
            if ((q.getAssembleia().getEncerramento() != 0) || (q.getAssembleia().getDataFim().before(hoje))) {
                throw new QuestaoException("A assembleia está encerrada");
            } else {
                q.setOpcoes(questaoDao.listaOpcQuestao(q));
                for (OpcQuestao o : q.getOpcoes()) {
                    o.setVotos(questaoDao.listaVotosOpcQuestao(o));
                }

                if (voto.getMorador() == null) {
                    throw new QuestaoException("Necessário indicar o morador votante");
                } else if (voto.getOpcao() == null) {
                    throw new QuestaoException("Necessário indicar a opção de voto");
                } else {
                    questaoDao.atualizarVoto(voto);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao processar voto";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void salvarEleicao(Assembleia assembleia, List<Morador> moradores) throws QuestaoException {
        Questao eleicao = null;
        try {
            QuestaoValidator.validaEleicao(assembleia, moradores);
            eleicao = new Questao();
            eleicao.setAssembleia(assembleia);
            eleicao.setDescricao("ELEIÇÃO PARA DEFINIR NOVO SÍNDICO");
            eleicao.setTitulo("ELEIÇÃO DE SÍNDICO");
            eleicao.setOpcoes(new ArrayList<>());
            eleicao.setArquivos(new ArrayList<>());

            for (Morador m : moradores) {
                OpcQuestao o = new OpcQuestao();
                o.setQuestao(eleicao);
                o.setDescricao(m.getNome());
                eleicao.getOpcoes().add(o);

                Arquivo a = new Arquivo();
                a.setDescricao(m.getNome());
                a.setExtensao(m.getImagem().getExtensao());
                a.setQuestao(eleicao);
                eleicao.getArquivos().add(a);
            }
            questaoDao.novaQuestao(eleicao);
            for (int i = 0; moradores.size() > i; i++) {
                File arquivoOrigem = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\" + moradores.get(i).getImagem().getId() + moradores.get(i).getImagem().getExtensao());
                File arquivoDestino = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemQuestao\\" + eleicao.getArquivos().get(i).getId() + eleicao.getArquivos().get(i).getExtensao());
                FileUtils.copyFile(arquivoOrigem, arquivoDestino);
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar eleição";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (IOException ex) {
            if (eleicao != null) {
                try {
                    questaoDao.apagarQuestao(eleicao);
                } catch (DaoException ex1) {
                    ex1.printStackTrace(System.out);
                }
            }
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar eleição";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void editarEleicao(Assembleia assembleia, List<Morador> moradores) throws QuestaoException {
        Questao eleicao = null;
        try {
            QuestaoValidator.validaEleicao(assembleia, moradores);

            for (Questao q : assembleia.getQuestoes()) {
                eleicao = q.getTitulo().equals("ELEIÇÃO DE SÍNDICO") ? q : null;
            }

            if (eleicao == null) {
                throw new QuestaoException("Eleição não encontrada");
            } else {
                for (Morador m : moradores) {
                    OpcQuestao o = new OpcQuestao();
                    o.setQuestao(eleicao);
                    o.setDescricao(m.getNome());
                    eleicao.getOpcoes().add(o);

                    Arquivo a = new Arquivo();
                    a.setDescricao(m.getNome());
                    a.setExtensao(m.getImagem().getExtensao());
                    a.setQuestao(eleicao);
                    eleicao.getArquivos().add(a);
                }
                questaoDao.atualizarQuestao(eleicao);
                for (Morador m : moradores) {
                    File arquivoOrigem = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemPessoa\\" + m.getImagem().getId() + m.getImagem().getExtensao());
                    for (Arquivo a : eleicao.getArquivos()) {
                        if (a.getDescricao().equals(m.getNome())) {
                            File arquivoDestino = new File("C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemQuestao\\" + a.getId() + a.getExtensao());
                            FileUtils.copyFile(arquivoOrigem, arquivoDestino);
                        }
                    }
                }
            }
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar eleição";
            SgrUtil.mensagemErroRedirecionamento(msg);
        } catch (IOException ex) {
            if (eleicao != null) {
                try {
                    questaoDao.apagarQuestao(eleicao);
                } catch (DaoException ex1) {
                    ex1.printStackTrace(System.out);
                }
            }
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao salvar eleição";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static void apagarCandidato(Assembleia assembleia, Morador morador) throws QuestaoException {
        if ((assembleia == null) || (morador == null)) {
            throw new QuestaoException("Morador e/ou assembleia devem ser informados");
        } else if (assembleia.getTipo() != 2) {
            throw new QuestaoException("Eleição sindical deve ser definida em AGO");
        } else {
            Questao questaoEleicao = null;
            for (Questao q : assembleia.getQuestoes()) {
                if (q.getTitulo().equals("ELEIÇÃO DE SÍNDICO")) {
                    questaoEleicao = q;
                }
            }
            if (questaoEleicao == null) {
                throw new QuestaoException("Eleição síndical não encontrada");
            } else if (questaoEleicao.getOpcoes().size() <= 2) {
                throw new QuestaoException("Necessário indicar ao menos dois candidatos");
            } else {
                for (OpcQuestao o : questaoEleicao.getOpcoes()) {
                    if (o.getDescricao().equals(morador.getNome())) {
                        apagarOpcQuestao(o);
                    }
                }
                for (Arquivo a : questaoEleicao.getArquivos()) {
                    if (a.getDescricao().equals(morador.getNome())) {
                        apagarImagem(a);
                    }
                }
            }
        }
    }

    private static void salvarImagemQuestao(UploadedFile imagem, Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = "C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemQuestao";
        SgrUtil.gravarArquivo(imagem, caminho, nomeArquivo);
    }

    private static void apagarImagemQuestao(Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = "C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemQuestao";
        SgrUtil.apagarArquivo(caminho, nomeArquivo);
    }
}
