import API from '../api.js';

export async function renderProfilePage() {
    const container = document.getElementById('app-content');
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user) {
        window.navigateTo('/login');
        return;
    }

    // Initial View State
    renderViewMode(container, user);
}

function renderViewMode(container, user) {
    container.innerHTML = `
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card bg-dark border-secondary p-4 shadow">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h3 class="auth-header mb-0">User Profile</h3>
                        <button class="btn btn-outline-info btn-sm rounded-pill" id="edit-profile-btn">
                            <i class="fas fa-user-edit me-1"></i> Edit Info
                        </button>
                    </div>
                    
                    <div class="text-center mb-4">
                        <div class="bg-secondary rounded-circle d-inline-flex align-items-center justify-content-center" style="width: 80px; height: 80px;">
                            <i class="fas fa-user fa-3x text-white-50"></i>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="text-muted small fw-bold">USERNAME</label>
                        <p class="text-white fs-5">${user.username}</p>
                    </div>
                    <div class="mb-3">
                        <label class="text-muted small fw-bold">EMAIL ADDRESS</label>
                        <p class="text-white fs-5">${user.email}</p>
                    </div>
                    <div class="mb-3">
                        <label class="text-muted small fw-bold">ACCOUNT ROLE</label>
                        <p><span class="badge bg-primary">${user.role}</span></p>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('edit-profile-btn').onclick = () => renderEditMode(container, user);
}

function renderEditMode(container, user) {
    container.innerHTML = `
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card bg-dark border-info p-4 shadow">
                    <h3 class="auth-header mb-4 text-info">Edit Profile</h3>
                    <form id="edit-profile-form">
                        <div id="edit-error" class="alert alert-danger small mb-3" style="display:none"></div>
                        
                        <div class="mb-3">
                            <label class="form-label small fw-bold text-muted">USERNAME</label>
                            <input type="text" class="form-control bg-black text-white border-secondary" 
                                   id="edit-username" value="${user.username}" required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label small fw-bold text-muted">EMAIL</label>
                            <input type="email" class="form-control bg-black text-white border-secondary" 
                                   id="edit-email" value="${user.email}" required>
                        </div>

                        <div class="d-flex gap-2 mt-4">
                            <button type="submit" class="btn btn-info px-4 rounded-pill fw-bold">Save Changes</button>
                            <button type="button" class="btn btn-outline-secondary px-4 rounded-pill" id="cancel-edit">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;

    document.getElementById('cancel-edit').onclick = () => renderViewMode(container, user);

    document.getElementById('edit-profile-form').onsubmit = async (e) => {
        e.preventDefault();
        const updatedData = {
            username: document.getElementById('edit-username').value.trim(),
            email: document.getElementById('edit-email').value.trim(),
            role: user.role // Role usually shouldn't be edited by the user
        };

        try {
            // Call PUT /api/users/{id}
            const updatedUser = await API.users.update(user.id, updatedData);
            
            // CRITICAL: Update localStorage so the app recognizes the new name/email
            localStorage.setItem('user', JSON.stringify(updatedUser)); 
            
            // Refresh the whole app state (navbar, etc.)
            window.navigateTo('/profile');
        } catch (err) {
            const errEl = document.getElementById('edit-error');
            errEl.innerText = err.message;
            errEl.style.display = 'block';
        }
    };
}