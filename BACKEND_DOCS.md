# Documentação do Backend — Sistema de Festas

Guia completo para integração do frontend Angular com a API REST.

---

## Sumário

1. [Visão Geral](#visão-geral)
2. [Configuração da URL Base](#configuração-da-url-base)
3. [Autenticação JWT](#autenticação-jwt)
4. [Papéis de Usuário (Roles)](#papéis-de-usuário-roles)
5. [Modelos de Dados](#modelos-de-dados)
6. [Endpoints — Auth](#endpoints--auth)
7. [Endpoints — Clientes](#endpoints--clientes)
8. [Endpoints — Endereços](#endpoints--endereços)
9. [Endpoints — Temas de Festa](#endpoints--temas-de-festa)
10. [Endpoints — Tipos de Evento](#endpoints--tipos-de-evento)
11. [Endpoints — Solicitações de Orçamento](#endpoints--solicitações-de-orçamento)
12. [Endpoints — Admin](#endpoints--admin)
13. [Respostas de Erro](#respostas-de-erro)
14. [Regras de Negócio](#regras-de-negócio)
15. [Fluxo Completo de Uso](#fluxo-completo-de-uso)

---

## Visão Geral

API REST construída com **Spring Boot 3.1** + **Java 17**.
Autenticação via **JWT Bearer Token**.
Banco de dados **MySQL**.

---

## Configuração da URL Base

| Ambiente    | URL                            |
|-------------|--------------------------------|
| Local       | `http://localhost:8080`        |
| Produção    | `http://18.230.20.100:8080`    |

Todos os endpoints começam com `/api/`.

---

## Autenticação JWT

### Como funciona

1. O frontend faz `POST /api/auth/login` com email e senha
2. A API retorna um token JWT
3. Em todas as requisições protegidas, o frontend envia o token no header:

```
Authorization: Bearer <token>
```

### Validade do token

O token expira em **2 horas**. Após isso o usuário precisa fazer login novamente.

### Onde guardar o token

Guardar no `localStorage` do navegador (já implementado no `AuthService` do frontend).

---

## Papéis de Usuário (Roles)

Existem dois papéis no sistema:

| Role         | Descrição                                      |
|--------------|------------------------------------------------|
| `ROLE_USER`  | Usuário comum — acesso limitado às próprias solicitações |
| `ROLE_ADMIN` | Administrador — acesso total ao sistema        |

### O que cada role pode fazer

| Recurso                  | USER | ADMIN |
|--------------------------|------|-------|
| Login / Registro         | ✅   | ✅    |
| Ver temas (GET)          | ✅   | ✅    |
| Ver tipos de evento (GET)| ✅   | ✅    |
| Criar/editar/deletar temas e tipos | ❌ | ✅ |
| Ver/criar/editar clientes | ❌  | ✅    |
| Ver/criar/editar endereços | ❌ | ✅    |
| Criar solicitação de orçamento | ✅ | ✅ |
| Ver suas próprias solicitações | ✅ | ✅ |
| Ver todas as solicitações | ❌  | ✅    |
| Gerenciar usuários       | ❌   | ✅    |

---

## Modelos de Dados

### Usuario
```typescript
interface Usuario {
  id: number;
  login: string; // email
  // senha nunca é retornada pela API
}
```

### Cliente
```typescript
interface Cliente {
  id: number;
  nome: string;           // obrigatório
  telefone: string;       // obrigatório
  statusCadastro: string; // "COMPLETO" ou "INCOMPLETO" (definido automaticamente)
  usuario?: Usuario;      // usuário vinculado (opcional)
}
```

> **Atenção:** `statusCadastro` é definido automaticamente pelo backend:
> - Se tem telefone → `"COMPLETO"`
> - Se não tem telefone → `"INCOMPLETO"`

### Endereco
```typescript
interface Endereco {
  id: number;
  rua: string;         // obrigatório
  numero: string;      // obrigatório
  complemento?: string;
  bairro: string;      // obrigatório
  cidade: string;      // obrigatório
  estado: string;      // obrigatório
  cep: string;         // obrigatório
}
```

### TemaFesta
```typescript
interface TemaFesta {
  id: number;
  nome: string;          // obrigatório
  descricao?: string;
  precoBase?: number;
  ativo: boolean;        // true por padrão
}
```

### TipoEvento
```typescript
interface TipoEvento {
  id: number;
  nome: string;           // obrigatório
  descricao?: string;
  capacidadeMinima?: number;
  capacidadeMaxima?: number;
}
```

### SolicitacaoOrcamento
```typescript
interface SolicitacaoOrcamento {
  id: number;
  cliente: { id: number };             // obrigatório
  dataEvento: string;                  // formato "YYYY-MM-DD", obrigatório
  endereco: Endereco;                  // objeto completo, obrigatório
  quantidadeConvidados: number;        // obrigatório
  precisaMesasCadeiras?: boolean;
  tipoEvento: { id: number };          // obrigatório
  temas?: Array<{ id: number }>;       // lista de temas
  valorPretendido?: number;
  statusOrcamento: string;             // "PENDENTE" | "APROVADO" | "REJEITADO"
  dataCriacao: string;                 // preenchido automaticamente pelo backend
}
```

> **Atenção:**
> - `statusOrcamento` é definido como `"PENDENTE"` automaticamente ao criar
> - `dataCriacao` é preenchido automaticamente pelo backend
> - O `endereco` dentro de solicitação é criado junto (cascade) — não precisa criar endereço separado

---

## Endpoints — Auth

### POST /api/auth/login
Autentica o usuário e retorna o token JWT.

**Não requer token.**

**Request:**
```json
{
  "login": "usuario@email.com",
  "senha": "senha123"
}
```

**Response 200:**
```json
{
  "tokenJWT": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response 401** (credenciais erradas):
```json
"Falha na autenticação: Login ou senha incorretos."
```

---

### POST /api/auth/registrar
Cria um novo usuário com `ROLE_USER`.

**Não requer token.**

**Request:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response 200:** sem body

**Response 400** (email já cadastrado):
```json
{
  "status": 400,
  "erro": "Usuário já existente!"
}
```

---

## Endpoints — Clientes

> Todos os endpoints de clientes exigem **ROLE_ADMIN**.

### GET /api/clientes
Lista todos os clientes.

**Response 200:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "telefone": "11999999999",
    "statusCadastro": "COMPLETO",
    "usuario": {
      "id": 2,
      "login": "joao@email.com"
    }
  }
]
```

---

### GET /api/clientes/{id}
Busca cliente por ID.

**Response 200:** objeto Cliente
**Response 404:** não encontrado

---

### POST /api/clientes
Cria um novo cliente.

**Request:**
```json
{
  "nome": "João Silva",
  "telefone": "11999999999",
  "usuario": { "id": 2 }
}
```
> O campo `usuario` é opcional. Use para vincular o cliente a um usuário cadastrado.

**Response 201:** objeto Cliente criado

---

### PUT /api/clientes/{id}
Atualiza um cliente.

**Request:** mesmo formato do POST
**Response 200:** objeto Cliente atualizado

---

### DELETE /api/clientes/{id}
Remove um cliente.

**Response 204:** sem body

---

### GET /api/clientes/buscar?nome={nome}
Busca clientes por nome (parcial, case-insensitive).

**Response 200:** lista de Clientes

---

### GET /api/clientes/telefone?telefone={telefone}
Busca cliente por telefone exato.

**Response 200:** objeto Cliente
**Response 404:** não encontrado

---

### GET /api/clientes/status/{status}
Filtra clientes por status (`COMPLETO` ou `INCOMPLETO`).

**Response 200:** lista de Clientes

---

## Endpoints — Endereços

> Todos os endpoints de endereços exigem **ROLE_ADMIN**.

### GET /api/enderecos
Lista todos os endereços.

### GET /api/enderecos/{id}
Busca endereço por ID.

### POST /api/enderecos
Cria um endereço.

**Request:**
```json
{
  "rua": "Rua das Flores",
  "numero": "123",
  "complemento": "Apto 4",
  "bairro": "Centro",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01310-100"
}
```
**Response 201:** objeto Endereco criado

### PUT /api/enderecos/{id}
Atualiza um endereço.

### DELETE /api/enderecos/{id}
Remove um endereço.

### GET /api/enderecos/cidade?cidade={cidade}
Busca endereços por cidade (parcial, case-insensitive).

### GET /api/enderecos/estado/{estado}
Filtra endereços por estado (ex: `SP`, `RJ`).

---

## Endpoints — Temas de Festa

> **GET** é público (sem token). **POST/PUT/DELETE** exigem **ROLE_ADMIN**.

### GET /api/temas
Lista todos os temas.

**Response 200:**
```json
[
  {
    "id": 1,
    "nome": "Jardim Encantado",
    "descricao": "Tema floral e delicado",
    "precoBase": 1500.00,
    "ativo": true
  }
]
```

### GET /api/temas/{id}
Busca tema por ID.

### POST /api/temas
Cria um tema. (ADMIN)

**Request:**
```json
{
  "nome": "Jardim Encantado",
  "descricao": "Tema floral e delicado",
  "precoBase": 1500.00
}
```
> `ativo` é definido como `true` automaticamente se não for enviado.

**Response 201:** objeto TemaFesta criado

### PUT /api/temas/{id}
Atualiza um tema. (ADMIN)

### DELETE /api/temas/{id}
Remove um tema. (ADMIN)

### GET /api/temas/buscar?nome={nome}
Busca temas por nome (parcial, case-insensitive). Público.

### GET /api/temas/ativos
Lista apenas temas com `ativo = true`. Público.

---

## Endpoints — Tipos de Evento

> **GET** é público (sem token). **POST/PUT/DELETE** exigem **ROLE_ADMIN**.

### GET /api/tipos-evento
Lista todos os tipos de evento.

**Response 200:**
```json
[
  {
    "id": 1,
    "nome": "Aniversário",
    "descricao": "Festa de aniversário",
    "capacidadeMinima": 10,
    "capacidadeMaxima": 200
  }
]
```

### GET /api/tipos-evento/{id}
Busca tipo de evento por ID.

### POST /api/tipos-evento
Cria um tipo de evento. (ADMIN)

**Request:**
```json
{
  "nome": "Aniversário",
  "descricao": "Festa de aniversário",
  "capacidadeMinima": 10,
  "capacidadeMaxima": 200
}
```

### PUT /api/tipos-evento/{id}
Atualiza um tipo de evento. (ADMIN)

### DELETE /api/tipos-evento/{id}
Remove um tipo de evento. (ADMIN)

### GET /api/tipos-evento/buscar?nome={nome}
Busca por nome (parcial, case-insensitive). Público.

### GET /api/tipos-evento/capacidade/{capacidade}
Retorna tipos de evento que comportam determinada quantidade de pessoas.

**Exemplo:** `/api/tipos-evento/capacidade/50`
Retorna tipos onde `capacidadeMinima <= 50 <= capacidadeMaxima`.

---

## Endpoints — Solicitações de Orçamento

> Requer autenticação. **USER** vê apenas as próprias. **ADMIN** vê todas.

### GET /api/solicitacoes
- **ADMIN:** retorna todas as solicitações
- **USER:** retorna apenas as solicitações vinculadas ao cliente do usuário logado

**Response 200:**
```json
[
  {
    "id": 1,
    "cliente": { "id": 1, "nome": "João Silva", "telefone": "11999999999" },
    "dataEvento": "2025-12-20",
    "endereco": {
      "id": 1,
      "rua": "Rua das Flores",
      "numero": "123",
      "bairro": "Centro",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310-100"
    },
    "quantidadeConvidados": 50,
    "precisaMesasCadeiras": true,
    "tipoEvento": { "id": 1, "nome": "Aniversário" },
    "temas": [{ "id": 1, "nome": "Jardim Encantado" }],
    "valorPretendido": 5000.00,
    "statusOrcamento": "PENDENTE",
    "dataCriacao": "2025-03-14T17:00:00"
  }
]
```

---

### GET /api/solicitacoes/{id}
- **ADMIN:** pode ver qualquer solicitação
- **USER:** só pode ver se a solicitação pertence ao seu cliente

**Response 403** se o USER tentar ver solicitação de outro:
```json
{
  "status": 403,
  "erro": "Você só pode visualizar suas próprias solicitações"
}
```

---

### POST /api/solicitacoes
Cria uma nova solicitação.

**Request:**
```json
{
  "cliente": { "id": 1 },
  "dataEvento": "2025-12-20",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  },
  "quantidadeConvidados": 50,
  "precisaMesasCadeiras": true,
  "tipoEvento": { "id": 1 },
  "temas": [{ "id": 1 }, { "id": 2 }],
  "valorPretendido": 5000.00
}
```

> **Importante:**
> - O endereço é criado automaticamente junto com a solicitação
> - `statusOrcamento` é definido como `"PENDENTE"` automaticamente
> - `dataCriacao` é preenchido automaticamente

**Response 201:** objeto SolicitacaoOrcamento criado

**Response 400** (sem cliente):
```json
{
  "status": 400,
  "erro": "Não é possível criar solicitação sem associar a um cliente"
}
```

---

### PUT /api/solicitacoes/{id}
Atualiza uma solicitação.

- **USER:** só pode editar a própria
- **ADMIN:** pode editar qualquer uma

**Request:** mesmo formato do POST

---

### DELETE /api/solicitacoes/{id}
Remove uma solicitação.

**Response 204:** sem body

---

### GET /api/solicitacoes/status?status={status}
Filtra solicitações por status.

**Valores possíveis:** `PENDENTE`, `APROVADO`, `REJEITADO`

---

### GET /api/solicitacoes/cliente/{clienteId}
Retorna todas as solicitações de um cliente específico.

- **USER:** só funciona se o cliente pertencer ao usuário logado

---

## Endpoints — Admin

> Todos exigem **ROLE_ADMIN**.

### GET /api/admin/usuarios
Lista todos os usuários com suas roles.

**Response 200:**
```json
[
  {
    "id": 1,
    "email": "admin@festas.com",
    "roles": ["ROLE_ADMIN"]
  },
  {
    "id": 2,
    "email": "joao@email.com",
    "roles": ["ROLE_USER"]
  }
]
```

---

### POST /api/admin/usuarios/{id}/promover-admin
Promove um usuário para ADMIN.

**Response 200:**
```json
{
  "message": "Usuário promovido a ADMIN com sucesso",
  "email": "joao@email.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

---

### DELETE /api/admin/usuarios/{id}/remover-admin
Remove o papel de ADMIN de um usuário (mantém ROLE_USER).

---

## Respostas de Erro

Todos os erros seguem o mesmo formato:

```json
{
  "timestamp": "2025-03-14T17:00:00",
  "status": 400,
  "erro": "Mensagem descritiva do erro"
}
```

Erros de validação de campos retornam:
```json
{
  "timestamp": "2025-03-14T17:00:00",
  "status": 400,
  "erro": "Validação falhou",
  "campos": {
    "nome": "O campo nome é obrigatório",
    "telefone": "O campo telefone é obrigatório"
  }
}
```

| Status | Significado                                      |
|--------|--------------------------------------------------|
| 200    | Sucesso                                          |
| 201    | Criado com sucesso                               |
| 204    | Deletado com sucesso (sem body)                  |
| 400    | Dados inválidos / regra de negócio violada       |
| 401    | Não autenticado (token ausente ou inválido)      |
| 403    | Sem permissão para este recurso                  |
| 404    | Recurso não encontrado                           |
| 500    | Erro interno do servidor                         |

---

## Regras de Negócio

### Cliente
- `statusCadastro` é calculado automaticamente: tem telefone = `COMPLETO`, sem telefone = `INCOMPLETO`
- Um cliente pode ser vinculado a um usuário (`usuario_id`), mas não é obrigatório
- Cada usuário só pode ter um cliente vinculado (relação 1:1)

### Solicitação de Orçamento
- Obrigatoriamente precisa de um cliente com ID válido
- O endereço é criado junto com a solicitação (não precisa existir antes)
- Status inicial sempre é `PENDENTE`
- Um USER só pode ver/editar as solicitações vinculadas ao seu cliente
- Um ADMIN pode ver e editar qualquer solicitação

### Tema de Festa
- `ativo` é `true` por padrão ao criar
- Temas inativos não aparecem em `/api/temas/ativos`

### Usuário
- Ao registrar, recebe `ROLE_USER` automaticamente
- O admin padrão é criado na primeira execução do sistema

---

## Fluxo Completo de Uso

### Fluxo do Usuário Comum (USER)

```
1. POST /api/auth/registrar        → cria conta
2. POST /api/auth/login            → obtém token
3. GET  /api/temas                 → vê temas disponíveis (sem token)
4. GET  /api/tipos-evento          → vê tipos de evento (sem token)
5. POST /api/solicitacoes          → cria orçamento (precisa de cliente_id)
6. GET  /api/solicitacoes          → vê seus orçamentos
```

> **Atenção:** Para criar uma solicitação, o usuário precisa ter um `cliente` vinculado.
> Quem vincula o cliente ao usuário é o ADMIN.

---

### Fluxo do Administrador (ADMIN)

```
1. POST /api/auth/login                         → login com conta admin
2. POST /api/clientes                           → cadastra cliente
3. PUT  /api/clientes/{id} + usuario.id         → vincula cliente a um usuário
4. POST /api/temas                              → cria temas
5. POST /api/tipos-evento                       → cria tipos de evento
6. GET  /api/solicitacoes                       → vê todos os orçamentos
7. PUT  /api/solicitacoes/{id}                  → aprova/rejeita orçamento
8. POST /api/admin/usuarios/{id}/promover-admin → promove usuário
```

---

## CORS

O backend aceita requisições dos seguintes domínios:

```
http://localhost:4200
http://localhost:3000
```
> Para produção, configure a variável de ambiente `CORS_ALLOWED_ORIGINS` no servidor.

Headers permitidos: `Authorization`, `Content-Type`, `Accept`, `Origin`, `X-Requested-With`

---

## Swagger UI

A documentação interativa da API está disponível em:

```
http://localhost:8080/swagger-ui.html
```

Permite testar os endpoints diretamente no navegador.
