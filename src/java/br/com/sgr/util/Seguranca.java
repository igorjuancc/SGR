package br.com.sgr.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Seguranca {
    public static String md5(String senha) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
        return String.format("%32x", hash);
    }    
    
    public static Boolean isEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
    
    //Cria senha
    public static String gerarSenha(int tamanhoSenha) {
        UUID uuid = UUID.randomUUID();
        String senhaRandom = uuid.toString().substring(0, tamanhoSenha);
        return senhaRandom;
    }
}
