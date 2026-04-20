# AI Assistant

AI Assistant is a Spring Boot microservice for conversational AI and agenda management. It combines a generative AI chat endpoint with a lightweight MongoDB-backed event agenda, health monitoring, and Swagger/OpenAPI documentation.

## Key Features

- **Conversational AI endpoint** with session history support
- **Agenda management** for events stored in MongoDB
- **Health check** endpoint for service and database status
- **Swagger UI** and API docs for easy exploration
- **Dynamic documentation aggregation** via `docs/ai-assistant.md`

## API Endpoints

### Agent (AI Assistant)

- `POST /agent/chat`
  - Request: `ChatRequest`
  - Response: `ChatResponse`
  - Description: Sends a user message to the AI assistant and returns a model response.
  - Body fields:
    - `sessionId` (optional): session identifier to preserve context
    - `message` (required): the user prompt

- `GET /agent/session/{id}/history`
  - Description: Returns the full message history for the given session.
  - Path parameter:
    - `id`: session identifier

### Agenda

- `GET /agenda`
  - Description: Returns all events by default.
  - Optional query parameters:
    - `date` (ISO date) - filter events on a specific date
    - `range=week` - return events for the upcoming 7 days

- `POST /agenda`
  - Description: Creates a new calendar event.
  - Body: `EventRequest`
  - Request fields:
    - `title`
    - `dateTime` (ISO date-time)
    - `participants`
    - `notes`

- `DELETE /agenda/{id}`
  - Description: Deletes the event with the given ID.

- `PATCH /agenda/{id}`
  - Description: Updates an existing event by ID.

### Health

- `GET /health`
  - Description: Returns service availability and database connectivity status.

## API Documentation

The application publishes OpenAPI metadata and a Swagger UI.

- API docs: `http://localhost:8080/api/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Docs redirect: `http://localhost:8080/docs`

> Note: The built-in documentation content is loaded from `src/main/resources/docs/ai-assistant.md` when `docs.enabled=true`.

## Configuration

Use environment variables or `application.yml` to configure:

- `SERVER_PORT` - HTTP port (default `8080`)
- `MONGO_URI` - MongoDB connection string
- `GROQ_API_KEY` - Groq / OpenAI API key
- `GROQ_API_URL` - Groq API base URL
- `GROQ_MODEL` - model name (default `llama-3.3-70b-versatile`)
- `LLM_TEMPERATURE` - model temperature
- `LLM_MAX_TOKENS` - maximum tokens per request
- `LOG_LEVEL` - logging level for the application

## Build and Run

### Prerequisites

- Java 21+
- Maven installed
- MongoDB accessible via `MONGO_URI`

### Run locally

```bash
mvn clean package
java -jar target/*.jar
```

### Environment example

```bash
export SERVER_PORT=8080
export MONGO_URI="mongodb://localhost:27017/ai-assistant"
export GROQ_API_KEY="your-groq-api-key"
export LLM_TEMPERATURE=0.7
export LLM_MAX_TOKENS=1000
mvn spring-boot:run
```

## Notes

- The application uses `springdoc-openapi` to expose API metadata.
- The Swagger UI is served from static resources under `/swagger`.
- Update `src/main/resources/docs/ai-assistant.md` to refresh the Swagger description content.
