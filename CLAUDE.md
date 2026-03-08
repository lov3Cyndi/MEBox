# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MEBox is a full-stack web application with a Vue 3 frontend and Spring Boot backend. The application appears to be a gaming content platform supporting heroes, maps, and videos with user authentication and rating systems.

## Directory Structure

```
MEBox/
├── 02_frontend/          # Vue 3 frontend (Vite + Ant Design Vue)
│   ├── src/
│   │   ├── api/          # Axios API calls
│   │   ├── router/       # Vue Router configuration
│   │   ├── stores/       # Pinia state management
│   │   ├── views/        # Page components
│   │   ├── utils/        # Utility functions
│   │   ├── App.vue       # Root component
│   │   └── main.js       # Entry point
│   └── vite.config.js   # Vite configuration
│
└── 03_backend/          # Spring Boot backend
    ├── src/main/java/org/cyndi/backend/
    │   ├── controller/   # REST endpoints
    │   ├── entity/       # JPA entities
    │   ├── mapper/       # MyBatis Plus mappers
    │   ├── dto/          # Data transfer objects
    │   ├── service/      # Business logic
    │   ├── security/    # JWT authentication
    │   └── config/      # Spring configuration
    └── pom.xml
```

## Commands

### Frontend

```bash
cd 02_frontend
npm install          # Install dependencies
npm run dev         # Start development server (port 5173)
npm run build       # Build for production
npm run preview     # Preview production build
```

### Backend

```bash
cd 03_backend
./mvnw spring-boot:run    # Start Spring Boot (port 8080)
./mvnw clean package      # Build JAR
./mvnw test               # Run tests
```

## API Communication

- Frontend runs on `http://localhost:5173`
- Backend runs on `http://localhost:8080`
- Frontend proxies `/api` requests to backend via Vite proxy configuration
- Authentication uses JWT tokens stored in localStorage

## Database

- MySQL database: `mebox`
- Connection: `jdbc:mysql://localhost:3306/mebox`
- Credentials: root / cyndi
- ORM: MyBatis Plus with automatic table creation via `schema.sql`

## Key Entities

- **User**: id, username, email, password, role, createdAt
- **Hero**: id, name, description, imageUrl
- **GameMap**: id, name, description, imageUrl
- **Video**: id, title, description, url, thumbnailUrl, userId
- **Comment**: id, content, videoId, userId
- **Rating**: id, score, videoId, userId
- **HeroMedia**: id, heroId, type (image/video), url
- **HeroRelation**: id, heroId, relatedHeroId, relationType

## Authentication

- JWT-based authentication with Spring Security
- Token stored in Authorization header
- Endpoints in `AuthController` handle login/register
- `JwtAuthenticationFilter` validates tokens on protected routes
