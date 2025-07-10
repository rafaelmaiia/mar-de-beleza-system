# Mar de Beleza - Sistema de Agendamento

Projeto full-stack em desenvolvimento de um sistema de agendamento para salões de beleza. Permite o gerenciamento de **clientes**, **profissionais** e **horários de atendimento**, com um backend robusto e seguro.

## Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3**
- **Spring Security (com JWT)**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**

### Frontend
- **React**
- **TypeScript**
- **Vite**


## Como Rodar o Projeto

Este é um **monorepo** contendo o backend e o frontend.

### Pré-requisitos
- JDK 21
- Node.js (versão LTS)
- PostgreSQL rodando localmente

### 1. Clone o Repositório
```bash
git clone [https://github.com/rafaelmaiia/mar-de-beleza-system.git](https://github.com/rafaelmaiia/mar-de-beleza-system.git)
cd mar-de-beleza-system
```

### 2. Rodando o Backend
1. Navegue para a pasta /backend no seu terminal.
```bash
cd backend
./mvnw spring-boot:run
```

A API estará disponível em http://localhost:8080.

### 3. Rodando o Frontend
1. Em um novo terminal, navegue para a pasta /frontend.
```bash
cd frontend
npm install       # apenas na primeira vez
npm run dev
```

A aplicação estará disponível em: http://localhost:5173

## Status do Projeto

O backend está em uma fase robusta e madura, com as principais funcionalidades de CRUD, segurança com JWT, validação, lógica de negócio e testes implementados. **O foco atual é no desenvolvimento da interface do frontend em React + TypeScript.**

## Status do Projeto

- Backend robusto e funcional
  - CRUDs completos
  - Segurança com JWT
  - Validações e lógica de negócio
  - Testes unitários e de integração

- Frontend em desenvolvimento com React + TypeScript  
  - Em progresso: telas de login, cadastro e agendamento

## Estrutura do Projeto

```
mar-de-beleza-system/
├── backend/     # API Spring Boot
└── frontend/    # Interface React + TS
```
