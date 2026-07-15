# Antigravity Scrape Brief — VERIFY: timepiece register-interest form + share button (TASK-PDP-005)

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why:** the Phase-1 chunk scrapes captured the "Register Interest" CTA and the
share button's existence, but NOT the register form's internal fields or the
share button's real selector. Those were mapped best-effort from screenshots and
are tagged `ROLLOUT-GAP · VERIFY` in `PdpRegisterInterestModalComponent` and
`SharedTimepiecePdp`. Confirm the real selectors so we can replace the guesses.

## Targets

| # | URL | Why |
| --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/products/monarch-rose-gold | active family — enabled "Register Interest" opens the form |
| 2 | https://www.timepieces-d2c.example/products/manifesta-mother-of-pearl | second active family — confirm the form is identical |
| 3 | https://www.timepieces-d2c.example/products/astonia-stealth-silver | SOLD-OUT pilot — confirm its CTA/share/floating state (differs) |

## A. Register-interest form (targets 1 & 2 — open it and map every field)

Click the sticky "Register Interest" CTA to open the **"Register your interest"**
modal, then capture (locator ladder: `data-testid` → `id` → aria/name →
placeholder/text → structural — record the tier + whether a `data-testid` exists):

1. Modal container + heading ("Register your interest").
2. **Full name** input — selector + placeholder.
3. **Email** input — selector + placeholder.
4. **Phone**: the country-code toggle (shows "IN +91") selector, AND the phone
   number input selector + placeholder.
5. **Consent checkbox** ("I agree to receive communication…") — the `<input>`
   selector (and its label).
6. **Submit** button ("Register interest") — selector. (Do NOT actually submit.)
7. **Close** control — selector (is it `button[aria-label='Close']`?).
8. Any inline validation/required markers.

Confirm the form is byte-for-byte the same on targets 1 and 2.

## B. Share button (all 3 targets)

1. The share trigger on the PDP (share icon near the wishlist / allocation badges)
   — exact selector. Is it `button[aria-label='Share']`, a `data-testid`, or
   something else? Record it.
2. On click, what happens — native OS share sheet (`navigator.share`), an in-page
   share modal, or a copy-link popover? If it's an in-page modal, capture its
   container + option selectors; if native, just note "native share sheet".

## C. Floating "Register Interest" bar (all 3 targets)

Scroll down until the floating bottom bar appears; capture its container + button
selector, and confirm whether it also opens the register-interest form.

## D. Astonia sold-out state (target 3)

Astonia products are fully allocated. Confirm:
1. The primary CTA text + state (expected disabled "Interest submitted") + selector.
2. Is there STILL a **share** button on Astonia? A **floating** bar? A **Book
   Consultation** link? (Astonia's Phase-0 scrape predates these checks.)
3. Does clicking the disabled CTA do anything (open a form or nothing)?

## Output

Return one Markdown file `verify-register-share.md` with sections A–D, each
listing the confirmed selectors (with tier + data-testid yes/no). I'll then
replace the `ROLLOUT-GAP · VERIFY` guesses in `PdpRegisterInterestModalComponent`
and `SharedTimepiecePdp` with the real selectors, and note Astonia's share/
floating/consultation state.
