# Investor Information Portal

Educational and informational company research scaffold. It is not investment advice. The data layer is designed for passive scheduled ingestion from public sources; user searches are intended to read the database only.

## Start locally

Requires Docker Compose and a Docker build environment with access to Maven Central. API keys are optional. Copy `.env.example` to `.env` if you want to change local settings, then run:

```sh
docker compose up --build
```

Open `http://localhost:8080`. The notice blocks the page until accepted. The admin data-source page is at `/admin.html` and uses HTTP Basic authentication configured with `ADMIN_USERNAME` and `ADMIN_PASSWORD`. Change the example password before exposing the service.

No external source is enabled by default. Mock companies are fictional and explicitly marked **SAMPLE DATA**. Adapter feature flags use `SOURCE_<SOURCE_ID>_ENABLED=true`; keys and flags are described in `.env.example` and `docs/ADDING_A_SOURCE.md`.

## Data layer

`DataSourceAdapter` defines the common adapter contract. `Adapters` registers source adapters; `MockAdapter` provides local sample companies. Scheduled and manual refreshes map facts with provenance and persist them in PostgreSQL. The admin API reports status and offers one-source refresh. A source with no credentials or a disabled feature flag is not fetched.

Source facts retain `source_id`, `source_url`, `retrieved_at`, and confidence. Source metadata and the initial database structure are in Flyway migration `V1__initial.sql`.

## Configuration

See `.env.example` for PostgreSQL, source credentials, Turnstile, SMTP, support, and JWT settings. The current initial build does not require third-party keys. Keep secrets out of source control.

## Current implementation boundary

This repository establishes the data-source and application foundation requested for the structure-first build. It is not yet the complete investor portal described in the broader product brief: registration/refresh-token/MFA flows, contact delivery, user watchlists, analyst search/profile APIs, per-source production response mappings, rate limiting, and comprehensive WireMock coverage remain future implementation work. Some official-source entries are intentionally conservative catalog adapters and must be wired to documented API/download/feed endpoints before enabling. In particular, Indian exchange/regulator entries must use official downloads or feeds and must never scrape pages.

## Source onboarding

Follow [docs/ADDING_A_SOURCE.md](docs/ADDING_A_SOURCE.md). Security assumptions and current implementation notes are in [SECURITY.md](SECURITY.md).
