const pages = {
    home: () => `
        <div class="card">
            <h2 class="card-title">Добро пожаловать в BookLoverBox!</h2>
            <p>Платформа для начинающих авторов и читателей.</p>
            <p>Публикуйте свои произведения, читайте книги других авторов, оставляйте отзывы.</p>
        </div>
        <div id="booksContainer"></div>
    `,

    login: () => `
        <div class="card" style="max-width: 400px; margin: 2rem auto;">
            <h2 class="card-title">Вход</h2>
            <form onsubmit="handleLogin(event)">
                <div class="form-group">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-input" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Пароль</label>
                    <input type="password" name="password" class="form-input" required>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Войти</button>
            </form>
        </div>
    `,

    register: () => `
        <div class="card" style="max-width: 400px; margin: 2rem auto;">
            <h2 class="card-title">Регистрация</h2>
            <form onsubmit="handleRegister(event)">
                <div class="form-group">
                    <label class="form-label">Полное имя</label>
                    <input type="text" name="fullName" class="form-input" required placeholder="Иван Иванов">
                </div>
                <div class="form-group">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-input" required placeholder="ivan@example.com">
                </div>
                <div class="form-group">
                    <label class="form-label">Пароль</label>
                    <input type="password" name="password" class="form-input" required minlength="6">
                </div>
                <div class="form-group">
                    <label class="form-label">
                        <input type="checkbox" name="wantsToBeAuthor" style="margin-right: 8px;">
                        Хочу стать автором
                    </label>
                    <small style="display: block; margin-top: 4px; color: var(--secondary-color);">
                        Авторы могут публиковать свои книги
                    </small>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Зарегистрироваться</button>
                <p style="text-align: center; margin-top: 1rem;">
                    Уже есть аккаунт? <a href="#" onclick="event.preventDefault(); showPage('login')" style="color: var(--primary-color);">Войти</a>
                </p>
            </form>
        </div>
    `,

    books: () => `
        <div class="card">
            <h2 class="card-title">Каталог книг</h2>
            <div class="search-bar">
                <input type="text" id="searchInput" class="search-input" placeholder="Поиск книг по названию..." oninput="searchBooks(this.value)">
            </div>
        </div>
        <div id="booksContainer"></div>
    `,

    mybooks: () => `
        <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <h2 class="card-title" style="margin: 0;">Мои книги</h2>
                <button onclick="event.preventDefault(); showPage('addbook')" class="btn btn-primary">Добавить книгу</button>
            </div>
        </div>
        <div id="myBooksContainer"></div>
    `,

    addbook: () => `
        <div class="card" style="max-width: 800px; margin: 2rem auto;">
            <h2 class="card-title">Добавить новую книгу</h2>
            <form onsubmit="handleAddBook(event)" id="addBookForm">
                <div class="form-group">
                    <label class="form-label">Название книги *</label>
                    <input type="text" name="title" class="form-input" required maxlength="200" 
                           placeholder="Мастер и Маргарита">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Автор *</label>
                    <input type="text" name="authorName" class="form-input" required maxlength="200"
                           placeholder="Булгаков, Михаил Афанасьевич">
                    <small style="color: #666;">Формат: Фамилия, Имя Отчество</small>
                </div>
                
                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Жанр *</label>
                        <select name="genreId" class="form-input" required id="genreSelect">
                            <option value="">Выберите жанр...</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Формат файла</label>
                        <textarea name="content" class="form-input" rows="10" 
                              placeholder="Вставте текст"></textarea>
                        
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Описание / Аннотация</label>
                    <textarea name="description" class="form-input" rows="5" 
                              placeholder="Краткое описание книги..."></textarea>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Теги</label>
                    <input type="text" name="tags" class="form-input" 
                           placeholder="мистика, сатира, классика">
                    <small style="color: #666;">Через запятую, до 10 тегов</small>
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Язык</label>
                        <select name="language" class="form-input">
                            <option value="Русский" selected>Русский</option>
                            <option value="Английский">Английский</option>
                            <option value="Другой">Другой</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Год издания</label>
                        <input type="number" name="publicationYear" class="form-input" 
                               min="1000" max="2100" placeholder="2023">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Возраст</label>
                        <select name="ageRating" class="form-input">
                            <option value="0+" selected>0+</option>
                            <option value="6+">6+</option>
                            <option value="12+">12+</option>
                            <option value="16+">16+</option>
                            <option value="18+">18+</option>
                        </select>
                    </div>
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Издательство</label>
                        <input type="text" name="publisher" class="form-input" 
                               placeholder="Художественная литература">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Страниц</label>
                        <input type="number" name="pageCount" class="form-input" 
                               min="1" placeholder="384">
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">URL обложки</label>
                    <input type="url" name="coverUrl" class="form-input" 
                           placeholder="https://example.com/cover.jpg">
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Правообладатель</label>
                        <input type="text" name="copyrightHolder" class="form-input" 
                               placeholder="Общественное достояние">
                    </div>
                    <div class="form-group">
                        <label class="form-label">ISBN</label>
                        <input type="text" name="isbn" class="form-input" 
                               placeholder="978-5-17-123456-7">
                    </div>
                </div>
                
                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary">Создать книгу</button>
                    <button type="button" onclick="event.preventDefault(); showPage('mybooks')" 
                            class="btn btn-outline">Отмена</button>
                </div>
            </form>
        </div>
    `,

    moderation: () => `
        <div class="card">
            <h2 class="card-title">Модерация книг</h2>
            <p>Книги, ожидающие модерации</p>
        </div>
        <div id="moderationContainer"></div>
    `,

    admin: () => `
        <div class="card">
            <h2 class="card-title">Панель администратора</h2>
        </div>
        <div id="statsContainer"></div>
        <div class="card">
            <h3 class="card-title">Действия</h3>
            <button onclick="exportBooks()" class="btn btn-primary">Экспорт книг в CSV</button>
        </div>
        <div id="usersContainer"></div>
    `,
    
    bookdetail: () => `
        <div id="bookDetailContainer">
            <div class="loading">Загрузка...</div>
        </div>
    `
};

function showPage(pageName) {
    const content = document.getElementById('pageContent');
    content.innerHTML = pages[pageName] ? pages[pageName]() : '<p>Страница не найдена</p>';

    switch(pageName) {
        case 'home':
        case 'books':
            loadBooks();
            break;
        case 'mybooks':
            loadMyBooks();
            break;
        case 'addbook':
            loadGenresForForm();
            break;
        case 'moderation':
            loadPendingBooks();
            break;
        case 'admin':
            loadAdminData();
            break;
    }
}

async function loadBooks() {
    const container = document.getElementById('booksContainer');
    container.innerHTML = '<div class="loading">Загрузка...</div>';

    try {
        const response = await api.books.getAll();
        // API возвращает Page объект с полем content
        const books = response.content || response;
        
        if (!books || !Array.isArray(books) || books.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📚</div><p>Книги не найдены</p></div>';
            return;
        }

        container.innerHTML = `<div class="grid grid-3">${books.map(book => createBookCard(book)).join('')}</div>`;
    } catch (error) {
        console.error('Error loading books:', error);
        container.innerHTML = `<div class="alert alert-error">Ошибка загрузки: ${error.message}</div>`;
    }
}

async function loadMyBooks() {
    const container = document.getElementById('myBooksContainer');
    container.innerHTML = '<div class="loading">Загрузка...</div>';

    try {
        const response = await api.books.getMy();
        // API возвращает Page объект с полем content
        const books = response.content || response;
        
        if (!books || !Array.isArray(books) || books.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📚</div><p>У вас пока нет книг</p></div>';
            return;
        }

        container.innerHTML = `<div class="grid grid-3">${books.map(book => createMyBookCard(book)).join('')}</div>`;
    } catch (error) {
        console.error('Error loading my books:', error);
        container.innerHTML = `<div class="alert alert-error">Ошибка загрузки: ${error.message}</div>`;
    }
}

async function loadPendingBooks() {
    const container = document.getElementById('moderationContainer');
    container.innerHTML = '<div class="loading">Загрузка...</div>';

    try {
        const response = await api.moderation.getPending();
        // API возвращает Page объект с полем content
        const books = response.content || response;
        
        if (!books || !Array.isArray(books) || books.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">✅</div><p>Нет книг на модерации</p></div>';
            return;
        }

        container.innerHTML = `<div class="grid grid-2">${books.map(book => createModerationCard(book)).join('')}</div>`;
    } catch (error) {
        console.error('Error loading pending books:', error);
        container.innerHTML = `<div class="alert alert-error">Ошибка загрузки: ${error.message}</div>`;
    }
}

async function loadAdminData() {
    try {
        const stats = await api.statistics.getOverview();
        const users = await api.statistics.getUsers();

        document.getElementById('statsContainer').innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value">${stats.totalBooks || 0}</div>
                    <div class="stat-label">Всего книг</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.totalUsers || 0}</div>
                    <div class="stat-label">Пользователей</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.totalReviews || 0}</div>
                    <div class="stat-label">Отзывов</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${stats.pendingBooks || 0}</div>
                    <div class="stat-label">На модерации</div>
                </div>
            </div>
        `;

        document.getElementById('usersContainer').innerHTML = `
            <div class="card">
                <h3 class="card-title">Статистика пользователей</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid var(--border-color);">
                            <th style="padding: 0.75rem; text-align: left;">Пользователь</th>
                            <th style="padding: 0.75rem; text-align: left;">Книг</th>
                            <th style="padding: 0.75rem; text-align: left;">Отзывов</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${users.map(user => `
                            <tr style="border-bottom: 1px solid var(--border-color);">
                                <td style="padding: 0.75rem;">${user.userName || user.userEmail}</td>
                                <td style="padding: 0.75rem;">${user.totalBooks || 0}</td>
                                <td style="padding: 0.75rem;">${user.totalReviews || 0}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        showAlert('Ошибка загрузки данных: ' + error.message, 'error');
    }
}

function createBookCard(book) {
    const statusBadge = book.status === 'PUBLISHED' ? 
        '<span class="badge badge-success">Опубликовано</span>' : 
        '<span class="badge badge-warning">На модерации</span>';

    return `
        <div class="book-card">
            <div class="book-title">${book.title}</div>
            <div class="book-author">Автор: ${book.authorName || 'Неизвестен'}</div>
            <div class="book-genre">${book.genreName || 'Без жанра'}</div>
            ${statusBadge}
            <div class="book-description">${book.description || 'Нет описания'}</div>
            <div class="book-stats">
                <span>⭐ ${book.averageRating ? book.averageRating.toFixed(1) : 'Нет оценок'}</span>
                <span>👁 ${book.viewsCount || 0}</span>
                <span>💬 ${book.reviewsCount || 0}</span>
            </div>
            <div class="book-actions">
                <button onclick="showBookDetail(${book.id})" class="btn btn-primary">Подробнее</button>
            </div>
        </div>
    `;
}

function createMyBookCard(book) {
    const statusBadge = {
        'PUBLISHED': '<span class="badge badge-success">Опубликовано</span>',
        'PENDING': '<span class="badge badge-warning">На модерации</span>',
        'REJECTED': '<span class="badge badge-danger">Отклонено</span>',
        'DRAFT': '<span class="badge badge-info">Черновик</span>'
    }[book.status] || '';

    return `
        <div class="book-card">
            <div class="book-title">${book.title}</div>
            <div class="book-genre">${book.genreName || 'Без жанра'}</div>
            ${statusBadge}
            <div class="book-description">${book.description || 'Нет описания'}</div>
            <div class="book-stats">
                <span>⭐ ${book.averageRating ? book.averageRating.toFixed(1) : 'Нет оценок'}</span>
                <span>👁 ${book.viewsCount || 0}</span>
                <span>💬 ${book.reviewsCount || 0}</span>
            </div>
            <div class="book-actions">
                <button onclick="showBookDetail(${book.id})" class="btn btn-primary">Подробнее</button>
                <button onclick="deleteBook(${book.id})" class="btn btn-danger">Удалить</button>
            </div>
        </div>
    `;
}

function createModerationCard(book) {
    const description = (book.description || 'Нет описания').replace(/'/g, "\\'").replace(/"/g, '&quot;');
    return `
        <div class="book-card">
            <div class="book-title">${book.title}</div>
            <div class="book-author">Автор: ${book.authorName || 'Неизвестен'}</div>
            <div class="book-genre">${book.genreName || 'Без жанра'}</div>
            <div class="book-description">${book.description || 'Нет описания'}</div>
            <div class="book-actions">
                <button onclick="showModerateModal(${book.id}, \`${description}\`)" class="btn btn-primary">Модерировать</button>
                <button onclick="showBookDetail(${book.id})" class="btn btn-outline">Подробнее</button>
            </div>
        </div>
    `;
}

async function searchBooks(query) {
    if (!query.trim()) {
        loadBooks();
        return;
    }

    const container = document.getElementById('booksContainer');
    container.innerHTML = '<div class="loading">Поиск...</div>';

    try {
        const response = await api.books.search(query);
        // API возвращает Page объект с полем content
        const books = response.content || response;
        
        if (!books || !Array.isArray(books) || books.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="empty-state-icon">🔍</div><p>Ничего не найдено</p></div>';
            return;
        }

        container.innerHTML = `<div class="grid grid-3">${books.map(book => createBookCard(book)).join('')}</div>`;
    } catch (error) {
        console.error('Error searching books:', error);
        container.innerHTML = `<div class="alert alert-error">Ошибка поиска: ${error.message}</div>`;
    }
}

async function approveBook(bookId) {
    try {
        await api.moderation.approve(bookId);
        showAlert('Книга одобрена', 'success');
        loadPendingBooks();
    } catch (error) {
        showAlert('Ошибка: ' + error.message, 'error');
    }
}

async function rejectBook(bookId) {
    const reason = prompt('Причина отклонения:');
    if (!reason) return;

    try {
        await api.moderation.reject(bookId, reason);
        showAlert('Книга отклонена', 'success');
        loadPendingBooks();
    } catch (error) {
        showAlert('Ошибка: ' + error.message, 'error');
    }
}

async function deleteBook(bookId) {
    if (!confirm('Удалить книгу?')) return;

    try {
        await api.books.delete(bookId);
        showAlert('Книга удалена', 'success');
        loadMyBooks();
    } catch (error) {
        showAlert('Ошибка: ' + error.message, 'error');
    }
}

async function exportBooks() {
    try {
        await api.batch.exportBooks();
        showAlert('Экспорт завершен', 'success');
    } catch (error) {
        showAlert('Ошибка экспорта: ' + error.message, 'error');
    }
}

async function loadGenresForForm() {
    try {
        const genres = await api.genres.getAll();
        const select = document.getElementById('genreSelect');
        if (select && genres && Array.isArray(genres)) {
            genres.forEach(genre => {
                const option = document.createElement('option');
                option.value = genre.id;
                option.textContent = genre.name;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading genres:', error);
        showAlert('Ошибка загрузки жанров', 'error');
    }
}

async function handleAddBook(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    
    // Функция для преобразования пустых строк в null
    const getValueOrNull = (value) => {
        if (!value || value.trim() === '') return null;
        return value;
    };
    
    const bookData = {
        title: formData.get('title'),
        authorName: formData.get('authorName'),
        description: getValueOrNull(formData.get('description')),
        genreId: parseInt(formData.get('genreId')),
        tags: getValueOrNull(formData.get('tags')),
        content: getValueOrNull(formData.get('content')),
        language: formData.get('language') || 'Русский',
        publicationYear: formData.get('publicationYear') ? parseInt(formData.get('publicationYear')) : null,
        publisher: getValueOrNull(formData.get('publisher')),
        pageCount: formData.get('pageCount') ? parseInt(formData.get('pageCount')) : null,
        ageRating: formData.get('ageRating') || '0+',
        coverUrl: getValueOrNull(formData.get('coverUrl')),
        copyrightHolder: getValueOrNull(formData.get('copyrightHolder')),
        isbn: getValueOrNull(formData.get('isbn'))
    };
    
    try {
        await api.books.create(bookData);
        showAlert('Книга успешно создана!', 'success');
        showPage('mybooks');
    } catch (error) {
        console.error('Error creating book:', error);
        showAlert('Ошибка создания книги: ' + error.message, 'error');
    }
}

// Детальный просмотр книги
let currentBookId = null;

async function showBookDetail(bookId) {
    currentBookId = bookId;
    showPage('bookdetail');
    
    const container = document.getElementById('bookDetailContainer');
    container.innerHTML = '<div class="loading">Загрузка...</div>';
    
    try {
        const book = await api.books.getById(bookId);
        const reviews = await api.reviews.getByBook(bookId);
        const reviewsList = reviews.content || reviews || [];
        
        container.innerHTML = `
            <div class="card">
                <button onclick="history.back()" class="btn btn-outline" style="margin-bottom: 20px;">← Назад</button>
                
                <div class="book-detail">
                    ${book.coverUrl ? `<img src="${book.coverUrl}" alt="${book.title}" class="book-detail-cover">` : ''}
                    
                    <div class="book-detail-info">
                        <h1>${book.title}</h1>
                        <p class="book-author">Автор: ${book.authorName}</p>
                        
                        <div class="book-meta">
                            <span><strong>Жанр:</strong> ${book.genreName || 'Не указан'}</span>
                            <span><strong>Год издания:</strong> ${book.publicationYear || 'Не указан'}</span>
                            <span><strong>Страниц:</strong> ${book.pageCount || 'Не указано'}</span>
                            <span><strong>Язык:</strong> ${book.language || 'Русский'}</span>
                            <span><strong>Возрастной рейтинг:</strong> ${book.ageRating || '0+'}</span>
                        </div>
                        
                        <div class="book-description">
                            <h3>Описание</h3>
                            <p>${book.description || 'Описание отсутствует'}</p>
                        </div>
                        
                        ${book.publisher ? `<p><strong>Издательство:</strong> ${book.publisher}</p>` : ''}
                        ${book.isbn ? `<p><strong>ISBN:</strong> ${book.isbn}</p>` : ''}
                        ${book.tags ? `<p><strong>Теги:</strong> ${book.tags}</p>` : ''}
                    </div>
                </div>
            </div>
            
            <div class="card">
                <h3>Отзывы (${reviewsList.length})</h3>
                
                <div class="review-form" style="margin-bottom: 20px;">
                    <h4>Оставить отзыв</h4>
                    <form onsubmit="handleAddReview(event, ${bookId})">
                        <div class="form-group">
                            <label>Оценка</label>
                            <select name="rating" required class="form-control">
                                <option value="5">⭐⭐⭐⭐⭐ Отлично</option>
                                <option value="4">⭐⭐⭐⭐ Хорошо</option>
                                <option value="3">⭐⭐⭐ Нормально</option>
                                <option value="2">⭐⭐ Плохо</option>
                                <option value="1">⭐ Ужасно</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Комментарий</label>
                            <textarea name="comment" rows="4" required class="form-control" placeholder="Ваш отзыв о книге..."></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Отправить отзыв</button>
                    </form>
                </div>
                
                <div class="reviews-list">
                    ${reviewsList.length > 0 ? reviewsList.map(review => `
                        <div class="review-item">
                            <div class="review-header">
                                <strong>${review.userName || 'Аноним'}</strong>
                                <span class="review-rating">${'⭐'.repeat(review.rating)}</span>
                            </div>
                            <p class="review-comment">${review.comment}</p>
                            <small class="review-date">${new Date(review.createdAt).toLocaleDateString('ru-RU')}</small>
                        </div>
                    `).join('') : '<p>Отзывов пока нет. Будьте первым!</p>'}
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading book detail:', error);
        container.innerHTML = `<div class="alert alert-error">Ошибка загрузки: ${error.message}</div>`;
    }
}

async function handleAddReview(event, bookId) {
    event.preventDefault();
    const formData = new FormData(event.target);
    
    const reviewData = {
        rating: parseInt(formData.get('rating')),
        comment: formData.get('comment')
    };
    
    try {
        await api.reviews.create(bookId, reviewData);
        showAlert('Отзыв добавлен!', 'success');
        showBookDetail(bookId); // Перезагрузка страницы
    } catch (error) {
        console.error('Error adding review:', error);
        showAlert('Ошибка: ' + error.message, 'error');
    }
}

// Модерация с редактированием
function showModerateModal(bookId, currentDescription) {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>Модерация книги</h3>
            <form onsubmit="handleModerateBook(event, ${bookId})">
                <div class="form-group">
                    <label>Описание книги (можно отредактировать)</label>
                    <textarea name="description" rows="6" class="form-control">${currentDescription || ''}</textarea>
                </div>
                <div class="form-actions">
                    <button type="submit" name="action" value="approve" class="btn btn-success">✓ Опубликовать в библиотеку</button>
                    <button type="submit" name="action" value="reject" class="btn btn-warning">← Вернуть в черновики</button>
                    <button type="button" onclick="closeModal()" class="btn btn-outline">Отмена</button>
                </div>
            </form>
        </div>
    `;
    document.body.appendChild(modal);
}

async function handleModerateBook(event, bookId) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const action = event.submitter.value;
    
    const moderateData = {
        description: formData.get('description'),
        approved: action === 'approve',
        rejectionReason: action === 'reject' ? 'Требуется доработка' : null
    };
    
    try {
        await api.moderation.moderate(bookId, moderateData);
        showAlert(action === 'approve' ? 'Книга опубликована!' : 'Книга возвращена в черновики', 'success');
        closeModal();
        loadPendingBooks();
    } catch (error) {
        console.error('Error moderating book:', error);
        showAlert('Ошибка: ' + error.message, 'error');
    }
}

function closeModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.remove();
    }
}
