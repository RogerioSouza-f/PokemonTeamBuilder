"""
PokéTeam Builder API — Backend próprio em FastAPI.

Responsável por armazenar e gerenciar os Times criados pelo usuário
no app Android (PokéTeam Builder). Usa SQLite como banco de dados,
via SQLAlchemy ORM.

Endpoints:
    GET    /teams        -> lista todos os times
    GET    /teams/{id}   -> retorna um time específico
    POST   /teams        -> cria um novo time
    PUT    /teams/{id}   -> atualiza um time existente
    DELETE /teams/{id}   -> remove um time

Para rodar:
    pip install -r requirements.txt
    uvicorn main:app --reload --host 0.0.0.0 --port 8000
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field, ConfigDict
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.orm import declarative_base, sessionmaker, Session
from typing import List
import json

# ---------------------------------------------------------------------
# Configuração do banco de dados (SQLite)
# ---------------------------------------------------------------------
DATABASE_URL = "sqlite:///./poketeam.db"
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


class TeamModel(Base):
    """Modelo de tabela SQLAlchemy que representa um Time no banco."""
    __tablename__ = "teams"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    description = Column(String, nullable=True, default="")
    # Guardamos a lista de IDs de Pokémon serializada como JSON (string)
    # dentro de uma coluna de texto — abordagem simples e suficiente
    # para o escopo deste projeto acadêmico.
    pokemon_json = Column(String, nullable=False, default="[]")

    @property
    def pokemon(self) -> List[int]:
        return json.loads(self.pokemon_json)

    @pokemon.setter
    def pokemon(self, value: List[int]):
        self.pokemon_json = json.dumps(value)


Base.metadata.create_all(bind=engine)


# ---------------------------------------------------------------------
# Schemas Pydantic (validação de entrada/saída)
# ---------------------------------------------------------------------
class TeamBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    description: str = ""
    pokemon: List[int] = Field(default_factory=list, max_length=6)


class TeamCreate(TeamBase):
    """Schema usado no corpo do POST /teams (sem id)."""
    pass


class TeamUpdate(TeamBase):
    """Schema usado no corpo do PUT /teams/{id} (sem id)."""
    pass


class TeamResponse(TeamBase):
    id: int
    model_config = ConfigDict(from_attributes=True)


# ---------------------------------------------------------------------
# App FastAPI
# ---------------------------------------------------------------------
app = FastAPI(
    title="PokéTeam Builder API",
    description="API própria para gerenciamento de Times do app PokéTeam Builder.",
    version="1.0.0"
)

# CORS liberado para facilitar testes locais (ex: Postman, emulador Android)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def to_response(team: TeamModel) -> TeamResponse:
    return TeamResponse(id=team.id, name=team.name, description=team.description, pokemon=team.pokemon)


# ---------------------------------------------------------------------
# Endpoints
# ---------------------------------------------------------------------
@app.get("/", tags=["root"])
def root():
    return {"message": "PokéTeam Builder API está no ar. Veja /docs para a documentação interativa."}


@app.get("/teams", response_model=List[TeamResponse], tags=["teams"])
def get_all_teams():
    """Retorna todos os times cadastrados."""
    db = SessionLocal()
    try:
        teams = db.query(TeamModel).all()
        return [to_response(t) for t in teams]
    finally:
        db.close()


@app.get("/teams/{team_id}", response_model=TeamResponse, tags=["teams"])
def get_team(team_id: int):
    """Retorna um time específico pelo id."""
    db = SessionLocal()
    try:
        team = db.query(TeamModel).filter(TeamModel.id == team_id).first()
        if not team:
            raise HTTPException(status_code=404, detail="Time não encontrado")
        return to_response(team)
    finally:
        db.close()


@app.post("/teams", response_model=TeamResponse, status_code=201, tags=["teams"])
def create_team(payload: TeamCreate):
    """Cria um novo time."""
    if len(payload.pokemon) > 6:
        raise HTTPException(status_code=400, detail="Um time pode ter no máximo 6 Pokémon")

    db = SessionLocal()
    try:
        team = TeamModel(name=payload.name, description=payload.description)
        team.pokemon = payload.pokemon
        db.add(team)
        db.commit()
        db.refresh(team)
        return to_response(team)
    finally:
        db.close()


@app.put("/teams/{team_id}", response_model=TeamResponse, tags=["teams"])
def update_team(team_id: int, payload: TeamUpdate):
    """Atualiza um time existente por completo."""
    if len(payload.pokemon) > 6:
        raise HTTPException(status_code=400, detail="Um time pode ter no máximo 6 Pokémon")

    db = SessionLocal()
    try:
        team = db.query(TeamModel).filter(TeamModel.id == team_id).first()
        if not team:
            raise HTTPException(status_code=404, detail="Time não encontrado")

        team.name = payload.name
        team.description = payload.description
        team.pokemon = payload.pokemon
        db.commit()
        db.refresh(team)
        return to_response(team)
    finally:
        db.close()


@app.delete("/teams/{team_id}", status_code=204, tags=["teams"])
def delete_team(team_id: int):
    """Remove um time pelo id."""
    db = SessionLocal()
    try:
        team = db.query(TeamModel).filter(TeamModel.id == team_id).first()
        if not team:
            raise HTTPException(status_code=404, detail="Time não encontrado")
        db.delete(team)
        db.commit()
        return None
    finally:
        db.close()
