# Product Requirements Document: Timepieces Identity & Authentication

## 1. Overview
This document outlines the product requirements for the Timepieces web application, specifically focusing on the user authentication flow and the surrounding automation testing framework used to ensure its reliability. 

## 2. Product Features: Identity & Authentication

### 2.1 User Login via Phone Number (OTP)
The core authentication mechanism for the Timepieces application is a passwordless, phone-number-based login system secured via One-Time Passwords (OTP).

#### User Flow
1. **Landing**: The user navigates to the Timepieces login page and is presented with a "Welcome" message.
2. **Region Selection**: The user selects their country from a region dropdown to automatically configure the correct country code.
3. **Phone Number Entry**: The user inputs their mobile phone number.
4. **Submission**: The user clicks the "Send OTP" button.
5. **Security Check**: The application validates the request using a CAPTCHA (e.g., Cloudflare Turnstile). 
6. **OTP Verification**: Upon successful validation, the user is redirected to the OTP verification screen where they enter the code received on their mobile device.

#### Acceptance Criteria
* The Welcome message must be clearly visible upon page load.
* The region dropdown must accurately filter and apply the correct country dialing codes.
* The "Send OTP" button must trigger the CAPTCHA validation flow (unless bypassed in staging environments).
* Successful OTP request must immediately route the user to the OTP entry screen.

## 3. Product Features: Homepage

### 3.1 Layout & Structure
The homepage serves as the primary gateway for users to explore Timepieces timepieces, brand stories, and collections. The layout is vertically structured with distinct modular sections.

#### 3.1.1 Header
* **Hamburger Menu (Top-Left)**: Triggers a side-drawer navigation menu containing main links: Timepieces, Accessories (New), Friends & Partners, Timepieces House, The Wall, Updates, and Timepieces World. It also includes utility links: Sign in (button), Contact, Wishlist, Track Orders, and a Region/Language selector (e.g., IN EN).
* **Logo (Top-Center)**: The primary Timepieces brand logo, centered in the header.
* **Icon Array (Top-Right)**: Contains actionable icons including Global Search (planned for future addition), Stories, Profile, Wishlist, and Cart/Bag.

#### 3.1.2 Main Content Sections (Top to Bottom)
1. **Hero Section**: A large, center-dominated video background highlighting brand aesthetics, overlayed with an "Early Access" signup call-to-action (CTA).
2. **Upcoming Drops**: A section teasing unreleased or soon-to-be-available timepieces (e.g. "Two releases, limited pieces").
3. **Collections**: Showcases available watch collections (e.g. The Collection - Northa, N-Tice, Aviator).
4. **Quotes**: A visual section displaying thematic quotes alongside lifestyle imagery.
5. **Friends and Partners**: Highlights brand ambassadors, profiles, and partnerships.
6. **Rotating Watch Component**: An interactive, scroll-driven component featuring static images of a watch ("Crafted with intent") that rotate or animate as the user scrolls down the page.
7. **Timepieces Advantage**: A group of informational cards detailing the benefits, unique selling propositions (USPs), or technical features of the timepieces.
8. **Emotions in Motion**: A media integration block displaying a grid of social feeds (like Instagram) and lifestyle content.
9. **Featured In**: A media recognition section highlighting press and publications at the bottom.

#### 3.1.3 Footer
* **Global Footer**: Located at the absolute bottom of the page, containing categorized navigation links to all active and utility pages across the site.

## 4. Automation Framework Architecture

To maintain the quality and reliability of the Timepieces application, a robust, BDD-driven test automation framework is utilized. The framework is designed for high maintainability, parallel execution, and comprehensive reporting.

### 4.1 Technology Stack
* **Language**: Java (Targeting Java 25)
* **Build Tool**: Maven
* **Core Automation**: Selenium WebDriver (Web), Appium (Mobile), Rest-Assured (API)
* **Self-Healing**: Healenium (automatically fixes broken web locators during execution)
* **BDD Engine**: Cucumber-JVM
* **Test Runner**: TestNG (configured for parallel method execution with ThreadLocal driver management)
* **Reporting**: Allure Reports
* **Logging**: Log4j2
* **Screen Recording**: Monte Screen Recorder

### 4.2 Design Patterns & Principles
* **Behavior-Driven Development (BDD)**: Test scenarios are written in Gherkin syntax (Given/When/Then) to ensure tests serve as living documentation.
* **Page Object Model (POM)**: Web pages are represented as classes, abstracting the underlying HTML locators and interactions.
* **Fluent Interfaces**: Page objects utilize fluent patterns (e.g., `loginPage.open().configureCountryCodeByName(...)`) to make code more readable.
* **Separation of Concerns**: Assertions are strictly isolated to Step Definitions; Page Objects only perform interactions and state queries.
* **Parallel Execution Ready**: Thread-safe driver management via `DriverFactory` and `Hooks.java` to support concurrent test runs.

## 5. Future Additions
*All new features and subsequent updates to this PRD will be handled manually going forward.*
