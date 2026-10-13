# DailyHelp — Everyday Task Manager

A full-stack task manager with a React/Vite interface, Spring Boot REST API, MySQL persistence, Flyway migrations, and session-based authentication. This is a hobby/MVP baseline, not a claim of production security certification.

## Requirements
- JDK 21, Maven 3.9+, Node.js 22 LTS, npm, Docker Desktop, Git
- macOS with zsh commands below

## 1. Configure local environment
```zsh
cd dailyhelp
cp .env.example .env
# Edit .env and replace both example passwords. Keep .env out of Git.
set -a; source .env; set +a
```

The Compose MySQL container binds to `127.0.0.1:3307`, so it does not conflict with a local MySQL service on port 3306. The named Docker volume preserves your tasks across container/backend restarts.

## 2. Start database and run backend
```zsh
docker compose up -d mysql
docker compose ps
cd backend
mvn test
mvn spring-boot:run
```

Expected: `dailyhelp-mysql` becomes healthy; Maven tests end with `BUILD SUCCESS`; Spring Boot starts on port 8080 and Flyway logs a successful migration. Verify `http://localhost:8080/actuator/health` returns `{"status":"UP"}`. Leave this terminal running.

## 3. Start frontend in another terminal
```zsh
cd dailyhelp/frontend
cp .env.example .env
npm install
npm run build
npm run dev
```

Open `http://localhost:5173`. Register with a real email and a password of at least 8 characters. Create tasks, edit them, mark them completed, search/filter/sort, and delete them. Dashboard values are read from the API. Check persistence by restarting the backend and reloading the page.

## 4. Test persistence and APIs
```zsh
docker exec -it dailyhelp-mysql mysql -udailyhelp -p dailyhelp
```
Enter the value of `DB_PASSWORD` from `.env`, then run `SELECT id,name,email FROM users;` and `SELECT id,owner_id,title,status FROM tasks;`. Do not share the output publicly if it contains personal data. OpenAPI UI is at `http://localhost:8080/swagger-ui/index.html`. Import `docs/API.md` as the API reference; use browser UI or Postman to exercise endpoints. For requests from Postman, first call `GET /api/csrf`, keep cookies enabled, and send the `X-XSRF-TOKEN` cookie value as `X-XSRF-TOKEN` header for POST/PUT/PATCH/DELETE.

## 5. Stop/restart
```zsh
# Stop backend with Ctrl+C; stop Vite with Ctrl+C
docker compose stop mysql
docker compose start mysql
# To remove container AND all local database data (destructive):
docker compose down -v
```

## Architecture
`React page -> api/client.js -> HTTP session + CSRF -> Spring Security -> controller -> service -> owner-scoped repository -> MySQL`. DTOs define the API contract; JPA entities are never serialized directly. `LocalDate` is used for due dates (calendar dates, no timezone conversion); creation/update timestamps are `Instant` and stored/serialized in UTC.

## Configuration
Backend: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGIN`, `PORT`, `SESSION_COOKIE_SECURE`, and `SESSION_COOKIE_SAME_SITE`. For separate cross-site frontend/backend domains, use HTTPS and set `SESSION_COOKIE_SECURE=true` and `SESSION_COOKIE_SAME_SITE=None` for both session and CSRF cookies; verify this with the chosen host. Local development defaults to insecure cookies and `Lax`. Frontend: `VITE_API_URL` (baked into the static build, and not secret). The frontend must use `withCredentials`; the backend must allow the exact frontend origin and credentials. In production, set the session cookie to `Secure`, use HTTPS, configure trusted proxy headers, and set the correct CORS origin. Never place database passwords or secrets in `VITE_*` variables.

## Deployment notes
- Frontend: build with `npm run build`; publish `frontend/dist` as a static site (Vercel Hobby is free for personal/non-commercial use, subject to its current limits). Configure `VITE_API_URL` to the deployed API base URL before building.
- Backend: deploy `backend/Dockerfile` to a container-capable host. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGIN`, `PORT`, and production cookie settings in the host dashboard. Use a managed MySQL service with durable storage or a paid database plan with backups.
- Current free hosting trade-offs: Render documents that free web services spin down after 15 minutes idle, may take about a minute to wake, and its free Postgres expires after 30 days. Therefore do not put important long-lived data in that free database. This project uses MySQL locally; a provider offering only PostgreSQL would require a database migration/driver/config change and testing, not simply changing a URL. Check the provider's current plan limits before deployment.
- Hobby deployment is not reliable production hosting: free services can sleep/restart, quotas change, and backups/uptime guarantees may be absent. For real users, budget for persistent managed DB, automated backups, monitoring, and a paid always-on API.

## Production hardening checklist
1. Use HTTPS everywhere and secure cookies; validate proxy/forwarded-header setup.
2. Add login/registration rate limiting, email verification, password reset, and account lifecycle flows.
3. Add security headers/CSP, dependency scanning, backup/restore drills, and alerting.
4. Decide session expiry/idle timeout policy and rotate session IDs on authentication.
5. Configure database least-privilege access, TLS, and automated backups.
6. Add integration tests against a disposable MySQL database and end-to-end browser tests.
7. Review privacy policy, logging retention, accessibility, and deployment provider terms.

## Known MVP limitations
- No email verification/password reset, social login, attachments, reminders, or team sharing.
- Service unit tests are included; full browser/E2E and real-MySQL integration coverage should be added before public launch.
- Authentication is session/cookie based; CSRF protection is enabled. Production cookie settings and rate limiting require host-specific configuration/hardening.
- `TaskService` currently treats missing and not-owned task IDs identically as 404, by design.
