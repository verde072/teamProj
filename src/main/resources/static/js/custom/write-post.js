// ckeditor 적용
ClassicEditor
    .create(document.querySelector('#editor'), {
        language: 'ko',
        ckfinder: {
            uploadUrl: '/post/upload'
        }
    }).catch(error => {
    console.error(error);
});

// 해시태그 라이브러리 적용
var tagify = new Tagify(document.getElementById('hashtagInput'));
