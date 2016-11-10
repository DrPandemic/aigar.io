const loginButton = document.getElementById("login-button");
loginButton.onclick = () => {
  const password = document.getElementById("password-field").value;
  localStorage.setItem("adminPassword", password);
  window.location.href = "/web/admin.html";
  return false;
};
