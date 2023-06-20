$(document).ready(function() {
  var selectedDate = null; // 선택된 날짜 변수 초기화

  $('#calendar').fullCalendar({
    dayClick: function(date, jsEvent, view) {
      if (moment(date).isBefore(moment(), 'day')) {
        return false; // 선택할 수 없는 지난 날짜인 경우
      }

      $('#calendar .fc-selected').removeClass('fc-selected'); // 기존 선택 해제
      $(this).addClass('fc-selected'); // 선택한 날짜에 표시 추가

      selectedDate = moment(date).format('YYYY-MM-DD'); // 선택된 날짜 저장

      updateWeatherInfo();
    },
    dayRender: function(date, cell) {
      if (moment(date).isBefore(moment(), 'day')) {
        cell.addClass('fc-past'); // 지난 날짜는 스타일 적용
      }
    },
    header: {
      left: 'prev,next',
      center: 'title',
      right: 'today'
    },
    titleFormat: 'YYYY년 M월', // 날짜 형식 변경
    dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
    monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
  });

  function updateWeatherInfo() {
    if (selectedDate) {
      // 날짜를 선택한 경우에만 해당 정보 표시
      $('#selected-date').text(selectedDate);
      $('#weather-info').show();
    } else {
      // 날짜를 선택하지 않은 경우 정보 숨김
      $('#selected-date').text('');
      $('#weather-info').hide();
    }

    // 선택된 날짜가 변경되었을 때 API를 통해 날씨 정보 가져오기
    if (selectedDate) {
      // 초기화
      $('#weather-description').text('');
      $('#temperature').text('');
      $('#humidity').text('');
      $('#error').text('');
      $('.weather-icon').removeClass().addClass('weather-icon'); // 이전 아이콘 클래스 제거

      $.ajax({
        url: 'https://api.openweathermap.org/data/2.5/forecast',
        type: 'GET',
        dataType: 'json',
        data: {
          q: 'Seoul',
          appid: '6e0b43163870f54935b87aa653d3a308',
          units: 'metric'
        },
        success: function(data) {
          var forecasts = data.list;
          var selectedForecast = forecasts.find(function(forecast) {
            return moment(forecast.dt_txt).format('YYYY-MM-DD') === selectedDate;
          });

          if (selectedForecast) {
            $('#weather-description').text('날씨: ' + getWeatherInfo(selectedForecast.weather[0].icon).description);
            $('#temperature').text('온도: ' + selectedForecast.main.temp + ' °C');
            $('#humidity').text('습도: ' + selectedForecast.main.humidity + '%');
            $('#error').text('');
            var weatherIconClass = getWeatherInfo(selectedForecast.weather[0].icon).iconClass;
            $('.weather-icon').addClass(weatherIconClass);
          } else {
            $('#error').text('일주일 간의 날씨 정보만 제공합니다.');
          }
        },
        error: function() {
          $('#weather-description').text('');
          $('#temperature').text('');
          $('#humidity').text('');
          $('#error').text('날씨 정보를 가져오는데 실패했습니다.');
        }
      });
    }
  }

  function getWeatherInfo(iconCode) {
    switch (iconCode) {
      case '01d':
        return { description: '맑음', iconClass: 'wi wi-day-sunny' }; // 맑은 날씨
      case '01n':
        return { description: '맑은 밤', iconClass: 'wi wi-night-clear' }; // 맑은 밤
      case '02d':
        return { description: '구름 조금', iconClass: 'wi wi-day-cloudy' }; // 구름이 조금 있는 날씨
      case '02n':
        return { description: '구름 조금 있는 밤', iconClass: 'wi wi-night-alt-cloudy' }; // 구름이 조금 있는 밤
      case '03d':
      case '03n':
        return { description: '구름', iconClass: 'wi wi-cloud' }; // 구름이 있는 날씨
      case '04d':
      case '04n':
        return { description: '흐림', iconClass: 'wi wi-cloudy' }; // 흐린 날씨
      case '09d':
      case '09n':
        return { description: '소나기', iconClass: 'wi wi-showers' }; // 소나기
      case '10d':
      case '10n':
        return { description: '비', iconClass: 'wi wi-day-rain' }; // 비가 오는 날씨
      case '11d':
        return { description: '천둥번개', iconClass: 'wi wi-day-thunderstorm' }; // 천둥 번개가 치는 날씨
      case '11n':
        return { description: '천둥번개 밤', iconClass: 'wi wi-night-alt-thunderstorm' }; // 천둥 번개가 치는 밤
      case '13d':
        return { description: '눈', iconClass: 'wi wi-day-snow' }; // 눈이 오는 날씨
      case '13n':
        return { description: '눈 오는 밤', iconClass: 'wi wi-night-alt-snow' }; // 눈이 오는 밤
      case '50d':
        return { description: '안개', iconClass: 'wi wi-day-fog' }; // 안개 낀 날씨
      case '50n':
        return { description: '안개 낀 밤', iconClass: 'wi wi-night-fog' }; // 안개 낀 밤
      default:
        return { description: '맑음', iconClass: 'wi wi-day-sunny' }; // 기본적으로 맑은 날씨로 표시
    }
  }

  updateWeatherInfo(); // 페이지 로드 시 초기화
});
