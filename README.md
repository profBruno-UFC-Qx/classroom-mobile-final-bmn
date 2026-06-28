# EnergyTrack – Monitor de Consumo de Energia

O EnergyTrack é um aplicativo mobile desenvolvido em Kotlin e Jetpack Compose para monitoramento de consumo de energia elétrica em ambientes residenciais ou comerciais de pequeno porte.

A aplicação permite acompanhar o consumo energético de dispositivos, visualizar históricos, cadastrar equipamentos, editar informações e simular dados vindos de sensores físicos por meio de uma API desenvolvida em FastAPI.

O projeto utiliza uma arquitetura baseada em API, onde os dados de consumo são enviados por um simulador de hardware e posteriormente consumidos pelo aplicativo Android.

---

## Membros da equipe

* Matrícula: 553556 – Beatriz de Sousa Alves – Engenharia da Computação
* Matrícula: 552819 – Maria Eduarda Almeida Rodrigues – Engenharia da Computação
* Matrícula: 552319 – Nathalia de Oliveira Lima – Engenharia da Computação

---

## Objetivo Geral

Desenvolver um aplicativo mobile utilizando Kotlin e Jetpack Compose que permita monitorar, analisar e acompanhar o consumo de energia elétrica de dispositivos conectados a um ambiente.

A aplicação busca apresentar informações claras sobre consumo, custo estimado, histórico de medições e status dos equipamentos, contribuindo para decisões mais conscientes e econômicas sobre o uso de energia.

---

## Público-Alvo

O EnergyTrack é voltado para:

* Consumidores residenciais;
* Pequenos empreendedores;
* Ambientes comerciais de pequeno porte;
* Usuários interessados em acompanhar gastos energéticos;
* Pessoas que desejam identificar equipamentos com maior consumo de energia.

---

## Impacto Esperado

O projeto busca incentivar o uso consciente da energia elétrica, permitindo que o usuário identifique dispositivos com consumo elevado, acompanhe o histórico energético e tenha uma visão mais clara do custo associado ao uso dos equipamentos.

Com o monitoramento contínuo, espera-se contribuir para:

* Redução de desperdícios de energia;
* Maior controle sobre gastos mensais;
* Identificação de padrões de consumo;
* Incentivo a práticas mais sustentáveis;
* Melhor compreensão sobre o consumo de cada equipamento.

---

## Principais Funcionalidades

* Login, cadastro e recuperação de senha;
* Dashboard com resumo do consumo energético;
* Exibição de consumo mensal, consumo diário e custo estimado;
* Indicador de meta mensal de consumo;
* Histórico de consumo recebido pela API;
* Cadastro de dispositivos elétricos;
* Edição de dispositivos;
* Remoção de dispositivos;
* Ativação e desativação de dispositivos;
* Busca e filtros por dispositivos ativos e inativos;
* Ícones específicos para tipos de dispositivos;
* Integração com API FastAPI;
* Simulação de telemetria de hardware;
* Cálculo de consumo energético em kWh;
* Layout responsivo para dispositivos Android;
* Interface baseada nas cores verde e amarelo da identidade visual do EnergyTrack.

---

## Arquitetura da Solução

O sistema é composto por três partes principais:

```text
Simulador de Hardware
        ↓
API FastAPI
        ↓
Aplicativo Android EnergyTrack
```

### Fluxo de funcionamento

1. O simulador envia dados como tensão, corrente e potência para a API.
2. A API calcula o consumo energético estimado em kWh.
3. A API armazena os dados de dispositivos e telemetria.
4. O aplicativo Android consulta a API.
5. Dashboard, histórico e dispositivos são atualizados com os dados recebidos.

---

## Tecnologias Utilizadas

### Aplicativo Mobile

* Kotlin
* Jetpack Compose
* Material Design 3
* ViewModel
* StateFlow
* Retrofit
* Gson Converter
* Android Studio
* Gradle

### Backend

* Python
* FastAPI
* Uvicorn
* Requests
* API REST
* JSON para persistência dos dados simulados

### Simulação de Hardware

* Simulador em Python
* Envio periódico de tensão, corrente e potência
* Simulação de dispositivos como:

  * Ar-condicionado;
  * Geladeira;
  * Máquina de lavar;
  * TV;
  * Lâmpadas;
  * Outros equipamentos cadastrados pelo usuário.

---

## Estrutura do Projeto

```text
energytrack/
├── EnergyConsumption/
│   ├── app/
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── README.md
│
├── backend/
│   ├── main.py
│   ├── hardware_simulator.py
│   ├── requirements.txt
│   └── energytrack_db.json
│
└── README.md
```

---

# Instruções para Execução

## Pré-requisitos

Antes de executar o projeto, é necessário possuir:

* Android Studio instalado;
* JDK configurado;
* Python 3 instalado;
* Dispositivo Android com Depuração USB ativada ou emulador Android;
* Computador e dispositivo conectados à mesma rede Wi-Fi;
* Git instalado.

---

## 1. Clonar o repositório

Abra o PowerShell ou terminal e execute:

```bash
git clone https://github.com/profBruno-UFC-Qx/classroom-mobile-final-bmn
```

Depois entre na pasta do projeto:

```bash
cd classroom-mobile-final-bmn
```

---

## 2. Executar o Backend FastAPI

Entre na pasta do backend:

```bash
cd energytrack/backend
```

Crie o ambiente virtual:

```bash
python -m venv .venv
```

No Windows PowerShell, ative o ambiente virtual:

```powershell
.\.venv\Scripts\Activate.ps1
```

Caso o PowerShell bloqueie a execução de scripts, execute:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Depois ative novamente:

```powershell
.\.venv\Scripts\Activate.ps1
```

Instale as dependências:

```bash
python -m pip install -r requirements.txt
```

Inicie a API:

```bash
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

Quando aparecer algo semelhante a:

```text
Application startup complete.
```

significa que a API está funcionando.

A API pode ser testada no navegador através de:

```text
http://localhost:8000/health
```

A resposta esperada é:

```json
{
  "status": "ok",
  "service": "EnergyTrack Hardware API"
}
```

---

## 3. Executar o Simulador de Hardware

Abra outro PowerShell, mantendo a API aberta no primeiro terminal.

Entre novamente na pasta do backend:

```powershell
cd energytrack/backend
```

Ative o ambiente virtual:

```powershell
.\.venv\Scripts\Activate.ps1
```

Execute o simulador:

```bash
python hardware_simulator.py
```

O simulador enviará medições periódicas para a API, exibindo mensagens semelhantes a:

```text
Ar-condicionado: 920.4 W
Geladeira: 145.8 W
Máquina de lavar: 487.2 W
```

Essas medições são recebidas pela API e convertidas em consumo estimado de energia.

---

## 4. Configurar a API no Aplicativo Android

No computador, abra o PowerShell e execute:

```powershell
ipconfig
```

Procure o endereço IPv4 da rede Wi-Fi, por exemplo:

```text
192.168.0.15
```

No Android Studio, abra o arquivo:

```text
EnergyConsumption/app/src/main/java/com/example/energyconsumption/EnergyApi.kt
```

Localize a configuração:

```kotlin
const val BASE_URL = "http://SEU_IP_AQUI:8000/"
```

Substitua pelo IPv4 do seu computador:

```kotlin
const val BASE_URL = "http://192.168.0.15:8000/"
```

> O celular e o computador precisam estar conectados na mesma rede Wi-Fi.

Para testar se o celular consegue acessar a API, abra o navegador do celular e digite:

```text
http://SEU_IP:8000/health
```

Exemplo:

```text
http://192.168.0.15:8000/health
```

---

## 5. Executar o Aplicativo Android

Abra a pasta:

```text
energytrack/EnergyConsumption
```

no Android Studio.

Aguarde o Gradle sincronizar o projeto.

Depois:

1. Conecte um celular Android via cabo USB;
2. Ative a opção **Depuração USB** no celular;
3. Autorize a depuração quando aparecer a mensagem no dispositivo;
4. Selecione o celular na lista de dispositivos do Android Studio;
5. Clique em **Run ▶**.

O aplicativo será instalado no celular.

---

## Endpoint de Telemetria

Quando o hardware real estiver disponível, ele deverá enviar dados para a API utilizando o endpoint:

```text
POST /api/telemetry
```

Exemplo de JSON enviado pelo hardware:

```json
{
  "deviceId": "sensor-ar-sala",
  "voltageV": 220.0,
  "currentA": 4.1,
  "powerW": 902.0
}
```

A API utiliza esses valores para estimar o consumo energético com base na potência e no intervalo entre as medições.

A fórmula utilizada é:

```text
Energia (kWh) = Potência (W) × Tempo (h) / 1000
```

---

## Integração com Hardware Real - Trabalhos Futuros

Atualmente, o projeto utiliza um simulador em Python para representar equipamentos conectados.

Em uma implementação real, o simulador pode ser substituído por dispositivos como:

* ESP32;
* STM32;
* Raspberry Pi;
* Sensores de corrente;
* Sensores de tensão;
* Medidores de potência.

Esses dispositivos deverão enviar telemetrias para a API utilizando requisições HTTP.

---

## Observações

* Não envie a pasta `.venv` para o GitHub;
* Não envie as pastas `build`, `.gradle` e `.idea`;
* O arquivo `energytrack_db.json` é criado automaticamente pela API;
* O simulador deve permanecer rodando para gerar dados de consumo;
* Caso a API seja encerrada, o aplicativo exibirá erro de conexão;
* Para testar no celular, a API deve estar rodando no computador.

---

## Demonstração

O aplicativo possui telas para:

* Login;
* Cadastro;
* Dashboard;
* Dispositivos;
* Histórico;
* Perfil;
* Simulação de consumo em tempo real.

---

## Possíveis Melhorias Futuras

* Integração com ESP32 e sensores físicos;
* Notificações de consumo elevado;
* Autenticação real com banco de dados;
* Persistência com SQLite ou PostgreSQL;
* Gráficos por hora, dia e mês;
* Exportação de relatórios;
* Controle remoto de dispositivos;
* Previsão de consumo baseada em inteligência artificial;
* Integração com medidores inteligentes.

