// tagify 라이브러리 적용
var tagify = new Tagify(document.getElementById('hashtagInput'));

let calendar;

document.addEventListener('DOMContentLoaded', function () {
    initSelect();

    const calendarEl = document.getElementById('timetable');

    calendar = new FullCalendar.Calendar(calendarEl, {
        locale: 'ko',
        initialView: 'timeGridWeek',
        slotMinTime: '08:00:00',
        editable: true,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        eventDrop: function(info) {
            // 일정의 날짜가 변경되었을 때 호출
            //updateEvent(info.event);
        },
        eventResize: function(info) {
            // 일정의 시간 범위가 변경되었을 때 호출
            //updateEvent(info.event);
        },
        eventClick: function (info) {
            if (confirm('이 일정을 삭제하시겠습니까?')) {
                info.event.remove(); // 클릭한 이벤트 삭제
            }
        },
        eventAdd: function(info) {
            // 일정이 추가될 때 서버로 비동기 요청
            console.log(info.event.title);
            console.log(info.event.start);
            fetch('/schedule/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: info.event.title,
                    startTime: info.event.start.toISOString(),
                    endTime: info.event.end.toISOString()
                })
            })
                .then(response => response.json())
                .then(data => console.log('추가된 일정:', data))
                .catch(error => console.error('일정 추가 중 오류 발생:', error));
        },
        // eventRemove: function(info) {
        //     // 일정이 삭제될 때 서버로 비동기 요청
        //     fetch(`/schedule/remove/${info.event.id}`, {
        //         method: 'DELETE',
        //         headers: {
        //             'Content-Type': 'application/json'
        //         }
        //     })
        //         .then(response => response.json())
        //         .then(data => console.log('삭제된 일정:', data))
        //         .catch(error => console.error('일정 삭제 중 오류 발생:', error));
        // }
    });

    calendar.render();
});

function initSelect() {
    const startHour = 9;
    const endHour = 23;
    const startTime = document.getElementById("startTime");
    const endTime = document.getElementById("endTime");

    for (let hour = startHour; hour <= endHour; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const time = `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;

            const startOption = document.createElement("option");
            startOption.value = time;
            startOption.textContent = time;
            startTime.appendChild(startOption);

            const endOption = document.createElement("option");
            endOption.value = time;
            endOption.textContent = time;
            endTime.appendChild(endOption);
        }
    }
}

document.getElementById('addSchedule').addEventListener('click', () => {
    const scheduleForm = document.getElementById('scheduleForm');

    const title = scheduleForm.title.value;
    const date = scheduleForm.date.value;
    const startTime = scheduleForm.startTime.value;
    const endTime = scheduleForm.endTime.value;
    console.log(endTime);


    // 선택된 색상 가져오기
    let selectedColor = document.querySelector('input[name="color"]:checked').value;

    calendar.addEvent({
        title: title,
        start: date + "T" + startTime,
        end: date + "T" + endTime,
        backgroundColor: selectedColor,  // 연한 색상 지정
        borderColor: selectedColor,       // 연한 테두리 색상
        textColor: '#333333'
    });
});