# Antigravity Scrape Brief — Phase 7, Chunk 7A: /becomemore onboarding journey

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** map the NEW onboarding journey at `https://www.timepieces-d2c.example/becomemore`
— a guided flow of **7–8 steps**. It will become a NEW page object
(`Onboarding_BecomeMore.java`); the old onboarding page stays as-is until the
old flow is sunset. Locator ladder: `data-testid` → `id` → aria/role →
text-anchor → semantic class → structural; record the tier + whether a
`data-testid` exists; report only the live DOM.

## 0. Access & entry
- Is `/becomemore` reachable LOGGED OUT, or does it redirect to login (i.e. is
  it the post-login onboarding)? Report the gate behavior. If it needs auth,
  run it inside an authenticated session (Cloudflare + OTP as before) and note
  that in the artifact.
- Page `<title>`; the journey's root/container selector.

## 1. The step journey (walk EVERY step, in order)
For EACH of the 7–8 steps, capture:
- The step's **identity**: heading/label + any step number/progress indicator
  (stepper dots/bar — container + active-state encoding).
- The step's **content/inputs**: every field (selector + placeholder/label +
  type), choice chips/cards, uploads, or informational content.
- The **progression controls**: Next/Continue button (selector + label +
  disabled-until-valid behavior), Back control, any Skip.
- **Validation**: what blocks progression (required fields, formats) + where
  error messages render.
- The **URL behavior**: does each step change the URL (path/query/hash) or is
  it a single-page state machine?
- Do NOT SUBMIT anything final that creates/updates a real profile — walk
  forward only as far as safe, and say where you stopped and why.

## 2. Completion state
- What renders at the end of the journey (confirmation screen? redirect to
  `/account`?) — capture what you can see WITHOUT finalizing a real submission.

## Raw inventory
- Full `data-testid` inventory across the steps.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 1 Markdown file: `becomemore.md`, with a per-step section (Step 1..N)
plus the access/completion notes + raw inventory. Drop it back and I'll build
`Onboarding_BecomeMore.java` (+ a step-indicator component if warranted).
