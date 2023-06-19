/*$(".chk-button").on("click", function () {
      $(".chk-code").removeClass("chk-code");
    });*/

  function requestMessage() {
  var mobileNumber = $("#mobile").val();
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");
  var currentTime = new Date().toLocaleString();

  $.ajax({
    url: "/sms/send",
    type: "POST",
    contentType: "application/json",
    data: mobileNumber,
    beforeSend: function (xhr) {
      xhr.setRequestHeader(header, token);
    },
    success: function (response) {
      console.log(response);
      if (response.hasOwnProperty("smsResponse") && response.smsResponse.statusCode === "202") {
        $(".chk-code").removeClass("chk-code");
        alert("인증번호가 발송되었습니다.");
        $("#verificationTime").text("요청 시간: " + currentTime); // Update verification time in HTML

        // Calculate expiration time
        var verificationTimeLimit = response.verificationTimeLimit;
        var minutes = parseInt(verificationTimeLimit.slice(2, -1)); // Extract minutes from the format "PT3M"
        var expirationTime = minutes * 60 * 1000; // Convert minutes to milliseconds

        var interval = 1000; // 1 second interval

        var countdown = setInterval(function () {
          if (expirationTime <= 0) {
            clearInterval(countdown);
            $("#verificationExpirationTime").text("만료 시간: 시간 초과").show();
            $("#verificationCode").val("").prop("readonly", true);
            $("#verifyButton").prop("disabled", true);
            return;
          }

          var remainingMinutes = Math.floor(expirationTime / (60 * 1000));
          var remainingSeconds = Math.floor((expirationTime % (60 * 1000)) / 1000);

          var formattedExpirationTime = remainingMinutes + ":" + (remainingSeconds < 10 ? "0" : "") + remainingSeconds;
          $("#verificationExpirationTime").text("만료 시간: " + formattedExpirationTime).show();

          expirationTime -= interval;
        }, interval);
      } else if (response.hasOwnProperty("smsResponse") && response.smsResponse.statusCode === 400) {
        alert("이미 존재하는 휴대폰 번호입니다.");
      } else {
        alert("인증번호 발송에 실패하였습니다.");
        }
    },
    error: function (xhr, status, error) {
      if (xhr.status === 400) {
      console.log(error);
        alert(xhr.responseJSON.message); // Display the server's error message
      } else {
        alert("서버와의 통신에 문제가 발생했습니다. 상태 코드: " + xhr.status);
      }
    },
  });
}




    function verifyCode() {
      var enteredCode = $("#verificationCode").val(); // 입력한 인증번호
      var token = $("meta[name='_csrf']").attr("content");
      var header = $("meta[name='_csrf_header']").attr("content");
      $.ajax({
        url: "/sms/verify",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ verificationCode: enteredCode }), // 전송 데이터를 객체 형태로 JSON으로 변환
        beforeSend: function (xhr) {
          xhr.setRequestHeader(header, token);
        },
        success: function (response) {
          console.log(response);
          if (response === true) {
            alert("인증이 완료되었습니다.");
            $("#verificationExpirationTime").hide();
            $("#verificationCode").prop("readonly", true);
            $("#verifyButton").prop("disabled", true);
            $("#verification").val('true');
          } else {
            alert("인증번호가 다릅니다.");
          }
        },
        error: function (xhr, status, error) {
          alert("서버와의 통신에 문제가 발생했습니다.");
        }
      });
    }