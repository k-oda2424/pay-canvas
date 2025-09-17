# Repository Guidelines

## Project Structure & Module Organization

Use a simple, predictable layout:

```
/src       # application/library code
/test      # automated tests mirroring src
/assets    # static assets (images, styles, fixtures)
/scripts   # dev tooling and one-off scripts
/docs      # additional documentation
```

Keep modules small and cohesive. Co-locate tests and fixtures when it improves clarity. Prefer index files only when they simplify imports.

## Build, Test, and Development Commands

Prefer Make targets when available:
- make setup: install dependencies and prepare tooling
- make dev: run local dev server or watch mode
- make build: produce a production build or compiled artifact
- make test: run tests with coverage
- make lint / make fmt: lint and auto-format the codebase

If no Makefile exists, use language-native scripts (examples):
- npm ci | npm install
- npm run dev | npm run build | npm test

## Coding Style & Naming Conventions

- Indentation: 2 spaces; UTF-8; LF line endings; max line length ~100.
- Filenames/dirs: kebab-case (e.g., `payment-canvas/`); tests: mirror src path.
- Identifiers: camelCase for variables/functions; PascalCase for classes/types; UPPER_SNAKE_CASE for constants.
- Formatting: run `make fmt` or `npm run format` (use Prettier/black/gofmt per stack).
- Linting: run `make lint` or `npm run lint`; fix warnings proactively.

## Testing Guidelines

- Place tests under `/test` mirroring `/src` (e.g., `src/canvas/draw.ts` → `test/canvas/draw.spec.ts`).
- Name tests `*.spec.*` or `*_test.*` per ecosystem. Aim for ≥80% coverage on core modules.
- Write fast, isolated unit tests; add integration tests for edge cases and critical flows.
- Run locally before pushing: `make test` or `npm test`.

## Commit & Pull Request Guidelines

- Use Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`. Keep subject ≤72 chars.
- Reference issues in body (e.g., `Closes #123`) and explain motivation + approach.
- PRs: include a clear summary, linked issue, test evidence (logs/screenshots), and note any breaking changes or migrations.
- Keep PRs small and focused; update docs and examples when behavior changes.

## Security & Configuration Tips

- Never commit secrets. Use `.env.example` and local `.env` (gitignored). Rotate keys when in doubt.
- Prefer least-privileged tokens for CI. Validate inputs and handle errors explicitly.
- Run linters/tests on every branch; enforce status checks before merging.

