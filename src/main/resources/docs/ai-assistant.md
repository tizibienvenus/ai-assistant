# Inov Consulting AI Assistant API

Welcome to the AI Assistant API documentation. This service includes an intelligent chat agent with session memory and an agenda management API backed by MongoDB.

## Overview

The AI Assistant is designed to support conversational interactions and schedule management in a single service:

- **AI Chat**: Send natural-language messages to the assistant and receive AI-generated responses.
- **Session history**: Persist and retrieve conversation history by session ID.
- **Agenda**: Create, view, update, and delete calendar events.
- **Health**: Monitor service availability and database connectivity.

## Usage

### 1. Chat with the AI Agent

- Endpoint: `POST /agent/chat`
- Request:

```json
{
  "sessionId": "session-123",
  "message": "Plan my week with meetings and follow-ups"
}
```

- Response:

```json
{
  "sessionId": "session-123",
  "response": "Je recommande de réserver des créneaux le mardi et le jeudi pour les réunions...",
  "toolUsed": null,
  "turn": 1
}
```

### 2. Retrieve Session History

- Endpoint: `GET /agent/session/{id}/history`
- Example: `GET /agent/session/session-123/history`

Response includes:
- `session_id`
- `messages`
- `created_at`
- `last_activity`

### 3. Agenda Management

#### List Events

- Endpoint: `GET /agenda`
- Optional date filter: `GET /agenda?date=2026-04-20`
- Optional weekly range: `GET /agenda?range=week`

#### Create an Event

- Endpoint: `POST /agenda`
- Request body:

```json
{
  "title": "Réunion produit",
  "dateTime": "2026-04-22T10:00:00",
  "participants": ["alice@example.com", "bob@example.com"],
  "notes": "Préparer la démonstration"
}
```

#### Update an Event

- Endpoint: `PATCH /agenda/{id}`
- Request body uses the same schema as creation.

#### Delete an Event

- Endpoint: `DELETE /agenda/{id}`

## Health Check

- Endpoint: `GET /health`
- Returns service and MongoDB connectivity state.

## API Reference

The OpenAPI specification is available at `/api/v3/api-docs`.
The Swagger UI is served from `/swagger-ui/index.html` or `/docs`.

## Configuration Notes

This service loads dynamic documentation from `src/main/resources/docs/ai-assistant.md` when the `docs.enabled` property is enabled.

Environment variables used by the service:

- `SERVER_PORT`
- `MONGO_URI`
- `GROQ_API_KEY`
- `GROQ_API_URL`
- `GROQ_MODEL`
- `LLM_TEMPERATURE`
- `LLM_MAX_TOKENS`
- `LOG_LEVEL`

## Contact

For questions or support, contact:

- **TIZI BIENVENUS**
- Email: `tizibienvenus@gmail.com`
- LinkedIn: https://www.linkedin.com/in/bienvenus-tizi-806637241/
