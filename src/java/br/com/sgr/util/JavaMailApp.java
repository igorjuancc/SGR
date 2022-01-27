package br.com.sgr.util;

import br.com.sgr.bean.Email;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailApp {

    public static Boolean enviaEmail(Email novoEmail) {
        Boolean rtn = false;
        Session session = null;
        try {
            Properties props = new Properties();
            /**
             * Parâmetros de conexão com servidor Gmail
             */
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("*****SEU EMAIL CORPORATIVO (GMAIL)*****",
                            "*****SENHA DO SEU EMAIL*****");
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("*****SEU EMAIL CORPORATIVO (GMAIL)*****"));
            //Remetente

            //Destinatário(s)
            Address[] toUser = InternetAddress.parse(validaDestinatario(novoEmail.getDestinatario()));

            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject(novoEmail.getAssunto());
            message.setText(novoEmail.getMensagem());
            /**
             * Método para enviar a mensagem criada
             */

            Transport.send(message);

            rtn = true;
        } catch (MessagingException e) {
            System.out.println("****Problemas ao enviar email ****" + e);
            rtn = false;
        }
        return rtn;
    }

    private static String validaDestinatario(List<String> destinatario) {
        String emails = "";
        try {
            if ((destinatario != null) && (!destinatario.isEmpty())) {
                if (destinatario.size() == 1) {
                    emails = (Seguranca.isEmail(destinatario.get(0).trim()))
                            ? destinatario.get(0).trim() : "";
                } else {
                    for (String dest : destinatario) {
                        if (Seguranca.isEmail(dest.trim())) {
                            emails = emails + dest.trim() + ",";
                        }
                    }
                    emails = emails.substring(0, emails.length() - 1);
                }
            }
        } catch (Exception e) {
            System.out.println("****Problemas ao validar destinatario(s) de email ****" + e);
        }
        return emails;
    }
}
