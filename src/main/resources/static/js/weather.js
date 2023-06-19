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
            }
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
                            $('#weather-description').text('날씨: ' + selectedForecast.weather[0].description);
                            $('#temperature').text('온도: ' + selectedForecast.main.temp + ' °C');
                            $('#humidity').text('습도: ' + selectedForecast.main.humidity + '%');
                            $('#error').text('');
                            var weatherIconClass = getWeatherIconClass(selectedForecast.weather[0].icon);
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

        function getWeatherIconClass(iconCode) {
            switch (iconCode) {
                case '01d':
                    return 'wi wi-day-sunny';
                case '01n':
                    return 'wi wi-night-clear';
                case '02d':
                    return 'wi wi-day-cloudy';
                case '02n':
                    return 'wi wi-night-alt-cloudy';
                case '03d':
                case '03n':
                    return 'wi wi-cloud';
                case '04d':
                case '04n':
                    return 'wi wi-cloudy';
                case '09d':
                case '09n':
                    return 'wi wi-showers';
                case '10d':
                    return 'wi wi-day-rain';
                case '10n':
                    return 'wi wi-night-alt-rain';
                case '11d':
                    return 'wi wi-day-thunderstorm';
                case '11n':
                    return 'wi wi-night-alt-thunderstorm';
                case '13d':
                    return 'wi wi-day-snow';
                case '13n':
                    return 'wi wi-night-alt-snow';
                case '50d':
                    return 'wi wi-day-fog';
                case '50n':
                    return 'wi wi-night-fog';
                default:
                    return 'wi wi-day-sunny';
            }
        }

        updateWeatherInfo(); // 페이지 로드 시 초기화
    });