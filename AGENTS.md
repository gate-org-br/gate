# Project Notes for Codex

- Main web component sources live under `modules/gate/src/main/wc`.
- Do not manually maintain, review, or worry about generated files under `modules/gate/src/main/resources/META-INF/resources/gate` or `modules/gate/docs/gate`; they are regenerated automatically by the build.
- After changing files in `src/main/wc`, run `npm run build` from `modules/gate` to regenerate resources and docs.
- `modules/gate/package.json` defines `build` as `node build.mjs` and `build:min` as `node build.mjs --minify`.
- `build.mjs` cleans generated `*.js`, `*.mjs`, `*.css`, and `*.map` files in the resources output, then copies `.mjs` sources as `.js`, copies `.css`, processes `.wc` files plus optional `.wcc` and `.wcs`, compiles `.less`, copies icons, generates icon metadata/font, optionally minifies JS, and copies resources to `docs/gate`.
- The custom `SSE` wrapper is still needed for initial process requests because native `EventSource` only supports GET and cannot send POST payloads. Recovery by `/Progress?uuid=...` can use native `EventSource`.
- Commit messages and user-facing project messages should be written in English.
