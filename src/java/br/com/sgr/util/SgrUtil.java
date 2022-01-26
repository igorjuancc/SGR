package br.com.sgr.util;

import br.com.sgr.exception.ArquivoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.UploadedFile;

public class SgrUtil {

    //Validação CPF
    public static boolean isCPF(String CPF) {
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF == null || CPF.trim().equals("")
                || CPF.equals("00000000000")
                || CPF.equals("11111111111")
                || CPF.equals("22222222222") || CPF.equals("33333333333")
                || CPF.equals("44444444444") || CPF.equals("55555555555")
                || CPF.equals("66666666666") || CPF.equals("77777777777")
                || CPF.equals("88888888888") || CPF.equals("99999999999")
                || (CPF.length() != 11)) {
            return (false);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0         
                // (48 eh a posicao de '0' na tabela ASCII)         
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48); // converte no respectivo caractere numerico
            }
            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) {
                return (true);
            } else {
                return (false);
            }
        } catch (InputMismatchException erro) {
            return (false);
        }
    }

    //Impressão CPF
    public static String imprimeCPF(String CPF) {
        return (CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "."
                + CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }

    //Calculo idade
    public static int idade(Date dataCalc) {
        Calendar dataNascimento = new GregorianCalendar();
        Calendar dataHoje = Calendar.getInstance();
        int idade;

        dataNascimento.setTime(dataCalc);
        idade = dataHoje.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);
        return idade;
    }

    //Salvar arquivo em pasta
    public static void gravarArquivo(UploadedFile imagem, String local, String nomeArquivo) throws ArquivoException {
        FileOutputStream fos = null;
        try {
            File caminho = new File(local, nomeArquivo);
            fos = new FileOutputStream(caminho);
            fos.write(imagem.getContents());
        } catch (IOException ex) {
            throw new ArquivoException("Problemas ao salvar arquivo em disco"
                    + ex.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                throw new ArquivoException("Problemas ao finalizar saída de fluxo de novo arquivo"
                        + ex.getMessage());
            }
        }
    }

    //Excluir arquivo da pasta
    public static void apagarArquivo(String local, String nomeArquivo) throws ArquivoException {
        try {
            File pasta = new File(local, nomeArquivo);
            pasta.delete();
        } catch (Exception e) {
            throw new ArquivoException("Problemas ao apagar arquivo do disco", e.getCause());
        }
    }

    public static Boolean verificaExistenciaImagem(String local, String nomeArquivo) {
        try {
            File arquivo = new File(local + nomeArquivo);
            return arquivo.exists();
        } catch (Exception e) {
            System.out.println("****Problemas ao verificar arquivo: " + nomeArquivo + "****");
            e.printStackTrace();
            return false;
        }
    }

    //Emissão de msg primeface
    public static FacesMessage emiteMsg(String mensagem, int tipo) {
        FacesMessage msg = null;
        switch (tipo) {
            case 1:
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, mensagem);
                break;
            case 2:
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, mensagem);
                break;
            case 3:
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
                break;
        }
        return msg;
    }

    //Mensagem e redirecionamento erro
    public static void mensagemErroRedirecionamento(String msg) {
        try {
            ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
            FacesContext.getCurrentInstance().addMessage(null, emiteMsg(msg, 3));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            ctxExt.redirect(ctxExt.getRequestContextPath() + "/Erro.jsf");
        } catch (IOException ex) {
            Logger.getLogger(SgrUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Data formatada para impressão
    public static String formataData(Date data) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(data);
    }

    //Imprime telefone
    public static String imprimeFone(String fone) {
        try {
            return ("(" + fone.substring(0, 2) + ")" + fone.substring(2, 6) + "-"
                    + fone.substring(6, 10));
        } catch (Exception e) {
            return null;
        }
    }

    //Imprime celular
    public static String imprimeCel(String cel) {
        try {
            return ("(" + cel.substring(0, 2) + ")" + cel.substring(2, 7) + "-"
                    + cel.substring(7, 11));
        } catch (Exception e) {
            return null;
        }
    }

    public static String imprimeValor(Double valor) {
        try {
            BigDecimal val = new BigDecimal(Double.toString(valor));
            return NumberFormat.getCurrencyInstance().format(val);
        } catch (Exception e) {
            return null;
        }
    }

    public static String nomeMesAbrv(int mes) {
        List<String> nomeMes = Arrays.asList("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez");
        return nomeMes.get(mes - 1);
    }

    public static String nomeMes(int mes) {
        List<String> nomeMes = Arrays.asList("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");
        return nomeMes.get(mes);
    }

    public static List<String> listaCores(int qnt, String seq) {
        try {
            if ((qnt > 72) || (seq == null)) {
                return null;
            } else {
                List<String> cores;
                List<String> coresSelect = null;

                switch (seq) {
                    case "A":
                        coresSelect = Arrays.asList("rgb(106,90,205)", "rgb(135,206,235)", "rgb(70,130,180)", "rgb(176,196,222)", "rgb(105,89,205)", "rgb(72,61,139)", "rgb(131,111,255)", "rgb(25,25,112)", "rgb(0,0,128)", "rgb(0,0,139)", "rgb(0,0,205)", "rgb(0,0,255)", "rgb(100,149,237)", "rgb(65,105,225)", "rgb(30,144,255)", "rgb(0,191,255)", "rgb(135,206,250)", "rgb(173,216,230)", "rgb(112,128,144)", "rgb(119,136,153)");
                        break;
                    case "B":
                        coresSelect = Arrays.asList("rgb(128,0,0)", "rgb(178,34,34)", "rgb(165,42,42)", "rgb(250,128,114)", "rgb(233,150,122)", "rgb(139,0,0)", "rgb(255,160,122)", "rgb(255,127,80)", "rgb(255,99,71)", "rgb(255,0,0)", "rgb(255,69,0)", "rgb(255,140,0)", "rgb(255,165,0)", "rgb(255,215,0)", "rgb(255,255,0)", "rgb(240,230,140)", "rgb(255,192,203)", "rgb(240,128,128)", "rgb(205,92,92)", "rgb(220,20,60)");
                        break;
                    case "C":
                        coresSelect = Arrays.asList("rgb(47,79,79)", "rgb(0,250,154)", "rgb(0,255,127)", "rgb(152,251,152)", "rgb(144,238,144)", "rgb(143,188,143)", "rgb(60,179,113)", "rgb(46,139,87)", "rgb(0,100,0)", "rgb(0,128,0)", "rgb(34,139,34)", "rgb(50,205,50)", "rgb(0,255,0)", "rgb(124,252,0)", "rgb(127,255,0)", "rgb(173,255,47)", "rgb(154,205,50)", "rgb(107,142,35)", "rgb(85,107,47)", "rgb(128,128,0)");
                        break;
                    case "D":
                        coresSelect = Arrays.asList("rgb(255,215,0)", "rgb(255,69,0)", "rgb(255,255,0)", "rgb(154,205,50)", "rgb(184,134,11)", "rgb(255,140,0)", "rgb(240,230,140)", "rgb(189,83,107)", "rgb(255,165,0)", "rgb(218,165,32)", "rgb(173,255,47)","rgb(128,0,128)", "rgb(128,128,128)", "rgb(138,43,226)", "rgb(139,0,139)", "rgb(139,69,19)", "rgb(147,112,219)", "rgb(148,0,211)", "rgb(153,50,204)", "rgb(160,82,45)");
                        break;
                }                                
                cores = coresSelect.subList(0, qnt);
                if (qnt > 20) {
                    qnt = qnt - 10;
                    List<String> coresReserva = Arrays.asList("rgb(128,0,128)", "rgb(128,128,128)", "rgb(138,43,226)", "rgb(139,0,139)", "rgb(139,69,19)", "rgb(147,112,219)", "rgb(148,0,211)", "rgb(153,50,204)", "rgb(160,82,45)", "rgb(169,169,169)", "rgb(184,134,11)", "rgb(186,85,211)", "rgb(188,143,143)", "rgb(189,83,107)", "rgb(192,192,192)", "rgb(199,21,133)", "rgb(205,133,63)", "rgb(210,105,30)", "rgb(210,180,140)", "rgb(211,211,211)", "rgb(218,112,214)", "rgb(218,165,32)", "rgb(219,112,147)", "rgb(220,220,220)", "rgb(221,160,221)", "rgb(222,184,135)", "rgb(238,130,238)", "rgb(244,164,96)", "rgb(245,222,179)", "rgb(255,0,255)", "rgb(255,105,180)", "rgb(255,182,193)", "rgb(255,192,203)", "rgb(255,20,147)", "rgb(255,222,173)", "rgb(28,28,28)", "rgb(32,178,170)", "rgb(54,54,54)", "rgb(64,224,208)", "rgb(72,209,204)", "rgb(75,0,130)", "rgb(79,79,79)", "rgb(95,158,160)");
                    cores.addAll(coresReserva.subList(0, qnt));
                }
                return cores;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void coresRgb(List<String> cores) {
        if ((cores != null) && (cores.size() > 0)) {
            for (int i = 0; i < cores.size(); i++) {
                cores.set(i, cores.get(i).replace(")", ", 0.5)"));
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String caminhoProjeto() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        return ec.getRealPath("/");
    }
}
