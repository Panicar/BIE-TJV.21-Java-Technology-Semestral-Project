import API from '../api.js';

const showError = (id, message) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.innerText = message;
    el.style.display = message ? 'block' : 'none';
};

export function renderLoginPage() {
    const container = document.getElementById('app-content');
    container.innerHTML = `
        <div class="row justify-content-center mt-5">
            <div class="col-md-4">
                <div class="card p-4 shadow">
                    <h3 class="auth-header mb-4 text-center">Login</h3>
                    <div id="login-error" class="alert alert-danger small mb-3" style="display:none"></div>
                    <form id="login-form">
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" class="form-control" id="login-username" placeholder="Username" required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Password</label>
                            <input type="password" class="form-control" id="login-password" placeholder="••••••••" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold" id="login-btn">
                            <span id="login-spinner" class="spinner-border spinner-border-sm me-2" style="display:none"></span>
                            Sign In
                        </button>
                    </form>
                </div>
            </div>
        </div>`;

    const form = document.getElementById('login-form');
    const btn = document.getElementById('login-btn');
    const spinner = document.getElementById('login-spinner');

    form.onsubmit = async (e) => {
        e.preventDefault(); // Prevents page refresh
        
        const username = document.getElementById('login-username').value.trim();
        const pass = document.getElementById('login-password').value.trim();

        // 1. Validation
        if (!username || !pass) {
            return showError('login-error', "Username and password are required.");
        }

        // 2. Prevent Redundant Submits
        btn.disabled = true;
        spinner.style.display = 'inline-block';
        showError('login-error', "");

        try {
            // getUserByUsername endpoint
            const user = await API.users.getByUsername(username); 
            localStorage.setItem('user', JSON.stringify(user));
            window.navigateTo('/'); 
        } catch (err) {
            showError('login-error', "Invalid username or connection error.");
            btn.disabled = false;
            spinner.style.display = 'none';
        }
    };
}

export function renderSignupPage() {
    const container = document.getElementById('app-content');
    container.innerHTML = `
        <div class="row justify-content-center mt-5">
            <div class="col-md-5">
                <div class="card p-4 shadow">
                    <h3 class="auth-header mb-4 text-center">Create Account</h3>
                    <div id="signup-error" class="alert alert-danger small mb-3" style="display:none"></div>
                    <form id="signup-form">
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" class="form-control" id="reg-username" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="reg-email" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="password" class="form-control" id="reg-password" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2 mt-2 fw-bold" id="signup-btn">
                            <span id="signup-spinner" class="spinner-border spinner-border-sm me-2" style="display:none"></span>
                            Register
                        </button>
                    </form>
                </div>
            </div>
        </div>`;

    const form = document.getElementById('signup-form');
    const btn = document.getElementById('signup-btn');
    const spinner = document.getElementById('signup-spinner');

    form.onsubmit = async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('reg-username').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const password = document.getElementById('reg-password').value.trim();

        if (!username || !email || !password) {
            return showError('signup-error', "All fields are required.");
        }

        btn.disabled = true;
        spinner.style.display = 'inline-block';

        try {
            // createUser endpoint
            await API.users.create({ 
                username, 
                email, 
                password, 
                role: 'USER', 
                isActive: true 
            }); 
            window.navigateTo('/login');
        } catch (err) {
            showError('signup-error', "Registration failed. Try a different username.");
            btn.disabled = false;
            spinner.style.display = 'none';
        }
    };
}