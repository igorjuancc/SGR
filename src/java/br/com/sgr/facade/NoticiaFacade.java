package br.com.sgr.facade;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.bean.Noticia;
import br.com.sgr.dao.NoticiaDao;
import br.com.sgr.exception.ArquivoException;
import br.com.sgr.exception.DaoException;
import br.com.sgr.exception.NoticiaException;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import br.com.sgr.validator.NoticiaValidator;
import java.util.Date;
import java.util.List;
import org.primefaces.model.UploadedFile;

public class NoticiaFacade {

    private static final NoticiaDao noticiaDao = new NoticiaDao();

    public static void cadastrarNoticia(Noticia noticia, List<UploadedFile> upImagens) throws NoticiaException, ArquivoException {
        try {
            noticia.setDescricao(noticia.getDescricao().trim());
            noticia.setTitulo(noticia.getTitulo().trim().toUpperCase());
            noticia.setData(new Date());
            noticia.setUltimaAlteracao(noticia.getData());
            if (noticia.getArquivos() != null) {
                for (Arquivo a : noticia.getArquivos()) {
                    if ((a.getDescricao() != null)) {
                        a.setDescricao(a.getDescricao().trim().toUpperCase());
                    }
                }
            }
            NoticiaValidator.validaNoticia(noticia);
            noticiaDao.cadastrarNoticia(noticia);
            LogBDFacade.inserirLog(1, noticia);

            if (upImagens != null) {
                salvarImagensNoticia(noticia, upImagens);
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao cadastrar uma nova notícia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static void salvarImagensNoticia(Noticia noticia, List<UploadedFile> upImagens) throws ArquivoException {
        String mensagem = "";
        for (UploadedFile img : upImagens) {
            Arquivo arq = noticia.getArquivos().get(upImagens.indexOf(img));
            try {
                if (img != null) {
                    salvarImagemNoticia(img, arq);
                }
            } catch (ArquivoException ex) {
                ArquivoFacade.apagarArquivo(arq);
                mensagem += "Problemas ao salvar arquivo n°" + Integer.toString(noticia.getArquivos().indexOf(arq) + 1)
                        + " em disco<br/>";
            }
        }
        if (!mensagem.equals("")) {
            throw new ArquivoException(mensagem);
        }
    }

    public static void atualizarNoticia(Noticia noticia, List<UploadedFile> upImagens) throws NoticiaException, ArquivoException {
        try {
            if (noticia.getId() <= 0) {
                System.out.println("ID de notícia inválido");
                String msg = "Houve um problema editar notícia";
                SgrUtil.mensagemErroRedirecionamento(msg);
            } else {
                noticia.setDescricao(noticia.getDescricao().trim());
                noticia.setTitulo(noticia.getTitulo().trim().toUpperCase());
                noticia.setUltimaAlteracao(new Date());
                if (noticia.getArquivos() != null) {
                    for (Arquivo a : noticia.getArquivos()) {
                        if ((a.getDescricao() != null)) {
                            a.setDescricao(a.getDescricao().trim().toUpperCase());
                        }
                    }
                }
                NoticiaValidator.validaNoticia(noticia);
                noticiaDao.atualizarNoticia(noticia);
                LogBDFacade.inserirLog(3, noticia);
                if (upImagens != null) {
                    salvarImagensNoticia(noticia, upImagens);
                }
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao editar notícia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public static List<Noticia> listaNoticiaFiltro(FiltroBD filtro) {
        try {
            String verFiltro = filtro.getPropriedadeOrdenacao() == null ? "" : filtro.getPropriedadeOrdenacao();
            String ordem = filtro.isAsc() ? "ASC" : "DESC";
            switch (verFiltro) {
                case "id":
                    filtro.setDescricao("FROM Noticia n ORDER BY n.id ");
                    break;
                case "titulo":
                    filtro.setDescricao("FROM Noticia n ORDER BY n.titulo ");
                    break;
                case "autor":
                    filtro.setDescricao("FROM Noticia n ORDER BY n.autor ");
                    break;
                case "ultimaAlteracao":
                    filtro.setDescricao("FROM Noticia n ORDER BY n.ultimaAlteracao ");
                    break;
                default:
                    ordem = "DESC";
                    filtro.setDescricao("FROM Noticia n ORDER BY n.data ");
                    break;
            }
            filtro.setDescricao(filtro.getDescricao() + ordem);
            return noticiaDao.listaNoticiaFiltro(filtro);
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao listar notícias";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return null;
        }
    }

    public static int totalNoticias() {
        try {
            return noticiaDao.totalNoticias();
        } catch (DaoException e) {
            e.printStackTrace(System.out);
            String msg = "Houve um problema ao buscar o número total de notícias";
            SgrUtil.mensagemErroRedirecionamento(msg);
            return 0;
        }
    }

    public static void apagarNoticia(Noticia noticia) throws ArquivoException {
        try {
            String mensagem = "";
            noticiaDao.apagarNoticia(noticia);
            LogBDFacade.inserirLog(2, noticia);
            for (Arquivo a : noticia.getArquivos()) {
                try {
                    apagarImagemNoticia(a);
                } catch (ArquivoException ex) {
                    mensagem += "Problemas ao apagar arquivo n°"
                            + Integer.toString(noticia.getArquivos().indexOf(a) + 1)
                            + " do disco<br/>";
                }
            }
            if (!mensagem.equals("")) {
                throw new ArquivoException(mensagem);
            }
        } catch (DaoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar notícia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }   
    }

    public static Noticia buscarNoticiaId(int id) throws DaoException {
        try {
            return noticiaDao.buscarNoticiaId(id);
        } catch (DaoException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("****Problemas ao buscar por notícia por id [Facade]****" + e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void apagarImagem(Arquivo imagem) {
        try {
            ArquivoFacade.apagarArquivo(imagem);
            apagarImagemNoticia(imagem);
            LogBDFacade.inserirLog(3, imagem.getNoticia());
        } catch (ArquivoException ex) {
            ex.printStackTrace(System.out);
            String msg = "Houve um problema ao apagar imagem de notícia";
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    private static void apagarImagemNoticia(Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = "C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemNoticia";
        SgrUtil.apagarArquivo(caminho, nomeArquivo);
    }

    private static void salvarImagemNoticia(UploadedFile imagem, Arquivo arquivo) throws ArquivoException {
        String nomeArquivo = Integer.toString(arquivo.getId()) + arquivo.getExtensao();
        String caminho = "C:\\Users\\Igor Juan\\Desktop\\TCC\\SGR\\web\\ImagemNoticia";
        SgrUtil.gravarArquivo(imagem, caminho, nomeArquivo);
    }
}
