# Antigravity Scrape Brief — Phase 3, Chunk 3D: Friends & Partners

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** map the public Friends & Partners landing page AND one representative
partner profile page (so we can decide whether the profile warrants its own POM
class). Both are PUBLIC — no login required. Locator ladder:
`data-testid` → `id` → aria/role → text-anchor → semantic class → structural;
record the tier + whether a `data-testid` exists; report only the live DOM.

| # | URL | Class | Chrome |
| --- | --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/friends-and-partners | `FriendsAndPartners` | header + footer |
| 2 | a representative profile, e.g. https://www.timepieces-d2c.example/friends-and-partners/nikhil-kamath | (new) `PartnerProfilePage`? | header + footer |

## 1. FriendsAndPartners landing (`/friends-and-partners`)
A directory of eminent personalities, divided into **3 sections**: "Founders &
Makers", "Thought Leaders", "Voices". Each profile is a card; **some cards carry
a video icon/badge**. Clicking a card → that personality's profile page
(`/friends-and-partners/<slug>`). Capture:
- Page `<title>` (confirm exact — scaffold guesses "Timepieces | Friends & Partners").
- Main heading / intro.
- **The 3 section containers**: section wrapper selector + each section heading
  ("Founders & Makers", "Thought Leaders", "Voices") — how a section is delimited.
- **Profile card selector**: how name, role/title, and image are structured inside
  a card; the card's link (`a[href*='/friends-and-partners/']`).
- **Video-icon badge**: the selector for the icon that marks cards which have a
  video (so we can assert "this card has a video"); confirm it's only on some.
- **Any category/section filter** (tabs or buttons to jump between the 3
  sections)? Container + button selectors + labels, if present.
- Roughly how many profile cards total.

## 2. A representative partner profile (`/friends-and-partners/<slug>`)
A **highlighted/standout** profile page (deliberately styled to differ from a
regular customer profile). Pick one WITH a video (e.g. `nikhil-kamath` or
`bhuvan-bam`) so the video path is captured; note the slug you used. Capture:
- Page `<title>` pattern (does it include the person's name?).
- The profile hero: name selector, role/bio selector, portrait image.
- **"Play Video" button**: selector + exact label; what it opens (inline player /
  modal / YouTube embed) + the player/modal selector. (Not all profiles have
  one — note that this one does.)
- **"Numbered watches bought" grid**: the container + each watch card selector;
  confirm each card links to a product PDP (`a[href*='/products/']`); how the
  watch's number/name is shown.
- Back navigation to the Friends & Partners page (selector + label).

## Raw inventory (per page)
- Full `data-testid` inventory.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 2 Markdown files: `friends-and-partners.md` and
`friends-and-partners-profile.md`, each with the captures above + the raw
inventory. Drop them back and I'll implement `FriendsAndPartners` (and add a
`PartnerProfilePage` class if the profile shape warrants it), then move to 3B
(Orders + Wishlist), which needs the **authenticated** Antigravity session.
