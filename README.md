# GitHub Analyzer API

A Spring Boot REST API for tracking GitHub repositories, contributions, issues, pull requests with analytics and AI-powered features.

## Tech Stack

- **Backend:** Spring Boot 3.2
- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Security:** Spring Security + JWT
- **Build Tool:** Maven
- **AI:** OpenAI GPT-3.5 Turbo
- **Docs:** Swagger UI

## Features

- JWT Authentication (register, login, email verification, forgot/reset password)
- User profiles with leaderboard ranking
- Repository management (CRUD)
- Issues & Pull Requests tracking with search and filters
- Contribution tracking with points system
- GitHub REST API integration (import repos, sync issues/PRs/commits/stars/forks)
- Analytics dashboard (contributions per month, PRs vs Issues, language-wise, yearly growth, daily heatmap)
- Notifications (PR merged, issue assigned, repo updated)
- AI repository summary generation
- AI issue auto-categorization (Bug, Feature, Enhancement, Documentation, Security)

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+

### Setup

1. Clone the repository
```bash
git clone https://github.com/ajay-ramasamy/github-analyzer-.git
cd github-analyzer-
```

2. Create the config file from the example
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

3. Fill in your credentials in `application.properties`
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
app.jwt.secret=YOUR_JWT_SECRET
openai.api.key=YOUR_OPENAI_KEY
spring.mail.username=YOUR_GMAIL
spring.mail.password=YOUR_APP_PASSWORD
```

4. Run the application
```bash
mvn spring-boot:run
```

The API will start at `http://localhost:8080`

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |
| GET | `/api/auth/verify-email?token=` | Verify email |
| POST | `/api/auth/forgot-password` | Send reset link |
| POST | `/api/auth/reset-password` | Reset password |
| POST | `/api/auth/change-password` | Change password |

### Users
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users/me` | Get profile |
| PUT | `/api/users/me` | Update profile |
| PUT | `/api/users/me/github-token` | Connect GitHub |
| GET | `/api/users/leaderboard` | Leaderboard |

### Repositories
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/repositories` | Get all repositories |
| GET | `/api/repositories/{id}` | Get by ID |
| POST | `/api/repositories` | Create |
| PUT | `/api/repositories/{id}` | Update |
| DELETE | `/api/repositories/{id}` | Delete |
| GET | `/api/repositories/search?name=&language=` | Search |

### Issues
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/repositories/{repoId}/issues` | Get all issues |
| POST | `/api/issues` | Create issue |
| PUT | `/api/issues/{id}` | Update issue |
| DELETE | `/api/issues/{id}` | Delete issue |
| GET | `/api/repositories/{repoId}/issues/search` | Search issues |

### Pull Requests
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/repositories/{repoId}/pull-requests` | Get all PRs |
| POST | `/api/pull-requests` | Create PR |
| PUT | `/api/pull-requests/{id}` | Update PR |
| DELETE | `/api/pull-requests/{id}` | Delete PR |
| GET | `/api/repositories/{repoId}/pull-requests/search` | Search PRs |

### Contributions
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/repositories/{repoId}/contributions` | Get all |
| POST | `/api/contributions` | Create |
| PUT | `/api/contributions/{id}` | Update |
| DELETE | `/api/contributions/{id}` | Delete |

### GitHub Sync
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/github/import-repositories` | Import from GitHub |
| POST | `/api/github/repositories/{id}/sync-issues` | Sync issues |
| POST | `/api/github/repositories/{id}/sync-pull-requests` | Sync PRs |
| POST | `/api/github/repositories/{id}/sync-commits` | Sync commits |
| POST | `/api/github/repositories/{id}/sync-stats` | Sync stars/forks |

### Analytics
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/analytics/dashboard` | Full dashboard data |

### Notifications
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/notifications` | Get all |
| GET | `/api/notifications/unread` | Get unread |
| GET | `/api/notifications/unread/count` | Unread count |
| PUT | `/api/notifications/{id}/read` | Mark as read |

### AI
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/ai/repositories/{id}/summarize` | Generate repo summary |
| POST | `/api/ai/issues/{id}/categorize` | Categorize issue |

## Authentication

All endpoints except `/api/auth/**` require a Bearer token:

```
Authorization: Bearer <jwt_token>
```

## Database Schema

- `users` — user accounts
- `repositories` — GitHub repositories
- `issues` — repository issues
- `pull_requests` — repository pull requests
- `contributions` — commits and contributions
- `notifications` — user notifications
