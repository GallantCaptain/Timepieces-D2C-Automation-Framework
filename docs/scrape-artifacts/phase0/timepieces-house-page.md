# Experience Center Scrape: timepieces-house-page

URL used: https://www.timepieces-d2c.example/experience-center
Page title: Timepieces Watches | Early Access to Limited Edition Pieces

## Core elements
- Page title header: `h1` (text: "Timepieces Experience centers")

### Experience Center: New Delhi
- Container: `.bg-white.border.rounded-2xl:has(h2:contains("New Delhi"))`
- Title: `h2` (text: "The Timepieces House, New Delhi")
- Address: `p` (text: "Ground Floor, Commercial Complex, Greater Kailash 3, New Delhi, Delhi 110048")
- Phone Link: `a[href="tel:18000000000"]`
- Status: `span` (text: "Reopens in next drop")
- Get Directions button: `button` containing "Get Directions"
- Share Location button: `button` containing "Share Location"

### Experience Center: Gurugram
- Container: `.bg-white.border.rounded-2xl:has(h2:contains("Gurugram"))`
- Title: `h2` (text: "The Timepieces House, Gurugram")
- Address: `p` (text: "Dlf Summit plaza, DLF Phase 5, Sector 54, Gurugram, Haryana 122009")
- Phone Link: `a[href="tel:18000000000"]`
- Status: `span` (text: "Coming Soon")
- Get Directions button: `button` containing "Get Directions"
- Share Location button: `button` containing "Share Location"

## Event details section
- Video container: `video.object-cover` (src: `https://cdn.shopify.com/videos/c/o/v/<video-id>.mp4`)
- Audio Mute/Unmute toggle button: `button[aria-label="Unmute"]` (toggles to `button[aria-label="Mute"]` on click and unmutes video audio)
- Section header: `h2` (text: "Timepieces House")
- Event status: `span` (text: "Reopens in next drop")
- Event address: `p` (text: "Ground Floor, Commercial Complex, Greater Kailash 3, New Delhi, Delhi 110048")
- About Event header: `h3` (text: "About event")
- About Event details: `p` (text starts: "For a limited window, Timepieces opens a private residence in Delhi...")

## AR / other
- AR present: no
- Anything else notable found: N/A

## Raw data-testid inventory
- header[data-testid="site-header"] — site header banner

## Elements with NO data-testid (fallback locators used)
- New Delhi card container — no data-testid; used `.bg-white.border.rounded-2xl:has(h2:contains("New Delhi"))` (structural / text-anchor tier) — flag for data-testid rollout
- Gurugram card container — no data-testid; used `.bg-white.border.rounded-2xl:has(h2:contains("Gurugram"))` (structural / text-anchor tier) — flag for data-testid rollout
- Get Directions buttons — no data-testid; used `//button[contains(text(), 'Get Directions')]` (text-anchor tier) — flag for data-testid rollout
- Share Location buttons — no data-testid; used `//button[contains(text(), 'Share Location')]` (text-anchor tier) — flag for data-testid rollout
- Audio Mute/Unmute toggle button — no data-testid; used `button[aria-label='Unmute']` / `button[aria-label='Mute']` (aria-attribute tier) — flag for data-testid rollout
- Video element — no data-testid; used `video` (structural tier) — flag for data-testid rollout
