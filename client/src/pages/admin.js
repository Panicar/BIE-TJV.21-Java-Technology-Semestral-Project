import API from '../api.js';

export async function renderAdminPage() {
    const container = document.getElementById('app-content');
    const currentUser = JSON.parse(localStorage.getItem('user'));

    // Security Check
    if (!currentUser || currentUser.role !== 'ADMIN') {
        window.navigateTo('/');
        return;
    }

    container.innerHTML = `<div class="text-center mt-5"><div class="spinner-border text-danger"></div></div>`;

    try {
        const users = await API.users.getAll();
        
        container.innerHTML = `
            <div class="container mt-4">
                <h2 class="auth-header mb-4 text-danger"><i class="fas fa-users-cog me-2"></i>User Management</h2>
                
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle border-secondary">
                        <thead class="text-muted small">
                            <tr>
                                <th>ID</th>
                                <th>USER</th>
                                <th>EMAIL</th>
                                <th>ROLE</th>
                                <th>ACTIONS</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${users.map(u => {
                                // Check if this row is the current admin
                                const isSelf = u.id === currentUser.id;
                                return `
                                <tr>
                                    <td>${u.id}</td>
                                    <td>
                                        <div class="fw-bold">${u.username} ${isSelf ? '<span class="badge bg-secondary ms-1" style="font-size: 0.6rem;">YOU</span>' : ''}</div>
                                    </td>
                                    <td class="text-muted">${u.email}</td>
                                    <td>
                                        <select class="form-select form-select-sm bg-black text-white border-secondary role-select" 
                                                data-user-id="${u.id}" 
                                                style="width: 130px;"
                                                ${isSelf ? 'disabled' : ''}>
                                            <option value="USER" ${u.role === 'USER' ? 'selected' : ''}>USER</option>
                                            <option value="MODERATOR" ${u.role === 'MODERATOR' ? 'selected' : ''}>MODERATOR</option>
                                            <option value="ADMIN" ${u.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                        </select>
                                        ${isSelf ? '<div class="text-muted" style="font-size: 0.6rem; margin-top: 2px;">Cannot change own role</div>' : ''}
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-info view-activity-btn" data-user-id="${u.id}">
                                            <i class="fas fa-history me-1"></i> Activity
                                        </button>
                                        <button class="btn btn-sm btn-outline-danger ms-1 delete-user-btn" 
                                                data-user-id="${u.id}"
                                                ${isSelf ? 'disabled' : ''}>
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>`;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
                <div id="activity-modal-container"></div>
            </div>
        `;

        setupAdminListeners(currentUser);
    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger">Admin access failed: ${err.message}</div>`;
    }
}


function setupAdminListeners(currentUser) {
    // 1. Handle Role Change
    document.querySelectorAll('.role-select').forEach(select => {
        select.onchange = async () => {
            const userId = select.dataset.userId;
            
            // Safety check: verify they aren't somehow editing themselves
            if (parseInt(userId) === currentUser.id) {
                alert("Self-demotion is restricted to maintain system stability.");
                select.value = "ADMIN";
                return;
            }

            const newRole = select.value;
            try {
                const user = await API.users.getById(userId);
                await API.users.update(userId, { ...user, role: newRole });
                alert("Role updated successfully!");
            } catch (err) { 
                alert("Failed to update role: " + err.message); 
            }
        };
    });

    // 2. Handle Activity View
    document.querySelectorAll('.view-activity-btn').forEach(btn => {
        btn.onclick = async () => {
            const userId = btn.dataset.userId;
            try {
                const reviews = await API.reviews.getByUser(userId);
                showActivityOverlay(reviews);
            } catch (err) { 
                alert("Failed to fetch activity: " + err.message); 
            }
        };
    });

    // 3. Handle Delete User
    document.querySelectorAll('.delete-user-btn').forEach(btn => {
        btn.onclick = async () => {
            const userId = btn.dataset.userId;
            if (confirm("Permanently delete this user?")) {
                try {
                    await API.users.delete(userId);
                    renderAdminPage();
                } catch (err) { 
                    alert("Delete failed: " + err.message); 
                }
            }
        };
    });
}


function showActivityOverlay(reviews, userId) {
    const modal = document.getElementById('activity-modal-container');
    modal.innerHTML = `
        <div class="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center" 
             style="background: rgba(0,0,0,0.8); z-index: 1050;">
            <div class="card bg-dark border-info w-75 h-75 shadow-lg">
                <div class="card-header d-flex justify-content-between align-items-center sticky-top bg-dark border-secondary">
                    <h5 class="mb-0 text-info"><i class="fas fa-history me-2"></i>Activity Log: ${reviews[0]?.username || 'User'}</h5>
                    <button class="btn-close btn-close-white" onclick="this.parentElement.parentElement.parentElement.remove()"></button>
                </div>
                <div class="card-body overflow-auto">
                    ${reviews.length > 0 ? reviews.map(r => `
                        <div class="card bg-black border-secondary mb-3 p-3" id="admin-rev-${r.id}">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <span class="badge bg-outline-primary border border-primary text-primary mb-2">Item ID: ${r.itemId}</span>
                                    <p class="text-light mb-1">${r.comment || '<i class="text-muted">No comment.</i>'}</p>
                                    <small class="text-muted">${r.reviewDate || 'Recent'}</small>
                                </div>
                                <div class="text-end">
                                    <div class="text-warning mb-2">${'★'.repeat(r.rating)}</div>
                                    <button class="btn btn-sm btn-outline-danger admin-delete-rev" data-rev-id="${r.id}">
                                        <i class="fas fa-trash-alt me-1"></i> Delete Review
                                    </button>
                                </div>
                            </div>
                        </div>
                    `).join('') : '<p class="text-muted text-center mt-5">No activity found for this user.</p>'}
                </div>
            </div>
        </div>
    `;

    // Attach listeners for the newly created delete buttons
    modal.querySelectorAll('.admin-delete-rev').forEach(btn => {
        btn.onclick = async () => {
            const reviewId = btn.dataset.revId;
            if (confirm("Are you sure you want to moderate and delete this review?")) {
                try {
                    await API.reviews.delete(reviewId);
                    // Remove from UI immediately
                    document.getElementById(`admin-rev-${reviewId}`).remove();
                    
                    // If no reviews left, show empty state
                    if (modal.querySelectorAll('.card.bg-black').length === 0) {
                        modal.querySelector('.card-body').innerHTML = '<p class="text-muted text-center mt-5">No activity found for this user.</p>';
                    }
                } catch (err) {
                    alert("Moderation failed: " + err.message);
                }
            }
        };
    });
}