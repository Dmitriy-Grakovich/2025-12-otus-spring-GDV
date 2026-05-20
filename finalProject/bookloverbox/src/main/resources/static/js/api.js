const API_BASE = '/api';

const api = {
    async request(url, options = {}) {
        const token = localStorage.getItem('token');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(API_BASE + url, {
                ...options,
                headers
            });

            if (response.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.reload();
                return null;
            }

            if (!response.ok) {
                let errorMessage = 'Ошибка запроса';
                try {
                    const error = await response.json();
                    errorMessage = error.message || error.error || errorMessage;
                } catch (e) {
                    errorMessage = `Ошибка ${response.status}: ${response.statusText}`;
                }
                throw new Error(errorMessage);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return null;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    auth: {
        async login(email, password) {
            return await api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
        },

        async register(data) {
            return await api.request('/auth/register', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
    },

    books: {
        async getAll() {
            return await api.request('/books');
        },

        async getById(id) {
            return await api.request(`/books/${id}`);
        },

        async getMy() {
            return await api.request('/books/my');
        },

        async create(data) {
            return await api.request('/books', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        async update(id, data) {
            return await api.request(`/books/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        },

        async delete(id) {
            return await api.request(`/books/${id}`, {
                method: 'DELETE'
            });
        },

        async search(title) {
            return await api.request(`/books/search?title=${encodeURIComponent(title)}`);
        }
    },

    reviews: {
        async getByBook(bookId) {
            return await api.request(`/reviews/books/${bookId}`);
        },

        async create(bookId, data) {
            return await api.request(`/reviews/books/${bookId}`, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        async update(reviewId, data) {
            return await api.request(`/reviews/${reviewId}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        },

        async delete(reviewId) {
            return await api.request(`/reviews/${reviewId}`, {
                method: 'DELETE'
            });
        }
    },

    genres: {
        async getAll() {
            return await api.request('/genres');
        },

        async create(data) {
            return await api.request('/genres', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }
    },

    moderation: {
        async getPending() {
            return await api.request('/moderator/books/pending');
        },

        async moderate(bookId, data) {
            return await api.request(`/moderator/books/${bookId}/moderate`, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        async approve(bookId) {
            return await api.request(`/moderator/books/${bookId}/approve`, {
                method: 'POST'
            });
        },

        async reject(bookId, reason) {
            return await api.request(`/moderator/books/${bookId}/reject`, {
                method: 'POST',
                body: JSON.stringify({ reason })
            });
        }
    },

    statistics: {
        async getOverview() {
            return await api.request('/statistics/overview');
        },

        async getUsers() {
            return await api.request('/statistics/users');
        }
    },

    batch: {
        async exportBooks() {
            const token = localStorage.getItem('token');
            const response = await fetch(API_BASE + '/batch/export-books', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('Ошибка экспорта');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'books-export.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        }
    }
};
