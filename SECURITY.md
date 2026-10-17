# Security notes and threat model

This application handles public company data and is intended for informational use. Threats include malicious or oversized upstream responses, SSRF, secret leakage, unauthorized admin refreshes, SQL injection, cross-user data access, contact spam, and misleading or unprovenanced facts.

## Implemented controls

- External requests are constructed from adapter-owned fixed HTTPS endpoints and checked against an exact host allow-list; user input is not used to form URLs.
- HTTP requests have connection and response time limits, a response-size bound, retries for transient failures, and an in-memory cache.
- External JSON is parsed and passed through a validation gate before mapping. Facts retain source ID, source URL, retrieval time, confidence, and a sample marker.
- Source adapters and scheduled ingestion are off by default; no third-party keys are needed at startup. User-facing sample entities are fictional and labeled.
- JPA uses parameterized persistence; the initial schema uses UUID keys and indexes company names for trigram search.
- The admin page and refresh API require the ADMIN role through HTTP Basic. Set a strong `ADMIN_PASSWORD` before exposing the service.
- CSP, frame denial, content-type protection, and referrer policy headers are configured. The application container is non-root and read-only. PostgreSQL uses a separate internal Compose network; the app has a loopback-published host port and an outbound network for allow-listed public-source calls.

## OWASP mapping and work still needed

This is a foundation, not a production security certification. Complete authentication lifecycle (short access JWT, rotating hashed refresh tokens, CSRF, MFA, lockout, password reset), role/ownership policies, per-route rate limiting, contact form protections, audit events, outbound private-address resolution checks, encrypted secrets/backups, and security/abuse-case tests before production use. Configure CORS to the deployed origin when browser APIs are introduced. Do not expose actuator or database ports publicly.

The admin account currently uses Spring Security HTTP Basic and is configured by environment variables. Never deploy the local fallback password. Upstream data is untrusted; adapter-specific JSON schemas and reviewed source mappings must replace generic raw-response mapping before any source is enabled.

## Passive OSINT boundaries

Use only public/official sources and each source's current terms and rate limits. India registry and exchange records must be obtained from official downloads/feeds only. Domain checks must be limited to a company's own official domain, performed by scheduled/admin jobs, and results reviewed before import. Do not run OSINT against individuals or expose collection to user input. SpiderFoot is not exposed or automatically invoked by this application.
