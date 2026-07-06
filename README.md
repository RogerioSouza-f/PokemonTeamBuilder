# ⚡ PokéTeam Builder

<p align="center">
  <img src="https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png" width="120">
</p>

<p align="center">
  <strong>Aplicativo Android desenvolvido em Kotlin utilizando Jetpack Compose, arquitetura MVVM, FastAPI e PokéAPI.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue">
  <img src="https://img.shields.io/badge/MVVM-Architecture-success">
  <img src="https://img.shields.io/badge/API-FastAPI-009688">
  <img src="https://img.shields.io/badge/Database-Room-orange">
  <img src="https://img.shields.io/badge/License-MIT-green">
</p>

---

#  Sobre o projeto

O **PokéTeam Builder** é um aplicativo Android criado para facilitar a criação e gerenciamento de times Pokémon.

Além do consumo da **PokéAPI**, o projeto possui uma **API própria em FastAPI**, responsável por armazenar os times criados pelo usuário utilizando operações completas de HTTP (**GET, POST, PUT e DELETE**).

O projeto foi desenvolvido seguindo boas práticas de desenvolvimento Android moderno, com foco em escalabilidade, organização de código e experiência do usuário.

---

# ✨ Funcionalidades

- 🔍 Buscar Pokémon por nome
- 📖 Pokédex completa
- ❤️ Favoritar Pokémon
- 🕘 Histórico de pesquisas
- ⭐ Pokémon recentes
- 🧬 Cadeia evolutiva
- 👥 Criar times com até 6 Pokémon
- ✏️ Editar times
- 🗑️ Excluir times
- 📤 Compartilhar times
- 🌙 Tema Claro/Escuro
- 🌐 Alteração de idioma
- 👤 Nome do treinador
- 💾 Persistência local
- ☁️ Persistência remota utilizando FastAPI

---

# Tecnologias

## Android

- Kotlin
- Jetpack Compose
- Material Design 3
- Navigation Compose
- ViewModel
- StateFlow
- Coroutines
- Hilt
- Retrofit
- OkHttp
- Kotlin Serialization
- Coil
- Room
- DataStore

## Backend

- FastAPI
- SQLAlchemy
- SQLite
- Pytest

---

#  APIs

### PokéAPI

Responsável pelos dados dos Pokémon.

https://pokeapi.co/

Operações implementadas:

- GET
- POST
- PUT
- DELETE

---

# Persistência

### Room

- Favoritos
- Histórico
- Recentes

### DataStore

- Tema
- Idioma
- Nome do treinador
- Último time

### SQLite (Servidor)

- Times

---

#  Endpoints

| Método | Endpoint |
|---------|----------|
| GET | /teams |
| GET | /teams/{id} |
| POST | /teams |
| PUT | /teams/{id} |
| DELETE | /teams/{id} |

---


# Desenvolvedores


GitHub:
> 

LinkedIn:
> 

---
