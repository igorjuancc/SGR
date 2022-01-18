package br.com.sgr.mb;

import br.com.sgr.bean.Funcionario;
import br.com.sgr.bean.Morador;
import br.com.sgr.facade.LoginFacade;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@ManagedBean
@Named(value = "LoginMB")
@SessionScoped
public class LoginMB implements Serializable {

    private Funcionario funcionario = new Funcionario();
    private Morador morador = new Morador();

    public LoginMB() {
    }

    public void efetuarLoginMorador() {
        try {
            this.morador.setSenha(md5(this.morador.getSenha()));
            this.morador = LoginFacade.loginMorador(this.morador);

            if (this.morador.getNome() == null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ou Senha Inválido!", "Usuario ou Senha Inválido!");
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, msg);
            } else {
                if ((this.morador.getStatus() == 0)) {
                    this.morador = new Morador();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario não autorizado!", "Usuario não autorizado");
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, msg);
                } else {
                    ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
                    ctxExt.redirect(ctxExt.getRequestContextPath() + "/morador/MoradorHome.jsf");
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de login de morador");
        }
    }

    public void efetuarLoginFuncionario() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();

            this.funcionario.setCpf(this.funcionario.getCpf().replace(".", ""));
            this.funcionario.setCpf(this.funcionario.getCpf().replace("-", ""));
            this.funcionario.setSenha(md5(this.funcionario.getSenha()));
            this.funcionario = LoginFacade.LoginFuncionario(this.funcionario);

            if (this.funcionario.getNome() == null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ou Senha Inválido!", "Usuario ou Senha Inválido!");

                ctx.addMessage(null, msg);
            } else {
                ExternalContext ctxExt = ctx.getExternalContext();
                if (this.funcionario.getStatus() == 1) {
                    if ((this.funcionario.getFuncao().getId() == 1)) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/sindico/SindicoHome.jsf");
                    }
                    if ((this.funcionario.getFuncao().getId() == 2)) {
                        ctxExt.redirect(ctxExt.getRequestContextPath() + "/porteiro/PorteiroHome.jsf");
                    }
                } else if ((this.funcionario.getStatus() == 0) || (this.funcionario.getStatus() == 3)) {
                    ctxExt.redirect(ctxExt.getRequestContextPath() + "/PrimeiroAcessoFuncionario.jsf");
                } else {
                    this.funcionario = new Funcionario();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario não autorizado!", "Usuario não autorizado");
                    ctx.addMessage(null, msg);
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de login de funcionário");
        }
    }

    private static String md5(String senha) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
        return String.format("%32x", hash);
    }

    public void logout() {
        try {
            ExternalContext ctxExt = FacesContext.getCurrentInstance().getExternalContext();
            if ((this.funcionario != null) || (this.morador != null)) {
                ctxExt.invalidateSession();
                ctxExt.redirect(ctxExt.getRequestContextPath() + "/index.jsf");
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de logoff");
        }
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }
}
