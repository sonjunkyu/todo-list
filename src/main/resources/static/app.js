const API_URL = '/api/todos';
const form = document.getElementById('createForm');
const list = document.getElementById('list');

function renderList(todos) {
    if (!list) return;
    list.innerHTML = '';
    todos.forEach(todo => {
        const li = document.createElement('li');
        // Store the raw todo data on the element for easy access
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
        const res = await fetch(API_URL);
        if (!res.ok) {
            if (res.status === 401) location.href = '/login';
            throw new Error('Failed to fetch todos');
        }
        const todos = await res.json();
        renderList(todos);
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
        const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
        if (res.ok) {
            resetForm();
            await fetchTodos();
        } else {
            alert(`Failed to ${editId ? 'update' : 'create'} todo`);
        }
    } catch (error) {
        console.error(error);
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
            fetch(`${API_URL}/${todo.id}`, { method: 'DELETE' })
                .then(res => {
                    if (res.ok) {
                        li.remove(); // Optimistic update
                    } else {
                        alert('삭제 실패');
                    }
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
        return; // Not a relevant change
    }

    try {
        const res = await fetch(`${API_URL}/${todo.id}`, { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
        if (res.ok) {
            // A full re-fetch is a bit heavy, let's update the data in place
            const updatedTodo = await res.json();
            li.dataset.todo = JSON.stringify(updatedTodo);
            li.className = updatedTodo.completed ? 'done' : '';
        } else {
            alert('업데이트 실패');
            // Revert optimistic change if needed
            if (body.completed !== undefined) target.checked = !target.checked;
            if (body.dueDate !== undefined) target.value = todo.dueDate;
        }
    } catch (error) {
        console.error(error);
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
