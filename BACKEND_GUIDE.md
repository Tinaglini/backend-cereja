# Backend Guide — Tia Cereja (sistema-festas)

> Este documento foi criado para a instância Claude Code que cuida do **frontend Angular**.
> O backend foi reescrito/modificado — use este guia como fonte da verdade para integração.

---

## Visão Geral

| Item | Valor |
|------|-------|
| Framework | Spring Boot 3.1.0 |
| Java | 17 |
| Banco de dados | MySQL 8.0 |
| Autenticação | JWT (HS256, 2 horas) |
| Porta padrão | `8080` |
| Base URL | `http://localhost:8080/api` |
| Documentação interativa | `http://localhost:8080/swagger-ui/index.html` |

---

## Variáveis de Ambiente necessárias

O backend **não sobe** sem essas variáveis:

| Variável | Obrigatória | Padrão | Descrição |
|----------|------------|--------|-----------|
| `JWT_SECRET` | ✅ | — | Segredo para assinar os tokens JWT |
| `DB_PASSWORD` | ✅ | — | Senha do banco MySQL |
| `DB_USERNAME` | ❌ | `root` | Usuário do banco |
| `ADMIN_PASSWORD` | ✅ | — | Senha do admin padrão criado no boot |
| `ADMIN_EMAIL` | ❌ | `admin@festas.com` | Email do admin padrão |
| `CORS_ALLOWED_ORIGINS` | ❌ | `http://localhost:4200,http://localhost:3000` | Origins permitidas |

---

## CORS

O backend já está configurado para aceitar requisições do Angular em `http://localhost:4200`.

**Configuração atual:**
- Allowed Origins: `CORS_ALLOWED_ORIGINS` (default inclui `localhost:4200`)
- Allowed Methods: `GET, POST, PUT, DELETE, OPTIONS, PATCH`
- Allowed Headers: `Authorization, Content-Type, Accept, Origin, X-Requested-With`
- Exposed Headers: `Authorization, Content-Type`
- Credentials: **permitido** (`withCredentials: true` no Angular)
- Max Age: `3600s`

---

## Autenticação

### Como funciona

1. Frontend faz `POST /api/auth/login` → recebe `{ "token": "eyJ..." }`
2. Guarda o token (localStorage ou memory)
3. Em toda requisição autenticada, envia: `Authorization: Bearer <token>`
4. Token expira em **2 horas**

### Papéis (Roles)

| Role | Descrição |
|------|-----------|
| `ROLE_USER` | Usuário comum — cadastrado automaticamente no registro |
| `ROLE_ADMIN` | Administrador — pode gerenciar tudo |

O token JWT contém uma claim `"roles"` com a lista de papéis do usuário.

---

## Todos os Endpoints

### Autenticação — `/api/auth` (público)

#### `POST /api/auth/registrar`
Cria um novo usuário. Automaticamente atribui `ROLE_USER` e cria o perfil `Cliente` com status `INCOMPLETO`.

**Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```
**Response:** `200 OK` (sem body)

---

#### `POST /api/auth/login`
Autentica e retorna JWT.

**Body:**
```json
{
  "login": "joao@email.com",
  "senha": "senha123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Administração de Usuários — `/api/admin/usuarios` (ROLE_ADMIN)

#### `GET /api/admin/usuarios`
Lista todos os usuários com seus papéis.

#### `POST /api/admin/usuarios/{id}/promover-admin`
Adiciona `ROLE_ADMIN` ao usuário.

**Response:**
```json
{
  "message": "Usuário promovido a admin com sucesso",
  "email": "joao@email.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

#### `DELETE /api/admin/usuarios/{id}/remover-admin`
Remove `ROLE_ADMIN` do usuário (mantém `ROLE_USER`).

---

### Clientes — `/api/clientes` (ROLE_ADMIN)

> Clientes são criados automaticamente no registro. O admin gerencia aqui.

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/api/clientes` | Lista todos |
| GET | `/api/clientes/{id}` | Busca por ID |
| POST | `/api/clientes` | Cria cliente (201) |
| PUT | `/api/clientes/{id}` | Atualiza |
| DELETE | `/api/clientes/{id}` | Remove (204) |
| GET | `/api/clientes/buscar?nome=X` | Busca por nome (case-insensitive) |
| GET | `/api/clientes/telefone?telefone=X` | Busca por telefone |
| GET | `/api/clientes/status/{status}` | Filtra por `COMPLETO` ou `INCOMPLETO` |

**Campos do Cliente:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "telefone": "(11) 99999-9999",
  "statusCadastro": "COMPLETO",
  "usuario": { "id": 1, "login": "joao@email.com" }
}
```
> `statusCadastro` é calculado automaticamente: `COMPLETO` se tiver telefone, `INCOMPLETO` se não tiver.

---

### Temas de Festa — `/api/temas`

> GET é público. POST/PUT/DELETE requerem `ROLE_ADMIN`.

| Método | Path | Auth | Descrição |
|--------|------|------|-----------|
| GET | `/api/temas` | Público | Lista todos |
| GET | `/api/temas/{id}` | Público | Busca por ID |
| GET | `/api/temas/ativos` | Público | Lista apenas ativos |
| GET | `/api/temas/buscar?nome=X` | Público | Busca por nome |
| POST | `/api/temas` | ADMIN | Cria tema (201) |
| PUT | `/api/temas/{id}` | ADMIN | Atualiza |
| DELETE | `/api/temas/{id}` | ADMIN | Remove (204) |

**Campos do TemaFesta:**
```json
{
  "id": 1,
  "nome": "Festa Junina",
  "descricao": "Tema com decoração caipira",
  "precoBase": 1500.00,
  "ativo": true
}
```

---

### Tipos de Evento — `/api/tipos-evento`

> GET é público. POST/PUT/DELETE/PATCH requerem `ROLE_ADMIN`.

| Método | Path | Auth | Descrição |
|--------|------|------|-----------|
| GET | `/api/tipos-evento` | Público | Lista todos |
| GET | `/api/tipos-evento/{id}` | Público | Busca por ID |
| GET | `/api/tipos-evento/ativos` | Público | Lista apenas ativos |
| GET | `/api/tipos-evento/buscar?nome=X` | Público | Busca por nome |
| GET | `/api/tipos-evento/capacidade/{n}` | Público | Tipos que comportam N pessoas |
| POST | `/api/tipos-evento` | ADMIN | Cria tipo (201) |
| PUT | `/api/tipos-evento/{id}` | ADMIN | Atualiza |
| DELETE | `/api/tipos-evento/{id}` | ADMIN | Remove (204) |
| PATCH | `/api/tipos-evento/{id}/status` | ADMIN | Alterna ativo/inativo |

**Campos do TipoEvento:**
```json
{
  "id": 1,
  "nome": "Casamento",
  "descricao": "Evento nupcial completo",
  "capacidadeMinima": 50,
  "capacidadeMaxima": 300,
  "ativo": true
}
```

---

### Endereços — `/api/enderecos` (ROLE_ADMIN)

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/api/enderecos` | Lista todos |
| GET | `/api/enderecos/{id}` | Busca por ID |
| POST | `/api/enderecos` | Cria (201) |
| PUT | `/api/enderecos/{id}` | Atualiza |
| DELETE | `/api/enderecos/{id}` | Remove (204) |
| GET | `/api/enderecos/cidade?cidade=X` | Busca por cidade |
| GET | `/api/enderecos/estado/{uf}` | Busca por estado (ex: SP) |

**Campos do Endereco:**
```json
{
  "id": 1,
  "rua": "Rua das Flores",
  "numero": "123",
  "complemento": "Apto 4",
  "bairro": "Centro",
  "cidade": "São Paulo",
  "estado": "SP",
  "cep": "01001-000"
}
```

---

### Solicitações de Orçamento — `/api/solicitacoes` (ROLE_USER ou ROLE_ADMIN)

> Esta é a entidade principal do sistema. Cada usuário só vê e edita as próprias solicitações. O admin vê todas.

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/api/solicitacoes` | USER: só as suas. ADMIN: todas |
| GET | `/api/solicitacoes/{id}` | Busca por ID (verifica dono) |
| POST | `/api/solicitacoes` | Cria solicitação (201) |
| PUT | `/api/solicitacoes/{id}` | Atualiza (verifica dono) |
| DELETE | `/api/solicitacoes/{id}` | Remove (204) |
| GET | `/api/solicitacoes/status?status=X` | Filtra por status |
| GET | `/api/solicitacoes/cliente/{clienteId}` | Solicitações de um cliente |

**Campos da SolicitacaoOrcamento:**
```json
{
  "id": 1,
  "cliente": { "id": 1, "nome": "João Silva" },
  "dataEvento": "2024-12-31",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01001-000"
  },
  "quantidadeConvidados": 100,
  "precisaMesasCadeiras": true,
  "tipoEvento": { "id": 1, "nome": "Casamento" },
  "valorPretendido": 5000.00,
  "statusOrcamento": "PENDENTE",
  "dataCriacao": "2024-03-15T10:30:00",
  "temas": [
    { "id": 1, "nome": "Festa Junina" }
  ]
}
```

**Valores possíveis para `statusOrcamento`:**
- `PENDENTE` — padrão ao criar
- `APROVADO`
- `REJEITADO`

> `dataCriacao` e `statusOrcamento` inicial são definidos automaticamente pelo backend. O frontend **não precisa enviá-los** no POST.

---

### Endpoints de Teste

| Método | Path | Auth | Resposta |
|--------|------|------|----------|
| GET | `/api/public/test` | Nenhuma | Mensagem pública |
| GET | `/api/user/test` | ROLE_USER ou ADMIN | Mensagem para user |
| GET | `/api/admin/test` | ROLE_ADMIN | Mensagem para admin |

---

## Fluxo de Autenticação (passo a passo para o Angular)

```
1. POST /api/auth/login  →  recebe { token }
2. Salvar token (localStorage recomendado)
3. HttpInterceptor: adicionar header em toda requisição autenticada:
   Authorization: Bearer <token>
4. Se 401 → token expirado → redirecionar para /login
5. Verificar role do usuário decodificando o JWT:
   - claim "roles": ["ROLE_USER"] ou ["ROLE_USER", "ROLE_ADMIN"]
```

**Decodificação do JWT no Angular (jwt-decode já está instalado):**
```typescript
import { jwtDecode } from 'jwt-decode';

const decoded: any = jwtDecode(token);
const roles: string[] = decoded.roles; // ["ROLE_USER", "ROLE_ADMIN"]
const email: string = decoded.sub;     // email do usuário
const exp: number = decoded.exp;       // timestamp de expiração
```

---

## Formato de Erros

Todos os erros seguem este padrão:

```json
{
  "timestamp": "2024-03-15T10:30:45.123456",
  "status": 404,
  "erro": "Recurso não encontrado",
  "campos": {}
}
```

Para erros de validação (400), `campos` contém o campo e a mensagem:
```json
{
  "timestamp": "2024-03-15T10:30:45.123456",
  "status": 400,
  "erro": "Erro de validação",
  "campos": {
    "nome": "não deve estar em branco",
    "email": "deve ser um endereço de e-mail bem formado"
  }
}
```

**Códigos HTTP usados:**

| Código | Situação |
|--------|----------|
| 200 | OK |
| 201 | Criado com sucesso |
| 204 | Deletado com sucesso (sem body) |
| 400 | Dados inválidos / validação falhou |
| 401 | Não autenticado (sem token ou token inválido) |
| 403 | Sem permissão (role insuficiente ou não é dono) |
| 404 | Recurso não encontrado |
| 500 | Erro interno |

---

## Relacionamento entre Entidades

```
Usuario (1) ──── (1) Cliente
                      │
                      │ (1,N)
                      ▼
              SolicitacaoOrcamento
                  │         │
                (N,1)     (N,N)
                  │         │
               Endereco   TemaFesta
                  │
                (N,1)
                  │
              TipoEvento
```

- Um `Usuario` tem um `Cliente` (criado no registro)
- Um `Cliente` tem várias `SolicitacaoOrcamento`
- Cada `SolicitacaoOrcamento` tem um `Endereco`, um `TipoEvento` e vários `TemaFesta`

---

## Regras de Negócio importantes

1. **Registro cria Cliente automaticamente** — ao registrar, o backend cria o `Cliente` vinculado com `statusCadastro = INCOMPLETO`
2. **Status do cliente é automático** — `COMPLETO` se tiver telefone, `INCOMPLETO` se não tiver
3. **Solicitação pertence ao usuário** — o backend usa o JWT para associar a solicitação ao `Cliente` do usuário logado
4. **USER só acessa o próprio** — ao buscar solicitações, o backend filtra automaticamente pelo usuário do token
5. **`dataCriacao` é imutável** — ao fazer PUT, o backend preserva a data original
6. **Status inicial é PENDENTE** — o frontend não precisa enviar `statusOrcamento` no POST

---

## Datas

- Formato de entrada e saída: `yyyy-MM-dd` para datas (ex: `"2024-12-31"`)
- Timestamps: ISO 8601 (ex: `"2024-03-15T10:30:00"`)
- O backend rejeita outros formatos com `400 Bad Request`

---

## Inicialização automática ao subir

O backend cria automaticamente no primeiro boot:
- As roles `ROLE_USER` e `ROLE_ADMIN` no banco
- Um usuário admin padrão com email `ADMIN_EMAIL` e senha `ADMIN_PASSWORD`
