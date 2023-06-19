
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

    // 마커를 담을 배열입니다
    var markers = [];

    // 주소 찍어줄 info창
    var infowindow = new kakao.maps.InfoWindow({zindex:1}); // 중심 주소 Info

    // 중심지 담을 전역 변수 선언
    var center;

    // 장소 검색 객체를 생성합니다
    var ps = new kakao.maps.services.Places();


// 최초의 본인 위치 잡기 (위치 정보를 허용하시겠습니까? 부분 허용 후 실행됨)
if (navigator.geolocation) {
    // GeoLocation을 이용해서 접속 위치를 얻어옵니다
    navigator.geolocation.getCurrentPosition(function(position) {

        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

         // 위도 경도 locPosition 담아두기
        var locPosition = new kakao.maps.LatLng(lat, lon);

        // 마커와 인포윈도우를 표시합니다
        displayMarker(locPosition);
      });

} else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다

    var locPosition = new kakao.maps.LatLng(33.450701, 126.570667);

    displayMarker(locPosition);
}

// 마커 찍어주는 함수
function displayMarker(locPosition) {
         // 마커를 생성합니다
        centerMarker = new kakao.maps.Marker({
                    map: map,
                    position: locPosition
             });
        // 자신의 현재 위치 찍어줄 마커 생성 (지금은 일단 고정임)
        myLocationMarker = new kakao.maps.Marker({
            map : map,
            position : locPosition,
            image : myLocationMarkerImage
        });

    // 지도 중심좌표를 접속위치로 변경합니다
    map.setCenter(locPosition);
}

// 맵 드래그시 이벤트 등록
//kakao.maps.event.addListener(map, 'dragend', updateMyMarkerPosition);

// 드래그시 기존의 마커를 제거 하기 위한 함수
//function removeMyMarker(centerMarker) {
//    if (centerMarker) {
//        centerMarker.setMap(null);
//    }
//}

// 드래그 완료시 마커를 변경 할 함수
//function updateMyMarkerPosition() {
//    // 기존 마커 지우고
//    removeMyMarker(centerMarker);
//    // 센터 잡아서
//    center = map.getCenter();
//    console.log(center);
//    // 마커를 새로 생성(이게 효율적인진 모르겠다)
//    centerMarker = new kakao.maps.Marker({
//                    map: map,
//                    position: center
//    });
//    // 마커를 바꿔주고 해당 마커의 상세 주소 찍어주기
//    searchDetailAddrFromCoords(center, function(result, status) {
//     if (status === kakao.maps.services.Status.OK) {
//            var detailAddr = !!result[0].road_address ? '<div>도로명주소 : ' + result[0].road_address.address_name + '</div>' : '';
//            detailAddr += '<div>지번 주소 : ' + result[0].address.address_name + '</div>';
//
//            var content = '<div class="bAddr">' +
//                            '<span class="title">법정동 주소정보</span>' +
//                            detailAddr +
//                        '</div>';
//
//            // 인포윈도우에 드래그한 위치에 대한 법정동 상세 주소정보를 표시합니다
//            infowindow.setContent(content);
//            infowindow.open(map, centerMarker);
//        }
//    });
//}
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

        // 위도, 경도, 주소 hidden input 가져오기
        var latitudeInput = document.getElementById('latitude');
        var longitudeInput = document.getElementById('longitude');
        var addressInput = document.getElementById('address');

        for(var i = 0; i < result.length; i++) {
            // 행정동의 region_type 값은 'H' 이므로
            if (result[i].region_type === 'H') {
                infoDiv.innerHTML = result[i].address_name;

                // 위도, 경도, 주소 hidden input 값 업데이트
                latitudeInput.value = lat;
                longitudeInput.value = lon;
                addressInput.value = result[i].address_name;

                break;
            }
        }
    }
}

// 키워드로 장소를 검색합니다
searchPlaces();
// 키워드 검색을 요청하는 함수입니다
function searchPlaces() {

    var keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch( keyword, placesSearchCB);
}

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);

    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

        alert('검색 결과가 존재하지 않습니다.');
        return;

    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;

    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlaces(places) {

    var listEl = document.getElementById('placesList'),
    menuEl = document.getElementById('menu_wrap'),
    fragment = document.createDocumentFragment(),
    bounds = new kakao.maps.LatLngBounds(),
    listStr = '';

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    for ( var i=0; i<places.length; i++ ) {
        // 마커를 생성하고 지도에 표시합니다
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i),
            itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(placePosition);

        // 마커와 검색결과 항목에 mouseover 했을때
        // 해당 장소에 인포윈도우에 장소명을 표시합니다
        // mouseout 했을 때는 인포윈도우를 닫습니다
        (function(marker, title, placePosition) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            /*itemEl.onmouseover =  function () {
                displayInfowindow(marker, title);
            };*/ // 이거 병신이라 일단 주석처리

            itemEl.onmouseout =  function () {
                infowindow.close();
            };

            // List 클릭시 거기로 이동하시오
            itemEl.addEventListener('click', function(){
                            removeMyMarker(centerMarker);
                            center = placePosition;
                            centerMarker = new kakao.maps.Marker({
                                        map: map,
                                        position: center
                                    });
                            displayInfowindow(marker, title);
                            map.setLevel(3);
                            map.panTo(placePosition);
                        });
        })(marker, places[i].place_name, placePosition);

        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
    map.setBounds(bounds);
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {

    var el = document.createElement('li'),
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
                '<div class="info">' +
                '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
                    '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>';
    }

      itemStr += '  <span class="tel">' + places.phone  + '</span>' +
                '</div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

 //마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, idx, title) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions =  {
            spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
            marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker);  // 배열에 생성된 마커를 추가합니다

    return marker;
}


// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

// 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
// 인포윈도우에 장소명을 표시합니다
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild (el.lastChild);
    }
}









/*
<div class="option">
                <div>
                    <form onsubmit="searchPlaces(); return false;">
                        키워드 : <input type="text" id="keyword" size="15">
                        <button type="submit">검색하기</button>
                    </form>
                </div>
            </div>
            <hr>
            <ul id="placesList"></ul>
            <div id="pagination"></div>*/
