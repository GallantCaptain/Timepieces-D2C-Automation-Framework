# Antigravity Scrape Brief — Phase 6, Chunk 6B: Logged-in sidebar + header state

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

> **PRECONDITION — AUTHENTICATED session** (already active). This is the LAST
> unmapped remnant: DEBT-008's second half — the logged-in user container's
> child elements in the sidebar. **Redact personal values** (name, phone,
> email, photo URL) — selectors/structure only.

Locator ladder: `data-testid` → `id` → aria/role → text-anchor → semantic
class → structural; record the tier + whether a `data-testid` exists.

## 1. The logged-in sidebar user container
Open the sidebar (hamburger) while AUTHENTICATED and capture:
- The **user container** that replaces the guest "Sign In" button: its own
  selector + every CHILD element — avatar/photo, display name, any
  designation/label, chevron/affordance — selector + tier each.
- What clicking the container (or its parts) does (→ `/account`?).
- The **logged-in-only entries** (e.g. "Orders") vs the guest-only entries
  (e.g. "Track Order", "Sign In") — the full authenticated entry list (label +
  href/action), so we can assert the guest↔auth menu diff.
- Any wishlist/notification counters visible in the sidebar or header when
  authenticated.

## 2. Authenticated header state
- The header `header-account-toggle` behavior when LOGGED IN (icon change?
  direct link to `/account`? opens a menu?).
- Anything else in the header that differs from the guest state.

## Raw inventory
- Full `data-testid` inventory of the sidebar (authenticated state).
- NO-`data-testid` children + fallback selectors (with tier) for `ROLLOUT-GAP`s.

## Output format
Return 1 Markdown file: `auth-sidebar.md`. Drop it back and I'll finish
`SidebarMenuComponent`'s logged-in children (+ header state), closing DEBT-008
completely. While the authenticated session is live, it's also the right moment
to EXECUTE the @AuthGated orders scenarios locally
(`Account_Orders_Management.feature`) — the glue is fully wired.
