const API_URL = '/api/todos';
const form = document.getElementById('createForm');
const list = document.getElementById('list');

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

async function apiFetch(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken,
        ...options.headers,
    };
    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
        if (response.status === 401) {
            location.href = '/login';
        }
        throw new Error(`API request failed with status ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }
    return response.json();
}

function renderList(todos) {
    if (!list) return;
    list.innerHTML = '';
    todos.forEach(todo => {
        const li = document.createElement('li');

        li.dataset.todo = JSON.stringify(todo);
        li.className = todo.completed ? 'done' : '';

        li.innerHTML = `
            <div class="todo-header">
                <input type="checkbox" ${todo.completed ? 'checked' : ''} class="toggle">
                <h3>${todo.title}</h3>
            </div>
            <p class="description">${todo.description ?? ''}</p>
            <div class="meta">
                <span>생성: ${todo.createdAt?.replace('T', ' ').slice(0, 16)}</span>
                <span>마감: <input type="date" value="${todo.dueDate}" class="due"></span>
            </div>
            <div class="actions">
                <button class="edit">수정</button>
                <button class="delete">삭제</button>
            </div>
        `;
        list.appendChild(li);
    });
}

function resetForm() {
    if (!form) return;
    form.reset();
    form.querySelector('#editId').value = '';
    form.querySelector('h2').textContent = 'Todo 생성';
    const submitButton = form.querySelector('button[type="submit"]');
    submitButton.textContent = '추가';

    const cancelButton = form.querySelector('.cancel-edit');
    if (cancelButton) {
        cancelButton.remove();
    }
}

function startEditMode(todo) {
    if (!form) return;
    form.querySelector('#editId').value = todo.id;
    form.querySelector('#title').value = todo.title;
    form.querySelector('#description').value = todo.description;
    form.querySelector('#dueDate').value = todo.dueDate;
    form.querySelector('h2').textContent = 'Todo 수정';

    const submitButton = form.querySelector('button[type="submit"]');
    submitButton.textContent = '저장';

    const actions = form.querySelector('.form-actions');
    if (!actions.querySelector('.cancel-edit')) {
        const cancelButton = document.createElement('button');
        cancelButton.type = 'button';
        cancelButton.textContent = '취소';
        cancelButton.className = 'btn btn-secondary cancel-edit';
        cancelButton.onclick = resetForm;
        actions.appendChild(cancelButton);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function fetchTodos() {
    try {
        const todos = await apiFetch(API_URL);
        if (todos) {
            renderList(todos);
        }
    } catch (error) {
        console.error(error);
    }
}

async function handleFormSubmit(e) {
    e.preventDefault();
    const editId = form.querySelector('#editId').value;
    const body = {
        title: form.querySelector('#title').value.trim(),
        description: form.querySelector('#description').value.trim(),
        dueDate: form.querySelector('#dueDate').value
    };

    const url = editId ? `${API_URL}/${editId}` : API_URL;
    const method = editId ? 'PATCH' : 'POST';

    try {
        await apiFetch(url, { method, body: JSON.stringify(body) });
        resetForm();
        await fetchTodos();
    } catch (error) {
        console.error(error);
        alert(`Failed to ${editId ? 'update' : 'create'} todo`);
    }
}

function handleListClick(e) {
    const target = e.target;
    const li = target.closest('li');
    if (!li) return;

    const todo = JSON.parse(li.dataset.todo);

    if (target.classList.contains('edit')) {
        startEditMode(todo);
    }

    if (target.classList.contains('delete')) {
        if (confirm('정말 삭제하시겠습니까?')) {
            apiFetch(`${API_URL}/${todo.id}`, { method: 'DELETE' })
                .then(() => {
                    li.remove(); // Optimistic update
                })
                .catch(err => {
                    console.error(err);
                    alert('삭제 실패');
                });
        }
    }
}

async function handleListChange(e) {
    const target = e.target;
    const li = target.closest('li');
    if (!li) return;

    const todo = JSON.parse(li.dataset.todo);
    let body = {};

    if (target.classList.contains('toggle')) {
        body.completed = target.checked;
    } else if (target.classList.contains('due')) {
        body.dueDate = target.value;
    } else {
        return;
    }

    try {
        const updatedTodo = await apiFetch(`${API_URL}/${todo.id}`, { method: 'PATCH', body: JSON.stringify(body) });
        li.dataset.todo = JSON.stringify(updatedTodo);
        li.className = updatedTodo.completed ? 'done' : '';
    } catch (error) {
        console.error(error);
        alert('업데이트 실패');

        if (body.completed !== undefined) target.checked = !target.checked;
        if (body.dueDate !== undefined) target.value = todo.dueDate;
    }
}

if (form) {
    form.addEventListener('submit', handleFormSubmit);
}
if (list) {
    list.addEventListener('click', handleListClick);
    list.addEventListener('change', handleListChange);
}

fetchTodos();
