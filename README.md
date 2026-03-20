# TaskFlow

Personal task management application built with the Double Diamond design methodology.

**4-step daily flow:** Capture → Morning Review → Execute → Reflect

---

## Quick Start (one command)

```bash
# 1. Copy environment file
cp .env.example .env

# 2. Start everything
docker-compose up --build
```

Then open **http://localhost:3000** in your browser.

---

## Architecture

| Layer | Technology |
|---|---|
| Frontend | React 18 + TypeScript + Vite + Tailwind CSS |
| Backend | Spring Boot 3 + Java 17 |
| Relational DB | PostgreSQL 16 |
| NoSQL DB | MongoDB 7 |
| Messaging | Apache ActiveMQ 5.18 |
| Auth | JWT (Spring Security) |
| Orchestration | Docker Compose |

---

## Services

| Service | URL | Purpose |
|---|---|---|
| Frontend | http://localhost:3000 | React app served by nginx |
| Backend API | http://localhost:8080 | Spring Boot REST API |
| ActiveMQ Admin | http://localhost:8161 | Message queue console |
| PostgreSQL | localhost:5432 | Relational data |
| MongoDB | localhost:27017 | Logs, notifications, preferences |

---

## Features

- **Quick Capture** — Add tasks instantly with one line of text
- **Auto-categorization** — Tasks categorized automatically by keywords (Work, Health, Learning, Finance, Home, Personal)
- **Morning Review** — Pick your 3 daily priorities each morning
- **Deadline Reminders** — ActiveMQ scheduler sends notifications 24h before deadlines
- **Nightly Reflection** — View completed tasks and priority completion rate
- **JWT Authentication** — Secure login/register with access + refresh tokens

---

## Development (without Docker)

### Backend

```bash
cd backend
mvn spring-boot:run
```

Requires PostgreSQL on `localhost:5432`, MongoDB on `localhost:27017`, ActiveMQ on `localhost:61616`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Opens at **http://localhost:5173** with Vite proxy to backend.

---

## Environment Variables

See `.env.example` for all configurable values.

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_PASSWORD` | `taskflow_secret` | PostgreSQL password |
| `MONGO_PASSWORD` | `taskflow_secret` | MongoDB password |
| `ACTIVEMQ_PASSWORD` | `admin_secret` | ActiveMQ admin password |
| `JWT_SECRET` | *(dev default)* | **Change in production!** |

---

## API Reference

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | None | Register new user |
| POST | `/api/auth/login` | None | Login, get JWT pair |
| GET | `/api/tasks` | Bearer | List tasks |
| POST | `/api/tasks` | Bearer | Create task |
| PATCH | `/api/tasks/{id}/complete` | Bearer | Mark task complete |
| GET | `/api/priorities/today` | Bearer | Get today's priorities |
| POST | `/api/priorities` | Bearer | Set 3 daily priorities |
| GET | `/api/reflection/today` | Bearer | Nightly reflection data |
| GET | `/api/notifications` | Bearer | User notifications |
