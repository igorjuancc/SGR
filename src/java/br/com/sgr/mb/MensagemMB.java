package br.com.sgr.mb;

import br.com.sgr.bean.Mensagem;
import br.com.sgr.bean.Pessoa;
import br.com.sgr.exception.MensagemException;
import br.com.sgr.facade.MensagemFacade;
import br.com.sgr.facade.PessoaFacade;
import br.com.sgr.util.FiltroBD;
import br.com.sgr.util.SgrUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@Named(value = "mensagemMB")
@ViewScoped
public class MensagemMB implements Serializable {

    @ManagedProperty("#{LoginMB}")
    private LoginMB login;
    private Boolean exibeContatos;
    private Boolean mensagemEnviada;
    private Boolean resposta;
    private List<Pessoa> receptores;
    private List<Pessoa> pessoasAtivasSistema;
    private Mensagem mensagem;
    private Mensagem mensagemRecebida;

    private LazyDataModel<Mensagem> mensagensRecebidas;
    private LazyDataModel<Mensagem> mensagensEnviadas;
    private LazyDataModel<Mensagem> mensagensEnviadasLixeira;
    private LazyDataModel<Mensagem> mensagensRecebidasLixeira;
    private FiltroBD filtroMensagens;

    @PostConstruct
    public void init() {
        try {
            this.filtroMensagens = new FiltroBD();
            reiniciarListas();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            String msg = "Problemas ao inicializar página " + FacesContext.getCurrentInstance().getViewRoot().getViewId();
            SgrUtil.mensagemErroRedirecionamento(msg);
        }
    }

    public void iniciaListaMensagensRecebidas() {
        mensagensRecebidas = new LazyDataModel<Mensagem>() {
            private static final long serialVersionUID = 1L;
            List<Mensagem> listaMensagemFiltro = null;

            @Override
            public List<Mensagem> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMensagens.setPrimeiroRegistro(first);
                filtroMensagens.setQntdRegistros(Long.valueOf(pageSize));
                filtroMensagens.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMensagens.setPropriedadeOrdenacao(sortField);
                Pessoa p = login.getFuncionario().getId() != 0 ? login.getFuncionario() : login.getMorador();
                setRowCount(MensagemFacade.totalMensagens(p, 2));
                listaMensagemFiltro = MensagemFacade.mensagemFiltro(filtroMensagens, p, 2);
                return listaMensagemFiltro;
            }
        };
    }

    public void iniciaListaMensagensRecebidasLixeira() {
        mensagensRecebidasLixeira = new LazyDataModel<Mensagem>() {
            private static final long serialVersionUID = 1L;
            List<Mensagem> listaMensagemFiltro = null;

            @Override
            public List<Mensagem> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMensagens.setPrimeiroRegistro(first);
                filtroMensagens.setQntdRegistros(Long.valueOf(pageSize));
                filtroMensagens.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMensagens.setPropriedadeOrdenacao(sortField);
                Pessoa p = login.getFuncionario().getId() != 0 ? login.getFuncionario() : login.getMorador();
                setRowCount(MensagemFacade.totalMensagens(p, 4));
                listaMensagemFiltro = MensagemFacade.mensagemFiltro(filtroMensagens, p, 4);
                return listaMensagemFiltro;
            }
        };
    }

    public void iniciaListaMensagensEnviadas() {
        mensagensEnviadas = new LazyDataModel<Mensagem>() {
            private static final long serialVersionUID = 1L;
            List<Mensagem> listaMensagemFiltro = null;

            @Override
            public List<Mensagem> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMensagens.setPrimeiroRegistro(first);
                filtroMensagens.setQntdRegistros(Long.valueOf(pageSize));
                filtroMensagens.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMensagens.setPropriedadeOrdenacao(sortField);
                Pessoa p = login.getFuncionario().getId() != 0 ? login.getFuncionario() : login.getMorador();
                setRowCount(MensagemFacade.totalMensagens(p, 1));
                listaMensagemFiltro = MensagemFacade.mensagemFiltro(filtroMensagens, p, 1);
                return listaMensagemFiltro;
            }
        };
    }

    public void iniciaListaMensagensEnviadasLixeira() {
        mensagensEnviadasLixeira = new LazyDataModel<Mensagem>() {
            private static final long serialVersionUID = 1L;
            List<Mensagem> listaMensagemFiltro = null;

            @Override
            public List<Mensagem> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                filtroMensagens.setPrimeiroRegistro(first);
                filtroMensagens.setQntdRegistros(Long.valueOf(pageSize));
                filtroMensagens.setAsc(SortOrder.ASCENDING.equals(sortOrder));
                filtroMensagens.setPropriedadeOrdenacao(sortField);
                Pessoa p = login.getFuncionario().getId() != 0 ? login.getFuncionario() : login.getMorador();
                setRowCount(MensagemFacade.totalMensagens(p, 3));
                listaMensagemFiltro = MensagemFacade.mensagemFiltro(filtroMensagens, p, 3);
                return listaMensagemFiltro;
            }
        };
    }

    public void novaMensagem() {
        try {
            if (this.login.getFuncionario().getCpf() != null) {
                this.mensagem.setAutor(this.login.getFuncionario());
            } else {
                this.mensagem.setAutor(this.login.getMorador());
            }

            MensagemFacade.novaMensagem(this.mensagem, this.receptores);
            this.mensagemEnviada = true;
        } catch (MensagemException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, SgrUtil.emiteMsg(e.getMessage(), 2));
            } else {
                e.printStackTrace(System.out);
                SgrUtil.mensagemErroRedirecionamento("Houve um problema ao enviar nova mensagem");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao enviar nova mensagem");
        }
    }

    public List<Pessoa> buscaParteNomeContato(String nome) {
        try {
            List<Pessoa> pessoas = PessoaFacade.buscaParteNomeContato(nome);
            if (!pessoas.isEmpty()) {
                if ((this.receptores != null) && (!this.receptores.isEmpty())) {
                    pessoas.removeAll(this.receptores);
                }
            }
            return pessoas;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de pessoa");
            return null;
        }
    }

    public void addContatoLista(Pessoa contato) {
        try {
            if (this.receptores == null) {
                this.receptores = new ArrayList<>();
            }
            this.receptores.add(contato);
            iniciaListaPessoasAtivas();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de pessoa");
        }
    }

    public void addGrupoContato(int opc) {
        try {
            switch (opc) {
                case 1:
                    this.receptores = PessoaFacade.pessoasAtivas();
                    iniciaListaPessoasAtivas();
                    break;
                case 2:
                    this.receptores = PessoaFacade.moradoresAtivos();
                    iniciaListaPessoasAtivas();
                    break;
                case 3:
                    this.receptores = PessoaFacade.funcionariosAtivos();
                    iniciaListaPessoasAtivas();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao buscar dados de pessoas");
        }
    }

    public void iniciaListaPessoasAtivas() {
        try {
            this.pessoasAtivasSistema = PessoaFacade.pessoasAtivas();
            if (this.receptores != null) {
                for (int i = 0; i < this.receptores.size(); i++) {
                    for (int j = 0; j < this.pessoasAtivasSistema.size(); j++) {
                        if (this.receptores.get(i).getId() == this.pessoasAtivasSistema.get(j).getId()) {
                            this.pessoasAtivasSistema.remove(j);
                            j = this.pessoasAtivasSistema.size();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao buscar dados de pessoas");
        }
    }

    public void redirecionar(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de mensagem");
        }
    }

    //Verificação[opc] autor (1) receptor (2)
    public void verMensagem(Mensagem mensagem, int opc) {
        try {
            MensagemFacade.verMensagem(mensagem, opc);
            this.resposta = (opc == 2);
            this.mensagemRecebida = mensagem;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagem");
        }
    }

    //Verificação[opc] autor (1) receptor (2)
    public void enviarLixeiraMensagem(Mensagem mensagem, int opc) {
        try {
            FacesMessage msg;
            FacesContext ctx = FacesContext.getCurrentInstance();

            MensagemFacade.enviarLixeiraMensagem(mensagem, opc);
            reiniciarListas();
            updateTela();
            msg = SgrUtil.emiteMsg("Mensagem enviada a lixeira", 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagem");
        }
    }

    //Verificação[opc] autor (1) receptor (2)
    public void restaurarMensagem(Mensagem mensagem, int opc) {
        try {
            FacesMessage msg;
            FacesContext ctx = FacesContext.getCurrentInstance();

            MensagemFacade.restaurarMensagem(mensagem, opc);
            reiniciarListas();
            updateTela();
            msg = SgrUtil.emiteMsg("Mensagem restaurada", 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagem");
        }
    }

    //Verificação[opc] autor (1) receptor (2)
    public void apagarMensagem(Mensagem mensagem, int opc) {
        try {
            FacesMessage msg;
            FacesContext ctx = FacesContext.getCurrentInstance();

            MensagemFacade.apagarMensagem(mensagem, opc);
            reiniciarListas();
            updateTela();
            msg = SgrUtil.emiteMsg("Mensagem deletada com sucesso", 1);
            ctx.addMessage(null, msg);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagem");
        }
    }

    public void setRespostaMensagem() {
        try {
            this.mensagem.setAssunto("RE: " + this.mensagemRecebida.getAssunto());
            this.mensagem.setOrigemResposta(this.mensagemRecebida);
            this.receptores.clear();
            this.receptores.add(this.mensagemRecebida.getAutor());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagem");
        }
    }

    public void reiniciarListas() {
        try {
            this.mensagem = new Mensagem();
            this.mensagemRecebida = new Mensagem();
            this.receptores = new ArrayList<>();
            this.resposta = null;
            iniciaListaMensagensEnviadas();
            iniciaListaMensagensEnviadasLixeira();
            iniciaListaMensagensRecebidas();
            iniciaListaMensagensRecebidasLixeira();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar mensagens");
        }
    }

    public void setNovaMensagem() {
        try {
            this.mensagem = new Mensagem();
            this.resposta = false;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            SgrUtil.mensagemErroRedirecionamento("Houve um problema ao processar dados de mensagem");
        }
    }

    //Atualizar compontentes ao apagar/enviar/restaurar mensagens
    public void updateTela() {
        PrimeFaces.current().ajax().update("formDivMensagem:tabViewMensagens:tabelaEnviadas");
        PrimeFaces.current().ajax().update("formDivMensagem:tabViewMensagens:tabelaEnviadosLixeira");
        PrimeFaces.current().ajax().update("formDivMensagem:tabViewMensagens:tabelaRecebidos");
        PrimeFaces.current().ajax().update("formDivMensagem:tabViewMensagens:tabelaRecebidosLixeira");
        PrimeFaces.current().ajax().update("formDivMensagem:msgGrowl");
    }

    public List<Pessoa> getReceptores() {
        return receptores;
    }

    public void setReceptores(List<Pessoa> receptores) {
        this.receptores = receptores;
    }

    public LoginMB getLogin() {
        return login;
    }

    public void setLogin(LoginMB login) {
        this.login = login;
    }

    public List<Pessoa> getPessoasAtivasSistema() {
        return pessoasAtivasSistema;
    }

    public void setPessoasAtivasSistema(List<Pessoa> pessoasAtivasSistema) {
        this.pessoasAtivasSistema = pessoasAtivasSistema;
    }

    public Boolean getExibeContatos() {
        return exibeContatos;
    }

    public void setExibeContatos(Boolean exibeContatos) {
        this.exibeContatos = exibeContatos;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public Boolean getMensagemEnviada() {
        return mensagemEnviada;
    }

    public void setMensagemEnviada(Boolean mensagemEnviada) {
        this.mensagemEnviada = mensagemEnviada;
    }

    public Mensagem getMensagemRecebida() {
        return mensagemRecebida;
    }

    public void setMensagemRecebida(Mensagem mensagemRecebida) {
        this.mensagemRecebida = mensagemRecebida;
    }

    public LazyDataModel<Mensagem> getMensagensRecebidas() {
        return mensagensRecebidas;
    }

    public void setMensagensRecebidas(LazyDataModel<Mensagem> mensagensRecebidas) {
        this.mensagensRecebidas = mensagensRecebidas;
    }

    public LazyDataModel<Mensagem> getMensagensEnviadas() {
        return mensagensEnviadas;
    }

    public void setMensagensEnviadas(LazyDataModel<Mensagem> mensagensEnviadas) {
        this.mensagensEnviadas = mensagensEnviadas;
    }

    public LazyDataModel<Mensagem> getMensagensEnviadasLixeira() {
        return mensagensEnviadasLixeira;
    }

    public void setMensagensEnviadasLixeira(LazyDataModel<Mensagem> mensagensEnviadasLixeira) {
        this.mensagensEnviadasLixeira = mensagensEnviadasLixeira;
    }

    public LazyDataModel<Mensagem> getMensagensRecebidasLixeira() {
        return mensagensRecebidasLixeira;
    }

    public void setMensagensRecebidasLixeira(LazyDataModel<Mensagem> mensagensRecebidasLixeira) {
        this.mensagensRecebidasLixeira = mensagensRecebidasLixeira;
    }

    public Boolean getResposta() {
        return resposta;
    }

    public void setResposta(Boolean resposta) {
        this.resposta = resposta;
    }
}
