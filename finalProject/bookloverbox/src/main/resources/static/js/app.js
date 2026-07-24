document.addEventListener('DOMContentLoaded', () => {
    auth.updateUI();
    showPage('home');
});

async function handleLogin(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const email = formData.get('email');
    const password = formData.get('password');

    await auth.login(email, password);
}

async function handleRegister(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = {
        fullName: formData.get('fullName'),
        email: formData.get('email'),
        password: formData.get('password'),
        wantsToBeAuthor: formData.get('wantsToBeAuthor') === 'on'
    };

    await auth.register(data);
}

function showAlert(message, type = 'info') {
    const alertClass = `alert-${type === 'error' ? 'error' : type}`;
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass}`;
    alertDiv.textContent = message;
    alertDiv.style.position = 'fixed';
    alertDiv.style.top = '20px';
    alertDiv.style.right = '20px';
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';

    document.body.appendChild(alertDiv);

    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}
