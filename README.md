<p align="left"> <img src="https://komarev.com/ghpvc/?username=rajureddi&label=Repo%20views&color=0e75b6&style=flat" alt="rajureddi" /> </p>

<h1 align="center">UPI payments. Without the internet.</h1>

<p align="center">
  Send money. Check your balance. Scan QR codes.<br>
  All over plain <code>*99#</code> USSD on your SIM. No data, no Wi-Fi, no account.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/platform-Android-2ecc71?style=flat-square" alt="platform Android" />
  <img src="https://img.shields.io/badge/min%20SDK-26-007acc?style=flat-square" alt="min SDK 26" />
  <img src="https://img.shields.io/badge/Kotlin-100%25-7f52ff?style=flat-square" alt="Kotlin 100%" />
  <img src="https://img.shields.io/badge/license-MIT-4c1?style=flat-square" alt="license MIT" />
  <img src="https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square" alt="PRs welcome" />
</p>

<p align="center">
  <a href="https://github.com/rajureddi/TurantPay">Website</a> · <a href="https://turantpay-web.vercel.app/">PWA</a> · <a href="https://github.com/rajureddi/TurantPay/releases/download/v1.1.0/TurantPay.apk">Download APK</a>
</p>

---

## **TurantPay: App for Offline UPI Payments**

**TurantPay** is a modern Android utility designed to bridge the gap between digital payments and offline accessibility. It provides a professional Graphical User Interface (GUI) over the official *99# USSD framework provided by NPCI, making offline banking as easy as a smartphone app.

* **PWA Web App:** [https://turantpay-web.vercel.app/](https://turantpay-web.vercel.app/)
* **Download APK:** [TurantPay.apk](https://github.com/rajureddi/TurantPay/releases/download/v1.1.0/TurantPay.apk)

---

### **🚀 Features**

* **Offline-First:** No 4G/5G or Wi-Fi required. Works on the GSM signaling channel.
* **Smart QR Scanner:** Scan any standard UPI QR code to automatically generate the correct USSD dialing string.
* **One-Click UI:** Simplified buttons for:
  * Sending money via Mobile Number or VPA.
  * Checking Bank Balance instantly.
  * Viewing Mini-Statements.
* **Privacy Focused:** Zero server-side storage of sensitive data (PINs or Account details).
* **Secure USSD:** PINs and Details are entered directly in the secure system dialer. Apps won't have any permission to read USSD responses. You can safely download and use.

---

### **🛠️ Technology Stack**

* **Language:** Kotlin / Java
* **Framework:** Android SDK
* **Integration:** NPCI *99# (USSD) Protocol
* **Tools:** CameraX (for QR Scanning), Material Design 3

---

### **DEMO**

https://github.com/user-attachments/assets/b0dc4816-ee0a-473c-b60c-84e898862e6c

---

### **📲 How It Works**

TurantPay automates the manual entry usually required for *99#. Instead of typing `*99*1*1#`, the app uses Android's `Intent.ACTION_CALL` to dial the precisely formatted string based on your UI interaction or QR scan.

---

### **🛡️ Security & Privacy**

* **No PIN Storage:** The app never asks for or stores your UPI PIN. PIN entry happens on the official secure system dialer.
* **Data Localism:** All transaction logs are stored locally on your device.
* **Official Gateway:** TurantPay acts as a bridge to the official NPCI gateway; we do not process the money ourselves.

---

### **📄 License**

This project is licensed under the MIT License - see the [LICENSE](https://github.com/rajureddi/TurantPay/blob/master/License.txt) file for details.

---

Contact for feedback or issues: **rajubandam694@gmail.com**
