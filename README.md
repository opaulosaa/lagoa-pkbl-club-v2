# 🎾 API de Reservas de Quadras Esportivas

Uma API RESTful desenvolvida em Java com Spring Boot para o gerenciamento de aluguéis e reservas de quadras esportivas (Tênis, Padel e Pickleball). O sistema conta com autenticação e autorização via tokens JWT, criptografia de senhas e regras de negócio robustas para validação de agendamentos.

## 🚀 Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot 3** (Web, Data JPA, Security)
* **PostgreSQL** (Banco de Dados Relacional)
* **JSON Web Tokens (JWT)** (Autenticação e Autorização)
* **BCrypt** (Criptografia de Senhas)
* **Lombok** (Redução de Boilerplate)
* **Maven** (Gerenciador de Dependências)

## ⚙️ Funcionalidades e Regras de Negócio

* **Gestão de Usuários:** Cadastro de usuários com perfis de acesso (`ADMIN`, `PROFESSOR`, `ALUNO`).
* **Autenticação:** Login seguro devolvendo token JWT (Stateless).
* **Gestão de Quadras:**
  * Criação e atualização restrita a perfis `ADMIN` e `PROFESSOR`.
  * Exclusão lógica (**Soft Delete**), inativando a quadra sem perder o histórico de reservas.
* **Gestão de Reservas:**
  * Bloqueio de reservas conflitantes (choque de horários na mesma quadra).
  * Validação cronológica (bloqueio de reservas no passado).
  * Validação de funcionamento: As reservas só podem ocorrer entre 06:00 e 22:00.
  * Validação de hora cheia: O sistema aceita apenas horários redondos (ex: 14:00 às 15:00) e duração mínima de 1 hora.

## 🛠️ Como Executar o Projeto Localmente

### Pré-requisitos
* Java JDK 17 ou superior instalado.
* PostgreSQL instalado e rodando.
* Maven.
