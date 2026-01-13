---

### **TurantPay: Smart UI for Offline UPI Payments**

**TurantPay** is a modern Android utility designed to bridge the gap between digital payments and offline accessibility. It provides a professional Graphical User Interface (GUI) over the official ***99# USSD** framework provided by NPCI, making offline banking as easy as a smartphone app.

---

### **🚀 Features**

* **Offline-First:** No 4G/5G or Wi-Fi required. Works on the GSM signaling channel.
* **Smart QR Scanner:** Scan any standard UPI QR code to automatically generate the correct USSD dialing string.
* **One-Click UI:** Simplified buttons for:
* Sending money via Mobile Number or VPA.
* Checking Bank Balance instantly.
* Viewing Mini-Statements.
* **Privacy Focused:** Zero server-side storage of sensitive data (PINs or Account details).
* Pins and Details are entered in ussd response apps wont have any android level permission to read that safely you can see.


### **🛠️ Technology Stack**

* **Language:** Kotlin / Java
* **Framework:** Android SDK
* **Integration:** NPCI *99# (USSD) Protocol
* **Tools:** CameraX (for QR Scanning), Material Design 3
### **DEMO


https://github.com/user-attachments/assets/b0dc4816-ee0a-473c-b60c-84e898862e6c


### **📲 How It Works**

TurantPay automates the manual entry usually required for *99#. Instead of typing `*99*1*1#`, the app uses Android's `Intent.ACTION_CALL` to dial the precisely formatted string based on your UI interaction or QR scan.

### **🛡️ Security & Privacy**

* **No PIN Storage:** The app never asks for or stores your UPI PIN. PIN entry happens on the official secure system dialer.
* **Data Localism:** All transaction logs are stored locally on your device.
* **Official Gateway:** TurantPay acts as a bridge to the official NPCI gateway; we do not process the money ourselves.


### **📄 License**

This project is licensed under the MIT License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.

---
