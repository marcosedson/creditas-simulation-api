# cred-simulation-api

Este projeto utiliza o Quarkus, um framework Java Supersônico e Subatômico, para o desenvolvimento de aplicações Java otimizadas para ambientes modernos, como containers e Kubernetes.

Se você deseja saber mais sobre o Quarkus, visite seu site oficial: [Quarkus.io](https://quarkus.io/).

---

## **Instruções de Setup**

### **Requisitos**
Antes de executar a aplicação, certifique-se de que seu ambiente atenda aos seguintes requisitos:
- **Java**: JDK 17 ou superior (configurar `JAVA_HOME` corretamente)
- **Maven**: Versão 3.8.1 ou superior
- **Docker** (opcional): Necessário caso deseje criar builds nativos usando containers

#### **Passo a Passo**
1. Clone o repositório:
   ```bash
   git clone https://github.com/marcosedson/cred-simulation-api.git
   cd cred-simulation-api
   ```

2. Verifique se as variáveis de ambiente necessárias estão configuradas corretamente, como `JAVA_HOME`.

3. Compile e execute no modo de desenvolvimento para habilitar live coding:
   ```bash
   ./mvnw quarkus:dev
   ```

4. A aplicação ficará disponível localmente em [http://localhost:8080](http://localhost:8080).

---

## **Construindo e Executando a Aplicação**

### **Pacote em formato JAR**
Para empacotar a aplicação, use:
```bash
./mvnw package
```

Arquivos gerados:
- Executável: `target/quarkus-app/quarkus-run.jar`
- Dependências: `target/quarkus-app/lib/`

Execute com:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

### **Build nativo (opcional)**
Se você deseja um executável nativo:
1. Instale o **GraalVM** ou use um container para gerar um build nativo:
   ```bash
   ./mvnw package -Dnative
   ```

2. Caso prefira usar containers:
   ```bash
   ./mvnw package -Dnative -Dquarkus.native.container-build=true
   ```

3. O binário gerado estará disponível em:
   ```
   ./target/cred-simulation-api-1.0-SNAPSHOT-runner
   ```

---

## **Exemplos de Requisições para os Endpoints**

Este projeto fornece endpoints para processar simulações de crédito assíncronas e síncronas.  
Uma collection de Postman chamada `creditas-simulations-api.postman_collection.json` foi incluída no repositório para simplificar os testes. Você pode importá-la no Postman para acessar rapidamente exemplos prontos para cada endpoint.

### **Endpoint 1: Processar Simulação de Crédito (Síncrona)**
**Descrição**: Realiza o processamento de uma única simulação de crédito.  
**URL**: `POST /v1/simulations`  
**Headers**: `Content-Type: application/json`  
**Body**:

```json
{
   "loanAmount": 15000,
   "birthDate": "1985-06-15",
   "paymentTermInMonths": 18,
   "interestRateType": "FIXED",
   "interestRateMargin": 1.5,
   "currency": "BRL"
}
```

**Resposta de Sucesso**:
```json
{
  "loanAmount": 15000,
  "paymentTermInMonths": 18,
  "monthlyPayment": 894.35,
  "totalPayment": 16098.30,
  "interestRateType": "FIXED",
  "currency": "BRL"
}
```

---

### **Endpoint 2: Iniciar Processamento de Múltiplas Simulações (Assíncrono)**
**Descrição**: Envia múltiplas simulações de crédito para processamento assíncrono.  
**URL**: `POST /v1/simulations/tasks`  
**Headers**: `Content-Type: application/json`  
**Body**:

```json
[
  {
    "loanAmount": 20000,
    "birthDate": "1990-02-20",
    "paymentTermInMonths": 24,
    "interestRateType": "VARIABLE",
    "interestRateMargin": 2.0,
    "currency": "USD"
  },
  {
    "loanAmount": 10000,
    "birthDate": "1980-12-01",
    "paymentTermInMonths": 12,
    "interestRateType": "FIXED",
    "interestRateMargin": 1.0,
    "currency": "EUR"
  }
]
```

**Resposta de Sucesso**:
```json
{
  "taskId": "task123"
}
```

---

### **Endpoint 3: Consultar Status da Tarefa**
**Descrição**: Verifica o status do processamento de uma tarefa específica.  
**URL**: `GET /v1/simulations/tasks/{taskId}/status`  
**Headers**: Nenhum necessário.

**Exemplo de Resposta (Status Pendente)**:
```json
{
  "taskId": "task123",
  "status": "PROCESSING"
}
```

**Exemplo de Resposta (Concluído)**:
```json
{
  "taskId": "task123",
  "status": "COMPLETED"
}
```

---

### **Endpoint 4: Obter Resultados da Tarefa**
**Descrição**: Recupera os resultados processados de uma tarefa específica.  
**URL**: `GET /v1/simulations/tasks/{taskId}/results`  
**Headers**: Nenhum necessário.

**Exemplo de Resposta**:
```json
[
   {
      "loanAmount": 20000,
      "paymentTermInMonths": 24,
      "monthlyPayment": 895.23,
      "totalPayment": 21485.52,
      "interestRateType": "VARIABLE",
      "currency": "USD"
   },
   {
      "loanAmount": 10000,
      "paymentTermInMonths": 12,
      "monthlyPayment": 857.53,
      "totalPayment": 10290.36,
      "interestRateType": "FIXED",
      "currency": "EUR"
   }
]
```

---

## **Estrutura do Projeto**

### **Descrição**
A API foi construída utilizando **Quarkus** com princípios reativos para alto desempenho em aplicações modernas. A arquitetura segue o padrão **Hexagonal (Ports and Adapters)**, garantindo maior flexibilidade e fácil integração com outras tecnologias.

### **Estrutura de Diretórios**
```plaintext
src/main
├── java/com/example
│   ├── controller             # Controladores REST que expõem os endpoints da API
│   ├── domain                 # Objetos e lógica relacionados ao domínio
│   ├── enums                  # Enums utilizados no domínio e na aplicação
│   ├── response               # Objetos de resposta (DTOs) utilizados pelos endpoints
│   ├── service                # Regras de negócio e serviços
│   └── util                   # Utilitários e funções auxiliares gerais
├── resources
│   ├── application.properties  # Configurações da aplicação
└── test/java/com/example
    └── *                      # Testes de unidade e integração
```

---

## **Decisões de Arquitetura**

A escolha pela arquitetura hexagonal (Ports and Adapters) foi baseada na necessidade de desacoplar os serviços de negócios do restante da aplicação (como repositórios, APIs ou outros conectores). Para processar grandes volumes de simulações, utiliza-se APIs reativas e processamento assíncrono via `CompletableFuture`.

---