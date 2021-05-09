let user;
let APIUrl = "http://localhost:8001/";

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///HTML generators//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function loginHTML() {
  return `
  <span id="adminLoginLabel"></span>
  <form>
  <div class="formItem">
    <label for="username">usern<span onclick="adminLogin()">a</span>me: </label>
    <input type="text" name="username" id="username" required>
  </div>
  <div class="formItem">
    <label for="password">password: </label>
    <input type="password" name="password" id="password" required>
  </div>
  <div class="formItem">
    <input type="submit" value="Login">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span><br>
  <button type="button" class="btn btn-light" onclick="newUser()">New user</button>`;
}

function newUserHTML() {
  return `
  <form>
  <div class="formItem">
    <label for="username">username: </label>
    <input type="text" name="username" id="username" required>
  </div>
  <div class="formItem">
    <label for="password1">password: </label>
    <input type="password" name="password1" id="password1" required>
  </div>
  <div class="formItem">
    <label for="password2">password: </label>
    <input type="password" name="password2" id="password2" required>
  </div>
  <div class="formItem">
    <label for="fname">First name: </label>
    <input type="text" name="fname" id="fname">
  </div>
  <div class="formItem">
    <label for="lname">Last name: </label>
    <input type="text" name="lname" id="lname">
  </div>
  <div class="formItem">
    <input type="submit" value="Submit">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span>`;
}

function cashHTML(cashType, acctType, bal) {
  return `
  Account: ${acctType}<br>Current balance: ${bal}
  <br><br>Enter amount you woud like to ${cashType}:
  <form>
  <div class="formItem">
    <input type="number" step="0.01" min="0"${cashType == "deposit" ? "" : ` max="${bal}"`} name="ammount" id="ammount" required>
  </div>
  <div class="formItem">
    <input type="submit" value="Complete">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span><br>`;
}

function newAccountHTML() {
  return `
  Enter name for account:
  <form>
  <div class="formItem">
    <input type="text" name="accountType" id="accountType" required>
  </div>
  <div class="formItem">
    <br>Starting balance:<br>
    <input type="number" step="0.01" min="0" name="startingBalance" id="startingBalance" required>
  </div>
  <div class="formItem">
    <input type="submit" value="Complete">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span><br>`;
}

function exchangeHTML(acctType, bal) {
  return `
  Account: ${acctType}<br>Current balance: ${bal}
  <br><br>Enter amount you woud like to transfer:
  <form>
  <div class="formItem">
    <input type="number" step="0.01" min="0" max="${bal}" name="ammount" id="ammount" required>
  </div>
  <div class="formItem">
    <br>Account ID to transfer to:<br>
    <input type="number" min="0" name="id" id="id" required>
  </div>
  <div class="formItem">
    <input type="submit" value="Complete">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span><br>`;
}

function createCard(content) {
  return `
  <div class="card" style="margin-bottom: 10px;">
  <div class="card-body">
    ${content}
  </div>
</div>`;
}

function accountDisplay(element) {
  return `
  <div class="col-2">` +
    createCard(
      "account id: " + element.accountID +
      "<br>account type: " + element.accountType +
      "<br>balance: " + element.balance +
      (element.approved ?
        `<br><br><button type="button" class="btn btn-primary" id="accountID${element.accountID}create">Create Transaction</button>
        <br><br><button type="button" class="btn btn-primary" id="accountID${element.accountID}view">view Transactions</button>`
        : `<br><span style="color:red;">pending activation</span>`)) +
    `</div>`;
}

function displayTransaction(t) {
  return `
  id: ${t.transactionID}<br>
  time: ${t.timestamp}<br>
  ${!t.receivingAccountID || !t.issuingAccountID ?
      !t.issuingAccountID ?
        "Deposit<br>"
        : "Withdrawl<br>"
      : `Issueing user: ${t.issuingUsername}<br>`}
  amount: ${t.amount}`;
}

function displayPendingTransaction(t) {
  return `
  account: ${t.acceptingAccountName}<br>
  issuer: ${t.issuingFname}, ${t.issuingLname}<br>
  amount: ${t.amount}<br><br>
  <button type="button" class="btn btn-warning" onclick="acceptTransaction(${t.pendingTransactionID})">Accept Transaction</button>
  <button type="button" class="btn btn-danger" onclick="denyTransaction(${t.pendingTransactionID})">Deny Transaction</button>`;
}

function transactMenu(account) {
  return `
  <button type="button" class="btn btn-primary" onclick="exchange(${account.accountID}, ${account.balance}, '${account.accountType}')">create transaction with other user</button>
  <button type="button" class="btn btn-primary" onclick="cash(true, ${account.accountID}, ${account.balance}, '${account.accountType}')">deposit</button>
  <button type="button" class="btn btn-primary" onclick="cash(false, ${account.accountID}, ${account.balance}, '${account.accountType}')">withdrawl</button>
  `;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///main injectors//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function reset() {
  apiCheckIn(() => {
    if (user) {
      document.getElementById("InjectableBody").innerHTML = "";
      document.getElementById("greetings").innerText =
        " " + user.fname + " " + user.lname;
      listUsersAccounts();
      document.getElementById("logoutButton").innerHTML = `<button type="button" class="btn btn-primary" onclick="logout()">logout</button>`;
    } else {
      document.getElementById("greetings").innerText = "";
      document.getElementById("logoutButton").innerHTML = "";
      login();
    }
  }, () => {
    document.getElementById("InjectableBody").innerHTML = `<h3 style="color:red;">Cant connect to server</h3>`;
  });
}

function listUsersAccounts() {
  apiUsersAccounts(user.username, user.password, x => {
    let printer = document.getElementById("InjectableBody");
    printer.innerHTML = `
    <div class="col-2">
      ${createCard(`
        <button type="button" class="btn btn-primary" onclick="newAccount()">Create a new account</button>
        ${!user.pendingCount ? "" : `<br><br><button type="button" class="btn btn-primary" onclick="pendingTransactions()">View pending transactions</button>`}
      `)}
    </div>`;
    x.forEach(element => {
      printer.innerHTML += accountDisplay(element);
    });
    printer.innerHTML = `<div class="row">${printer.innerHTML}</div>`;
    x.forEach(element => {
      if (element.approved) {
        document.getElementById(`accountID${element.accountID}create`)
          .addEventListener("click", () => transact(element));
        document.getElementById(`accountID${element.accountID}view`)
          .addEventListener("click", () => transactionsFrom(element));
      }
    })
  })
}

function newAccount() {
  document.getElementById("InjectableBody").innerHTML = newAccountHTML();

  document.forms[0].addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    apiNewAccount(user.username, user.password, formData.get("accountType"), formData.get("startingBalance"))
    reset();
  });
}

function transactionsFrom(account) {
  apiAccountTransactionHistory(user.username, user.password, account.accountID, x => {
    let printer = document.getElementById("InjectableBody");
    printer.innerHTML = "";
    x.forEach(element => {
      printer.innerHTML += `
      <div class="col-2">
        ${createCard(displayTransaction(element))}
      </div>`
    })
    printer.innerHTML = `<div class="row">${printer.innerHTML}</div>`;
  });
}

function pendingTransactions() {
  apiUserPending(user.username, user.password, x => {
    let printer = document.getElementById("InjectableBody");
    printer.innerHTML = "";
    x.forEach(element => {
      printer.innerHTML += `
      <div class="col-2" id="pend${element.pendingTransactionID}">
        ${createCard(displayPendingTransaction(element))}
      </div>`
    })
    printer.innerHTML = `<div class="row">${printer.innerHTML}</div>`;
  });
}

function acceptTransaction(id) {
  apiAcceptTransaction(user.username, user.password, id);
  document.getElementById(`pend${id}`).remove();
}

function denyTransaction(id) {
  apiDenyTransaction(user.username, user.password, id);
  document.getElementById(`pend${id}`).remove();
}

function transact(account) {
  let printer = document.getElementById("InjectableBody");
  printer.innerHTML = `selected account: ${account.accountType}<br><br>` + transactMenu(account);
}

function cash(isDeposit, id, bal, type) {
  document.getElementById("InjectableBody").innerHTML = cashHTML(isDeposit ? "deposit" : "withdrawl", type, bal);

  document.forms[0].addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    if (isDeposit || formData.get("ammount") <= bal) {
      apiCash(user.username, user.password, formData.get("ammount"), isDeposit, id);
      reset();
    } else {
      document.getElementById("errorNotification").innerText =
        "enter an ammount within your balance";
    }
  });
}

function exchange(id, bal, type) {
  document.getElementById("InjectableBody").innerHTML = exchangeHTML(type, bal);

  document.forms[0].addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    if (formData.get("ammount") <= bal) {
      apiExchange(user.username, user.password, formData.get("ammount"), id, formData.get("id"));
      reset();
    } else {
      document.getElementById("errorNotification").innerText =
        "enter an ammount within your balance";
    }
  });
}

function login() {
  document.getElementById("InjectableBody").innerHTML = loginHTML();

  document.forms[0].addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    apiLogin(formData.get("username"), formData.get("password"), (x) => {
      if (x) {
        user = x;
        reset();
      } else {
        document.getElementById("errorNotification").innerText =
          "username/password not found, try again";
      }
    });
  });
}

function newUser() {
  document.getElementById("InjectableBody").innerHTML = newUserHTML();

  document.forms[0].addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    if (formData.get("password1") == formData.get("password2")) {
      apiCreateUser(formData.get("username"), formData.get("password1"),
        formData.get("fname"), formData.get("lname"), x => {
          user = x;
          reset();
        });
    } else {
      document.getElementById("errorNotification").innerText =
        "passwords must match";
    }
  });
}

function adminLogin() {
  document.getElementById("adminLoginLabel").innerText = "Admin login";
}

function logout() {
  user = null;
  reset();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///api calls//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function apiCheckIn(good, bad) {
  fetch(APIUrl + `yo_we_good`,
    { method: "GET" })
    .then((Response) => Response.json())
    .then((x) => {
      if (x == "yea we good") good();
      else bad();
    })
    .catch(() => bad());
}

function apiLogin(username, password, call) {
  fetch(APIUrl + `user/${username}/${password}`,
    { method: "GET" })
    .then((Response) => Response.json())
    .then((x) => call(x))
    .catch(() => call(null));
}

function apiCreateUser(username, password, fname, lname, call) {
  fetch(APIUrl + `user/${username}/${password}/${fname}/${lname}`,
    { method: "POST" })
    .then((Response) => Response.json())
    .then((x) => call(x))
    .catch(() => call(null));
}

function apiUsersAccounts(username, password, call) {
  fetch(APIUrl + `accounts/${username}/${password}`,
    { method: "GET" })
    .then((Response) => Response.json())
    .then((x) => call(x))
    .catch(() => call(null));
}

function apiNewAccount(username, password, accountName, startingBalance) {
  fetch(APIUrl + `accounts/${username}/${password}/${accountName}/${startingBalance}`,
    { method: "POST" });
}

function apiCash(username, password, ammount, isDeposit, id, call) {
  fetch(APIUrl + `transactions/${username}/${password}/${id}/${ammount}/${isDeposit ? "deposit" : "withdrawal"}`,
    { method: "POST" });
}

function apiExchange(username, password, ammount, issueingID, recievingID, call) {
  fetch(APIUrl + `transactions/${username}/${password}/${issueingID}/${ammount}/${recievingID}`,
    { method: "POST" });
}

function apiAccountTransactionHistory(username, password, id, call) {
  fetch(APIUrl + `transactions/${username}/${password}/${id}`,
    { method: "GET" })
    .then((Response) => Response.json())
    .then((x) => call(x));
}

function apiUserPending(username, password, call) {
  fetch(APIUrl + `transactions/${username}/${password}`,
    { method: "GET" })
    .then((Response) => Response.json())
    .then((x) => call(x));
}

function apiAcceptTransaction(username, password, id) {
  fetch(APIUrl + `transactions/${username}/${password}/${id}`,
    { method: "PUT" });
}

function apiDenyTransaction(username, password, id) {
  fetch(APIUrl + `transactions/${username}/${password}/${id}`,
    { method: "DELETE" });
}

/////////////////////////
///start initial setup///
/////////////////////////

reset();