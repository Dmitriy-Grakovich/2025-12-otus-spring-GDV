const auth = {
    getUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    getToken() {
        return localStorage.getItem('token');
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    hasRole(role) {
        const user = this.getUser();
        if (!user || !user.roles) return false;
        
        // roles может быть массивом или Set (преобразованным в массив)
        const rolesArray = Array.isArray(user.roles) ? user.roles : Array.from(user.roles);
        return rolesArray.includes(role);
    },

    isAdmin() {
        return this.hasRole('ROLE_ADMIN');
    },

    isModerator() {
        return this.hasRole('ROLE_MODERATOR') || this.isAdmin();
    },

    isAuthor() {
        return this.hasRole('ROLE_AUTHOR');
    },

    async login(email, password) {
        try {
            const response = await api.auth.login(email, password);
            // Backend возвращает accessToken, refreshToken, email, roles
            localStorage.setItem('token', response.accessToken);
            localStorage.setItem('refreshToken', response.refreshToken);
            
            // Создаем объект пользователя из ответа
            const user = {
                email: response.email,
                roles: response.roles,
                fullName: response.fullName || response.email
            };
            localStorage.setItem('user', JSON.stringify(user));
            
            this.updateUI();
            showPage('home');
            showAlert('Вход выполнен успешно', 'success');
        } catch (error) {
            console.error('Login error:', error);
            showAlert(error.message || 'Ошибка входа', 'error');
        }
    },

    async register(data) {
        try {
            const response = await api.auth.register(data);
            localStorage.setItem('token', response.token);
            localStorage.setItem('user', JSON.stringify(response.user));
            this.updateUI();
            showPage('home');
            showAlert('Регистрация прошла успешно', 'success');
        } catch (error) {
            showAlert(error.message || 'Ошибка регистрации', 'error');
        }
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.updateUI();
        showPage('home');
        showAlert('Вы вышли из системы', 'info');
    },

    updateUI() {
        const isAuth = this.isAuthenticated();
        const user = this.getUser();

        document.querySelectorAll('.guest-only').forEach(el => {
            el.style.display = isAuth ? 'none' : '';
        });

        document.querySelectorAll('.auth-only').forEach(el => {
            el.style.display = isAuth ? '' : 'none';
        });

        document.querySelectorAll('.admin-only').forEach(el => {
            el.style.display = this.isAdmin() ? '' : 'none';
        });

        document.querySelectorAll('.moderator-only').forEach(el => {
            el.style.display = this.isModerator() ? '' : 'none';
        });

        if (isAuth && user) {
            const userName = user.fullName || user.email || user.username || 'Пользователь';
            const userNameEl = document.getElementById('userName');
            if (userNameEl) {
                userNameEl.textContent = userName;
            }
        }
    }
};

function logout() {
    auth.logout();
}
