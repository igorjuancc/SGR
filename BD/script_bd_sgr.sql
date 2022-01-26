CREATE TABLE tb_pessoa(
	id_pessoa SERIAL PRIMARY KEY NOT NULL,
	nome_pessoa VARCHAR(100) NOT NULL,
	cpf_pessoa VARCHAR(11) UNIQUE,
	email_pessoa VARCHAR(30) UNIQUE,
	data_pessoa DATE NOT NULL,
	sexo_pessoa CHAR(1) NOT NULL,
	fone_pessoa VARCHAR(11),
	cel_pessoa VARCHAR(11),
	id_arquivo_pessoa INT,
);

CREATE TABLE tb_apartamento(
	id_apartamento INT PRIMARY KEY NOT NULL UNIQUE,
	bloco_apartamento CHAR(1) NOT NULL,
	andar_apartamento INT NOT NULL
);

-- Tipo morador [1]-Responsavel [2]-Dependente  
-- Status morador [0] - Desativado [1] - Acesso ao predio/Sem sistema(Dependente)	[2] - Acesso ao predio/Limitado sistema	
--				  [3] - Acesso ao predio/Total sistema	[4] - Desligado [5] - Reativado
CREATE TABLE tb_morador(
	id_morador SERIAL PRIMARY KEY NOT NULL,
	tipo_morador INT NOT NULL,
	senha_morador VARCHAR(100),
	apartamento_morador INT NOT NULL,
	status_morador INT NOT NULL,
	data_aprovacao_morador DATE,
	FOREIGN KEY (id_morador) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (apartamento_morador) REFERENCES tb_apartamento (id_apartamento)
);

CREATE TABLE tb_veiculo(
	id_veiculo SERIAL PRIMARY KEY NOT NULL,
	id_morador INT NOT NULL,
	placa_veiculo VARCHAR(8) NOT NULL UNIQUE,
	marca_veiculo VARCHAR(20) NOT NULL,
	modelo_veiculo VARCHAR(20) NOT NULL,
	cor_veiculo VARCHAR(20) NOT NULL,
	ano_veiculo INT NOT NULL,
	FOREIGN KEY (id_morador) REFERENCES tb_morador (id_morador)
);

CREATE TABLE tb_visita(
	id_visita SERIAL PRIMARY KEY NOT NULL,
	id_apartamento INT NOT NULL,
	data_inicio DATE,
	data_fim DATE,
	FOREIGN KEY (id_apartamento) REFERENCES tb_apartamento (id_apartamento)
);

CREATE TABLE tb_visita_pessoa (
	id_visita INTEGER NOT NULL REFERENCES tb_visita (id_visita),
	id_visitante INTEGER NOT NULL REFERENCES tb_visitante (id_visitante)
);

CREATE TABLE tb_visitante(
	id_visitante SERIAL PRIMARY KEY NOT NULL,
	id_morador_cadastro_visitante INT NOT NULL,
	data_cadastro_visitante DATE,	
	FOREIGN KEY (id_visitante) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_morador_cadastro_visitante) REFERENCES tb_morador (id_morador)
);

CREATE TABLE tb_reg_entrada_saida_pessoa (
	id_reg_entrada_saida_pessoa SERIAL PRIMARY KEY NOT NULL,
	id_pessoa_reg_entrada_saida_pessoa INTEGER NOT NULL,
	data_entrada_reg_entrada_saida_pessoa TIMESTAMP,
	data_saida_reg_entrada_saida_pessoa TIMESTAMP,
	tipo_pessoa_reg_entrada_saida_pessoa VARCHAR(2) NOT NULL,
	id_funcionario_reg_entrada_pessoa INTEGER,
	id_funcionario_reg_saida_pessoa INTEGER,
	FOREIGN KEY (id_pessoa_reg_entrada_saida_pessoa) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_funcionario_reg_entrada_pessoa) REFERENCES tb_funcionario (id_funcionario),
	FOREIGN KEY (id_funcionario_reg_saida_pessoa) REFERENCES tb_funcionario (id_funcionario)
);

CREATE TABLE tb_vaga(
	id_vaga SERIAL PRIMARY KEY NOT NULL,
	id_apartamento_vaga INT,
	FOREIGN KEY (id_apartamento_vaga) REFERENCES tb_apartamento (id_apartamento)	
);

CREATE TABLE tb_funcao(
	id_funcao SERIAL PRIMARY KEY NOT NULL,
	desc_funcao VARCHAR(50) NOT NULL
);

-- Status funcionario [0] - Novo funcionario	[1] - Ativado	[2] - Desativado	[3] - Reativação
CREATE TABLE tb_funcionario(
	id_funcionario SERIAL PRIMARY KEY NOT NULL,
	id_funcao INT NOT NULL,
	empresa_funcionario VARCHAR(50),
	status_funcionario INT NOT NULL,
	senha_funcionario VARCHAR(100),
	FOREIGN KEY (id_funcionario) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_funcao) REFERENCES tb_funcao (id_funcao)
);

CREATE TABLE tb_categoria_financeiro(
	id_categoria_financeiro SERIAL PRIMARY KEY NOT NULL,
	desc_categoria VARCHAR(50)	
);

CREATE TABLE tb_financeiro(
	id_financeiro SERIAL PRIMARY KEY NOT NULL,
	id_categoria_financeiro INT NOT NULL,
	id_funcionario_financeiro INT NOT NULL,
	id_boleto_financeiro INT,
	desc_financeiro VARCHAR(50),
	data_financeiro DATE,
	tipo_financeiro INT NOT NULL,	-- [0] Receita [1]Despesa
	valor_financeiro DECIMAL NOT NULL, 
	FOREIGN KEY (id_funcionario_financeiro) REFERENCES tb_funcionario (id_funcionario),
	FOREIGN KEY (id_boleto_financeiro) REFERENCES tb_boleto (id_boleto),
	FOREIGN KEY (id_categoria_financeiro) REFERENCES tb_categoria_financeiro (id_categoria_financeiro)
);

CREATE TABLE tb_noticia(
	id_noticia SERIAL PRIMARY KEY NOT NULL,
	id_funcionario_noticia INT NOT NULL,
	id_func_edit_noticia INT NOT NULL,
	titulo_noticia VARCHAR(50),
	desc_noticia TEXT,
	data_noticia TIMESTAMP,
	data_edit_noticia TIMESTAMP,
	FOREIGN KEY (id_funcionario_noticia) REFERENCES tb_funcionario (id_funcionario),
	FOREIGN KEY (id_func_edit_noticia) REFERENCES tb_funcionario (id_funcionario)
);

-- Status autor/receptor [0] - Deletada	[1] - Nova	[2] - Aberta	[3] - Lixeira (Nova)	[4] - Lixeira (Aberta)
CREATE TABLE tb_mensagem(
	id_mensagem SERIAL PRIMARY KEY NOT NULL,
	id_autor_mensagem INT NOT NULL,
	id_receptor_mensagem INT NOT NULL,
	id_resposta_mensagem INT,
	status_autor_mensagem INT,
	status_receptor_mensagem INT,
	desc_mensagem TEXT,
	assunto_mensagem VARCHAR(30),
	data_mensagem TIMESTAMP,
	FOREIGN KEY (id_autor_mensagem) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_receptor_mensagem) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_resposta_mensagem) REFERENCES tb_mensagem (id_mensagem)
);

-- Status fim assembleia [0] - Finalizar [1] - Aguardando/Encerrando  [2] - Finalizado 
CREATE TABLE tb_assembleia(
	id_assembleia SERIAL PRIMARY KEY NOT NULL,
	id_criador_assembleia INT NOT NULL,
	id_presid_assembleia INT NOT NULL,
	tipo_assembleia INT NOT NULL,
	data_inicio_assembleia DATE NOT NULL,
	data_fim_assembleia DATE,
	parecer_assembleia TEXT,
	fim_assembleia INT,
	FOREIGN KEY (id_criador_assembleia) REFERENCES tb_funcionario (id_funcionario),
	FOREIGN KEY (id_presid_assembleia) REFERENCES tb_morador (id_morador)
);

CREATE TABLE tb_questao(
	id_questao SERIAL PRIMARY KEY NOT NULL,
	id_assembleia INT NOT NULL,
	titulo_questao VARCHAR(100),
	desc_questao VARCHAR(300),
	FOREIGN KEY (id_assembleia) REFERENCES tb_assembleia (id_assembleia)
);

CREATE TABLE tb_opc_questao(
	id_opc_questao SERIAL PRIMARY KEY NOT NULL,
	id_questao INT NOT NULL,
	desc_opc_questao VARCHAR(100),	
	FOREIGN KEY (id_questao) REFERENCES tb_questao (id_questao)
);

CREATE TABLE tb_arquivo(
	id_arquivo SERIAL PRIMARY KEY NOT NULL,
	id_questao_arquivo INT,
	id_noticia_arquivo INT,
	id_notificacao_arquivo INT,
	ext_arquivo VARCHAR(5),
	desc_arquivo VARCHAR(100),
	FOREIGN KEY (id_questao_arquivo) REFERENCES tb_questao (id_questao),
	FOREIGN KEY (id_noticia_arquivo) REFERENCES tb_noticia (id_noticia),
	FOREIGN KEY (id_notificacao_arquivo) REFERENCES tb_notificacao (id_notificacao)
);

CREATE TABLE tb_salao_festa(
	id_req_salao SERIAL PRIMARY KEY NOT NULL,
	id_morador_salao INT NOT NULL,
	id_funcionario_salao INT NOT NULL,
	data_salao DATE,
	FOREIGN KEY (id_morador_salao) REFERENCES tb_morador (id_morador),
	FOREIGN KEY (id_funcionario_salao) REFERENCES tb_funcionario (id_funcionario)
);

CREATE TABLE tb_voto(
	id_voto SERIAL PRIMARY KEY NOT NULL,
	id_morador INT NOT NULL,
	id_opc_questao INT NOT NULL,
	FOREIGN KEY (id_morador) REFERENCES tb_morador (id_morador),
	FOREIGN KEY (id_opc_questao) REFERENCES tb_opc_questao (id_opc_questao)
);

CREATE TABLE tb_tipo_atendimento(
	id_tipo_atendimento SERIAL PRIMARY KEY NOT NULL,
	desc_tipo_atendimento VARCHAR(100)	
);

CREATE TABLE tb_atendimento(
	id_atendimento SERIAL PRIMARY KEY NOT NULL,
	id_tipo_atendimento INT NOT NULL,
	id_morador_atendimento INT NOT NULL,
	id_funcionario_atendimento INT,
	id_notificacao_atendimento INT,
	desc_atendimento VARCHAR(255),
	data_abertura_atendimento DATE,
	data_fechamento_atendimento DATE,
	status_atendimento INT,
	FOREIGN KEY (id_tipo_atendimento) REFERENCES tb_tipo_atendimento (id_tipo_atendimento),
	FOREIGN KEY (id_morador_atendimento) REFERENCES tb_morador (id_morador),
	FOREIGN KEY (id_funcionario_atendimento) REFERENCES tb_funcionario (id_funcionario)
);

/* Tipo: [1]Atendimento [2]Questao */
CREATE TABLE tb_comentario(
	id_comentario SERIAL PRIMARY KEY NOT NULL,
	id_pessoa_comentario INT NOT NULL,
	tipo_comentario INT NOT NULL,	
	data_comentario TIMESTAMP NOT NULL,
	desc_comentario VARCHAR(200),
	id_atendimento_comentario INT,	
	id_questao_comentario INT,
	tipo_pessoa_comentario VARCHAR(2) NOT NULL,
	FOREIGN KEY (id_pessoa_comentario) REFERENCES tb_pessoa (id_pessoa),
	FOREIGN KEY (id_atendimento_comentario) REFERENCES tb_atendimento (id_atendimento),
	FOREIGN KEY (id_questao_comentario) REFERENCES tb_questao (id_questao)	
);


CREATE TABLE tb_log_sistema(
	id_log_sistema SERIAL PRIMARY KEY NOT NULL,
	id_operacao_log_sistema INT NOT NULL,	-- [1]Adicionar [2]Remover [3]Atualizar [4]Aprovar [5]Desligar [6]Modificar Acesso [7]Religar [8]Emitir [9]Recuperar acesso
	id_usuario_log_sistema INT NOT NULL,
	id_objeto_log_sistema INT NOT NULL,
	tipo_objeto_log_sistema INT NOT NULL,	-- [0]Responsavel [1]Dependente [2]Funcionario [3]Visitante [4]Noticia [5]Arquivo [6]Veiculo [7]Vaga Estacionamento [8]Notificacao [9]Boleto [10]Infração [11]Assembleia
	detalhe_log_sistema VARCHAR(200),
	data_log_sistema TIMESTAMP NOT NULL
);

CREATE TABLE tb_boleto(
	id_boleto SERIAL PRIMARY KEY NOT NULL,
	tipo_boleto INT NOT NULL, 				-- [1]Taxa de condominio [2]Multa 
	data_emissao_boleto TIMESTAMP NOT NULL,
	data_vencimento_boleto TIMESTAMP NOT NULL,
	data_referencia_boleto TIMESTAMP,
	id_morador_boleto INT NOT NULL,
	valor_boleto DECIMAL NOT NULL,	
	valor_multa_boleto DECIMAL,
	FOREIGN KEY (id_morador_boleto) REFERENCES tb_morador (id_morador)
);

CREATE TABLE tb_infracao(
	id_infracao SERIAL PRIMARY KEY NOT NULL,
	clas_infracao INT NOT NULL, 		-- [1]Leve [2]Media [3]Grave
	desc_infracao VARCHAR(100) NOT NULL	
);

CREATE TABLE tb_notificacao(
	id_notificacao SERIAL PRIMARY KEY NOT NULL,
	tipo_notificacao INT NOT NULL, 		-- [0]Advertencia [1]Multa
	data_referencia_notificacao DATE NOT NULL,
	data_emissao_notificacao DATE NOT NULL,
	desc_notificacao VARCHAR(300) NOT NULL,
	id_morador_notificacao INT NOT NULL,
	id_funcionario_notificacao INT NOT NULL,
	id_infracao_notificacao INT NOT NULL,
	id_boleto_notificacao INT,
	FOREIGN KEY (id_morador_notificacao) REFERENCES tb_morador (id_morador),
	FOREIGN KEY (id_funcionario_notificacao) REFERENCES tb_funcionario (id_funcionario),
	FOREIGN KEY (id_boleto_notificacao) REFERENCES tb_boleto (id_boleto)
);

CREATE TABLE tb_notificacao_atendimento (
	id_notificacao INTEGER NOT NULL REFERENCES tb_notificacao (id_notificacao),
	id_atendimento INTEGER NOT NULL REFERENCES tb_atendimento (id_atendimento)
);

CREATE TABLE tb_log_cadastro_morador(
	id_log_cadastro_morador SERIAL PRIMARY KEY NOT NULL,
	id_morador_log_cadastro_morador INT NOT NULL,                            
	tipo_morador_log_cadastro_morador INT NOT NULL,							-- [0]Responsavel [1]Dependente
	id_pessoa_aprovacao_log_cadastro_morador INT NOT NULL,
	tipo_pessoa_aprovacao_log_cadastro_morador INT NOT NULL,                -- [0]Sindico [1]Morador Responsavel
	data_aprovacao_log_cadastro_morador TIMESTAMP NOT NULL,
	id_pessoa_desligamento_log_cadastro_morador INT,				
	tipo_pessoa_desligamento_log_cadastro_morador INT,                      -- [0]Sindico [1]Morador Responsavel
	data_desligamento_log_cadastro_morador TIMESTAMP	
);

-- =============================================================================================================

INSERT INTO tb_apartamento(id_apartamento,bloco_apartamento,andar_apartamento) VALUES 
	(1,'A',0),(2,'A',0),(3,'A',0),(4,'A',0),
	(5,'A',1),(6,'A',1),(7,'A',1),(8,'A',1),
	(9,'A',2),(10,'A',2),(11,'A',2),(12,'A',2),
	(13,'A',3),(14,'A',3),(15,'A',3),(16,'A',3),
	(17,'A',4),(18,'A',4),(19,'A',4),(20,'A',4),
	
	(21,'B',0),(22,'B',0),(23,'B',0),(24,'B',0),
	(25,'B',1),(26,'B',1),(27,'B',1),(28,'B',1),
	(29,'B',2),(30,'B',2),(31,'B',2),(32,'B',2),
	(33,'B',3),(34,'B',3),(35,'B',3),(36,'B',3),
	(37,'B',4),(38,'B',4),(39,'B',4),(40,'B',4),
	
	(41,'C',0),(42,'C',0),(43,'C',0),(44,'C',0),
	(45,'C',1),(46,'C',1),(47,'C',1),(48,'C',1),
	(49,'C',2),(50,'C',2),(51,'C',2),(52,'C',2),
	(53,'C',3),(54,'C',3),(55,'C',3),(56,'C',3),
	(57,'C',4),(58,'C',4),(59,'C',4),(60,'C',4),
;

INSERT INTO tb_funcao(id_funcao,desc_funcao) VALUES (1,'Síndico'),(2,'Porteiro');
INSERT INTO tb_tipo_atendimento (desc_tipo_atendimento) VALUES ('PAUTAS'),('SUGESTÃO'),('RECLAMAÇÃO'),('VAGA DE GARAGEM'),('SALÃO DE FESTAS'),('FINANCEIRO'),('OUTROS');
INSERT INTO tb_vaga (id_vaga) VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20);
INSERT INTO tb_categoria_financeiro (desc_categoria) VALUES ('TAXA DE CONDOMÍNIO'),('MANUTENÇÃO'),('FUNCIONÁRIO'),('ENERGIA ELÉTRICA'),('JARDINAGEM'),('ÁGUA'),('GÁS'),('MULTA'),('TAXA DE MUDANÇA'),('TELEFONIA'),('PRODUTOS');
INSERT INTO tb_pessoa(id_pessoa,nome_pessoa,cpf_pessoa,email_pessoa,data_pessoa,sexo_pessoa,id_arquivo_pessoa) VALUES (1,'VITO ANDOLINI','55624311060','vito@aol.com','1961-01-01','M','1');
INSERT INTO tb_funcionario(id_funcionario,id_funcao,senha_funcionario,status_funcionario) VALUES (1,1,'e8d95a51f3af4a3b134bf6bb680a213a',1);
INSERT INTO tb_arquivo(id_arquivo,ext_arquivo) VALUES (1,'.PNG');

INSERT INTO tb_financeiro (id_categoria_financeiro,id_funcionario_financeiro, desc_financeiro, data_financeiro, tipo_financeiro, valor_financeiro) values 
(6,1,'MENSAL','2019-1-1',1,100),
(4,1,'MENSAL','2019-2-2',1,200),
(6,1,'MENSAL','2019-3-3',1,300),
(4,1,'MENSAL','2019-4-4',1,100),
(6,1,'MENSAL','2019-5-5',1,200),
(4,1,'MENSAL','2019-6-6',1,300),
(6,1,'MENSAL','2019-7-7',1,100),
(4,1,'MENSAL','2019-8-8',1,200),
(6,1,'MENSAL','2019-9-9',1,300),
(4,1,'MENSAL','2019-10-10',1,100),
(6,1,'MENSAL','2019-11-11',1,200),
(4,1,'MENSAL','2019-12-12',1,300),
(6,1,'MENSAL','2020-1-13',1,100),
(4,1,'MENSAL','2020-2-14',1,200),
(6,1,'MENSAL','2020-3-15',1,300),
(4,1,'MENSAL','2020-4-16',1,100),
(6,1,'MENSAL','2020-5-17',1,200),
(4,1,'MENSAL','2020-6-18',1,300),
(6,1,'MENSAL','2020-7-19',1,100),
(4,1,'MENSAL','2020-8-20',1,200),
(6,1,'MENSAL','2020-9-21',1,300),
(4,1,'MENSAL','2020-10-22',1,100),
(6,1,'MENSAL','2020-11-23',1,200),
(4,1,'MENSAL','2020-12-24',1,300),
(6,1,'MENSAL','2021-1-25',1,100),
(4,1,'MENSAL','2021-2-26',1,200),
(6,1,'MENSAL','2021-3-27',1,300),
(4,1,'MENSAL','2021-4-28',1,100),
(6,1,'MENSAL','2021-5-29',1,200),
(4,1,'MENSAL','2021-6-30',1,300),
(6,1,'MENSAL','2021-7-1',1,100),
(4,1,'MENSAL','2021-8-2',1,200),
(6,1,'MENSAL','2021-9-3',1,300),
(4,1,'MENSAL','2021-10-4',1,100),
(6,1,'MENSAL','2021-11-5',1,200),
(6,1,'MENSAL','2021-12-6',1,300);

-- Limpar tabelas
-- =============================================================================================================

delete from tb_arquivo;
delete from tb_comentario;
delete from tb_financeiro;
delete from tb_log_cadastro_morador;
delete from tb_log_sistema;
delete from tb_mensagem;
delete from tb_noticia;
delete from tb_notificacao;
delete from tb_notificacao_atendimento;
delete from tb_reg_entrada_saida_pessoa;
delete from tb_salao_festa;
delete from tb_veiculo;
delete from tb_visita_pessoa;
delete from tb_visitante;
delete from tb_voto;
delete from tb_visita;
delete from tb_opc_questao;
delete from tb_questao;
delete from tb_boleto;
delete from tb_atendimento;
delete from tb_assembleia;
delete from tb_funcionario;
delete from tb_morador;
delete from tb_pessoa;
