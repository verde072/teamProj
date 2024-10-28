// tagify 라이브러리 적용
var tagify = new Tagify(document.getElementById('hashtagInput'), {
    editTags: false,
});


// 폼 제출
function submitForm(mode) {
    let form = document.getElementById('postForm');
    form.action = "/post/" + mode;
    if (mode === 'delete') form.method = 'delete'
    else if (mode === 'write') form.method = 'get'
    form.submit();
}