<div align="center">
  <img src="https://github.com/igorjuancc/SGR/blob/master/web/css/logo.png" width="160vw" height="80vh" />
</div>

# 🏢 Sistema de Gerenciamento Residêncial
<div align="justify">
O Sistema de Gerenciamento Residêncial - SGR é uma aplicação WEB, desenvolvida para atender as demandas e automatizar rotinas de moradores e funcionários em uma área de convívio 
mútuo. <br />
A plataforma disponibiliza controle de acesso de visitantes, cadastro de moradores, agendamento de visitas e utilização de salão de festas, gerenciamento de vagas de garagem, painel de notícias, emissão de boletos de cobrança, troca de mensagens, notificação de multas, controle de finanças e consulta ao balanço, emissão de relatórios gerencias e realização de assembléia online para condomínios.
</div>

# 🔎 Índice

<!--ts-->
   * [Resumo](#Sistema-de-Gerenciamento-Residêncial)
   * [Índice](#Índice)
   * [Instalação](#instalacao)
   * [Como usar](#como-usar)
      * [Pre Requisitos](#pre-requisitos)
      * [Local files](#local-files)
      * [Remote files](#remote-files)
      * [Multiple files](#multiple-files)
      * [Combo](#combo)
   * [Tests](#testes)
   * [Tecnologias](#tecnologias)
<!--te-->

# 🚀 Começando

## 📋 Pré-requisitos

Todas as bibliotecas e o servidor para deploy da aplicação estão disponíveis no Google Drive que pode ser acessado através desse [link](https://drive.google.com/drive/folders/1-a_gxheCg5lmCAPxIASGAi7MjywlnjqY).
<br/>
Informações sobre servidor ou ferramentas para execução do projeto podem ser encontradas na seção [Guias](#Guias) desse documento.

### ⚠️ Obrigatórios

#### 💽 Softwares

* [JRE - Java Runtime Environment 8 (Ou superior)](https://www.java.com/pt-BR/download/manual.jsp)    
* [JDK - Java Development Kit 8 (Ou superior)](https://www.oracle.com/br/java/technologies/javase/javase8-archive-downloads.html)    
* [PostgreSQL 10 (Ou superior)](https://www.postgresql.org/download/)  
* [GlassFish 4.1](https://download.oracle.com/glassfish/4.1/release/index.html) 
* [Netbeans 8.2 (Ou superior)](https://netbeans.apache.org/download/archive/index.html) 
    * Ou outra IDE de sua preferência

#### 📚 Bibliotecas

* [JSF 2.2](https://mvnrepository.com/artifact/com.sun.faces/jsf-api/2.2.20) 
* [Hibernate 4.3.1](https://mvnrepository.com/artifact/org.hibernate/hibernate-core/4.3.1.Final)
* [Bopepo 0.2.3](https://github.com/jrimum/bopepo)
    * [Commons Lang 2.4](https://mvnrepository.com/artifact/commons-lang/commons-lang/2.4)
    * [IText 2.0.8](https://mvnrepository.com/artifact/com.lowagie/itext/2.0.8)
    * [JRimum Bopepo 0.2.3](https://github.com/jrimum/bopepo)
    * [JRimum Domkee 0.2.3](https://github.com/jrimum/domkee)
    * [JRimum Vallia 0.2.3](https://github.com/jrimum/vallia)
    * [Log4j 1.2.15](https://mvnrepository.com/artifact/log4j/log4j/1.2.15)
* [JasperReports 6.13](https://community.jaspersoft.com/project/jasperreports-library/releases)
    * [Commons BeanUtils 1.9.4](https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils/1.9.4)
    * [Commons Collections 4.4.4](https://mvnrepository.com/artifact/org.apache.commons/commons-collections4/4.4)
    * [Commons Digester 2.1](https://mvnrepository.com/artifact/commons-digester/commons-digester/2.1)
    * [Commons Logging 1.2](https://mvnrepository.com/artifact/commons-logging/commons-logging/1.2)
    * [IText 2.1.7](https://mvnrepository.com/artifact/com.lowagie/itext/2.1.7)
    * [JasperReports 6.13](https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports/6.13.0)
    * [Joda Time 2.4](https://mvnrepository.com/artifact/joda-time/joda-time/2.4)
* [PrimeFaces 7.0](https://www.primefaces.org/downloads/) 
* [Commons FileUpload 1.4](https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload/1.4) 
* [Commons IO 2.6](https://mvnrepository.com/artifact/commons-io/commons-io/2.6) 
* [JavaMail API 1.6.0](https://mvnrepository.com/artifact/javax.mail/javax.mail-api/1.6.0) 
* [JDBC Driver Postgresql 9.4.1209](https://repo1.maven.org/maven2/org/postgresql/postgresql/9.4.1209/) 

### 🔀 Opcionais

* [Git 2.33 (Ou superior)](https://git-scm.com/downloads)
* [Apache Ant 1.10.7 (Ou superior)](https://ant.apache.org/easyant/download.cgi)

## ⚙️ Configuração

1. Para executar o projeto, efetue o download ou o colone desse repositório.
```
git clone https://github.com/igorjuancc/SGR.git
```
2. Acesse o PostgreSQL via psql ou pgAdmin e crie uma nova base de dados com o nome **SGR**.
    
    2.1 Utlize o arquivo **script_bd_sgr.sql** localizado na pasta "DB" desse projeto para criar as tabelas e os inserts necessários para utilizar a aplicação.
     
3. Abra o projeto clonado com o NetBeans (ou a IDE de sua preferência) e efetue a importação de **todas** as bibliotecas indicadas na seção [Obrigatórios](#Obrigatórios) desse repositório.
4. Abra o arquivo hibernate.cfg.xml localizado no pacote default do projeto e modifique as propriedades de conexão com as informações pertinentes ao seu computador ou ao servidor utilizado para o deploy da aplicação.
5. Ainda na IDE utilizada, modifique as seguintes linhas do arquivo JavaMailApp.java do pacote sgr.util: 36 e 41 com um email valído do gmail (**apenas**) e a linha 37 com a senha desse email. Esse email será utilizado para o envio de mensagens da aplicação (Necessário configurar o acesso de aplicações externas, consulte a aba [Guias](#Guias) para mais informações). 
6. Com o auxilio do Netbeans (ou da IDE utilizada) ou do Apache Ant, crie o arquivo .war da aplicação.

    5.1  Com o Apache Ant dentro da pasta do projeto.
    ```
     $ ant
    ```
    
6. Copie o arquivo SGR.war da pasta "dist" para a pasta autodeploy do servidor glassfish ou insira via interface gráfica do console do servidor.
```
cd ~/SGR/src/dist
cp SGR.war ~/glassfish4/glassfish/domains/domain1/autodeploy
```
7. Inicie (ou reinicie) o servidor da aplicação e acesse o projeto no navegador de acordo com as configurações do glassfish, normalmente http://localhost:8080/SGR/index.jsf.

## 🥇 Utilização do SGR

1. A aplicação SGR inicia-se na tela de index do projeto, na qual existem dois botões para as funcionalidades da aplicação "Morador" e "Funcionário".

## Guias
>[Guia GlassFish 4.1](https://github.com/igorjuancc/guia/blob/main/Servidores/GlassFish/4.1/glassfish-4.1.md) 


## Sobre o Projeto
O Sistema de Gerenciamento Residêncial - SGR é uma aplicação WEB, desenvolvida para atender as demandas e automatizar rotinas de moradores e funcionários em uma área de convívio 
mútuo. 
O projeto foi desenvolvido como requisito parcial para obtenção do grau de Tecnólogo em Análise de Desenvolvimento de Sistemas, do Setor de Educação Profissional e Tecnológica, 
da Universidade Federal do Paraná, dentro da disciplina TI166 - Trabalho de Conclusão de Curso.

### Técnologias 
 * Java EE 8
 * JSF 2.x
 * Hibernate 5.x
 * PrimeFaces 7.0
 
### Ferramentas
 * Netbeans 8.2
 * PgAdmin 4
 * PostgreSQL 10
 * Jaspersoft Studio 6.17
 * Git 2.34 e GitHub
 
### Bibliotecas Adicionais
 * JRimum Bopepo 
 * Java Mail API
 
### Funcionalidades
 #### Comum (Aos perfis)
  - [x] Login e Logoff
  - [x] Mural de Notícias
  - [x] Balanço Financeiro
  - [x] Mensagens
  - [x] Alteração de Dados Pessoais
  - [x] Alertas 
 #### Síndico 
   - [x] Controle de acesso de moradores
   - [x] Lista de débitos de moradores
   - [x] Cadastro, edição, visualização e exclusão de advertências e multas
   - [x] Cadastro, edição e exclusão de infrações
   - [x] Cadastro, edição, visualização e exclusão de assembléias
   - [x] Cadastro, edição, visualização e exclusão de questões de assembléia
   - [x] Cadastro, edição, visualização e exclusão de eleições síndicais
   - [x] Visualização e realização de atendimentos
   - [x] Gerenciamento de Estacionamento
   - [x] Cadastro, edição, visualização e exclusão de registros financeiros
   - [x] Cadastro, edição, visualização e exclusão de funcionários
   - [x] Consulta a dados de moradores e geração de nova senha
   - [x] Cadastro, edição, visualização e exclusão de notícias
   - [x] Cadastro, edição, visualização e exclusão de eleições síndicais
   - [x] Aprovação de cadastro de novo morador
   - [x] Emissão de relatórios gerênciais
   - [x] Reserva de salão de festas   
 #### Morador 
   - [x] Participação e votos (Assembléia)   
   - [x] Inserção de parecer (Assembléia)  
   - [x] Cadastro, edição, visualização e exclusão de atendimentos
   - [x] Emissão de boletos de cobrança
   - [x] Consulta a advertências e multas
   - [x] Cadastro, edição, visualização e exclusão de dependentes
   - [x] Consulta e solicitação de reserva de salão de festas
   - [x] Consulta e solicitação de vagas de estacionamento
   - [x] Cadastro, edição, visualização e exclusão de veículos
   - [x] Cadastro, edição, visualização e exclusão de visitantes
   - [x] Cadastro, edição, visualização e exclusão de visitas
   - [x] Recuperação de senha
  #### Porteiro 
   - [x] Consulta moradores   
   - [x] Consulta veículos
   - [x] Consulta, visitantes agendados
   - [x] Registro de entrada e saída de visitantes
   - [x] Registro de foto de visitantes   
 
## Pré-requisitos

### Obrigatório

* JRE - Java Runtime Environment 8 (Ou superior)
* JDK - Java Development Kit 8 (ou superior)

### Opcional

* Git 2.33 (Ou superior)
