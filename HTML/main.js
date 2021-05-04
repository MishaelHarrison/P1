let user;
let APIUrl = "http://localhost:8001/";

/////////////////////
///HTML generators///
/////////////////////

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
    <input type="submit" value="Login!">
  </div>
  </form>
  <span id="errorNotification"  style="color:red;"></span>`;
}

function createCard(content) {
  return `
  <div class="card">
  <div class="card-body">
    ${content}
  </div>
</div>`;
}

function accountDisplay(element) {
  return `
  <div class="col-2">` +
    createCard(
      "account type: " + element.accountType +
      "<br>balance: " + element.balance +
      (element.approved ?
        `<button type="button" class="btn btn-primary" id="accountID${element.accountID}">Create Transaction</button>`
        : `<br><span style="color:red;">pending activation</span>`)) +
    `</div>`;
}

function accountMenu(account) {
  return `
  <button type="button" class="btn btn-primary" onclick="alert('did the thing')">create transaction with other user</button>
  <button type="button" class="btn btn-primary" onclick="alert('did the thing')">deposit</button>
  <button type="button" class="btn btn-primary" onclick="alert('did the thing')">withdrawl</button>
  `;
}

////////////////////
///main injectors///
////////////////////

function reset() {
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
}

function listUsersAccounts() {
  apiUsersAccounts(user.username, user.password, x => {
    let printer = document.getElementById("InjectableBody");
    if (x) {
      x.forEach(element => {
        printer.innerHTML += accountDisplay(element);
      });
      printer.innerHTML = `<div class="row">${printer.innerHTML}</div>`;
      x.forEach(element => {
        if (element.approved) document.getElementById(`accountID${element.accountID}`).addEventListener("click", x => {
          transact(element);
        });
      })
    }
  })
}

function transact(account) {
  let printer = document.getElementById("InjectableBody");
  printer.innerHTML = `selected account: ${account.accountType}<br>` + accountMenu(account);

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

function adminLogin() {
  document.getElementById("adminLoginLabel").innerText = "Admin login";
}

function logout() {
  user = null;
  reset();
}

///////////////
///api calls///
///////////////

function apiLogin(username, password, call) {
  fetch(APIUrl + `user/${username}/${password}`)
    .then((Response) => Response.json())
    .then((x) => call(x))
    .catch((x) => call(null));
}

function apiUsersAccounts(username, password, call) {
  fetch(APIUrl + `accounts/${username}/${password}`)
    .then((Response) => Response.json())
    .then((x) => call(x))
    .catch((x) => call(null));
}

reset();
