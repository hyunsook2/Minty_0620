function validateForm() {
  let username = document.getElementById("username").value;
  let password = document.getElementById("password").value;

  if (password !== "a") {
    alert("Wrong password");
    return false;
  }

  let userId;

  if (username === "mario") {
    userId = 3;
  } else if (username === "ari") {
    userId = 1;
  } else if (username === "nelson") {
    userId = 2;
  } else if (username === "eliezer") {
    userId = 4;
  } else {
    alert("Wrong password");
    return false;
  }

  localStorage.setItem("userId", userId);
  window.location.href = "chat-app.html";

  return false;
}