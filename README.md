<div align="center">
  <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/logo.png" alt="Logo Mar de Beleza" width="150"/>
  <h1>Mar de Beleza - Sistema de Agendamento</h1>
</div>

<div align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-yellow" alt="Status do Projeto"/>
  <img src="https://img.shields.io/badge/Java-21-blue?logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3-green?logo=spring" alt="Spring Boot 3"/>
  <img src="https://img.shields.io/badge/React-18-blue?logo=react" alt="React"/>
  <img src="https://img.shields.io/badge/TypeScript-5-blue?logo=typescript" alt="TypeScript"/>
</div>

<br>

Este é um **projeto autoral full-stack**, nascido da necessidade real de otimizar a gestão de agendamentos e clientes de um salão de beleza. O sistema foi projetado para gerenciar **clientes**, **profissionais**, **serviços** e **horários**, com um backend robusto e um frontend moderno e intuitivo.


## 📈 Status do Projeto

- **Backend:** 100% Funcional (CRUDs, Segurança JWT, Validações, Testes Unitários e de Integração).
- **Frontend:** Em desenvolvimento ativo com React, TypeScript e Vite.


## 📸 Apresentação Visual

| Login e Dashboard | Gestão de Clientes e Profissionais |
| :---: | :---: |
| <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/tela_login.png" alt="Tela de Login" width="300"/> | <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/tela_clientes.png" alt="Tela de Clientes" width="300"/> |
| *Tela de autenticação e dashboard com a agenda do dia.* | *Listagem e busca de clientes e profissionais.* |
| <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/tela_dashboard.png" alt="Tela do Dashboard" width="300"/> | <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/tela_profissionais.png" alt="Tela de Profissionais" width="300"/> |

| Gestão de Serviços e Menu de páginas | Modais de Interação |
| :---: | :---: |
| <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/tela_servicos.png" alt="Tela de Serviços" width="300"/> | <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/modal_novo_agendamento.png" alt="Modal de Novo Agendamento" width="300"/> |
| *Gerenciamento completo de serviços e outras funcionalidades.* | *Criação de novos agendamentos e atualização de status.* |
| <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/Menu_hamburguer_com_outras_paginas.png" alt="Menu de Navegação" width="300"/> | <img src="https://github.com/rafaelmaiia/mar-de-beleza-system/blob/main/assets/modal_atualizar_status.png" alt="Modal de Atualizar Status" width="300"/> |


## ✨ Funcionalidades

- **Autenticação de Usuários:** Login seguro para acesso ao sistema.
- **Dashboard Principal:** Visualização rápida da agenda do dia e do próximo agendamento.
- **Gestão de Agendamentos:**
  - Criação, edição e exclusão de agendamentos.
  - Atualização de status (Confirmado, Concluído, Cancelado, etc.).
  - Navegação pela agenda por data.
- **Gestão de Clientes:** CRUD completo de clientes com busca por nome.
- **Gestão de Profissionais:** CRUD de profissionais com filtro por especialidade (Sobrancelha, Cabelo, etc.).
- **Gestão de Serviços:** CRUD do catálogo de serviços com informações de duração e preço.


## 💻 Tecnologias Utilizadas

| Backend | Frontend |
| :--- | :--- |
| • Java 21 | • React & TypeScript |
| • Spring Boot 3 | • Vite |
| • Spring Security (JWT) | • Tailwind CSS |
| • Spring Data JPA | • CSS Modules |
| • PostgreSQL | |
| • Maven | |


## Como Rodar o Projeto

Este é um **monorepo** contendo o backend e o frontend.

### Pré-requisitos
- JDK 21
- Node.js (versão LTS)
- PostgreSQL rodando localmente

### 1. Clone o Repositório
```bash
git clone https://github.com/rafaelmaiia/mar-de-beleza-system.git
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


## 🌳 Estrutura do Projeto

```
mar-de-beleza-system/
├── backend/     # API Spring Boot
└── frontend/    # Interface React + TS
```
