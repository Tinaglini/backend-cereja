# Festas - Orçamentos (Spring Boot)

## Rodar
- Requisitos: Java 17+, Maven 3.9+
- `mvn spring-boot:run`
- Console H2: `/h2-console` (JDBC URL: `jdbc:h2:mem:festasdb`)

## Endpoints principais
- `POST /clientes` — cria cliente
- `GET /clientes?nome=` — lista/busca
- `POST /temas` — cria tema
- `GET /temas?nome=` — lista/busca
- `POST /orcamentos` — cria orçamento (com itens e local)
- `GET /orcamentos?de=2025-08-01&ate=2025-08-31` — filtra por data
- `GET /orcamentos/search/valorPorPessoa?teto=80`
- `GET /orcamentos/search/tema?nomeTema=casamento`

## Exemplo POST /orcamentos
Veja a coleção Postman em `postman/Festas Orçamentos.postman_collection.json`.

## Regras de negócio
- Não cria orçamento sem cliente válido.
- Data não pode ser passada.
- Calcula `valorPorPessoa` e define `porte` automaticamente.
- Lança exceção se `valorPorPessoa` < 50.
