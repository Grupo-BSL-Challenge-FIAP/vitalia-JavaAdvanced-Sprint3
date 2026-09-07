[# 🐾 Vitalia API

O Vitalia é uma plataforma de monitoramento inteligente para animais de estimação, integrando tutores, veterinários e dispositivos IoT para garantir a saúde e o bem-estar animal.

---

# 🚀 Swagger UI & Consoles

Acesse a documentação interativa e realize testes diretamente pelo navegador:

```bash
https://vitalia-txa9.onrender.com/swagger-ui/index.html#/
```
```bash
http://localhost:8080/swagger-ui/index.html
```
```bash
http://localhost:8080
```
---

# 📅 Cronograma

| Atividade | Responsável | Data | Status |
|--------|----------|------------|------------|
| Implementação da entidade Account | Manuelalacerda | 13/05 |Concluído |
| Configuração inicial do Swagger/OpenAPI | Manuelalacerda | 13/05 |Concluído |
| Criação do README e guia de testes | Manuelalacerda | 13/05 |Concluído |
| Implementação da entidade Responsible | Manuelalacerda | 13/05 |Concluído |
| Ajustes de validações do AccountRequest | Manuelalacerda | 15/05 |Concluído |
| Ajustes de validações do ResponsibleRequest | Manuelalacerda | 15/05 |Concluído |
| Implementação da entidade Veterinarian | Manuelalacerda | 15/05 |Concluído |
| Implementação da entidade Pet | Manuelalacerda | 15/05 |Concluído |
| Ajustes e correções dos endpoints | Manuelalacerda | 15/05 |Concluído |
| Implementação da entidade ClinicalHistory | Manuelalacerda | 15/05 |Concluído |
| Documentação do módulo ClinicalHistory | Manuelalacerda | 15/05 |Concluído |
| Implementação da entidade Alert | Manuelalacerda | 15/05 |Concluído |
| Implementação da entidade Appointment | Manuelalacerda | 15/05 |Concluído |
| Correção de paginação e cache do módulo Pet | Manuelalacerda | 18/05 |Concluído |
| Correção de paginação e cache do módulo Responsible | Manuelalacerda | 18/05 |Concluído |
| Configuração de cache na aplicação | Manuelalacerda | 18/05 |Concluído |
| Ajustes do SwaggerConfig | Manuelalacerda | 18/05 |Concluído |
| Correção dos HTTP Responses | Manuelalacerda | 18/05 |Concluído |
| Organização de imagens e estrutura do projeto | Manuelalacerda | 21/05 |Concluído |
| Atualizações da documentação README | Manuelalacerda | 21/05 |Concluído |
| Adição da pasta de testes e exportação Postman | Manuelalacerda | 21/05 |Concluído |
| Refatoração e otimização das buscas e serviços (Pet, ClinicalHistory, Appointment) | Manuelalacerda | 31/08 | Concluído |
| Atualização do script V1 para o Oracle e migração de Account para AppUser | Manuelalacerda | 31/08 | Concluído |
| Configuração de segurança com Spring Security, JWT, CORS e tratamentos de exceção | Manuelalacerda | 31/08 | Concluído |
| Implementação do canal WebSocket para alertas e fluxo veterinário | Manuelalacerda | 31/08 | Concluído |
| Finalização da validação de contratos, roles e expiração do JWT | Manuelalacerda | 01/09 | Concluído |
| Atualização do TokenService para incluir claims obrigatórias (userId, iat) | Manuelalacerda | 01/09 | Concluído |
| Atualização das URLs da API pública | Manuelalacerda | 04/09 | Concluído |
| Adição de script de migration do Flyway e validação de schema com Oracle | Infnet / Manuelalacerda | 06/09 | Concluído |
| Restrição de cadastros administrativos e atribuição de role padrão | Manuelalacerda | 06/09 | Concluído |
| Alinhamento do esquema inicial do Flyway com validações do Hibernate | Manuelalacerda | 07/09 | Concluído |
| Adição de validação de role VETERINARIAN em Appointment e ClinicalHistory | Manuelalacerda | 07/09 | Concluído |
| Refatoração de repositórios (remoção de RoleRepository excedente) | Manuelalacerda | 07/09 | Concluído |
| Finalização da suíte de testes unitários e validações da sprint | Manuelalacerda | 07/09 | Concluído |

---

## 👥 Integrantes do Grupo

<table>
  <tr>
    <td width="130">
      <img src="https://github.com/moisesBarsoti.png" width="120" style="border-radius: 50%;"/>
    </td>
    <td>
      <b>Moisés Barsoti Andrade de Oliveira</b><br/>
      <b>RM:</b> 565049 &nbsp;&nbsp;|&nbsp;&nbsp;<b>Turma:</b> 2TDSPG - FIAP <br/>
    </td>
  </tr>

  <tr>
    <td width="130">
      <img src="https://github.com/sSofia-s.png" width="120" style="border-radius: 50%;"/>
    </td>
    <td>
      <b>Sofia Siqueira Fontes</b><br/>
      <b>RM:</b> 563829 &nbsp;&nbsp;|&nbsp;&nbsp;<b>Turma:</b> 2TDSPG - FIAP <br/>
    </td>
  </tr>

  <tr>
    <td width="130">
      <img src="https://github.com/manuelalacerda.png" width="120" style="border-radius: 50%;"/>
    </td>
    <td>
      <b>Manuela de Lacerda Soares</b><br/>
      <b>RM:</b> 564887 &nbsp;&nbsp;|&nbsp;&nbsp;<b>Turma:</b> 2TDSPG - FIAP <br/>
    </td>
  </tr>
</table>

---

# ⚙️ Como Configurar e Executar com Oracle / Flyway

### 1. Pré-requisitos
* Java JDK 21 instalada.
* Acesso a uma instância do **Oracle Database**.

### 2. Configuração de Variáveis de Ambiente
Configure as seguintes variáveis no seu ambiente ou no arquivo de propriedades:
* `ORACLE_URL`: `jdbc:oracle:thin:@//host:port/service`
* `ORACLE_USER`: Seu usuário do banco
* `ORACLE_PASSWORD`: Sua senha do banco
* `JWT_SECRET`: Chave secreta para os tokens

### 3. Executando as Migrações
O projeto utiliza o **Flyway** para gerenciar e versionar o banco de dados Oracle de forma automatizada ao iniciar a aplicação.

---

# 🔐 1. Auth Controller (`/auth`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| POST | `/auth/login` | Realiza o login na aplicação |
| GET | `/auth/me` | Retorna os dados do usuário autenticado atualmente |
| POST | `/auth/register` | Cadastro geral de usuário |
| POST | `/auth/register/admin` | Cadastro de usuário com perfil administrador |
| POST | `/auth/register/tutor` | Cadastro de usuário com perfil tutor |
| POST | `/auth/register/vet` | Cadastro de usuário com perfil veterinário |

---

POST
```bash
{
  "email": "manuela.soares@vitalia.com",
  "password": "SenhaSegura123!",
  "role": "TUTOR"
}
```
---

# 👤 2. App User Controller (`/users`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | `/users` | Lista todos os usuários |
| POST | `/users` | Cria um novo usuário |
| GET | `/users/{id}` | Busca um usuário pelo ID |
| PUT | `/users/{id}` | Atualiza os dados de um usuário |
| DELETE | `/users/{id}` | Remove um usuário do sistema |

---
POST
```bash
{
  "email": "usuario.teste@petguardian.com",
  "password": "SenhaForte123!",
  "role": "TUTOR",
  "active": true
}
```
---

# 🐶 3. Pets (`/pets`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | `/pets` | Lista todos os pets (com paginação e ordenação) |
| POST | `/pets` | Cadastra um novo pet vinculado ao tutor autenticado |
| GET | `/pets/{id}` | Busca um pet pelo ID |
| PUT | `/pets/{id}` | Atualiza os dados de um pet |
| DELETE | `/pets/{id}` | Remove um pet do sistema |
| GET | `/pets/my-pets` | Lista todos os pets do tutor autenticado |
| GET | `/pets/search/name` | Busca pets por nome (parcial, sem distinção de maiúsculas) |

---
POST
```bash
{
  "name": "Thor",
  "species": "Cão",
  "breed": "Golden Retriever",
  "gender": "Macho",
  "birthDate": "2021-03-10",
  "weight": 32.5,
  "currentStatus": "NORMAL",
  "responsibleId": 1,
  "veterinarianId": 1
}
```
---

# 📊 4. Clinical History Controller (`/clinical-histories`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | `/clinical-histories` | Lista todos os históricos clínicos |
| POST | `/clinical-histories` | Cadastra um novo registro clínico |
| GET | `/clinical-histories/{id}` | Busca um registro clínico pelo ID |
| PUT | `/clinical-histories/{id}` | Atualiza um registro clínico |
| DELETE | `/clinical-histories/{id}` | Remove um registro clínico |
| GET | `/clinical-histories/pet/{petId}` | Lista o histórico clínico de um pet específico |

---
POST
```bash
{
  "temperature": 38.5,
  "heartRate": 85,
  "activityLevel": "HIGH",
  "healthScore": 95.0,
  "description": "Monitoramento via ESP32",
  "observations": "Dados coletados após exercício matinal",
  "petId": 1
}
```
---
# 📅 5. Appointment Controller (`/appointments`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | `/appointments` | Lista todos os agendamentos |
| POST | `/appointments` | Cria um novo agendamento |
| GET | `/appointments/{id}` | Busca um agendamento pelo ID |
| PUT | `/appointments/{id}` | Atualiza um agendamento |
| DELETE | `/appointments/{id}` | Remove um agendamento |
| GET | `/appointments/pet/{petId}` | Lista todas as consultas de um pet específico |

---
POST
```bash
{
  "appointmentDate": "2026-06-20T14:30:00",
  "reason": "Consulta de rotina e check-up semestral",
  "petId": 1,
  "veterinarianId": 1
}
```

---

# 🚨 6. Alert Controller (`/alerts`)

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | `/alerts` | Lista todos os alertas |
| POST | `/alerts` | Cria um novo alerta |
| GET | `/alerts/{id}` | Busca um alerta pelo ID |
| PUT | `/alerts/{id}` | Atualiza um alerta |
| DELETE | `/alerts/{id}` | Remove um alerta |
| GET | `/alerts/pet/{petId}` | Lista os alertas de um pet específico |

---
POST
```bash
{
  "type": "TEMPERATURE",
  "message": "Febre detectada: 40.2°C. O animal já foi medicado.",
  "riskLevel": "HIGH",
  "status": "RESOLVED",
  "petId": 1
}
```

---

### 🗄 Modelo

<div align="center">
  <img src="doc/Pet%20Care%20Management%20Model-2026-05-18-150201.png" alt="Modelo de Gestão de Cuidados de Pets" width="200" />
  <img src="doc/Pet%20Care%20Management%20Model-2026-05-18-150244.png" alt="Modelo de Gestão de Cuidados de Pets" width="305" />
  <img src="doc/Pet%20Care%20Management%20Model-2026-05-18-150318.png" alt="Modelo de Gestão de Cuidados de Pets" width="215" />
</div>
