# PokéTeam Builder 📱⚡

Aplicativo Android de gerenciamento de times Pokémon, desenvolvido como projeto acadêmico para demonstrar consumo de APIs externas, persistência de dados local e operações HTTP completas (GET, POST, PUT, DELETE).

---

## 📋 Sumário

1. [Visão geral](#visão-geral)
2. [Tecnologias utilizadas](#tecnologias-utilizadas)
3. [Arquitetura do projeto](#arquitetura-do-projeto)
4. [Estrutura de pastas](#estrutura-de-pastas)
5. [Pré-requisitos](#pré-requisitos)
6. [Como executar a API própria (FastAPI)](#como-executar-a-api-própria-fastapi)
7. [Como executar o app Android](#como-executar-o-app-android)
8. [Funcionalidades](#funcionalidades)
9. [Tratamento de erros](#tratamento-de-erros)
10. [Testes](#testes)
11. [Fluxo de navegação](#fluxo-de-navegação)
12. [Observações finais](#observações-finais)

---

## Visão geral

O **PokéTeam Builder** permite ao usuário:

- Buscar Pokémon por nome na [PokéAPI](https://pokeapi.co/) pública.
- Navegar por uma Pokédex completa com paginação.
- Criar times de até 6 Pokémon, com nome e descrição.
- Salvar, editar e excluir times através de uma **API própria em FastAPI**, com persistência em SQLite.
- Favoritar Pokémon, ver histórico de buscas e Pokémon vistos recentemente (Room).
- Configurar tema claro/escuro, nome do treinador e idioma (DataStore).

---

## Tecnologias utilizadas

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitetura | MVVM + Repository Pattern |
| Injeção de dependência | Hilt |
| Rede | Retrofit + OkHttp + kotlinx.serialization |
| Assincronismo | Kotlin Coroutines + Flow/StateFlow |
| Imagens | Coil |
| Navegação | Navigation Compose |
| Persistência local | Room (favoritos, recentes, histórico) |
| Preferências | DataStore (tema, nome do treinador, idioma, último time) |
| API externa | [PokéAPI](https://pokeapi.co/) (somente leitura) |
| API própria | FastAPI + SQLAlchemy + SQLite |

---

## Arquitetura do projeto

O projeto segue **MVVM (Model-View-ViewModel)** combinado com o **Repository Pattern**:

```
UI (Jetpack Compose)
      ↓ observa (StateFlow)
ViewModel (lógica de apresentação)
      ↓ chama
Repository (única fonte da verdade)
      ↓ decide de onde vêm os dados
      ├── Retrofit → PokéAPI / API própria (FastAPI)
      └── Room / DataStore → dados locais
```

**Regra de ouro:** a UI nunca acessa Retrofit, Room ou DataStore diretamente — tudo passa pelo Repository, que devolve um `NetworkResult<T>` (sealed class com estados `Success`, `Error`, `Loading`) para a ViewModel tratar.

---

## Estrutura de pastas

```
app/src/main/java/com/pokemon/poketeambuilder/
│
├── data/
│   ├── local/              → Room (dao, entity) + DataStore
│   ├── remote/              → Retrofit (api, dto, interceptor)
│   ├── repository/          → Repositories + Mappers (DTO → Domínio)
│   └── model/                → Modelos de domínio (Pokemon, Team, etc.)
│
├── network/                  → NetworkResult / AppError (tratamento de erro)
├── di/                        → Módulos Hilt (Network, Database, DataStore)
│
├── ui/
│   ├── screens/               → Todas as telas (Home, Search, Details, Team, Settings)
│   ├── components/            → Componentes reutilizáveis (Card, Chip, StatBar...)
│   ├── theme/                 → Tema Material 3 inspirado na Pokédex
│   └── navigation/            → NavGraph + rotas (Screen.kt)
│
├── viewmodel/                 → Um ViewModel por fluxo de tela
├── utils/                     → Constants, Extensions, ConnectivityObserver, cores por tipo
│
├── MainActivity.kt
└── PokeTeamBuilderApp.kt      → @HiltAndroidApp
```

A API própria (FastAPI) vive em um projeto **separado**, fora do módulo Android:

```
poketeam-fastapi/
├── main.py            → Aplicação FastAPI com os 5 endpoints de /teams
├── test_main.py        → Testes automatizados (pytest)
└── requirements.txt     → Dependências Python
```

---

## Pré-requisitos

- **Android Studio** Koala (2024.1.1) ou superior
- **JDK 17**
- **Python 3.10+** (para rodar a API própria)
- Emulador Android (API 21+) ou dispositivo físico
- Conexão com a internet (para consumir a PokéAPI)

---

## Como executar a API própria (FastAPI)

A API própria é responsável por armazenar os **Times** criados no app (a PokéAPI é somente leitura e não é afetada).

### 1. Instale as dependências

```bash
cd poketeam-fastapi
pip install -r requirements.txt
```

### 2. Rode o servidor

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

Isso inicia o servidor em `http://localhost:8000`. Um banco SQLite (`poketeam.db`) é criado automaticamente na primeira execução.

### 3. Verifique se está no ar

Acesse `http://localhost:8000/docs` no navegador — o FastAPI gera automaticamente uma documentação interativa (Swagger UI) onde você pode testar os endpoints manualmente:

| Método | Rota | Descrição |
|---|---|---|
| GET | `/teams` | Lista todos os times |
| GET | `/teams/{id}` | Retorna um time específico |
| POST | `/teams` | Cria um novo time |
| PUT | `/teams/{id}` | Atualiza um time existente |
| DELETE | `/teams/{id}` | Remove um time |

### 4. Testando com curl (opcional)

```bash
# Criar um time
curl -X POST http://localhost:8000/teams \
  -H "Content-Type: application/json" \
  -d '{"name":"Time Competitivo","description":"Time para batalhas","pokemon":[25,6,448,94,149,445]}'

# Listar times
curl http://localhost:8000/teams
```

### 5. Rodando os testes automatizados da API

```bash
pip install pytest
pytest test_main.py -v
```

---

## Como executar o app Android

### 1. Suba a API própria primeiro

Siga os passos da seção anterior — o app espera encontrá-la em `http://10.0.2.2:8000/` quando rodando no **emulador Android** (esse é o endereço especial que o emulador usa para acessar o `localhost` da máquina host).

> **Testando em dispositivo físico?** Troque a constante `TEAM_API_BASE_URL` em `utils/Constants.kt` pelo IP da sua máquina na rede local (ex: `http://192.168.0.10:8000/`), e garanta que o celular esteja na mesma rede Wi-Fi.

### 2. Abra o projeto no Android Studio

`File > Open` e selecione a pasta raiz `PokeTeamBuilder/`. Aguarde o Gradle sincronizar (ele vai baixar Retrofit, Room, Hilt, Coil, etc. automaticamente).

### 3. Rode o app

Selecione um emulador (API 21+) ou conecte um dispositivo físico, e clique em **Run ▶**.

### 4. Fluxo de teste sugerido

1. Na Home, toque em **Buscar Pokémon** → pesquise `pikachu` → veja detalhes.
2. Toque em **Criar Time** → dê um nome, descrição, adicione 2-3 Pokémon → salve (isso dispara um `POST /teams` na sua API FastAPI).
3. Volte à Home → **Ver Times** → veja o card do time criado (`GET /teams`).
4. Abra o time → **Editar** → altere algo → salve (`PUT /teams/{id}`).
5. Abra o time → toque no ícone de lixeira → confirme → time é removido (`DELETE /teams/{id}`).

---

## Funcionalidades

### Tela Inicial
Quantidade de times, botões **Criar Time**, **Ver Times**, **Buscar Pokémon**.

### Buscar Pokémon
Busca por nome via `GET /pokemon/{nome}` na PokéAPI. Exibe imagem, nome, tipos, habilidades, stats, peso e altura. Mantém histórico de buscas (Room).

### Lista de Pokémon (Pokédex)
Listagem paginada (scroll infinito) via `GET /pokemon?limit=20&offset=N`, com ordenação alfabética ou por número da Pokédex.

### Criar / Editar Time
Formulário com nome, descrição e seleção de até 6 Pokémon (buscados pelo nome). Ao salvar, dispara `POST` (criação) ou `PUT` (edição) na API própria.

### Meus Times
Lista todos os times (`GET /teams`) em cards com nome, descrição e quantidade de Pokémon.

### Detalhes do Time
Nome, descrição e todos os Pokémon do time com imagem, tipos e stats principais. Permite editar, excluir (com confirmação) e **compartilhar** o time via Intent nativo do Android.

### Configurações
Tema claro/escuro, nome do treinador e idioma — tudo persistido via **DataStore**.

### Extras implementados
- ✅ Favoritar Pokémon (Room)
- ✅ Compartilhar time (Android Share Intent)
- ✅ Ordenar alfabeticamente / por número da Pokédex
- ✅ Histórico de buscas e Pokémon vistos recentemente (Room)
- ✅ Cadeia evolutiva (quando disponível na PokéAPI)

---

## Tratamento de erros

A sealed class `AppError` (em `network/NetworkResult.kt`) padroniza as mensagens exibidas ao usuário para os 4 cenários exigidos:

| Cenário | Classe | Quando ocorre |
|---|---|---|
| Sem internet | `AppError.NoInternet` | `IOException` (ex: sem conexão) |
| Pokémon inexistente | `AppError.PokemonNotFound` | HTTP 404 ao buscar Pokémon |
| Erro da API | `AppError.ApiError(code, message)` | Qualquer outro erro HTTP (500, 400, etc.) |
| Erro interno | `AppError.UnknownError` | Exceções inesperadas |

Essas mensagens são exibidas através do componente reutilizável `ErrorMessage.kt`, que também oferece um botão **"Tentar novamente"**.

---

## Testes

### Android (JUnit + MockK + Coroutines Test)

Exemplo em `app/src/test/java/.../data/repository/TeamRepositoryTest.kt`, cobrindo GET, POST, PUT e DELETE do `TeamRepository` com mocks do `TeamApiService` (sem chamadas de rede reais).

Rodar pelo Android Studio (botão ▶ ao lado da classe) ou via terminal:

```bash
./gradlew test
```

### API FastAPI (pytest)

Exemplo em `poketeam-fastapi/test_main.py`, cobrindo os 5 endpoints e casos de erro (time inexistente, mais de 6 Pokémon).

```bash
cd poketeam-fastapi
pytest test_main.py -v
```

---

## Fluxo de navegação

```
Home
 ├─→ Buscar Pokémon → Detalhes do Pokémon
 ├─→ Ver Pokédex (lista paginada) → Detalhes do Pokémon
 ├─→ Criar Time → (volta para Home/Lista após salvar)
 ├─→ Meus Times → Detalhes do Time
 │                    ├─→ Editar Time
 │                    └─→ Excluir Time (com confirmação)
 └─→ Configurações
```

Implementado com **Navigation Compose** (`ui/navigation/NavGraph.kt` + `Screen.kt`), usando rotas tipadas com argumentos (`pokemonId`, `teamId`).

---

## Observações finais

- O app usa **`android:usesCleartextTraffic="true"`** no `AndroidManifest.xml` para permitir tráfego HTTP (sem SSL) até a API própria em desenvolvimento local — isso é aceitável para fins acadêmicos, mas **não deve ser usado em produção** sem HTTPS.
- A PokéAPI não exige autenticação nem chave de API.
- Os times ficam armazenados apenas na API própria (SQLite do servidor), **não** no Room do celular — o Room é usado exclusivamente para favoritos, histórico e recentes.
- Caso queira resetar os dados dos times, basta apagar o arquivo `poketeam.db` gerado pela API FastAPI e reiniciá-la.

---

**Desenvolvido para fins acadêmicos — demonstração de consumo de API REST, persistência local e arquitetura MVVM em Android.**
#   P o k e m o n T e a m B u i l d e r  
 