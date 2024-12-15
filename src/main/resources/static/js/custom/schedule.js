var tagify = new Tagify(document.getElementById('hashtagInput')); // tagify 라이브러리 적용
let calendar; // 캘린더 객체 선언

document.addEventListener('DOMContentLoaded', async function () {
    initSelect();
    await initCalendar();
    await getSchedule();
});

async function initCalendar() {
    const calendarEl = document.getElementById('timetable');
    calendar = new FullCalendar.Calendar(calendarEl, {
        timeZone: 'Asia/Seoul',
        locale: 'ko',
        initialView: 'timeGridWeek',
        slotMinTime: '08:00:00',
        editable: true,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        eventDrop: function (info) {
            updateEvent(info);
        },
        eventResize: function (info) {
            updateEvent(info);
        },
        eventAdd: function (info) {
            if(info.event.extendedProps.notAdd){
                return;
            }
            addEvent(info);

        },
        eventChange: function (info) {
            updateEvent(info);
        },
        eventRemove: function (info) {
            removeEvent(info);
        },
        eventClick: function (info) {
            if (confirm('이 일정을 삭제하시겠습니까?')) {
                info.event.remove(); // 클릭한 이벤트 삭제
            }
        },
    });

    calendar.render();
}

document.getElementById('addSchedule').addEventListener('click', () => {
    addSchedule();
});

document.getElementById('aiBtn').addEventListener('click', () => {
    generateSchedule();
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

// 캘린더에 일정 추가
function addSchedule() {
    const scheduleForm = document.getElementById('scheduleForm');

    const title = scheduleForm.title.value;
    const date = scheduleForm.date.value;
    const startTime = scheduleForm.startTime.value;
    const endTime = scheduleForm.endTime.value;

    // 선택된 색상 가져오기
    let selectedColor = document.querySelector('input[name="color"]:checked').value;

    calendar.addEvent({
        title: title,
        start: date + "T" + startTime,
        end: date + "T" + endTime,
        backgroundColor: selectedColor,
        borderColor: selectedColor,
        textColor: '#333333'
    });
}

// ai 캘린더 자동생성
function generateSchedule() {
    showLoading();


    fetch('/schedule/generate', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({prompt: document.querySelector('#prompt').value})
    })
        .then(response => response.json())
        .then(data => {
            hideLoading();
            const travelEvents = data.travelEvents;
            travelEvents.forEach(event => {
                const color=getRandomColor();

                calendar.addEvent({
                    title: event.title,
                    start: event.start,
                    end: event.end,
                    backgroundColor: color,
                    borderColor: color,
                    textColor: '#333333'
                });
            });
        })
        .catch(error => {
            console.error('일정 생성 중 오류:', error);
            alert('일정 생성 중 오류가 발생했습니다.');
            hideLoading();
        });
}

// 로그인유저 스케줄 조회
async function getSchedule() {
    fetch('/schedule/get')
        .then(response => {
            if (!response.ok) {
                throw new Error('스케줄 데이터를 가져오는 데 실패했습니다.');
            }
            return response.json();
        })
        .then(data => {
            console.log(data)

            for (const schedule of data) {
                console.log(schedule)
                calendar.addEvent({
                    title: schedule.title,
                    start: schedule.startTime,
                    end: schedule.endTime,
                    backgroundColor: schedule.color,
                    borderColor: schedule.color,
                    textColor: '#333333',
                    extendedProps: {
                        notAdd: true
                    }
                });
            }
            calendar.render();
        })
        .catch(error => {
            console.error('스케줄 데이터를 로드하는 중 오류 발생:', error);
        });

}

function getRandomColor() {
    const colors = ['#D6E9F0', '#FADADD', '#D4E7D4'];
    const randomIndex = Math.floor(Math.random() * colors.length);
    return colors[randomIndex];
}

// 일정 추가
function addEvent(info) {
    fetch('/schedule/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: info.event.title,
            startTime: info.event.start.toISOString(),
            endTime: info.event.end.toISOString(),
            color: info.event.backgroundColor,
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('추가된 일정:', data);
        })
        .catch(error => console.error('일정 추가 중 오류 발생:', error));
}

// 일정 수정
function updateEvent(info) {
    fetch('/schedule/update', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            oldStartTime: info.oldEvent.start.toISOString(),
            oldEndTime: info.oldEvent.end.toISOString(),
            title: info.event.title,
            startTime: info.event.start.toISOString(),
            endTime: info.event.end.toISOString(),
            color:info.event.backgroundColor
        })
    })
        .then(response => response.json())
        .then(data => console.log('업데이트된 일정:', data))
        .catch(error => console.error('일정 업데이트 중 오류 발생:', error));
}

// 일정 삭제
function removeEvent(info) {
    const params = new URLSearchParams({
        title: info.event.title,
        startTime: info.event.start.toISOString(),
        endTime: info.event.end ? info.event.end.toISOString() : ''
    });

    fetch(`/schedule/delete?${params.toString()}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorData => {
                    throw new Error(errorData.message || '삭제 실패');
                });
            }
            return response.json();
        })
        .then(data => console.log('삭제 완료:', data))
        .catch(error => console.error('삭제 중 오류 발생:', error));

}


// Initialize Speech Recognition
const recognition = new webkitSpeechRecognition();
recognition.lang = 'ko-KR'; // Set language (change to 'ko-KR' for Korean)
recognition.interimResults = true; // Show interim results
recognition.continuous = true; // Keep listening until stopped

// Variables
let isListening = false; // Tracks whether listening is active
let timeout = null; // Timer for auto stop
const speechBtn = document.getElementById('speechBtn');
const btnIcon = speechBtn.querySelector('i'); // 아이콘 엘리먼트 선택

// Start/Stop toggle
speechBtn.addEventListener('click', () => {
    if (isListening) {
        recognition.stop();
    } else {
        recognition.start();
    }
});

// On start
recognition.onstart = () => {
    isListening = true;
    btnIcon.classList.remove('icon-microphone'); // 마이크 아이콘 제거
    btnIcon.classList.add('icon-stop'); // 중지 아이콘 추가
    console.log('Speech recognition started');
};

// On end
recognition.onend = () => {
    isListening = false;
    btnIcon.classList.remove('icon-stop'); // 중지 아이콘 제거
    btnIcon.classList.add('icon-microphone'); // 마이크 아이콘 추가
    clearTimeout(timeout); // Clear the timeout
    console.log('Speech recognition stopped');
};

// On result
recognition.onresult = (event) => {
    let transcript = '';
    for (let i = event.resultIndex; i < event.results.length; i++) {
        transcript += event.results[i][0].transcript;
    }
    const prompt = document.getElementById('prompt');
    prompt.innerText = transcript;

    resetTimer();
};

// On error
recognition.onerror = (event) => {
    console.error('Speech recognition error:', event.error);
    recognition.stop(); // Stop on error
};

// Timer to stop recognition if no speech is detected for 3 seconds
function resetTimer() {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
        recognition.stop();
        console.log('No speech detected for 3 seconds. Stopping...');
    }, 3000); // 3 seconds
}


