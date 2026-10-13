# DailyHelp API contract
Base URL: `http://localhost:8080/api`. Session cookie authentication; JSON requests. All routes except registration, login, CSRF bootstrap, health, and OpenAPI require an authenticated session. Send `X-XSRF-TOKEN` with state-changing requests; the frontend client handles this.

## Authentication
- `GET /csrf` — creates/returns CSRF token and sets `XSRF-TOKEN` cookie.
- `POST /auth/register` — `{"name":"Nikhil","email":"n@example.com","password":"at-least-8"}`; returns 201 and public user fields. Duplicate email returns 400 with a safe message.
- `POST /auth/login` — `{"email":"n@example.com","password":"..."}`; returns public user fields and establishes session. Invalid credentials return 401.
- `GET /auth/me` — current user; 401 if unauthenticated.
- `POST /auth/logout` — invalidates session and returns 204.

## Tasks (authenticated)
- `GET /tasks?q=plan&status=PENDING&priority=HIGH&sort=createdAt&page=0&size=20` — paginated results. `sort`: `createdAt`, `dueDate`, `priority`; status: `PENDING|IN_PROGRESS|COMPLETED`; priority: `LOW|MEDIUM|HIGH`.
- `POST /tasks` — `{"title":"Plan week","description":"Optional","dueDate":"2026-10-20","priority":"MEDIUM","status":"PENDING"}`; returns 201. `dueDate` may be null.
- `GET /tasks/{id}` — details; returns 404 when missing or not owned by caller.
- `PUT /tasks/{id}` — same body as create; returns updated record.
- `PATCH /tasks/{id}/status` — `{"status":"COMPLETED"}`.
- `DELETE /tasks/{id}` — returns 204.
- `GET /dashboard` — live totals, overdue count, completion percentage, recent and upcoming tasks.

## Error response
Validation errors return 400, missing/non-owned resources 404, unauthenticated requests 401, forbidden/CSRF failures 403, and unexpected server errors a generic 500. Typical body: `{"status":400,"error":"Bad Request","message":"Validation failed","path":"/api/tasks","fields":{"title":"must not be blank"}}`. The backend intentionally does not return stack traces or internal exception details.

## Authorization
The API never accepts an owner ID. It derives the user identity from the server-side session and scopes task reads/updates/deletes by both task ID and owner ID.
