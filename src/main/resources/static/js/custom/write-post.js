let tagify = new Tagify(document.getElementById('hashtagInput')); // 해시태그 라이브러리 적용
let editorInstance;     // ckeditor 인스턴스

// ckeditor 적용
ClassicEditor
    .create(document.querySelector('#editor'), {
        language: 'ko',
        ckfinder: {
            uploadUrl: '/post/upload'
        }
    }).then(editor => {
    editorInstance = editor;
    // 파일 크기 제한: 10MB
    const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    editor.plugins.get('FileRepository').createUploadAdapter = loader => {
        return {
            upload: () => {
                return loader.file.then(file => {
                    if (file.size > MAX_FILE_SIZE) {
                        alert('파일 크기가 10MB를 초과했습니다.');
                        return Promise.reject(new Error('파일 크기가 10MB를 초과했습니다.'));
                    }

                    // 서버에 파일 업로드
                    const data = new FormData();
                    data.append('file', file);

                    return fetch('/post/upload', {
                        method: 'POST',
                        body: data,
                    })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(error => {
                                    throw new Error(error.message || '파일 업로드 실패');
                                });
                            }
                            return response.json();
                        })
                        .then(result => {
                            return {
                                default: result.url, // 업로드된 파일 URL
                            };
                        })
                        .catch(error => {
                            console.error('업로드 오류:', error);
                            alert(error.message || '파일 업로드 중 오류가 발생했습니다.');
                            return Promise.reject(error);
                        });
                });
            },
        };
    };
}).catch(error => {
    console.error(error);
});


// ai 자동생성
document.getElementById('generatePostButton').addEventListener('click', async () => {
    const prompt = document.getElementById('promptInput').value;

    if (!prompt.trim()) {
        alert('프롬프트를 입력하세요!');
        return;
    }

    showLoading();

    // 서버로 프롬프트 전달
    try {
        const response = await fetch('/post/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({prompt}),
        });

        const result = await response.json();
        if (response.ok) {
            const currentContent = editorInstance.getData();
            const newContent = `<p>${result.post}</p>`;
            const updatedContent = currentContent + newContent;
            editorInstance.setData(updatedContent);

            document.getElementById('promptInput').value = "";
            hideLoading();

        } else {
            alert('오류 발생: ' + result.message);
            hideLoading();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('게시글 생성 중 오류가 발생했습니다.');
        hideLoading();
    }
});

document.getElementById('generateTags').addEventListener('click', async () => {
    const editorContent = editorInstance.getData();

    if (!editorContent) {
        alert("후기를 작성해주세요")
        return;
    }

    showLoading();

    try {
        const response = await fetch('/post/generate-tags', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({review: editorContent})
        });

        const data = await response.json();
        if (response.ok) {
            const recommendedTags = data.tags;
            tagify.addTags(recommendedTags);
            hideLoading();
        } else {
            alert('오류 발생: ' + result.message);
            hideLoading();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('해시태그 생성 중 오류가 발생했습니다.');
        hideLoading();
    }
});

