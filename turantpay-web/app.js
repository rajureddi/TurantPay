/* ==========================================================================
   TurantPay Web — Application Logic & USSD Phone Dialer Integration
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  // PWA Service Worker Registration with Cache Busting v9.0
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js?v=9.0')
      .then(reg => {
        console.log('[TurantPay PWA] Service Worker v9.0 Registered', reg);
        reg.update();
      })
      .catch(err => console.error('[TurantPay PWA] SW Registration Failed', err));
  }

  // State Management
  let deferredPrompt = null;
  let html5QrcodeScanner = null;
  let scannedVpa = '';

  // DOM Elements
  const greetingText = document.getElementById('greetingText');
  const pwaBanner = document.getElementById('pwaBanner');
  const btnInstall = document.getElementById('btnInstall');
  const btnInstallSettings = document.getElementById('btnInstallSettings');
  const btnConfirmPopupInstall = document.getElementById('btnConfirmPopupInstall');
  const btnDismissInstallPopup = document.getElementById('btnDismissInstallPopup');
  
  // Modals & Bottom Sheets
  const mobileModal = document.getElementById('mobileModal');
  const qrModal = document.getElementById('qrModal');
  const qrResultModal = document.getElementById('qrResultModal');
  const settingsModal = document.getElementById('settingsModal');
  const installPromptModal = document.getElementById('installPromptModal');
  const toast = document.getElementById('toast');

  // Input Fields
  const inputMobile = document.getElementById('inputMobile');
  const inputAmount = document.getElementById('inputAmount');
  const scannedVpaText = document.getElementById('scannedVpaText');

  // Action Buttons & Nav items
  const btnSendMobile = document.getElementById('btnSendMobile');
  const btnCheckBalance = document.getElementById('btnCheckBalance');
  const btnStartScan = document.getElementById('btnStartScan');
  const fabScan = document.getElementById('fabScan');
  const btnConfirmMobilePay = document.getElementById('btnConfirmMobilePay');
  const btnDialVpaUssd = document.getElementById('btnDialVpaUssd');
  const btnRecopyUpi = document.getElementById('btnRecopyUpi');
  const btnNavHome = document.getElementById('btnNavHome');
  const btnNavSettings = document.getElementById('btnNavSettings');

  // Check standalone mode
  const isStandalone = window.matchMedia('(display-mode: standalone)').matches || window.navigator.standalone === true;

  // 1. Time-based Greeting
  function setGreeting() {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) {
      greetingText.innerText = "GOOD MORNING,";
    } else if (hour >= 12 && hour < 17) {
      greetingText.innerText = "GOOD AFTERNOON,";
    } else if (hour >= 17 && hour < 21) {
      greetingText.innerText = "GOOD EVENING,";
    } else {
      greetingText.innerText = "GOOD NIGHT,";
    }
  }
  setGreeting();

  // 2. PWA Installation Event Listener
  window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredPrompt = e;
    if (pwaBanner) pwaBanner.style.display = 'flex';
  });

  // ALWAYS Show Install Popup Modal on Initial Load (if not standalone)
  if (!isStandalone && installPromptModal) {
    setTimeout(() => {
      openModal(installPromptModal);
    }, 600);
  }

  // Trigger PWA installation prompt or step-by-step fallback
  function triggerPwaInstall() {
    if (deferredPrompt) {
      deferredPrompt.prompt();
      deferredPrompt.userChoice.then((choiceResult) => {
        if (choiceResult.outcome === 'accepted') {
          console.log('User accepted PWA installation');
          showToast("TurantPay Web App Installed!");
          if (pwaBanner) pwaBanner.style.display = 'none';
          closeModal(installPromptModal);
        }
        deferredPrompt = null;
      });
    } else {
      // Fallback instructions for iOS Safari / Chrome preview
      showToast("To Install: Tap browser Share/Menu → select 'Add to Home Screen'");
      setTimeout(() => {
        alert("📲 To Install TurantPay Web App:\n\n1. Tap your browser menu or Share button (iOS/Android)\n2. Select 'Add to Home Screen'\n3. TurantPay will install as an offline app!");
      }, 300);
      closeModal(installPromptModal);
    }
  }

  if (btnInstall) btnInstall.addEventListener('click', triggerPwaInstall);
  if (btnInstallSettings) btnInstallSettings.addEventListener('click', triggerPwaInstall);
  if (btnConfirmPopupInstall) btnConfirmPopupInstall.addEventListener('click', triggerPwaInstall);

  if (btnDismissInstallPopup) {
    btnDismissInstallPopup.addEventListener('click', () => {
      closeModal(installPromptModal);
    });
  }

  // 3. Helper: Toast Notifications
  function showToast(message) {
    if (!toast) return;
    toast.querySelector('.toast-text').innerText = message;
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 3500);
  }

  // 4. USSD Dialer Helper (Opens Native Phone Dialer on iOS & Android)
  function dialUssd(code) {
    window.location.href = `tel:${code}`;
  }

  // 5. Modal Controllers
  function openModal(modal) {
    if (modal) modal.classList.add('active');
  }

  function closeModal(modal) {
    if (modal) modal.classList.remove('active');
    if (modal === qrModal && html5QrcodeScanner) {
      stopScanner();
    }
    if (modal === settingsModal) {
      if (btnNavSettings) btnNavSettings.classList.remove('active');
      if (btnNavHome) btnNavHome.classList.add('active');
    }
  }

  document.querySelectorAll('.btn-close, .modal-overlay').forEach(el => {
    el.addEventListener('click', (e) => {
      if (e.target === el || el.classList.contains('btn-close')) {
        const modal = el.closest('.modal-overlay');
        closeModal(modal);
      }
    });
  });

  // Settings & About Navigation
  if (btnNavSettings) {
    btnNavSettings.addEventListener('click', () => {
      btnNavHome.classList.remove('active');
      btnNavSettings.classList.add('active');
      openModal(settingsModal);
    });
  }

  if (btnNavHome) {
    btnNavHome.addEventListener('click', () => {
      btnNavSettings.classList.remove('active');
      btnNavHome.classList.add('active');
      closeModal(settingsModal);
    });
  }

  // Feature 1: Send Money to Mobile Flow (*99*1*1*mobile*amount*1#)
  if (btnSendMobile) {
    btnSendMobile.addEventListener('click', () => openModal(mobileModal));
  }

  if (btnConfirmMobilePay) {
    btnConfirmMobilePay.addEventListener('click', () => {
      const phone = inputMobile.value.trim();
      const amount = inputAmount.value.trim();

      if (!phone || phone.length < 10) {
        showToast("Enter a valid 10-digit mobile number");
        return;
      }
      if (!amount || parseFloat(amount) <= 0) {
        showToast("Enter a valid payment amount");
        return;
      }

      const ussdCode = `*99*1*1*${phone}*${amount}*1#`;
      closeModal(mobileModal);
      showToast(`Opening Dialer with USSD: ${ussdCode}`);
      setTimeout(() => dialUssd(ussdCode), 500);
    });
  }

  // Feature 2: Check Balance Flow (*99*3#)
  if (btnCheckBalance) {
    btnCheckBalance.addEventListener('click', () => {
      showToast("Opening Phone Dialer for Balance Check (*99*3#)");
      setTimeout(() => dialUssd("*99*3#"), 400);
    });
  }

  // Feature 3: Scan & Pay QR Flow (*99*1*3# + auto copy VPA)
  function startScanner() {
    openModal(qrModal);
    if (!html5QrcodeScanner) {
      html5QrcodeScanner = new Html5Qrcode("reader");
    }

    const config = { fps: 10, qrbox: { width: 220, height: 220 } };
    
    html5QrcodeScanner.start(
      { facingMode: "environment" },
      config,
      (decodedText) => {
        stopScanner();
        closeModal(qrModal);
        handleQrResult(decodedText);
      },
      (errorMessage) => {}
    ).catch(err => {
      console.error("Camera access error:", err);
      showToast("Camera access error.");
    });
  }

  function stopScanner() {
    if (html5QrcodeScanner) {
      html5QrcodeScanner.stop().then(() => {
        console.log("Scanner stopped");
      }).catch(err => console.log("Stop scanner err:", err));
    }
  }

  function handleQrResult(qrData) {
    if (qrData.includes("pa=")) {
      scannedVpa = qrData.substring(qrData.indexOf("pa=") + 3).split("&")[0];
    } else {
      scannedVpa = qrData;
    }

    copyToClipboard(scannedVpa);
    
    if (scannedVpaText) scannedVpaText.innerText = scannedVpa;
    openModal(qrResultModal);
    showToast("UPI ID Copied to Clipboard!");
  }

  function copyToClipboard(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text);
    } else {
      const textInput = document.createElement("input");
      textInput.value = text;
      document.body.appendChild(textInput);
      textInput.select();
      document.execCommand("copy");
      document.body.removeChild(textInput);
    }
  }

  if (btnStartScan) btnStartScan.addEventListener('click', startScanner);
  if (fabScan) fabScan.addEventListener('click', startScanner);

  if (btnRecopyUpi) {
    btnRecopyUpi.addEventListener('click', () => {
      copyToClipboard(scannedVpa);
      showToast("UPI ID re-copied to clipboard!");
    });
  }

  if (btnDialVpaUssd) {
    btnDialVpaUssd.addEventListener('click', () => {
      closeModal(qrResultModal);
      showToast("Opening Dialer with USSD: *99*1*3#");
      setTimeout(() => dialUssd("*99*1*3#"), 400);
    });
  }
});
