# Plataforma de Gestão de Boletos

![Java](https://img.shields.io/badge/Java-21-ED3B3B?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-231F20?style=flat-square&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-Frontend-DD0031?style=flat-square&logo=angular&logoColor=white)

Uma plataforma distribuída de gestão de boletos que automatiza o monitoramento, a notificação pré-vencimento e o processamento de pagamentos, eliminando esquecimentos e juros por atraso de forma 100% manual.


---

## Arquitetura

O sistema segue o modelo de **microsserviços orientados a eventos** com um API Gateway como ponto de entrada único para o frontend Angular.


<img width="818" height="799" alt="image" src="https://github.com/user-attachments/assets/afbd3c71-9edb-4c9c-a722-88d77e3af52f" />



---

## Microsserviços

### BoletoService

Porta de entrada do sistema. Responsável por receber, validar e registrar boletos, disparando o fluxo de eventos.

**Responsabilidades:**
- Criar e persistir boletos
- Validar dados de entrada via Bean Validation
- Publicar evento `BoletoCriado` no Kafka
- Expor endpoints REST para criação e consulta
- Cache em memória para leituras frequentes

**Modelo:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único |
| `dataCriacao` | LocalDateTime | Data de criação automática |
| `dataVencimento` | LocalDate | Data de vencimento informada |
| `tipoBoleto` | Enum | `AGUA`, `ENERGIA`, `INTERNET`, `TELEFONE`, `CONDOMINIO` |
| `enderecoCobranca` | String | Endereço de cobrança |
| `cep` | String | CEP do endereço |
| `numeroResidencia` | Integer | Número da residência |
| `valor` | BigDecimal | Valor do boleto |

**Stack:** Java 21 · Spring Boot · Spring Data JPA · PostgreSQL · Apache Kafka · Bean Validation · Cache em memória · Docker · JUnit · Mockito

---

### VencimentoService

Responsável por monitorar ciclos de vencimento e aplicar regras financeiras automaticamente.

**Responsabilidades:**
- Consumir evento `BoletoCriado`
- Verificar boletos próximos ao vencimento (3 dias antes)
- Verificar boletos vencidos e aplicar juros de 5%
- Publicar eventos `BoletoPertoDoVencimento` e `BoletoVencido`
- Garantir idempotência via campo `boletoJaProcessado`

**Modelo:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único |
| `idBoleto` | UUID | Referência ao boleto original |
| `valorOriginal` | BigDecimal | Valor sem acréscimos |
| `juros` | BigDecimal | Valor calculado dos juros (5%) |
| `dataVencimento` | LocalDate | Data de vencimento |
| `boletoJaProcessado` | Boolean | Flag de idempotência |

**Stack:** Java 21 · Spring Boot · Spring Data JPA · PostgreSQL · Apache Kafka · Docker · JUnit · Mockito

---

### NotificacaoService

Responsável pelo envio de e-mails e registro do histórico de notificações. É o único serviço que conhece o e-mail do destinatário, garantindo baixo acoplamento.

**Responsabilidades:**
- Consumir eventos de vencimento (`BoletoPertoDoVencimento`)
- Enviar e-mails usando `JavaMailSender` com templates Thymeleaf
- Persistir histórico de notificações enviadas

> **Princípio de privacidade:** o `emailDestino` existe **somente** neste serviço. Nenhum outro microsserviço conhece dados de contato do usuário — isso garante isolamento de domínio e minimiza superfície de exposição de dados sensíveis.

**Modelo:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único |
| `mensagem` | String | Conteúdo da notificação |
| `emailDestino` | String | E-mail do destinatário |
| `dataEnvio` | LocalDateTime | Data e hora do envio |

**Stack:** Java 21 · Spring Boot · Spring Mail · Thymeleaf · PostgreSQL · Apache Kafka · Docker · JUnit · Mockito

---

### PagamentoService

Responsável por processar o pagamento automático dos boletos vencidos, garantindo que cada evento seja processado exatamente uma vez.

**Responsabilidades:**
- Consumir evento `BoletoVencido`
- Realizar débito automático
- Registrar status do pagamento
- Garantir idempotência via campo `eventoJaProcessado`

**Modelo:**

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | Identificador único |
| `idBoleto` | UUID | Referência ao boleto original |
| `status` | Enum | `REALIZADO`, `FALHOU` |
| `dataPagamento` | LocalDateTime | Data e hora do pagamento |
| `valorPago` | BigDecimal | Valor efetivamente pago |
| `eventoJaProcessado` | Boolean | Flag de idempotência |

**Stack:** Java 21 · Spring Boot · Spring Data JPA · PostgreSQL · Apache Kafka · Docker · JUnit · Mockito

---

### API Gateway

Ponto de entrada único da aplicação para o frontend Angular.

**Responsabilidades:**
- Roteamento para os microsserviços internos
- Configuração centralizada de CORS (único ponto)
- Controle de acesso e entrada da aplicação

---


## State Machine

O ciclo de vida de cada boleto é controlado por uma State Machine, garantindo transições válidas e rastreabilidade de estado.

**Estados:**

```
CRIADO ──────────────────────────────────────► PAGO
  │                                              ▲
  ▼                                              │
PROXIMO_VENCIMENTO ──────► VENCIDO ─────────────┘
```

**Eventos que disparam transições:**

| Evento | Transição |
|---|---|
| `BOLETO_CRIADO` | `→ CRIADO` |
| `BOLETO_PERTO_VENCIMENTO` | `CRIADO → PROXIMO_VENCIMENTO` |
| `BOLETO_VENCIDO` | `PROXIMO_VENCIMENTO → VENCIDO` |
| `PAGAMENTO_REALIZADO` | `VENCIDO → PAGO` |
| `NOTIFICADO` | Registra notificação (sem mudar estado) |

---

## Decisões Técnicas

### Por que Microsserviços e não Monolito?

Em um sistema financeiro com regras de negócio distintas (criação, vencimento, pagamento, notificação), o monolito cria acoplamento indesejado. Qualquer alteração na lógica de juros, por exemplo, poderia impactar o envio de e-mails.

Com microsserviços, cada domínio evolui de forma independente: o VencimentoService pode ser escalado em períodos de alta demanda (fim de mês) sem afetar os demais. O isolamento de falhas também é benefício direto: se o NotificacaoService sofrer instabilidade, os pagamentos continuam funcionando.

---

### Por que Apache Kafka e não RabbitMQ?

| Critério | Kafka | RabbitMQ |
|---|---|---|
| **Throughput** | Altíssimo (milhares de eventos/s) | Moderado |
| **Persistência** | Eventos persistidos em log imutável | Fila temporária (mensagem some após consumo) |
| **Event Replay** | Nativo — reprocesse eventos históricos | Não disponível |
| **Múltiplos consumidores** | Consumer groups independentes | Concorrência por mensagem |
| **Casos de uso** | Stream processing, event sourcing | Task queues, RPC |

Neste projeto, Kafka é essencial porque:
- `VencimentoService`, `NotificacaoService` e `PagamentoService` precisam consumir o mesmo evento de forma independente
- Se um serviço falhar, ele pode reprocessar eventos a partir de um offset específico (event replay)
- O histórico de eventos é auditável e imutável

RabbitMQ seria mais adequado para filas simples de tarefas com menor volume e sem necessidade de reprocessamento.

---

### Por que PostgreSQL e não MySQL?

| Critério | PostgreSQL | MySQL |
|---|---|---|
| **Precisão financeira** | `NUMERIC`/`DECIMAL` com alta precisão | Pode ter variações em arredondamento |
| **Conformidade ACID** | Total, inclusive em DDL | Depende do storage engine (InnoDB) |
| **JSON nativo** | `JSONB` indexável | `JSON` sem índice estrutural |
| **Extensibilidade** | Tipos customizados, funções em múltiplas linguagens | Mais limitado |
| **Concorrência** | MVCC robusto, sem lock de leitura | Lock de leitura em algumas operações |

Para um sistema financeiro, precisão decimal e consistência transacional são inegociáveis. O PostgreSQL oferece `NUMERIC` com precisão arbitrária e conformidade ACID completa, eliminando riscos de arredondamento que poderiam gerar inconsistências em valores monetários.

---

### Por que Java 21 e não versões anteriores?

Java 21 é a LTS mais recente no momento do desenvolvimento, trazendo Virtual Threads (Project Loom) que melhoram a performance em operações I/O intensivas (como chamadas ao Kafka e ao banco) sem aumentar a complexidade do código. Também inclui melhorias de performance no GC e expressividade com Records e Pattern Matching.

---

### Por que Spring Boot?

Ecossistema maduro com suporte nativo a Kafka (`spring-kafka`), JPA, validação, cache, e mail. Reduz boilerplate sem esconder a complexidade, permitindo configuração explícita quando necessário. A integração com Docker via Buildpacks e o suporte a profiles facilita o gerenciamento de ambientes.

---

## Padrões e Garantias

### Retry

Todos os consumidores Kafka implementam retry automático para falhas transitórias (ex: banco indisponível momentaneamente). Configurado via `@RetryableTopic` ou `SeekToCurrentErrorHandler` com backoff exponencial.

### Dead Letter Topic (DLT)

Após esgotar as tentativas de retry, mensagens inválidas ou com erro persistente são roteadas para um tópico separado (ex: `boleto-criado.DLT`) para análise e reprocessamento manual. Implementado via `@DltHandler` em cada consumer.

### Idempotência

Garante que o mesmo evento Kafka não seja processado duas vezes, mesmo em caso de reentrega:

- `VencimentoService` — campo `boletoJaProcessado` verificado antes de qualquer operação
- `PagamentoService` — campo `eventoJaProcessado` verificado antes de realizar débito

Estratégia: antes de processar, consulta o banco pelo `idBoleto`. Se já processado, descarta silenciosamente.

### Sem Lombok

O projeto utiliza apenas anotações padrão do Jakarta (`@NotNull`, `@NotBlank`, `@Enumerated`) sem dependência do Lombok. Getters, setters e construtores são gerados explicitamente, tornando o código mais transparente e sem dependência de processamento em tempo de compilação.

### Exceções Personalizadas

Cada serviço possui um pacote `exception` com classes específicas ao domínio (ex: `BoletoNotFoundException`, `BoletoJaProcessadoException`), tratadas via `@ControllerAdvice` com respostas HTTP semânticas.

### Configuração Centralizada de Tópicos

Uma classe `KafkaTopicsConfig` por serviço declara todos os `TopicBuilder` beans, garantindo criação automática dos tópicos com as configurações corretas (partições, réplicas, retenção).

---

## Estrutura de Pacotes

Padrão aplicado em todos os microsserviços:

```
src/main/java/com/financeiro/{servico}/
├── config/          # Configuração do Kafka, tópicos, cache, beans
├── controller/      # Endpoints REST (somente no BoletoService e API Gateway)
├── consumer/        # Consumers Kafka com @KafkaListener
├── producer/        # Producers Kafka
├── service/         # Regras de negócio
├── repository/      # Interfaces JPA
├── model/           # Entidades JPA
├── dto/             # Objetos de transferência de dados
├── statemachine/    # Configuração e handlers da State Machine
└── exception/       # Exceções personalizadas e @ControllerAdvice
```

---

## API REST

Todos os endpoints são acessados via **API Gateway**.

### Criar boleto

```
POST /api/boletos
```

**Payload:**

```json
{
  "dataVencimento": "2026-12-30",
  "tipoBoleto": "ENERGIA",
  "enderecoCobranca": "Rua das Flores, 100",
  "cep": "08000-000",
  "numeroResidencia": 100,
  "valor": 250.00
}
```

**Tipos disponíveis:** `AGUA` · `ENERGIA` · `INTERNET` · `TELEFONE` · `CONDOMINIO`

### Consultar boleto

```
GET /api/boletos/{id}
```

**Resposta:**

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "dataCriacao": "2026-06-08T10:00:00",
  "dataVencimento": "2026-12-30",
  "tipoBoleto": "ENERGIA",
  "enderecoCobranca": "Rua das Flores, 100",
  "cep": "08000-000",
  "numeroResidencia": 100,
  "valor": 250.00,
  "status": "CRIADO"
}
```

---

## Como Executar

### Pré-requisitos

- Docker e Docker Compose
- Java 21 (para build local)
- Maven 3.9+

### 1. Build do projeto

```bash
./mvnw clean install -DskipTests
```

### 2. Subir toda a infraestrutura

```bash
docker-compose up --build
```

Isso sobe:
- PostgreSQL (uma instância por serviço)
- Apache Kafka + Zookeeper
- Todos os microsserviços
- API Gateway
- Frontend Angular

### 3. Acessar

| Serviço | URL |
|---|---|
| Frontend | `http://localhost:4200` |
| API Gateway | `http://localhost:8080` |
| Kafka UI (se configurado) | `http://localhost:9000` |

---

## Testes

Cada microsserviço possui testes unitários cobrindo as camadas de serviço e consumer:

```bash
# Rodar testes de todos os módulos
./mvnw test

# Rodar testes de um módulo específico
./mvnw test -pl boleto-service
```

**Cobertura mínima esperada:**
- `service/` — lógica de negócio completa
- `consumer/` — cenários de sucesso, retry e DLT
- `statemachine/` — todas as transições de estado

Ferramentas: JUnit 5 · Mockito · `@EmbeddedKafka` para testes de integração Kafka

---

## Próximos Passos

### Frontend Angular

O próximo passo do projeto é a implementação do frontend em **Angular**, que se comunicará exclusivamente com o **API Gateway**.

**Funcionalidades planejadas:**
- Criação de boletos via formulário
- Listagem e consulta de boletos por status
- Visualização do histórico de notificações
- Painel de acompanhamento do ciclo de vida (State Machine)
- Indicadores de boletos próximos ao vencimento e vencidos

**Integração:**
```
Angular → API Gateway (HTTP) → Microsserviços
```

O CORS já está configurado no API Gateway para aceitar requisições do frontend, sem necessidade de alteração nos microsserviços.

---

## Autor

**Herik Kou**
