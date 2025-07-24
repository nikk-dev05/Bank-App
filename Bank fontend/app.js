
const BASE_URL = "http://localhost:8080";
let token = localStorage.getItem("token");
let userAccountNumber = null;

async function register() {
  const username = document.getElementById("reg-username").value.trim();
  const password = document.getElementById("reg-password").value.trim();
  const errorEl = document.getElementById("register-error");

  if (!username || !password) {
    showError("Please enter both username and password.", errorEl);
    return;
  }

  const payload = { username, password };

  try {
    const response = await fetch(`${BASE_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      showSuccess("✅ Registration successful! Redirecting to login...", errorEl);
      triggerSparkle();

      document.getElementById("reg-username").value = "";
      document.getElementById("reg-password").value = "";

      setTimeout(() => {
        document.getElementById("register-container").classList.add("hidden");
        document.getElementById("login-container").classList.remove("hidden");
        errorEl.textContent = "";
      }, 2000);
    } else {
      const errMsg = await response.text();
      showError(errMsg || "Registration failed.", errorEl);
    }
  } catch (error) {
    showError("Server error: " + error.message, errorEl);
  }
}
async function login() {
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value.trim();
  const errorEl = document.getElementById("login-error");

  if (!username || !password) {
    errorEl.textContent = "Please enter both username and password.";
    return;
  }
  try {
    const response = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });

    if (!response.ok) {
      throw new Error("Invalid username or password");
    }

    const data = await response.json();
    token = data.token;
    localStorage.setItem("token", token);

    
    await loadUserInfo();
  } catch (err) {
    errorEl.textContent = err.message;
  }
}

async function fetchUserBankDetails() {
  try {
    const response = await fetch(`${BASE_URL}/bank`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
      },
    });

    if (!response.ok) throw new Error("Failed to fetch bank details");

    const data = await response.json();
     userAccountNumber = data.accountNumber;
    document.getElementById("user-name").textContent = data.accountHolderName;
    document.getElementById("user-username").textContent = data.username;
    document.getElementById("account-number").textContent = data.accountNumber;
    document.getElementById("account-balance").textContent = data.accountBalance;
  } catch (error) {
    alert("Error loading user bank details: " + error.message);
  }
}
async function loadUserInfo() {
  token = localStorage.getItem("token");
  const errorEl = document.getElementById("login-error");

  if (!token) {
    errorEl.textContent = "You must login again.";
    return;
  }

  try {
    const response = await fetch(`${BASE_URL}/bank/my`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error("Failed to load user info. Status: " + response.status);
    }

    const userDataArray = await response.json();
    console.log("userData:", userDataArray);

    if (!Array.isArray(userDataArray) || userDataArray.length === 0) {
      throw new Error("No account data found for user.");
    }

    const userData = userDataArray[0];
     userAccountNumber = userData.accountNumber;

    document.getElementById("user-name").textContent = userData.accountHolderName;
    document.getElementById("user-username").textContent = userData.user.username;
    document.getElementById("account-number").textContent = userData.accountNumber;
    document.getElementById("account-balance").textContent = userData.accountBalance;

    await loadTransactions(userData.accountNumber);


    document.getElementById("login-container").classList.add("hidden");
    document.getElementById("dashboard-container").classList.remove("hidden");
   if (!window.confettiShownYet) {
  showWelcomePopup();
  launchConfetti();
  window.confettiShownYet = true;
}


  } catch (error) {
    console.error("Error fetching user info:", error);
    alert("Error: " + error.message);
  }
}
async function loadTransactions(accountNumber) {
  const token = localStorage.getItem("token");

  try {
    const response = await fetch(`${BASE_URL}/bank/${accountNumber}/transactions`, {
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      throw new Error("Failed to fetch transactions");
    }

    const transactions = await response.json();
    console.log("Transaction Data:", transactions);

    
    const container = document.getElementById("transaction-container");
    container.innerHTML = "";

    if (transactions.length === 0) {
      container.innerHTML = "<p>No transactions found.</p>";
    } else {
      transactions.forEach(txn => {
        container.innerHTML += `
          <div>
            <strong>Type:</strong> ${txn.type} <br/>
            <strong>Amount:</strong> ₹${txn.amount} <br/>
            <strong>Date:</strong> ${new Date(txn.timestamp).toLocaleString()}
            <hr/>
          </div>`;
      });
    }

  } catch (error) {
    console.error("Error fetching transactions:", error);
  }
}
function logout() {
  token = null;
  localStorage.removeItem("token");
  document.getElementById("login-container").classList.remove("hidden");
  document.getElementById("dashboard-container").classList.add("hidden");
  document.getElementById("username").value = "";
  document.getElementById("password").value = "";
  document.getElementById("login-error").textContent = "";
}
async function depositMoney() {
  const amount = document.getElementById('deposit-amount').value;
  if (!amount || isNaN(amount) || amount <= 0) {
    alert("Please enter a valid amount to deposit.");
    return;
  }

  try {
    const response = await fetch(`${BASE_URL}/bank/${userAccountNumber}/deposit?amount=${amount}`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`,
      },
    });

    if (!response.ok) throw new Error("Failed to deposit money");

    const data = await response.json(); 

  
    showActionMessage(`₹${amount} deposited successfully. Available Balance is : ₹${data.accountBalance}`);

    await loadUserInfo();

    document.getElementById('deposit-amount').value = '';
  } catch (error) {
    alert("Deposit failed: " + error.message);
  }
}

async function withdrawMoney() {
  const token = localStorage.getItem("token");

  const accountNumberSpan = document.getElementById("account-number");
  const amountField = document.getElementById("withdraw-amount");

  if (!accountNumberSpan || !amountField) {
    alert("Missing input fields. Please check your HTML.");
    return;
  }

  const accountId = accountNumberSpan.innerText.trim();
  const amount = amountField.value;

  try {
    const response = await fetch(`${BASE_URL}/bank/${accountId}/withdraw?amount=${amount}`, {
      method: "PUT",
      headers: {
        "Authorization": "Bearer " + token
      }
    });

    const responseText = await response.text();
    if (response.ok) {
      const data = JSON.parse(responseText);
      
  
      showActionMessage(`₹${amount} withdrawn successfully. Available Balance is : ₹${data.accountBalance}`);
      document.getElementById("account-balance").innerText = data.accountBalance;

    
      amountField.value = '';
    } else {
      console.error("Backend error response:", responseText);
      alert("Withdrawal failed ❌: " + responseText);
    }
  } catch (err) {
    console.error("Withdraw error (network/client):", err);
    alert("Withdrawal failed ❌: " + err.message);
  }
}

function showLogin() {
  document.getElementById("register-container").classList.add("hidden");
  document.getElementById("login-container").classList.remove("hidden");
}

function showRegister() {
  document.getElementById("login-container").classList.add("hidden");
  document.getElementById("register-container").classList.remove("hidden");
}
function showSuccess(message, container) {
  container.textContent = message;
  container.className = "message success";
}

function showError(message, container) {
  container.textContent = message;
  container.className = "message error";
}

function triggerSparkle() {
  const sparkle = document.createElement("div");
  sparkle.className = "sparkle-bg";
  document.body.appendChild(sparkle);

  setTimeout(() => sparkle.remove(), 2000);
}
function showWelcomePopup() {
  const popup = document.getElementById("welcome-popup");
  popup.classList.remove("hidden");

  setTimeout(() => popup.classList.add("hidden"), 3000);
}
function launchConfetti() {
  const canvas = document.getElementById("confetti-canvas");
  const ctx = canvas.getContext("2d");
  canvas.classList.remove("hidden");

  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  const pieces = [];
  for (let i = 0; i < 100; i++) {
    pieces.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height - canvas.height,
      radius: 6 + Math.random() * 4,
      color: `hsl(${Math.random() * 360}, 100%, 50%)`,
      speed: 2 + Math.random() * 3
    });
  }

  let frame = 0;
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    pieces.forEach(p => {
      ctx.beginPath();
      ctx.arc(p.x, p.y += p.speed, p.radius, 0, Math.PI * 2);
      ctx.fillStyle = p.color;
      ctx.fill();
    });
  };

  const loop = () => {
    frame++;
    animate();
    if (frame < 100) requestAnimationFrame(loop);
    else canvas.classList.add("hidden");
  };
  loop();
}
function showActionMessage(message) {
  const msgBox = document.getElementById("action-message");
  msgBox.textContent = message;
  msgBox.classList.add("show");

  setTimeout(() => {
    msgBox.classList.remove("show");
  }, 3000);
}



