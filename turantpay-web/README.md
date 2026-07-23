# ⚡ TurantPay Web — Offline UPI Payments (*99#)

> **Offline-first UPI payment Progressive Web App (PWA) powered by NPCI's `*99#` USSD technology. Designed for iOS (iPhone) and Android smartphones to execute digital payments without an internet connection.**

![TurantPay Banner](https://img.shields.io/badge/TurantPay-Offline%20UPI-FF6D00?style=for-the-badge&logo=android)
![PWA](https://img.shields.io/badge/PWA-Installable-1565C0?style=for-the-badge&logo=pwa)
![Status](https://img.shields.io/badge/Status-Live%20Ready-00E676?style=for-the-badge)

---

## 🌟 Overview

**TurantPay Web** brings offline UPI payments to both **iOS (Safari)** and **Android (Chrome)** users through a Progressive Web App interface. 

In India, millions of users experience internet connectivity issues or network blackouts in remote areas, basements, or during congestion. TurantPay bridges smart devices with NPCI’s National Unified USSD Platform (NUUP) over `*99#`, allowing instant offline banking over standard GSM cellular signals without needing 2G/3G/4G/WiFi data.

---

## ✨ Features

### 📱 1. Send Money to Mobile (`*99*1*1#`)
- Enter any 10-digit mobile number + payment amount (₹).
- Tapping **"Open Dialer"** automatically launches your phone's native dialer pre-filled with:
  ```text
  *99*1*1*<mobile_number>*<amount>*1#
  ```
- Simply press the green **Call** button to execute the transaction!

### 📷 2. Scan & Pay QR (`*99*1*3#`)
- Built-in camera QR scanner to scan any UPI merchant QR code.
- **Auto-copies VPA:** Extracted UPI ID (`pa=...`) is automatically copied to your device's clipboard immediately upon scan.
- **Manual Re-copy button:** Quickly re-copy the UPI ID if needed.
- **Instruction Note:** Gives clear step-by-step guidance:
  > *"After dialing \*99\*1\*3#, paste your copied UPI ID in the next screen on your dialer."*
- Launches Phone Dialer pre-filled with `*99*1*3#`.

### 💳 3. Check Bank Balance (`*99*3#`)
- One-tap quick action tile.
- Immediately launches your Phone Dialer pre-filled with:
  ```text
  *99*3#
  ```

### 📲 4. Progressive Web App (PWA) — 100% Offline
- **Auto-Install Popup:** Prompts visitors to install TurantPay on their home screen on first load.
- **Settings Install Button:** Easy one-tap install option inside the Settings sheet.
- **Service Worker Caching (`sw.js`):** Once loaded or installed, the web app functions 100% offline without needing internet.

### 🎨 5. Authentic TurantPay App Palette
- Built with TurantPay's signature **Energetic Orange (`#FF6D00`)** & **Deep Royal Blue (`#1565C0` / `#0B132B`)** design system.
- Smooth glassmorphism cards, bottom sheet dialogs, and responsive mobile frame layout.

### 🍏 6. Cross-Platform iOS & Android Support
- Native `tel:` URI links enable seamless integration with the native Phone Dialer app on **iOS (iPhone)** and **Android** devices.

---

## 📁 Repository Structure

```text
turantpay-web/
├── index.html        # Main PWA application layout & modal bottom sheets
├── styles.css        # TurantPay Orange & Deep Blue CSS design system
├── app.js            # Camera QR scanner, clipboard auto-copy, and USSD dialer handlers
├── sw.js             # Service Worker for 100% offline PWA caching
├── manifest.json     # Web App Manifest for PWA Home Screen installation
└── README.md         # Documentation & Features Guide
```

---

## 🚀 How to Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/rajureddi/TurantPay.git
   cd TurantPay/turantpay-web
   ```

2. Serve using any local web server (e.g. Node `http-server` or `live-server`):
   ```bash
   npx http-server . -p 8085
   ```

3. Open your browser at `http://127.0.0.1:8085`.

---

## 🌐 How to Deploy to GitHub Pages

To host TurantPay Web for free using **GitHub Pages**:

1. Push your `turantpay-web/` files to your GitHub repository ([rajureddi/TurantPay](https://github.com/rajureddi/TurantPay)).
2. Go to repository **Settings** → **Pages**.
3. Under **Build and deployment**:
   - **Source:** Deploy from a branch.
   - **Branch:** Select `master` / `main` and folder `/turantpay-web` (or root `/`).
4. Click **Save**.
5. Your live PWA URL will be generated at:
   `https://rajureddi.github.io/TurantPay/`

---

## 👨‍💻 Developer Information

- **Developer:** Raju Reddi
- **GitHub:** [@rajureddi](https://github.com/rajureddi)
- **Project:** TurantPay — Offline Digital Payments for India (`*99#`)
- **License:** MIT

---

*Built with ❤️ for Bharat — enabling digital payments for everyone, everywhere, with or without internet.*
