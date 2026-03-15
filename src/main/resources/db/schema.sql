-- ============================================================
-- SCHEMA — Sistema de Festas
-- Gerado a partir das entidades JPA do projeto
-- O Hibernate (ddl-auto=update) cria/atualiza as tabelas
-- automaticamente. Este arquivo serve como referência e para
-- provisionamento inicial via Docker.
-- ============================================================

CREATE DATABASE IF NOT EXISTS festasdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE festasdb;

-- ------------------------------------------------------------
-- roles
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_nome (nome)
);

-- ------------------------------------------------------------
-- usuarios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
  id    BIGINT       NOT NULL AUTO_INCREMENT,
  login VARCHAR(255) NOT NULL,
  senha VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_login (login)
);

-- ------------------------------------------------------------
-- usuario_roles  (N:N)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario_roles (
  usuario_id BIGINT NOT NULL,
  role_id    BIGINT NOT NULL,
  PRIMARY KEY (usuario_id, role_id),
  CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT fk_ur_role    FOREIGN KEY (role_id)    REFERENCES roles    (id)
);

-- ------------------------------------------------------------
-- cliente
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  nome            VARCHAR(255) NOT NULL,
  telefone        VARCHAR(255),
  status_cadastro VARCHAR(50),
  usuario_id      BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cliente_usuario (usuario_id),
  CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- ------------------------------------------------------------
-- tipo_evento
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tipo_evento (
  id                 BIGINT       NOT NULL AUTO_INCREMENT,
  nome               VARCHAR(255) NOT NULL,
  descricao          TEXT,
  capacidade_minima  INT,
  capacidade_maxima  INT,
  ativo              TINYINT(1),
  PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- tema_festa
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tema_festa (
  id         BIGINT         NOT NULL AUTO_INCREMENT,
  nome       VARCHAR(255)   NOT NULL,
  descricao  TEXT,
  preco_base DECIMAL(19, 2),
  ativo      TINYINT(1),
  PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- endereco
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS endereco (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  rua         VARCHAR(255) NOT NULL,
  numero      VARCHAR(50)  NOT NULL,
  complemento VARCHAR(255),
  bairro      VARCHAR(255) NOT NULL,
  cidade      VARCHAR(255) NOT NULL,
  estado      VARCHAR(2)   NOT NULL,
  cep         VARCHAR(9)   NOT NULL,
  PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- solicitacao_orcamento
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS solicitacao_orcamento (
  id                     BIGINT         NOT NULL AUTO_INCREMENT,
  cliente_id             BIGINT,
  data_evento            DATE           NOT NULL,
  endereco_id            BIGINT         NOT NULL,
  quantidade_convidados  INT            NOT NULL,
  precisa_mesas_cadeiras TINYINT(1),
  tipo_evento_id         BIGINT         NOT NULL,
  valor_pretendido       DECIMAL(19, 2),
  status_orcamento       VARCHAR(50),
  data_criacao           DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_so_cliente     FOREIGN KEY (cliente_id)     REFERENCES cliente    (id),
  CONSTRAINT fk_so_endereco    FOREIGN KEY (endereco_id)    REFERENCES endereco   (id),
  CONSTRAINT fk_so_tipo_evento FOREIGN KEY (tipo_evento_id) REFERENCES tipo_evento (id)
);

-- ------------------------------------------------------------
-- solicitacao_tema  (N:N)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS solicitacao_tema (
  solicitacao_id BIGINT NOT NULL,
  tema_id        BIGINT NOT NULL,
  PRIMARY KEY (solicitacao_id, tema_id),
  CONSTRAINT fk_st_solicitacao FOREIGN KEY (solicitacao_id) REFERENCES solicitacao_orcamento (id),
  CONSTRAINT fk_st_tema        FOREIGN KEY (tema_id)        REFERENCES tema_festa            (id)
);

-- ------------------------------------------------------------
-- Dados iniciais — roles obrigatórias
-- (o DataInitializer do Spring também faz isso em runtime)
-- ------------------------------------------------------------
INSERT IGNORE INTO roles (nome) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (nome) VALUES ('ROLE_ADMIN');
