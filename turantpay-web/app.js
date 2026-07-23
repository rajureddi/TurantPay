/* ==========================================================================
   TurantPay Web — Application Logic & USSD Phone Dialer Integration
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  // PWA Service Worker Registration with Cache Busting v14.0
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js?v=14.0')
      .then(reg => {
        console.log('[TurantPay PWA] Service Worker v14.0 Registered', reg);
        reg.update();
      })
      .catch(err => console.error('[TurantPay PWA] SW Registration Failed', err));
  }

  // State Management
  let deferredPrompt = null;
  let html5QrcodeScanner = null;
  let currentFacingMode = "environment";
  let scannedVpa = '';

  // DOM Elements
  const greetingText = document.getElementById('greetingText');
  const pwaBanner = document.getElementById('pwaBanner');
  const btnInstall = document.getElementById('btnInstall');
  const btnInstallSettings = document.getElementById('btnInstallSettings');
  const btnConfirmPopupInstall = document.getElementById('btnConfirmPopupInstall');
  const btnDismissInstallPopup = document.getElementById('btnDismissInstallPopup');
  const btnHeaderDownload = document.getElementById('btnHeaderDownload');
  
  // Modals & Full Page Views
  const mobileModal = document.getElementById('mobileModal');
  const qrModal = document.getElementById('qrModal'); // Full page scanner
  const fullPageResultScreen = document.getElementById('fullPageResultScreen'); // Full page result screen
  const settingsModal = document.getElementById('settingsModal');
  const installPromptModal = document.getElementById('installPromptModal');
  const iosHelpModal = document.getElementById('iosHelpModal');
  const toast = document.getElementById('toast');

  // Hidden File Input
  const qrFileInput = document.getElementById('qrFileInput');

  // Scanner UI Buttons
  const btnCloseScanner = document.getElementById('btnCloseScanner');
  const btnTopUpload = document.getElementById('btnTopUpload');
  const btnGalleryUpload = document.getElementById('btnGalleryUpload');
  const btnToggleCamera = document.getElementById('btnToggleCamera');

  // Result UI Elements
  const btnBackFromResult = document.getElementById('btnBackFromResult');
  const scannedVpaText = document.getElementById('scannedVpaText');
  const btnCopyVpa = document.getElementById('btnCopyVpa');

  // Conditional Detail Display Rows
  const rowMerchantName = document.getElementById('rowMerchantName');
  const textMerchantName = document.getElementById('textMerchantName');
  const rowAmount = document.getElementById('rowAmount');
  const textScannedAmount = document.getElementById('textScannedAmount');
  const rowNote = document.getElementById('rowNote');
  const textScannedNote = document.getElementById('textScannedNote');

  // Action Buttons & Nav items
  const btnResultSendMoney = document.getElementById('btnResultSendMoney');
  const btnResultScanAnother = document.getElementById('btnResultScanAnother');
  const btnSendMobile = document.getElementById('btnSendMobile');
  const btnCheckBalance = document.getElementById('btnCheckBalance');
  const btnStartScan = document.getElementById('btnStartScan');
  const fabScan = document.getElementById('fabScan');
  const btnConfirmMobilePay = document.getElementById('btnConfirmMobilePay');
  const btnNavHome = document.getElementById('btnNavHome');
  const btnNavSettings = document.getElementById('btnNavSettings');
  const btnGotIosHelp = document.getElementById('btnGotIosHelp');

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

  // Header Download / iOS Help Trigger
  if (btnHeaderDownload) {
    btnHeaderDownload.addEventListener('click', () => {
      if (deferredPrompt) {
        triggerPwaInstall();
      } else {
        openModal(iosHelpModal);
      }
    });
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
      openModal(iosHelpModal);
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

  if (btnGotIosHelp) {
    btnGotIosHelp.addEventListener('click', () => {
      closeModal(iosHelpModal);
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
    if (modal) {
      modal.classList.add('active');
      modal.style.display = 'flex';
    }
  }

  function closeModal(modal) {
    if (modal) {
      modal.classList.remove('active');
      setTimeout(() => {
        if (!modal.classList.contains('active')) {
          modal.style.display = 'none';
        }
      }, 300);
    }
    if (modal === qrModal) {
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

  // Feature 3: FULL PAGE QR CAMERA SCANNER UI & GALLERY DECODER
  function startScanner() {
    if (fullPageResultScreen) {
      fullPageResultScreen.classList.remove('active');
      fullPageResultScreen.style.display = 'none';
    }

    openModal(qrModal);

    if (!html5QrcodeScanner) {
      html5QrcodeScanner = new Html5Qrcode("reader");
    }

    const config = { 
      fps: 25, 
      qrbox: (viewfinderWidth, viewfinderHeight) => {
        const minEdge = Math.min(viewfinderWidth, viewfinderHeight);
        const qrSize = Math.floor(minEdge * 0.85);
        return { width: qrSize, height: qrSize };
      },
      experimentalFeatures: {
        useBarCodeDetectorIfSupported: true
      }
    };

    const onScanSuccess = (decodedText) => {
      handleQrResult(decodedText);
      closeModal(qrModal);
      stopScanner();
    };

    html5QrcodeScanner.start(
      { facingMode: currentFacingMode },
      config,
      onScanSuccess,
      () => {}
    ).catch(err => {
      console.warn("Camera start warning:", err);
    });
  }

  function stopScanner() {
    if (html5QrcodeScanner) {
      html5QrcodeScanner.stop().catch(err => {});
    }
  }

  // Scanner UI Buttons
  if (btnCloseScanner) {
    btnCloseScanner.addEventListener('click', () => {
      closeModal(qrModal);
    });
  }

  // Gallery Upload Handlers (Using Dedicated Reader Container)
  if (btnTopUpload) btnTopUpload.addEventListener('click', () => qrFileInput.click());
  if (btnGalleryUpload) btnGalleryUpload.addEventListener('click', () => qrFileInput.click());

  if (qrFileInput) {
    qrFileInput.addEventListener('change', (e) => {
      if (!e.target.files || e.target.files.length === 0) return;
      const imageFile = e.target.files[0];
      
      showToast("Reading QR Image from Gallery...");

      // Stop camera scanner first if active
      stopScanner();

      const fileScanner = new Html5Qrcode("fileReader");
      fileScanner.scanFile(imageFile, true)
        .then((decodedText) => {
          fileScanner.clear();
          closeModal(qrModal);
          handleQrResult(decodedText);
        })
        .catch((err) => {
          console.error("Gallery decode error:", err);
          fileScanner.clear();
          showToast("No valid UPI QR code found in selected image.");
        });
    });
  }

  // Toggle Camera Facing Mode
  if (btnToggleCamera) {
    btnToggleCamera.addEventListener('click', () => {
      currentFacingMode = (currentFacingMode === "environment") ? "user" : "environment";
      stopScanner();
      setTimeout(startScanner, 300);
    });
  }

  // ==========================================================================
  // FULL PAGE EXTRACTED RESULT SCREEN LOGIC (CONDITIONAL DETAILS ONLY)
  // ==========================================================================
  function handleQrResult(qrData) {
    let cleanData = decodeURIComponent(qrData);
    let extractedMerchant = "";
    let extractedAmount = "";
    let extractedNote = "";

    if (cleanData.includes("pa=")) {
      scannedVpa = cleanData.substring(cleanData.indexOf("pa=") + 3).split("&")[0];
      if (cleanData.includes("pn=")) {
        extractedMerchant = cleanData.substring(cleanData.indexOf("pn=") + 3).split("&")[0];
      }
      if (cleanData.includes("am=")) {
        extractedAmount = cleanData.substring(cleanData.indexOf("am=") + 3).split("&")[0];
      }
      if (cleanData.includes("tn=")) {
        extractedNote = cleanData.substring(cleanData.indexOf("tn=") + 3).split("&")[0];
      }
    } else if (cleanData.toLowerCase().startsWith("upi://pay")) {
      const urlParams = new URLSearchParams(cleanData.substring(cleanData.indexOf("?")));
      scannedVpa = urlParams.get("pa") || cleanData;
      extractedMerchant = urlParams.get("pn") || "";
      extractedAmount = urlParams.get("am") || "";
      extractedNote = urlParams.get("tn") || "";
    } else {
      scannedVpa = cleanData;
    }

    // Auto Copy VPA to Clipboard
    copyToClipboard(scannedVpa);

    // Display Extracted UPI ID
    if (scannedVpaText) scannedVpaText.innerText = scannedVpa;

    // CONDITIONAL ROW 1: Merchant / Payee Name (only shown if present in QR)
    if (extractedMerchant && extractedMerchant.trim().length > 0) {
      if (textMerchantName) textMerchantName.innerText = extractedMerchant;
      if (rowMerchantName) rowMerchantName.style.display = 'flex';
    } else {
      if (rowMerchantName) rowMerchantName.style.display = 'none';
    }

    // CONDITIONAL ROW 2: Scanned Amount (only shown if present in QR)
    if (extractedAmount && parseFloat(extractedAmount) > 0) {
      if (textScannedAmount) textScannedAmount.innerText = `₹${extractedAmount}`;
      if (rowAmount) rowAmount.style.display = 'flex';
    } else {
      if (rowAmount) rowAmount.style.display = 'none';
    }

    // CONDITIONAL ROW 3: Transaction Note / Remarks (only shown if present in QR)
    if (extractedNote && extractedNote.trim().length > 0) {
      if (textScannedNote) textScannedNote.innerText = extractedNote;
      if (rowNote) rowNote.style.display = 'flex';
    } else {
      if (rowNote) rowNote.style.display = 'none';
    }

    // Open Full Page Result Screen
    if (fullPageResultScreen) {
      fullPageResultScreen.style.display = 'flex';
      setTimeout(() => fullPageResultScreen.classList.add('active'), 10);
    }
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

  if (btnCopyVpa) {
    btnCopyVpa.addEventListener('click', () => {
      copyToClipboard(scannedVpa);
      showToast("UPI ID re-copied to clipboard!");
    });
  }

  if (btnBackFromResult) {
    btnBackFromResult.addEventListener('click', () => {
      if (fullPageResultScreen) {
        fullPageResultScreen.classList.remove('active');
        setTimeout(() => fullPageResultScreen.style.display = 'none', 300);
      }
    });
  }

  if (btnResultScanAnother) {
    btnResultScanAnother.addEventListener('click', () => {
      if (fullPageResultScreen) {
        fullPageResultScreen.classList.remove('active');
        setTimeout(() => fullPageResultScreen.style.display = 'none', 300);
      }
      setTimeout(startScanner, 350);
    });
  }

  if (btnResultSendMoney) {
    btnResultSendMoney.addEventListener('click', () => {
      showToast("Opening Phone Dialer with USSD: *99*1*3#");
      setTimeout(() => dialUssd("*99*1*3#"), 400);
    });
  }

  if (btnStartScan) btnStartScan.addEventListener('click', startScanner);
  if (fabScan) fabScan.addEventListener('click', startScanner);
});
