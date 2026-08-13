# task-management-backend

Team-based task management API (Spring Boot + PostgreSQL + JWT).  
Portfolio-style Jira/Trello subset with role-based access control.

## Stack

- Java 17, Spring Boot 4
- Spring Security + JWT
- Spring Data JPA, PostgreSQL
- Validation + global exception handling

## Architecture

```
controller  →  service  →  repository  →  PostgreSQL
                ↑
            security (JWT filter)
```

### Domain model

```
users 1──* team_members *──1 teams
users 1──* tasks (assignee / createdBy)
teams 1──* tasks
teams 1──* team_invites
tasks 1──* comments
```

| Entity | Notes |
|--------|--------|
| User | email/password/name |
| Team | owner + members |
| TeamMember | role `ADMIN` \| `MEMBER` |
| TeamInvite | reusable link token (7 days) |
| Task | status `TODO` \| `DOING` \| `DONE`, assignee, dueDate |
| Comment | task discussion |

## Local setup

### 1. PostgreSQL

```bash
brew services start postgresql@18
psql postgres
```

```sql
CREATE USER taskuser WITH PASSWORD 'taskpass';
CREATE DATABASE taskdb OWNER taskuser;
GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;
\c taskdb
GRANT USAGE, CREATE ON SCHEMA public TO taskuser;
```

### 2. Run

```bash
./gradlew bootRun
```

Optional demo seed:

```bash
APP_SEED_ENABLED=true ./gradlew bootRun
```

Demo accounts (when seed enabled):

| Email | Password | Role |
|-------|----------|------|
| `admin@demo.com` | `password123` | Admin of Demo Team |
| `member@demo.com` | `password123` | Member |

API base: `http://localhost:8080`

## Main API

### Auth

| Method | Path | Auth |
|--------|------|------|
| POST | `/api/auth/signup` | public |
| POST | `/api/auth/login` | public |
| GET/PUT | `/api/users/me` | JWT |

### Teams & invites

| Method | Path | Auth |
|--------|------|------|
| POST/GET | `/api/teams` | JWT |
| GET | `/api/teams/{id}` | member |
| GET | `/api/teams/{id}/members` | member |
| POST | `/api/teams/{id}/invites` | **admin** |
| POST | `/api/invites/{token}/accept` | JWT |

### Tasks & comments

| Method | Path | Auth |
|--------|------|------|
| GET/POST | `/api/teams/{id}/tasks` | member |
| GET/PUT | `/api/teams/{id}/tasks/{taskId}` | member |
| DELETE | `/api/teams/{id}/tasks/{taskId}` | **admin** |
| GET/POST | `/api/teams/{id}/tasks/{taskId}/comments` | member |
| DELETE | `.../comments/{commentId}` | author or admin |

Use header: `Authorization: Bearer <accessToken>`

### Example

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@demo.com","password":"password123"}'
```

## Configuration

| Variable | Purpose | Default |
|----------|---------|---------|
| `DATABASE_URL` | JDBC URL | local `taskdb` |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | DB credentials | `taskuser` / `taskpass` |
| `JWT_SECRET` | HMAC secret (≥256 bit) | dev secret |
| `CORS_ALLOWED_ORIGINS` | comma-separated FE origins | `http://localhost:3000` |
| `APP_SEED_ENABLED` | load demo data | `false` |
| `PORT` | HTTP port | `8080` |

See `.env.example`.

## Deploy (Render)

Render Web Service **does not offer a Java runtime** — use **Docker** (this repo includes a `Dockerfile`).

1. Create a **PostgreSQL** instance on Render.
2. Create a **Web Service** from this repo.
   - **Language**: `Docker`
   - Dockerfile path: `./Dockerfile` (default)
3. Set env vars:
   - `DATABASE_URL` = JDBC URL (`jdbc:postgresql://...`)
   - `DATABASE_USERNAME` / `DATABASE_PASSWORD`
   - `JWT_SECRET` = long random string
   - `CORS_ALLOWED_ORIGINS` = your Vercel URL (e.g. `https://your-app.vercel.app`)
   - `JPA_DDL_AUTO=update`
   - `APP_SEED_ENABLED=true` (optional, first boot only)
4. Confirm health by calling `/api/auth/login`.

`render.yaml` uses `runtime: docker`.

## Interview one-liner

> I built a team-based task management system with role-based access control and RESTful APIs using Spring Boot.
