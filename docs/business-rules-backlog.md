# Product validations & business rules backlog

Tracked open questions from the 2026-10-02 session. Each item records the
decision (implement / deferred / rejected) and which layer owns it, so future
sessions can pick items up without re-deriving the reasoning.

Layer key: DTO = `ProductRequest` constraints + `@Valid` in controller,
Entity = invariants in `Product`, Service = orchestration and checks,
DB = schema constraint (the only enforcement that survives concurrency).

## Observed issues (from live testing)

- [ ] **Price scale rounding.** `20.9999` is stored as `21.00` with no error.
  Cause: Hibernate maps `BigDecimal` to `NUMERIC(19,2)`; H2 rounds half-up on
  write, invisibly to all Java layers. Decide: are sub-cent prices invalid
  input (reject with `@Digits` scale enforcement → 400) or real data (widen
  with `@Column(precision, scale)`)? Owner: DTO or Entity. Status: undecided.
- [ ] **Duplicate `productName` accepted.** Needs a DB unique constraint
  (`@Column(unique = true)`) as the real enforcement — a service-level
  check-then-insert races — plus an optional service check for a clean 409
  message. Suggested first item to implement: exercises every layer at once
  (entity, DTO, service, advice, schema). Owner: DB + Service. Status: planned.

## Candidate rules by area

### Identity & naming
- [ ] Blank/whitespace-only names — covered by `@NotBlank`; open: trim `"  x  "`
  on write vs reject? Owner: DTO/Service. Status: undecided.
- [ ] Name length cap — `@Size(max = 255)` to match default `VARCHAR(255)`;
  longer currently fails at the DB. Owner: DTO. Status: planned.
- [ ] Case-insensitive duplicates ("Widget" vs "widget") — unique constraints
  are case-sensitive by default. Owner: DB. Status: undecided.
- [ ] Reserved names / profanity — named to dismiss. Status: rejected.

### Price
- [ ] Zero price — currently `@Positive` bans it (no giveaways). Confirm
  intended vs `@PositiveOrZero` for free products. Owner: DTO. Status: needs
  confirmation.
- [ ] Absurd magnitude — a documented ceiling (not silent). Owner: DTO.
  Status: deferred.
- [ ] Multi-currency — `BigDecimal price` assumes one currency. Known
  limitation; needs amount+currency if a second currency ever appears.
  Status: rejected for now, noted.

### Quantity / stock
- [x] Negative stock on update — enforced by entity invariant.
- [x] Oversell on decrement — `InsufficientQuantityException` → 409; no
  endpoint calls `removeQuantity` yet, quantity PATCH endpoints close the loop.
- [ ] Zero-crossing as domain event (notifications, low-stock flags) — number
  vs event decision. Status: deferred.
- [ ] Maximum stock cap (warehouse capacity) — discussed, likely fictional.
  Status: rejected unless a real requirement appears.

### Lifecycle
- [ ] Delete/update policy for referenced products (orders, carts) — nothing
  references products yet, so deletes are safe. Revisit when a FK appears:
  block, cascade, or soft-delete. Status: deferred.
- [ ] Soft delete (`active`/`deletedAt` + filtered reads) — needs an
  auditability requirement first. Status: rejected until needed.
- [ ] Update with identical values — currently silent no-op success. Fine.
  Status: accepted as-is.
- [ ] PUT-vs-PATCH semantics — `updateFrom` overwrites everything; a body
  omitting a field fails validation rather than skipping it. Decide before
  quantity endpoints land. Owner: Controller/Service. Status: undecided.

### Abuse & robustness
- [ ] Duplicate POST retries creating twins — unique-name constraint
  accidentally solves the common case; full idempotency keys deferred.
  Status: partial (via unique name), keys deferred.
- [ ] Unknown JSON fields — currently ignored silently (closed in practice by
  `@NotNull` on missing fields). Consider `FAIL_ON_UNKNOWN_PROPERTIES` if
  strictness wanted. Status: accepted as-is.
- [ ] Absurd quantities (`Integer.MAX_VALUE` stock) — harmless, funny in the
  response. Status: accepted as-is.

## Suggested implementation order
1. Unique-name constraint (DB + service 409) — touches every layer.
2. Name length cap (`@Size`).
3. Price scale decision (reject vs widen).
4. PUT-vs-PATCH semantics, then quantity endpoints.
