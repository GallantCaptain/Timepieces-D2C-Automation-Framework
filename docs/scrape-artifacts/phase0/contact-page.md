# Contact Page Scrape: contact-page

URL used: https://www.timepieces-d2c.example/contact
Page title: Contact Us | Timepieces Watches

## Core elements
- First Name input: `input[data-testid="contact-first-name"]` (text placeholder: "eg. John")
- Last Name input: `input[data-testid="contact-last-name"]` (text placeholder: "eg. Deo")
- Country selector button: `button[data-testid="contact-country-toggle"]` (default value observed: "🇮🇳 +91")
- Phone input: `input[data-testid="contact-phone"]` (text placeholder: "Enter 10 digit number")
- Email input: `input[data-testid="contact-email"]` (text placeholder: "Enter your email")
- Subject input: `input[data-testid="contact-subject"]` (text placeholder: "write the subject")
- Message textarea: `textarea[data-testid="contact-message"]` (text placeholder: "write here...")
- Submit button: `button[data-testid="contact-submit"]` (text: "Send Enquiry")

## AR / other
- AR present: no
- Anything else notable found:
  - Toll Free link: `a[href="tel:18000000000"]`
  - WhatsApp chat link: `a[href="https://wa.me/910000000000"]`
  - Support email link: `a[href="mailto:support@timepieces-d2c.example"]`

## Raw data-testid inventory
- input[data-testid="contact-first-name"] — First name input field
- input[data-testid="contact-last-name"] — Last name input field
- button[data-testid="contact-country-toggle"] — Phone number country code selector toggle button
- input[data-testid="contact-phone"] — Phone number input field
- input[data-testid="contact-email"] — Email address input field
- input[data-testid="contact-subject"] — Subject input field
- textarea[data-testid="contact-message"] — Message/Additional Info textarea input field
- button[data-testid="contact-submit"] — Send enquiry submit button

## Elements with NO data-testid (fallback locators used)
- Toll Free link — no data-testid; used `a[href="tel:18000000000"]` (aria-attribute tier)
- WhatsApp chat link — no data-testid; used `a[href="https://wa.me/910000000000"]` (aria-attribute tier)
- Support email link — no data-testid; used `a[href="mailto:support@timepieces-d2c.example"]` (aria-attribute tier)
