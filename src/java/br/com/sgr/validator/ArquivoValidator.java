package br.com.sgr.validator;

import br.com.sgr.bean.Arquivo;
import br.com.sgr.exception.ArquivoException;

public class ArquivoValidator {

    public static void validaImagem(Arquivo imagem) throws ArquivoException {
        if (imagem == null) {
            throw new ArquivoException("Imagem inválida");
        } else {
            if (imagem.getExtensao() == null) {
                throw new ArquivoException("Não foi possível identificar a extenção da imagem");
            } else {
                if ((!imagem.getExtensao().equals(".GIF"))
                        && (!imagem.getExtensao().equals(".JPG"))
                        && (!imagem.getExtensao().equals(".JPEG"))
                        && (!imagem.getExtensao().equals(".PNG"))) {
                    throw new ArquivoException("Extenção de imagem inválida");
                }
            }
            if (imagem.getDescricao() != null) {
                if (imagem.getDescricao().length() > 100) {
                    throw new ArquivoException("Descrição não pode ultrapassar [100] caracteres");
                }
            }
        }
    }

    public static void validaVideo(Arquivo video) throws ArquivoException {
        if (video == null) {
            throw new ArquivoException("Vídeo inválido");
        } else {
            if (video.getExtensao() == null) {
                throw new ArquivoException("Não foi possível identificar a extenção do vídeo");
            } else {
                if (!video.getExtensao().equals(".MP4")) {
                    throw new ArquivoException("Extenção de vídeo inválida");
                }
            }
            if (video.getDescricao() != null) {
                if (video.getDescricao().length() > 100) {
                    throw new ArquivoException("Descrição não pode ultrapassar [100] caracteres");
                }
            }
        }
    }

    public static void filtroExtencaoArquivo(Arquivo arquivo) throws ArquivoException {
        if (arquivo == null) {
            throw new ArquivoException("Arquivo inválido");
        } else {
            if ((arquivo.getExtensao() == null)
                    || (arquivo.getExtensao().trim().equals("")
                    || (arquivo.getExtensao().trim().isEmpty()))) {
                throw new ArquivoException("Extenção de arquivo inválida");
            } else if (arquivo.getExtensao().equals(".MP4")) {
                validaVideo(arquivo);
            } else if ((arquivo.getExtensao().equals(".GIF"))
                    || (arquivo.getExtensao().equals(".JPG"))
                    || (arquivo.getExtensao().equals(".JPEG"))
                    || (arquivo.getExtensao().equals(".PNG"))) {
                validaImagem(arquivo);
            } else {
                throw new ArquivoException("Extenção de arquivo inválida");                
            }
        }
    }
}
