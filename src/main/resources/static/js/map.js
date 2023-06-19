
    // 초기화 및 변수 선언 Setting

    // 지도 div
	var mapContainer = document.querySelector('#map'),

	// 최초 지도 Setting
    mapOption = {
        center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };

    // 현재 자신의 위치 Marker Setting
    var myLocationMarker;   // 자신 위치 찍어주는 Element 전역 변수로 선언
    var myLocationImageSrc = 'https://play-lh.googleusercontent.com/5WifOWRs00-sCNxCvFNJ22d4xg_NQkAODjmOKuCQqe57SjmDw8S6VOSLkqo6fs4zqis', // 이미지 주소
    myLocationImageSize = new kakao.maps.Size(64, 69), // 이미지 크기
    myLocationImageOption = {offset: new kakao.maps.Point(27, 69)};   // 몰?루
    var myLocationMarkerImage = new kakao.maps.MarkerImage(myLocationImageSrc, myLocationImageSize, myLocationImageOption); // 마커 이미지 담아서 초기화

    // 지도 생성
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // Geocoder 라이브러리 초기화
    var geocoder = new kakao.maps.services.Geocoder();

    // 지도에 따라 움직이는 마커 전역 변수로 선언
    var centerMarker;

    // 주소 찍어줄 info창
    var infowindow = new kakao.maps.InfoWindow({zindex:1}); // 중심 주소 Info

    // 중심지 담을 전역 변수 선언
    var center;


// 최초의 본인 위치 잡기 (위치 정보를 허용하시겠습니까? 부분 허용 후 실행됨)
if (navigator.geolocation) {
    // GeoLocation을 이용해서 접속 위치를 얻어옵니다
    navigator.geolocation.getCurrentPosition(function(position) {

        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

         // 위도 경도 locPosition 담아두기
        var locPosition = new kakao.maps.LatLng(lat, lon);
        document.getElementById('latitude').value = lat;
                document.getElementById('longitude').value = lon;
        // 마커와 인포윈도우를 표시합니다
        displayMarker(locPosition);
      });

} else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다

    var locPosition = new kakao.maps.LatLng(33.450701, 126.570667);

    displayMarker(locPosition);
}

// 마커 찍어주는 함수
function displayMarker(locPosition) {
        // 자신의 현재 위치 찍어줄 마커 생성 (고정)
        myLocationMarker = new kakao.maps.Marker({
            map : map,
            position : locPosition,
            image : myLocationMarkerImage
        });

    // 지도 중심좌표를 접속위치로 변경합니다
    map.setCenter(locPosition);
}


// 현재 지도 중심좌표로 주소를 검색해서 지도 좌측 상단에 표시합니다
searchAddrFromCoords(map.getCenter(), displayCenterInfo);

// 지도를 클릭했을 때 클릭 위치 좌표에 대한 주소정보를 표시하도록 이벤트를 등록합니다
kakao.maps.event.addListener(map, 'idle', function() {
    searchAddrFromCoords(map.getCenter(), displayCenterInfo);
});

function searchAddrFromCoords(coords, callback) {
    // 좌표로 행정동 주소 정보를 요청합니다
    geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
}

function searchDetailAddrFromCoords(coords, callback) {
    // 좌표로 법정동 상세 주소 정보를 요청합니다
    geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
}

// 지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출하는 함수입니다
function displayCenterInfo(result, status) {
    if (status === kakao.maps.services.Status.OK) {
        var infoDiv = document.getElementById('centerAddr');
        for(var i = 0; i < result.length; i++) {
            // 행정동의 region_type 값은 'H' 이므로
            if (result[i].region_type === 'H') {
                infoDiv.innerHTML = result[i].address_name;
                document.getElementById('address').value = result[i].address_name;
                break;
            }
        }
    }
}





