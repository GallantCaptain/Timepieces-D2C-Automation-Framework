# Test Strategy: Login and Account States

This document details the functional differences and test automation strategies for handling authentication and user profile states in the Timepieces automation framework.

---

## 1. Functional Differences & Lifecycle

| Aspect | `/login` (Authentication Entry) | `/account/profile` (Profile Completion/Edit) |
| :--- | :--- | :--- |
| **Access Control** | **Public** (Anyone can visit). | **Protected** (Requires an active session). |
| **User State Before**| Guest (No session cookies/tokens). | Authenticated (Active session). |
| **First-Time Flow** | Acts as the **creator**. User enters phone/email, verifies OTP, and the system initializes a bare-bones account. | Acts as the **completer**. If it's a new account, the user is redirected here to fill in missing fields (Name, Email, Gender) to make the account active. |
| **Subsequent Visits** | Just enters password/OTP to establish a session, then redirects to the storefront. | Used to update/edit existing details (addresses, profile fields). |

---

## 2. Test Automation Strategies

Because the entry requirements for these two pages are completely different, our automation strategies must adapt:

### A. Testing the `/login` page
*   **Testing Focus**: Authentication loops, correct/incorrect OTPs, field validations (email regex, password requirements), and redirects upon success.
*   **Test Pre-requisite**: **Start with a clean slate**. You must ensure the driver has cleared cookies/localStorage so the user is truly a guest.
*   **Key Test Case**: 
    1. Navigate to `/login`.
    2. Submit valid credentials.
    3. Assert redirection to the storefront or the profile page (if incomplete).

### B. Testing the `/account/profile` page
*   **Testing Focus**: Form editing, field validation, successful saving, state persistence (does the name stay updated after refresh?), and **authentication guards** (redirecting unauthenticated users).
*   **Test Pre-requisite**: **Avoid logging in through the UI!** 
    *   *SDET Best Practice:* Do not log in through the UI before every profile test. It makes tests slow and flaky. Instead, inject the session token/cookies directly via Webdriver API or run a fast API request to log in in a `@Before` hook, then navigate directly to `/account/profile`.
*   **Handling the "First-Time Incomplete" State**:
    *   To test the first-time profile setup, your test data generator (or API script) needs to register a brand-new user *without* completing their profile details. 
    *   When the automation logs in as this new user, verify they are forced onto the `/account/profile` page and cannot navigate to checkout until they fill in their mandatory profile details.

---

## 3. Critical Test Cases to Automate (The Pareto 80/20)

When writing feature files for these flows, these are the business-critical scenarios to cover:

### Guard Tests (Security & Usability)
1.  **Guest Access to Profile**: Navigate directly to `/account/profile` as a guest. Verify that the system redirects you to `/login` (optionally retaining the return URL, e.g., `/login?return_to=/account/profile`).
2.  **Auth Bypass**: Log in, navigate to `/account/profile`, log out, and press the back button. Verify the page does not load cached profile data.

### Registration vs. Login Flow Tests
1.  **First-Time Login Completion**:
    *   Given a newly created user logs in for the first time.
    *   Then they are directed to the profile completion page.
    *   When they complete the mandatory profile fields (First name, Last name, Email).
    *   Then they are allowed to enter the main storefront.
2.  **Profile Update (Editing)**:
    *   Given an existing user is logged in.
    *   When they navigate to `/account/profile` and change their phone number or name.
    *   Then the changes are saved successfully, and a success banner is displayed.
    *   And when the page is refreshed, the updated details persist.
