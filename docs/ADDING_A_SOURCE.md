# Adding and enabling a data source

All ingestion is backend initiated. Search and profile requests must read stored records and must never call external sources. Each adapter must use only public data under the source's terms, validate responses, retain attribution, and target a fixed allow-listed HTTPS host. Do not accept URLs or domain names from a user.

## Enable an existing adapter

1. Review the source's current API terms, license, rate limits, and privacy rules. Record the source URL and attribution in the migration/source registry.
2. Put the API key in the deployment `.env` using the variable listed in `.env.example`. Do not commit `.env`.
3. Set the adapter feature flag, for example `SOURCE_FINNHUB_ENABLED=true`. Flags default to false. Keyless public feeds also require their flag.
4. Restart with `docker compose up --build -d`.
5. Open `/admin.html` and sign in with the configured administrator account (`ADMIN_USERNAME` / `ADMIN_PASSWORD` in this scaffold). Find the adapter and click **Run now**.
6. Check the adapter status and last run in Data Sources. Review imported records and their `source_id`, `source_url`, `retrieved_at`, and confidence. Mock records are marked **SAMPLE DATA** and are fictional.

## Add a new adapter

1. Implement `DataSourceAdapter` (id, isEnabled, fetch, validate, mapToEntities) in `backend/src/main/java/com/purelyinfo/ingestion`.
2. Use a fixed HTTPS endpoint and host allow-list entry, bounded timeout/response size, retries with backoff, and a cache. Read credentials only from environment variables. Never derive a URL from user input.
3. Validate the response schema before mapping it. Map each fact to the canonical `SourceFact` provenance fields and record the source license/terms.
4. Register the adapter in `Adapters` and its disabled-by-default flag in `application.yml`; add the environment variable to `.env.example`.
5. Add a WireMock test using a recorded, sanitized response for validation, mapping, disabled/key-missing behavior, and failure handling. No live key should be needed.
6. Add or update migration/source metadata. Run the test suite, then follow the enablement steps above.

Adapters currently use environment flag names such as `SOURCE_FINNHUB_ENABLED`; secrets are not needed to start the application. Statuses are Disabled, Key missing, Healthy, or Error. Keyless public sources use only their explicit feature flag.
