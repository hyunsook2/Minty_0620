//function loginWithKakao() {
//    location.href='login/oauth2/callback/kakao'
//}
//
//  // 아래는 데모를 위한 UI 코드입니다.
//  displayToken()
//  function displayToken() {
//    var token = getCookie('authorize-access-token');
//
//    if(token) {
//      Kakao.Auth.setAccessToken(token);
//      Kakao.Auth.getStatusInfo()
//        .then(function(res) {
//          if (res.status === 'connected') {
//            document.getElementById('token-result').innerText
//              = 'login success, token: ' + Kakao.Auth.getAccessToken();
//          }
//        })
//        .catch(function(err) {
//          Kakao.Auth.setAccessToken(null);
//        });
//    }
//  }
//
//  function getCookie(name) {
//    var parts = document.cookie.split(name + '=');
//    if (parts.length === 2) { return parts[1].split(';')[0]; }
//  }