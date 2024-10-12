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