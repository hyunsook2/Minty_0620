var rolLength = 6;
var setNum;
var hiddenInput = document.createElement("input");
hiddenInput.className = "hidden-input";

// Check if the user has already spun the roulette today
const hasSpunToday = () => {
  var lastSpinDate = localStorage.getItem("lastSpinDate");
  if (lastSpinDate) {
    var today = new Date().toDateString();
    return lastSpinDate === today;
  }
  return false;
};

// Save the current spin date in the local storage
const saveSpinDate = () => {
  var today = new Date().toDateString();
  localStorage.setItem("lastSpinDate", today);
};

// 랜덤 숫자 생성 함수
const rRandom = () => {
  var min = Math.ceil(0);
  var max = Math.floor(rolLength - 1);
  return Math.floor(Math.random() * (max - min)) + min;
};

// 룰렛 회전 함수
const rRotate = () => {
  if (hasSpunToday()) {
    alert("룰렛은 하루에 한 번만 돌릴 수 있습니다.");
    return;
  }

  var panel = document.querySelector(".rouletter-wacu");
  var btn = document.querySelector(".rouletter-btn");
  var deg = [];

  for (var i = 1, len = rolLength; i <= len; i++) {
    deg.push((360 / len) * i);
  }

  var num = 0;
  document.body.append(hiddenInput);
  setNum = hiddenInput.value = rRandom();

  var ani = setInterval(() => {
    num++;
    panel.style.transform = "rotate(" + 360 * num + "deg)";
    btn.disabled = true;
    btn.style.pointerEvents = "none";

    if (num === 50) {
      clearInterval(ani);
      panel.style.transform = `rotate(${deg[setNum]}deg)`;

      // Save the spin date
      saveSpinDate();

      // Display the popup and save the result
      rLayerPopup(setNum);
      hiddenInput.remove();
    }
  }, 50);
};

// 팝업 함수와 결과 저장 함수
const rLayerPopup = (num) => {
  switch (num) {
    case 1:
      alert("당첨!! 100 포인트 지급");
      saveRouletteResult("당첨, 100포인트", 100);
      break;
    case 3:
      alert("당첨!! 300 포인트 지급");
      saveRouletteResult("당첨, 300포인트", 300);
      break;
    case 5:
      alert("당첨!! 1000 포인트 지급");
      saveRouletteResult("당첨, 1000포인트", 1000);
      break;
    default:
      alert("꽝! 다음 기회에");
      break;
  }
};

// 결과 저장 함수 (AJAX로 서버에 전송)
const saveRouletteResult = (result, point) => {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/roulette/save", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.setRequestHeader("X-CSRF-TOKEN", document.querySelector('meta[name="_csrf"]').getAttribute('content'));
  xhr.setRequestHeader("X-CSRF-HEADER", document.querySelector('meta[name="_csrf_header"]').getAttribute('content'));

  xhr.onreadystatechange = function () {
    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
      console.log("Roulette result saved successfully");
      console.log(`Points (${point}) saved successfully`);
    } else if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log("Failed to save roulette result");
    }
  };

  const data = JSON.stringify({ result, point });
  xhr.send(data);
};

// 리셋 함수
const rReset = (ele) => {
  setTimeout(() => {
    ele.disabled = false;
    ele.style.pointerEvents = "auto";
  }, 5500);
};

// 룰렛 이벤트 클릭 버튼
document.addEventListener("click", function (e) {
  var target = e.target;
  if (target.tagName === "BUTTON") {
    rRotate();
    rReset(target);
  }
});