const postForm = document.getElementById('postForm')

// tagify 라이브러리 적용
var tagify = new Tagify(document.getElementById('hashtagInput'));

// 검색버튼 클릭 이벤트
document.querySelector('#submitBtn').addEventListener('click', submit);

function submit() {
    if (postForm.keyword.value && postForm.key.value === "") {
        alert("검색조건을 선택해주세요");
        postForm.key.focus();
        return false;
    }

    postForm.submit();
}
function openChatPopup(userId) {
    const width = 350;  // 팝업창 너비
    const height = 500; // 팝업창 높이
    const left = (window.innerWidth - width) / 2; // 화면 중앙 정렬
    const top = (window.innerHeight - height) / 2; // 화면 중앙 정렬

    // 팝업창 열기
    window.open(
        `/chat?userId=${userId}`, // URL에 userId 전달
        "ChatWindow", // 창 이름
        `width=${width},height=${height},top=${top},left=${left},resizable=no`
    );
}