"""
Testes automatizados da API PokéTeam Builder, usando o TestClient do
FastAPI (baseado em requests) para validar os 5 endpoints:
GET /teams, GET /teams/{id}, POST /teams, PUT /teams/{id}, DELETE /teams/{id}.

Para rodar:
    pip install pytest
    pytest test_main.py -v
"""

from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_create_and_get_team():
    payload = {
        "name": "Time de Teste",
        "description": "Criado via pytest",
        "pokemon": [25, 6, 9]
    }
    response = client.post("/teams", json=payload)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "Time de Teste"
    assert data["pokemon"] == [25, 6, 9]

    team_id = data["id"]
    get_response = client.get(f"/teams/{team_id}")
    assert get_response.status_code == 200
    assert get_response.json()["id"] == team_id


def test_get_all_teams_returns_list():
    response = client.get("/teams")
    assert response.status_code == 200
    assert isinstance(response.json(), list)


def test_update_team():
    create = client.post("/teams", json={"name": "Original", "description": "x", "pokemon": [1]})
    team_id = create.json()["id"]

    update_payload = {"name": "Atualizado", "description": "y", "pokemon": [1, 4]}
    response = client.put(f"/teams/{team_id}", json=update_payload)
    assert response.status_code == 200
    assert response.json()["name"] == "Atualizado"


def test_delete_team():
    create = client.post("/teams", json={"name": "Para excluir", "description": "", "pokemon": []})
    team_id = create.json()["id"]

    delete_response = client.delete(f"/teams/{team_id}")
    assert delete_response.status_code == 204

    get_response = client.get(f"/teams/{team_id}")
    assert get_response.status_code == 404


def test_create_team_with_more_than_six_pokemon_fails():
    payload = {"name": "Time Inválido", "description": "", "pokemon": [1, 2, 3, 4, 5, 6, 7]}
    response = client.post("/teams", json=payload)
    assert response.status_code == 400


def test_get_nonexistent_team_returns_404():
    response = client.get("/teams/999999")
    assert response.status_code == 404
